package kh.edu.ppua.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s not found with id: %d", resourceName, id));
    }

    public ResourceNotFoundException(Class<?> clazz, Long id) {
        super(String.format("%s not found with id: %d", clazz.getSimpleName(), id));
    }

    public ResourceNotFoundException(Class<?> clazz, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", clazz.getSimpleName(), fieldName, fieldValue));
    }
}