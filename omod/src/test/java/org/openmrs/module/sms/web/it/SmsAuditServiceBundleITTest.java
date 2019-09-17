package org.openmrs.module.sms.web.it;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.openmrs.module.sms.api.audit.SmsAuditService;
import org.openmrs.module.sms.api.audit.SmsDirection;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.dao.SmsRecordDao;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Verify SmsAuditService present & functional.
 */
public class SmsAuditServiceBundleITTest extends BaseModuleWebContextSensitiveTest {

	@Autowired
	@Qualifier("sms.SmsRecordDao")
	private SmsRecordDao smsRecordDao;

	@Autowired
	@Qualifier("smsAuditService")
	private SmsAuditService smsAuditService;

	@After
	public void cleanUpDatabase() throws Exception {
		this.deleteAllData();
	}

	@Test
	public void verifyServiceFunctional() {
		SmsRecord smsRecord = new SmsRecord("config", SmsDirection.INBOUND, "from", "message", DateTime.now(),
				"PENDING", "status", "mid", "pid", null);
		smsRecordDao.create(smsRecord);

		List<SmsRecord> smsRecords = smsAuditService.findAllSmsRecords();
		assertEquals(1, smsRecords.size());
		assertEquals(smsRecords.get(0), smsRecord);
	}
}
