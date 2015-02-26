package com.nanuvem.lom.business;

import java.util.List;

import com.nanuvem.lom.api.Cardinality;
import com.nanuvem.lom.api.Instance;
import com.nanuvem.lom.api.MetadataException;
import com.nanuvem.lom.api.Relation;
import com.nanuvem.lom.api.RelationType;
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
        if(relation.getRelationType() == null){
            throw new MetadataException("Invalid argument: The relation type is mandatory!");
        }
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
        RelationType relationType = relation.getRelationType();
        if (relationType.getTargetCardinality() == Cardinality.ONE) {
            List<Relation> sourceInstanceRelations = this.findRelationsBySourceInstance(sourceInstance, relationType);
            if (sourceInstanceRelations.size() != 0) {
                throw new MetadataException(
                        "Invalid argument, the target cardinality is ONE, the target instance cannot be associated to the source instance!");
            }
        }
        if (relationType.getSourceCardinality() == Cardinality.ONE
                && relationType.getTargetCardinality() == Cardinality.MANY) {
            List<Relation> targetInstanceRelations = this.findRelationsByTargetInstance(targetInstance);
            for (Relation targetRelation : targetInstanceRelations) {
                if (targetRelation.getRelationType().equals(relationType))
                    throw new MetadataException(
                            "Invalid argument, the source cardinality is ONE, the target instance cannot be associated to the source instance!");
            }
        }
        
        
        Relation createdRelation = dao.create(relation);
        return createdRelation;
    }

    private List<Relation> findRelationsByTargetInstance(Instance targetInstance) {
        return dao.findRelationsByTargetInstance(targetInstance);
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

    public List<Relation> findRelationsBySourceInstance(Instance source, RelationType relationType) {
        return dao.findRelationsBySourceInstance(source, relationType);
    }

    public List<Relation> findRelationsByRelationType(RelationType relationType) {
        return dao.findRelationsByRelationType(relationType);
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

    public List<Relation> findRelationsBySourceInstance(Instance source, RelationType relationType) {
        return this.relationDao.findRelationsBySourceInstance(source, relationType);
    }

    public List<Relation> findRelationsByRelationType(RelationType relationType) {
        return this.relationDao.findRelationsByRelationType(relationType);
    }

    public List<Relation> findRelationsByTargetInstance(Instance targetInstance) {
        return this.relationDao.findRelationsByTargetInstance(targetInstance);
    }

}