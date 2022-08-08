package org.openmrs.module.sms.api.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.sms.api.data.AdHocSMSData;
import org.openmrs.module.sms.api.event.SmsEvent;
import org.openmrs.module.sms.api.service.ScheduleAdHocSMSesService;
import org.openmrs.module.sms.api.service.SmsSchedulerService;
import org.openmrs.module.sms.api.task.SmsScheduledTask;
import org.openmrs.module.sms.api.util.DateUtil;
import org.openmrs.module.sms.api.util.OpenMRSIDGeneratorUtil;
import org.openmrs.module.sms.api.util.SmsEventSubjectsConstants;
import org.openmrs.module.sms.api.util.SmsEventsHelper;

public class ScheduleAdHocSMSesServiceImpl implements ScheduleAdHocSMSesService {

  private static final Log LOGGER = LogFactory.getLog(ScheduleAdHocSMSesServiceImpl.class);

  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

  @Override
  public void scheduleAdHocSMSes(List<AdHocSMSData> smsData) {
    SmsSchedulerService smsSchedulerService = Context.getService(SmsSchedulerService.class);
    smsData.forEach(sms -> scheduleSMS(sms, smsSchedulerService));
  }

  private void scheduleSMS(AdHocSMSData smsData, SmsSchedulerService smsSchedulerService) {
    SmsEvent smsEvent = buildSMSEvent(smsData);
    Date deliveryTime = getSMSDeliveryDate(smsData.getContactTime());
    if (deliveryTime != null) {
      smsSchedulerService.safeScheduleRunOnceJob(smsEvent, deliveryTime, new SmsScheduledTask());
    } else {
      LOGGER.warn(String.format(
          "Scheduling SMS will be skipped because following date: %s has invalid format",
          smsData.getContactTime()));
    }
  }

  private SmsEvent buildSMSEvent(AdHocSMSData smsData) {
    return SmsEventsHelper.outboundEvent(
        SmsEventSubjectsConstants.SCHEDULED,
        smsData.getConfig(),
        Collections.singletonList(smsData.getPhone()),
        smsData.getSmsText().replace("\n", " "),
        OpenMRSIDGeneratorUtil.generateOpenMRSID(),
        null,
        null,
        null,
        null,
        smsData.getParameters()
    );
  }

  private Date getSMSDeliveryDate(String dateTime) {
    return DateUtil.parse(dateTime, DATE_FORMAT);
  }
}
