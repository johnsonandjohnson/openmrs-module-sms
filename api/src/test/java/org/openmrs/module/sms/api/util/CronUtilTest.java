/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.sms.api.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import org.junit.Test;

public class CronUtilTest {

  @Test
  public void shouldGetNextDateBasedOnCronExpression() {
    String cronExpression = "0 30 */3 ? * *"; //every three hours starting at :30 minute
    Date dateToProcess = DateUtil.parse("2022-10-25 09:30", DateUtil.BASIC_DATE_TIME_FORMAT);

    Date actual = CronUtil.getNextDate(cronExpression, dateToProcess);
    String parsedResult = DateUtil.getDateInGivenFormat(actual, DateUtil.BASIC_DATE_TIME_FORMAT);

    assertNotNull(actual);
    assertEquals("2022-10-25 12:30", parsedResult);
  }
}
