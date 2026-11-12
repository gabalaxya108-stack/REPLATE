"""
REPLATE Academic Project Report Data Module
Contains structured text, tables, figures, code snippets, and metadata.
Strictly based on the actual verified Java 21, Spring Boot 3.3.4, and PostgreSQL implementation.
"""

PROJECT_METADATA = {
    "title": "REPLATE",
    "subtitle": "Food Waste Donation & Redistribution System",
    "tagline": "Rescue Food. Restore Hope.",
    "academic_course": "Object-Oriented Programming Using Java",
    "degree": "Bachelor of Technology in Computer Science & Engineering",
    "department": "Department of Computer Science & Engineering",
    "institution": "[Institution / University Name]",
    "student_name": "[Student Name]",
    "roll_number": "[Roll Number / Registration ID]",
    "project_guide": "[Faculty Guide Name, Designation]",
    "hod_name": "[Head of Department Name]",
    "academic_year": "2024 – 2025",
    "semester": "VI Semester",
    "submission_date": "November 2024",
    "backend_stack": "Java 21 LTS, Spring Boot 3.3.4, Spring Security 6, Spring Data JPA, Hibernate 6.5, JJWT 0.12.6, PostgreSQL 15, Maven",
    "frontend_stack": "React 18.3, TypeScript 5.5, Vite 5.4, Tailwind CSS 3.4, Lucide React",
    "test_suite_status": "73 / 73 Tests Passing (100% Pass Rate across 7 Test Classes)",
    "database_schema": "8 Core Relational Tables + 1 System Audit Log in PostgreSQL 15"
}

ABBREVIATIONS = [
    ("ACID", "Atomicity, Consistency, Isolation, Durability"),
    ("API", "Application Programming Interface"),
    ("BCrypt", "Blowfish-based Cryptographic Adaptive Password Hashing"),
    ("CORS", "Cross-Origin Resource Sharing"),
    ("CSRF", "Cross-Site Request Forgery"),
    ("DTO", "Data Transfer Object"),
    ("E2E", "End-to-End Testing"),
    ("ERD", "Entity-Relationship Diagram"),
    ("FSSAI", "Food Safety and Standards Authority of India"),
    ("GPS", "Global Positioning System"),
    ("HTTP", "Hypertext Transfer Protocol"),
    ("INR", "Indian Rupee (₹)"),
    ("JJWT", "Java JSON Web Token Library"),
    ("JPA", "Java Persistence API (Jakarta Persistence)"),
    ("JSON", "JavaScript Object Notation"),
    ("JWT", "JSON Web Token"),
    ("LTS", "Long-Term Support"),
    ("MVC", "Model-View-Controller"),
    ("NGO", "Non-Governmental Organization"),
    ("OOAD", "Object-Oriented Analysis and Design"),
    ("OOP", "Object-Oriented Programming"),
    ("ORM", "Object-Relational Mapping"),
    ("OTP", "One-Time Password / Verification PIN"),
    ("POJO", "Plain Old Java Object"),
    ("RBAC", "Role-Based Access Control"),
    ("REST", "Representational State Transfer"),
    ("SDLC", "Software Development Life Cycle"),
    ("SPA", "Single Page Application"),
    ("SQL", "Structured Query Language"),
    ("SRS", "Software Requirements Specification"),
    ("UI / UX", "User Interface / User Experience"),
    ("UML", "Unified Modeling Language"),
    ("URI / URL", "Uniform Resource Identifier / Locator")
]

