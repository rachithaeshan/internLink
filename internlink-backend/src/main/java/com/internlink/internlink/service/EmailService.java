package com.internlink.internlink.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.name}")
    private String appName;

    @Async
    public void sendEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, appName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    // Student Welcome Email
    public void sendStudentWelcome(String to, String name) {
        String subject = "Welcome to InternLink, " + name + "!";
        String body = buildEmail(
                "Welcome aboard, " + name + "! 🎓",
                "Your InternLink student account has been created successfully.",
                "You can now browse internships, build your profile, upload your CV, and start applying for opportunities.",
                new String[]{"Browse internships", "Upload your CV", "Track applications"},
                "Start exploring",
                "http://localhost:3000/student/dashboard",
                "#6366f1"
        );
        sendEmail(to, subject, body);
    }

    // Company Welcome Email
    public void sendCompanyWelcome(String to, String companyName) {
        String subject = "Welcome to InternLink — " + companyName;
        String body = buildEmail(
                "Welcome, " + companyName + "! 🏢",
                "Your company account on InternLink has been created successfully.",
                "You can now post internship opportunities and start reviewing talented candidates.",
                new String[]{"Post internship roles", "Review applicants", "Shortlist candidates"},
                "Go to dashboard",
                "http://localhost:3000/company/dashboard",
                "#8b5cf6"
        );
        sendEmail(to, subject, body);
    }

    // Application Received (to student)
    public void sendApplicationConfirmation(String to, String studentName,
                                            String internshipTitle, String companyName) {
        String subject = "Application submitted — " + internshipTitle;
        String body = buildEmail(
                "Application received! ✅",
                "Hi " + studentName + ", your application has been submitted successfully.",
                "You applied for <strong>" + internshipTitle + "</strong> at <strong>" + companyName + "</strong>. "
                        + "We will notify you as soon as the company reviews your application.",
                new String[]{"Application is under review", "You will be notified of any updates", "Check status anytime"},
                "View my applications",
                "http://localhost:3000/student/dashboard",
                "#10b981"
        );
        sendEmail(to, subject, body);
    }

    // Application Shortlisted
    public void sendShortlisted(String to, String studentName,
                                String internshipTitle, String companyName) {
        String subject = "You have been shortlisted — " + internshipTitle;
        String body = buildEmail(
                "Great news, " + studentName + "!",
                "You have been shortlisted for the internship below.",
                "<strong>" + internshipTitle + "</strong> at <strong>" + companyName + "</strong>. "
                        + "The company is interested in your profile. Stay tuned for further updates.",
                new String[]{"Profile stood out", "Company is reviewing shortlisted candidates", "Further updates coming soon"},
                "View application",
                "http://localhost:3000/student/dashboard",
                "#f59e0b"
        );
        sendEmail(to, subject, body);
    }

    // Application Accepted
    public void sendAccepted(String to, String studentName,
                             String internshipTitle, String companyName) {
        String subject = "Congratulations! You have been accepted — " + internshipTitle;
        String body = buildEmail(
                "Congratulations, " + studentName + "!",
                "You have been accepted for the internship!",
                "You have been accepted for <strong>" + internshipTitle + "</strong> at <strong>" + companyName + "</strong>. "
                        + "The company will be in touch with you shortly with further details about your start date and onboarding.",
                new String[]{"Offer confirmed", "Company will contact you soon", "Prepare for onboarding"},
                "View details",
                "http://localhost:3000/student/dashboard",
                "#10b981"
        );
        sendEmail(to, subject, body);
    }

    // Application Rejected
    public void sendRejected(String to, String studentName,
                             String internshipTitle, String companyName) {
        String subject = "Update on your application — " + internshipTitle;
        String body = buildEmail(
                "Application update, " + studentName,
                "Thank you for your interest in the internship.",
                "After careful consideration, <strong>" + companyName + "</strong> has decided not to move forward "
                        + "with your application for <strong>" + internshipTitle + "</strong> at this time. "
                        + "Do not be discouraged — keep applying and building your profile!",
                new String[]{"Keep your profile updated", "Apply to more internships", "Build your skills"},
                "Explore more internships",
                "http://localhost:3000/student/dashboard",
                "#6366f1"
        );
        sendEmail(to, subject, body);
    }

    // New Application Alert (to company)
    public void sendNewApplicationAlert(String to, String companyName,
                                        String studentName, String internshipTitle) {
        String subject = "New application received — " + internshipTitle;
        String body = buildEmail(
                "New applicant, " + companyName + "!",
                studentName + " has applied for your internship posting.",
                "<strong>" + studentName + "</strong> has submitted an application for "
                        + "<strong>" + internshipTitle + "</strong>. "
                        + "Log in to review their profile and resume.",
                new String[]{"Review applicant profile", "View their resume", "Shortlist or respond"},
                "Review applicant",
                "http://localhost:3000/company/dashboard",
                "#8b5cf6"
        );
        sendEmail(to, subject, body);
    }

    // HTML Email Template Builder
    private String buildEmail(String heading, String subheading, String bodyText,
                              String[] features, String ctaText, String ctaUrl,
                              String accentColor) {

        StringBuilder featuresHtml = new StringBuilder();
        for (String feature : features) {
            featuresHtml.append("<tr>")
                    .append("<td style='padding: 6px 0; font-size: 14px; color: #6b5c47;'>")
                    .append("<span style='color: ").append(accentColor).append("; margin-right: 8px; font-weight: 600;'>&#10003;</span>")
                    .append(feature)
                    .append("</td>")
                    .append("</tr>");
        }

        return "<!DOCTYPE html>"
                + "<html>"
                + "<head><meta charset='UTF-8'><meta name='viewport' content='width=device-width'></head>"
                + "<body style='margin:0;padding:0;background:#f5f1eb;font-family:-apple-system,BlinkMacSystemFont,sans-serif;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0' style='background:#f5f1eb;padding:40px 0;'>"
                + "<tr><td align='center'>"
                + "<table width='600' cellpadding='0' cellspacing='0' style='max-width:600px;width:100%;'>"

                // Header
                + "<tr><td style='background:#1c1a17;border-radius:16px 16px 0 0;padding:32px 40px;text-align:center;'>"
                + "<div style='font-family:Georgia,serif;font-size:26px;color:#faf8f5;letter-spacing:-0.5px;'>"
                + "Intern<em style='font-style:italic;color:#c4a679;'>Link</em>"
                + "</div>"
                + "</td></tr>"

                // Body
                + "<tr><td style='background:#ffffff;padding:40px;'>"
                + "<h1 style='font-family:Georgia,serif;font-size:26px;font-weight:400;color:#1c1a17;margin:0 0 8px;letter-spacing:-0.5px;'>"
                + heading
                + "</h1>"
                + "<p style='font-size:15px;color:#8b7355;margin:0 0 24px;font-weight:400;'>"
                + subheading
                + "</p>"
                + "<div style='background:#faf8f5;border-left:3px solid " + accentColor + ";border-radius:0 8px 8px 0;padding:20px 24px;margin-bottom:28px;'>"
                + "<p style='font-size:14px;color:#6b5c47;line-height:1.7;margin:0;'>" + bodyText + "</p>"
                + "</div>"
                + "<table width='100%' cellpadding='0' cellspacing='0' style='margin-bottom:28px;'>"
                + featuresHtml
                + "</table>"
                + "<table width='100%' cellpadding='0' cellspacing='0'>"
                + "<tr><td align='center'>"
                + "<a href='" + ctaUrl + "' style='display:inline-block;background:" + accentColor + ";color:#ffffff;text-decoration:none;padding:14px 36px;border-radius:10px;font-size:14px;font-weight:600;letter-spacing:0.3px;'>"
                + ctaText
                + "</a>"
                + "</td></tr>"
                + "</table>"
                + "</td></tr>"

                // Footer
                + "<tr><td style='background:#f0ebe3;border-radius:0 0 16px 16px;padding:24px 40px;text-align:center;'>"
                + "<p style='font-size:12px;color:#b5a898;margin:0;line-height:1.6;'>"
                + "This email was sent by InternLink. You are receiving this because you have an account on our platform.<br/>"
                + "&copy; 2026 InternLink. All rights reserved."
                + "</p>"
                + "</td></tr>"

                + "</table>"
                + "</td></tr>"
                + "</table>"
                + "</body>"
                + "</html>";
    }
}