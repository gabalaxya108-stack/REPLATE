# REPLATE: University Examiner Viva Voce Q&A Cheat Sheet
## 15 Critical Technical Defense Questions & Model Answers
**Course:** Object-Oriented Programming Using Java (B.Tech CSE/IT)

---

### Q1: How does REPLATE demonstrate Encapsulation beyond trivial getters and setters?
**Model Answer:**  
> "In REPLATE, encapsulation is implemented as active domain invariant protection rather than passive data exposure. For example, in `FoodDonation.java`, the field `availableQuantity` is private, and external services cannot directly mutate it. Instead, they must invoke the synchronized domain method `public synchronized boolean deductQuantity(BigDecimal requestedQty)`. This method validates that `requestedQty > 0` and `requestedQty <= availableQuantity`, decrements the quantity, and automatically evaluates whether the status should advance to `PARTIALLY_ALLOCATED` or `FULLY_ALLOCATED`. No external controller or service can inadvertently set a negative quantity or force an invalid status. This ensures that the entity remains in a valid, coherent state at all times."

---

### Q2: What is the architectural difference between Abstract Classes and Interfaces, and where are they used in your project?
**Model Answer:**  
> "An abstract class defines an `IS-A` relationship with shared structural state, constructors, and default implementations, whereas an interface defines a behavioral `CAN-DO` functional contract without state.
> In REPLATE:
> - **Abstract Class:** `BaseEntity.java` is an abstract mapped superclass. Every persistent model (`FoodDonation`, `FoodRequest`, `BaseUser`, `DeliveryTask`) *IS-A* `BaseEntity`, inheriting surrogate identity `id`, optimistic concurrency `@Version version`, and lifecycle timestamps (`createdAt`, `updatedAt`).
> - **Interfaces:** `Trackable.java` and `Approvable.java` are pure behavioral interfaces. Heterogeneous classes implement them. For instance, both `FoodDonation` and `DeliveryTask` implement `Trackable` to participate in polymorphic tracking workflows without sharing a database table."

---

### Q3: How is Runtime Polymorphism (Dynamic Method Dispatch) implemented in REPLATE?
**Model Answer:**  
> "Runtime polymorphism is implemented via the `Trackable` interface:
> ```java
> public interface Trackable {
>     String getCurrentStatusDescription();
>     int getProgressPercentage();
>     boolean isTerminalState();
> }
> ```
> Both `FoodDonation` and `DeliveryTask` implement `Trackable`. When a client requests status tracking, the tracking pipeline interacts with the object via the `Trackable` reference type:
> ```java
> Trackable trackable = deliveryService.getDeliveryTaskById(id);
> int progress = trackable.getProgressPercentage();
> ```
> The JVM executes dynamic method dispatch at runtime, invoking `DeliveryTask.getProgressPercentage()` or `FoodDonation.getProgressPercentage()` depending on the concrete instance. This completely decouples the presentation layer from the persistence implementation."

---

### Q4: How is Compile-Time Polymorphism (Method Overloading) implemented in REPLATE?
**Model Answer:**  
> "Compile-time polymorphism is implemented in `ImpactCalculator.java` through method overloading. We have multiple methods with the exact same name but differing parameter types and counts:
> 1. `calculateCo2Offset(BigDecimal weightInKg)`: Directly computes emissions offset from gross weight using the UNEP 2.50 kg CO2e factor.
> 2. `calculateCo2Offset(Integer mealBoxes, FoodCategory category)`: Computes emissions offset by first deriving weight from meal box count (0.45 kg/box) and applying category-specific emission multipliers.
> 3. `calculateValueSaved(Integer mealBoxes)` vs `calculateValueSaved(BigDecimal weightInKg)`.
> The Java compiler resolves the exact method binding at compile time based on the call-site argument types."

---

### Q5: Why did you separate Request Approval from Stock Allocation? Why not allocate immediately upon approval?
**Model Answer:**  
> "Separation of concerns. In commercial banquet and catering scenarios, multiple welfare organizations submit claims for the same batch. Request approval (`request.approve()`) represents the donor's administrative review and institutional agreement, but does *not* lock inventory. 
> If approval deducted stock immediately, the donor would be forced to decide all allocations upfront, holding long-lived transactional locks. By decoupling them:
> 1. Request review is non-blocking and flexible.
> 2. Stock allocation is executed exclusively by `AllocationServiceImpl` within an ACID transaction isolated under `READ_COMMITTED` with pessimistic write locks (`SELECT ... FOR UPDATE`).
> This guarantees that inventory is only committed when the transfer is finalized."

