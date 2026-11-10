# REPLATE — Automated Testing & Verification Guide
## Academic Course: Object-Oriented Programming Using Java
**Project:** REPLATE — Food Waste Donation & Redistribution System  
**Test Frameworks:** JUnit 5 (Jupiter), Spring Boot Test, Spring Security Test, MockMvc, AssertJ

---

## 1. Test Suite Summary

| Test Class | Category | Test Count | Description |
|---|---|:---:|---|
| `PasswordEncoderTest` | Unit Test | 1 | Verifies BCrypt salt generation, hashing strength, and password matching against raw inputs. |
| `ReplateApplicationTests` | Smoke / Context Test | 1 | Validates full Spring Boot application context initialization, HikariCP pool connection to PostgreSQL `replate_db`, and schema validation against JPA entities. |
| `AuthenticationFlowIntegrationTest` | Integration Test | 12 | Tests complete REST security layer: Donor registration, NGO registration, Volunteer registration, duplicate email rejection, admin self-registration blockage, login with valid/invalid credentials, stateless JWT issuance, unauthenticated `/me` rejection, and role-based endpoint isolation. |
| `DonorRegistrationPersistenceE2ETest` | End-to-End Test | 1 | Verifies the complete lifecycle: HTTP registration call $\to$ PostgreSQL `app_user` row creation $\to$ BCrypt hash check $\to$ `donor_profile` foreign key persistence $\to$ authenticated `/api/v1/auth/me` profile retrieval $\to$ subsequent login $\to$ duplicate constraint enforcement. |
| `FoodDonationLifecycleIntegrationTest` | Integration Test | 9 | Tests complete donation lifecycle: draft creation, immediate publishing, invalid quantity rejection, expiry-before-preparation rejection, draft-to-available transition, donor ownership enforcement, cancellation rules, public discovery draft privacy, and paginated donor listings. |
| `FoodRequestLifecycleIntegrationTest` | Integration Test | 10 | Tests complete food request lifecycle: claim submission by NGO, non-NGO rejection, invalid request against draft/expired batches, excessive quantity rejection, duplicate pending request conflict prevention, donor approval without premature inventory deduction, donor rejection with reason, NGO withdrawal of pending claim, terminal state mutation prevention, and role-based data isolation. |
| `FoodAllocationLifecycleIntegrationTest` | Integration & Concurrency Test | 17 | Tests complete allocation engine: full allocation, partial allocation, pending/rejected/withdrawn claim rejections, expired donation blockage, negative quantity rejections, request & donation bound violations, multiple allocations per request, inventory exhaustion enforcement, unauthorized actor rejections, status transitions (PARTIALLY_ALLOCATED and FULLY_ALLOCATED), rollback safety, and multi-threaded concurrency safety with 5 concurrent threads asserting zero overselling. |
| **Total Automated Tests** | **All Layers** | **51** | **100% Passing (0 failures, 0 errors, 0 skipped)** |

---

## 2. Test Execution Commands

### Running Backend Tests:
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-21.jdk/Contents/Home
cd backend
mvn test
```

### Running Frontend Production Build & TypeScript Verification:
```bash
cd frontend
npm run build
```

---

## 3. End-to-End Persistence Evidence

Live verification conducted against PostgreSQL database `replate_db` on `localhost:5432`:

1. **HTTP Registration Command:**
   - Payload: Registered `Vidyarthi Bhavan Heritage` (`contact@vidyarthibhavan.in`), FSSAI `11220334000999`, role `DONOR`.
   - Result: HTTP 201 Created with valid signed JWT.

2. **Direct PostgreSQL Database Query:**
   ```sql
   SELECT u.id, u.full_name, u.email, u.role, u.status, u.city, 
          d.organization_name, d.fssai_license_no, substring(u.password_hash, 1, 15) as hash_prefix 
   FROM app_user u 
   JOIN donor_profile d ON u.id = d.user_id 
   WHERE u.email = 'contact@vidyarthibhavan.in';
   ```
   **Output:**
   ```
    id |         full_name         |           email            | role  | status |   city    |          organization_name           | fssai_license_no |   hash_prefix   
   ----+---------------------------+----------------------------+-------+--------+-----------+--------------------------------------+------------------+-----------------
    15 | Vidyarthi Bhavan Heritage | contact@vidyarthibhavan.in | DONOR | ACTIVE | Bengaluru | Vidyarthi Bhavan Heritage Restaurant | 11220334000999   | $2a$10$YAobxTK8
   (1 row)
   ```

3. **Subsequent Login & Verification:**
   - `POST /api/v1/auth/login` with `contact@vidyarthibhavan.in` and password `Vidyarthi@123` verified successfully (HTTP 200).
   - `GET /api/v1/auth/me` resolved user summary with encapsulated donor details.
