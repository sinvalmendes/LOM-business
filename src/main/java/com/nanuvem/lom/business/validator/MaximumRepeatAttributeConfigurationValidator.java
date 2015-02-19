package com.nanuvem.lom.business.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nanuvem.lom.business.validator.configuration.AttributeValidator;
import com.nanuvem.lom.business.validator.configuration.ConfigurationFieldValidator;

public class MaximumRepeatAttributeConfigurationValidator implements
		ValueValidator<Integer> {

	public void validate(List<ValidationError> errors, String attribute,
			String value, Integer maxRepeat, boolean defaultValue) {

		Map<String, Integer> mapCounter = new HashMap<String, Integer>();
		int characterCounter = 0;

		for (int i = 0; i < value.toCharArray().length; i++) {
			Integer count = mapCounter
					.get(String.valueOf(value.toCharArray()[i]));
			if (count == null) {
				count = new Integer(-1);
			}
			count++;
			mapCounter.put(String.valueOf(value.toCharArray()[i]), count);

			if (characterCounter < count) {
				characterCounter = count;
			}
		}

		if (characterCounter > maxRepeat) {
			String messagePlural = characterCounter > 1 ? " more than "
					+ (maxRepeat + 1) + " " : " ";
			String message = (defaultValue) ? "the default value must not have"
					+ messagePlural + "repeated characters" : "The value for '"
					+ attribute + "' must not have" + messagePlural
					+ "repeated characters";

			ValidationError.addError(errors, message);
		}
	}

	public AttributeValidator createFieldValidator(String field) {
		return new ConfigurationFieldValidator(field, Integer.class);
	}

}
