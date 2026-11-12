-- REPLATE Test Seed Data for In-Memory H2
INSERT INTO app_user (id, email, password_hash, phone_number, full_name, role, status, address_line1, city, state, pincode, latitude, longitude, created_at, updated_at) VALUES
(1, 'contact@saravanabhavan.in', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-8025211234', 'Saravana Bhavan Foods Pvt Ltd', 'DONOR', 'ACTIVE', '100 Feet Road, Indiranagar', 'Bengaluru', 'Karnataka', '560038', 12.9784, 77.6408, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'events@haldiramsdelhi.com', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-1143529900', 'Haldirams Sweets & Catering', 'DONOR', 'ACTIVE', 'B-Block, Connaught Place', 'New Delhi', 'Delhi', '110001', 28.6328, 77.2197, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'banquets@tridentmumbai.com', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-2266324343', 'Trident Nariman Point Banquets', 'DONOR', 'ACTIVE', 'Nariman Point, Marine Drive', 'Mumbai', 'Maharashtra', '400021', 18.9274, 72.8211, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'foodrescue@robinhoodarmy.org', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-9876500123', 'Robin Hood Army (Bengaluru)', 'NGO', 'ACTIVE', 'Koramangala 4th Block', 'Bengaluru', 'Karnataka', '560034', 12.9352, 77.6245, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'relief@rotibankmumbai.org', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-9820011223', 'Roti Bank Mumbai Foundation', 'NGO', 'ACTIVE', 'Opposite Dadar Central Station', 'Mumbai', 'Maharashtra', '400014', 19.0178, 72.8478, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'aarav.sharma@gmail.com', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-9811234567', 'Aarav Sharma', 'VOLUNTEER', 'ACTIVE', 'Defence Colony', 'New Delhi', 'Delhi', '110024', 28.5729, 77.2289, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'pooja.deshmukh@gmail.com', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-9769876543', 'Pooja Deshmukh', 'VOLUNTEER', 'ACTIVE', 'Shivaji Park, Dadar West', 'Mumbai', 'Maharashtra', '400028', 19.0269, 72.8373, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'admin@replate.org', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-8012345678', 'Replate Systems Admin', 'ADMIN', 'ACTIVE', 'Koramangala Incubator Hub', 'Bengaluru', 'Karnataka', '560095', 12.9345, 77.6190, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(26, 'gabalaxya108@gmail.com', '$2a$10$Sr3MUDeLyXSZsRpbcAg3suYfkyEkwemku9B7q/bnTzc99O4kGUZ82', '9997496746', 'laxya gaba', 'DONOR', 'ACTIVE', 'Chakarpur Main Road', 'Bengaluru', 'Karnataka', '560001', 12.9716, 77.5946, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(27, 'contact@vidyarthibhavan.in', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-8026677588', 'Vidyarthi Bhavan Heritage', 'DONOR', 'ACTIVE', 'Gandhi Bazaar, Basavanagudi', 'Bengaluru', 'Karnataka', '560004', 12.9427, 77.5724, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(28, 'feedingdelhi@careindia.org', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-1126543210', 'Care India Feeding Mission', 'NGO', 'ACTIVE', 'Green Park Extension', 'New Delhi', 'Delhi', '110016', 28.5584, 77.2023, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(29, 'suspended.volunteer@replate.org', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-9999000011', 'Suspended Rider', 'VOLUNTEER', 'SUSPENDED', 'South Extension', 'New Delhi', 'Delhi', '110049', 28.5684, 77.2189, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(30, 'vikram.patil@gmail.com', '$2a$10$/DIyxQtL.6ii82a30k0p8uGq/Oj.smE4MU0Dc8vxi9gjFb/WeXbbS', '+91-9845012345', 'Vikram Patil', 'VOLUNTEER', 'ACTIVE', 'Indiranagar 12th Main', 'Bengaluru', 'Karnataka', '560038', 12.9719, 77.6412, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO donor_profile (user_id, organization_name, organization_type, fssai_license_no, operating_hours) VALUES
(1, 'Saravana Bhavan Foods Pvt Ltd', 'RESTAURANT', '11220334000123', '07:00 AM - 11:00 PM'),
(2, 'Haldirams Sweets & Catering', 'CATERER', '13318005000456', '09:00 AM - 10:30 PM'),
(3, 'Trident Nariman Point Banquets', 'HOTEL_BANQUET', '11516001000789', '24 Hours'),
(26, 'bhojan', 'RESTAURANT', '15467364563546', '10:00 AM - 11:00 PM'),
(27, 'Vidyarthi Bhavan Heritage', 'RESTAURANT', '11219332000345', '06:30 AM - 10:00 PM');

INSERT INTO ngo_profile (user_id, ngo_name, darpan_id, registration_80g, daily_beneficiary_capacity, verification_status) VALUES
(4, 'Robin Hood Army (Bengaluru)', 'KA/2018/0192834', 'AAATR1234PF20214', 450, 'VERIFIED'),
(5, 'Roti Bank Mumbai Foundation', 'MH/2017/0088712', 'AABCR9876QF20191', 800, 'VERIFIED'),
(28, 'Care India Feeding Mission', 'DL/2019/0076543', 'AABCI1234EF20202', 600, 'VERIFIED');

INSERT INTO volunteer_profile (user_id, vehicle_type, emergency_contact, total_deliveries) VALUES
(6, 'TWO_WHEELER', '+91-9811234560', 42),
(7, 'FOUR_WHEELER', '+91-9769876540', 68),
(29, 'TWO_WHEELER', '+91-9999000012', 0),
(30, 'AUTO_VAN', '+91-9845012345', 29);

INSERT INTO food_donation (id, donor_id, title, category, total_quantity, available_quantity, quantity_unit, dietary_type, storage_condition, preparation_time, expiry_time, pickup_notes, status, version, created_at, updated_at) VALUES
(1, 1, 'Fresh South Indian Vegetable Pulao & Mixed Veg Kurma', 'COOKED_MEALS', 80.00, 40.00, 'MEAL_BOXES', 'PURE_VEG', 'HOT_INSULATED', CURRENT_TIMESTAMP, DATEADD('HOUR', 6, CURRENT_TIMESTAMP), 'Packed in food-grade thermal insulated containers.', 'PARTIALLY_ALLOCATED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 'Assorted Tandoori Rotis, Naans & Dal Makhani', 'COOKED_MEALS', 120.00, 70.00, 'PORTIONS', 'PURE_VEG', 'HOT_INSULATED', CURRENT_TIMESTAMP, DATEADD('HOUR', 5, CURRENT_TIMESTAMP), 'From afternoon corporate banquet hall.', 'PARTIALLY_ALLOCATED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 'Gourmet Shahi Paneer, Jeera Pulao & Gulab Jamun', 'COOKED_MEALS', 100.00, 40.00, 'MEAL_BOXES', 'PURE_VEG', 'HOT_INSULATED', CURRENT_TIMESTAMP, DATEADD('HOUR', 5, CURRENT_TIMESTAMP), 'Service entrance near Gate 3.', 'PARTIALLY_ALLOCATED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 27, 'Traditional Masala Dosa Batter & Coconut Sambar Packets', 'COOKED_MEALS', 60.00, 60.00, 'LITERS', 'PURE_VEG', 'REFRIGERATED', CURRENT_TIMESTAMP, DATEADD('HOUR', 8, CURRENT_TIMESTAMP), 'Cold storage dispatch section.', 'AVAILABLE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO food_request (id, donation_id, ngo_id, requested_quantity, intended_beneficiaries, status, rejection_reason, created_at, updated_at) VALUES
(1, 1, 4, 40.00, 'Distribution to 40 children and elderly at Indiranagar transit camp shelter.', 'APPROVED', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 28, 50.00, 'Dinner distribution for night shift sanitation workers in Connaught Place.', 'APPROVED', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 5, 60.00, 'Distribution to 60 pavement dwellers near Dadar and Parel stations.', 'APPROVED', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO allocation (id, request_id, donation_id, allocated_quantity, status, allocated_by, created_at, updated_at) VALUES
(1, 1, 1, 40.00, 'IN_TRANSIT', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 2, 50.00, 'AWAITING_PICKUP', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 3, 60.00, 'AWAITING_PICKUP', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO delivery_task (id, allocation_id, volunteer_id, status, pickup_otp, delivery_otp, assigned_at, accepted_at, picked_up_at, in_transit_at, delivered_at, recipient_name, recipient_phone, notes, created_at, updated_at) VALUES
(1, 1, 30, 'IN_TRANSIT', '482910', '731940', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'Brother Thomas (Shelter Lead)', '+91-9876543201', 'Vehicle in transit along Old Airport Road.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 6, 'ACCEPTED', '391024', '852190', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, NULL, 'Sunita Rao (Care India Supervisor)', '+91-9811234890', 'Volunteer arrived at Haldirams Connaught Place.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 7, 'ASSIGNED', '620183', '914820', CURRENT_TIMESTAMP, NULL, NULL, NULL, NULL, 'Mahesh Shinde (Roti Bank Coordinator)', '+91-9820011999', 'Dispatch assignment notified to volunteer Pooja Deshmukh.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

ALTER TABLE app_user ALTER COLUMN id RESTART WITH 100;
ALTER TABLE food_donation ALTER COLUMN id RESTART WITH 100;
ALTER TABLE food_request ALTER COLUMN id RESTART WITH 100;
ALTER TABLE allocation ALTER COLUMN id RESTART WITH 100;
ALTER TABLE delivery_task ALTER COLUMN id RESTART WITH 100;
