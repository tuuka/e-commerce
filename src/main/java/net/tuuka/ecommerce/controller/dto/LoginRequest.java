package net.tuuka.ecommerce.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class LoginRequest {

    private String username;
    private String password;
}
