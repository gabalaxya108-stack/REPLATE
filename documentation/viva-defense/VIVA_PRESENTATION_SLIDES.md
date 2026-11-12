# REPLATE: Food Waste Donation & Redistribution System
## Academic Capstone Viva Voce Presentation Deck
**Course:** Object-Oriented Programming Using Java (B.Tech CSE/IT)  
**Evaluation Standard:** Production-Grade Enterprise Engineering with Rigorous Java OOP Principles

---

## Slide 1: Title Slide
- **Project Title:** REPLATE: Food Waste Donation & Redistribution System
- **Tagline:** Rescue Food. Restore Hope.
- **Candidate:** [Student Name] (Roll No: [Roll Number / Registration ID])
- **Supervisor:** [Faculty Guide Name, Designation]
- **Department:** Department of Computer Science & Engineering
- **Institution:** [Institution / University Name]
- **Academic Year:** 2024–2025

> **Speaker Notes:**  
> "Good morning, respected external examiner and faculty members. Today I am presenting REPLATE, an enterprise-grade full-stack food donation and redistribution platform built with Java 21, Spring Boot 3.3.4, PostgreSQL 15, and React 18 with TypeScript. While this serves as our end-semester capstone project for Object-Oriented Programming Using Java, we have engineered it to the standard of a production startup."

---

## Slide 2: Problem Statement & Motivation
- **The Global & National Crisis:**
  - 19% of all food produced globally is wasted (UNEP Food Waste Index 2024).
  - Commercial kitchens (hotels, banquet halls, caterers, messes) routinely produce bulk edible surpluses.
  - Perishable cooked food has a narrow 4–12 hour safety window.
- **The Coordination Breakdown:**
  - Traditional redistribution relies on ad-hoc phone calls and uncoordinated WhatsApp groups.
  - Results in **inventory race conditions** (multiple NGOs claiming the same batch).
  - Lack of **chain-of-custody tracking** during volunteer transit.
  - Zero digital compliance with FSSAI recovery regulations.

> **Speaker Notes:**  
> "The core issue is not a shortage of food, nor a lack of hunger. It is a time-sensitive coordination and inventory concurrency failure. When commercial banquets finish at night, high-volume safe meals are discarded into landfills, generating methane. Our system bridges commercial surplus with verified welfare organizations through strict transactional consistency."

---

## Slide 3: Project Architecture & 4-Tier Design
- **Presentation Tier:** React 18.3 Single Page Application, TypeScript 5.5, Vite 5.4, Tailwind CSS 3.4.
- **Security & Filter Tier:** Stateless Spring Security 6 `SecurityFilterChain`, custom `JwtAuthenticationFilter` (HMAC-SHA256, 24-hr TTL).
- **Service Tier (Domain Logic):** Java 21 LTS micro-monolith, state machine domain entities, behavioral contracts, and ACID transaction boundaries.
- **Persistence Tier:** Spring Data JPA + Hibernate 6.5 ORM + HikariCP Connection Pool.
- **Relational Storage:** PostgreSQL 15 (3NF Normalized, row-level write locking).

*(Visual Reference: `figures/figure_01_system_architecture.png`)*

> **Speaker Notes:**  
> "We chose a 4-tier micro-monolithic architecture over distributed microservices. This guarantees immediate ACID consistency in PostgreSQL, eliminates inter-service network serialization overhead, and ensures atomic inventory locking while maintaining strict separation of concerns through clean Java package boundaries."

---

## Slide 4: Multi-Role Actor Boundaries
- **1. Commercial Donor (`ROLE_DONOR`):**
  - Publishes surplus food batches (`DRAFT` or `AVAILABLE`).
  - Reviews incoming NGO claims and approves/rejects them.
  - Executes pessimistic-locked inventory stock allocation.
  - Holds the secret 6-digit **Kitchen Pickup PIN**.
- **2. Verified NGO (`ROLE_NGO`):**
  - Discovers active surplus via the public food feed.
  - Submits formal food claims with beneficiary headcounts.
  - Holds the secret 6-digit **Delivery Confirmation PIN**.
- **3. Logistics Volunteer (`ROLE_VOLUNTEER`):**
  - Accepts transit dispatch missions.
  - Verifies Kitchen Pickup PIN to take physical custody.
  - Verifies Delivery Confirmation PIN at destination shelter.
- **4. System Administrator (`ROLE_ADMIN`):**
  - Platform governance, compliance verification, and audit ledger monitoring. Public registration strictly forbidden (HTTP 403).

