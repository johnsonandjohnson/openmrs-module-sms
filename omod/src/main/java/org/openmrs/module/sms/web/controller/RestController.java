package org.openmrs.module.sms.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.module.sms.api.web.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public abstract class RestController {

    private static final Log LOGGER = LogFactory.getLog(RestController.class);
    private static final String ERR_SYSTEM = "system.error";

    /**
     * Exception handler for lack of the adequate permissions - Http status code of 403
     *
     * @param e the exception throw
     * @return a error response
     */
    @ExceptionHandler(APIAuthenticationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse handleException(APIAuthenticationException e) {
        LOGGER.error(e.getMessage(), e);
        return new ErrorResponse(ERR_SYSTEM, e.getMessage());
    }
}
