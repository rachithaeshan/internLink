package com.internlink.internlink.controller;

import com.internlink.internlink.entity.Internship;
import com.internlink.internlink.entity.User;
import com.internlink.internlink.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Long>> dashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/block")
    public ResponseEntity<User> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.blockUser(id));
    }

    @PutMapping("/users/{id}/unblock")
    public ResponseEntity<User> unblockUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.unblockUser(id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/internships")
    public ResponseEntity<List<Internship>> getAllInternships() {
        return ResponseEntity.ok(adminService.getAllInternships());
    }

    @GetMapping("/internships/pending")
    public ResponseEntity<List<Internship>> getPending() {
        return ResponseEntity.ok(adminService.getPendingInternships());
    }

    @PutMapping("/internships/{id}/approve")
    public ResponseEntity<Internship> approve(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveInternship(id));
    }

    @DeleteMapping("/internships/{id}")
    public ResponseEntity<Void> deleteInternship(@PathVariable Long id) {
        adminService.deleteInternship(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reports/users")
    public ResponseEntity<byte[]> downloadUsersReport() throws IOException {
        byte[] data = adminService.generateUsersReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users_report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @GetMapping("/reports/internships")
    public ResponseEntity<byte[]> downloadInternshipsReport() throws IOException {
        byte[] data = adminService.generateInternshipsReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=internships_report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @GetMapping("/reports/applications")
    public ResponseEntity<byte[]> downloadApplicationsReport() throws IOException {
        byte[] data = adminService.generateApplicationsReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=applications_report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }
}