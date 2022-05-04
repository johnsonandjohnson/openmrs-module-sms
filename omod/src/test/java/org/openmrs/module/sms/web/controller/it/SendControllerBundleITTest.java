/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.sms.web.controller.it;

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

import static java.util.Collections.singletonList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verify SendController present & functional.
 */
@WebAppConfiguration
public class SendControllerBundleITTest extends BaseModuleWebContextSensitiveTest {

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
                .contentType(MediaType.parseMediaType("application/json"))
                .content(outgoingSmsJson.getBytes("UTF-8")))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));

        //TODO: figure out how to create configs an then use them to "pretend send" using an SimpleHttpServer that
        //TODO: responds the way an SMS provider would.
    }

}
