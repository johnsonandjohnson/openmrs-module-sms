package org.openmrs.module.sms.web.controller;

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

@Controller
@RequestMapping(value = "/sms")
public class SmsRecordController {

	private static final Log LOGGER = LogFactory.getLog(SmsRecordController.class);

	@Autowired
	@Qualifier("smsAuditService")
	private SmsAuditService smsAuditService;

	@RequestMapping(value = "/log", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public SmsRecordsPageable getAll(GridSettings gridSettings) {
		PagingInfo page = gridSettings.toPageInfo();
		SmsRecordSearchCriteria searchCriteria = gridSettings.toSmsRecordSearchCriteria();
		return new SmsRecordsPageable(page, smsAuditService.findPageableByCriteria(page, searchCriteria));
	}

	/**
	 * Handles an exception in the controller. The message of the exception will be returned as the HTTP body.
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
