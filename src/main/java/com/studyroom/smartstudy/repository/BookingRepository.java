package com.studyroom.smartstudy.repository;

import com.studyroom.smartstudy.model.User;

import com.studyroom.smartstudy.model.Booking;
import com.studyroom.smartstudy.model.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
//import org.springframework.data.jpa.repository.Query;


import java.time.LocalDateTime;
import java.util.List;


// Marks this as Repository layer
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Custom method to find overlapping bookings for a room
    // This is the core clash detection query
    List<Booking> findByRoomAndStartTimeLessThanAndEndTimeGreaterThan(
            StudyRoom room,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    List<Booking> findByUser(User user);

    // Custom query to count bookings per hour
@Query("SELECT HOUR(b.startTime), COUNT(b) FROM Booking b GROUP BY HOUR(b.startTime)")
List<Object[]> getBookingsGroupedByHour();

@Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, b.checkInTime, b.checkOutTime)) FROM Booking b WHERE b.checkInTime IS NOT NULL AND b.checkOutTime IS NOT NULL")
Double getAverageUsageMinutes();

List<Booking> findByRoomAndStatus(StudyRoom room, String status);

List<Booking> findByRoom(StudyRoom room);

}
