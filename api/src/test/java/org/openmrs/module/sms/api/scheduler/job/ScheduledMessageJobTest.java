/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.sms.api.scheduler.job;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.openmrs.module.sms.ContextMockedTest;
import org.openmrs.module.sms.api.adhocsms.AdHocSMSInputSourceProcessorContext;
import org.openmrs.module.sms.api.adhocsms.ScheduledMessageDetails;
import org.openmrs.module.sms.api.data.AdHocSMSData;
import org.openmrs.module.sms.api.util.ScheduledMessageDetailsUtil;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;

public class ScheduledMessageJobTest extends ContextMockedTest {

  @Test
  public void shouldExecuteScheduledMessageJobLogic() throws SchedulerException {
    TaskDefinition task = buildScheduledMessageTask();
    when(adHocSMSInputSourceProcessorService.getAdHocSMSData(any(AdHocSMSInputSourceProcessorContext.class)))
        .thenReturn(Collections.singletonList(buildAdHocSMSDataObject()));
    ScheduledMessageJob scheduledMessageJob = new ScheduledMessageJob();
    scheduledMessageJob.initialize(task);

    scheduledMessageJob.execute();

    verify(adHocSMSInputSourceProcessorService).getAdHocSMSData(any(AdHocSMSInputSourceProcessorContext.class));
    verify(scheduleAdHocSMSesService).scheduleAdHocSMSes(anyListOf(AdHocSMSData.class));
    verify(schedulerService).saveTaskDefinition(task);
    verify(schedulerService).scheduleTask(task);
  }

  private TaskDefinition buildScheduledMessageTask() {
    TaskDefinition task = new TaskDefinition();
    task.setName("Test Name");
    task.setTaskClass(ScheduledMessageJob.class.getName());
    task.setStartOnStartup(false);
    task.setProperties(wrapByMap(ScheduledMessageJob.JOB_PROPERTIES_KEY,
        ScheduledMessageDetailsUtil.toJSONString(buildScheduledMessageDetailsObject())));

    return task;
  }

  private Map<String, String> wrapByMap(String key, String value) {
    Map<String, String> map = new HashMap<>();
    map.put(key, value);
    return map;
  }

  private ScheduledMessageDetails buildScheduledMessageDetailsObject() {
    ScheduledMessageDetails scheduledMessageDetails = new ScheduledMessageDetails();
    scheduledMessageDetails.setName("testName");
    scheduledMessageDetails.setDescription("testDescription");
    scheduledMessageDetails.setReportUuid("a4236158-457e-11ed-b38c-0242ac140002");
    scheduledMessageDetails.setCronExpression("0 */15 * ? * *");

    return scheduledMessageDetails;
  }

  private AdHocSMSData buildAdHocSMSDataObject() {
    AdHocSMSData smsData = new AdHocSMSData();
    smsData.setPhone("111222333");
    smsData.setSmsText("test sms text");
    smsData.setConfig("test config");
    smsData.setContactTime("2022-20-25 10:00");

    return smsData;
  }
}
