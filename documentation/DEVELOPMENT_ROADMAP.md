# REPLATE — Development Roadmap
## Multi-Phase Execution Plan

---

### Phase 1: Foundation, Design System & Public Experience (Current Phase)
- [x] Environment inspection (Java 21, Maven, Node.js, PostgreSQL).
- [x] Comprehensive architectural documentation (SRS, Architecture, OOP Tracker, DB Design).
- [x] Relational schema definitions & Indianized seed dataset.
- [ ] Tailwind CSS design system with custom brand tokens (#245C45, #A8BFA8, #D98A68, #F8F8F3, #26332D).
- [ ] Polished, responsive Landing Page highlighting food waste metrics in India, workflow, role cards, live impact counters, and Indian NGO/donor showcases.
- [ ] Role-based Authentication UI (Login & Multi-role Registration for Donor, NGO, Volunteer, Admin).
- [ ] Spring Boot 3.x project skeleton under `com.replate` with Java 21, dependencies, and OOP domain foundations.

---

### Phase 2: Core Domain Logic & REST API Development
- Implementation of base classes, domain entities, and Hibernate mappings.
- Custom exception handling hierarchy (`ReplateException`, `DonationExpiredException`, etc.).
- Authentication Service with Spring Security 6 & JWT tokens.
- Donor endpoints: Food creation, category classification, dynamic availability calculations.
- NGO endpoints: Discovery, geo-filtering, request generation.
- Concurrency-controlled Allocation service (`@Transactional` with pessimistic locking).
- Delivery task dispatch & OTP verification workflow.

---

### Phase 3: Role-Specific Dashboards & Full-Stack Integration
- **Donor Dashboard:** Real-time inventory tracking, request approvals, distribution analytics.
- **NGO Portal:** Available food feed, request status tracker, incoming delivery monitor.
- **Volunteer Console:** Task claiming board, pickup & delivery OTP validation, route guides.
- **Admin Governance Console:** NGO verification queue, audit trail inspector, platform heatmaps.
- Integration between React frontend and Spring Boot REST APIs via typed Axios clients and TanStack Query.

---

### Phase 4: Quality Assurance, Testing & Academic Viva Readiness
- Spring Boot JUnit 5 & Mockito test suite for domain business logic.
- Concurrency simulation test for double-allocation prevention.
- Finalization of `OOP_CONCEPT_TRACKER.md` with line-by-line source citations.
- Complete academic project report and presentation deck.

