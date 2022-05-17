package org.openmrs.module.sms.web.controller.it;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.sms.web.controller.SmsController;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@WebAppConfiguration
public class SmsControllerTest extends BaseModuleWebContextSensitiveTest {

	private SmsController smsController;

	@Test
	public void onGet()  {
		SmsController smsController = new SmsController();
		assertThat(smsController.onGet(), is(notNullValue()));
	}
}