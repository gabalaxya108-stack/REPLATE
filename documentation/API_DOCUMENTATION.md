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

---

## 4. Food Donation Lifecycle & Inventory Endpoints

### 4.1 Create Food Donation Listing
- **Method:** `POST`
- **URL:** `/donations`
- **Access:** `DONOR`, `ADMIN` (`Authorization: Bearer <token>`)
- **Parameters:** `publishImmediately` (boolean, default `false`)
- **Rules:**
  - `totalQuantity` must be strictly positive (`> 0`).
  - `expiryTime` must be strictly after `preparationTime`.
  - If `publishImmediately == true`, `expiryTime` must be strictly after `Instant.now()`, creating listing in `AVAILABLE` state.
  - If `publishImmediately == false`, initial status is `DRAFT` (private, invisible to public feed).

#### Request Body Schema (`CreateDonationRequestDTO`):
```json
{
  "title": "Saravana Bhavan South Indian Lunch Thali",
  "category": "COOKED_MEALS",
  "totalQuantity": 75.00,
  "quantityUnit": "MEAL_BOXES",
  "dietaryType": "PURE_VEG",
  "storageCondition": "HOT_INSULATED",
  "preparationTime": "2026-11-09T05:30:00Z",
  "expiryTime": "2026-11-09T12:00:00Z",
  "pickupNotes": "Packed in 3-tier insulated containers at Indiranagar kitchen.",
  "publishImmediately": true
}
```

#### Success Response (`HTTP 201 Created`):
```json
{
  "success": true,
  "message": "Food donation published successfully and is now active for NGO requests.",
  "data": {
    "id": 18,
    "donorId": 1,
    "donorName": "Saravana Bhavan Foods Pvt Ltd",
    "donorCity": "Bengaluru",
    "title": "Saravana Bhavan South Indian Lunch Thali",
    "category": "COOKED_MEALS",
    "totalQuantity": 75.00,
    "availableQuantity": 75.00,
    "quantityUnit": "MEAL_BOXES",
    "dietaryType": "PURE_VEG",
    "storageCondition": "HOT_INSULATED",
    "status": "AVAILABLE",
    "statusDescription": "Available for NGOs (75.00 Meal Boxes / Thalis remaining)",
    "progressPercentage": 25,
    "terminal": false
  }
}
```

---

### 4.2 Update Food Donation Listing
- **Method:** `PUT`
- **URL:** `/donations/{id}`
- **Access:** `DONOR` (Owner only), `ADMIN`
- **Rules:**
  - Allowed only when status is `DRAFT` or `AVAILABLE`.
  - Total quantity cannot be reduced below already allocated stock.
  - Ownership is strictly enforced; non-owner gets `HTTP 403 Forbidden`.

---

### 4.3 Publish Draft Listing
- **Method:** `POST`
- **URL:** `/donations/{id}/publish`
- **Access:** `DONOR` (Owner only), `ADMIN`
- **Rules:** Transitions listing from `DRAFT` to `AVAILABLE`. Requires future expiry timestamp.

---

### 4.4 Cancel Food Donation Listing
- **Method:** `POST`
- **URL:** `/donations/{id}/cancel`
- **Access:** `DONOR` (Owner only), `ADMIN`
- **Rules:** Transitions listing to `CANCELLED`. Cannot cancel `COMPLETED` or `EXPIRED` listings.

---

### 4.5 Donor's Own Listings
- **Method:** `GET`
- **URL:** `/donations/mine?page=0&size=10`
- **Access:** `DONOR`, `ADMIN`
- **Response:** Paginated list of all listings created by the authenticated donor (including drafts).

---

### 4.6 Public Surplus Food Discovery
- **Method:** `GET`
- **URL:** `/donations/public?category=COOKED_MEALS&dietaryType=PURE_VEG&city=Bengaluru&query=Pulao&page=0&size=12`
- **Access:** Public (Unauthenticated)
- **Rules:** Filters strictly for `status = 'AVAILABLE'` and `expiryTime > Instant.now()`. Drafts and expired listings are excluded.

---

## 5. NGO Food Request & Claim Endpoints

### 5.1 Submit Food Redistribution Claim
- **Method:** `POST`
- **URL:** `/requests`
- **Access:** `NGO`, `ADMIN`
- **Rules:**
  - Authenticated user's NGO profile is automatically resolved; NGO ID cannot be spoofed.
  - Target donation must be in `AVAILABLE` status.
  - Requested quantity must be $> 0$ and $\le$ `availableQuantity`.
  - Duplicate pending requests from the same NGO for the same batch return `HTTP 409 Conflict`.
  - **Scope Boundary:** Does NOT deduct `availableQuantity` or alter `donation.status` (deferred to Step 7 allocation engine).

#### Request Body (`CreateFoodRequestDTO`):
```json
{
  "donationId": 18,
  "requestedQuantity": 25.00,
  "intendedBeneficiaries": "Redistribution to 25 senior citizens at Shanti Old Age Home, Indiranagar"
}
```

#### Response (`FoodRequestResponseDTO`):
```json
{
  "success": true,
  "message": "Food request submitted successfully and is awaiting donor review.",
  "data": {
    "id": 12,
    "donationId": 18,
    "donationTitle": "Saravana Bhavan South Indian Lunch Thali",
    "donationAvailableQuantity": 75.00,
    "donorName": "Saravana Bhavan Foods Pvt Ltd",
    "ngoName": "Robin Hood Army (Bengaluru)",
    "ngoDarpanId": "KA/2018/0192834",
    "requestedQuantity": 25.00,
    "status": "PENDING"
  }
}
```

