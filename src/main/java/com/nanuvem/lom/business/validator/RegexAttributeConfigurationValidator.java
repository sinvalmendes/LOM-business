package com.nanuvem.lom.business.validator;

import java.util.List;

import com.nanuvem.lom.business.validator.configuration.AttributeValidator;
import com.nanuvem.lom.business.validator.configuration.ConfigurationFieldValidator;

public class RegexAttributeConfigurationValidator implements ValueValidator<String> {

    public void validate(List<ValidationError> errors, String attribute, String value, String regexValue,
            boolean defaultValue) {

        if (!value.matches(regexValue)) {
            String message = (defaultValue) ? "the default value does not match regex configuration"
                    : "The value for the '" + attribute + "' attribute does not meet the defined regular expression";
            ValidationError.addError(errors, message);
        }
    }

    public AttributeValidator createFieldValidator(String field) {
        return new ConfigurationFieldValidator(field, String.class);
    }
}
