package com.studyroom.smartstudy.model;

import jakarta.persistence.*;  // JPA annotations
import lombok.*;              // Lombok annotations


// Marks this class as a database table
@Entity

// Specifies table name in MySQL
@Table(name = "study_rooms")

// Lombok: Automatically generates getters, setters, constructor, etc.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyRoom {

    // Primary key
    @Id

    // Auto-increment ID in MySQL
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    // Maximum capacity of room
    @Column(nullable = false)
    private int capacity;

    // Current occupancy count (how many students inside)
    private int currentOccupancy;


    // Name of the room (e.g., Room A101)
    @Column(nullable = false, unique = true)
    private String name;
}