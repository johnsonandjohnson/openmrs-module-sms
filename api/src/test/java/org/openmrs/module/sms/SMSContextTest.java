package org.openmrs.module.sms;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class SMSContextTest extends BaseModuleContextSensitiveTest {

	@Test
	public void shouldSetupContext() {
		Assert.assertNotNull(Context.getAdministrationService());
	}
}
