package net.tuuka.ecommerce.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.tuuka.ecommerce.model.user.AppUserRole;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class JwtResponse {
    private final String token;
    private final String type = "Bearer";
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Set<String> authorities;
    private final Date expiresAt;
}