---

### Q6: What is the difference between Optimistic and Pessimistic Locking, and where are they used in REPLATE?
**Model Answer:**  
> "- **Optimistic Locking:** Assumes concurrent collisions are rare. We implement this on `FoodDonation` using JPA `@Version private Long version;`. When a donor updates non-conflicting metadata (such as pickup notes), Hibernate validates `WHERE version = :currentVersion`. If another process modified the row, an `OptimisticLockException` is thrown.
> - **Pessimistic Write Locking:** Forces sequential execution at the database engine level. We use `@Lock(LockModeType.PESSIMISTIC_WRITE)` in `FoodDonationRepository` and `FoodRequestRepository`. When an allocation transaction starts, PostgreSQL executes `SELECT ... FOR UPDATE`, blocking competing allocation requests until the current transaction commits or rolls back.
> We use pessimistic locking for inventory allocation because high contention would cause mass optimistic lock exceptions, degrading user experience."

---

### Q7: How does your 5-thread concurrency test prove that inventory cannot be oversold?
**Model Answer:**  
> "In `FoodAllocationLifecycleIntegrationTest.testConcurrentAllocationsCannotOversellInventory`, we initialize a donation batch of exactly 25.00 meal boxes. Five separate NGOs each submit an approved claim for 10.00 boxes (total demand = 50.00 boxes).
> Using a `CountDownLatch` and an `ExecutorService`, all 5 threads fire allocation requests simultaneously. Because `AllocationServiceImpl` acquires pessimistic write locks, PostgreSQL serializes the threads. Exactly 2 threads succeed (deducting 20.00 boxes), while the remaining 3 threads encounter `InsufficientStockException`. The database asserts that final available quantity is exactly 5.00 boxes, with zero overselling, zero race conditions, and zero negative inventory balance."

---

### Q8: How does the Dual-OTP verification handshake work, and why does it not mutate inventory stock?
**Model Answer:**  
> "When an allocation is confirmed, the system initializes a `DeliveryTask` with two cryptographically pseudo-random 6-digit PINs:
> 1. **Kitchen Pickup PIN:** Delivered to the Donor. The volunteer must physically arrive at the donor premises and solicit this PIN to transition the task to `PICKED_UP` and `IN_TRANSIT`.
> 2. **Delivery Confirmation PIN:** Delivered to the receiving NGO. The volunteer must solicit this PIN at destination dropoff to transition the task to `DELIVERED`.
> **Stock Invariant:** Stock deduction occurred *once* when the allocation was committed. Physical transit transitions update timestamps (`picked_up_at`, `delivered_at`) and audit logs, but *never* invoke `donation.deductQuantity()`. This ensures zero double-deduction accounting discrepancies."

---

### Q9: Why did you choose polymorphic composition with `@MapsId` instead of Single-Table or Table-Per-Class Inheritance for user roles?
**Model Answer:**  
> "Single-Table Inheritance (`@Inheritance(strategy = InheritanceType.SINGLE_TABLE)`) results in an un-normalized, sparse table with dozens of nullable columns (e.g. NGOs do not have FSSAI licenses; donors do not have NGO Darpan IDs). 
> Instead, we used polymorphic composition with shared primary keys:
> - `app_user` contains universal authentication, contact, and address attributes.
> - `donor_profile`, `ngo_profile`, and `volunteer_profile` extend the base user via `@OneToOne @MapsId @JoinColumn(name = 'user_id')`.
> This guarantees Third Normal Form (3NF), enforces non-null foreign key constraints, avoids sparse tables, and allows each role's profile to be fetched cleanly without locking unrelated role data."

---

### Q10: How does your application implement Value Objects and Composition?
**Model Answer:**  
> "Through `LocationCoordinate.java`. In domain-driven design, a value object has no distinct surrogate identity and is defined entirely by its attributes. 
> `LocationCoordinate` encapsulates street address, city, state, pincode, latitude, and longitude. It is embedded into `BaseUser` and `FoodDonation` using JPA `@Embedded` and `@Embeddable`.
> It also provides defensive validation (e.g. verifying that latitude is between -90 and +90 degrees) and contains business methods like calculating Haversine distance. If the enclosing `BaseUser` is deleted, its location ceases to exist, which is the textbook definition of composition."

