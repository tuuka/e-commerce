package net.tuuka.ecommerce.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.entity.AppUser;
import net.tuuka.ecommerce.entity.AppUserAuthority;

import java.util.Set;

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

    public Set<AppUserAuthority> getUserAuthorities() {
        return appUser.getUserAuthorities();
    }
}
