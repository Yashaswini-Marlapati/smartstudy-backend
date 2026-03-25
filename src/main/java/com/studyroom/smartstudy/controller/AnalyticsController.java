package com.studyroom.smartstudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    // Dummy intelligent analytics (can later connect to booking table)

    @GetMapping("/student")
    public Map<String, Object> getAnalytics() {

        Map<String, Object> data = new HashMap<>();

        // Simulated peak hour
        data.put("peakHour", "10 AM - 12 PM");

        // Simulated average usage in minutes
        data.put("averageUsage", 95);

        // Simulated best time recommendation
        data.put("bestTime", "3 PM - 5 PM");

        return data;
    }
}