package com.nanuvem.lom.business.validator;

import java.util.List;

import com.nanuvem.lom.business.validator.configuration.AttributeValidator;
import com.nanuvem.lom.business.validator.configuration.ConfigurationFieldValidator;

public class MinimumUppersAttributeConfigurationValidator implements ValueValidator<Integer> {

    public void validate(List<ValidationError> errors, String attribute, String value, Integer minUppers,
            boolean defaultValue) {

        int uppercaseCharacterCounter = 0;
        for (int i = 0; i < value.length(); i++) {
            if (Character.isUpperCase(value.toCharArray()[i])) {
                uppercaseCharacterCounter++;
            }
        }
        if (uppercaseCharacterCounter < minUppers) {
            String messagePlural = minUppers > 1 ? "s" : "";

            String message = (defaultValue) ? "the default value must have at least " + minUppers
                    + " upper case character" + messagePlural : "The value for '" + attribute + "' must have at least "
                    + minUppers + " uppercase character" + messagePlural;
            ValidationError.addError(errors, message);
        }
    }

    public AttributeValidator createFieldValidator(String field) {
        return new ConfigurationFieldValidator(field, Integer.class);
    }

}
