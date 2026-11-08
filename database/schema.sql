-- ====================================================================
-- REPLATE Database Schema (PostgreSQL 14+)
-- Food Waste Donation & Redistribution System
-- Package: com.replate
-- ====================================================================

DROP TABLE IF EXISTS audit_log CASCADE;
DROP TABLE IF EXISTS delivery_task CASCADE;
DROP TABLE IF EXISTS allocation CASCADE;
DROP TABLE IF EXISTS food_request CASCADE;
DROP TABLE IF EXISTS food_donation CASCADE;
DROP TABLE IF EXISTS volunteer_profile CASCADE;
DROP TABLE IF EXISTS ngo_profile CASCADE;
DROP TABLE IF EXISTS donor_profile CASCADE;
DROP TABLE IF EXISTS app_user CASCADE;

-- 1. Base User Table
CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    role VARCHAR(30) NOT NULL CHECK (role IN ('DONOR', 'NGO', 'VOLUNTEER', 'ADMIN')),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('PENDING_VERIFICATION', 'ACTIVE', 'SUSPENDED')),
    address_line1 VARCHAR(255) NOT NULL,
    city VARCHAR(80) NOT NULL,
    state VARCHAR(80) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    latitude NUMERIC(10, 7),
    longitude NUMERIC(10, 7),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Donor Profile
CREATE TABLE donor_profile (
    user_id BIGINT PRIMARY KEY REFERENCES app_user(id) ON DELETE CASCADE,
    organization_name VARCHAR(150) NOT NULL,
    organization_type VARCHAR(50) NOT NULL CHECK (organization_type IN ('RESTAURANT', 'HOTEL_BANQUET', 'HOSTEL_MESS', 'CATERER', 'SWEET_CORNER')),
    fssai_license_no VARCHAR(30) NOT NULL UNIQUE,
    operating_hours VARCHAR(100)
);

-- 3. NGO Profile
CREATE TABLE ngo_profile (
    user_id BIGINT PRIMARY KEY REFERENCES app_user(id) ON DELETE CASCADE,
    ngo_name VARCHAR(150) NOT NULL,
    darpan_id VARCHAR(50) UNIQUE,
    registration_80g VARCHAR(50),
    daily_beneficiary_capacity INT NOT NULL DEFAULT 50 CHECK (daily_beneficiary_capacity > 0),
    verification_status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (verification_status IN ('PENDING', 'VERIFIED', 'REJECTED'))
);

-- 4. Volunteer Profile
CREATE TABLE volunteer_profile (
    user_id BIGINT PRIMARY KEY REFERENCES app_user(id) ON DELETE CASCADE,
    vehicle_type VARCHAR(40) NOT NULL CHECK (vehicle_type IN ('TWO_WHEELER', 'FOUR_WHEELER', 'AUTO_VAN', 'WALK')),
    emergency_contact VARCHAR(20) NOT NULL,
    total_deliveries INT NOT NULL DEFAULT 0 CHECK (total_deliveries >= 0)
);

-- 5. Food Donation Listing
CREATE TABLE food_donation (
    id BIGSERIAL PRIMARY KEY,
    donor_id BIGINT NOT NULL REFERENCES donor_profile(user_id) ON DELETE RESTRICT,
    title VARCHAR(160) NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (category IN ('COOKED_MEALS', 'BREADS_ROTIS', 'DAIRY_SWEETS', 'RAW_PRODUCE', 'PACKAGED_GOODS')),
    total_quantity NUMERIC(10,2) NOT NULL CHECK (total_quantity > 0),
    available_quantity NUMERIC(10,2) NOT NULL CHECK (available_quantity >= 0),
    quantity_unit VARCHAR(20) NOT NULL CHECK (quantity_unit IN ('KG', 'MEAL_BOXES', 'LITERS', 'PORTIONS')),
    dietary_type VARCHAR(30) NOT NULL CHECK (dietary_type IN ('PURE_VEG', 'NON_VEG', 'VEGAN')),
    storage_condition VARCHAR(40) NOT NULL CHECK (storage_condition IN ('HOT_INSULATED', 'REFRIGERATED', 'AMBIENT')),
    preparation_time TIMESTAMP WITH TIME ZONE NOT NULL,
    expiry_time TIMESTAMP WITH TIME ZONE NOT NULL,
    pickup_notes TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'PARTIALLY_ALLOCATED', 'FULLY_ALLOCATED', 'COMPLETED', 'EXPIRED', 'CANCELLED')),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_expiry_after_prep CHECK (expiry_time > preparation_time),
    CONSTRAINT check_available_lte_total CHECK (available_quantity <= total_quantity)
);

-- 6. Food Request
CREATE TABLE food_request (
    id BIGSERIAL PRIMARY KEY,
    donation_id BIGINT NOT NULL REFERENCES food_donation(id) ON DELETE CASCADE,
    ngo_id BIGINT NOT NULL REFERENCES ngo_profile(user_id) ON DELETE RESTRICT,
    requested_quantity NUMERIC(10,2) NOT NULL CHECK (requested_quantity > 0),
    intended_beneficiaries TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')),
    rejection_reason VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 7. Allocation Record
CREATE TABLE allocation (
    id BIGSERIAL PRIMARY KEY,
    request_id BIGINT NOT NULL UNIQUE REFERENCES food_request(id) ON DELETE RESTRICT,
    donation_id BIGINT NOT NULL REFERENCES food_donation(id) ON DELETE RESTRICT,
    allocated_quantity NUMERIC(10,2) NOT NULL CHECK (allocated_quantity > 0),
    status VARCHAR(30) NOT NULL DEFAULT 'AWAITING_PICKUP' CHECK (status IN ('AWAITING_PICKUP', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8. Delivery Task
CREATE TABLE delivery_task (
    id BIGSERIAL PRIMARY KEY,
    allocation_id BIGINT NOT NULL UNIQUE REFERENCES allocation(id) ON DELETE CASCADE,
    volunteer_id BIGINT REFERENCES volunteer_profile(user_id) ON DELETE SET NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'UNASSIGNED' CHECK (status IN ('UNASSIGNED', 'ASSIGNED', 'PICKED_UP', 'DELIVERED', 'FAILED')),
    pickup_otp VARCHAR(6) NOT NULL,
    delivery_otp VARCHAR(6) NOT NULL,
    picked_up_at TIMESTAMP WITH TIME ZONE,
    delivered_at TIMESTAMP WITH TIME ZONE,
    notes TEXT
);

-- 9. System Audit Log
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES app_user(id) ON DELETE SET NULL,
    action VARCHAR(80) NOT NULL,
    entity_name VARCHAR(60) NOT NULL,
    entity_id BIGINT NOT NULL,
    details TEXT,
    ip_address VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for Fast Querying & Concurrency Checks
CREATE INDEX idx_food_donation_status ON food_donation(status);
CREATE INDEX idx_food_donation_expiry ON food_donation(expiry_time);
CREATE INDEX idx_food_request_donation ON food_request(donation_id);
CREATE INDEX idx_food_request_ngo ON food_request(ngo_id);
CREATE INDEX idx_allocation_donation ON allocation(donation_id);
CREATE INDEX idx_delivery_volunteer ON delivery_task(volunteer_id);
