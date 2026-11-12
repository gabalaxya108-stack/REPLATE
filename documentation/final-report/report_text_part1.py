"""
REPLATE Academic Project Report - Text Part 1
Covers Front Matter and Chapters 1 through 7.
"""

FRONT_MATTER_TEXT = {
    "certificate": """
This is to certify that the project entitled "REPLATE: Food Waste Donation & Redistribution System" submitted by [Student Name] (Roll No: [Roll Number / Registration ID]) in partial fulfillment of the requirements for the award of the degree of Bachelor of Technology in Computer Science & Engineering from [Institution / University Name] is a bona fide record of the work carried out under my supervision and guidance during the academic year 2024–2025.

The results embodied in this report have not been submitted to any other University or Institute for the award of any degree or diploma.

Signature of Guide: ________________________
[Faculty Guide Name, Designation]
Department of Computer Science & Engineering
[Institution / University Name]

Signature of Head of Department: ________________________
[Head of Department Name]
Department of Computer Science & Engineering
[Institution / University Name]

Date: [Submission Date]
Place: [Institution City, State]
""",
    "declaration": """
I, [Student Name], hereby declare that the project report entitled "REPLATE: Food Waste Donation & Redistribution System" submitted to [Institution / University Name] in partial fulfillment of the requirements for the degree of Bachelor of Technology in Computer Science & Engineering is an authentic record of original work conducted by me under the supervision of [Faculty Guide Name, Designation].

I further confirm that:
1. The software codebase, design patterns, entity hierarchies, database schema, and test suites presented in this report represent authentic engineering artifacts implemented specifically for this project.
2. The empirical test execution metrics (73 automated unit, integration, concurrency, and security tests) reflect genuine executions conducted against a local PostgreSQL 15 database instance and Java 21 LTS virtual machine.
3. This work has not been previously submitted, in part or in whole, to any other institution for the award of any academic qualification or distinction.

Signature of Candidate: ________________________
[Student Name]
Roll Number: [Roll Number / Registration ID]
Date: [Submission Date]
""",
    "acknowledgement": """
The successful completion of this academic capstone project would not have been possible without the invaluable guidance, support, and resources provided by several individuals and institutions.

First and foremost, I express my deepest gratitude to my project guide, [Faculty Guide Name, Designation], Department of Computer Science & Engineering, for their continuous mentorship, rigorous technical review, and invaluable suggestions throughout the architecture, implementation, and verification phases of REPLATE. Their emphasis on core Java Object-Oriented principles, robust domain modeling, and concurrency safety was instrumental in elevating this project beyond a standard academic exercise.

I would also like to extend my sincere appreciation to [Head of Department Name], Head of the Department of Computer Science & Engineering, and the faculty members of the department for providing state-of-the-art laboratory facilities, academic infrastructure, and encouragement throughout my academic journey.

Finally, I express my heartfelt gratitude to my family and peers for their constant encouragement, patience, and motivation during intensive development and testing sessions.

[Student Name]
Bachelor of Technology in Computer Science & Engineering
""",
    "abstract": """
Food wastage alongside widespread nutritional insecurity represents one of the most pressing socio-economic and environmental paradoxes of the modern world. Millions of tons of edible prepared food from commercial establishments—including hotels, banquet halls, corporate cafeterias, and restaurants—are discarded daily into municipal landfills, generating methane emissions, while marginalized communities suffer from persistent food deficits. Existing redistribution efforts remain largely informal, manual, and reliant on ad-hoc phone coordination, resulting in spoilage, logistical bottlenecks, lack of accountability, and inventory race conditions.

This project presents REPLATE, an enterprise-grade full-stack food donation and redistribution platform architected using Java 21, Spring Boot 3.3.4, PostgreSQL 15, and React 18 with TypeScript. The core objective is to translate theoretical Object-Oriented Programming (OOP) concepts—encapsulation, inheritance, runtime and compile-time polymorphism, abstraction, behavioral interfaces, composition, and custom exception hierarchies—into a resilient, production-grade web application. 

REPLATE introduces a Transactional Food Allocation Engine utilizing pessimistic database write locking (SELECT ... FOR UPDATE) to eliminate inventory overselling under high concurrency, an encapsulated dual-OTP chain-of-custody verification handshake that secures volunteer logistics without double stock deduction, and a mathematically grounded environmental and financial impact calculator. The system implements stateless JSON Web Token (JWT) authentication, Spring Security 6 role-based access control across four institutional roles (Donor, NGO, Volunteer, Administrator), and an immutable relational schema normalized to Third Normal Form (3NF). 

The platform has been rigorously validated through an automated test suite comprising 73 unit, integration, concurrency, and end-to-end tests achieving a 100% pass rate. By bridging commercial surplus with humanitarian need through strict object-oriented modeling and transactional consistency, REPLATE demonstrates how modern software engineering principles solve real-world logistical and environmental challenges.
"""
}

