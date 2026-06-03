package com.internlink.internlink.dto;


import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String role; // STUDENT, COMPANY, ADMIN

    // Student fields
    private String university;
    private String degree;

    // Company fields
    private String companyName;
    private String industry;
    private String website;
}