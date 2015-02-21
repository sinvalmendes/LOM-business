package com.nanuvem.lom.business.validator;

import java.util.List;

import com.nanuvem.lom.business.validator.configuration.AttributeValidator;
import com.nanuvem.lom.business.validator.configuration.ConfigurationFieldValidator;

public class MaximumLengthAttributeConfigurationValidator implements ValueValidator<Integer> {

    public void validate(List<ValidationError> errors, String attribute, String value, Integer maxLength,
            boolean defaultValue) {

        if (value != null && value.length() > maxLength) {
            String message = (defaultValue) ? "the default value is greater than maxlength" : "The value for '"
                    + attribute + "' must have a maximum length of " + maxLength + " characters";
            ValidationError.addError(errors, message);
        }
    }

    public AttributeValidator createFieldValidator(String field) {
        return new ConfigurationFieldValidator(field, Integer.class);
    }

}
