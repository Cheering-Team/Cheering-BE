package com.cheering.auth.jwt;

import com.cheering.auth.constant.Role;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class LoginUserAuthentication implements Authentication {

    private final String id;
    private final Collection<? extends GrantedAuthority> roles;
    private boolean isAuthenticated;

    public LoginUserAuthentication(String id, Collection<? extends GrantedAuthority> roles) {
        this.id = id;
        this.roles = roles;
        this.isAuthenticated = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (isAuthenticated) {
            return roles;
        }
        return Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_USER.name()));
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        if (id != null && isAuthenticated) {
            return id;
        }
        return null;
    }
}
