package com.studyroom.smartstudy.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @GetMapping("/admin/test")
    public String adminTest() {
        return "Welcome Admin!";
    }

    @GetMapping("/student/test")
    public String studentTest() {
        return "Welcome Student!";
    }
}
