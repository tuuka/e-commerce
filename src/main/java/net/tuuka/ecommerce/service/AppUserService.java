package net.tuuka.ecommerce.service;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.dao.AppUserRepository;
import net.tuuka.ecommerce.entity.AppUser;
import net.tuuka.ecommerce.entity.AppUserAuthority;
import net.tuuka.ecommerce.entity.AppUserRole;
import net.tuuka.ecommerce.entity.ConfirmationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_WITH_EMAIL_NOT_FOUND = "User with email '%s' not found.";
    private final static String USER_WITH_ID_NOT_FOUND = "User with id '%d' not found.";
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser persistedAppUser = getAppUserByEmail(email);
        if (persistedAppUser == null) throw
                new UsernameNotFoundException(
                        String.format(USER_WITH_EMAIL_NOT_FOUND, email));
        return persistedAppUser;
    }

    @Transactional
    public ConfirmationToken singUpUser(AppUser appUser) {

        AppUser persistedAppUser = getAppUserByEmail(appUser.getEmail());

        if (persistedAppUser != null) {
            if (persistedAppUser.isEnabled())
                throw new IllegalStateException("email '" + persistedAppUser.getEmail()
                        + "' already taken");
            else {
                // delete all existed confirmation tokens for user
                confirmationTokenService.deleteTokensByUser(persistedAppUser);

                // if user exists but is not confirmed dont create new user
                appUser = persistedAppUser;
            }
        } else {
            // It is new AppUser...
            String encodedPassword = passwordEncoder.encode(appUser.getPassword());
            appUser.setPassword(encodedPassword);
            appUserRepository.save(appUser);
        }

        return confirmationTokenService.getNewToken(appUser);
    }

    @Transactional
    public void enableAppUser(String email) {

        AppUser appUser = getAppUserByEmail(email);
        if (appUser == null) throw
                new IllegalStateException(String.format(USER_WITH_EMAIL_NOT_FOUND, email));
        appUser.setEnabled(true);

        // delete confirmation token (if needed)
        confirmationTokenService.deleteTokensByUser(appUser);
        appUserRepository.save(appUser);
    }

    @Transactional
    public void updateAppUserAuthorities(long userId, String[] roles) {

        AppUser persistedUser = getAppUserById(userId);

        persistedUser.setUserAuthorities(Stream.of(roles)
                .map(r -> new AppUserAuthority(AppUserRole.valueOf("ROLE_" + r)))
                .collect(Collectors.toSet()));
        appUserRepository.save(persistedUser);
    }

    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }

    @Transactional
    public void deleteAppUserById(long userId) {
        confirmationTokenService.deleteTokensByUser(getAppUserById(userId));
        appUserRepository.deleteById(userId);
    }

    private AppUser getAppUserById(long userId) {
        return appUserRepository.findById(userId).orElseThrow(() ->
                new IllegalStateException(String.format(USER_WITH_ID_NOT_FOUND, userId)));
    }

    public AppUser getAppUserByEmail(String email) {
        return appUserRepository.findByEmail(email).orElse(null);
    }

}
