package com.example.demo.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.model.User;

public class UserDetailsImpl implements UserDetails {

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    // Constructor: takes our User entity and pulls out the needed fields
    public UserDetailsImpl(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        // Convert Role enum (e.g. CUSTOMER) → "ROLE_CUSTOMER" → GrantedAuthority
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    // Returns the roles/permissions this user has
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Returns the stored password (used by Spring Security to verify login)
    @Override
    public String getPassword() {
        return password;
    }

    // Returns the username (used by Spring Security to identify the user)
    @Override
    public String getUsername() {
        return username;
    }

    // Has the account expired? false = account is still valid
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Is the account locked? true = not locked, user can log in
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Have the credentials (password) expired? true = still valid
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Is the account active/enabled?
    @Override
    public boolean isEnabled() {
        return true;
    }
}
