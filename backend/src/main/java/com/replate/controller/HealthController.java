package com.replate.controller;

import com.replate.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ApiResponse<Map<String, Object>> getHealth() {
        return ApiResponse.success(
                Map.of(
                        "system", "REPLATE — Food Waste Donation & Redistribution System",
                        "status", "UP",
                        "academicCourse", "Object-Oriented Programming Using Java",
                        "version", "1.0.0-SNAPSHOT",
                        "region", "India"
                ),
                "System is fully operational"
        );
    }
}

