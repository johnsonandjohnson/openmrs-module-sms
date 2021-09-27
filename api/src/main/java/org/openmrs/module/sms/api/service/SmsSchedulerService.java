package org.openmrs.module.sms.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sms.api.event.SmsEvent;
import org.openmrs.module.sms.api.task.AbstractSmsTask;

import java.util.Date;

public interface SmsSchedulerService extends OpenmrsService {

    void safeScheduleRunOnceJob(SmsEvent event, Date startTime, AbstractSmsTask task);
}
