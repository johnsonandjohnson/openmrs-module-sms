package org.openmrs.module.sms.api.audit;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.sms.api.dao.SmsRecordDao;
import org.openmrs.module.sms.builder.SmsRecordSearchCriteriaBuilder;
import org.openmrs.module.sms.domain.PagingInfo;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SmsAuditServiceImplTest extends BaseModuleContextSensitiveTest {

    private static final String XML_DATA_SET_PATH = "datasets/";
    private static final int EXPECTED_ONE = 1;
    private static final int EXPECTED_TWO = 2;
    private static final String FIRST_UUID = "16f280e0-3e91-48b4-acb2-c29c30595b45";
    private static final String SECOND_UUID = "e1c969a8-8c87-4058-ab2c-d3da042f4def";
    public static final int PAGE = 1;
    public static final int PAGE_SIZE = 1;

    @Autowired
    @Qualifier("sms.SmsRecordDao")
    private SmsRecordDao smsRecordDao;

    @Autowired
    @Qualifier("smsAuditService")
    private SmsAuditService smsAuditService;

    @Before
    public void setUp() throws Exception {
        executeDataSet(XML_DATA_SET_PATH + "SmsRecordDataSet.xml");
    }

    @Test
    public void findAllSmsRecordsShouldReturnExpectedResults() {
        List<SmsRecord> actual = smsAuditService.findAllSmsRecords();
        assertThat(actual.size(), is(EXPECTED_ONE));
        assertThat(actual.get(0).getUuid(), is(FIRST_UUID));
    }

    @Test
    public void findAllSmsRecordsByCriteriaShouldReturnExpectedResults() {
        SmsRecordSearchCriteria criteria = prepareCriteria();
        SmsRecords actual = smsAuditService.findAllSmsRecords(criteria);
        assertThat(actual.getCount(), is(EXPECTED_TWO));
        assertThat(actual.getRecords().get(0).getUuid(), is(FIRST_UUID));
        assertThat(actual.getRecords().get(1).getUuid(), is(SECOND_UUID));
    }

    @Test
    public void countAllSmsRecordsShouldReturnExpectedResult() {
        SmsRecordSearchCriteria criteria = prepareCriteria();
        long actual = smsAuditService.countAllSmsRecords(criteria);
        assertThat(actual, is((long) EXPECTED_TWO));
    }

    @Test
    public void findPageableByCriteria() {
        PagingInfo pagingInfo = new PagingInfo(PAGE, PAGE_SIZE);
        SmsRecordSearchCriteria criteria = prepareCriteria();
        SmsRecords actual = smsAuditService.findPageableByCriteria(pagingInfo, criteria);
        assertThat(actual.getRecords().size(), is(EXPECTED_ONE));
        assertThat(actual.getRecords().get(0).getUuid(), is(FIRST_UUID));
        assertThat(actual.getCount(), is(EXPECTED_TWO));
    }

    private SmsRecordSearchCriteria prepareCriteria() {
        return new SmsRecordSearchCriteriaBuilder()
                .withConfig("nexmo")
                .withErrorMessage(null)
                .withInterval(null)
                .withMessageContent(null)
                .withOrder(null)
                .withPhoneNumber(null)
                .withProviderStatus(null)
                .build();
    }
}
