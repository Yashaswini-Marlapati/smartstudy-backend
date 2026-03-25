package com.studyroom.smartstudy.controller;

import com.studyroom.smartstudy.model.StudyRoom;
import com.studyroom.smartstudy.model.Booking;
import com.studyroom.smartstudy.repository.StudyRoomRepository;
import com.studyroom.smartstudy.repository.BookingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@CrossOrigin(origins = "http://localhost:5173")
public class StudyRoomController {

    @Autowired
    private StudyRoomRepository studyRoomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // ===============================
    // ADMIN - ADD ROOM
    // ===============================
    @PostMapping("/admin/add")
    public ResponseEntity<?> addRoom(@RequestBody StudyRoom room) {

        String roomName = room.getName().trim();

        // Check duplicate (case insensitive recommended)
        if (studyRoomRepository.existsByNameIgnoreCase(roomName)) {
            return ResponseEntity
                    .badRequest()
                    .body("Room with this name already exists");
        }

        room.setName(roomName);
        room.setCurrentOccupancy(0);

        StudyRoom savedRoom = studyRoomRepository.save(room);

        return ResponseEntity.ok(savedRoom);
    }

    // ===============================
    // STUDENT - VIEW ALL ROOMS
    // ===============================
    @GetMapping("/student/all")
    public List<StudyRoom> getAllRooms() {
        return studyRoomRepository.findAll();
    }

    // ===============================
    // ADMIN - VIEW ALL ROOMS
    // ===============================
    @GetMapping("/admin/all")
    public List<StudyRoom> getAllRoomsForAdmin() {
        return studyRoomRepository.findAll();
    }

    // ===============================
    // ADMIN - DELETE ROOM BY NAME
    // ===============================
    @DeleteMapping("/admin/delete-by-name/{name}")
    public ResponseEntity<?> deleteRoomByName(@PathVariable String name) {

        StudyRoom room = studyRoomRepository
                .findByNameIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Prevent delete if bookings exist
        List<Booking> bookings = bookingRepository.findByRoom(room);

        if (!bookings.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Cannot delete room with existing bookings");
        }

        studyRoomRepository.delete(room);

        return ResponseEntity.ok("Room deleted successfully");
    }
}