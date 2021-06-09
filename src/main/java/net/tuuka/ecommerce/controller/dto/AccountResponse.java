package net.tuuka.ecommerce.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.entity.AppUser;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AccountResponse {

    @JsonIgnore
    private final AppUser appUser;

    public String getFirstName() {
        return appUser.getFirstName();
    }

    public String getLastName() {
        return appUser.getLastName();
    }

    public String getEmail() {
        return appUser.getEmail();
    }

    public Set<String> getAuthorities() {
        return appUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }
}
