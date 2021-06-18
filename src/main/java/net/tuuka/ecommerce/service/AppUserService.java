package net.tuuka.ecommerce.service;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.dao.AppUserRepository;
import net.tuuka.ecommerce.model.user.AppUser;
import net.tuuka.ecommerce.model.user.ConfirmationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

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
            this.saveAppUser(appUser);

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

    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }

    @Transactional
    public void deleteAppUserById(long userId) {
        confirmationTokenService.deleteTokensByUser(getAppUserById(userId));
        appUserRepository.deleteById(userId);
    }

    public AppUser getAppUserById(long userId) {
        return appUserRepository.findById(userId).orElseThrow(() ->
                new IllegalStateException(String.format(USER_WITH_ID_NOT_FOUND, userId)));
    }

    public AppUser getAppUserByEmail(String email) {
        return appUserRepository.findByEmail(email).orElse(null);
    }

    public AppUser saveAppUser(AppUser appUser) {

        if (getAppUserByEmail(appUser.getEmail()) != null)
            throw new IllegalStateException("User with email: '" + appUser.getEmail() + "' already exist!");
        String encodedPassword = passwordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        return appUserRepository.save(appUser);

    }

    public AppUser updateAppUser(AppUser appUser) {

        AppUser existingUser = getAppUserById(appUser.getId());
        appUser.setPassword(existingUser.getPassword());

        return appUserRepository.save(appUser);

    }

    @Transactional
    public void updateAppUserAuthorities(long userId, String[] roles) {

        AppUser persistedUser = getAppUserById(userId);

        persistedUser.setAuthorities(Arrays.asList(roles));
        appUserRepository.save(persistedUser);
    }

}
