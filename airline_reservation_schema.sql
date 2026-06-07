-- airline_reservation_schema.sql
-- Run this script in MySQL Workbench

CREATE DATABASE IF NOT EXISTS airline_reservation_pk;
USE airline_reservation_pk;

-- Users table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15),
    role ENUM('passenger', 'admin') DEFAULT 'passenger',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Flights table
CREATE TABLE flights (
    flight_id INT PRIMARY KEY AUTO_INCREMENT,
    flight_number VARCHAR(10) UNIQUE NOT NULL,
    airline VARCHAR(50) NOT NULL,
    source VARCHAR(50) NOT NULL,
    destination VARCHAR(50) NOT NULL,
    departure_date DATE NOT NULL,
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    total_seats INT NOT NULL,
    available_seats INT NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    flight_type ENUM('domestic', 'international') DEFAULT 'domestic',
    status ENUM('active', 'cancelled', 'completed') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bookings table
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_reference VARCHAR(20) UNIQUE NOT NULL,
    user_id INT NOT NULL,
    flight_id INT NOT NULL,
    seat_numbers VARCHAR(100) NOT NULL,
    passenger_count INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    booking_status ENUM('confirmed', 'cancelled', 'pending') DEFAULT 'confirmed',
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flights(flight_id)
);

-- Payments table
CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50),
    payment_status ENUM('success', 'failed', 'refunded') DEFAULT 'success',
    transaction_id VARCHAR(100) UNIQUE,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
);

-- Insert admin user (password: admin123)
INSERT INTO users (username, password, email, full_name, phone_number, role) 
VALUES ('admin', 'admin123', 'admin@flyjinnah.com', 'System Administrator', '03001234567', 'admin');

-- Insert sample Pakistani flights (Fly Jinnah, Airblue, PIA)
INSERT INTO flights (flight_number, airline, source, destination, departure_date, departure_time, arrival_time, total_seats, available_seats, base_price, flight_type) VALUES
-- Domestic Flights
('9P-741', 'Fly Jinnah', 'Karachi (KHI)', 'Islamabad (ISB)', CURDATE() + 1, '06:00:00', '07:45:00', 180, 180, 15999.00, 'domestic'),
('9P-742', 'Fly Jinnah', 'Islamabad (ISB)', 'Karachi (KHI)', CURDATE() + 1, '08:30:00', '10:15:00', 180, 180, 15999.00, 'domestic'),
('PA-201', 'PIA', 'Lahore (LHE)', 'Karachi (KHI)', CURDATE() + 1, '07:00:00', '08:45:00', 200, 200, 18999.00, 'domestic'),
('PA-202', 'PIA', 'Karachi (KHI)', 'Lahore (LHE)', CURDATE() + 1, '14:00:00', '15:45:00', 200, 200, 18999.00, 'domestic'),
('ED-301', 'Airblue', 'Islamabad (ISB)', 'Lahore (LHE)', CURDATE() + 1, '09:00:00', '10:00:00', 160, 160, 12999.00, 'domestic'),
('9P-743', 'Fly Jinnah', 'Karachi (KHI)', 'Multan (MUX)', CURDATE() + 2, '11:00:00', '12:15:00', 150, 150, 13999.00, 'domestic'),
('PA-203', 'PIA', 'Islamabad (ISB)', 'Peshawar (PEW)', CURDATE() + 2, '16:00:00', '16:45:00', 120, 120, 9999.00, 'domestic'),
('ED-302', 'Airblue', 'Karachi (KHI)', 'Islamabad (ISB)', CURDATE() + 2, '18:00:00', '19:45:00', 180, 180, 16999.00, 'domestic'),

-- International Flights
('9P-801', 'Fly Jinnah', 'Karachi (KHI)', 'Dubai (DXB)', CURDATE() + 3, '20:00:00', '22:30:00', 280, 280, 35000.00, 'international'),
('9P-802', 'Fly Jinnah', 'Dubai (DXB)', 'Karachi (KHI)', CURDATE() + 3, '23:30:00', '04:00:00', 280, 280, 35000.00, 'international'),
('PA-701', 'PIA', 'Lahore (LHE)', 'London (LHR)', CURDATE() + 4, '09:00:00', '16:30:00', 350, 350, 125000.00, 'international'),
('PA-702', 'PIA', 'London (LHR)', 'Lahore (LHE)', CURDATE() + 5, '18:00:00', '10:30:00', 350, 350, 125000.00, 'international'),
('ED-501', 'Airblue', 'Islamabad (ISB)', 'Jeddah (JED)', CURDATE() + 3, '22:00:00', '02:00:00', 300, 300, 85000.00, 'international');

SELECT * FROM flights;