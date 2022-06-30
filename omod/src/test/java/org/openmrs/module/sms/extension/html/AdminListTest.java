/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.sms.extension.html;

import org.junit.Test;
import org.openmrs.module.Extension;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AdminListTest {
    private static final String PROJECT_ID = "sms";

    @Test
    public void getMediaType_shouldReturnHtml() {
        final AdminList adminList = new AdminList();

        assertEquals(Extension.MEDIA_TYPE.html, adminList.getMediaType());
    }

    @Test
    public void getTitle_shouldReturnTitle() {
        final String expectedTitleKey = PROJECT_ID + ".title";
        final AdminList adminList = new AdminList();

        assertEquals(expectedTitleKey, adminList.getTitle());
    }

    @Test
    public void getLinks_shouldReturnCorrectLinks() {
        final String expectedSwaggerUri = PROJECT_ID + "/swagger/index.html";
        final String expectedSwaggerTitle = PROJECT_ID + ".refapp.title";

        final AdminList adminList = new AdminList();
        final Map<String, String> adminLinks = adminList.getLinks();

        assertEquals("There should be only one link", 1, adminLinks.size());

        Map.Entry<String, String> swaggerLink = null;

        for (Map.Entry<String, String> link : adminLinks.entrySet()) {
            if (link.getKey().endsWith(expectedSwaggerUri)) {
                swaggerLink = link;
            }
        }

        assertNotNull("There should be a swagger link, ending with: " + expectedSwaggerUri, swaggerLink);
        assertEquals(expectedSwaggerTitle, swaggerLink.getValue());
    }

}