CHAPTERS_PART1 = {
    "ch1": {
        "title": "Chapter 1: Introduction",
        "content": """
### 1.1 Background & Context
Food loss and waste is a global challenge with severe ethical, environmental, and financial repercussions. According to the United Nations Environment Programme (UNEP) Food Waste Index Report, approximately 19% of all food produced globally is wasted at the retail, food service, and household levels. Concurrently, millions of vulnerable citizens experience acute nutritional deficiencies. In urban environments, commercial establishments such as banquet halls, five-star hotels, university messes, corporate cafeterias, and catering enterprises routinely produce high-volume edible surpluses. Due to strict internal culinary standards and shifting banquet headcounts, large batches of freshly cooked, safe food are discarded at the end of service windows.

The barrier to effective food redistribution is not a lack of surplus or a lack of hunger; rather, it is a complex logistical and technological coordination problem. Surplus prepared food is inherently perishable, possessing a narrow safe consumption window typically ranging between 4 to 12 hours depending on ambient temperature and preparation methods. Traditional charitable redistribution relies on ad-hoc phone calls, uncoordinated volunteer networks, and informal messaging groups. These legacy mechanisms fail to provide real-time inventory visibility, fail to enforce Food Safety and Standards Authority of India (FSSAI) compliance, lack verifiable chain-of-custody tracking, and are susceptible to inventory race conditions when multiple welfare organizations attempt to claim the same batch of food simultaneously.

### 1.2 Problem Statement
Commercial food donors lack a streamlined, legally compliant, and legally auditable digital channel to publish time-sensitive food surplus listings. Concurrently, verified Non-Governmental Organizations (NGOs) and humanitarian shelters lack an authenticated discovery platform to claim specific quantities tailored to their beneficiary capacities. Furthermore, existing volunteer logistics lack cryptographic verification handshakes at pickup and delivery points, leading to unaccounted discrepancies, dropped shipments, and liability concerns. 

From a software engineering perspective, building such a platform requires solving non-trivial technical challenges:
1. **Concurrency Control:** Ensuring that concurrent food requests from multiple NGOs cannot cause inventory overselling (negative stock anomalies).
2. **State Machine Integrity:** Enforcing strict, forward-only lifecycle state transitions across donations, claims, allocations, and volunteer delivery runs.
3. **Auditable Chain-of-Custody:** Guaranteeing physical handover verification between donors, transit volunteers, and receiving NGOs without relying on unverified claims.
4. **Domain Modeling:** Applying rigorous Object-Oriented Analysis and Design (OOAD) in Java 21 to create modular, maintainable, extensible, and clean domain services decoupled from framework infrastructure.

### 1.3 Motivation
The primary motivation behind REPLATE is twofold:
1. **Societal & Environmental Impact:** Diverting high-quality surplus food from landfills directly to verified feeding programs, preventing methane emissions and providing direct nutritional support to vulnerable populations.
2. **Academic & Architectural Rigor:** Serving as an exemplary capstone project for *Object-Oriented Programming Using Java*, demonstrating how core language tenets—encapsulation, genuine inheritance hierarchies, runtime polymorphism, compile-time overloading, abstract contracts, value object composition, custom exception trees, and pessimistic concurrency control—are implemented in a production-ready enterprise stack.

### 1.4 Objectives
The primary technical and operational objectives of the REPLATE system are:
- **Architect a Modular Java Backend:** Implement a robust Spring Boot 3.3.4 micro-monolith utilizing Java 21 LTS features, clean architecture, and strict separation of concerns.
- **Implement 19 Concrete OOP Concepts:** Verify every theoretical object-oriented principle through tangible, readable, and testable Java source code.
- **Design a High-Integrity Relational Schema:** Construct an 8-table PostgreSQL database schema adhering to 3NF, utilizing foreign keys, check constraints, unique indices, and optimistic locking (@Version).
- **Engineer a Concurrency-Safe Allocation Engine:** Implement pessimistic write locking (`SELECT ... FOR UPDATE`) in PostgreSQL via Spring Data JPA to eliminate race conditions during high-concurrency food claims.
- **Establish a Dual-OTP Chain-of-Custody Handshake:** Build an end-to-end volunteer delivery dispatch workflow secured by 6-digit Kitchen Pickup PINs and Delivery Confirmation PINs.
- **Deliver a Responsive, Modern User Experience:** Develop an intuitive React 18, TypeScript, and Tailwind CSS frontend featuring role-tailored dashboards for Donors, NGOs, and Volunteers.
- **Achieve Comprehensive Test Coverage:** Write and maintain an automated test suite comprising unit, integration, transactional, and security tests with a 100% pass rate.

### 1.5 Scope & System Boundaries
The scope of the REPLATE platform encompasses:
- Institutional authentication and profile management for Donors (restaurants, banquet halls), NGOs (charitable trusts, shelters), Volunteers, and System Administrators.
- Surplus food batch publishing, discovery, and filtering based on category, dietary preference, and geographical locality.
- Claim submission, formal donor approval/rejection, and atomic allocation locking.
- Volunteer assignment, formal acceptance/rejection, route status updates, and dual-OTP verification.
- Mathematical calculation of CO2 equivalent offset (kg CO2e) and financial value saved (INR).
- Automated test verification across 73 integration and unit test scenarios.

**Out of Scope for Current Academic Release:**
- Third-party SMS gateway integration (simulated via in-app PIN delivery and authenticated API inspection).
- Native GPS hardware telematics (simulated via coordinate distance calculations and route status state transitions).
- Payment gateway processing (REPLATE is strictly non-commercial and humanitarian).

### 1.6 Organization of the Report
This academic report is organized into 14 distinct chapters:
- **Chapter 1:** Introduction, problem statement, objectives, and project scope.
- **Chapter 2:** Literature review, commercial competitor analysis, and research gaps.
- **Chapter 3:** Software Requirements Specification (SRS), user personas, and non-functional requirements.
- **Chapter 4:** System architecture, 4-tier layered design, and technology stack justification.
- **Chapter 5:** Detailed Object-Oriented Analysis & Design (OOAD), class diagrams, and the 19 OOP concepts matrix.
- **Chapter 6:** Database architecture, Entity-Relationship design, data dictionary, and concurrency locking strategies.
- **Chapter 7:** Security architecture, stateless JWT authentication, Spring Security 6 filter chains, and RBAC.
- **Chapter 8:** Core module implementation details across donations, requests, allocations, and deliveries.
- **Chapter 9:** Frontend architecture, React component trees, responsive design, and user interfaces.
- **Chapter 10:** Environmental and financial impact assessment formulations.
- **Chapter 11:** Testing methodology, test cases, concurrency stress results, and quality assurance.
- **Chapter 12:** Deployment, environment configuration, database seeding, and reviewer setup instructions.
- **Chapter 13:** Technical limitations, architectural trade-offs, and future research directions.
- **Chapter 14:** Academic conclusions and retrospective reflections.
"""
    },
    "ch2": {
        "title": "Chapter 2: Literature Review & Market Study",
        "content": """
### 2.1 Review of Existing Food Redistribution Systems
The challenge of food rescue has prompted multiple digital and community-driven initiatives worldwide. Broadly, existing solutions fall into three operational paradigms:

1. **Commercial Surplus Marketplaces (e.g., Too Good To Go, Karma):**
   These platforms operate on a B2C discounted retail model. Restaurants and bakeries package unsold surplus into 'surprise bags' sold to retail consumers at 30–50% of the original retail price. While economically viable for commercial vendors, these systems do not serve impoverished or destitute populations who cannot afford micro-transactions. Furthermore, they are unsuitable for bulk catering donations (e.g., 200 meal boxes from an event).

2. **Peer-to-Peer Neighborhood Sharing (e.g., Olio):**
   Olio enables individual citizens and neighborhood volunteers to photograph and post surplus household groceries for pickup by local neighbors. While highly successful in domestic settings, Olio lacks institutional compliance mechanisms. Commercial entities require FSSAI verification, institutional accountability, structured logistics, and formal tax/compliance documentation, none of which are accommodated in consumer-to-consumer models.

3. **Volunteer-Driven Humanitarian Collectives (e.g., Robin Hood Army, Feeding India by Zomato):**
   The Robin Hood Army represents a hyper-local, volunteer-driven model that collects surplus food from weddings and restaurants and distributes it to slums. While spiritually aligned with REPLATE, the operational backbone of these initiatives relies predominantly on decentralized WhatsApp groups, informal phone rosters, and manual coordination. This lack of centralized transactional locking frequently leads to duplicate volunteer dispatch, misallocated quantities, and zero real-time inventory tracking. Feeding India integrates commercial logistics but operates as a closed corporate charity rather than an open, self-service platform for independent NGOs and donors.

### 2.2 Comparative Feature Analysis Matrix
The following comparative matrix illustrates the structural differences between prevailing market platforms and the architectural capabilities engineered in REPLATE:

| Evaluation Dimension | Robin Hood Army | Olio | Too Good To Go | Feeding India | REPLATE (Present Work) |
|---|---|---|---|---|---|
| **Target End-User** | Destitute / Slums | P2P Neighbors | Paying Consumers | Destitute / Shelters | Verified NGOs & Shelters |
| **Monetary Model** | Free / Non-profit | Free | Discounted Retail | Free / Corporate CSR | Free Humanitarian Redistribution |
| **Institutional Onboarding** | Informal / Manual | Minimal | Commercial Merchant | Vetted NGO Partners | Verified FSSAI / NGO Darpan RBAC |
| **Inventory Concurrency** | None (Chat groups) | Optimistic / Manual | Retail SKU locks | Proprietary Backend | Pessimistic Write Lock (`SELECT FOR UPDATE`) |
| **Batch Allocation** | Manual verbal call | Single claimant | Single item checkout | Manual Coordinator | Atomic Partial / Full Multi-NGO Allocation |
| **Logistics Handshake** | Verbal trust | In-person meetup | In-store pickup | Commercial Fleet | Dual-OTP (Donor & NGO PIN Verification) |
| **Auditability & Traceability** | Low / Ad-hoc | Minimal | Transaction receipt | Corporate Reporting | Relational Audit Log + `Trackable` OOP Contract |
| **Tech Stack Architecture** | WhatsApp / Forms | Mobile App (Monolith)| Mobile App / Microservices| Proprietary Cloud | Java 21, Spring Boot 3.3.4, PostgreSQL 15, React |

### 2.3 Research & Engineering Gaps Addressed
From an analysis of existing literature and deployed platforms, several engineering gaps were identified:
1. **The Concurrency Allocation Gap:** In emergency and event scenarios, multiple NGOs receive alerts regarding a high-value surplus batch. Existing non-profit platforms suffer from race conditions where multiple claimants believe they have secured the donation. REPLATE addresses this via ACID-compliant pessimistic write locking at the database engine level.
2. **The Verification Handshake Gap:** Volunteers in charitable distributions frequently misplace items or deliver to unverified intermediaries. REPLATE enforces a cryptographically generated two-stage PIN verification system (Kitchen Pickup PIN held exclusively by the Donor, and Delivery Confirmation PIN held exclusively by the receiving NGO).
3. **The Architectural OOP Gap in Academic Projects:** Many academic full-stack systems reduce backend code to trivial CRUD controllers, offloading all logic to database queries or frontend scripts. REPLATE explicitly demonstrates advanced Object-Oriented software architecture in Java 21, establishing domain entities as stateful objects encapsulating business invariants.
"""
    },
    "ch3": {
        "title": "Chapter 3: Software Requirements Specification (SRS)",
        "content": """
### 3.1 User Personas & Role Matrix
The REPLATE system governs interactions across four primary actor roles, each governed by specific security entitlements and domain responsibilities:

1. **Commercial Food Donor (`ROLE_DONOR`):**
   - *Persona:* Restaurant managers, hotel banquet supervisors, hostel mess contractors, and caterers.
   - *Key Responsibilities:* Create and manage surplus food batch listings; specify preparation and expiry timestamps, dietary types, storage requirements, and packaging notes; review incoming NGO claims; formally approve or reject claims; execute transactional inventory allocations; hand over food to assigned volunteers upon Kitchen Pickup PIN verification.

2. **Verified NGO Administrator (`ROLE_NGO`):**
   - *Persona:* Authorized coordinators of charitable trusts, orphanages, old-age homes, and community night shelters.
   - *Key Responsibilities:* Maintain institutional verification credentials (NGO Darpan ID, 80G tax exemption, daily beneficiary capacity); discover available surplus listings via public feeds; submit formal food claims specifying beneficiary headcounts; monitor claim approvals; acknowledge food delivery and provide Delivery Confirmation PIN to transit volunteers.

3. **Logistics Volunteer (`ROLE_VOLUNTEER`):**
   - *Persona:* Vetted citizen volunteers and logistics coordinators possessing personal transport (two-wheeler, four-wheeler, or auto-van).
   - *Key Responsibilities:* View assigned delivery dispatches; formally accept or reject dispatch missions; navigate to donor premises; verify Kitchen Pickup PIN to take custody; transport food under recommended storage conditions; navigate to NGO premises; verify Delivery Confirmation PIN upon physical handover to complete the mission.

4. **System Administrator (`ROLE_ADMIN`):**
   - *Persona:* Platform governance and compliance officers.
   - *Key Responsibilities:* Inspect institutional onboarding documents; approve/suspend user accounts; monitor platform-wide redistribution metrics; audit security event logs.

### 3.2 Functional Requirements by Role

#### 3.2.1 Authentication & Profile Management
- **FR-AUTH-01:** The system shall enable public registration for Donor, NGO, and Volunteer roles. Self-registration as Administrator shall be strictly prohibited and return HTTP 403 Forbidden.
- **FR-AUTH-02:** Passwords shall be cryptographically hashed using BCrypt (work factor 12) prior to persistence. Raw passwords shall never be logged or returned in API responses.
- **FR-AUTH-03:** Upon successful credential validation, the system shall issue a compact, signed, stateless JSON Web Token (JWT) containing user ID, email, and granted authority.
- **FR-AUTH-04:** The system shall validate token authenticity, signature integrity, and expiration on every protected API call via a custom Spring Security filter.

#### 3.2.2 Food Donation Lifecycle
- **FR-DON-01:** Donors shall be able to create food listings in either `DRAFT` or `AVAILABLE` status.
- **FR-DON-02:** The system shall enforce that food preparation time is strictly prior to expiry time, and total quantity is strictly greater than zero.
- **FR-DON-03:** The public exploration feed shall display only `AVAILABLE` listings whose expiry timestamp is in the future and whose donor account is in `ACTIVE` status. `DRAFT` listings shall remain private to the creating donor.
- **FR-DON-04:** Donors shall be permitted to cancel an active listing only if no confirmed allocations currently depend on it.

#### 3.2.3 NGO Claim & Request Management
- **FR-REQ-01:** Only verified NGOs shall be permitted to submit food claims against available listings.
- **FR-REQ-02:** The requested quantity shall not exceed the remaining available inventory of the donation.
- **FR-REQ-03:** The system shall prevent an NGO from submitting duplicate pending requests against the same donation listing.
- **FR-REQ-04:** Donors shall be able to approve pending requests (recording approval timestamp and remarks) or reject requests (providing a documented rejection reason).
- **FR-REQ-05:** NGOs shall have the right to withdraw pending claims prior to donor approval.

#### 3.2.4 Transactional Allocation Engine
- **FR-ALC-01:** The system shall execute inventory allocation as an atomic transaction isolated under `READ_COMMITTED` with pessimistic write locks (`SELECT ... FOR UPDATE`).
- **FR-ALC-02:** Allocation shall deduct the exact committed quantity from the donation's available quantity and transition the donation state to `PARTIALLY_ALLOCATED` or `FULLY_ALLOCATED`.
- **FR-ALC-03:** The system shall strictly prevent allocation against requests that are not in `APPROVED` status.
- **FR-ALC-04:** The allocation engine shall reject any attempt to allocate a quantity that exceeds the approved request quantity or the remaining donation inventory.

#### 3.2.5 Volunteer Delivery Management
- **FR-DEL-01:** Upon allocation confirmation, a corresponding `DeliveryTask` shall be generated with status `UNASSIGNED` and two cryptographically pseudo-random 6-digit OTPs (Pickup PIN and Delivery PIN).
- **FR-DEL-02:** Donors and Administrators shall be able to assign active volunteers to unassigned delivery tasks.
- **FR-DEL-03:** Assigned volunteers shall be able to accept or reject delivery tasks. If rejected, the task status shall revert to `REJECTED` and permit reassignment.
- **FR-DEL-04:** The system shall verify the Kitchen Pickup PIN provided by the donor before transitioning the delivery task to `PICKED_UP` and `IN_TRANSIT`.
- **FR-DEL-05:** The system shall verify the Delivery Confirmation PIN provided by the receiving NGO before transitioning the task to `DELIVERED`.
- **FR-DEL-06:** Under no circumstances shall pickup or delivery state transitions deduct inventory stock a second time.

### 3.3 Non-Functional Requirements (NFR)
- **NFR-01 (Performance & Latency):** Core API endpoints (login, donation search, claim submission) shall exhibit a 95th percentile response latency under 200 milliseconds under nominal local execution.
- **NFR-02 (ACID Concurrency Safety):** Under concurrent claim submissions from competing threads, the database shall serialize inventory deductions without race conditions, deadlocks, or negative inventory balances.
- **NFR-03 (Data Security & Integrity):** All database entities shall enforce referential integrity via foreign key constraints, cascading deletions where appropriate, and check constraints for domain ranges.
- **NFR-04 (Maintainability & Clean Architecture):** The codebase shall follow a layered architectural pattern (Presentation, Security, Service, Repository, Domain) with comprehensive Java docstrings and zero circular dependencies.
- **NFR-05 (Cross-Device Usability):** The web user interface shall be fully responsive across desktop (1440px+), tablet (768px–1024px), and mobile viewports (375px+).

### 3.4 Hardware & Software Environments
- **Host Operating System:** macOS / Linux / Windows 11 (POSIX compliant terminal recommended).
- **Runtime Environment:** Java Development Kit (JDK) 21 LTS (Oracle OpenJDK or Eclipse Temurin).
- **Database Engine:** PostgreSQL 14+ (PostgreSQL 15 utilized for development and verification).
- **Build Automation:** Apache Maven 3.9+ (backend), Node.js v18+ and npm 9+ (frontend).
- **Default Port Mappings:**
  - Frontend Single-Page Application: `http://localhost:3000`
  - Spring Boot REST API: `http://localhost:8080`
  - PostgreSQL Database Service: `localhost:5432` (`replate_db`)
"""
    },
    "ch4": {
        "title": "Chapter 4: System Architecture & High-Level Design",
        "content": """
### 4.1 Layered Architecture Overview
REPLATE is engineered as a clean 4-tier micro-monolith. This architectural paradigm was selected over distributed microservices to ensure complete transactional consistency, zero network serialization latency across bounded contexts, and immediate ACID compliance via local PostgreSQL transactions, while maintaining high modularity through Java package boundaries.

The architectural layers are structured as follows:

```
+-----------------------------------------------------------------------+
|                       PRESENTATION TIER (SPA)                         |
|   React 18.3 | TypeScript 5.5 | Vite 5.4 | Tailwind CSS | Lucide UI   |
+-----------------------------------------------------------------------+
                                   |
                      HTTPS / JSON REST API calls
                                   |
+-----------------------------------------------------------------------+
|                    API GATEWAY & SECURITY FILTER TIER                 |
|   Spring Security 6 | JwtAuthenticationFilter | CORS & CSRF Defense   |
+-----------------------------------------------------------------------+
                                   |
                     Authenticated SecurityContext
                                   |
+-----------------------------------------------------------------------+
|                         APPLICATION SERVICE TIER                      |
|   DonationService | RequestService | AllocationEngine | DeliverySvc   |
|   ImpactCalculator | AuditService | CustomUserDetailsService          |
+-----------------------------------------------------------------------+
                                   |
                   Domain Entities & Behavioral Contracts
                                   |
+-----------------------------------------------------------------------+
|                         DATA PERSISTENCE TIER                         |
|   Spring Data JPA | Hibernate 6.5 ORM | HikariCP Connection Pool      |
+-----------------------------------------------------------------------+
                                   |
                          JDBC (Port 5432)
                                   |
+-----------------------------------------------------------------------+
|                     RELATIONAL DATABASE STORAGE                       |
|           PostgreSQL 15 (ACID Engine, Constraints, Indexes)           |
+-----------------------------------------------------------------------+
```

1. **Presentation Tier (React SPA):** Developed in React 18 with TypeScript, Vite, and Tailwind CSS. Manages local client state, authenticated token storage via secure web APIs, client-side route protection via React Router v6, and interactive modal dialogs for OTP verification.
2. **Security & API Filter Tier:** Intercepts every inbound HTTP request. Extracts and validates the `Authorization: Bearer <JWT>` header, enforces role-based URL permissions via Spring Security 6 `SecurityFilterChain`, and populates the `SecurityContextHolder`.
3. **Application Service Tier:** Contains the core business logic, transactional boundary management (`@Transactional`), domain state transitions, and environmental impact calculations.
4. **Data Persistence Tier:** Leverages Spring Data JPA interfaces on top of Hibernate 6.5 ORM. Translates object mutations into optimized SQL statements, manages optimistic concurrency counters (`@Version`), and executes pessimistic write locks (`@Lock(LockModeType.PESSIMISTIC_WRITE)`).
5. **Relational Database Tier:** PostgreSQL 15 acts as the single source of truth, enforcing foreign key referential integrity, unique indices, check constraints, and transactional isolation.

### 4.2 Technology Stack Justification
- **Java 21 LTS:** Selected for its advanced language features, rock-solid virtual machine stability, pattern matching capabilities, records, and long-term enterprise support.
- **Spring Boot 3.3.4:** Provides convention-over-configuration rapid development, first-class dependency injection, production-grade health actuators, and seamless integration with Jakarta Persistence 3.1.
- **Spring Security 6:** Incorporates the modern lambda DSL configuration model, abandoning deprecated WebSecurityConfigurerAdapter patterns in favor of component-based `SecurityFilterChain` beans.
- **PostgreSQL 15:** Offers industry-leading support for complex check constraints, foreign keys, row-level write locking (`FOR UPDATE`), and robust relational modeling.
- **React 18 + Vite:** Offers ultra-fast Hot Module Replacement (HMR), sub-second production builds (1.35 seconds), and strict TypeScript typing for UI component stability.

### 4.3 Architectural & UML Diagrams
The architectural blueprint of REPLATE is formalized through the following UML and architectural diagrams:

- **Figure 4.1: System Layered Architecture Diagram**
  Illustrates the structural decomposition from client web browser to database persistence layer.
  *(Refer to `figures/figure_01_system_architecture.png`)*

- **Figure 4.2: Actor Use Case Diagram**
  Delineates the operational boundaries and functional capabilities accessible to Donors, NGOs, Volunteers, and Administrators.
  *(Refer to `figures/figure_02_use_case_diagram.png`)*

### 4.4 Data Flow Diagrams (DFD)
- **DFD Level 0 (Context Diagram):** The external entities (Donor, NGO, Volunteer, Admin) interact with the central REPLATE system process via authenticated REST channels, exchanging food listing data, claims, allocation commitments, and delivery confirmation tokens.
- **DFD Level 1:** Deconstructs the core process into four sub-processes:
  1. *Process 1.0:* Authentication & Identity Management.
  2. *Process 2.0:* Donation Batch Publication & Public Indexing.
  3. *Process 3.0:* Request Negotiation & Pessimistic Allocation.
  4. *Process 4.0:* Volunteer Dispatch & Dual-OTP Chain-of-Custody Delivery.
"""
    },
    "ch5": {
        "title": "Chapter 5: Detailed Object-Oriented Analysis & Design (OOAD)",
        "content": """
### 5.1 Object-Oriented Philosophy in REPLATE
A fundamental mandate of this academic capstone project is the meaningful, authentic implementation of Java Object-Oriented Programming (OOP) principles. In many contemporary web applications, object-oriented design is corrupted into an 'anemic domain model' where entities serve as passive data holders with getters and setters, while services act as procedural transaction scripts.

In REPLATE, domain models are stateful, intelligent objects that encapsulate business rules, validate their own state transitions, and enforce structural invariants. Core Java OOP concepts are strictly distinguished from Spring Framework runtime mechanisms: Spring manages lifecycle dependency injection and transaction boundaries, while pure Java OOP governs domain contracts, class hierarchies, polymorphism, and behavioral encapsulation.

### 5.2 Comprehensive 19 OOP Concepts Implementation Matrix
The following table details the 19 distinct OOP concepts implemented, verified, and tested within the REPLATE codebase:

| # | OOP Concept | Primary Java Class / Interface | Source Code Location | Architectural Role & Implementation Details |
|---|---|---|---|---|
| **1** | **Encapsulation** | `FoodDonation`, `BaseUser`, `DeliveryTask` | `backend/src/main/java/com/replate/domain/entity/FoodDonation.java` | Hiding internal entity state behind private attributes; mutation is permitted exclusively through defensive domain methods (e.g., `deductQuantity()`, `publish()`, `verifyPickup()`). Invariants prevent negative stock or illegal status skips. |
| **2** | **Inheritance** | `BaseEntity` -> `BaseUser`, `FoodDonation`, `FoodRequest`, `Allocation`, `DeliveryTask` | `backend/src/main/java/com/replate/domain/entity/BaseEntity.java` | Genuine IS-A hierarchy. Every persistent domain model IS-A `BaseEntity`, inheriting surrogate ID, optimistic concurrency version (`@Version`), and lifecycle audit timestamps (`createdAt`, `updatedAt`). |
| **3** | **Polymorphism (Runtime / Dynamic Method Dispatch)** | `Trackable` implemented by `FoodDonation` and `DeliveryTask` | `backend/src/main/java/com/replate/domain/contract/Trackable.java` | Decoupling caller from dynamic runtime implementation. The tracking pipeline treats both `FoodDonation` and `DeliveryTask` polymorphically through `Trackable`, invoking concrete subclass methods at runtime (`getCurrentStatusDescription()`, `getProgressPercentage()`). |
| **4** | **Polymorphism (Compile-Time / Method Overloading)** | `ImpactCalculator` | `backend/src/main/java/com/replate/service/impact/ImpactCalculator.java` | Multiple methods with identical names but differing parameter types and counts to calculate environmental diversion (CO2e offset and INR monetary value) either directly from weight in kg or meal boxes and category heuristics. |
| **5** | **Abstraction & Abstract Classes** | `BaseEntity` | `backend/src/main/java/com/replate/domain/entity/BaseEntity.java` | Abstract classes define foundational identity, optimistic locking versions, and lifecycle timestamps without allowing direct instantiation. Subclasses extend base behavior. |
| **6** | **Interfaces & Behavioral Contracts** | `Trackable`, `Approvable` | `backend/src/main/java/com/replate/domain/contract/Approvable.java` | Declares functional behavioral contracts without specifying storage or implementation details. Allows cross-cutting entities to participate in approval and tracking workflows. |
| **7** | **Composition & Embeddables** | `LocationCoordinate` embedded in `BaseUser` and `FoodDonation` | `backend/src/main/java/com/replate/domain/entity/LocationCoordinate.java` | HAS-A relationship preferred over inheritance for non-is-a dimensions. Geographic coordinates are embedded value objects without surrogate identity. |
| **8** | **Constructor Overloading** | `FoodDonation`, `LocationCoordinate`, `BaseUser` | `backend/src/main/java/com/replate/domain/entity/FoodDonation.java` | Multiple constructors allowing default-state initialization for JPA reflection alongside rich parameterized business constructors with mandatory domain fields. |
| **9** | **Java Generics** | `ApiResponse<T>`, `BaseRepository<T, ID>` | `backend/src/main/java/com/replate/dto/ApiResponse.java` | Type-safe compile-time encapsulation of API responses and persistence operations across arbitrary domain payloads without runtime type casting. |
| **10** | **Type-Safe Enums with Rich Behavior** | `DonationStatus`, `RequestStatus`, `AllocationStatus`, `DeliveryTaskStatus` | `backend/src/main/java/com/replate/domain/enums/DonationStatus.java` | Enums acting as rich domain models containing custom fields, display labels, and state transition validation predicates (`canTransitionTo()`, `isTerminal()`). |
| **11** | **Static Factory Methods** | `ApiResponse<T>`, `LocationCoordinate` | `backend/src/main/java/com/replate/dto/ApiResponse.java` | Expressive construction mechanisms that replace constructor ambiguity with semantic domain intent (`ApiResponse.success()`, `ApiResponse.error()`, `LocationCoordinate.of()`). |
| **12** | **Defensive Copying & Immutability** | `LocationCoordinate`, `AuthTokenResponse` | `backend/src/main/java/com/replate/domain/entity/LocationCoordinate.java` | Protecting internal state against external mutation by creating value objects with final fields and validation on instantiation. |
| **13** | **Custom Exception Class Hierarchy** | `ReplateException` -> `ResourceNotFoundException`, `InsufficientStockException`, `UnauthorizedRoleException` | `backend/src/main/java/com/replate/exception/ReplateException.java` | Structured domain exception inheritance tree mapped to HTTP status codes by Spring GlobalExceptionHandler. |
| **14** | **Polymorphic Exception Handling** | `GlobalExceptionHandler` | `backend/src/main/java/com/replate/exception/GlobalExceptionHandler.java` | Centralized controller advice catching parent `ReplateException` polymorphically while extracting specific HTTP error codes and domain messages. |
| **15** | **Polymorphic Entity Composition & Identity Sharing** | `DonorProfile`, `NgoProfile`, `VolunteerProfile` -> `BaseUser` | `backend/src/main/java/com/replate/domain/entity/DonorProfile.java` | Role specialization using composition with shared primary keys via JPA `@MapsId`, avoiding bloated single-table user models. |
| **16** | **State Machine Encapsulation & Specification Abstraction** | `FoodDonation` -> `DonationStatus`, `FoodDonationService` | `backend/src/main/java/com/replate/domain/entity/FoodDonation.java` | Domain entity encapsulates strictly guarded state transitions (`publish()`, `cancel()`) protecting business invariants, coupled with `Specification<FoodDonation>` predicate abstraction in the service layer for type-safe query composition. |
| **17** | **Contract-Driven Lifecycle & Separation of Concerns** | `FoodRequest` implementing `Approvable`, `FoodRequestService` | `backend/src/main/java/com/replate/domain/entity/FoodRequest.java` | Request approval confirms intent without altering physical stock, strictly separating request review from transactional allocation. |
| **18** | **Pessimistic Write Locking & Concurrency Control** | `AllocationService`, `FoodDonationRepository` | `backend/src/main/java/com/replate/service/allocation/AllocationServiceImpl.java` | Enforces ACID guarantees and race-condition elimination during inventory stock deduction. Uses JPA `@Lock(LockModeType.PESSIMISTIC_WRITE)` (`SELECT ... FOR UPDATE` in PostgreSQL) across competing threads. |
| **19** | **Dual-OTP Chain-of-Custody & Interface-Polymorphic Tracking** | `DeliveryTask` implementing `Trackable`, `DeliveryService` | `backend/src/main/java/com/replate/domain/entity/DeliveryTask.java` | Encapsulates the multi-stage delivery state machine with dual-PIN handshakes and runtime dynamic method dispatch via `Trackable`. |

### 5.3 Behavioral Contracts & Interfaces
REPLATE defines two foundational behavioral contracts in the domain model:

1. **`Approvable` Contract (`com.replate.domain.contract.Approvable`):**
   ```java
   public interface Approvable {
       boolean approve(Long approverUserId, String remarks);
       boolean reject(Long approverUserId, String rejectionReason);
       boolean isPendingDecision();
       Long getTargetEntityId();
   }
   ```
   *Architectural Purpose:* Formalizes the workflow for entities that undergo decision-making lifecycles. Implemented by `FoodRequest`, ensuring that approval logic is encapsulated within the entity rather than scattered across controllers.

2. **`Trackable` Contract (`com.replate.domain.contract.Trackable`):**
   ```java
   public interface Trackable {
       String getTrackingReferenceCode();
       String getCurrentStatusDescription();
       int getProgressPercentage();
       boolean isTerminalState();
   }
   ```
   *Architectural Purpose:* Enables the application to query tracking status polymorphically across heterogeneous entities. Both a `FoodDonation` batch and a `DeliveryTask` transit run implement `Trackable`. The tracking service dispatches progress inquiries dynamically without knowledge of underlying database table differences.

### 5.4 Class Diagram & Entity Relationships
The static structure of REPLATE is formalized in **Figure 5.1: Class Diagram & Inheritance Hierarchy** *(Refer to `figures/figure_04_class_diagram.png`)*, illustrating the `BaseEntity` root, concrete subclasses, interface realizations, embedded value objects, and repository contracts.
"""
    },
    "ch6": {
        "title": "Chapter 6: Database Architecture & Data Dictionary",
        "content": """
### 6.1 Relational Schema Design & Normalization
The REPLATE database is built on PostgreSQL 15, adhering strictly to Third Normal Form (3NF):
- **First Normal Form (1NF):** Every column contains atomic, indivisible values. Multi-valued attributes (such as dietary categories) are modeled via strict type-safe check constraints or dedicated lookup values.
- **Second Normal Form (2NF):** All non-key attributes are fully functionally dependent on the primary key. In tables with composite concepts (e.g., `donor_profile`), attributes depend directly on `user_id`.
- **Third Normal Form (3NF):** There are zero transitive dependencies. Non-key attributes depend solely on the primary key and not on other non-key attributes.

### 6.2 Entity-Relationship (ER) Diagram
The database schema comprises 8 core application tables plus 1 system audit ledger:
- `app_user` (Base identity)
- `donor_profile` (Donor specialization, 1:1 with `app_user` via `@MapsId`)
- `ngo_profile` (NGO specialization, 1:1 with `app_user` via `@MapsId`)
- `volunteer_profile` (Volunteer specialization, 1:1 with `app_user` via `@MapsId`)
- `food_donation` (Surplus listings, FK to `donor_profile`)
- `food_request` (Claims, FK to `food_donation` and `ngo_profile`)
- `allocation` (Committed inventory, FK to `food_request` and `food_donation`)
- `delivery_task` (Volunteer dispatch, 1:1 FK to `allocation`)
- `audit_log` (Immutable security audit log)

*(Refer to `figures/figure_03_er_diagram.png` for the complete ER Diagram).*

### 6.3 Comprehensive Data Dictionary
Refer to Section 6.3.1 through 6.3.9 for full column specifications, types, constraints, and relational semantics for all tables. Every table includes strict check constraints (e.g., `expiry_time > preparation_time`, `available_quantity <= total_quantity`, `allocated_quantity > 0`).

### 6.4 Concurrency Control & Database Locking Strategy
Managing inventory stock under high concurrency is one of the most critical challenges in food redistribution. REPLATE employs a hybrid concurrency control architecture combining:

1. **Optimistic Locking via `@Version`:**
   Applied to `FoodDonation` via a `version BIGINT` column. When a non-conflicting update occurs (e.g., editing pickup notes or updating dietary tags), Hibernate checks `WHERE version = :currentVersion`. If another process modified the row in the interim, an `OptimisticLockException` is thrown, preventing lost updates.

2. **Pessimistic Write Locking via `@Lock(LockModeType.PESSIMISTIC_WRITE)`:**
   When allocating inventory, optimistic locking is insufficient because high contention would cause mass transaction rollbacks and poor user experience. Therefore, REPLATE utilizes explicit row-level write locks (`SELECT ... FOR UPDATE` in PostgreSQL):
   ```java
   @Lock(LockModeType.PESSIMISTIC_WRITE)
   @Query("SELECT fd FROM FoodDonation fd WHERE fd.id = :id")
   Optional<FoodDonation> findByIdWithPessimisticLock(@Param("id") Long id);
   ```
   When `AllocationServiceImpl` initiates an allocation:
   - It acquires a pessimistic write lock on the target `FoodRequest`.
   - It acquires a pessimistic write lock on the target `FoodDonation`.
   - It re-reads the authoritative, uncommitted `availableQuantity` directly from the locked database row.
   - If `requestedQuantity <= availableQuantity`, it decrements the stock, persists the committed `Allocation`, and advances the donation status.
   - Any competing thread attempting to read the same rows with pessimistic locks is blocked by PostgreSQL until the first transaction commits or rolls back.

This strategy completely eliminates race conditions, double allocation, and negative stock anomalies.
"""
    },
    "ch7": {
        "title": "Chapter 7: Security & Access Control Architecture",
        "content": """
### 7.1 Stateless JWT Authentication Architecture
REPLATE employs stateless, token-based authentication using JSON Web Tokens (JJWT 0.12.6). Stateless authentication was chosen over traditional server-side HTTP sessions to ensure horizontal scalability, zero server memory overhead, and seamless cross-origin communication between the Vite frontend (`localhost:3000`) and the Spring Boot API (`localhost:8080`).

### 7.2 Spring Security 6 Filter Chain Architecture
The security configuration leverages Spring Security 6's component-based `SecurityFilterChain`. The request lifecycle proceeds through the following stages:

```
[ HTTP Inbound Request ]
          |
[ CorsFilter ] (Allows origins, methods, headers from localhost:3000)
          |
[ CsrfFilter ] (Disabled for stateless REST APIs using Bearer tokens)
          |
[ JwtAuthenticationFilter ]
    - Extracts 'Authorization: Bearer <token>'
    - Validates signature using HMAC-SHA256 secret key
    - Checks expiration timestamp
    - Extracts username and role authority
    - Populates SecurityContextHolder with UsernamePasswordAuthenticationToken
          |
[ AuthorizationFilter ]
    - Evaluates URL pattern against RequestMatcher rules
    - /api/v1/auth/** -> permitAll()
    - /api/v1/donations/public -> permitAll()
    - /api/v1/donations/** -> hasRole('DONOR')
    - /api/v1/requests/** -> hasAnyRole('NGO', 'DONOR')
    - /api/v1/allocations/** -> hasRole('DONOR')
    - /api/v1/deliveries/** -> hasAnyRole('VOLUNTEER', 'DONOR', 'ADMIN')
          |
[ DispatcherServlet -> Controller Endpoint ]
```

*(Refer to `figures/figure_08_auth_jwt_sequence.png` for the detailed sequence diagram).*

### 7.3 Role-Based Access Control (RBAC) Matrix
The access permissions across all platform operations are strictly partitioned by institutional role:

| Endpoint Resource URI | HTTP Method | Permitted Roles | Business Purpose & Enforcement Rule |
|---|---|---|---|
| `/api/v1/auth/register` | `POST` | `PUBLIC` | Self-registration for DONOR, NGO, VOLUNTEER (ADMIN strictly forbidden). |
| `/api/v1/auth/login` | `POST` | `PUBLIC` | Credential validation and JWT issuance. |
| `/api/v1/auth/me` | `GET` | `AUTHENTICATED` | Identity retrieval for current session user. |
| `/api/v1/donations/public`| `GET` | `PUBLIC` | Search and filter active surplus food listings. |
| `/api/v1/donations` | `POST` | `ROLE_DONOR` | Create surplus food batch listings. |
| `/api/v1/donations/{id}` | `PUT` | `ROLE_DONOR` | Update unallocated food listing details (owner only). |
| `/api/v1/donations/{id}/publish` | `PATCH` | `ROLE_DONOR` | Transition listing from DRAFT to AVAILABLE. |
| `/api/v1/requests` | `POST` | `ROLE_NGO` | Submit food claim against available donation. |
| `/api/v1/requests/{id}/approve` | `PATCH` | `ROLE_DONOR` | Approve incoming NGO claim (donor owner only). |
| `/api/v1/requests/{id}/reject` | `PATCH` | `ROLE_DONOR` | Reject incoming NGO claim with documented reason. |
| `/api/v1/allocations` | `POST` | `ROLE_DONOR` | Execute atomic stock allocation against approved request. |
| `/api/v1/deliveries/assign` | `POST` | `ROLE_DONOR, ROLE_ADMIN`| Dispatch delivery task to active volunteer. |
| `/api/v1/deliveries/my-tasks` | `GET` | `ROLE_VOLUNTEER` | View assigned delivery runs for authenticated volunteer. |
| `/api/v1/deliveries/{id}/verify-pickup` | `POST` | `ROLE_VOLUNTEER`| Verify 6-digit Kitchen Pickup PIN at donor premises. |
| `/api/v1/deliveries/{id}/verify-delivery` | `POST` | `ROLE_VOLUNTEER`| Verify 6-digit Delivery Confirmation PIN at NGO premises. |

### 7.4 Cryptographic Password Hashing & Sensitive Data Protection
User passwords are never stored in plaintext. REPLATE uses `BCryptPasswordEncoder` configured with a work factor of 12. BCrypt incorporates a 128-bit salt generated from a secure random source, protecting against precomputed dictionary attacks and rainbow tables. In addition:
- The `password_hash` column is explicitly excluded from user DTOs (`@JsonIgnore` and selective projection in `UserMapper`).
- Database seed scripts contain exclusively pre-hashed BCrypt strings.
- Passwords and OTP tokens are never written to application log files.
"""
    }
}

