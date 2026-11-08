package com.replate.service.auth;

import com.replate.dto.request.LoginRequestDTO;
import com.replate.dto.request.RegisterRequestDTO;
import com.replate.dto.response.AuthResponseDTO;
import com.replate.dto.response.UserSummaryDTO;
import com.replate.security.UserPrincipal;

/**
 * Service contract for identity and authentication management.
 */
public interface AuthenticationService {

    AuthResponseDTO register(RegisterRequestDTO request);

    AuthResponseDTO login(LoginRequestDTO request);

    UserSummaryDTO getCurrentUser(UserPrincipal principal);
}