*(Visual Reference: `figures/figure_02_use_case_diagram.png`)*

> **Speaker Notes:**  
> "Our platform partitions user permissions using Spring Security 6 role-based access control. Each actor has a dedicated operational boundary, ensuring zero unauthorized cross-role resource manipulation."

---

## Slide 5: Core Java OOP Concepts (19 Principles Implemented)
- **1. Encapsulation:** Guarded mutations in `FoodDonation.java` (`deductQuantity()`, `publish()`).
- **2. Inheritance:** Unified `IS-A` hierarchy rooted at `@MappedSuperclass BaseEntity`.
- **3. Runtime Polymorphism:** Dynamic dispatch via `Trackable` implemented by `FoodDonation` and `DeliveryTask`.
- **4. Compile-Time Polymorphism:** Method overloading in `ImpactCalculator.java` (`calculateCo2Offset`).
- **5. Abstraction:** Abstract `BaseEntity` managing identity, audit timestamps, and `@Version`.
- **6. Interfaces:** Behavioral contracts `Trackable` and `Approvable`.
- **7. Composition:** Value-object composition via `@Embedded LocationCoordinate`.
- **8. Constructor Overloading:** Dual constructors (JPA reflection vs domain creation).
- **9. Generics:** Type-safe API response envelope `ApiResponse<T>`.
- **10. Rich Enums:** Domain logic inside `DonationStatus`, `RequestStatus`, etc.

> **Speaker Notes:**  
> "Unlike anemic domain models where classes are passive getter-setter bags, REPLATE's domain entities encapsulate their own validation and business invariants. We distinguish core Java language OOP from Spring Framework mechanisms."

---

## Slide 6: Deep Dive: Runtime Polymorphism (`Trackable`)
```java
// Behavioral Contract
public interface Trackable {
    String getTrackingReferenceCode();
    String getCurrentStatusDescription();
    int getProgressPercentage();
    boolean isTerminalState();
}

// Implemented by heterogeneous entities:
public class FoodDonation extends BaseEntity implements Trackable { ... }
public class DeliveryTask extends BaseEntity implements Trackable { ... }
```
- **Architectural Value:** The tracking dashboard treats both food listings and transit dispatches polymorphically through `Trackable`.
- Dynamic method dispatch executes the concrete subclass method at runtime without coupling the caller to database schemas.

> **Speaker Notes:**  
> "Here is our implementation of runtime dynamic dispatch. Both FoodDonation and DeliveryTask implement Trackable. When querying progress, the system invokes Trackable.getProgressPercentage() polymorphically, decoupling the presentation layer from underlying persistence differences."

---

## Slide 7: Compile-Time Polymorphism (`ImpactCalculator`)
```java
public class ImpactCalculator {
    // Overloaded Signature 1: Gross Weight in Kilograms
    public BigDecimal calculateCo2Offset(BigDecimal weightInKg) {
        return weightInKg.multiply(new BigDecimal("2.50"));
    }

    // Overloaded Signature 2: Discrete Meal Box Units & Category
    public BigDecimal calculateCo2Offset(Integer mealBoxes, FoodCategory category) {
        BigDecimal baseWeight = new BigDecimal(mealBoxes).multiply(new BigDecimal("0.45"));
        return baseWeight.multiply(category.getEmissionsMultiplier());
    }
}
```
- **Architectural Value:** The Java compiler resolves the exact method signature at compile time based on parameter types and counts.

> **Speaker Notes:**  
> "For compile-time polymorphism, our ImpactCalculator overloads methods to accept either continuous weight measurements or discrete meal box counts, allowing callers to compute carbon offsets without manual conversion scripts."

---

## Slide 8: Relational Database Architecture (PostgreSQL 15)
- **Third Normal Form (3NF) Compliance:** Zero partial or transitive functional dependencies.
- **9 Relational Tables:**
  - `app_user` (Universal identity)
  - `donor_profile`, `ngo_profile`, `volunteer_profile` (Specialized 1:1 profiles sharing `user_id` via JPA `@MapsId`)
  - `food_donation` (Inventory batch listings with `@Version`)
  - `food_request` (NGO claims)
  - `allocation` (Committed inventory stock)
  - `delivery_task` (Volunteer transit with unique allocation link)
  - `audit_log` (Immutable security audit ledger)

*(Visual Reference: `figures/figure_03_er_diagram.png`)*

