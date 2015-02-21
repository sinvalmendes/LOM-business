package com.nanuvem.lom.business.validator;

import java.util.List;

public class ValidationError {

    private String message;

    public ValidationError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static void addError(List<ValidationError> errors, String message) {
        ValidationError validationError = new ValidationError(message);
        errors.add(validationError);
    }

}
