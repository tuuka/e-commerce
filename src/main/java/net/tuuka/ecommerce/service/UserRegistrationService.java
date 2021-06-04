package net.tuuka.ecommerce.service;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.controller.AuthApiController;
import net.tuuka.ecommerce.controller.dto.SignUpRequest;
import net.tuuka.ecommerce.entity.AppUser;
import net.tuuka.ecommerce.entity.ConfirmationToken;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final AppUserService appUserService;
//    private final EmailValidator emailValidator = new EmailValidator();
    private final ConfirmationTokenService tokenService;
//    private final EmailSender emailSender;

    public String register(SignUpRequest signUpRequest) {
//
//        if (!emailValidator.isValid(signUpRequest.getEmail(), null))
//            throw new IllegalStateException("Email not valid.");

        ConfirmationToken confirmationToken;

        // check if user already exist and is not activated
        AppUser existingUser = appUserService.getAppUserByEmail(signUpRequest.getEmail());

        if (existingUser != null && !existingUser.isEnabled()) {
            tokenService.deleteTokensByUser(existingUser);
            confirmationToken = tokenService.getNewToken(existingUser);
            sendConfirmationLink(confirmationToken);
            throw new IllegalStateException(
                    String.format("User with email '%s' already registered, but not activated. " +
                            "Resent confirmation email.", signUpRequest.getEmail()));
        }

        confirmationToken = appUserService.singUpUser(
                new AppUser(
                        signUpRequest.getFirstName(),
                        signUpRequest.getLastName(),
                        signUpRequest.getEmail(),
                        signUpRequest.getPassword()));

        return sendConfirmationLink(confirmationToken);
    }

    @Transactional
    public void confirmToken(String token) {

        ConfirmationToken confirmationToken = tokenService.findToken(token);

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        // if token is expired
        if (expiredAt.isBefore(LocalDateTime.now())) {
            // delete expired token
            tokenService.deleteTokensByUser(confirmationToken.getAppUser());
            // getting new token
            confirmationToken = tokenService.getNewToken(confirmationToken.getAppUser());
            // send new token on user's email
            sendConfirmationLink(confirmationToken);
            throw new IllegalStateException(String.format(
                    "Confirmation token expired, a new one has been send on email '%s'",
                    confirmationToken.getAppUser().getEmail()));
        }

        tokenService.setConfirmedAt(token);
        appUserService.enableAppUser(confirmationToken.getAppUser().getEmail());

    }

    private String sendConfirmationLink(ConfirmationToken confirmationToken) {

        String link = linkTo(methodOn(AuthApiController.class).
                confirmEmail(confirmationToken.getToken())).toString();
//        emailSender.send(confirmationToken.getAppUser().getEmail(),
//                buildEmail(confirmationToken.getAppUser().getFirstName(), link
//                ));
        return "Confirmation email sent";

    }

    private String buildEmail(String name, String link) {
        return "<div style='float:left;background-color:#ffffff;padding:10px 30px 10px 30px;border:1px solid #f6f6f6'>" +
                "<div style='float:left;max-width:470px'>" +
                "<p style='line-height:21px;font-family:Helvetica,Verdana,Arial,sans-serif;font-size:12px'>" +
                "<strong style='line-height:21px;font-family:Helvetica,Verdana,Arial,sans-serif;font-size:18px'>Hello, " + name +
                ". Please confirm your registration.</strong>" +
                "</p>" +
                "<div style='line-height:21px;min-height:100px;font-family:Helvetica,Verdana,Arial,sans-serif;font-size:12px'>" +
                "<p style='line-height:21px;font-family:Helvetica,Verdana,Arial,sans-serif;font-size:12px'>Thanks for registering.</p>" +
                "<p style='line-height:21px;font-family:Helvetica,Verdana,Arial,sans-serif;font-size:12px'>Confirm your registration by clicking the link below:</p>" +
                "<p style='line-height:21px;font-family:Helvetica,Verdana,Arial,sans-serif;font-size:12px;margin-bottom:25px;background-color:#f7f9fc;padding:15px'>" +
                "<a style='font-size:2em; color:#4371ab;text-decoration:none' href='" + link + "'<strong>Confirm</strong></a>" +
                "</p>" +
                "<p style='line-height:21px;font-family:Helvetica,Verdana,Arial,sans-serif;font-size:12px'>Thank you.<br>Colorlib</p>" +
                "</div>" +
                "</div>";
    }

}
