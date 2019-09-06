package org.openmrs.module.sms.web.it;

import org.junit.After;
import org.junit.Test;
import org.openmrs.module.sms.api.service.SmsService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertNotNull;

/**
 * Verify IncomingController present & functional.
 */
public class IncomingControllerBundleITTest extends BaseModuleWebContextSensitiveTest {

	@Autowired
	private SmsService smsService;

	@After
	public void cleanUpDatabase() throws Exception {
		this.deleteAllData();
	}

	@Test
	public void verifyServiceFunctional() {
		assertNotNull(smsService);
	}
}
