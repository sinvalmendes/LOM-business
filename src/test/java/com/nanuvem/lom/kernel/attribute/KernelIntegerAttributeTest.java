package com.nanuvem.lom.kernel.attribute;

import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.tests.attribute.IntegerAttributeTest;
import com.nanuvem.lom.kernel.KernelFacade;
import com.nanuvem.lom.kernel.dao.MemoryDaoFactory;

public class KernelIntegerAttributeTest extends IntegerAttributeTest {

	@Override
	public Facade createFacade() {
		return new KernelFacade(new MemoryDaoFactory());
	}

}
