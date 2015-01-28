package com.nanuvem.lom.business.relationtype;

import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.tests.relationtype.CreateRelationType;
import com.nanuvem.lom.business.BusinessFacade;
import com.nanuvem.lom.kernel.dao.MemoryDaoFactory;

public class BusinessCreateRelationTypeTest extends CreateRelationType {

	private MemoryDaoFactory daoFactory;

	@Override
	public Facade createFacade() {
		daoFactory = new MemoryDaoFactory();
		return new BusinessFacade(daoFactory);
	}

}
