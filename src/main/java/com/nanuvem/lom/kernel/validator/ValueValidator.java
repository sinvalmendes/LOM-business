package com.nanuvem.lom.kernel.validator;

import java.util.List;

import com.nanuvem.lom.kernel.validator.configuration.AttributeValidator;

public interface ValueValidator<T> {

	AttributeValidator createFieldValidator(String field);

	void validate(List<ValidationError> errors, String attribute, String value, T configurationValue, boolean defaultValue);

}