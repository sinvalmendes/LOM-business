package com.nanuvem.lom.kernel;

import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.tests.EntityServiceTest;
import com.nanuvem.lom.kernel.dao.MemoryDaoFactory;

public class KernelEntityServiceTest extends EntityServiceTest {

	private MemoryDaoFactory daoFactory;

	@Override
	public Facade createFacade() {
		daoFactory = new MemoryDaoFactory();
		return new KernelFacade(daoFactory);
	}

}
