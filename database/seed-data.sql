-- ====================================================================
-- REPLATE Comprehensive Production-Grade Seed Data (Indian Context)
-- High-fidelity dataset for real-time live demonstrations, viva defense,
-- and full end-to-end multi-role testing.
-- Default Password for all demo users: Password@123
-- ====================================================================

-- Clean previous transactional tables safely
TRUNCATE TABLE audit_log, delivery_task, allocation, food_request, food_donation, volunteer_profile, ngo_profile, donor_profile, app_user CASCADE;

-- 1. Base Users
-- Standard password hash for 'Password@123' (BCrypt cost 10): $2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS
-- gabalaxya108@gmail.com preserves original user password hash
INSERT INTO app_user (id, email, password_hash, phone_number, full_name, role, status, address_line1, city, state, pincode, latitude, longitude) VALUES
(1, 'contact@saravanabhavan.in', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-8025211234', 'Saravana Bhavan Foods Pvt Ltd', 'DONOR', 'ACTIVE', '100 Feet Road, Indiranagar', 'Bengaluru', 'Karnataka', '560038', 12.9784, 77.6408),
(2, 'events@haldiramsdelhi.com', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-1143529900', 'Haldirams Sweets & Catering', 'DONOR', 'ACTIVE', 'B-Block, Connaught Place', 'New Delhi', 'Delhi', '110001', 28.6328, 77.2197),
(3, 'banquets@tridentmumbai.com', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-2266324343', 'Trident Nariman Point Banquets', 'DONOR', 'ACTIVE', 'Nariman Point, Marine Drive', 'Mumbai', 'Maharashtra', '400021', 18.9274, 72.8211),
(4, 'foodrescue@robinhoodarmy.org', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-9876500123', 'Robin Hood Army (Bengaluru)', 'NGO', 'ACTIVE', 'Koramangala 4th Block', 'Bengaluru', 'Karnataka', '560034', 12.9352, 77.6245),
(5, 'relief@rotibankmumbai.org', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-9820011223', 'Roti Bank Mumbai Foundation', 'NGO', 'ACTIVE', 'Opposite Dadar Central Station', 'Mumbai', 'Maharashtra', '400014', 19.0178, 72.8478),
(6, 'aarav.sharma@gmail.com', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-9811234567', 'Aarav Sharma', 'VOLUNTEER', 'ACTIVE', 'Defence Colony', 'New Delhi', 'Delhi', '110024', 28.5729, 77.2289),
(7, 'pooja.deshmukh@gmail.com', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-9769876543', 'Pooja Deshmukh', 'VOLUNTEER', 'ACTIVE', 'Shivaji Park, Dadar West', 'Mumbai', 'Maharashtra', '400028', 19.0269, 72.8373),
(8, 'admin@replate.org', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-8012345678', 'Replate Systems Admin', 'ADMIN', 'ACTIVE', 'Koramangala Incubator Hub', 'Bengaluru', 'Karnataka', '560095', 12.9345, 77.6190),
(26, 'gabalaxya108@gmail.com', '$2a$10$Sr3MUDeLyXSZsRpbcAg3suYfkyEkwemku9B7q/bnTzc99O4kGUZ82', '9997496746', 'laxya gaba', 'DONOR', 'ACTIVE', 'Chakarpur Main Road', 'Bengaluru', 'Karnataka', '560001', 12.9716, 77.5946),
(27, 'contact@vidyarthibhavan.in', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-8026677588', 'Vidyarthi Bhavan Heritage', 'DONOR', 'ACTIVE', 'Gandhi Bazaar, Basavanagudi', 'Bengaluru', 'Karnataka', '560004', 12.9427, 77.5724),
(28, 'feedingdelhi@careindia.org', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-1126543210', 'Care India Feeding Mission', 'NGO', 'ACTIVE', 'Green Park Extension', 'New Delhi', 'Delhi', '110016', 28.5584, 77.2023),
(29, 'suspended.volunteer@replate.org', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-9999000011', 'Suspended Rider', 'VOLUNTEER', 'SUSPENDED', 'South Extension', 'New Delhi', 'Delhi', '110049', 28.5684, 77.2189),
(30, 'vikram.patil@gmail.com', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-9845012345', 'Vikram Patil', 'VOLUNTEER', 'ACTIVE', 'Indiranagar 12th Main', 'Bengaluru', 'Karnataka', '560038', 12.9719, 77.6412);

SELECT setval('app_user_id_seq', 50, true);

-- 2. Donor Profiles
INSERT INTO donor_profile (user_id, organization_name, organization_type, fssai_license_no, operating_hours) VALUES
(1, 'Saravana Bhavan Foods Pvt Ltd', 'RESTAURANT', '11220334000123', '07:00 AM - 11:00 PM'),
(2, 'Haldirams Sweets & Catering', 'CATERER', '13318005000456', '09:00 AM - 10:30 PM'),
(3, 'Trident Nariman Point Banquets', 'HOTEL_BANQUET', '11516001000789', '24 Hours'),
(26, 'bhojan', 'RESTAURANT', '15467364563546', '10:00 AM - 11:00 PM'),
(27, 'Vidyarthi Bhavan Heritage', 'RESTAURANT', '11219332000345', '06:30 AM - 10:00 PM');

-- 3. NGO Profiles
INSERT INTO ngo_profile (user_id, ngo_name, darpan_id, registration_80g, daily_beneficiary_capacity, verification_status) VALUES
(4, 'Robin Hood Army (Bengaluru)', 'KA/2018/0192834', 'AAATR1234PF20214', 450, 'VERIFIED'),
(5, 'Roti Bank Mumbai Foundation', 'MH/2017/0088712', 'AABCR9876QF20191', 800, 'VERIFIED'),
(28, 'Care India Feeding Mission', 'DL/2019/0076543', 'AABCI1234EF20202', 600, 'VERIFIED');

-- 4. Volunteer Profiles
INSERT INTO volunteer_profile (user_id, vehicle_type, emergency_contact, total_deliveries) VALUES
(6, 'TWO_WHEELER', '+91-9811234560', 42),
(7, 'FOUR_WHEELER', '+91-9769876540', 68),
(29, 'TWO_WHEELER', '+91-9999000012', 0),
(30, 'AUTO_VAN', '+91-9845012345', 29);

-- 5. Food Donations (All fresh with dynamic future expiry relative to NOW())
INSERT INTO food_donation (id, donor_id, title, category, total_quantity, available_quantity, quantity_unit, dietary_type, storage_condition, preparation_time, expiry_time, pickup_notes, status, version) VALUES
(1, 1, 'Fresh South Indian Vegetable Pulao & Mixed Veg Kurma', 'COOKED_MEALS', 80.00, 40.00, 'MEAL_BOXES', 'PURE_VEG', 'HOT_INSULATED', NOW() - INTERVAL '1 hour', NOW() + INTERVAL '6 hours', 'Packed in food-grade thermal insulated containers. Collect from rear kitchen dispatch gate.', 'PARTIALLY_ALLOCATED', 1),
(2, 2, 'Assorted Tandoori Rotis, Naans & Dal Makhani', 'COOKED_MEALS', 120.00, 70.00, 'PORTIONS', 'PURE_VEG', 'HOT_INSULATED', NOW() - INTERVAL '90 minutes', NOW() + INTERVAL '5 hours', 'From afternoon corporate banquet hall. Sealed foil meal boxes.', 'PARTIALLY_ALLOCATED', 1),
(3, 3, 'Gourmet Shahi Paneer, Jeera Pulao & Gulab Jamun', 'COOKED_MEALS', 100.00, 40.00, 'MEAL_BOXES', 'PURE_VEG', 'HOT_INSULATED', NOW() - INTERVAL '2 hours', NOW() + INTERVAL '4 hours 30 minutes', 'Service entrance near Gate 3. Contact Banquet Chef Ramesh at kitchen.', 'PARTIALLY_ALLOCATED', 1),
(4, 27, 'Traditional Masala Dosa Batter & Sambar Packets', 'COOKED_MEALS', 60.00, 60.00, 'LITERS', 'PURE_VEG', 'REFRIGERATED', NOW() - INTERVAL '3 hours', NOW() + INTERVAL '8 hours', 'Cold storage dispatch section. Chilled temperature maintained at 4°C.', 'AVAILABLE', 0),
(5, 2, 'Freshly Prepared Motichoor Ladoos & Kaju Katli', 'DAIRY_SWEETS', 50.00, 50.00, 'KG', 'PURE_VEG', 'AMBIENT', NOW() - INTERVAL '4 hours', NOW() + INTERVAL '24 hours', 'In commercial airtight food-grade sweet boxes. Ready for immediate pickup.', 'AVAILABLE', 0),
(6, 3, 'Organic Farm Fresh Apples, Oranges & Bananas', 'RAW_PRODUCE', 90.00, 90.00, 'KG', 'VEGAN', 'AMBIENT', NOW() - INTERVAL '5 hours', NOW() + INTERVAL '36 hours', 'Direct from breakfast buffet storage. High grade fresh fruit crates.', 'AVAILABLE', 0),
(7, 1, 'Artisan Whole Wheat Sandwich Breads & Dinner Rolls', 'BREADS_ROTIS', 75.00, 75.00, 'KG', 'PURE_VEG', 'AMBIENT', NOW() - INTERVAL '2 hours', NOW() + INTERVAL '14 hours', 'Baked fresh this morning. Packed in hygienic poly bags of 1 kg each.', 'AVAILABLE', 0),
(8, 2, 'Mughlai Chicken Dum Biryani & Mirchi Ka Salan', 'COOKED_MEALS', 110.00, 110.00, 'PORTIONS', 'NON_VEG', 'HOT_INSULATED', NOW() - INTERVAL '1 hour', NOW() + INTERVAL '5 hours', 'Non-veg section. Prepared in pure ghee. Packed in 500g sealed takeaway containers.', 'AVAILABLE', 0),
(9, 26, 'Homestyle Dal Tadka, Jeera Rice & Phulkas', 'COOKED_MEALS', 50.00, 50.00, 'MEAL_BOXES', 'PURE_VEG', 'HOT_INSULATED', NOW() - INTERVAL '45 minutes', NOW() + INTERVAL '6 hours', 'Freshly packed homestyle lunch boxes from Bhojan Kitchen.', 'AVAILABLE', 0),
(10, 3, 'Hyderabadi Subz Biryani, Salan & Double Ka Meetha', 'COOKED_MEALS', 85.00, 85.00, 'MEAL_BOXES', 'PURE_VEG', 'HOT_INSULATED', NOW() - INTERVAL '90 minutes', NOW() + INTERVAL '4 hours', 'Banquet Hall B pickup dock. Contact steward on arrival.', 'AVAILABLE', 0),
(11, 27, 'Filter Coffee Decoction & Organic Sweet Buns', 'PACKAGED_GOODS', 40.00, 40.00, 'PORTIONS', 'PURE_VEG', 'AMBIENT', NOW() - INTERVAL '3 hours', NOW() + INTERVAL '18 hours', 'Packaged in sealed food-safe beverage flasks and bakery boxes.', 'AVAILABLE', 0),
(12, 1, 'South Indian Deluxe Thali Meal Boxes', 'COOKED_MEALS', 100.00, 100.00, 'MEAL_BOXES', 'PURE_VEG', 'HOT_INSULATED', NOW() - INTERVAL '1 hour', NOW() + INTERVAL '5 hours 30 minutes', 'Complete meals including sambar rice, curd rice, and dry vegetable poriyal.', 'AVAILABLE', 0);

SELECT setval('food_donation_id_seq', 50, true);

-- 6. Food Requests
INSERT INTO food_request (id, donation_id, ngo_id, requested_quantity, intended_beneficiaries, status, rejection_reason) VALUES
(1, 1, 4, 40.00, 'Distribution to 40 children and elderly at Indiranagar transit camp shelter.', 'APPROVED', NULL),
(2, 2, 28, 50.00, 'Dinner distribution for night shift sanitation workers in Connaught Place.', 'APPROVED', NULL),
(3, 3, 5, 60.00, 'Distribution to 60 pavement dwellers near Dadar and Parel stations.', 'APPROVED', NULL),
(4, 9, 4, 30.00, 'Evening meal distribution for daily wage migrant families in Koramangala slum pockets.', 'PENDING', NULL),
(5, 8, 28, 40.00, 'Food relief for construction worker families near Barakhamba metro site.', 'PENDING', NULL);

SELECT setval('food_request_id_seq', 50, true);

-- 7. Allocations (Committed inventory stock)
INSERT INTO allocation (id, request_id, donation_id, allocated_quantity, status, allocated_by) VALUES
(1, 1, 1, 40.00, 'IN_TRANSIT', 1),
(2, 2, 2, 50.00, 'AWAITING_PICKUP', 2),
(3, 3, 3, 60.00, 'AWAITING_PICKUP', 3);

SELECT setval('allocation_id_seq', 50, true);

-- 8. Delivery Tasks
-- Task 1: In Transit (Bengaluru) - Vikram Patil (Auto-Van, user 30)
-- Task 2: Accepted & Ready for Pickup (Delhi) - Aarav Sharma (Two-Wheeler, user 6)
-- Task 3: Assigned (Mumbai) - Pooja Deshmukh (Four-Wheeler, user 7)
INSERT INTO delivery_task (id, allocation_id, volunteer_id, status, pickup_otp, delivery_otp, assigned_at, accepted_at, picked_up_at, in_transit_at, delivered_at, recipient_name, recipient_phone, notes) VALUES
(1, 1, 30, 'IN_TRANSIT', '482910', '731940', NOW() - INTERVAL '45 minutes', NOW() - INTERVAL '40 minutes', NOW() - INTERVAL '15 minutes', NOW() - INTERVAL '12 minutes', NULL, 'Brother Thomas (Shelter Lead)', '+91-9876543201', 'Insulated thermal carriers inspected. Food temperature verified at 64°C. Vehicle in transit along Old Airport Road.'),
(2, 2, 6, 'ACCEPTED', '391024', '852190', NOW() - INTERVAL '25 minutes', NOW() - INTERVAL '15 minutes', NULL, NULL, NULL, 'Sunita Rao (Care India Supervisor)', '+91-9811234890', 'Volunteer arrived at Haldirams Connaught Place dispatch dock. Awaiting donor Kitchen Pickup PIN verification.'),
(3, 3, 7, 'ASSIGNED', '620183', '914820', NOW() - INTERVAL '10 minutes', NULL, NULL, NULL, NULL, 'Mahesh Shinde (Roti Bank Coordinator)', '+91-9820011999', 'Dispatch assignment notified to volunteer Pooja Deshmukh. Awaiting formal acceptance in mobile hub.');

SELECT setval('delivery_task_id_seq', 50, true);

-- 9. System Audit Log Entries
INSERT INTO audit_log (user_id, action, entity_name, entity_id, details, ip_address, created_at) VALUES
(1, 'DONATION_PUBLISHED', 'FoodDonation', 1, 'Published 80 MEAL_BOXES of Fresh South Indian Vegetable Pulao', '127.0.0.1', NOW() - INTERVAL '1 hour'),
(4, 'REQUEST_SUBMITTED', 'FoodRequest', 1, 'Robin Hood Army claimed 40 MEAL_BOXES', '127.0.0.1', NOW() - INTERVAL '55 minutes'),
(1, 'REQUEST_APPROVED', 'FoodRequest', 1, 'Donor approved claim for 40 boxes', '127.0.0.1', NOW() - INTERVAL '50 minutes'),
(1, 'ALLOCATION_COMMITTED', 'Allocation', 1, 'Committed 40 MEAL_BOXES under Pessimistic Lock', '127.0.0.1', NOW() - INTERVAL '45 minutes'),
(30, 'PICKUP_OTP_VERIFIED', 'DeliveryTask', 1, 'Kitchen Pickup PIN 482910 verified. Status advanced to IN_TRANSIT', '127.0.0.1', NOW() - INTERVAL '15 minutes');

