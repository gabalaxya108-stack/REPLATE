package com.replate.controller;

import com.replate.dto.request.LoginRequestDTO;
import com.replate.dto.request.RegisterRequestDTO;
import com.replate.dto.response.ApiResponse;
import com.replate.dto.response.AuthResponseDTO;
import com.replate.dto.response.UserSummaryDTO;
import com.replate.security.UserPrincipal;
import com.replate.service.auth.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = authenticationService.register(request);
        return new ResponseEntity<>(
                ApiResponse.success(response, "User registered successfully. Authentication token issued."),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authenticationService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserSummaryDTO>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return new ResponseEntity<>(
                    ApiResponse.error("Unauthorized: Authentication required to access user profile."),
                    HttpStatus.UNAUTHORIZED
            );
        }
        UserSummaryDTO summary = authenticationService.getCurrentUser(principal);
        return ResponseEntity.ok(ApiResponse.success(summary, "User profile retrieved successfully"));
    }
}
