# REPLATE: Live Demonstration Script for University External Examiner
## A Step-by-Step 5-Minute Practical Walkthrough

This guide provides an exact, minute-by-minute script to follow while demonstrating the live REPLATE application to the external examiner during your university viva examination.

---

## 0. Pre-Demo Environment Checklist (Run 2 Minutes Before Evaluation)

Verify both services are responding locally:
- **Frontend SPA:** [`http://localhost:3000`](http://localhost:3000)
- **Backend API:** `http://localhost:8080/api/v1/donations/public`
- **Database:** PostgreSQL 15 on port `5432` (`replate_db`)

Have 3 separate browser windows or tabs ready:
1. **Tab A:** Incognito window (for Public & Volunteer views)
2. **Tab B:** Regular window (for Donor console)
3. **Tab C:** Secondary window / profile (for NGO console)

---

## Minute 0:00 – 1:00: Public Landing Page & Environmental Impact Engine

### What to Show:
1. Open [`http://localhost:3000`](http://localhost:3000).
2. Scroll to the **Hero Section** and highlight the tagline: *"Rescue Food. Restore Hope."*
3. Scroll down to the **"Measurable Sustainability"** section.

### What to Say to the Examiner:
> *"Respected examiner, we begin at the public landing page. REPLATE is not just a food listing board; it integrates a deterministic environmental and financial impact assessment engine. Every transaction records weight, timestamps, and food category, calculating landfill methane avoidance using the standard UNEP factor of 2.50 kg CO2 equivalent per kilogram of diverted food. In our Java backend, this is powered by compile-time method overloading inside `ImpactCalculator.java`."*

---

## Minute 1:00 – 2:00: Public Food Discovery & Dynamic Filtering

### What to Show:
1. Click **"Explore Food"** in the navigation bar to navigate to [`http://localhost:3000/explore`](http://localhost:3000/explore).
2. Point out the live cards:
   - Notice the category badges (`Cooked Meals`, `Breads & Rotis`, `Dairy & Sweets`, `Raw Produce`).
   - Highlight the **dynamic countdown timer to expiry** on each card (e.g., *"Expires in 5h 22m"*).
3. Test a filter:
   - Click the **"Pure Veg"** filter chip. The catalog filters instantly.
   - Search for **"Bengaluru"** or select **"Cooked Meals"**.
4. Click on a specific food card (e.g., *South Indian Deluxe Thali Meal Boxes* from Saravana Bhavan).
5. Point out the storage condition: *"HOT_INSULATED, 65°C"* and pickup notes.

### What to Say to the Examiner:
> *"Here in the public discovery feed, our JPA repository executes dynamic queries composed via Spring Data JPA Specifications. Crucially, the feed strictly enforces food safety by filtering out expired food (`expiry_time > CURRENT_TIMESTAMP`) and drafts. NGOs and shelters can inspect the nutritional parameters, preparation timestamp, and required storage conditions before requesting."*

---

## Minute 2:00 – 3:00: Donor Console & Surplus Batch Publishing

### What to Show:
1. Click **"Login"** in Tab B and enter:
   - **Email:** `contact@saravanabhavan.in`
   - **Password:** `Password@123`
2. You land directly on the **Donor Dashboard**:
   - Point out the active inventory listings table and available stock counters.
3. Click **"Post Surplus Food"**:
   - Enter Title: *"Evening Catering Mini Samosas & Mint Chutney"*
   - Category: `COOKED_MEALS`
   - Total Quantity: `50`, Unit: `MEAL_BOXES`, Dietary: `PURE_VEG`
   - Storage: `HOT_INSULATED`
   - Set Expiry to 6 hours from now.
   - Select **"Publish Immediately"** and click **"Create Donation"**.
4. Notice the newly listed batch appears with status `AVAILABLE` and available stock `50.00`.
5. Switch to Tab A (`/explore`) and refresh: the new listing is instantly visible to all NGOs!

### What to Say to the Examiner:
> *"In the donor console, domain state machine transitions are strictly guarded inside `FoodDonation.java`. When a donor publishes a listing, the entity self-validates that total quantity is strictly positive and preparation time is strictly prior to expiry time. Notice how the user identity is modeled using polymorphic composition with `@MapsId`, meaning the donor profile shares its primary key directly with the base user record."*

---

## Minute 3:00 – 4:00: NGO Claim & Transactional Allocation with Pessimistic Locking

### What to Show:
1. Open Tab C and log in as an NGO:
   - **Email:** `foodrescue@robinhoodarmy.org`
   - **Password:** `Password@123`
2. Navigate to [`http://localhost:3000/explore`](http://localhost:3000/explore).
3. Click on the newly published *"Evening Catering Mini Samosas"* card.
4. Click **"Request Food"**:
   - Requested Quantity: `30`
   - Beneficiaries: *"Distribution to 30 children at Koramangala night shelter."*
   - Click **"Submit Claim"**.
5. Switch back to the **Donor Window (Tab B)**:
   - Click the **"Review Claims"** tab.
   - The incoming claim for 30 boxes from Robin Hood Army is visible under `PENDING`.
   - Click **"Approve Claim"**.
6. Now click **"Allocate Food"**:
   - Allocate Quantity: `30` boxes.
   - Click **"Confirm Allocation"**.
7. Notice:
   - The donation available quantity decrements from `50.00` to `20.00`!
   - Status automatically advances to `PARTIALLY_ALLOCATED`.
   - A committed `Allocation` record is created, and the **Kitchen Pickup PIN** is generated!

### What to Say to the Examiner:
> *"Respected examiner, this is the core concurrency innovation of REPLATE. Approving the request recorded institutional agreement without touching inventory stock. When the donor confirmed allocation, `AllocationServiceImpl` acquired a pessimistic write lock (`SELECT ... FOR UPDATE` in PostgreSQL) on both the request and the donation. Under `READ_COMMITTED` isolation, this eliminates race conditions and prevents inventory overselling under high concurrency."*

---

## Minute 4:00 – 5:00: Volunteer Logistics & Dual-OTP Handshake

### What to Show:
1. In Tab B (Donor Console), open the **"Logistics Dispatch"** tab:
   - Point out the confirmed allocation and the **Kitchen Pickup PIN** (e.g. `482910` or the newly generated 6-digit code).
2. In Tab C (NGO Console), open the **"Allocations & Handover PINs"** tab:
   - Point out the **Delivery Confirmation PIN** (e.g. `731940`).
3. Now open an Incognito window (Tab A) and log in as Volunteer **Aarav Sharma**:
   - **Email:** `aarav.sharma@gmail.com`
   - **Password:** `Password@123`
4. The **Volunteer Logistics Hub** opens:
   - Show the assigned delivery task.
   - Click **"Confirm Pickup"**:
     - Enter the 6-digit Kitchen Pickup PIN from the Donor.
     - Click **"Verify Pickup"**: Status immediately changes to `PICKED_UP`.
   - Click **"Start Transit"**: Status transitions to `IN_TRANSIT`.
   - Click **"Complete Delivery"**:
     - Enter the 6-digit Delivery Confirmation PIN from the NGO.
     - Enter Recipient Name: *"Sister Mary"* and click **"Verify Delivery"**.
   - Status transitions to `DELIVERED`!
5. Point out to the examiner:
   - The delivery run is completed and archived.
   - **Crucial Invariant:** Physical pickup and delivery verification *never* mutated inventory stock a second time!

### What to Say to the Examiner:
> *"Here we demonstrate our encapsulated dual-OTP chain-of-custody workflow. The volunteer cannot claim pickup without the physical donor's Kitchen Pickup PIN, and cannot complete delivery without the destination NGO's Confirmation PIN. Both entities implement our pure `Trackable` behavioral interface polymorphically. Furthermore, zero stock re-deduction occurs during transit, completely eliminating double-decrement accounting bugs."*

---

## Quick Reference Summary Table for Live Demo

| Step | User / Role | Action | URL / Location | Technical Concept Highlight |
|---|---|---|---|---|
| **1** | Public Guest | View Impact Metrics | [`http://localhost:3000`](http://localhost:3000) | Method Overloading in `ImpactCalculator.java` (Compile-Time Polymorphism) |
| **2** | Public Guest | Search & Filter Listings | [`http://localhost:3000/explore`](http://localhost:3000/explore) | JPA Criteria Specifications & Future Expiry Enforcement |
| **3** | Donor (`contact@saravanabhavan.in`) | Post & Publish Surplus Food | `/donor/dashboard` | Stateful Domain Entities, `@MapsId` Polymorphic Composition |
| **4** | NGO (`foodrescue@robinhoodarmy.org`) | Submit Food Demand Claim | `/explore` $\to$ Modal | Contract-Driven Separation of Concerns (`Approvable` interface) |
| **5** | Donor (`contact@saravanabhavan.in`) | Approve & Allocate Stock | `/donor/dashboard` | ACID Pessimistic Write Locking (`SELECT ... FOR UPDATE`) |
| **6** | Volunteer (`aarav.sharma@gmail.com`)| Verify Pickup & Dropoff OTPs | `/volunteer/dashboard` | Dual-OTP Chain of Custody & Runtime Polymorphism (`Trackable`) |
