package net.tuuka.ecommerce.service;

import net.tuuka.ecommerce.dao.ConfirmationTokenRepository;
import net.tuuka.ecommerce.entity.AppUser;
import net.tuuka.ecommerce.entity.ConfirmationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final int tokenTimeToExpire;

    public ConfirmationTokenService(ConfirmationTokenRepository repository,
                                    @Value("${app.security.token_expires_time_min}") int tokenTimeToExpire) {
        this.confirmationTokenRepository = repository;
        this.tokenTimeToExpire = tokenTimeToExpire;
    }

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public ConfirmationToken findToken(String token) {
        return confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Confirmation token not found"));
    }

    public void setConfirmedAt(String token) {
        ConfirmationToken confirmationToken = findToken(token);
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmationToken);
    }

    public void deleteTokensByUser(AppUser appUser) {
        List<ConfirmationToken> persistedTokens = confirmationTokenRepository.findByAppUser(appUser);
        if (persistedTokens.size() > 0) confirmationTokenRepository.deleteAll(persistedTokens);
    }

    public ConfirmationToken getNewToken(AppUser appUser) {

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(this.tokenTimeToExpire),
                null,
                appUser
        );
        this.saveConfirmationToken(confirmationToken);
        return confirmationToken;
    }
}
