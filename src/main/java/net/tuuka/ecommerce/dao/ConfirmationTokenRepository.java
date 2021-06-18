package net.tuuka.ecommerce.dao;

import net.tuuka.ecommerce.model.user.AppUser;
import net.tuuka.ecommerce.model.user.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);

    List<ConfirmationToken> findByAppUser(AppUser appUser);
}
