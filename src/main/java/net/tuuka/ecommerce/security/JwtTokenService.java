package net.tuuka.ecommerce.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import net.tuuka.ecommerce.entity.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenService {

    @Value("${app.security.token_expires_time_min}")
    private Integer TOKEN_EXPIRES_MIN;

    private final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(Authentication authentication) {

        String id = UUID.randomUUID().toString().replace("-", "");
        AppUser user = (AppUser) authentication.getPrincipal();
        Date now = new Date();
        Date exp = Date.from(LocalDateTime.now().plusMinutes(TOKEN_EXPIRES_MIN)
                .atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setId(id)
                .setSubject((user.getUsername()))
                .setIssuedAt(now)
                .setNotBefore(now)
                .setExpiration(exp)
                .signWith(KEY)
                .compact();

    }

    public boolean validateJwtToken(String authToken) {

        try {
            Jwts.parserBuilder().setSigningKey(KEY).build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature -> Message: {} ", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token -> Message: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token -> Message: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token -> Message: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty -> Message: {}", e.getMessage());
        }

        return false;
    }

    public String getUserNameFromJwtToken(String authToken) {
        return Jwts.parserBuilder().setSigningKey(KEY).build()
                .parseClaimsJws(authToken).getBody().getSubject();
    }

}
