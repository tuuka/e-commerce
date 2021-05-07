package net.tuuka.ecommerce.exception.aspect;

import net.tuuka.ecommerce.controller.ProductRestController;
import net.tuuka.ecommerce.controller.model.ResponseRepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(assignableTypes = {ProductRestController.class})
public class ProductControllerAdvice {

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseRepresentationModel productNotFoundException(RuntimeException ex) {
        return new ResponseRepresentationModel(ex.getMessage());
    }

}