OOP_CONCEPTS_TABLE = [
    {
        "id": 1,
        "name": "Encapsulation",
        "artifacts": "FoodDonation, BaseUser, DeliveryTask",
        "file": "backend/src/main/java/com/replate/domain/entity/FoodDonation.java",
        "explanation": "Hiding internal entity state behind private fields and providing strictly guarded domain methods that maintain business invariants.",
        "code_ref": "public synchronized boolean deductQuantity(BigDecimal requestedQty) ensuring available quantity cannot become negative and automatically transitioning status to FULLY_ALLOCATED or PARTIALLY_ALLOCATED."
    },
    {
        "id": 2,
        "name": "Inheritance",
        "artifacts": "BaseEntity -> BaseUser, FoodDonation, FoodRequest, Allocation, DeliveryTask",
        "file": "backend/src/main/java/com/replate/domain/entity/BaseEntity.java",
        "explanation": "Genuine IS-A hierarchy where all persistent models inherit surrogate ID, optimistic concurrency @Version, and lifecycle timestamps.",
        "code_ref": "public class BaseUser extends BaseEntity and public class FoodDonation extends BaseEntity."
    },
    {
        "id": 3,
        "name": "Polymorphism (Runtime / Dynamic Method Dispatch)",
        "artifacts": "Trackable implemented by FoodDonation and DeliveryTask",
        "file": "backend/src/main/java/com/replate/domain/contract/Trackable.java",
        "explanation": "Decoupling callers from concrete implementations. The tracking engine treats entities polymorphically via Trackable.",
        "code_ref": "Trackable item = delivery; item.getCurrentStatusDescription(); dynamically invokes DeliveryTask.getCurrentStatusDescription()."
    },
    {
        "id": 4,
        "name": "Polymorphism (Compile-Time / Overloading)",
        "artifacts": "ImpactCalculator",
        "file": "backend/src/main/java/com/replate/service/impact/ImpactCalculator.java",
        "explanation": "Overloaded methods with identical names but differing signatures calculating CO2e offset and monetary savings from weight or meal units.",
        "code_ref": "calculateCo2Offset(BigDecimal weightInKg) vs calculateCo2Offset(Integer mealBoxes, FoodCategory category)."
    },
    {
        "id": 5,
        "name": "Abstraction & Abstract Classes",
        "artifacts": "BaseEntity",
        "file": "backend/src/main/java/com/replate/domain/entity/BaseEntity.java",
        "explanation": "Abstract base class defining structural identity, optimistic concurrency version, and audit timestamps without allowing direct instantiation.",
        "code_ref": "@MappedSuperclass public abstract class BaseEntity implements Serializable with protected lifecycle hooks."
    },
    {
        "id": 6,
        "name": "Interfaces & Behavioral Contracts",
        "artifacts": "Trackable, Approvable",
        "file": "backend/src/main/java/com/replate/domain/contract/Approvable.java",
        "explanation": "Defines pure functional contracts for cross-cutting workflows without binding to database schemas.",
        "code_ref": "public interface Approvable { boolean approve(Long approverUserId, String remarks); boolean reject(Long approverUserId, String reason); }"
    },
    {
        "id": 7,
        "name": "Composition & Embeddables",
        "artifacts": "LocationCoordinate in BaseUser and FoodDonation",
        "file": "backend/src/main/java/com/replate/domain/entity/LocationCoordinate.java",
        "explanation": "HAS-A value-object composition for geographical coordinates without surrogate identity, co-persisted with enclosing entities.",
        "code_ref": "@Embedded private LocationCoordinate location; with immutable getters and spatial distance calculation."
    },
    {
        "id": 8,
        "name": "Constructor Overloading",
        "artifacts": "FoodDonation, LocationCoordinate, BaseUser, DeliveryTask",
        "file": "backend/src/main/java/com/replate/domain/entity/FoodDonation.java",
        "explanation": "Providing protected no-argument constructors for JPA reflection alongside rich parameterized business constructors.",
        "code_ref": "FoodDonation() for Hibernate and FoodDonation(donor, title, category, quantity, unit, ...) for domain instantiation."
    },
    {
        "id": 9,
        "name": "Java Generics",
        "artifacts": "ApiResponse<T>, BaseRepository<T, ID>",
        "file": "backend/src/main/java/com/replate/dto/ApiResponse.java",
        "explanation": "Type-safe compile-time encapsulation of API responses and persistence operations across arbitrary domain payloads.",
        "code_ref": "public class ApiResponse<T> { private boolean success; private T data; public static <T> ApiResponse<T> success(T data) { ... } }"
    },
    {
        "id": 10,
        "name": "Type-Safe Enums with Rich Behavior",
        "artifacts": "DonationStatus, RequestStatus, AllocationStatus, DeliveryTaskStatus, FoodCategory",
        "file": "backend/src/main/java/com/replate/domain/enums/DonationStatus.java",
        "explanation": "Enums acting as rich domain models with custom fields, display labels, and state transition validation predicates.",
        "code_ref": "public boolean canTransitionTo(DonationStatus target) and public boolean isTerminal() defined directly inside enum."
    },
    {
        "id": 11,
        "name": "Static Factory Methods",
        "artifacts": "ApiResponse<T>, LocationCoordinate",
        "file": "backend/src/main/java/com/replate/dto/ApiResponse.java",
        "explanation": "Expressive construction mechanisms that replace constructor ambiguity with semantic domain intent.",
        "code_ref": "ApiResponse.success(data, message), ApiResponse.error(errorCode, message), and LocationCoordinate.of(lat, lon)."
    },
    {
        "id": 12,
        "name": "Defensive Copying & Immutability",
        "artifacts": "LocationCoordinate, AuthTokenResponse",
        "file": "backend/src/main/java/com/replate/domain/entity/LocationCoordinate.java",
        "explanation": "Protecting internal state against external mutation by creating value objects with final fields and validation on instantiation.",
        "code_ref": "LocationCoordinate with final latitude and longitude, validating valid GPS ranges (-90 to +90, -180 to +180) upon construction."
    },
    {
        "id": 13,
        "name": "Custom Exception Class Hierarchy",
        "artifacts": "ReplateException -> ResourceNotFoundException, InvalidStateTransitionException, InsufficientStockException",
        "file": "backend/src/main/java/com/replate/exception/ReplateException.java",
        "explanation": "Structured domain exception inheritance tree mapped to HTTP status codes by Spring GlobalExceptionHandler.",
        "code_ref": "public abstract class ReplateException extends RuntimeException and public class InsufficientStockException extends ReplateException."
    },
    {
        "id": 14,
        "name": "Polymorphic Exception Handling",
        "artifacts": "GlobalExceptionHandler",
        "file": "backend/src/main/java/com/replate/exception/GlobalExceptionHandler.java",
        "explanation": "Centralized controller advice catching parent ReplateException polymorphically while extracting specific HTTP error codes.",
        "code_ref": "@ExceptionHandler(ReplateException.class) public ResponseEntity<ApiResponse<Void>> handleDomainException(ReplateException ex)."
    },
    {
        "id": 15,
        "name": "Polymorphic Entity Composition & Identity Sharing",
        "artifacts": "DonorProfile, NgoProfile, VolunteerProfile -> BaseUser",
        "file": "backend/src/main/java/com/replate/domain/entity/DonorProfile.java",
        "explanation": "Role specialization using composition with shared primary keys via JPA @MapsId, avoiding bloated single-table user models.",
        "code_ref": "@OneToOne @MapsId @JoinColumn(name = 'user_id') private BaseUser user; with specialized fields for each actor role."
    },
    {
        "id": 16,
        "name": "State Machine Encapsulation & Specification Abstraction",
        "artifacts": "FoodDonation -> DonationStatus, FoodDonationService",
        "file": "backend/src/main/java/com/replate/domain/entity/FoodDonation.java",
        "explanation": "Guarded state transitions protecting business invariants combined with Spring Data Specification predicate composition.",
        "code_ref": "donation.publish() enforcing DRAFT status and future expiry; dynamic CriteriaBuilder predicate query in FoodDonationSpecification."
    },
    {
        "id": 17,
        "name": "Contract-Driven Lifecycle & Separation of Concerns",
        "artifacts": "FoodRequest implementing Approvable, FoodRequestService",
        "file": "backend/src/main/java/com/replate/domain/entity/FoodRequest.java",
        "explanation": "Request approval confirms intent without altering physical stock, strictly separating request review from transactional allocation.",
        "code_ref": "request.approve(donorUserId, remarks) validates ownership and transition; inventory deduction is strictly deferred to AllocationService."
    },
    {
        "id": 18,
        "name": "Pessimistic Write Locking & Concurrency Control",
        "artifacts": "AllocationService, FoodDonationRepository, FoodRequestRepository",
        "file": "backend/src/main/java/com/replate/service/allocation/AllocationServiceImpl.java",
        "explanation": "ACID compliance and race-condition elimination during inventory allocation using SELECT ... FOR UPDATE pessimistic database locking.",
        "code_ref": "@Lock(LockModeType.PESSIMISTIC_WRITE) on findByIdWithPessimisticLock(), executing within @Transactional to eliminate overselling."
    },
    {
        "id": 19,
        "name": "Dual-OTP Chain-of-Custody & Interface-Polymorphic Tracking",
        "artifacts": "DeliveryTask implementing Trackable, DeliveryService",
        "file": "backend/src/main/java/com/replate/domain/entity/DeliveryTask.java",
        "explanation": "Encapsulates delivery state machine with dual-PIN handshakes (Kitchen Pickup PIN and Delivery Confirmation PIN) and polymorphic progress tracking.",
        "code_ref": "task.verifyPickup(otp, notes) and task.verifyDelivery(otp, recipient, notes) with dynamic Trackable.getProgressPercentage() dispatch."
    }
]

