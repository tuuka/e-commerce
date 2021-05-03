package net.tuuka.ecommerce.exception;

public class ProductCategoryNotFoundException extends RuntimeException{
    public ProductCategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductCategoryNotFoundException(Throwable cause) {
        super(cause);
    }

    public ProductCategoryNotFoundException(String message) {
        super(message);
    }
}
