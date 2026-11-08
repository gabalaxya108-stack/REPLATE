# Software Requirements Specification (SRS)
## Project Title: Food Waste Donation & Redistribution System
### Brand / Product: REPLATE
**Tagline:** Rescue Food. Restore Hope.  
**Academic Course:** Object-Oriented Programming Using Java  
**Package Root:** `com.replate`  
**Target Region:** India (Pan-Indian Metropolitan & Tier-1 Hubs: Bengaluru, Delhi NCR, Mumbai, Hyderabad, Pune, Chennai)

---

## 1. Introduction

### 1.1 Purpose
This document specifies the software requirements for **REPLATE**, an enterprise-grade food recovery and redistribution platform developed in Java and modern web technologies. REPLATE provides a structured, verifiable, and transparent channel connecting commercial food donors (restaurants, banquet halls, corporate cafeterias, and hostels) with verified non-governmental organizations (NGOs) and community volunteers to divert surplus edible food from landfills directly to communities experiencing food insecurity.

### 1.2 Scope
REPLATE encompasses:
- Donor meal creation, shelf-life estimation, quantity tracking, and batch dispatch management.
- NGO verification, geographical discovery, targeted quantity requesting, and delivery tracking.
- Volunteer dispatch, pickup QR verification, and real-time delivery confirmation.
- Administrator compliance monitoring, NGO KYC auditing, and platform impact analytics.
- Strict concurrency control on food allocation (preventing double-allocation of perishable goods).
- Object-oriented architecture demonstrating inheritance hierarchies, polymorphic behaviors, encapsulated invariants, and transactional data integrity.

---

## 2. Overall Description

### 2.1 Product Perspective
In India, an estimated 68+ million tonnes of food is wasted annually while millions face nutritional deficits. Traditional charitable donations suffer from:
1. Lack of real-time coordination before food reaches critical shelf-life expiry.
2. Inability to allocate partial batches (e.g. an NGO only needing 40 meals out of a 150-meal banquet excess).
3. Logistical bottlenecks where NGOs lack immediate transit capabilities.
4. Food safety liabilities and absence of transparent chain-of-custody tracking.

REPLATE solves this through a transactional state-machine engine powered by Java 21 and Spring Boot, paired with a React TypeScript responsive interface.

### 2.2 User Classes and Characteristics

| Role | Target Persona & Examples (Indian Context) | Key Responsibilities |
| :--- | :--- | :--- |
| **DONOR** | Commercial kitchens (*e.g., Saravana Bhavan, Paradise Biryani, Haldiram's Catering, Trident Banquets*) | Post surplus food with prep time, temperature conditions, expiry window, and pickup coordinates; approve/reject NGO requests. |
| **NGO / RECEIVER** | Certified welfare organizations (*e.g., Robin Hood Army, Roti Bank Mumbai, Feeding India, Akshaya Patra*) | Browse nearby verified batches; request exact quantities with intended beneficiary profiles; confirm receipt. |
| **VOLUNTEER** | Registered community logistics partners (*e.g., college students, local youth corps*) | Accept delivery runs; inspect food freshness at pickup; execute handover to NGO representative. |
| **ADMIN** | Platform Operations & Food Safety Auditors | Verify NGO 80G/12A/Darpan registrations; monitor platform metrics; handle escalations; inspect audit logs. |

---

## 3. Functional Requirements

### 3.1 Module 1: Identity & Access Management (IAM)
- **FR-1.1**: The system shall support role-based registration (`DONOR`, `NGO`, `VOLUNTEER`, `ADMIN`).
- **FR-1.2**: Donors and NGOs must provide institutional identity (FSSAI registration number for commercial donors, NGO Darpan / Society Registration ID for NGOs).
- **FR-1.3**: Authentication shall use stateless JWT (JSON Web Tokens) with encrypted passwords (BCrypt).
- **FR-1.4**: All API endpoints shall enforce strict role-based access control (RBAC).

### 3.2 Module 2: Food Donation Management
- **FR-2.1**: Donors can create donation listings with attributes: Food Title, Category (`COOKED_MEALS`, `BAKERY_BREADS`, `DAIRY_SWEETS`, `RAW_PRODUCE`, `PACKAGED_FOOD`), Quantity, Unit (`KG`, `MEAL_BOXES`, `LITERS`, `PORTIONS`), Preparation Timestamp, Expiry/Best-Before Timestamp, Dietary Classification (`PURE_VEG`, `NON_VEG`), Storage Condition (`HOT_INSULATED`, `REFRIGERATED`, `AMBIENT`), and Pickup Address.
- **FR-2.2**: The system shall reject donation submissions where the expiry timestamp is less than the minimum safe window (default: 2 hours from submission).
- **FR-2.3**: System auto-calculates current available quantity as:  
  $$\text{Available} = \text{Total Quantity} - \sum \text{Approved Allocations}$$
- **FR-2.4**: When available quantity reaches zero, the donation state automatically transitions to `ALLOCATED`.

### 3.3 Module 3: Request & Transactional Allocation Engine
- **FR-3.1**: Verified NGOs can place requests specifying `Requested Quantity` ($\le \text{Available Quantity}$).
- **FR-3.2**: Donors receive instant notifications of requests and can approve, reject, or conditionally modify quantities.
- **FR-3.3**: Upon approval, the system generates an `Allocation` record inside an ACID database transaction. Row-level pessimistic locking or optimistic concurrency versioning prevents race conditions.

### 3.4 Module 4: Volunteer Logistics & Chain of Custody
- **FR-4.1**: Approved allocations create a `DeliveryTask` available to verified volunteers within the target geographical cluster.
- **FR-4.2**: Once a volunteer accepts, state transitions from `UNASSIGNED` $\to$ `ASSIGNED`.
- **FR-4.3**: Pickup step requires volunteer confirmation with physical packaging check (`PICKED_UP`).
- **FR-4.4**: Delivery step requires NGO representative confirmation code / digital sign-off (`DELIVERED`).

### 3.5 Module 5: Impact Metrics & Reporting
- **FR-5.1**: Every completed delivery updates cumulative platform metrics: Total Meals Rescued, Total CO₂e Emissions Diverted (estimated at $2.5 \text{ kg CO}_2\text{e}$ per kg food rescued), and Total Value of Food Saved (INR ₹).

---

## 4. Non-Functional Requirements

### 4.1 Performance & Reliability
- API response latency under 200ms for p95 requests.
- Concurrency safety for simultaneous requests on high-demand banquet batches.

### 4.2 Security
- Strict input validation via Hibernate Validator / Bean Validation (JSR 380).
- Spring Security filter chain with JWT authorization header validation.
- SQL injection immunity via Spring Data JPA parameterized queries.

### 4.3 Usability & Design Guidelines
- Warm, human-centric visual design using forest green (`#245C45`), sage (`#A8BFA8`), and terracotta (`#D98A68`).
- Mobile-first responsive layouts matching contemporary Indian accessibility standards.
