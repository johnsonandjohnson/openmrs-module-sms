package org.openmrs.module.sms.web.controller.it;

import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.module.sms.api.audit.SmsAuditService;
import org.openmrs.module.sms.api.audit.SmsRecordSearchCriteria;
import org.openmrs.module.sms.domain.PagingInfo;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Verify SmsAuditService when unauthorized.
 */
public class SmsAuditServiceBundleUnauthorizedITTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    @Qualifier("smsAuditService")
    private SmsAuditService smsAuditService;

    @Override
    public void authenticate() {
        // disable default authentication
    }

    @Test(expected = APIAuthenticationException.class)
    public void shouldForbidUnauthorizedUserToFind() {
        smsAuditService.findPageableByCriteria(
                Mockito.mock(PagingInfo.class),
                Mockito.mock(SmsRecordSearchCriteria.class)
        );
    }
}