> **Speaker Notes:**  
> "We avoided a monolithic single-table user model. By using polymorphic composition with shared primary keys via JPA @MapsId, each role maintains its own dedicated table with strict constraints (e.g. FSSAI licenses, Darpan IDs) while sharing a single authentication record."

---

## Slide 9: Concurrency Control & The Allocation Engine
- **The Problem:** 5 competing NGOs submit claims for a 25-box batch simultaneously.
- **The Solution:** Dual Pessimistic Write Locking (`SELECT ... FOR UPDATE` in PostgreSQL):
```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public AllocationResponse createAllocation(Long donorUserId, CreateAllocationRequest req) {
    // 1. Lock Request Row
    FoodRequest request = requestRepo.findByIdWithPessimisticLock(req.getRequestId());
    
    // 2. Lock Donation Row (Blocks competing threads)
    FoodDonation donation = donationRepo.findByIdWithPessimisticLock(request.getDonation().getId());
    
    // 3. Re-read Authoritative Available Quantity & Deduct
    donation.deductQuantity(req.getAllocatedQuantity());
    
    // 4. Persist Allocation Record
    return allocationRepo.save(allocation);
}
```
- **Stress Test Verified:** 5 concurrent threads executing allocations serialized with **zero overselling** and zero negative inventory balance.

*(Visual Reference: `figures/figure_06_request_allocation_sequence.png`)*

> **Speaker Notes:**  
> "This is the core transactional innovation in REPLATE. When allocating inventory, optimistic locking would cause mass rollbacks. We use JPA pessimistic write locking, issuing PostgreSQL SELECT FOR UPDATE locks on both the request and donation. Competing threads are serialized, eliminating race conditions entirely."

---

## Slide 10: Volunteer Logistics & Dual-OTP Verification Handshake
- **The Problem:** In transit, food can be misplaced, dropped, or claimed by unauthorized third parties.
- **The Solution:** Cryptographic Dual-PIN Verification Handshake:
  - **Kitchen Pickup PIN (6 digits):** Generated upon allocation, delivered exclusively to the Donor. Verified by Volunteer at the donor kitchen.
  - **Delivery Confirmation PIN (6 digits):** Delivered exclusively to the receiving NGO. Verified by Volunteer upon physical dropoff.
- **Zero Stock Re-Deduction:** Pickup and delivery transitions advance task status and persist audit timestamps, but *never* touch inventory stock.

*(Visual Reference: `figures/figure_07_volunteer_delivery_sequence.png`)*

> **Speaker Notes:**  
> "To guarantee end-to-end chain of custody, our delivery engine generates two distinct 6-digit PINs. The volunteer must collect the Kitchen Pickup PIN from the donor, and the Delivery Confirmation PIN from the NGO. Crucially, these handshakes update timestamps without altering inventory stock, preventing double-deduction accounting bugs."

---

## Slide 11: Security & Stateless JWT Authentication
- **Spring Security 6 Architecture:**
  - Stateless request filtering via `JwtAuthenticationFilter`.
  - Passwords hashed using **BCrypt** (work factor 12, secure 128-bit random salt).
  - Signed tokens (JJWT 0.12.6, HMAC-SHA256, 24-hr expiry).
  - Sensitive attributes (`password_hash`) suppressed from all DTOs and JSON serialization.
  - Public self-registration as `ADMIN` strictly forbidden (HTTP 403 Forbidden).

*(Visual Reference: `figures/figure_08_auth_jwt_sequence.png`)*

> **Speaker Notes:**  
> "Our security architecture uses stateless JWT authentication with Spring Security 6. Passwords use BCrypt hashing with work factor 12. Every request is verified through our filter chain, populating the SecurityContext with granted role authorities."

---

## Slide 12: Environmental & Social Impact Assessment
- **Mathematical Emissions Model:**
  $$\text{CO}_2\text{e Offset (kg)} = \text{Rescued Weight (kg)} \times 2.50$$
  $$\text{Meal Box Offset} = (\text{Boxes} \times 0.45\text{ kg}) \times 2.50 = \text{Boxes} \times 1.125\text{ kg CO}_2\text{e}$$
- **Economic Value Preserved:**
  $$\text{Value Saved (INR)} = \text{Meals Rescued} \times ₹60.00$$
- **Real-Time Reporting:** Live aggregate impact counters displayed on the public landing page feed corporate ESG and CSR audit reporting.

