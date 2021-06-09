package net.tuuka.ecommerce.exception.aspect;

import net.tuuka.ecommerce.controller.ProductCategoryRestController;
import net.tuuka.ecommerce.controller.ProductRestController;
import net.tuuka.ecommerce.controller.dto.SimpleMessageResponse;
import net.tuuka.ecommerce.exception.ProductCategoryNotEmptyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice(assignableTypes = {
        ProductRestController.class,
        ProductCategoryRestController.class
})
public class ProductControllerAdvice {

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    SimpleMessageResponse productNotFoundException(ProductCategoryNotEmptyException ex) {
        return new SimpleMessageResponse(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    SimpleMessageResponse productNotFoundException(IllegalStateException ex) {
        return new SimpleMessageResponse(ex.getMessage());
    }

    @ExceptionHandler
//    @ResponseBody
//    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<?> productNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header("Content-Type", "application/hal+json;charset=UTF-8")
                .location(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri())
                .body(new SimpleMessageResponse(ex.getMessage()));
    }

}
