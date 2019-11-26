package org.openmrs.module.sms.api.task;

import org.openmrs.api.context.Context;
import org.openmrs.module.sms.api.exception.SmsRuntimeException;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.tasks.AbstractTask;

public abstract class AbstractSmsTask extends AbstractTask {

	@Override
	public void execute() {
		try {
			executeTask();
		} finally {
			shutdownTask();
		}
	}

	protected abstract void executeTask();

	private void shutdownTask() {
		try {
			Context.getSchedulerService().shutdownTask(getTaskDefinition());
		} catch (SchedulerException ex) {
			throw new SmsRuntimeException(ex);
		}
	}
}
