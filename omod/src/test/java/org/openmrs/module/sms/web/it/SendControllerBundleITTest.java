package org.openmrs.module.sms.web.it;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.sms.api.service.OutgoingSms;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.ws.rs.core.HttpHeaders;

import static java.util.Collections.singletonList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verify SendController present & functional.
 */
@WebAppConfiguration
public class SendControllerBundleITTest extends BaseModuleWebContextSensitiveTest {

	private static final String ADMIN_USERNAME = "admin";

	private static final String ADMIN_PASS = "test";

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@After
	public void cleanUpDatabase() throws Exception {
		this.deleteAllData();
	}

	@Test
	public void verifyFunctional() throws Exception {
		OutgoingSms outgoingSms = new OutgoingSms("foo", singletonList("12065551212"), "hello, world");
		ObjectMapper mapper = new ObjectMapper();
		String outgoingSmsJson = mapper.writeValueAsString(outgoingSms);

		mockMvc.perform(post("/sms/send")
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationValue())
				.contentType(MediaType.parseMediaType("application/json"))
				.content(outgoingSmsJson.getBytes("UTF-8")))
				.andExpect(status().is(HttpStatus.NOT_FOUND.value()));

		//TODO: figure out how to create configs an then use them to "pretend send" using an SimpleHttpServer that
		//TODO: responds the way an SMS provider would.
	}

	private String getAuthorizationValue() {
		return "Basic " + Base64.encodeBase64String((ADMIN_USERNAME + ":" + ADMIN_PASS).getBytes());
	}
}