> **Speaker Notes:**  
> "We grounded our impact assessment on the standard UNEP landfill diversion factor of 2.5 kg CO2e avoided per kilogram of food waste. Furthermore, we compute monetary capital preserved at ₹60 per meal, proving direct economic benefit to humanitarian shelters."

---

## Slide 13: Quality Assurance & Automated Test Results
- **Automated Test Suite Status:** **73 / 73 Tests Passing (100% Pass Rate)**
- **Test Suite Breakdown:**
  - `FoodAllocationLifecycleIntegrationTest`: **17 Tests** (ACID Concurrency, Pessimistic Locking)
  - `DeliveryLifecycleIntegrationTest`: **22 Tests** (Dual-OTP Handshakes, State Machine)
  - `FoodDonationLifecycleIntegrationTest`: **9 Tests** (Lifecycle, Expiry Rules)
  - `FoodRequestLifecycleIntegrationTest`: **10 Tests** (NGO Claims, Approval Decoupling)
  - `AuthenticationFlowIntegrationTest`: **12 Tests** (JWT, BCrypt, RBAC Gateways)
  - `DonorRegistrationPersistenceE2ETest`: **1 Test** (PostgreSQL E2E Persistence)
  - `PasswordEncoderTest`: **1 Test** (BCrypt Work Factor 12 Random Salt)
  - `ReplateApplicationTests`: **1 Test** (Context Loads & Live PostgreSQL Connectivity)

> **Speaker Notes:**  
> "Our codebase is backed by 73 automated integration, unit, and concurrency tests executing against a real PostgreSQL instance. All 73 tests pass with a 100% success rate, confirming the structural reliability of every domain service."

---

## Slide 14: Live System Demonstration
- **1. Public Explore Feed (`http://localhost:3000/explore`):** 9 active fresh listings across Bengaluru, Delhi, Mumbai with live countdown timers.
- **2. Donor Console (`contact@saravanabhavan.in`):** Managing inventory, reviewing incoming NGO claims, and viewing Kitchen Pickup PIN.
- **3. NGO Console (`foodrescue@robinhoodarmy.org`):** Claiming food batches, tracking approvals, and inspecting Delivery Confirmation PIN.
- **4. Volunteer Logistics Hub (`aarav.sharma@gmail.com`):** Two-stage OTP verification modals for pickup and delivery handshakes.

*(Visual Reference: Screenshots 9.1 through 9.10)*

> **Speaker Notes:**  
> "We will now demonstrate the running application live on port 3000, walking through the donor posting, NGO claiming, inventory allocation, and volunteer PIN verification workflows."

---

## Slide 15: Limitations & Future Scope
- **Current Limitations:**
  - Database locking is localized to a single PostgreSQL instance (multi-region clusters would use distributed Redis Redlocks).
  - In-app PIN delivery rather than commercial paid SMS gateways (Twilio / MSG91).
  - Static Haversine spherical distance calculation rather than live road routing APIs.
- **Future Roadmap:**
  - Real-time STOMP WebSockets for instant push alerts when donors post surplus.
  - Algorithmic matching engine pairing batches based on NGO daily capacity and distance.
  - Offline Progressive Web App (PWA) with cryptographic offline delivery signatures.

> **Speaker Notes:**  
> "While the current system fulfills all academic and production requirements for urban centers, future extensions include WebSocket push notifications, automated matching algorithms, and distributed cache locks for nationwide scale."

---

## Slide 16: Conclusion & Retrospective
- **Key Achievements:**
  - Fully functional full-stack food redistribution platform connecting Donors, NGOs, and Volunteers.
  - Concrete verification of 19 core Java OOP principles in a real-world enterprise architecture.
  - Concurrency-safe inventory allocation via pessimistic write locking.
  - End-to-end chain of custody with zero stock re-deduction bugs.
  - Complete academic deliverables: 52-page formal report ([`.pdf`](file:///Users/laxyagaba/java%20project/documentation/final-report/REPLATE_Final_Project_Report.pdf), [`.docx`](file:///Users/laxyagaba/java%20project/documentation/final-report/REPLATE_Final_Project_Report.docx), [`.md`](file:///Users/laxyagaba/java%20project/documentation/final-report/REPLATE_Final_Project_Report.md)), 8 UML figures, 10 UI screenshots, and 73 passing tests.

> **Speaker Notes:**  
> "In conclusion, REPLATE demonstrates that Object-Oriented Programming in Java 21 is not just an academic curriculum subject, but an indispensable engineering methodology for building reliable, concurrent, and socially impactful software. Thank you, and I am now ready for your questions."

