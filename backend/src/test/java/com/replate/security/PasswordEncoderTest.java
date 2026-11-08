package com.replate.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordEncoderTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void testPasswordEncodingAndMatching() {
        String rawPassword = "Password@123";
        String encoded = encoder.encode(rawPassword);

        System.out.println("Generated BCrypt Hash for 'Password@123': " + encoded);

        assertTrue(encoder.matches(rawPassword, encoded), "Encoded password should match raw password");
    }
}
