/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.sms.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.sms.api.audit.SmsAuditService;
import org.openmrs.module.sms.api.audit.SmsRecordSearchCriteria;
import org.openmrs.module.sms.api.web.GridSettings;
import org.openmrs.module.sms.api.web.dto.SmsRecordsPageable;
import org.openmrs.module.sms.domain.PagingInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.net.HttpURLConnection;

@Api(value = "Fetch SMS records", tags = {"REST API to fetch SMS records"})
@Controller
@RequestMapping(value = "/sms")
public class SmsRecordController extends RestController {

    private static final Log LOGGER = LogFactory.getLog(SmsRecordController.class);

    @Autowired
    @Qualifier("sms.SmsAuditService")
    private SmsAuditService smsAuditService;

    @ApiOperation(value = "Fetch SMS records", notes = "Fetch SMS records")
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "On successful fetching SMS records"),
            @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failure to fetch SMS records"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Error while fetching SMS records")})
    @RequestMapping(value = "/log", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SmsRecordsPageable getAll(@ApiParam(name = "gridSettings", value = "Models the audit log filter settings UI")
            GridSettings gridSettings) {
        PagingInfo page = gridSettings.toPageInfo();
        SmsRecordSearchCriteria searchCriteria = gridSettings.toSmsRecordSearchCriteria();
        return new SmsRecordsPageable(page, smsAuditService.findPageableByCriteria(page, searchCriteria));
    }

    /**
     * Handles an exception in the controller. The message of the exception will be returned as the HTTP body.
     *
     * @param e the exception to handle
     * @return the message from the exception, to be treated as the response body
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        LOGGER.error("Error in SMS Records Controller", e);
        return e.getMessage();
    }
}