DATA_DICTIONARY = [
    {
        "table": "app_user",
        "description": "Base user identity table containing authentication credentials, contact details, address, and institutional role.",
        "columns": [
            ("id", "BIGSERIAL", "PRIMARY KEY", "Surrogate unique identifier for user"),
            ("email", "VARCHAR(150)", "NOT NULL, UNIQUE", "Unique login email address"),
            ("password_hash", "VARCHAR(255)", "NOT NULL", "BCrypt salted cryptographic hash (cost 12)"),
            ("phone_number", "VARCHAR(20)", "NOT NULL", "Contact phone number"),
            ("full_name", "VARCHAR(120)", "NOT NULL", "Legal full name or primary contact name"),
            ("role", "VARCHAR(30)", "NOT NULL, CHECK", "User role: DONOR, NGO, VOLUNTEER, ADMIN"),
            ("status", "VARCHAR(30)", "NOT NULL, DEFAULT 'ACTIVE'", "Account status: PENDING_VERIFICATION, ACTIVE, SUSPENDED"),
            ("address_line1", "VARCHAR(255)", "NOT NULL", "Street address"),
            ("city", "VARCHAR(80)", "NOT NULL", "City of operation"),
            ("state", "VARCHAR(80)", "NOT NULL", "State of operation"),
            ("pincode", "VARCHAR(10)", "NOT NULL", "Postal area pincode"),
            ("latitude", "NUMERIC(10,7)", "NULLABLE", "WGS-84 geographic latitude coordinate"),
            ("longitude", "NUMERIC(10,7)", "NULLABLE", "WGS-84 geographic longitude coordinate"),
            ("created_at", "TIMESTAMPTZ", "DEFAULT CURRENT_TIMESTAMP", "Record creation audit timestamp"),
            ("updated_at", "TIMESTAMPTZ", "DEFAULT CURRENT_TIMESTAMP", "Last update audit timestamp")
        ]
    },
    {
        "table": "donor_profile",
        "description": "Commercial and institutional food donor extension table linked 1:1 with app_user via shared primary key.",
        "columns": [
            ("user_id", "BIGINT", "PRIMARY KEY, FK -> app_user(id)", "User ID reference with ON DELETE CASCADE"),
            ("organization_name", "VARCHAR(150)", "NOT NULL", "Commercial business name (e.g. Royal Palace Hotel)"),
            ("organization_type", "VARCHAR(50)", "NOT NULL, CHECK", "RESTAUARNT, HOTEL_BANQUET, HOSTEL_MESS, CATERER, SWEET_CORNER"),
            ("fssai_license_no", "VARCHAR(30)", "NOT NULL, UNIQUE", "Government Food Safety License Number (14 digits)"),
            ("operating_hours", "VARCHAR(100)", "NULLABLE", "Operating and food release window hours")
        ]
    },
    {
        "table": "ngo_profile",
        "description": "Registered welfare and charity institution profile linked 1:1 with app_user.",
        "columns": [
            ("user_id", "BIGINT", "PRIMARY KEY, FK -> app_user(id)", "User ID reference with ON DELETE CASCADE"),
            ("ngo_name", "VARCHAR(150)", "NOT NULL", "Registered charitable trust or society name"),
            ("darpan_id", "VARCHAR(50)", "UNIQUE", "NITI Aayog NGO-Darpan Portal registration ID"),
            ("registration_80g", "VARCHAR(50)", "NULLABLE", "Income Tax Act 80G tax exemption certificate number"),
            ("daily_beneficiary_capacity", "INT", "NOT NULL, DEFAULT 50, CHECK (>0)", "Maximum people fed per day"),
            ("verification_status", "VARCHAR(30)", "NOT NULL, DEFAULT 'PENDING'", "PENDING, VERIFIED, REJECTED")
        ]
    },
    {
        "table": "volunteer_profile",
        "description": "Logistics volunteer profile linked 1:1 with app_user.",
        "columns": [
            ("user_id", "BIGINT", "PRIMARY KEY, FK -> app_user(id)", "User ID reference with ON DELETE CASCADE"),
            ("vehicle_type", "VARCHAR(40)", "NOT NULL, CHECK", "TWO_WHEELER, FOUR_WHEELER, AUTO_VAN, WALK"),
            ("emergency_contact", "VARCHAR(20)", "NOT NULL", "Emergency family/guardian contact number"),
            ("total_deliveries", "INT", "NOT NULL, DEFAULT 0, CHECK (>=0)", "Cumulative completed delivery counter")
        ]
    },
    {
        "table": "food_donation",
        "description": "Surplus food inventory listings created and managed by commercial donors.",
        "columns": [
            ("id", "BIGSERIAL", "PRIMARY KEY", "Surrogate unique identifier for donation batch"),
            ("donor_id", "BIGINT", "NOT NULL, FK -> donor_profile(user_id)", "Authoring donor ID reference (ON DELETE RESTRICT)"),
            ("title", "VARCHAR(160)", "NOT NULL", "Descriptive food listing title"),
            ("category", "VARCHAR(50)", "NOT NULL, CHECK", "COOKED_MEALS, BREADS_ROTIS, DAIRY_SWEETS, RAW_PRODUCE, PACKAGED_GOODS"),
            ("total_quantity", "NUMERIC(10,2)", "NOT NULL, CHECK (>0)", "Original listed food quantity"),
            ("available_quantity", "NUMERIC(10,2)", "NOT NULL, CHECK (>=0)", "Remaining unallocated physical inventory"),
            ("quantity_unit", "VARCHAR(20)", "NOT NULL, CHECK", "KG, MEAL_BOXES, LITERS, PORTIONS"),
            ("dietary_type", "VARCHAR(30)", "NOT NULL, CHECK", "PURE_VEG, NON_VEG, VEGAN"),
            ("storage_condition", "VARCHAR(40)", "NOT NULL, CHECK", "HOT_INSULATED, REFRIGERATED, AMBIENT"),
            ("preparation_time", "TIMESTAMPTZ", "NOT NULL", "Exact cooking or preparation timestamp"),
            ("expiry_time", "TIMESTAMPTZ", "NOT NULL", "Consumption safety cutoff timestamp (CHECK > prep_time)"),
            ("pickup_notes", "TEXT", "NULLABLE", "Donor packaging and gate pickup instructions"),
            ("status", "VARCHAR(30)", "NOT NULL, DEFAULT 'AVAILABLE'", "DRAFT, AVAILABLE, PARTIALLY_ALLOCATED, FULLY_ALLOCATED, COMPLETED, EXPIRED, CANCELLED"),
            ("version", "BIGINT", "NOT NULL, DEFAULT 0", "Hibernate optimistic locking version counter"),
            ("created_at", "TIMESTAMPTZ", "DEFAULT CURRENT_TIMESTAMP", "Record creation timestamp"),
            ("updated_at", "TIMESTAMPTZ", "DEFAULT CURRENT_TIMESTAMP", "Last update timestamp")
        ]
    },
    {
        "table": "food_request",
        "description": "Food acquisition claims submitted by verified NGOs against listed food donations.",
        "columns": [
            ("id", "BIGSERIAL", "PRIMARY KEY", "Surrogate unique identifier for request claim"),
            ("donation_id", "BIGINT", "NOT NULL, FK -> food_donation(id)", "Target food donation reference (ON DELETE CASCADE)"),
            ("ngo_id", "BIGINT", "NOT NULL, FK -> ngo_profile(user_id)", "Claiming NGO institution ID (ON DELETE RESTRICT)"),
            ("requested_quantity", "NUMERIC(10,2)", "NOT NULL, CHECK (>0)", "Quantity demanded for redistribution"),
            ("intended_beneficiaries", "TEXT", "NOT NULL", "Target community description (e.g. 80 shelter children)"),
            ("status", "VARCHAR(30)", "NOT NULL, DEFAULT 'PENDING'", "PENDING, APPROVED, REJECTED, WITHDRAWN, CANCELLED, EXPIRED"),
            ("rejection_reason", "VARCHAR(255)", "NULLABLE", "Donor rejection rationale if not approved"),
            ("created_at", "TIMESTAMPTZ", "DEFAULT CURRENT_TIMESTAMP", "Submission timestamp"),
            ("updated_at", "TIMESTAMPTZ", "DEFAULT CURRENT_TIMESTAMP", "Decision timestamp")
        ]
    },
    {
        "table": "allocation",
        "description": "Committed legal contract locking inventory stock from a donation to an approved NGO request.",
        "columns": [
            ("id", "BIGSERIAL", "PRIMARY KEY", "Surrogate unique identifier for allocation"),
            ("request_id", "BIGINT", "NOT NULL, FK -> food_request(id)", "Approved NGO request reference"),
            ("donation_id", "BIGINT", "NOT NULL, FK -> food_donation(id)", "Source donation listing reference"),
            ("allocated_quantity", "NUMERIC(10,2)", "NOT NULL, CHECK (>0)", "Guaranteed quantity locked for transfer"),
            ("status", "VARCHAR(30)", "NOT NULL, DEFAULT 'AWAITING_PICKUP'", "AWAITING_PICKUP, IN_TRANSIT, DELIVERED, CANCELLED"),
            ("allocated_by", "BIGINT", "FK -> app_user(id)", "Authorizing donor or admin user ID"),
            ("created_at", "TIMESTAMPTZ", "DEFAULT CURRENT_TIMESTAMP", "Commitment timestamp"),
            ("updated_at", "TIMESTAMPTZ", "DEFAULT CURRENT_TIMESTAMP", "Lifecycle update timestamp")
        ]
    },
    {
        "table": "delivery_task",
        "description": "Volunteer physical transport dispatch task with cryptographic dual-OTP verification.",
        "columns": [
            ("id", "BIGSERIAL", "PRIMARY KEY", "Surrogate unique identifier for delivery run"),
            ("allocation_id", "BIGINT", "NOT NULL, UNIQUE, FK -> allocation(id)", "1:1 link to committed allocation (ON DELETE CASCADE)"),
            ("volunteer_id", "BIGINT", "NULLABLE, FK -> volunteer_profile(user_id)", "Assigned driver/volunteer ID (ON DELETE SET NULL)"),
            ("status", "VARCHAR(30)", "NOT NULL, DEFAULT 'UNASSIGNED'", "UNASSIGNED, ASSIGNED, ACCEPTED, REJECTED, PICKED_UP, IN_TRANSIT, DELIVERED, CANCELLED, FAILED"),
            ("pickup_otp", "VARCHAR(6)", "NOT NULL", "6-digit Kitchen Pickup PIN held by Donor"),
            ("delivery_otp", "VARCHAR(6)", "NOT NULL", "6-digit Delivery Confirmation PIN held by NGO"),
            ("assigned_at", "TIMESTAMPTZ", "NULLABLE", "Volunteer dispatch assignment timestamp"),
            ("accepted_at", "TIMESTAMPTZ", "NULLABLE", "Volunteer formal acceptance timestamp"),
            ("picked_up_at", "TIMESTAMPTZ", "NULLABLE", "Donor OTP verification timestamp"),
            ("in_transit_at", "TIMESTAMPTZ", "NULLABLE", "Vehicle departure timestamp"),
            ("delivered_at", "TIMESTAMPTZ", "NULLABLE", "NGO OTP verification and dropoff timestamp"),
            ("recipient_name", "VARCHAR(120)", "NULLABLE", "Name of NGO receiving officer"),
            ("recipient_phone", "VARCHAR(20)", "NULLABLE", "Contact number of NGO receiver"),
            ("notes", "TEXT", "NULLABLE", "Volunteer dispatch remarks and route notes"),
            ("issue_reason", "TEXT", "NULLABLE", "Failure or cancellation reason if task aborted"),
            ("created_at", "TIMESTAMPTZ", "DEFAULT CURRENT_TIMESTAMP", "Task creation timestamp"),
            ("updated_at", "TIMESTAMPTZ", "DEFAULT CURRENT_TIMESTAMP", "Task update timestamp")
        ]
    },
    {
        "table": "audit_log",
        "description": "Immutable security and compliance event ledger recording critical operations.",
        "columns": [
            ("id", "BIGSERIAL", "PRIMARY KEY", "Surrogate unique log ID"),
            ("user_id", "BIGINT", "NULLABLE, FK -> app_user(id)", "Actor initiating the audited operation"),
            ("action", "VARCHAR(80)", "NOT NULL", "Security action name (e.g. ALLOCATION_CONFIRMED, OTP_VERIFIED)"),
            ("entity_name", "VARCHAR(60)", "NOT NULL", "Target entity class name"),
            ("entity_id", "BIGINT", "NOT NULL", "Target entity surrogate ID"),
            ("details", "TEXT", "NULLABLE", "Structured JSON or audit string detailing state change"),
            ("ip_address", "VARCHAR(50)", "NULLABLE", "Client IP address from HttpServletRequest"),
            ("created_at", "TIMESTAMPTZ", "DEFAULT CURRENT_TIMESTAMP", "Exact tamper-proof event timestamp")
        ]
    }
]

