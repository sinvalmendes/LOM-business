package com.nanuvem.lom.business.validator.definition;

import java.util.HashMap;
import java.util.Map;

import com.nanuvem.lom.api.AttributeType;

public class AttributeTypeDefinitionManager {

    private Map<String, AttributeTypeDefinition> definitions = new HashMap<String, AttributeTypeDefinition>();

    public AttributeTypeDefinitionManager() {
        add(AttributeType.TEXT.name(), new TextAttributeType());
        add(AttributeType.LONGTEXT.name(), new LongTextAttributeType());
        add(AttributeType.PASSWORD.name(), new PasswordAttributeType());
        add(AttributeType.INTEGER.name(), new IntegerAttributeType());

    }

    public void add(String name, AttributeTypeDefinition deployer) {
        definitions.put(name, deployer);
    }

    public AttributeTypeDefinition get(String name) {
        return definitions.get(name);
    }
}
