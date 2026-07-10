package com.braincampus.security.service;
import com.braincampus.auth.entity.User;
import com.braincampus.auth.repository.UserRepository;
import com.braincampus.security.userDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("Use loadUserByUsernameAndSchoolCode() instead.");
    }

    public UserDetails loadUserByUsernameAndSchoolCode(String email, String schoolCode) {

        User user = userRepository
                .findByEmailAndTenant_SchoolCode(email, schoolCode)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Invalid credentials"));

        return new CustomUserDetails(user);
    }
}
