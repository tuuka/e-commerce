package net.tuuka.ecommerce.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
public class JwtResponse {
    private final String token;
    private final String type = "Bearer";
    private final String username;
    private final Date expiresAt;
}
