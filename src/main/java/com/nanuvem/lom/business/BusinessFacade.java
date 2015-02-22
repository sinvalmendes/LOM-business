package com.nanuvem.lom.business;

import java.util.List;

import com.nanuvem.lom.api.Attribute;
import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.Instance;
import com.nanuvem.lom.api.Relation;
import com.nanuvem.lom.api.RelationType;
import com.nanuvem.lom.api.dao.DaoFactory;
import com.nanuvem.lom.business.validator.definition.AttributeTypeDefinitionManager;

public class BusinessFacade implements Facade {

    private EntityServiceImpl entityService;
    private AttributeServiceImpl attributeService;
    private InstanceServiceImpl instanceService;
    private RelationTypeServiceImpl relationTypeService;
    private RelationServiceImpl relationService;

    public BusinessFacade(DaoFactory daoFactory) {
        entityService = new EntityServiceImpl(daoFactory);
        AttributeTypeDefinitionManager deployers = new AttributeTypeDefinitionManager();
        attributeService = new AttributeServiceImpl(daoFactory, entityService, deployers);
        instanceService = new InstanceServiceImpl(daoFactory, entityService, attributeService, deployers);
        relationTypeService = new RelationTypeServiceImpl(daoFactory);
        relationService = new RelationServiceImpl(daoFactory);
    }

    public EntityServiceImpl getEntityService() {
        return this.entityService;
    }

    public Entity create(Entity entity) {
        return entityService.create(entity);
    }

    public Entity findEntityById(Long id) {
        return entityService.findById(id);
    }

    public Entity findEntityByFullName(String fullName) {
        return entityService.findByFullName(fullName);
    }

    public List<Entity> listAllEntities() {
        return entityService.listAll();
    }

    public List<Entity> listEntitiesByFullName(String fragment) {
        return entityService.listByFullName(fragment);
    }

    public Entity update(Entity entity) {
        return entityService.update(entity);
    }

    public void deleteEntity(Long id) {
        entityService.delete(id);
    }

    public Attribute create(Attribute attribute) {
        return attributeService.create(attribute);
    }

    public Attribute findAttributeById(Long id) {
        return attributeService.findAttributeById(id);
    }

    public Attribute findAttributeByNameAndEntityFullName(String name, String fullEntityName) {
        return attributeService.findAttributeByNameAndEntityFullName(name, fullEntityName);
    }

    public Attribute update(Attribute attribute) {
        return attributeService.update(attribute);
    }

    public Instance create(Instance instance) {
        return instanceService.create(instance);
    }

    public Instance findInstanceById(Long id) {
        return instanceService.findInstanceById(id);
    }

    public List<Instance> findInstancesByEntityId(Long entityId) {
        return instanceService.findInstancesByEntityId(entityId);
    }

    public RelationType create(RelationType relationType) {
        return relationTypeService.create(relationType);
    }

    public RelationType findRelationTypeById(Long id) {
        RelationType relationType = relationTypeService.findRelationTypeById(id);
        return RelationType.cloneObject(relationType);
    }

    public List<RelationType> listAllRelationTypes() {
        return relationTypeService.listAllRelationTypes();
    }

    public RelationType update(RelationType relationType) {
        return relationTypeService.update(relationType);
    }

    public void deleteRelationType(Long id) {
        relationTypeService.delete(id);
    }

    public Relation create(Relation relation) {
        return relationService.create(relation);
    }

    public Relation findRelationById(Long id) {
        return relationService.findRelationById(id);
    }

    public List<Relation> listAllRelations() {
        return relationService.listAllRelations();
    }

    public Relation update(Relation relation) {
        return relationService.update(relation);
    }

    public void deleteRelation(Long id) {
        relationService.delete(id);
    }

    public List<Relation> findRelationsBySourceInstance(Instance source) {
        return relationService.findRelationsBySourceInstance(source);
    }

    public List<Relation> findRelationsByRelationType(RelationType relationType) {
        return relationService.findRelationsByRelationType(relationType);
    }

}
