/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.sms.api.service;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.event.Event;
import org.openmrs.event.EventMessage;
import org.openmrs.module.sms.api.event.SmsEvent;
import org.openmrs.module.sms.api.util.DateUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class SmsEventServiceImpl extends BaseOpenmrsService implements SmsEventService {

    @Override
    public void sendEventMessage(SmsEvent event) {
        Event.fireEvent(event.getSubject(), convertParamsToEventMessage(event.getParameters()));
    }

    private EventMessage convertParamsToEventMessage(Map<String, Object> params) {
        EventMessage eventMessage = new EventMessage();

        for (Map.Entry<String,Object> map : params.entrySet()) {
            String key = map.getKey();
            if (params.get(key) instanceof Date) {
                eventMessage.put(key, DateUtil.dateToString((Date) params.get(key)));
            } else {
                eventMessage.put(key, (Serializable) params.get(key));
            }
        }

        return eventMessage;
    }
}
