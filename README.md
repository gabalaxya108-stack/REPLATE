# REPLATE — Food Waste Donation & Redistribution System
> **Rescue Food. Restore Hope.**  
> *Academic Course: Object-Oriented Programming Using Java*  
> *Package Root: `com.replate`*

---

## 🌿 Overview
**REPLATE** is an enterprise-grade full-stack platform designed to connect commercial food establishments (restaurants, banquets, university messes, corporate caterers) across Indian metropolitan areas with certified NGOs, community kitchens, and volunteer logistics networks.

By digitizing surplus food donation, real-time allocation, and chain-of-custody delivery, REPLATE redirects safe, edible meals away from landfills and toward vulnerable communities, cutting methane emissions while addressing hunger.

---

## 🛠 Technology Stack

### Backend
- **Language:** Java 21 LTS
- **Framework:** Spring Boot 3.3.x
- **Data Access:** Spring Data JPA / Hibernate 6.x
- **Security:** Spring Security 6 + Stateless JWT Auth
- **Database:** PostgreSQL 15+
- **Build Tool:** Apache Maven 3.9+

### Frontend
- **Framework:** React 18+ with TypeScript
- **Build Tool:** Vite
- **Styling:** Tailwind CSS (Custom Color Palette: Forest Green `#245C45`, Terracotta `#D98A68`, Warm Off-White `#F8F8F3`)
- **Icons & Motion:** Lucide React, Framer Motion
- **State Management & Fetching:** TanStack React Query, React Router v6

---

## 📁 Repository Structure

```text
java project/
├── backend/                  # Spring Boot 3.x Java 21 Application
│   ├── pom.xml
│   └── src/main/java/com/replate/
├── frontend/                 # React + TypeScript + Vite + Tailwind CSS Application
│   ├── package.json
│   └── src/
├── database/                 # Relational PostgreSQL Schemas & Seed Data
│   ├── schema.sql
│   └── seed-data.sql
├── documentation/            # Academic & Architectural Documentation
│   ├── SRS.md
│   ├── ARCHITECTURE.md
│   ├── OOP_CONCEPT_TRACKER.md
│   ├── DATABASE_DESIGN.md
│   └── DEVELOPMENT_ROADMAP.md
└── README.md
```

---

## 🇮🇳 Sample Indian Entities Modeled

- **Donors:** Haldiram's Catering (Delhi NCR), Paradise Food Court (Hyderabad), Saravana Bhavan (Chennai), Trident Banquets (Mumbai), Vidyarthi Bhavan (Bangalore).
- **NGOs:** Robin Hood Army, Roti Bank Mumbai, Akshaya Patra Foundation, Feeding India by Zomato, Uday Foundation.
- **Volunteers:** Community transport champions across Bengaluru, Delhi, Mumbai, Pune, and Hyderabad.

---

## 🚀 Running the Project

### Prerequisites
- Java 21 LTS
- Maven 3.8+
- Node.js 18+ & npm
- PostgreSQL 14+

### Backend Setup
```bash
cd backend
mvn spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
