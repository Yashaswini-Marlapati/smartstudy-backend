package com.studyroom.smartstudy.controller;

import com.studyroom.smartstudy.model.*;
import com.studyroom.smartstudy.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.security.core.Authentication;

import java.time.OffsetDateTime;

// REST controller for booking operations
@RestController
@RequestMapping("/booking")
@CrossOrigin(origins = "http://localhost:5173")

public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private StudyRoomRepository studyRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;



    // STUDENT books a room
@PostMapping("/student/book")
public String bookRoom(
        @RequestParam Long roomId,
        @RequestParam String start,
        @RequestParam String end,
        Authentication authentication
) {

    // Get logged-in user email from Spring Security
    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    StudyRoom room = studyRoomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found"));

    LocalDateTime startTime = LocalDateTime.parse(start);
    LocalDateTime endTime = LocalDateTime.parse(end);

    // Count active confirmed bookings for that room
long activeBookings = bookingRepository
        .findByRoomAndStatus(room, "CONFIRMED")
        .size();

if (activeBookings >= room.getCapacity()) {

    Waitlist waitlist = new Waitlist();
    waitlist.setRoom(room);
    waitlist.setUser(user);
    waitlist.setRequestTime(LocalDateTime.now());
    waitlist.setStatus("WAITING");

    waitlistRepository.save(waitlist);

    return "Room Full. Added to Waitlist.";
}


    Booking booking = new Booking();
    booking.setRoom(room);
    booking.setUser(user);
    booking.setStartTime(startTime);
    booking.setEndTime(endTime);
    booking.setStatus("CONFIRMED");

    bookingRepository.save(booking);

    return "Room Booked Successfully!";
}
    // Student checks in to a booking
// Student checks in to a booking
@PostMapping("/student/checkin")
public String checkIn(@RequestParam Long bookingId) {

    Booking booking = bookingRepository.findById(bookingId).orElse(null);

    if (booking == null) {
        return "Booking not found";
    }

    if (booking.getCheckInTime() != null) {
        return "Already checked in";
    }

    StudyRoom room = booking.getRoom();

    // Prevent over capacity
    if (room.getCurrentOccupancy() >= room.getCapacity()) {
        return "Room is full";
    }

    booking.setCheckInTime(LocalDateTime.now());
    room.setCurrentOccupancy(room.getCurrentOccupancy() + 1);

    bookingRepository.save(booking);
    studyRoomRepository.save(room);

    return "Checked in successfully";
}
// Student checks out from a booking
@PostMapping("/student/checkout")
public String checkOut(@RequestParam Long bookingId) {

    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

    // Prevent double checkout
    if (booking.getCheckOutTime() != null) {
        return "Already checked out";
    }

    if (booking.getCheckInTime() == null) {
        return "Cannot checkout without check-in";
    }

    // Set checkout time
    booking.setCheckOutTime(LocalDateTime.now());

    // Mark booking as COMPLETED
    booking.setStatus("COMPLETED");

    // Reduce occupancy safely
    StudyRoom room = booking.getRoom();
    room.setCurrentOccupancy(
            Math.max(0, room.getCurrentOccupancy() - 1)
    );

    studyRoomRepository.save(room);
    bookingRepository.save(booking);

    return "Checked out successfully";
}

// Get peak hour automatically
@GetMapping("/admin/peak-hour")
public String getPeakHour() {

    List<Object[]> data = bookingRepository.getBookingsGroupedByHour();

    int maxCount = 0;
    int peakHour = -1;

    for (Object[] row : data) {

        int hour = (Integer) row[0];
        long count = (Long) row[1];

        if (count > maxCount) {
            maxCount = (int) count;
            peakHour = hour;
        }
    }

    return "Peak Hour: " + peakHour + " with " + maxCount + " bookings";
}

@GetMapping("/admin/average-usage")
public String getAverageUsage() {

    Double avg = bookingRepository.getAverageUsageMinutes();

    if (avg == null) {
        return "No completed bookings yet";
    }

    return "Average Usage Time: " + avg + " minutes";
}

@GetMapping("/student/best-time")
public String getBestTime() {

    List<Object[]> data = bookingRepository.getBookingsGroupedByHour();

    int minCount = Integer.MAX_VALUE;
    int bestHour = -1;

    for (Object[] row : data) {

        int hour = (Integer) row[0];
        long count = (Long) row[1];

        if (count < minCount) {
            minCount = (int) count;
            bestHour = hour;
        }
    }

    return "Best Time to Book: " + bestHour + ":00 hours";
}

@GetMapping("/student/my-bookings")
public List<Booking> getMyBookings(Authentication authentication) {

    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return bookingRepository.findByUser(user);
}

LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

@Scheduled(fixedRate = 60000) // runs every 1 minute
public void autoCancelNoShowBookings() {

    List<Booking> bookings = bookingRepository.findAll();

    for (Booking booking : bookings) {

        // Only cancel if:
        // 1. Status is CONFIRMED
        // 2. No check-in
        // 3. 10 minutes passed from start time

        if (
            booking.getStatus().equals("CONFIRMED") &&
            booking.getCheckInTime() == null &&
            booking.getStartTime()
                   .plusMinutes(10)
                   .isBefore(LocalDateTime.now())
        ) {

            // Cancel booking
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);

            StudyRoom room = booking.getRoom();

            // Get next waiting student (FIFO)
            List<Waitlist> queue =
                waitlistRepository
                .findByRoomAndStatusOrderByRequestTimeAsc(
                        room, "WAITING");

            if (!queue.isEmpty()) {

                Waitlist next = queue.get(0);

                Booking newBooking = new Booking();
                newBooking.setRoom(room);
                newBooking.setUser(next.getUser());
                newBooking.setStartTime(LocalDateTime.now());
                newBooking.setEndTime(LocalDateTime.now().plusHours(1));
                newBooking.setStatus("CONFIRMED");

                bookingRepository.save(newBooking);

                next.setStatus("ALLOCATED");
                waitlistRepository.save(next);
            }
        }
    }
}
// ===============================
// ADMIN - View All Bookings
// ===============================
@GetMapping("/admin/all")
public List<Booking> getAllBookings() {

    // Return all bookings from DB
    return bookingRepository.findAll();
}

// ===============================
// ADMIN - Cancel Booking
// ===============================
@PostMapping("/admin/cancel")
public String cancelBooking(@RequestParam Long bookingId) {

    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

    if ("CANCELLED".equals(booking.getStatus())) {
        return "Booking already cancelled";
    }

    booking.setStatus("CANCELLED");

    // If user had checked in, reduce occupancy
    if (booking.getCheckInTime() != null) {
        StudyRoom room = booking.getRoom();
        room.setCurrentOccupancy(
                Math.max(0, room.getCurrentOccupancy() - 1)
        );
        studyRoomRepository.save(room);
    }

    bookingRepository.save(booking);

    return "Booking Cancelled Successfully";
}


}
