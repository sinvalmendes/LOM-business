package com.nanuvem.lom.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;

import com.nanuvem.lom.api.Attribute;
import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.MetadataException;
import com.nanuvem.lom.api.dao.AttributeDao;
import com.nanuvem.lom.api.dao.DaoFactory;
import com.nanuvem.lom.api.util.JsonNodeUtil;
import com.nanuvem.lom.business.validator.ValidationError;
import com.nanuvem.lom.business.validator.configuration.AttributeValidator;
import com.nanuvem.lom.business.validator.definition.AttributeTypeDefinition;
import com.nanuvem.lom.business.validator.definition.AttributeTypeDefinitionManager;

public class AttributeServiceImpl {

    private final Integer MINIMUM_ATTRIBUTE_SEQUENCE = 1;

    private final String PREFIX_EXCEPTION_MESSAGE_CONFIGURATION = "Invalid configuration for attribute";

    private AttributeDao attributeDao;
    private EntityServiceImpl entityService;
    private AttributeTypeDefinitionManager definitionManager;

    AttributeServiceImpl(DaoFactory dao, EntityServiceImpl entityService,
            AttributeTypeDefinitionManager definitionManager) {
        this.entityService = entityService;
        this.definitionManager = definitionManager;
        this.attributeDao = new AttributeDaoDecorator(dao.createAttributeDao());

    }

    private void validateCreate(Attribute attribute) {
        validateDuplicatedAttribute(attribute);
        defineAttributeSequenceNumber(attribute);

        validateAttributeName(attribute);

        if (attribute.getType() == null) {
            throw new MetadataException("The type of an Attribute is mandatory");
        }
        validateAttributeConfiguration(attribute);
    }

    private void defineAttributeSequenceNumber(Attribute attribute) {
        int currentNumberOfAttributes = attribute.getEntity().getAttributes().size();
        if (attribute.getSequence() != null) {
            boolean minValueForSequence = attribute.getSequence() < MINIMUM_ATTRIBUTE_SEQUENCE;
            boolean maxValueForSequence = currentNumberOfAttributes + 1 < attribute.getSequence();

            if (minValueForSequence || maxValueForSequence) {
                throw new MetadataException("Invalid value for Attribute sequence: " + attribute.getSequence());
            }
        } else {
            attribute.setSequence(currentNumberOfAttributes + 1);
        }
    }

    private void validateAttributeName(Attribute attribute) {
        if (attribute.getName() == null || attribute.getName().isEmpty()) {
            throw new MetadataException("The name of an Attribute is mandatory");
        }

        if (!Pattern.matches("[a-zA-Z1-9]{1,}", attribute.getName())) {
            throw new MetadataException("Invalid value for Attribute name: " + attribute.getName());
        }
    }

    private List<Attribute> findAllAttributesForEntity(Entity entity) {
        if (entity != null && !entity.getFullName().isEmpty()) {
            Entity foundEntity = entityService.findByFullName(entity.getFullName());
            if (foundEntity != null && foundEntity.getAttributes() != null && foundEntity.getAttributes().size() > 0) {
                return foundEntity.getAttributes();
            }
        }
        return null;
    }

    private void validateAttributeConfiguration(Attribute attribute) {
        String configuration = attribute.getConfiguration();
        if (configuration != null && !configuration.isEmpty()) {
            JsonNode jsonNode = JsonNodeUtil.validate(configuration, "Invalid value for Attribute configuration: "
                    + configuration);

            AttributeTypeDefinition definition = definitionManager.get(attribute.getType().name());
            validateFieldNames(definition, attribute, jsonNode);
            validateFieldValues(definition, attribute, jsonNode);
        }
    }

