-- ====================================================================
-- REPLATE Seed Data (Indian Context)
-- Realistic initial dataset for local testing and viva demonstrations
-- ====================================================================

-- 1. Base Users (BCrypt hashed password for 'Password@123': $2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS)
INSERT INTO app_user (id, email, password_hash, phone_number, full_name, role, status, address_line1, city, state, pincode, latitude, longitude) VALUES
(1, 'contact@saravanabhavan.in', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-8025211234', 'Saravana Bhavan Foods Pvt Ltd', 'DONOR', 'ACTIVE', '100 Feet Road, Indiranagar', 'Bengaluru', 'Karnataka', '560038', 12.9784, 77.6408),
(2, 'events@haldiramsdelhi.com', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-1143529900', 'Haldirams Sweets & Catering', 'DONOR', 'ACTIVE', 'B-Block, Connaught Place', 'New Delhi', 'Delhi', '110001', 28.6328, 77.2197),
(3, 'banquets@tridentmumbai.com', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-2266324343', 'Trident Nariman Point Banquets', 'DONOR', 'ACTIVE', 'Nariman Point, Marine Drive', 'Mumbai', 'Maharashtra', '400021', 18.9274, 72.8211),
(4, 'foodrescue@robinhoodarmy.org', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-9876500123', 'Robin Hood Army (Bengaluru)', 'NGO', 'ACTIVE', 'Koramangala 4th Block', 'Bengaluru', 'Karnataka', '560034', 12.9352, 77.6245),
(5, 'relief@rotibankmumbai.org', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-9820011223', 'Roti Bank Mumbai Foundation', 'NGO', 'ACTIVE', 'Opposite Dadar Central Station', 'Mumbai', 'Maharashtra', '400014', 19.0178, 72.8478),
(6, 'aarav.sharma@gmail.com', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-9811234567', 'Aarav Sharma', 'VOLUNTEER', 'ACTIVE', 'Defence Colony', 'New Delhi', 'Delhi', '110024', 28.5729, 77.2289),
(7, 'pooja.deshmukh@gmail.com', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-9769876543', 'Pooja Deshmukh', 'VOLUNTEER', 'ACTIVE', 'Shivaji Park, Dadar West', 'Mumbai', 'Maharashtra', '400028', 19.0269, 72.8373),
(8, 'admin@replate.org', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-8012345678', 'Replate Systems Admin', 'ADMIN', 'ACTIVE', 'Koramangala Incubator Hub', 'Bengaluru', 'Karnataka', '560095', 12.9345, 77.6190);

-- Sync sequence for app_user
SELECT setval('app_user_id_seq', (SELECT MAX(id) FROM app_user));

-- 2. Donor Profiles
INSERT INTO donor_profile (user_id, organization_name, organization_type, fssai_license_no, operating_hours) VALUES
(1, 'Saravana Bhavan Foods Pvt Ltd', 'RESTAURANT', '11220334000123', '07:00 AM - 11:00 PM'),
(2, 'Haldirams Sweets & Catering', 'CATERER', '13318005000456', '09:00 AM - 10:30 PM'),
(3, 'Trident Nariman Point Banquets', 'HOTEL_BANQUET', '11516001000789', '24 Hours');

-- 3. NGO Profiles
INSERT INTO ngo_profile (user_id, ngo_name, darpan_id, registration_80g, daily_beneficiary_capacity, verification_status) VALUES
(4, 'Robin Hood Army (Bengaluru)', 'KA/2018/0192834', 'AAATR1234PF20214', 350, 'VERIFIED'),
(5, 'Roti Bank Mumbai Foundation', 'MH/2017/0088712', 'AABCR9876QF20191', 800, 'VERIFIED');

-- 4. Volunteer Profiles
INSERT INTO volunteer_profile (user_id, vehicle_type, emergency_contact, total_deliveries) VALUES
(6, 'TWO_WHEELER', '+91-9811234560', 42),
(7, 'FOUR_WHEELER', '+91-9769876540', 68);

-- 5. Food Donations (Surplus Indian meals from commercial events)
INSERT INTO food_donation (id, donor_id, title, category, total_quantity, available_quantity, quantity_unit, dietary_type, storage_condition, preparation_time, expiry_time, pickup_notes, status, version) VALUES
(1, 1, 'Fresh Vegetable Pulao & Mixed Veg Kurma', 'COOKED_MEALS', 80.00, 30.00, 'MEAL_BOXES', 'PURE_VEG', 'HOT_INSULATED', NOW() - INTERVAL '2 hours', NOW() + INTERVAL '4 hours', 'Packed in insulated stainless food containers. Collect from rear kitchen dispatch gate.', 'PARTIALLY_ALLOCATED', 1),
(2, 2, 'Assorted Tandoori Rotis & Dal Makhani', 'COOKED_MEALS', 120.00, 120.00, 'PORTIONS', 'PURE_VEG', 'HOT_INSULATED', NOW() - INTERVAL '1 hour', NOW() + INTERVAL '5 hours', 'From afternoon corporate banquet hall. Sealed foil meal boxes.', 'AVAILABLE', 0),
(3, 3, 'Gourmet Paneer Butter Masala & Jeera Rice', 'COOKED_MEALS', 60.00, 60.00, 'MEAL_BOXES', 'PURE_VEG', 'HOT_INSULATED', NOW() - INTERVAL '90 minutes', NOW() + INTERVAL '3 hours 30 minutes', 'Service entrance near Gate 3. Contact Banquet Chef Ramesh.', 'AVAILABLE', 0),
(4, 2, 'Freshly Prepared Gulab Jamun & Rasgullas', 'DAIRY_SWEETS', 45.00, 45.00, 'KG', 'PURE_VEG', 'AMBIENT', NOW() - INTERVAL '3 hours', NOW() + INTERVAL '24 hours', 'In commercial airtight food-grade buckets.', 'AVAILABLE', 0);

SELECT setval('food_donation_id_seq', (SELECT MAX(id) FROM food_donation));

-- 6. Food Request (Robin Hood Army requesting 50 boxes from Saravana Bhavan)
INSERT INTO food_request (id, donation_id, ngo_id, requested_quantity, intended_beneficiaries, status, rejection_reason) VALUES
(1, 1, 4, 50.00, 'Distribution to 50 daily wage worker families at Indiranagar metro slum settlement.', 'APPROVED', NULL);

SELECT setval('food_request_id_seq', (SELECT MAX(id) FROM food_request));

-- 7. Allocation Record for the approved request
INSERT INTO allocation (id, request_id, donation_id, allocated_quantity, status) VALUES
(1, 1, 1, 50.00, 'IN_TRANSIT');

SELECT setval('allocation_id_seq', (SELECT MAX(id) FROM allocation));

-- 8. Delivery Task (Assigned to Volunteer Aarav Sharma)
INSERT INTO delivery_task (id, allocation_id, volunteer_id, status, pickup_otp, delivery_otp, picked_up_at, delivered_at, notes) VALUES
(1, 1, 6, 'PICKED_UP', '492810', '710345', NOW() - INTERVAL '25 minutes', NULL, 'Food temperature verified at 65°C upon pickup. Packed securely in thermo-insulated carriers.');

SELECT setval('delivery_task_id_seq', (SELECT MAX(id) FROM delivery_task));

