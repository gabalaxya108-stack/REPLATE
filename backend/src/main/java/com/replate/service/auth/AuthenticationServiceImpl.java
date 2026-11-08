package com.replate.service.auth;

import com.replate.domain.entity.*;
import com.replate.domain.enums.UserRole;
import com.replate.dto.request.LoginRequestDTO;
import com.replate.dto.request.RegisterRequestDTO;
import com.replate.dto.response.AuthResponseDTO;
import com.replate.dto.response.UserSummaryDTO;
import com.replate.exception.EmailAlreadyExistsException;
import com.replate.exception.ReplateException;
import com.replate.exception.ResourceNotFoundException;
import com.replate.exception.UnauthorizedRoleException;
import com.replate.repository.*;
import com.replate.security.JwtTokenProvider;
import com.replate.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final NgoProfileRepository ngoProfileRepository;
    private final VolunteerProfileRepository volunteerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final long jwtExpirationInMs;

    public AuthenticationServiceImpl(
            UserRepository userRepository,
            DonorProfileRepository donorProfileRepository,
            NgoProfileRepository ngoProfileRepository,
            VolunteerProfileRepository volunteerProfileRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            @Value("${jwt.expiration-ms:86400000}") long jwtExpirationInMs) {
        this.userRepository = userRepository;
        this.donorProfileRepository = donorProfileRepository;
        this.ngoProfileRepository = ngoProfileRepository;
        this.volunteerProfileRepository = volunteerProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        // Rule 1: Public registration cannot create ADMIN users
        if (request.getRole() == UserRole.ADMIN) {
            throw new UnauthorizedRoleException("Registration with ADMIN role is strictly prohibited through public channels.");
        }

        // Rule 2: Email uniqueness check
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new EmailAlreadyExistsException(request.getEmail().trim().toLowerCase());
        }

        // Rule 3: Construct BaseUser with Encapsulation & Password Hashing
        BaseUser user = new BaseUser();
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName().trim());
        user.setPhoneNumber(request.getPhoneNumber().trim());
        user.setRole(request.getRole());
        user.setStatus("ACTIVE");

        // Embedded Location Coordinate (Composition)
        LocationCoordinate location = new LocationCoordinate(
                request.getAddressLine1().trim(),
                request.getCity().trim(),
                request.getState().trim(),
                request.getPincode().trim(),
                request.getLatitude(),
                request.getLongitude()
        );
        user.setLocation(location);

        BaseUser savedUser = userRepository.saveAndFlush(user);

        // Rule 4: Create specialized role profiles
        switch (request.getRole()) {
            case DONOR -> createDonorProfile(savedUser, request);
            case NGO -> createNgoProfile(savedUser, request);
            case VOLUNTEER -> createVolunteerProfile(savedUser, request);
            default -> throw new ReplateException("Unsupported role: " + request.getRole());
        }

        // Issue JWT token
        String token = tokenProvider.generateTokenFromUsername(
                savedUser.getEmail(), savedUser.getId(), savedUser.getRole().name());

        return new AuthResponseDTO(
                token,
                jwtExpirationInMs,
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getLocation() != null ? savedUser.getLocation().getCity() : ""
        );
    }

    private void createDonorProfile(BaseUser user, RegisterRequestDTO request) {
        if (request.getOrganizationName() == null || request.getOrganizationName().isBlank()) {
            throw new ReplateException("Establishment / Organization name is required for Donors.");
        }
        if (request.getFssaiLicenseNo() == null || request.getFssaiLicenseNo().isBlank()) {
            throw new ReplateException("FSSAI License Number is mandatory for food donation compliance.");
        }
        if (donorProfileRepository.existsByFssaiLicenseNo(request.getFssaiLicenseNo().trim())) {
            throw new ReplateException("FSSAI License number is already registered.");
        }

        DonorProfile profile = new DonorProfile();
        profile.setUser(user);
        profile.setUserId(user.getId());
        profile.setOrganizationName(request.getOrganizationName().trim());
        profile.setOrganizationType(request.getOrganizationType() != null ? request.getOrganizationType() : "RESTAURANT");
        profile.setFssaiLicenseNo(request.getFssaiLicenseNo().trim());
        profile.setOperatingHours(request.getOperatingHours());
        donorProfileRepository.save(profile);
    }

    private void createNgoProfile(BaseUser user, RegisterRequestDTO request) {
        if (request.getNgoName() == null || request.getNgoName().isBlank()) {
            throw new ReplateException("NGO organization title is required.");
        }
        if (request.getDarpanId() != null && !request.getDarpanId().isBlank()) {
            if (ngoProfileRepository.existsByDarpanId(request.getDarpanId().trim())) {
                throw new ReplateException("NITI Aayog Darpan ID is already registered.");
            }
        }

        NgoProfile profile = new NgoProfile();
        profile.setUser(user);
        profile.setUserId(user.getId());
        profile.setNgoName(request.getNgoName().trim());
        profile.setDarpanId(request.getDarpanId() != null ? request.getDarpanId().trim() : null);
        profile.setRegistration80g(request.getRegistration80g());
        profile.setDailyBeneficiaryCapacity(
                request.getDailyBeneficiaryCapacity() != null && request.getDailyBeneficiaryCapacity() > 0
                        ? request.getDailyBeneficiaryCapacity()
                        : 50
        );
        profile.setVerificationStatus("VERIFIED"); // Default active for verified network
        ngoProfileRepository.save(profile);
    }

    private void createVolunteerProfile(BaseUser user, RegisterRequestDTO request) {
        String vehicle = request.getVehicleType() != null && !request.getVehicleType().isBlank()
                ? request.getVehicleType()
                : "TWO_WHEELER";
        String emergency = request.getEmergencyContact() != null && !request.getEmergencyContact().isBlank()
                ? request.getEmergencyContact().trim()
                : user.getPhoneNumber();

        VolunteerProfile profile = new VolunteerProfile();
        profile.setUser(user);
        profile.setUserId(user.getId());
        profile.setVehicleType(vehicle);
        profile.setEmergencyContact(emergency);
        profile.setTotalDeliveries(0);
        volunteerProfileRepository.save(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().trim().toLowerCase(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String token = tokenProvider.generateToken(authentication);

        BaseUser user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        return new AuthResponseDTO(
                token,
                jwtExpirationInMs,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getLocation() != null ? user.getLocation().getCity() : ""
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserSummaryDTO getCurrentUser(UserPrincipal principal) {
        BaseUser user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));

        UserSummaryDTO dto = new UserSummaryDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());

        if (user.getLocation() != null) {
            dto.setAddressLine1(user.getLocation().getAddressLine1());
            dto.setCity(user.getLocation().getCity());
            dto.setState(user.getLocation().getState());
            dto.setPincode(user.getLocation().getPincode());
        }

        Map<String, Object> details = new HashMap<>();
        switch (user.getRole()) {
            case DONOR -> donorProfileRepository.findById(user.getId()).ifPresent(dp -> {
                details.put("organizationName", dp.getOrganizationName());
                details.put("organizationType", dp.getOrganizationType());
                details.put("fssaiLicenseNo", dp.getFssaiLicenseNo());
                details.put("operatingHours", dp.getOperatingHours());
            });
            case NGO -> ngoProfileRepository.findById(user.getId()).ifPresent(np -> {
                details.put("ngoName", np.getNgoName());
                details.put("darpanId", np.getDarpanId());
                details.put("registration80g", np.getRegistration80g());
                details.put("dailyBeneficiaryCapacity", np.getDailyBeneficiaryCapacity());
                details.put("verificationStatus", np.getVerificationStatus());
            });
            case VOLUNTEER -> volunteerProfileRepository.findById(user.getId()).ifPresent(vp -> {
                details.put("vehicleType", vp.getVehicleType());
                details.put("emergencyContact", vp.getEmergencyContact());
                details.put("totalDeliveries", vp.getTotalDeliveries());
            });
            case ADMIN -> details.put("administrativeScope", "PLATFORM_SUPERUSER");
        }
        dto.setProfileDetails(details);

        return dto;
    }
}
