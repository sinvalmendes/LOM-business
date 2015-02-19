package com.nanuvem.lom.business;

import java.util.List;

import com.nanuvem.lom.api.Attribute;
import com.nanuvem.lom.api.Cardinality;
import com.nanuvem.lom.api.MetadataException;
import com.nanuvem.lom.api.RelationType;
import com.nanuvem.lom.api.dao.DaoFactory;
import com.nanuvem.lom.api.dao.RelationTypeDao;

public class RelationTypeServiceImpl {

	private RelationTypeDao dao;

	RelationTypeServiceImpl(DaoFactory daoFactory) {
		this.dao = new RelationTypeDaoDecorator(
				daoFactory.createRelationTypeDao());
	}

	public RelationType create(RelationType relationType) {
		if (relationType.getSourceCardinality() == null) {
			relationType.setSourceCardinality(Cardinality.ONE);
		}
		if (relationType.getTargetCardinality() == null) {
			relationType.setTargetCardinality(Cardinality.ONE);
		}
		if (relationType.getSourceEntity() == null) {
			throw new MetadataException(
					"Invalid value for source entity: The source entity is mandatory");
		} else if (relationType.getTargetEntity() == null) {
			throw new MetadataException(
					"Invalid value for target entity: The target entity is mandatory");
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

	public Attribute update(RelationType relationType) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<RelationType> listAllRelationTypes() {
		return this.relationTypeDao.listAllRelationTypes();
	}

	public void delete(Long id) {
		this.relationTypeDao.delete(id);
	}
}
