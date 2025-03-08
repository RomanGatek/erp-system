package cz.syntaxbro.erpsystem.security.services;

import cz.syntaxbro.erpsystem.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

    public User getUser() {
        return user;
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
    public boolean isEnabled() {
        return user.isActive();
    }

    // unimplemented
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // unimplemented
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // unimplemented
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
