# REPLATE — Food Waste Donation & Redistribution System
> **Rescue Food. Restore Hope.**  
> *Academic Course: Object-Oriented Programming Using Java (B.Tech CSE/IT)*  
> *Package Root: `com.replate`*  
> *Status: Production-Ready & Formally Verified (73/73 Tests Passing)*

---

## 🌿 Project Overview
**REPLATE** is an enterprise-grade full-stack food recovery platform designed to connect commercial food establishments (restaurants, banquet halls, university messes, corporate caterers) across Indian metropolitan clusters with certified NGOs, community kitchens, and volunteer logistics networks.

By digitizing surplus food donation, real-time allocation, and chain-of-custody delivery, REPLATE redirects safe, edible meals away from landfills and toward vulnerable communities, cutting methane emissions while addressing hunger.

---

## 📚 Academic Capstone Deliverables

All formal university examination and evaluation documents are generated, verified, and committed:

| Deliverable | File Path | Format & Details |
|---|---|---|
| **Final Academic Project Report (PDF)** | [`documentation/final-report/REPLATE_Final_Project_Report.pdf`](documentation/final-report/REPLATE_Final_Project_Report.pdf) | 52-Page Publication-Grade PDF, Running Headers, 20 Tables, 8 UML Diagrams, 10 Authentic UI Captures. |
| **Final Academic Project Report (DOCX)** | [`documentation/final-report/REPLATE_Final_Project_Report.docx`](documentation/final-report/REPLATE_Final_Project_Report.docx) | Fully Editable Microsoft Word Document with Academic 1-Inch Margins. |
| **Full Report Source (Markdown)** | [`documentation/final-report/REPLATE_Final_Project_Report.md`](documentation/final-report/REPLATE_Final_Project_Report.md) | GitHub Flavored Markdown (110 KB). |
| **Traceability & Verification Matrix** | [`documentation/final-report/REPORT_VERIFICATION.md`](documentation/final-report/REPORT_VERIFICATION.md) | Audit Matrix cross-referencing all 19 OOP concepts, 9 database tables, and 73 test suites. |
| **Viva Voce Presentation Slides** | [`documentation/viva-defense/VIVA_PRESENTATION_SLIDES.md`](documentation/viva-defense/VIVA_PRESENTATION_SLIDES.md) | 16-Slide Academic Defense Presentation with word-for-word Speaker Scripts. |
| **Examiner Live Demo Script** | [`documentation/viva-defense/LIVE_DEMO_EXAMINER_SCRIPT.md`](documentation/viva-defense/LIVE_DEMO_EXAMINER_SCRIPT.md) | Step-by-Step 5-Minute Practical Walkthrough for the live software demonstration. |
| **Viva Q&A Defense Cheat Sheet** | [`documentation/viva-defense/EXAMINER_QA_CHEAT_SHEET.md`](documentation/viva-defense/EXAMINER_QA_CHEAT_SHEET.md) | 15 Tough Technical Questions with Model Answers on Java OOP, Concurrency, and Security. |

---

## 🛠 Technology Stack

### Backend
- **Language:** Java 21 LTS
- **Framework:** Spring Boot 3.3.4
- **Persistence & ORM:** Spring Data JPA / Hibernate 6.5.3 (HikariCP Connection Pool)
- **Security:** Spring Security 6 + Stateless JWT Auth (JJWT 0.12.6, HMAC-SHA256, BCrypt cost 12)
- **Database Engine:** PostgreSQL 15 (Relational, 3NF normalized, row-level write locking)
- **Build Tool:** Apache Maven 3.9+
- **Quality Assurance:** JUnit 5, MockMvc, AssertJ (73 Tests, 100% Pass Rate)

### Frontend
- **Framework:** React 18.3 with TypeScript 5.5
- **Build Tool & Dev Server:** Vite 5.4 (sub-second HMR, 1.3s production build)
- **Styling:** Tailwind CSS 3.4
- **Icons & Motion:** Lucide React, Framer Motion
- **HTTP Client & Routing:** Axios, React Router v6

---

## 🏛 19 Core Java OOP Concepts Implemented

1. **Encapsulation:** Guarded mutations in `FoodDonation.java` (`deductQuantity()`, `publish()`).
2. **Inheritance:** Genuine `IS-A` hierarchy rooted at `@MappedSuperclass BaseEntity`.
3. **Runtime Polymorphism:** Dynamic method dispatch via pure interface `Trackable` implemented by `FoodDonation` and `DeliveryTask`.
4. **Compile-Time Polymorphism:** Method overloading in `ImpactCalculator.java` (`calculateCo2Offset`).
5. **Abstraction:** Abstract class `BaseEntity` managing identity, audit timestamps, and `@Version`.
6. **Interfaces:** Behavioral contracts `Trackable` and `Approvable`.
7. **Composition & Embeddables:** Value-object composition via `@Embedded LocationCoordinate`.
8. **Constructor Overloading:** Dual constructors (JPA reflection vs rich domain instantiation).
9. **Java Generics:** Type-safe API envelope `ApiResponse<T>`.
10. **Rich Enums:** Business logic encapsulated inside `DonationStatus`, `RequestStatus`, etc.
11. **Static Factory Methods:** `ApiResponse.success()`, `ApiResponse.error()`, `LocationCoordinate.of()`.
12. **Defensive Immutability:** Final attributes with boundary range validation on instantiation.
13. **Custom Exception Hierarchy:** Class tree rooted at `ReplateException extends RuntimeException`.
14. **Polymorphic Exception Handling:** Global `@ExceptionHandler(ReplateException.class)` controller advice.
15. **Polymorphic Entity Composition:** Role specialization using 1:1 composition with shared primary keys via `@MapsId`.
16. **State Machine Encapsulation:** Guarded domain transitions coupled with Spring Data JPA `Specification` queries.
17. **Contract-Driven Separation of Concerns:** Decoupling claim approval from inventory stock deduction.
18. **Pessimistic Concurrency Locking:** ACID-compliant row-level write locks (`SELECT ... FOR UPDATE`) in PostgreSQL.
19. **Dual-OTP Chain of Custody:** Cryptographic 6-digit Kitchen Pickup PIN and Delivery Confirmation PIN handshakes with zero stock re-deduction.

