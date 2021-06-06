package net.tuuka.ecommerce.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
@RequiredArgsConstructor
public class JwtResponse {
    private final String token;
    private final String type = "Bearer";

    @Value("${app.security.token_expires_time_min}")
    private Integer expiresInMin;
}
