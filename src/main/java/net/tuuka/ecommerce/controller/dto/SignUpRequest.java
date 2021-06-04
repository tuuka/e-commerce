package net.tuuka.ecommerce.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank
    @Size(min=3, max=30)
    private String firstName;

    @NotBlank
    @Size(min=3, max=30)
    private String lastName;

    @NotBlank
    @Size(min=3, max=30)
    private String password;

    @NotBlank
    @Size(min=3, max=30)
    @Email
    private String email;
}
