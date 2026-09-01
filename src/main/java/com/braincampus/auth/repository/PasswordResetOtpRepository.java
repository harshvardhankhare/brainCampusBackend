package com.braincampus.auth.repository;
import com.braincampus.auth.entity.PasswordResetOtp;
import com.braincampus.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetOtpRepository
        extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findTopByUserAndUsedFalseOrderByCreatedAtDesc(
            User user
    );
}