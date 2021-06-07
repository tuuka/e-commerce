package net.tuuka.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.controller.dto.SimpleMessageResponse;
import net.tuuka.ecommerce.controller.dto.JwtResponse;
import net.tuuka.ecommerce.controller.dto.LoginRequest;
import net.tuuka.ecommerce.controller.dto.SignUpRequest;
import net.tuuka.ecommerce.security.JwtTokenService;
import net.tuuka.ecommerce.service.UserRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final UserRegistrationService registrationService;
    private final JwtTokenService jwtTokenService;


    @PostMapping("/signup")
    public ResponseEntity<SimpleMessageResponse> register(
            @RequestBody SignUpRequest signUpRequest,
            HttpServletRequest httpRequest) {
//        UrlUtils.buildRequestUrl(httpRequest);
//        String baseLink = UrlUtils.buildFullRequestUrl(httpRequest);
        String result = registrationService.register(signUpRequest);

        return ResponseEntity.ok(new SimpleMessageResponse(result));

    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        JwtResponse jwtResponse = jwtTokenService.generateToken(authentication);

        return ResponseEntity.ok(jwtResponse);
    }

    @GetMapping("/confirm")
    public ResponseEntity<SimpleMessageResponse> confirmEmail(@RequestParam("token") String token) {
        String result = "Email confirmed";
        try {
            registrationService.confirmToken(token);
        } catch (IllegalStateException e) {
            result = e.getMessage();
        }
        return ResponseEntity.ok(new SimpleMessageResponse(result));

    }

}