package com.studyroom.smartstudy.repository;

import com.studyroom.smartstudy.model.Waitlist;
import com.studyroom.smartstudy.model.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


// Repository for Waitlist table
@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {

    // Find all waiting users for a room ordered by request time
    List<Waitlist> findByRoomAndStatusOrderByRequestTimeAsc(
            StudyRoom room,
            String status
    );
}
