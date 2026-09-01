package com.braincampus.auth.service;
import com.braincampus.auth.dto.*;
import com.braincampus.auth.entity.*;
import com.braincampus.auth.repository.*;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.UnauthorizedException;
import com.braincampus.security.jwt.JwtService;
import com.braincampus.security.userDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.braincampus.common.enums.RoleType;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private final EmailService emailService;

    public LoginResult login(LoginRequest request) {

        User user = userRepository.findByEmailAndTenant_SchoolCode(
                        request.getEmail(),
                        request.getSchoolCode()
                )
                .orElseThrow(() -> new UnauthorizedException("Invalid email, school code or password"));

        if (!user.getEnabled()) {
            throw new UnauthorizedException("User account is disabled");
        }

        if (user.getAccountLocked()) {
            throw new UnauthorizedException("User account is locked");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new UnauthorizedException(
                    "Invalid email, school code or password"
            );
        }

        CustomUserDetails userDetails =
                new CustomUserDetails(user);

        String accessToken =
                jwtService.generateAccessToken(userDetails);

        String refreshToken =
                createRefreshToken(user);

        Set<String> permissions = user
                .getRole()
                .getPermissions()
                .stream()
                .map(permission ->
                        permission.getName().name())
                .collect(Collectors.toSet());

        LoginResponse response = LoginResponse.builder()
                .accessToken(accessToken)
                .userId(user.getId())
                .fullName(buildFullName(user))
                .email(user.getEmail())
                .schoolCode(user.getTenant().getSchoolCode())
                .role(user.getRole().getName().name())
                .permissions(permissions)
                .build();

        return new LoginResult(response, refreshToken);
    }

    private String createRefreshToken(User user) {

        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .expiryDate(
                        LocalDateTime.now().plusDays(7)
                )
                .revoked(false)
                .user(user)
                .build();

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    private String buildFullName(User user) {

        if (user.getLastName() == null ||
                user.getLastName().isBlank()) {

            return user.getFirstName();
        }

        return user.getFirstName()
                + " "
                + user.getLastName();
    }
    public RegisterResponse register(RegisterRequest request) {

        // 1. Check school code
        if (tenantRepository.findBySchoolCode(request.getSchoolCode()).isPresent()) {
            throw new DuplicateResourceException(
                    "School code already exists"
            );
        }

        // 2. Create Tenant
        Tenant tenant = new Tenant();

        tenant.setSchoolName(request.getSchoolName());
        tenant.setSchoolCode(request.getSchoolCode());
        tenant.setEmail(request.getEmail());
        tenant.setPhone(request.getPhone());
        tenant.setActive(true);

        tenant = tenantRepository.save(tenant);


        // 3. Create ADMIN role for this tenant
        Role adminRole = new Role();

        adminRole.setName(RoleType.ADMIN);
        adminRole.setDescription("School Administrator");
        adminRole.setTenant(tenant);

        // 4. Give ADMIN all currently available permissions
        Set<Permission> permissions =
                permissionRepository.findAll()
                        .stream()
                        .collect(Collectors.toSet());

        adminRole.setPermissions(permissions);

        adminRole = roleRepository.save(adminRole);


        // 5. Check whether email already exists for this school
        if (userRepository
                .findByEmailAndTenant_SchoolCode(
                        request.getEmail(),
                        request.getSchoolCode()
                )
                .isPresent()) {

            throw new DuplicateResourceException(
                    "Email already registered for this school"
            );
        }


        // 6. Create Admin User
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        // NEVER store plain password
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setAccountExpired(false);
        user.setCredentialsExpired(false);

        user.setTenant(tenant);
        user.setRole(adminRole);

        user = userRepository.save(user);


        // 7. Return response
        return RegisterResponse.builder()
                .userId(user.getId())
                .schoolName(tenant.getSchoolName())
                .schoolCode(tenant.getSchoolCode())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(adminRole.getName().name())
                .message("School and admin account registered successfully")
                .build();
    }
    public void resetPassword(
            ResetPasswordRequest request
    ) {

        User user = userRepository
                .findByEmailAndTenant_SchoolCode(
                        request.getEmail(),
                        request.getSchoolCode()
                )
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "Invalid reset request"
                        )
                );

        PasswordResetOtp resetOtp = passwordResetOtpRepository
                        .findTopByUserAndUsedFalseOrderByCreatedAtDesc(
                                user
                        )
                        .orElseThrow(() ->
                                new UnauthorizedException(
                                        "Invalid or expired OTP"
                                )
                        );

        if (resetOtp.getExpiryTime()
                .isBefore(LocalDateTime.now())) {

            throw new UnauthorizedException(
                    "OTP has expired"
            );
        }

        if (!resetOtp.getOtp()
                .equals(request.getOtp())) {

            throw new UnauthorizedException(
                    "Invalid OTP"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        resetOtp.setUsed(true);

        userRepository.save(user);
        passwordResetOtpRepository.save(resetOtp);
    }
    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository
                .findByEmailAndTenant_SchoolCode(
                        request.getEmail(),
                        request.getSchoolCode()
                )
                .orElse(null);

        /*
         * Don't reveal whether the account exists.
         */
        if (user == null) {
            return;
        }

        // Invalidate previous unused OTP
        passwordResetOtpRepository
                .findTopByUserAndUsedFalseOrderByCreatedAtDesc(user)
                .ifPresent(oldOtp -> {
                    oldOtp.setUsed(true);
                    passwordResetOtpRepository.save(oldOtp);
                });

        // Generate 6-digit OTP
        String otp = String.format(
                "%06d",
                new SecureRandom().nextInt(1_000_000)
        );

        PasswordResetOtp passwordResetOtp =
                PasswordResetOtp.builder()
                        .user(user)
                        .otp(otp)
                        .expiryTime(
                                LocalDateTime.now().plusMinutes(5)
                        )
                        .used(false)
                        .build();

        passwordResetOtpRepository.save(passwordResetOtp);

        emailService.sendPasswordResetOtp(
                user.getEmail(),
                otp
        );
    }
}