    private void validateFieldNames(AttributeTypeDefinition definition, Attribute attribute, JsonNode jsonNode) {

        Iterator<String> fieldNames = jsonNode.getFieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if (!definition.containsConfigurationField(fieldName)) {
                throw new MetadataException("Invalid configuration for attribute " + attribute.getName() + ": the "
                        + fieldName + " configuration attribute is unknown");
            }
        }
    }

    private void validateFieldValues(AttributeTypeDefinition definition, Attribute attribute, JsonNode jsonNode) {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        for (AttributeValidator validator : definition.getValidators()) {
            validator.validateDefault(errors, jsonNode);
        }

        Util.throwValidationErrors(errors, PREFIX_EXCEPTION_MESSAGE_CONFIGURATION + " " + attribute.getName() + ": ");
    }

    private Entity validateExistingEntityForAttribute(Attribute attribute) {
        Entity entity = null;
        try {
            entity = entityService.findById(attribute.getEntity().getId());
        } catch (MetadataException e) {
            throw new MetadataException("Invalid Entity: " + attribute.getEntity().getFullName());
        }
        return entity;
    }

    private void validateDuplicatedAttribute(Attribute attribute) {
        List<Attribute> foundAttributes = this.findAllAttributesForEntity(attribute.getEntity());
        if (foundAttributes != null) {
            for (Attribute at : foundAttributes) {
                if (at.getName().equalsIgnoreCase(attribute.getName())) {
                    this.throwMetadataExceptionOnAttributeDuplication(attribute);
                }
            }
        }
    }

    private void validateExistingAttributeNotInEntityOnUpdate(Attribute attribute) {
        if (attribute.getId() != null) {
            Attribute foundAttributes = this.findAttributeByNameAndEntityFullName(attribute.getName(), attribute
                    .getEntity().getFullName());
            if (foundAttributes != null) {
                if (!attribute.getId().equals(foundAttributes.getId())) {
                    this.throwMetadataExceptionOnAttributeDuplication(attribute);
                }
            }
        }
    }

    private void throwMetadataExceptionOnAttributeDuplication(Attribute attribute) {
        throw new MetadataException("Attribute duplication on " + attribute.getEntity().getFullName()
                + " Entity. It already has an attribute " + StringUtils.lowerCase(attribute.getName() + "."));
    }

    public Attribute create(Attribute attribute) {
        Entity entity = validateExistingEntityForAttribute(attribute);
        attribute.setEntity(entity);
        validateCreate(attribute);
        Attribute createdAttribute = this.attributeDao.create(attribute);
        entityService.update(createdAttribute.getEntity());
        return createdAttribute;
    }

    public List<Attribute> listAllAttributes(String entityFullName) {
        Entity entity = entityService.findByFullName(entityFullName);
        return entity.getAttributes();
    }

    public Attribute findAttributeById(Long id) {
        if (id != null) {
            Attribute attribute = this.attributeDao.findAttributeById(id);
            return attribute;
        } else {
            return null;
        }
    }

    public Attribute findAttributeByNameAndEntityFullName(String nameAttribute, String entityFullName) {

        if ((nameAttribute != null && !nameAttribute.isEmpty())
                && (entityFullName != null && !entityFullName.isEmpty())) {

            Attribute attribute = this.attributeDao.findAttributeByNameAndEntityFullName(nameAttribute, entityFullName);
            return attribute;
        }
        return null;
    }

    public Attribute update(Attribute attribute) {
        this.validateAttributeName(attribute);
        this.validateUpdateSequence(attribute);
        this.validateUpdateType(attribute);
        this.validateExistingAttributeNotInEntityOnUpdate(attribute);
        this.validateAttributeConfiguration(attribute);

        Attribute updatedAttribute = this.attributeDao.update(attribute);
        entityService.update(updatedAttribute.getEntity());
        return updatedAttribute;
    }

    private void validateUpdateType(Attribute attribute) {
        Attribute attributeFound = this.findAttributeById(attribute.getId());

        if (!attributeFound.getType().equals(attribute.getType())) {
            throw new MetadataException("Can not change the type of an attribute");
        }
    }

    private void validateUpdateSequence(Attribute attribute) {
        Entity entity = entityService.findById(attribute.getEntity().getId());
        int currentNumberOfAttributes = entity.getAttributes().get(entity.getAttributes().size() - 1).getSequence();

        if (attribute.getSequence() != null) {
            boolean minValueForSequence = attribute.getSequence() < MINIMUM_ATTRIBUTE_SEQUENCE;
            boolean maxValueForSequence = currentNumberOfAttributes < attribute.getSequence();

            if (!(minValueForSequence || maxValueForSequence)) {
                return;
            }
        }
        throw new MetadataException("Invalid value for Attribute sequence: " + attribute.getSequence());
    }
}

class AttributeDaoDecorator implements AttributeDao {

    private AttributeDao attributeDao;

    public AttributeDaoDecorator(AttributeDao attributeDao) {
        this.attributeDao = attributeDao;
    }

    public Attribute create(Attribute attribute) {
        Attribute createdAttribute = Util.clone(attributeDao.create(Util.clone(attribute)));
        Util.removeDefaultNamespace(createdAttribute);
        return createdAttribute;
    }

    public Attribute findAttributeById(Long id) {
        Attribute attribute = Util.clone(attributeDao.findAttributeById(id));
        Util.removeDefaultNamespace(attribute);
        return Util.clone(attribute);
    }

    public Attribute findAttributeByNameAndEntityFullName(String nameAttribute, String entityFullName) {
        entityFullName = Util.setDefaultNamespace(entityFullName);
        Attribute attribute = Util.clone(attributeDao.findAttributeByNameAndEntityFullName(nameAttribute,
                entityFullName));
        Util.removeDefaultNamespace(attribute);
        return Util.clone(attribute);
    }

    public Attribute update(Attribute attribute) {
        Attribute updatedAttribute = Util.clone(attributeDao.update(Util.clone(attribute)));
        Util.removeDefaultNamespace(updatedAttribute);
        return Util.clone(updatedAttribute);
    }

}