TEST_RESULTS = [
    {
        "suite": "FoodAllocationLifecycleIntegrationTest",
        "tests": [
            ("testSuccessfulFullAllocation", "Verifies 100% allocation of donation quantity transitions status to FULLY_ALLOCATED and decrements stock to 0.00.", "PASS"),
            ("testSuccessfulPartialAllocation", "Verifies allocation of partial quantity decrements stock correctly and transitions status to PARTIALLY_ALLOCATED.", "PASS"),
            ("testAllocationAgainstPendingRequestRejected", "Guarantees donor cannot allocate inventory to a request that is still in PENDING state.", "PASS"),
            ("testAllocationAgainstRejectedRequestRejected", "Guarantees donor cannot allocate inventory against an explicitly rejected request.", "PASS"),
            ("testAllocationAgainstWithdrawnRequestRejected", "Guarantees donor cannot allocate inventory against a request withdrawn by the NGO.", "PASS"),
            ("testAllocationAgainstUnavailableDonationRejected", "Guarantees allocation is rejected if donation is CANCELLED, EXPIRED, or FULLY_ALLOCATED.", "PASS"),
            ("testZeroAndNegativeQuantityRejected", "Validates boundary conditions: allocations with quantity <= 0 are rejected with HTTP 400 Bad Request.", "PASS"),
            ("testAllocationExceedingApprovedRequestQuantityRejected", "Prevents donor from allocating more than the approved requested quantity.", "PASS"),
            ("testAllocationExceedingAvailableStockRejected", "Prevents allocation from exceeding current physical inventory (InsufficientStockException).", "PASS"),
            ("testMultipleAllocationsCannotExceedRequestQuantity", "Guarantees cumulative partial allocations against a single request cannot surpass request limit.", "PASS"),
            ("testMultipleNgoRequestsCannotExceedInventory", "Ensures multiple competing approved requests are bounded by total inventory.", "PASS"),
            ("testUnauthorizedDonorCannotAllocate", "Enforces access control: donor B cannot allocate food listed by donor A (HTTP 403 Forbidden).", "PASS"),
            ("testNgoCannotInitiateAllocation", "Ensures role isolation: an NGO token attempting to trigger allocation endpoint receives HTTP 403.", "PASS"),
            ("testDonationStatusPartiallyAllocated", "Asserts entity state machine transitions correctly to PARTIALLY_ALLOCATED.", "PASS"),
            ("testDonationStatusFullyAllocated", "Asserts entity state machine transitions correctly to FULLY_ALLOCATED when available stock hits 0.00.", "PASS"),
            ("testFailedAllocationRollbackIntegrity", "Verifies ACID rollback: when an exception occurs midway, database state remains untouched.", "PASS"),
            ("testConcurrentAllocationsCannotOversellInventory", "Concurrency stress test: 5 concurrent threads executing simultaneous allocations are serialized via Pessimistic Lock with 0 overselling.", "PASS")
        ]
    },
    {
        "suite": "DeliveryLifecycleIntegrationTest",
        "tests": [
            ("test01_createDeliveryTaskForConfirmedAllocation", "Validates delivery task generation upon allocation confirmation with auto-generated 6-digit OTPs.", "PASS"),
            ("test02_rejectDeliveryCreationForUnconfirmedAllocation", "Rejects delivery task creation if referenced allocation is not in AWAITING_PICKUP status.", "PASS"),
            ("test03_preventDuplicateActiveDeliveryTasksForAllocation", "Enforces database unique constraint preventing multiple active delivery runs for one allocation.", "PASS"),
            ("test04_successfullyAssignEligibleVolunteer", "Verifies donor/admin can assign an ACTIVE volunteer to an unassigned delivery task.", "PASS"),
            ("test05_rejectAssignmentToInactiveVolunteer", "Rejects assignment if target volunteer status is SUSPENDED or PENDING_VERIFICATION.", "PASS"),
            ("test06_preventUnauthorizedDonorAssignment", "Prevents an unassociated donor from dispatching a volunteer to another donor's shipment.", "PASS"),
            ("test07_volunteerSeesOnlyTheirOwnTasks", "Enforces volunteer privacy: volunteer A cannot inspect runs assigned to volunteer B.", "PASS"),
            ("test08_volunteerAcceptsAssignment", "Transitions task status from ASSIGNED to ACCEPTED upon volunteer formal confirmation.", "PASS"),
            ("test09_volunteerRejectsAssignment", "Transitions task status to REJECTED and resets volunteer_id to NULL for reassignment.", "PASS"),
            ("test10_reassignmentAfterRejectionWorks", "Validates that a rejected delivery run can be smoothly reassigned to a secondary volunteer.", "PASS"),
            ("test11_onlyAssignedVolunteerCanConfirmPickup", "Rejects pickup OTP submission from any user other than the assigned volunteer.", "PASS"),
            ("test12_pickupBeforeAcceptanceIsRejected", "Enforces lifecycle sequence: pickup PIN cannot be verified before volunteer formal acceptance.", "PASS"),
            ("test13_pickupTimestampIsPersisted", "Confirms picked_up_at audit timestamp is recorded upon valid Kitchen Pickup PIN verification.", "PASS"),
            ("test14_volunteerCanStartTransitAfterPickup", "Transitions task status to IN_TRANSIT and updates allocation status to IN_TRANSIT.", "PASS"),
            ("test15_deliveryCannotCompleteBeforePickup", "Prevents premature delivery: NGO dropoff PIN cannot be entered while goods remain at donor kitchen.", "PASS"),
            ("test16_deliveryCompletionSucceedsAfterValidTransitions", "Full happy path: verifies delivery PIN, records recipient details, and marks task DELIVERED.", "PASS"),
            ("test17_onlyAssignedVolunteerCanCompleteDelivery", "Rejects delivery completion attempts by third-party actors or unauthorized volunteers.", "PASS"),
            ("test18_completedDeliveryCannotBeReopened", "Terminal state protection: DELIVERED tasks reject further mutations or status rollbacks.", "PASS"),
            ("test19_cancelledOrFailedTasksCannotBeCompleted", "Ensures CANCELLED or FAILED tasks cannot be transitioned to DELIVERED.", "PASS"),
            ("test20_deliveryHistoryRemainsPersisted", "Verifies immutable historical audit records for completed volunteer delivery missions.", "PASS"),
            ("test21_allocationQuantityAndStockNotDeductedSecondTime", "Crucial accounting verification: pickup and delivery events NEVER alter inventory balance.", "PASS"),
            ("test22_failedStateTransitionsDoNotPartiallyUpdateRelatedRecords", "Verifies atomicity across DeliveryTask, Allocation, and FoodDonation during runtime exceptions.", "PASS")
        ]
    },
    {
        "suite": "FoodDonationLifecycleIntegrationTest",
        "tests": [
            ("testCreateDraftDonation", "Validates creation of unlisted DRAFT food listing with complete nutritional metadata.", "PASS"),
            ("testCreateAndPublishImmediately", "Validates immediate listing creation with AVAILABLE status and public feed visibility.", "PASS"),
            ("testZeroQuantityRejection", "Rejects food listing creation with total_quantity <= 0.00.", "PASS"),
            ("testExpiryBeforePrepTimeRejection", "Rejects food listing where expiry_time is prior to or equal to preparation_time.", "PASS"),
            ("testPublishDraftDonation", "Validates state transition from DRAFT to AVAILABLE and verifies future expiry check.", "PASS"),
            ("testEditOtherDonorListingRejection", "Enforces donor isolation: donor B cannot modify food listings owned by donor A.", "PASS"),
            ("testCancelActiveDonation", "Validates donor cancellation of active listing if no confirmed allocations exist.", "PASS"),
            ("testDraftPrivacyInPublicDiscovery", "Guarantees public food feed excludes DRAFT listings and listings from suspended donors.", "PASS"),
            ("testGetMyDonationsPaginated", "Validates donor private dashboard endpoint returns correct paginated batch records.", "PASS")
        ]
    },
    {
        "suite": "FoodRequestLifecycleIntegrationTest",
        "tests": [
            ("testSuccessfulNgoRequestCreation", "Validates verified NGO can submit food request against active donation listing.", "PASS"),
            ("testNonNgoCannotSubmitRequest", "Enforces role boundary: DONOR and VOLUNTEER roles cannot create food requests (HTTP 403).", "PASS"),
            ("testRequestNonExistentDonation", "Returns HTTP 404 ResourceNotFoundException when requesting non-existent donation ID.", "PASS"),
            ("testRequestDraftDonationRejection", "Prevents NGOs from claiming unpublished DRAFT food listings.", "PASS"),
            ("testInvalidQuantityRejection", "Rejects food request with requested_quantity <= 0.00.", "PASS"),
            ("testDuplicatePendingRequestRejection", "Prevents an NGO from submitting multiple pending requests against the same donation.", "PASS"),
            ("testDonorApprovesRequest", "Validates donor can approve incoming NGO claim, recording approval timestamp and remarks.", "PASS"),
            ("testDonorRejectsRequest", "Validates donor can reject incoming claim with mandatory documented reason.", "PASS"),
            ("testNgoWithdrawsRequest", "Validates NGO can withdraw pending claim before donor approval.", "PASS"),
            ("testViewRequestsPaginated", "Validates donor and NGO paginated request review dashboards.", "PASS")
        ]
    },
    {
        "suite": "AuthenticationFlowIntegrationTest",
        "tests": [
            ("testSuccessfulDonorRegistration", "Registers commercial donor, creates app_user and donor_profile rows, returns valid JWT token.", "PASS"),
            ("testDuplicateEmailRegistrationRejection", "Prevents registration with pre-existing email address (HTTP 409 Conflict).", "PASS"),
            ("testSuccessfulLogin", "Authenticates valid user with BCrypt verification and issues compact signed JWT.", "PASS"),
            ("testInvalidPasswordRejection", "Rejects login attempt with incorrect password (HTTP 401 Unauthorized).", "PASS"),
            ("testUnknownEmailRejection", "Rejects login attempt with non-existent email address (HTTP 401 Unauthorized).", "PASS"),
            ("testJwtGenerationAndValidation", "Generates cryptographic JWT, parses claims, and validates expiration timestamp.", "PASS"),
            ("testExpiredJwtRejection", "Rejects expired JWT tokens during request filter validation.", "PASS"),
            ("testMissingTokenRejection", "Rejects unauthenticated requests to protected endpoints with HTTP 401.", "PASS"),
            ("testInvalidTokenRejection", "Rejects tampered, malformed, or illegitimately signed JWT tokens.", "PASS"),
            ("testUnauthorizedRoleAccess", "Prevents unauthorized cross-role API calls (e.g. Volunteer accessing Donor endpoints).", "PASS"),
            ("testAdminRegistrationRejection", "Strictly blocks public self-registration with ADMIN role (HTTP 403 Forbidden).", "PASS"),
            ("testPasswordHashNotExposed", "Asserts password_hash is never serialized into response JSON payloads.", "PASS")
        ]
    },
    {
        "suite": "DonorRegistrationPersistenceE2ETest",
        "tests": [
            ("testFullDonorRegistrationAndPersistenceLifecycle", "End-to-end integration test: registers donor via HTTP POST, inspects PostgreSQL app_user and donor_profile tables, verifies BCrypt salt, and confirms /auth/me profile retrieval.", "PASS")
        ]
    },
    {
        "suite": "PasswordEncoderTest",
        "tests": [
            ("testPasswordEncodingAndMatching", "Unit test verifying BCryptPasswordEncoder salt randomness, cost factor 12, and constant-time match verification.", "PASS")
        ]
    }
]