---

## 👥 Demo Accounts & Evaluation Credentials

All demo accounts share the password: **`Password@123`**

| Role | Organization / User Name | Login Email | Location & Notes |
|---|---|---|---|
| **DONOR** | **Saravana Bhavan Foods Pvt Ltd** | `contact@saravanabhavan.in` | Bengaluru — Commercial Restaurant (FSSAI Verified) |
| **DONOR** | **Haldirams Sweets & Catering** | `events@haldiramsdelhi.com` | New Delhi — Caterer & Sweets |
| **DONOR** | **Trident Nariman Point Banquets** | `banquets@tridentmumbai.com` | Mumbai — Luxury Banquets & Events |
| **DONOR** | **The Taj Mahal Palace Banquets** | `banquets@tajpalacemumbai.com` | Mumbai — 5-Star Hotel Banquet |
| **DONOR** | **Vidyarthi Bhavan Heritage** | `contact@vidyarthibhavan.in` | Bengaluru — South Indian Heritage |
| **DONOR** | **Pind Balluchi Heritage** | `catering@pindballuchi.in` | Gurugram / Delhi NCR — North Indian Caterer |
| **DONOR** | **bhojan (Personal Account)** | `gabalaxya108@gmail.com` | Bengaluru — Commercial Restaurant |
| **NGO** | **Robin Hood Army (Bengaluru)** | `foodrescue@robinhoodarmy.org` | Bengaluru — Capacity: 450/day (Darpan Verified) |
| **NGO** | **Roti Bank Mumbai Foundation** | `relief@rotibankmumbai.org` | Mumbai — Capacity: 800/day (Darpan Verified) |
| **NGO** | **Care India Feeding Mission** | `feedingdelhi@careindia.org` | New Delhi — Capacity: 600/day (Darpan Verified) |
| **VOLUNTEER**| **Aarav Sharma** | `aarav.sharma@gmail.com` | New Delhi — Two-Wheeler |
| **VOLUNTEER**| **Pooja Deshmukh** | `pooja.deshmukh@gmail.com` | Mumbai — Four-Wheeler |
| **VOLUNTEER**| **Vikram Patil** | `vikram.patil@gmail.com` | Bengaluru — Auto-Van |
| **ADMIN** | **Replate Systems Admin** | `admin@replate.org` | Governance & Compliance Auditing |

---

## 🚀 Quick Start Guide

### 1. Database Setup (PostgreSQL 14+)
```bash
# Create database and seed fresh production demo data
psql -U postgres -c "CREATE DATABASE replate_db;"
psql -U postgres -d replate_db -f database/schema.sql
psql -U postgres -d replate_db -f database/seed-data.sql
```

### 2. Backend Execution (Spring Boot)
```bash
cd backend
mvn clean test        # Executes all 73 automated tests (100% pass)
mvn spring-boot:run  # Launches API on http://localhost:8080
```

### 3. Frontend Execution (React + Vite)
```bash
cd frontend
npm install
npm run build        # Verifies TypeScript compilation
npm run dev          # Launches SPA on http://localhost:3000
```

---

## 🧪 Automated Test Suite Verification

```text
-------------------------------------------------------
 T E S T S   E X E C U T I O N   S U M M A R Y
-------------------------------------------------------
Running com.replate.allocation.FoodAllocationLifecycleIntegrationTest:  17 Tests (PASS)
Running com.replate.delivery.DeliveryLifecycleIntegrationTest:        22 Tests (PASS)
Running com.replate.donation.FoodDonationLifecycleIntegrationTest:      9 Tests (PASS)
Running com.replate.request.FoodRequestLifecycleIntegrationTest:       10 Tests (PASS)
Running com.replate.security.AuthenticationFlowIntegrationTest:        12 Tests (PASS)
Running com.replate.security.DonorRegistrationPersistenceE2ETest:       1 Test  (PASS)
Running com.replate.security.PasswordEncoderTest:                       1 Test  (PASS)
Running com.replate.ReplateApplicationTests:                            1 Test  (PASS)
-------------------------------------------------------
BUILD SUCCESS — Total: 73 Tests Run, 0 Failures, 0 Errors (100% Passing)
-------------------------------------------------------
```

---

## 📄 License
This academic project is developed for educational and research purposes in partial fulfillment of the requirements for Bachelor of Technology in Computer Science & Engineering.