---

### Q11: How is stateless security handled with Spring Security 6?
**Model Answer:**  
> "We configured a stateless `SecurityFilterChain` bean using Spring Security 6's lambda DSL. Sessions are disabled via `sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))`.
> On every inbound request:
> 1. `JwtAuthenticationFilter` intercepts the request and extracts the `Authorization: Bearer <token>` header.
> 2. It verifies the HMAC-SHA256 signature using JJWT 0.12.6 and validates the expiration timestamp (24-hour TTL).
> 3. It parses the claims (username, user ID, role authority) and sets a `UsernamePasswordAuthenticationToken` in `SecurityContextHolder.getContext()`.
> 4. Role-based request matchers evaluate endpoints (e.g. `/api/v1/donations/**` requires `ROLE_DONOR`)."

---

### Q12: How are passwords secured in REPLATE?
**Model Answer:**  
> "Passwords are encrypted using `BCryptPasswordEncoder` with a work factor of 12. BCrypt incorporates a 128-bit salt generated from a cryptographically secure pseudo-random number generator (CSPRNG), preventing dictionary and rainbow table attacks.
> Furthermore:
> 1. Raw passwords are never persisted or logged.
> 2. The `password_hash` field is annotated with `@JsonIgnore` and explicitly suppressed from all DTOs and API responses.
> 3. Our test suite includes `testPasswordHashNotExposed` which verifies that the hash is never leaked over JSON endpoints."

---

### Q13: How does your Custom Exception Class Hierarchy work polymorphically?
**Model Answer:**  
> "We constructed a structured exception tree rooted at `ReplateException extends RuntimeException`:
> ```
> ReplateException (abstract, contains errorCode and HTTP status)
>  ├── ResourceNotFoundException (HTTP 404)
>  ├── InsufficientStockException (HTTP 400)
>  ├── InvalidStateTransitionException (HTTP 400)
>  ├── UnauthorizedRoleException (HTTP 403)
>  └── InvalidOtpException (HTTP 400)
> ```
> In `GlobalExceptionHandler.java`, we catch the root exception polymorphically:
> ```java
> @ExceptionHandler(ReplateException.class)
> public ResponseEntity<ApiResponse<Void>> handleDomainException(ReplateException ex) {
>     return ResponseEntity.status(ex.getHttpStatus())
>         .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
> }
> ```
> This eliminates duplicated controller try-catch blocks and guarantees uniform, standardized JSON error envelopes across the entire API."

---

### Q14: How does Java Generics improve reliability in REPLATE?
**Model Answer:**  
> "We implement Java Generics in `ApiResponse<T>`:
> ```java
> public class ApiResponse<T> {
>     private boolean success;
>     private String message;
>     private T data;
>     private Instant timestamp;
>     
>     public static <T> ApiResponse<T> success(T data) { ... }
> }
> ```
> By parameterizing the payload type `T`, controllers return strongly-typed response entities (e.g., `ApiResponse<DonationResponseDTO>` or `ApiResponse<PageResponseDTO<T>>`). The compiler catches type mismatches at compile time, eliminating runtime `ClassCastException` hazards."

---

### Q15: What automated tests have you implemented to verify these claims?
**Model Answer:**  
> "We have implemented 73 automated tests across 8 test suites achieving a 100% pass rate:
> 1. `FoodAllocationLifecycleIntegrationTest` (17 tests): Verifying full/partial allocations, negative quantity rejections, boundary checks, and 5-thread concurrency safety.
> 2. `DeliveryLifecycleIntegrationTest` (22 tests): Verifying volunteer assignment, pickup/delivery OTP verification, terminal state immutability, and zero stock re-deduction.
> 3. `FoodDonationLifecycleIntegrationTest` (9 tests): Verifying draft privacy, expiry rules, and publishing.
> 4. `FoodRequestLifecycleIntegrationTest` (10 tests): Verifying claim approvals, rejections, and withdrawals.
> 5. `AuthenticationFlowIntegrationTest` (12 tests): Verifying JWT filter, BCrypt hashing, and role isolation.
> 6. `DonorRegistrationPersistenceE2ETest` (1 test): E2E registration to PostgreSQL persistence check.
> 7. `PasswordEncoderTest` (1 test): BCrypt work factor 12 salt randomness check.
> 8. `ReplateApplicationTests` (1 test): Spring context bootstrapping check."
