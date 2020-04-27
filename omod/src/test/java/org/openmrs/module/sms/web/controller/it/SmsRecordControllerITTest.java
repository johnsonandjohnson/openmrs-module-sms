package org.openmrs.module.sms.web.controller.it;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.dao.SmsRecordDao;
import org.openmrs.module.sms.api.web.dto.SmsRecordsPageable;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
public class SmsRecordControllerITTest extends BaseModuleWebContextSensitiveTest {

    private static final String TEST_MESSAGE = "Test Message";

    private static final String TEST_MESSAGE2 = "Message 2";

    private static final String TEST_MESSAGE3 = "Content 3";

    private static final String PHONE_NUMBER = "531672341";

    private static final String PHONE_NUMBER2 = "987254321";

    private static final String PHONE_NUMBER3 = "354165471";

    private static final String CONFIG = "config";

    private static final String CONFIG2 = "nexmo";

    private static final String CONFIG3 = "twilio";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SmsRecordDao smsRecordDao;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Date timestamp = createDate(2010, Calendar.NOVEMBER, 16, 15, 12, 59);
        smsRecordDao.saveOrUpdate(createSmsRecord(TEST_MESSAGE, PHONE_NUMBER, CONFIG, timestamp));
        timestamp = createDate(2019, Calendar.SEPTEMBER, 30, 8, 22, 15);
        smsRecordDao.saveOrUpdate(createSmsRecord(TEST_MESSAGE2, PHONE_NUMBER2, CONFIG2, timestamp));
        timestamp = createDate(2015, Calendar.DECEMBER, 24, 18, 53, 42);
        smsRecordDao.saveOrUpdate(createSmsRecord(TEST_MESSAGE3, PHONE_NUMBER3, CONFIG3, timestamp));
    }

    @After
    public void cleanUpDatabase() throws Exception {
        this.deleteAllData();
    }

    @Test
    public void shouldReturnAllRecords() throws Exception {

        MvcResult result = mockMvc.perform(get("/sms/log/"))
                .andExpect(status().is(HttpStatus.OK.value())).andReturn();

        assertThat(result, is(notNullValue()));
        SmsRecordsPageable smsRecordsPageable = getSmsRecordsPageable(result);
        assertThat(smsRecordsPageable.getTotalRecords(), equalTo(3));
        assertThat(smsRecordsPageable.getPageIndex(), equalTo(1));
        assertThat(smsRecordsPageable.getPageSize(), equalTo(100));
    }

    @Test
    public void shouldFilterByTimestamp() throws Exception {

        MvcResult result = mockMvc.perform(get("/sms/log/")
                .param("timeTo", "2016-01-24 20:05:00"))
                .andExpect(status().is(HttpStatus.OK.value())).andReturn();

        assertThat(result, is(notNullValue()));
        SmsRecordsPageable smsRecordsPageable = getSmsRecordsPageable(result);
        assertThat(smsRecordsPageable.getTotalRecords(), equalTo(2));
        assertThat(smsRecordsPageable.getPageIndex(), equalTo(1));
        assertThat(smsRecordsPageable.getPageSize(), equalTo(100));
    }

    @Test
    public void shouldFilterByPhoneNumber() throws Exception {

        MvcResult result = mockMvc.perform(get("/sms/log/")
                .param("phoneNumber", "531"))
                .andExpect(status().is(HttpStatus.OK.value())).andReturn();

        assertThat(result, is(notNullValue()));
        SmsRecordsPageable smsRecordsPageable = getSmsRecordsPageable(result);
        assertThat(smsRecordsPageable.getTotalRecords(), equalTo(1));
        assertThat(smsRecordsPageable.getPageIndex(), equalTo(1));
        assertThat(smsRecordsPageable.getPageSize(), equalTo(100));
    }

    @Test
    public void shouldFilterByConfig() throws Exception {

        MvcResult result = mockMvc.perform(get("/sms/log/")
                .param("config", "wilio"))
                .andExpect(status().is(HttpStatus.OK.value())).andReturn();

        assertThat(result, is(notNullValue()));
        SmsRecordsPageable smsRecordsPageable = getSmsRecordsPageable(result);
        assertThat(smsRecordsPageable.getTotalRecords(), equalTo(1));
        assertThat(smsRecordsPageable.getPageIndex(), equalTo(1));
        assertThat(smsRecordsPageable.getPageSize(), equalTo(100));
    }

    @Test
    public void shouldFilterByMessage() throws Exception {

        MvcResult result = mockMvc.perform(get("/sms/log/")
                .param("messageContent", "Message"))
                .andExpect(status().is(HttpStatus.OK.value())).andReturn();

        assertThat(result, is(notNullValue()));
        SmsRecordsPageable smsRecordsPageable = getSmsRecordsPageable(result);
        assertThat(smsRecordsPageable.getTotalRecords(), equalTo(2));
        assertThat(smsRecordsPageable.getPageIndex(), equalTo(1));
        assertThat(smsRecordsPageable.getPageSize(), equalTo(100));
    }

    @Test
    public void shouldFilterByMessageAndPhoneNumber() throws Exception {

        MvcResult result = mockMvc.perform(get("/sms/log/")
                .param("messageContent", "Message")
                .param("phoneNumber", "432"))
                .andExpect(status().is(HttpStatus.OK.value())).andReturn();

        assertThat(result, is(notNullValue()));
        SmsRecordsPageable smsRecordsPageable = getSmsRecordsPageable(result);
        assertThat(smsRecordsPageable.getTotalRecords(), equalTo(1));
        assertThat(smsRecordsPageable.getPageIndex(), equalTo(1));
        assertThat(smsRecordsPageable.getPageSize(), equalTo(100));
    }

    private SmsRecordsPageable getSmsRecordsPageable(MvcResult result) throws java.io.IOException {
        return new ObjectMapper().readValue(result.getResponse().getContentAsString(), SmsRecordsPageable.class);
    }

    private SmsRecord createSmsRecord(String message, String phoneNumber, String config, Date timestamp) {
        SmsRecord record = new SmsRecord();
        record.setMessageContent(message);
        record.setPhoneNumber(phoneNumber);
        record.setConfig(config);
        record.setTimestamp(timestamp);
        return record;
    }

    private Date createDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return calendar.getTime();
    }
}
