package com.nanuvem.lom.business.instance;

import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.tests.instance.PasswordValueTest;
import com.nanuvem.lom.business.BusinessFacade;
import com.nanuvem.lom.kernel.dao.MemoryDaoFactory;

public class BusinessPasswordValueTest extends PasswordValueTest {

    private MemoryDaoFactory daoFactory;

    @Override
    public Facade createFacade() {
        daoFactory = new MemoryDaoFactory();
        return new BusinessFacade(daoFactory);
    }

}
