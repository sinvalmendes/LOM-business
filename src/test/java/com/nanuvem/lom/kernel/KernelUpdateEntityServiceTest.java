package com.nanuvem.lom.kernel;

import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.tests.UpdateEntityServiceTest;
import com.nanuvem.lom.kernel.dao.MemoryDaoFactory;

public class KernelUpdateEntityServiceTest extends UpdateEntityServiceTest {

	private MemoryDaoFactory daoFactory;

	@Override
	public Facade createFacade() {
		daoFactory = new MemoryDaoFactory();
		return new KernelFacade(daoFactory);
	}

}
