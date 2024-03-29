/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.sms.api.task;

import org.openmrs.api.context.Context;
import org.openmrs.module.sms.api.event.SmsEvent;
import org.openmrs.module.sms.api.http.SmsHttpService;
import org.openmrs.module.sms.api.service.OutgoingSms;
import org.openmrs.module.sms.api.util.DateUtil;

public class SmsScheduledTask extends AbstractSmsTask {

    @Override
    public void executeTask() {
        Context.getRegisteredComponent("sms.SmsHttpService", SmsHttpService.class)
                .send(new OutgoingSms(new SmsEvent(SmsEvent.convertProperties(getTaskDefinition().getProperties()))));
        getTaskDefinition().setLastExecutionTime(DateUtil.now());
    }
}
