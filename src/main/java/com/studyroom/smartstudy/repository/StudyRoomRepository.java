package com.studyroom.smartstudy.repository;

import com.studyroom.smartstudy.model.StudyRoom;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// Marks this interface as a Repository (data access layer)
@Repository

// JpaRepository gives built-in CRUD methods like save(), findAll(), findById(), delete()
public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    // No need to write anything now
    // Spring automatically provides basic database operations

    boolean existsByNameIgnoreCase(String name);

    Optional<StudyRoom> findByNameIgnoreCase(String name);

    
}

