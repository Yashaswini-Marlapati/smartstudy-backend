package com.studyroom.smartstudy.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


// Marks this class as database table
@Entity

// Table name in MySQL
@Table(name = "bookings")

// Lombok generates getters, setters, constructor etc.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    // Primary key
    @Id

    // Auto increment ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many bookings can belong to one StudyRoom
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private StudyRoom room;

    // Many bookings can belong to one User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Booking start time
    private LocalDateTime startTime;

    // Booking end time
    private LocalDateTime endTime;

    // Status of booking (CONFIRMED, WAITLISTED, CANCELLED)
    private String status;

    // Actual check-in time
    private LocalDateTime checkInTime;

    // Actual check-out time
    private LocalDateTime checkOutTime;
}
