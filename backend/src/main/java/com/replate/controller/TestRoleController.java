package com.replate.controller;

import com.replate.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRoleController {

    @GetMapping("/api/v1/admin/ping")
    public ResponseEntity<ApiResponse<String>> adminOnlyPing() {
        return ResponseEntity.ok(ApiResponse.success("ADMIN authorization verified"));
    }

    @GetMapping("/api/v1/donors/ping")
    public ResponseEntity<ApiResponse<String>> donorOnlyPing() {
        return ResponseEntity.ok(ApiResponse.success("DONOR authorization verified"));
    }
}

