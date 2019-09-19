package org.openmrs.module.sms.api.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sms.api.event.SmsEvent;
import org.openmrs.module.sms.api.exception.SmsRuntimeException;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.util.Date;

public class SmsSchedulerServiceImpl extends BaseOpenmrsService implements SmsSchedulerService {

	private static final Log LOGGER = LogFactory.getLog(SmsSchedulerServiceImpl.class);

	private SchedulerService schedulerService;

	@Override
	public void safeScheduleRunOnceJob(SmsEvent event, Date startTime, AbstractTask task) {
		String taskName = event.generateTaskName();
		shutdownTask(taskName);

		TaskDefinition taskDefinition = new TaskDefinition();
		taskDefinition.setName(taskName);
		taskDefinition.setTaskClass(task.getClass().getName());
		taskDefinition.setStartTime(startTime);
		taskDefinition.setStartOnStartup(true);
		taskDefinition.setProperties(event.convertProperties());
		taskDefinition.setRepeatInterval(0L);

		try {
			schedulerService.scheduleTask(taskDefinition);
		} catch(SchedulerException ex) {
			throw new SmsRuntimeException(ex);
		}
	}

	private void shutdownTask(String taskName) {
		try {
			schedulerService.shutdownTask(schedulerService.getTaskByName(taskName));
		} catch(SchedulerException ex) {
			LOGGER.error(ex);
		}
	}

	public void setSchedulerService(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}
}
