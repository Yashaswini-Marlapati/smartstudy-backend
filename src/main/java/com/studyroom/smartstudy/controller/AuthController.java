package com.studyroom.smartstudy.controller;

import com.studyroom.smartstudy.model.User;
import com.studyroom.smartstudy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Register new user
    @PostMapping("/register")
    public String register(@RequestBody User user) {

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user (role comes from frontend)
        userRepository.save(user);

        return "User Registered Successfully";
    }
}