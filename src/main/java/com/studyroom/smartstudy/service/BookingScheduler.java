package com.studyroom.smartstudy.service;

import com.studyroom.smartstudy.model.*;
import com.studyroom.smartstudy.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


// Marks this as service layer
@Service
public class BookingScheduler {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;


    // Runs every 1 minute
    @Scheduled(fixedRate = 60000)
    public void checkNoShowBookings() {

        // Get all confirmed bookings
        List<Booking> bookings = bookingRepository.findAll();

        for (Booking booking : bookings) {

            // If booking is confirmed and no check-in happened
            if ("CONFIRMED".equals(booking.getStatus())
                    && booking.getCheckInTime() == null) {

                // If current time is 10 minutes after start time
                if (LocalDateTime.now().isAfter(
                        booking.getStartTime().plusMinutes(10))) {

                    // Cancel booking
                    booking.setStatus("CANCELLED");
                    bookingRepository.save(booking);

                    // Promote next waitlist user
                    List<Waitlist> queue =
                            waitlistRepository
                                    .findByRoomAndStatusOrderByRequestTimeAsc(
                                            booking.getRoom(),
                                            "WAITING");

                    if (!queue.isEmpty()) {

                        Waitlist next = queue.get(0);

                        Booking newBooking = new Booking();
                        newBooking.setRoom(next.getRoom());
                        newBooking.setUser(next.getUser());
                        newBooking.setStartTime(booking.getStartTime());
                        newBooking.setEndTime(booking.getEndTime());
                        newBooking.setStatus("CONFIRMED");

                        bookingRepository.save(newBooking);

                        next.setStatus("ALLOCATED");
                        waitlistRepository.save(next);
                    }
                }
            }
        }
    }
}
