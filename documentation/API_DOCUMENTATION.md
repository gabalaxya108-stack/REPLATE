# REPLATE — Backend REST API Documentation
## Project: Food Waste Donation & Redistribution System
**Base URL:** `http://localhost:8080/api/v1` (Proxied in development via `http://localhost:3000/api/v1`)  
**Security Architecture:** Stateless JWT (Bearer Authorization) via Spring Security 6  
**Format:** JSON (`Content-Type: application/json`, `Accept: application/json`)

---

## 1. Unified Response Envelope

All API endpoints return a standardized `ApiResponse<T>` envelope:

```json
{
  "success": true,
  "message": "Human readable confirmation or status message",
  "data": { ... },
  "timestamp": "2026-11-08T10:10:50.246350Z"
}
```

In the event of an error (HTTP 400, 401, 403, 404, 409, 500), `success` is `false`, `message` provides the exact validation or security rejection cause, and `data` contains any contextual field error map.

---

## 2. Authentication & Session Endpoints

### 2.1 Public User Registration
- **Method:** `POST`
- **URL:** `/auth/register`
- **Access:** Public (Unauthenticated)
- **Rules:**
  - Role must be one of: `DONOR`, `NGO`, `VOLUNTEER`.
  - Attempting to register as `ADMIN` returns `HTTP 403 Forbidden`.
  - Email addresses are unique across all roles. Duplicate email returns `HTTP 409 Conflict`.
  - Passwords are encrypted using BCrypt (`strength = 10`) before database persistence. Raw passwords are never stored or logged.

#### Request Body Schema (`RegisterRequestDTO`):
```json
{
  "fullName": "The Rameshwaram Cafe Banquets",
  "email": "contact@rameshwaramcafe.in",
  "password": "Cafe@Strong123",
  "phoneNumber": "+91-9876543210",
  "role": "DONOR",
  "addressLine1": "12th Main Road, HAL 2nd Stage, Indiranagar",
  "city": "Bengaluru",
  "state": "Karnataka",
  "pincode": "560038",
  "latitude": 12.9716,
  "longitude": 77.5946,

  // Role-Specific for DONOR:
  "organizationName": "The Rameshwaram Cafe Banquets Pvt Ltd",
  "organizationType": "RESTAURANT",
  "fssaiLicenseNo": "11220334000888",
  "operatingHours": "06:00 AM - 11:30 PM",

  // Role-Specific for NGO:
  "ngoName": "Robin Hood Army Welfare Foundation",
  "darpanId": "KA/2018/0192834",
  "registration80g": "AAATR1234PF20214",
  "dailyBeneficiaryCapacity": 150,

  // Role-Specific for VOLUNTEER:
  "vehicleType": "TWO_WHEELER",
  "emergencyContact": "+91-9876500112"
}
```

#### Success Response (`HTTP 201 Created`):
```json
{
  "success": true,
  "message": "User registered successfully. Authentication token issued.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzM4NCJ9...",
    "tokenType": "Bearer",
    "expiresInMs": 86400000,
    "userId": 15,
    "fullName": "The Rameshwaram Cafe Banquets",
    "email": "contact@rameshwaramcafe.in",
    "role": "DONOR",
    "city": "Bengaluru"
  },
  "timestamp": "2026-11-08T10:10:50.246350Z"
}
```

---

### 2.2 User Login
- **Method:** `POST`
- **URL:** `/auth/login`
- **Access:** Public (Unauthenticated)
- **Rules:**
  - Authenticates email and password via Spring Security's `AuthenticationManager` with `DaoAuthenticationProvider`.
  - Returns `HTTP 401 Unauthorized` on credential mismatch.
  - Issues signed JJWT token containing `sub` (email), `userId`, `role`, and expiration claim (24 hours).

#### Request Body Schema (`LoginRequestDTO`):
```json
{
  "email": "contact@saravanabhavan.in",
  "password": "Password@123"
}
```

#### Success Response (`HTTP 200 OK`):
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzM4NCJ9...",
    "tokenType": "Bearer",
    "expiresInMs": 86400000,
    "userId": 1,
    "fullName": "Saravana Bhavan Foods Pvt Ltd",
    "email": "contact@saravanabhavan.in",
    "role": "DONOR",
    "city": "Bengaluru"
  },
  "timestamp": "2026-11-08T10:09:45.429070Z"
}
```

---

### 2.3 Current Authenticated User Profile
- **Method:** `GET`
- **URL:** `/auth/me`
- **Access:** Authenticated (`Authorization: Bearer <accessToken>`)
- **Rules:**
  - Validates JWT signature (HMAC-SHA384) and expiration.
  - Extracts principal context from `SecurityContextHolder`.
  - Fetches associated role-specific profile (`donor_profile`, `ngo_profile`, or `volunteer_profile`) using JPA primary key linkage.

#### Success Response (`HTTP 200 OK`):
```json
{
  "success": true,
  "message": "User profile retrieved successfully",
  "data": {
    "id": 1,
    "email": "contact@saravanabhavan.in",
    "fullName": "Saravana Bhavan Foods Pvt Ltd",
    "phoneNumber": "+91-8025211234",
    "role": "DONOR",
    "status": "ACTIVE",
    "addressLine1": "100 Feet Road, Indiranagar",
    "city": "Bengaluru",
    "state": "Karnataka",
    "pincode": "560038",
    "profileDetails": {
      "organizationType": "RESTAURANT",
      "organizationName": "Saravana Bhavan Foods Pvt Ltd",
      "fssaiLicenseNo": "11220334000123",
      "operatingHours": "07:00 AM - 11:00 PM"
    }
  },
  "timestamp": "2026-11-08T10:10:40.480797Z"
}
```

---

## 3. System Health Endpoint

### 3.1 Health Check & Diagnostic Status
- **Method:** `GET`
- **URL:** `/health`
- **Access:** Public (Unauthenticated)

#### Success Response (`HTTP 200 OK`):
```json
{
  "success": true,
  "message": "System is fully operational",
  "data": {
    "academicCourse": "Object-Oriented Programming Using Java",
    "region": "India",
    "status": "UP",
    "system": "REPLATE — Food Waste Donation & Redistribution System",
    "version": "1.0.0-SNAPSHOT"
  },
  "timestamp": "2026-11-08T09:20:02.106892Z"
}
```
