package net.tuuka.ecommerce.exception;

public class ProductCategoryNotEmptyException extends RuntimeException{
    public ProductCategoryNotEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductCategoryNotEmptyException(Throwable cause) {
        super(cause);
    }

    public ProductCategoryNotEmptyException(String message) {
        super(message);
    }
}
