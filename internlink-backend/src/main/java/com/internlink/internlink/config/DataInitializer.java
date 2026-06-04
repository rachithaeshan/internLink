package com.internlink.internlink.config;

import com.internlink.internlink.entity.Company;
import com.internlink.internlink.entity.Internship;
import com.internlink.internlink.entity.User;
import com.internlink.internlink.repository.CompanyRepository;
import com.internlink.internlink.repository.InternshipRepository;
import com.internlink.internlink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final InternshipRepository internshipRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting database initialization...");

        // Check if data already exists
        if (userRepository.findByEmail("tech@techwave.lk").isPresent()) {
            log.info("Database already initialized, skipping seed data");
            return;
        }

        // Create company users and companies
        createCompaniesAndInternships();

        log.info("Database initialization completed successfully");
    }

    private void createCompaniesAndInternships() {
        // 1. TechWave Solutions
        User techWaveUser = createCompanyUser("TechWave Solutions", "tech@techwave.lk", "password123");
        Company techWave = createCompany(techWaveUser, "TechWave Solutions", "https://www.techwave.lk", "Software Development");
        createInternship(techWave, "Junior Java Developer", "We are looking for a passionate Java developer to join our team. You will work on enterprise applications using Spring Boot.", "Colombo", "Java, Spring Boot, SQL", "3 months", LocalDate.now().plusDays(45));
        createInternship(techWave, "Frontend Developer (React)", "Help us build responsive web applications using React and TypeScript. Experience with modern CSS frameworks is a plus.", "Colombo", "React, TypeScript, CSS, HTML", "3 months", LocalDate.now().plusDays(50));
        createInternship(techWave, "Full Stack Developer", "Work on both frontend and backend systems. We use Java, React, and PostgreSQL.", "Colombo", "Java, React, PostgreSQL, REST APIs", "6 months", LocalDate.now().plusDays(60));
        createInternship(techWave, "DevOps Engineer", "Help us manage and deploy our cloud infrastructure using Docker and Kubernetes.", "Remote", "Docker, Kubernetes, AWS, Linux", "3 months", LocalDate.now().plusDays(40));

        // 2. Digital Dynamics Pvt Ltd
        User digitalUser = createCompanyUser("Digital Dynamics", "hr@digitaldynamics.com", "password123");
        Company digital = createCompany(digitalUser, "Digital Dynamics Pvt Ltd", "https://www.digitaldynamics.com", "Digital Marketing & Web Services");
        createInternship(digital, "Web Developer (Intern)", "Create and maintain websites using modern web technologies. Great opportunity to learn and grow.", "Colombo", "HTML, CSS, JavaScript, PHP", "3 months", LocalDate.now().plusDays(35));
        createInternship(digital, "UI/UX Designer", "Design beautiful and user-friendly interfaces for our web and mobile applications.", "Colombo", "Figma, UI Design, UX Principles", "3 months", LocalDate.now().plusDays(55));
        createInternship(digital, "Digital Marketing Executive", "Support our marketing team in executing campaigns and analyzing digital marketing metrics.", "Colombo", "Social Media, Google Analytics, SEO", "3 months", LocalDate.now().plusDays(48));
        createInternship(digital, "Mobile App Developer (Flutter)", "Develop cross-platform mobile applications using Flutter framework.", "Remote", "Flutter, Dart, Mobile Development", "6 months", LocalDate.now().plusDays(65));

        // 3. CloudTech Asia
        User cloudUser = createCompanyUser("CloudTech Asia", "jobs@cloudtech.asia", "password123");
        Company cloudTech = createCompany(cloudUser, "CloudTech Asia", "https://www.cloudtech.asia", "Cloud Computing & Infrastructure");
        createInternship(cloudTech, "Cloud Solutions Architect Intern", "Learn about designing scalable cloud solutions on AWS and Azure platforms.", "Colombo", "AWS, Azure, Cloud Architecture", "3 months", LocalDate.now().plusDays(42));
        createInternship(cloudTech, "Database Administrator", "Manage and optimize database systems. Experience with SQL and NoSQL databases required.", "Remote", "SQL, PostgreSQL, MongoDB, Database Optimization", "3 months", LocalDate.now().plusDays(50));
        createInternship(cloudTech, "Python Developer", "Write Python scripts for automation and backend services in our cloud infrastructure.", "Remote", "Python, Flask/Django, Linux", "3 months", LocalDate.now().plusDays(45));
        createInternship(cloudTech, "IT Support Technician", "Provide technical support to clients and manage IT infrastructure.", "Colombo", "Windows, Linux, Networking, Troubleshooting", "6 months", LocalDate.now().plusDays(58));

        // 4. InnovateLabs Sri Lanka
        User innovateUser = createCompanyUser("InnovateLabs", "careers@innovatelabs.lk", "password123");
        Company innovateLabs = createCompany(innovateUser, "InnovateLabs Sri Lanka", "https://www.innovatelabs.lk", "AI & Machine Learning");
        createInternship(innovateLabs, "Machine Learning Engineer", "Build and train machine learning models for real-world applications.", "Colombo", "Python, TensorFlow, Machine Learning, Data Science", "3 months", LocalDate.now().plusDays(40));
        createInternship(innovateLabs, "Data Analyst", "Analyze large datasets and create insights to drive business decisions.", "Remote", "Python, SQL, Tableau, Data Analysis", "3 months", LocalDate.now().plusDays(52));
        createInternship(innovateLabs, "AI Research Assistant", "Support research in artificial intelligence and deep learning applications.", "Colombo", "Python, PyTorch, Research Skills", "6 months", LocalDate.now().plusDays(70));
        createInternship(innovateLabs, "Business Analyst", "Gather requirements and analyze business processes for software solutions.", "Colombo", "Business Analysis, Documentation, SQL", "3 months", LocalDate.now().plusDays(46));

        // 5. SecureNet Solutions
        User secureUser = createCompanyUser("SecureNet", "recruitment@securenet.lk", "password123");
        Company secureNet = createCompany(secureUser, "SecureNet Solutions", "https://www.securenet.lk", "Cybersecurity");
        createInternship(secureNet, "Security Analyst Intern", "Learn about network security, penetration testing, and vulnerability assessment.", "Colombo", "Network Security, Linux, Penetration Testing", "3 months", LocalDate.now().plusDays(44));
        createInternship(secureNet, "Java Backend Developer", "Develop secure backend systems for our security solutions.", "Colombo", "Java, Spring Boot, Microservices, Security", "3 months", LocalDate.now().plusDays(51));
        createInternship(secureNet, "Quality Assurance Engineer", "Test security features and ensure quality of our applications.", "Remote", "QA Testing, Automation, Security Testing", "3 months", LocalDate.now().plusDays(49));
        createInternship(secureNet, "Systems Administrator", "Manage and maintain IT infrastructure with focus on security.", "Colombo", "Windows Server, Linux, Active Directory", "6 months", LocalDate.now().plusDays(62));

        // 6. EcomPlus Ltd
        User ecomUser = createCompanyUser("EcomPlus", "hr@ecomplus.lk", "password123");
        Company ecomPlus = createCompany(ecomUser, "EcomPlus Ltd", "https://www.ecomplus.lk", "E-commerce");
        createInternship(ecomPlus, "E-commerce Developer", "Build and maintain e-commerce platforms using modern web technologies.", "Colombo", "PHP, Laravel, MySQL, e-commerce platforms", "3 months", LocalDate.now().plusDays(47));
        createInternship(ecomPlus, "Product Manager Intern", "Assist in product development and management of e-commerce solutions.", "Colombo", "Product Management, Analytics, Communication", "3 months", LocalDate.now().plusDays(53));
        createInternship(ecomPlus, "Content Writer", "Create engaging product descriptions and marketing content for our e-commerce platform.", "Remote", "Content Writing, SEO, Digital Marketing", "3 months", LocalDate.now().plusDays(39));
        createInternship(ecomPlus, "Backend Engineer (Node.js)", "Develop scalable backend APIs for our e-commerce platform using Node.js.", "Remote", "Node.js, Express, MongoDB, REST APIs", "6 months", LocalDate.now().plusDays(64));

        log.info("Successfully created 6 companies with 24 internship opportunities");
    }

    private User createCompanyUser(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.COMPANY);
        user.setBlocked(false);
        User savedUser = userRepository.save(user);
        log.info("Created company user: {} with email: {}", name, email);
        return savedUser;
    }

    private Company createCompany(User user, String companyName, String website, String industry) {
        Company company = new Company();
        company.setUser(user);
        company.setCompanyName(companyName);
        company.setWebsite(website);
        company.setIndustry(industry);
        Company savedCompany = companyRepository.save(company);
        log.info("Created company: {} in industry: {}", companyName, industry);
        return savedCompany;
    }

    private void createInternship(Company company, String title, String description, String location, String skills, String duration, LocalDate deadline) {
        Internship internship = new Internship();
        internship.setCompany(company);
        internship.setTitle(title);
        internship.setDescription(description);
        internship.setLocation(location);
        internship.setSkills(skills);
        internship.setDuration(duration);
        internship.setDeadline(deadline);
        internship.setApproved(true); // Auto-approve for demo
        internship.setClosed(false);
        internshipRepository.save(internship);
        log.info("Created internship: {} for company: {}", title, company.getCompanyName());
    }
}
