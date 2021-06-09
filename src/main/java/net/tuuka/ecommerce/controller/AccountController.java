package net.tuuka.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.controller.dto.AccountResponse;
import net.tuuka.ecommerce.entity.AppUser;
import net.tuuka.ecommerce.service.AppUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final AppUserService appUserService;

    @GetMapping()
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    ResponseEntity<AccountResponse> getAccountInfo(Authentication authentication) {
        AppUser loggedUser = appUserService.getAppUserByEmail(authentication.getName());
        if (loggedUser == null) throw new UsernameNotFoundException("User does not exist");
        return ResponseEntity.ok(new AccountResponse(loggedUser));
    }
}
