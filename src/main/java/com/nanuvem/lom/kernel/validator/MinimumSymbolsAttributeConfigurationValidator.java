package com.nanuvem.lom.kernel.validator;

import java.util.List;

import com.nanuvem.lom.kernel.validator.configuration.AttributeValidator;
import com.nanuvem.lom.kernel.validator.configuration.ConfigurationFieldValidator;

public class MinimumSymbolsAttributeConfigurationValidator implements
		ValueValidator<Integer> {

	public void validate(List<ValidationError> errors, String attribute,
			String value, Integer minSymbols, boolean defaultValue) {

		int numericSymbolCounter = 0;
		for (int i = 0; i < value.length(); i++) {
			if (!Character.isLetterOrDigit(value.toCharArray()[i])) {
				numericSymbolCounter++;
			}
		}

		if (numericSymbolCounter < minSymbols) {
			String messagePlural = minSymbols > 1 ? "s" : "";

			String message = (defaultValue) ? 
					"the default value must have at least "
					+ minSymbols + " symbol character" + messagePlural: 
					"The value for '" + attribute + "' must have at least "
					+ minSymbols + " symbol character" + messagePlural;

			ValidationError.addError(errors, message);
		}
	}

	public AttributeValidator createFieldValidator(String field) {
		return new ConfigurationFieldValidator(field, Integer.class);
	}

}
