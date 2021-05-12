package net.tuuka.ecommerce.exception.aspect;

import net.tuuka.ecommerce.controller.v1.ProductCategoryRestController;
import net.tuuka.ecommerce.controller.v1.ProductRestController;
import net.tuuka.ecommerce.controller.model.ResponseRepresentationModel;
import net.tuuka.ecommerce.exception.ProductCategoryNotEmptyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(assignableTypes = {
        ProductRestController.class,
        ProductCategoryRestController.class
})
public class ProductControllerAdvice {

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    ResponseRepresentationModel productNotFoundException(ProductCategoryNotEmptyException ex) {
        return new ResponseRepresentationModel(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseRepresentationModel productNotFoundException(IllegalStateException ex) {
        return new ResponseRepresentationModel(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseRepresentationModel productNotFoundException(RuntimeException ex) {
        return new ResponseRepresentationModel(ex.getMessage());
    }
}
