package org.openmrs.module.sms.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.sms.api.event.SmsEvent;
import org.openmrs.module.sms.api.exception.SmsRuntimeException;
import org.openmrs.module.sms.api.task.SmsScheduledTask;
import org.openmrs.module.sms.api.util.Constants;
import org.openmrs.module.sms.api.util.DateUtil;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SmsSchedulerServiceTest {

    private static String TEST_SUBJECT = "subject";
    private static String JOB_ID_VALUE = "2";

    @Mock
    private SchedulerService schedulerService;

    @InjectMocks
    private SmsSchedulerService smsSchedulerService = new SmsSchedulerServiceImpl();

    private ArgumentCaptor<TaskDefinition> taskDefinitionCaptor;
    private SmsEvent smsEvent;

    @Before
    public void setUp() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_JOB_ID, JOB_ID_VALUE);
        smsEvent = new SmsEvent(TEST_SUBJECT, parameters);

        taskDefinitionCaptor = ArgumentCaptor.forClass(TaskDefinition.class);
    }

    @Test
    public void shouldProperlyScheduleTask() throws Exception {
        when(schedulerService.getTaskByName(TEST_SUBJECT + "-" + JOB_ID_VALUE)).thenReturn(new TaskDefinition());

        smsSchedulerService.safeScheduleRunOnceJob(smsEvent, DateUtil.now(), new SmsScheduledTask());

        verify(schedulerService).shutdownTask(any(TaskDefinition.class));
        verify(schedulerService).scheduleTask(taskDefinitionCaptor.capture());

        TaskDefinition captured = taskDefinitionCaptor.getValue();
        assertThat(captured.getName(), equalTo(TEST_SUBJECT + "-" + JOB_ID_VALUE));
        assertThat(captured.getTaskClass(), equalTo(SmsScheduledTask.class.getName()));
        assertThat(captured.getStartTime(), notNullValue());
        assertThat(captured.getStartOnStartup(), equalTo(false));
        assertThat(captured.getRepeatInterval(), equalTo(0L));

        Map<String, String> properties = new HashMap<>();
        properties.put(Constants.PARAM_JOB_ID, JOB_ID_VALUE);
        assertThat(captured.getProperties(), equalTo(properties));
    }

    @Test
    public void shouldScheduleEvenIfUnScheduleFailed() throws Exception {
        when(schedulerService.getTaskByName(TEST_SUBJECT + "-" + JOB_ID_VALUE)).thenReturn(new TaskDefinition());
        doThrow(SchedulerException.class).when(schedulerService).shutdownTask(any(TaskDefinition.class));

        smsSchedulerService.safeScheduleRunOnceJob(smsEvent, DateUtil.now(), new SmsScheduledTask());

        verify(schedulerService).shutdownTask(any(TaskDefinition.class));
        verify(schedulerService).scheduleTask(any(TaskDefinition.class));
    }

    @Test(expected = SmsRuntimeException.class)
    public void shouldThrowExceptionIfScheduleFailed() throws Exception {
        when(schedulerService.getTaskByName(TEST_SUBJECT + "-" + JOB_ID_VALUE)).thenReturn(new TaskDefinition());
        doThrow(SchedulerException.class).when(schedulerService).shutdownTask(any(TaskDefinition.class));
        doThrow(SchedulerException.class).when(schedulerService).scheduleTask(any(TaskDefinition.class));

        smsSchedulerService.safeScheduleRunOnceJob(smsEvent, DateUtil.now(), new SmsScheduledTask());

        verify(schedulerService).shutdownTask(any(TaskDefinition.class));
        verify(schedulerService).scheduleTask(any(TaskDefinition.class));
    }
}
