package com.nanuvem.lom.kernel.instance;

import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.tests.instance.LongTextValueTest;
import com.nanuvem.lom.kernel.KernelFacade;
import com.nanuvem.lom.kernel.dao.MemoryDaoFactory;

public class KernelLongTextValueTest extends LongTextValueTest {

	private MemoryDaoFactory daoFactory;

	@Override
	public Facade createFacade() {
		daoFactory = new MemoryDaoFactory();
		return new KernelFacade(daoFactory);
	}

}
