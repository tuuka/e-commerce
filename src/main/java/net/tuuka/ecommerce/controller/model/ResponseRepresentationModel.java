package net.tuuka.ecommerce.controller.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class ResponseRepresentationModel {
    private final String error;
}
