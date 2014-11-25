package com.nanuvem.lom.kernel.instance;

import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.tests.instance.PasswordValueTest;
import com.nanuvem.lom.kernel.KernelFacade;
import com.nanuvem.lom.kernel.dao.MemoryDaoFactory;

public class KernelPasswordValueTest extends PasswordValueTest {

	private MemoryDaoFactory daoFactory;

	@Override
	public Facade createFacade() {
		daoFactory = new MemoryDaoFactory();
		return new KernelFacade(daoFactory);
	}

}
