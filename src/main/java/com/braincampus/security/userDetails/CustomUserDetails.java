package com.braincampus.security.userDetails;
import com.braincampus.auth.entity.Permission;
import com.braincampus.auth.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<GrantedAuthority> authorities = new HashSet<>();

        // Role
        authorities.add(
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().getName().name()
                )
        );

        // Permissions
        for (Permission permission : user.getRole().getPermissions()) {
            authorities.add(
                    new SimpleGrantedAuthority(
                            permission.getName().name()
                    )
            );
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !user.getAccountExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.getAccountLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !user.getCredentialsExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }

    public Long getUserId() {
        return user.getId();
    }

    public Long getTenantId() {
        return user.getTenant().getId();
    }

    public String getSchoolCode() {
        return user.getTenant().getSchoolCode();
    }
}
