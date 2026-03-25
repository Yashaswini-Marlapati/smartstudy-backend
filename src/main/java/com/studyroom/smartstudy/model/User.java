package com.studyroom.smartstudy.model;

import jakarta.persistence.*;
import lombok.*;

@Entity             // 👉 Tells Hibernate this is a DB table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")     // 👉 Table name in MySQL
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