API_ENDPOINTS = [
    # Auth
    ("POST", "/api/v1/auth/register", "PUBLIC", "Register institutional user and profile (DONOR, NGO, VOLUNTEER)"),
    ("POST", "/api/v1/auth/login", "PUBLIC", "Authenticate user credentials and issue signed JWT bearer token"),
    ("GET", "/api/v1/auth/me", "AUTHENTICATED", "Retrieve authenticated user identity and institutional profile"),
    
    # Donations
    ("POST", "/api/v1/donations", "DONOR", "Create new surplus food donation listing (DRAFT or AVAILABLE)"),
    ("GET", "/api/v1/donations/public", "PUBLIC", "Search and filter active surplus food listings (category, dietary, city)"),
    ("GET", "/api/v1/donations/{id}", "PUBLIC", "Get comprehensive food donation details including nutritional info"),
    ("GET", "/api/v1/donations/my-donations", "DONOR", "Get paginated history of listings created by authenticated donor"),
    ("PUT", "/api/v1/donations/{id}", "DONOR", "Update unallocated food listing details"),
    ("PATCH", "/api/v1/donations/{id}/publish", "DONOR", "Publish draft donation listing to public feed"),
    ("PATCH", "/api/v1/donations/{id}/cancel", "DONOR", "Cancel active listing if no confirmed allocations exist"),
    
    # Requests
    ("POST", "/api/v1/requests", "NGO", "Submit food claim against available donation listing"),
    ("GET", "/api/v1/requests/donation/{donationId}", "DONOR", "View all NGO claims submitted against a specific donation batch"),
    ("GET", "/api/v1/requests/my-requests", "NGO", "View paginated history of claims submitted by authenticated NGO"),
    ("PATCH", "/api/v1/requests/{id}/approve", "DONOR", "Approve pending NGO claim with optional donor instructions"),
    ("PATCH", "/api/v1/requests/{id}/reject", "DONOR", "Reject pending NGO claim with mandatory reason"),
    ("PATCH", "/api/v1/requests/{id}/withdraw", "NGO", "Withdraw pending claim prior to donor review"),
    
    # Allocations
    ("POST", "/api/v1/allocations", "DONOR", "Execute pessimistic-locked inventory allocation against approved request"),
    ("GET", "/api/v1/allocations/my-allocations", "AUTHENTICATED", "View active and fulfilled allocations for Donor or NGO"),
    ("GET", "/api/v1/allocations/{id}", "AUTHENTICATED", "Get detailed allocation record including pickup and dropoff pins"),
    
    # Deliveries
    ("POST", "/api/v1/deliveries/assign", "DONOR / ADMIN", "Generate delivery task and assign active volunteer"),
    ("GET", "/api/v1/deliveries/my-tasks", "VOLUNTEER", "View delivery missions assigned to authenticated volunteer"),
    ("PATCH", "/api/v1/deliveries/{id}/accept", "VOLUNTEER", "Formally accept assigned delivery task"),
    ("PATCH", "/api/v1/deliveries/{id}/reject", "VOLUNTEER", "Decline assigned delivery task with explanation"),
    ("POST", "/api/v1/deliveries/{id}/verify-pickup", "VOLUNTEER", "Verify 6-digit Kitchen Pickup PIN at donor premises"),
    ("PATCH", "/api/v1/deliveries/{id}/start-transit", "VOLUNTEER", "Flag shipment vehicle in-transit to destination"),
    ("POST", "/api/v1/deliveries/{id}/verify-delivery", "VOLUNTEER", "Verify 6-digit Confirmation PIN at NGO and complete run"),
    
    # Impact
    ("GET", "/api/v1/impact/summary", "PUBLIC", "Retrieve platform aggregate CO2e diverted, meals saved, and value generated"),
    ("GET", "/api/v1/impact/donor", "DONOR", "Retrieve donor institutional sustainability metrics and ESG report")
]

