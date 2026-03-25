package com.studyroom.smartstudy.service;

import com.studyroom.smartstudy.model.User;
import com.studyroom.smartstudy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service   // 👉 Marks this class as a Service layer component
           // 👉 Spring will automatically create and manage this object
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired   // 👉 Injects UserRepository automatically
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // Fetch user from database using email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        // Convert our User object into Spring Security User object
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
