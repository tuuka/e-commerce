package net.tuuka.ecommerce.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class ErrorRepresentation {
    private final String error;
}
