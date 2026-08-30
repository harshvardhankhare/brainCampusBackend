package com.braincampus.auth.service;
import com.braincampus.auth.dto.*;
import com.braincampus.auth.entity.RefreshToken;
import com.braincampus.auth.entity.User;
import com.braincampus.auth.repository.RefreshTokenRepository;
import com.braincampus.auth.repository.UserRepository;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.UnauthorizedException;
import com.braincampus.security.jwt.JwtService;
import com.braincampus.security.userDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.braincampus.auth.entity.Permission;
import com.braincampus.auth.entity.Role;
import com.braincampus.auth.entity.Tenant;
import com.braincampus.auth.repository.PermissionRepository;
import com.braincampus.auth.repository.RoleRepository;
import com.braincampus.auth.repository.TenantRepository;
import com.braincampus.common.enums.PermissionType;
import com.braincampus.common.enums.RoleType;
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

    public LoginResult login(LoginRequest request) {

        User user = userRepository
                .findByEmailAndTenant_SchoolCode(
                        request.getEmail(),
                        request.getSchoolCode()
                )
                .orElseThrow(() ->
                        new UnauthorizedException(
                                "Invalid email, school code or password"
                        ));

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
}