---

### 5.2 NGO Submitted Claims (`/mine`)
- **Method:** `GET`
- **URL:** `/requests/mine?page=0&size=10`
- **Access:** `NGO`, `ADMIN`
- **Response:** Paginated list of all claims submitted by the logged-in NGO across all statuses (`PENDING`, `APPROVED`, `REJECTED`, `WITHDRAWN`).

---

### 5.3 Donor Incoming Claims (`/received`)
- **Method:** `GET`
- **URL:** `/requests/received?page=0&size=10`
- **Access:** `DONOR`, `ADMIN`
- **Response:** Paginated list of incoming claims submitted by NGOs for any donation owned by the authenticated donor.

---

### 5.4 Get Claim By ID
- **Method:** `GET`
- **URL:** `/requests/{id}`
- **Access:** Requesters (`NGO`), owning `DONOR`, or `ADMIN`. Unauthorized access returns `HTTP 403 Forbidden`.

---

### 5.5 Approve Food Claim
- **Method:** `POST`
- **URL:** `/requests/{id}/approve?remarks=Hot+insulated+dispatch+ready`
- **Access:** Owning `DONOR`, `ADMIN`
- **Rules:**
  - Request must be in `PENDING` status.
  - Approver must own the target donation.
  - Sets request status to `APPROVED`.
  - **Scope Boundary:** Stock deduction and `Allocation` creation are serialized in Step 7.

---

### 5.6 Reject Food Claim
- **Method:** `POST`
- **URL:** `/requests/{id}/reject`
- **Access:** Owning `DONOR`, `ADMIN`
- **Request Body (`RejectFoodRequestDTO`):**
```json
{
  "rejectionReason": "Cold storage logistics unavailable for this distance."
}
```
- **Rules:** Request must be `PENDING`. Transitions status to `REJECTED` with persisted reason.

---

### 5.7 Withdraw Food Claim
- **Method:** `POST`
- **URL:** `/requests/{id}/withdraw`
- **Access:** Requesting `NGO`, `ADMIN`
- **Rules:** Request must be `PENDING`. Allows the NGO to cancel its own claim prior to donor review.

---

## 6. Food Stock Allocation Endpoints

### 6.1 Create Food Allocation (Inventory Deduction)
- **Method:** `POST`
- **URL:** `/allocations`
- **Access:** Owning `DONOR`, `ADMIN`
- **Concurrency & Transaction Rules:**
  - Enforces Spring `@Transactional(isolation = Isolation.READ_COMMITTED)`.
  - Acquires exclusive row-level lock (`PESSIMISTIC_WRITE` $\to$ `SELECT ... FOR UPDATE`) on target `FoodRequest` and `FoodDonation` entities.
  - Re-reads authoritative inventory and calculates remaining unallocated request balance inside the transaction.
  - Atomically deducts `availableQuantity` on the donation and transitions status to `PARTIALLY_ALLOCATED` or `FULLY_ALLOCATED`.
  - Rejects attempts where requested quantity exceeds available stock or request balance.
  - Rejects attempts on expired, cancelled, or completed batches.
  - Captures `allocatedBy` actor identity for complete audit accountability.

#### Request Body Schema (`CreateAllocationDTO`):
```json
{
  "requestId": 35,
  "allocatedQuantity": 25.00,
  "notes": "First partial batch dispatch of 25 boxes"
}
```

#### Response (`AllocationResponseDTO`):
```json
{
  "success": true,
  "message": "Food stock allocated successfully. Dispatch awaiting volunteer assignment.",
  "data": {
    "id": 11,
    "requestId": 35,
    "donationId": 79,
    "donationTitle": "Fresh Paneer Butter Masala & Kulcha",
    "donationCategory": "COOKED_MEALS",
    "quantityUnit": "MEAL_BOXES",
    "allocatedQuantity": 25.00,
    "status": "AWAITING_PICKUP",
    "donorId": 1,
    "donorName": "Saravana Bhavan Foods Pvt Ltd",
    "ngoId": 4,
    "ngoName": "Robin Hood Army (Bengaluru)",
    "allocatedById": 1,
    "allocatedByName": "Saravana Bhavan Foods Pvt Ltd",
    "requestRequestedQuantity": 40.00,
    "donationAvailableQuantity": 35.00,
    "donationTotalQuantity": 60.00,
    "createdAt": "2026-11-10T11:24:15.735361Z"
  }
}
```

---

### 6.2 Authenticated User Allocations
- **Method:** `GET`
- **URL:** `/allocations/mine?page=0&size=10`
- **Access:** `DONOR`, `NGO`, `ADMIN`
- **Rules:**
  - Donors receive all outgoing allocations created from their kitchens.
  - NGOs receive all incoming allocations committed to their organization.
  - Admins receive all system allocations.

---

### 6.3 Get Allocation By ID
- **Method:** `GET`
- **URL:** `/allocations/{id}`
- **Access:** Owning `DONOR`, receiving `NGO`, or `ADMIN`. Unauthorized access returns `HTTP 403 Forbidden`.

---

### 6.4 List Allocations By Donation Listing
- **Method:** `GET`
- **URL:** `/allocations/donation/{donationId}`
- **Access:** Owning `DONOR`, `ADMIN`
- **Response:** List of all allocation batches drawn against the given donation ID.

---

### 6.5 List Allocations By Approved Request
- **Method:** `GET`
- **URL:** `/allocations/request/{requestId}`
- **Access:** Owning `DONOR`, receiving `NGO`, or `ADMIN`
- **Response:** List of all allocation batches fulfilling the given approved request ID.



