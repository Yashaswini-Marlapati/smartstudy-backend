package com.studyroom.smartstudy.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


// Marks this class as database table
@Entity

// Table name in MySQL
@Table(name = "waitlist")

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Waitlist {

    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many waitlist entries can belong to one room
    @ManyToOne
    @JoinColumn(name = "room_id")
    private StudyRoom room;

    // Many waitlist entries can belong to one user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Time when user requested booking
    private LocalDateTime requestTime;

    // Status (WAITING / ALLOCATED / CANCELLED)
    private String status;
}
