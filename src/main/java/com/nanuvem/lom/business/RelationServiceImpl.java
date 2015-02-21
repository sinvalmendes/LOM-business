package com.nanuvem.lom.business;

import java.util.List;

import com.nanuvem.lom.api.Instance;
import com.nanuvem.lom.api.MetadataException;
import com.nanuvem.lom.api.Relation;
import com.nanuvem.lom.api.dao.DaoFactory;
import com.nanuvem.lom.api.dao.RelationDao;
import com.nanuvem.lom.business.validator.definition.AttributeTypeDefinitionManager;

public class RelationServiceImpl {

    private RelationDao dao;
    private EntityServiceImpl entityService;
    private AttributeServiceImpl attributeService;
    private InstanceServiceImpl instanceService;

    RelationServiceImpl(DaoFactory daoFactory) {
        this.dao = new RelationDaoDecorator(daoFactory.createRelationDao());
        this.entityService = new EntityServiceImpl(daoFactory);
        AttributeTypeDefinitionManager deployers = new AttributeTypeDefinitionManager();
        this.attributeService = new AttributeServiceImpl(daoFactory, entityService, deployers);
        this.instanceService = new InstanceServiceImpl(daoFactory, entityService, attributeService, deployers);
    }

    public Relation create(Relation relation) {
        if (relation.getSource() == null || relation.getSource().getId() == null) {
            throw new MetadataException("Invalid argument: The source instance is mandatory!");
        }
        if (relation.getTarget() == null || relation.getTarget().getId() == null) {
            throw new MetadataException("Invalid argument: The target instance is mandatory!");
        }
        Instance sourceInstance = this.instanceService.findInstanceById(relation.getSource().getId());
        Instance targetInstance = this.instanceService.findInstanceById(relation.getTarget().getId());
        if (sourceInstance == null) {
            throw new MetadataException("Invalid argument: The source instance is mandatory!");
        }
        if (targetInstance == null) {
            throw new MetadataException("Invalid argument: The target instance is mandatory!");
        }
        Relation createdRelation = dao.create(relation);
        return createdRelation;
    }

    public Relation findRelationById(Long id) {
        return dao.findById(id);
    }

    public List<Relation> listAllRelations() {
        return dao.listAllRelations();
    }

    public Relation update(Relation relation) {
        // TODO Auto-generated method stub
        return null;
    }

    public void delete(Long id) {
        dao.delete(id);
    }

}

class RelationDaoDecorator implements RelationDao {

    private RelationDao relationDao;

    public RelationDaoDecorator(RelationDao relationDao) {
        this.relationDao = relationDao;
    }

    public Relation create(Relation relation) {
        return this.relationDao.create(relation);
    }

    public Relation findById(Long id) {
        return this.relationDao.findById(id);
    }

    public Relation update(Relation relation) {
        return this.relationDao.create(relation);
    }

    public List<Relation> listAllRelations() {
        return this.relationDao.listAllRelations();
    }

    public void delete(Long id) {
        this.relationDao.delete(id);
    }

}