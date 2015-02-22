package com.nanuvem.lom.business;

import java.util.List;

import com.nanuvem.lom.api.Cardinality;
import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.MetadataException;
import com.nanuvem.lom.api.Relation;
import com.nanuvem.lom.api.RelationType;
import com.nanuvem.lom.api.dao.DaoFactory;
import com.nanuvem.lom.api.dao.RelationTypeDao;

public class RelationTypeServiceImpl {

    private RelationTypeDao dao;
    private EntityServiceImpl entityService;
    private RelationServiceImpl relationService;

    RelationTypeServiceImpl(DaoFactory daoFactory) {
        this.dao = new RelationTypeDaoDecorator(daoFactory.createRelationTypeDao());
        this.entityService = new EntityServiceImpl(daoFactory);
        this.relationService = new RelationServiceImpl(daoFactory);
    }

    public RelationType create(RelationType relationType) {
        if (relationType.getSourceCardinality() == null) {
            relationType.setSourceCardinality(Cardinality.ONE);
        }
        if (relationType.getTargetCardinality() == null) {
            relationType.setTargetCardinality(Cardinality.ONE);
        }
        if (relationType.getSourceEntity() == null) {
            throw new MetadataException("Invalid value for source entity: The source entity is mandatory");
        } else if (relationType.getTargetEntity() == null) {
            throw new MetadataException("Invalid value for target entity: The target entity is mandatory");
        }

        RelationType createdRelationType = dao.create(relationType);
        return createdRelationType;
    }

    public RelationType findRelationTypeById(Long id) {
        return dao.findRelationTypeById(id);
    }

    public List<RelationType> listAllRelationTypes() {
        return dao.listAllRelationTypes();
    }

    public void delete(Long id) {
        dao.delete(id);
    }

    public RelationType update(RelationType relationType) {
        validateRelationTypeForUpdate(relationType);
        if (!relationType.isBidirectional()) {
            relationType.setReverseName(null);
        }
        /*
         * Validate if the relationType already exists in DB Validate if
         * thesourceEntity already exists in DB Validate if the targetEntity
         * already exists in DB
         */
        this.executeCardinalityChanges(relationType);
        return dao.update(relationType);
    }

    private void executeCardinalityChanges(RelationType relationType) {
        RelationType oldRelationType = this.findRelationTypeById(relationType.getId());
        if (oldRelationType.getSourceCardinality() == Cardinality.MANY
                && relationType.getSourceCardinality() == Cardinality.ONE) {
            if (oldRelationType.getTargetCardinality() == Cardinality.MANY
                    && relationType.getTargetCardinality() == Cardinality.ONE) {
                List<Relation> oldRelationTypeRelations = this.relationService
                        .findRelationsByRelationType(oldRelationType);
                oldRelationTypeRelations.remove(0);
                for (Relation relation : oldRelationTypeRelations) {
                    relationService.delete(relation.getId());
                }
            }
        }
    }

    private boolean validateRelationTypeForUpdate(RelationType relationType) {
        Entity sourceEntity = this.entityService.findById(relationType.getSourceEntity().getId());
        if (sourceEntity == null) {
            throw new MetadataException("Invalid argument: The source entity is mandatory!");
        }
        Entity targetEntity = this.entityService.findById(relationType.getTargetEntity().getId());
        if (targetEntity == null) {
            throw new MetadataException("Invalid argument: The target entity is mandatory!");
        }
        if (relationType.getReverseName() == null && relationType.isBidirectional()) {
            throw new MetadataException(
                    "Invalid argument: Reverse Name is mandatory when the relationship is bidirectional!");
        }
        return true;
    }

}

class RelationTypeDaoDecorator implements RelationTypeDao {

    private RelationTypeDao relationTypeDao;

    public RelationTypeDaoDecorator(RelationTypeDao RelationTypeDao) {
        this.relationTypeDao = RelationTypeDao;
    }

    public RelationType create(RelationType relationType) {
        return this.relationTypeDao.create(relationType);
    }

    public RelationType findRelationTypeById(Long id) {
        return this.relationTypeDao.findRelationTypeById(id);
    }

    public RelationType update(RelationType relationType) {
        return this.relationTypeDao.update(relationType);
    }

    public List<RelationType> listAllRelationTypes() {
        return this.relationTypeDao.listAllRelationTypes();
    }

    public void delete(Long id) {
        this.relationTypeDao.delete(id);
    }
}
