package com.internlink.internlink.service;

import com.internlink.internlink.entity.Application;
import com.internlink.internlink.entity.Internship;
import com.internlink.internlink.entity.User;
import com.internlink.internlink.repository.ApplicationRepository;
import com.internlink.internlink.repository.CompanyRepository;
import com.internlink.internlink.repository.InternshipRepository;
import com.internlink.internlink.repository.StudentRepository;
import com.internlink.internlink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final InternshipRepository internshipRepository;
    private final ApplicationRepository applicationRepository;

    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalStudents", studentRepository.count());
        stats.put("totalCompanies", companyRepository.count());
        stats.put("totalInternships", internshipRepository.count());
        stats.put("totalApplications", applicationRepository.count());
        stats.put("approvedInternships", (long) internshipRepository.findByApprovedTrue().size());
        stats.put("pendingInternships", (long) internshipRepository.findByApprovedFalse().size());
        stats.put("blockedUsers", userRepository.countByBlockedTrue());
        return stats;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User blockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBlocked(true);
        return userRepository.save(user);
    }

    public User unblockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBlocked(false);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<Internship> getPendingInternships() {
        return internshipRepository.findByApprovedFalse();
    }

    public Internship approveInternship(Long id) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found"));
        internship.setApproved(true);
        return internshipRepository.save(internship);
    }

    public void deleteInternship(Long id) {
        internshipRepository.deleteById(id);
    }

    public List<Internship> getAllInternships() {
        return internshipRepository.findAll();
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public byte[] generateUsersReport() throws IOException {
        List<User> users = userRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row header = sheet.createRow(0);
        String[] cols = {"ID", "Name", "Email", "Role", "Blocked"};
        for (int i = 0; i < cols.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (User u : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(u.getId());
            row.createCell(1).setCellValue(u.getName());
            row.createCell(2).setCellValue(u.getEmail());
            row.createCell(3).setCellValue(u.getRole().name());
            row.createCell(4).setCellValue(u.isBlocked() ? "Yes" : "No");
        }

        for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    public byte[] generateInternshipsReport() throws IOException {
        List<Internship> internships = internshipRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Internships");

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        Row header = sheet.createRow(0);
        String[] cols = {"ID", "Title", "Company", "Location", "Skills", "Duration", "Deadline", "Approved"};
        for (int i = 0; i < cols.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (Internship i : internships) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(i.getId());
            row.createCell(1).setCellValue(i.getTitle());
            row.createCell(2).setCellValue(i.getCompany().getCompanyName());
            row.createCell(3).setCellValue(i.getLocation());
            row.createCell(4).setCellValue(i.getSkills());
            row.createCell(5).setCellValue(i.getDuration());
            row.createCell(6).setCellValue(i.getDeadline() != null ? i.getDeadline().toString() : "");
            row.createCell(7).setCellValue(i.isApproved() ? "Yes" : "No");
        }

        for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    public byte[] generateApplicationsReport() throws IOException {
        List<Application> applications = applicationRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Applications");

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        Row header = sheet.createRow(0);
        String[] cols = {"ID", "Student", "Email", "Internship", "Company", "Applied Date", "Status"};
        for (int i = 0; i < cols.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (Application a : applications) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(a.getId());
            row.createCell(1).setCellValue(a.getStudent().getUser().getName());
            row.createCell(2).setCellValue(a.getStudent().getUser().getEmail());
            row.createCell(3).setCellValue(a.getInternship().getTitle());
            row.createCell(4).setCellValue(a.getInternship().getCompany().getCompanyName());
            row.createCell(5).setCellValue(a.getAppliedDate() != null ? a.getAppliedDate().toString() : "");
            row.createCell(6).setCellValue(a.getStatus().name());
        }

        for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}