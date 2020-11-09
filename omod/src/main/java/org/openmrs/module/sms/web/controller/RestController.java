package org.openmrs.module.sms.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.module.sms.api.audit.constants.DeliveryStatuses;
import org.openmrs.module.sms.api.exception.SmsRuntimeException;
import org.openmrs.module.sms.api.templates.Template;
import org.openmrs.module.sms.api.web.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    public Map<String, String> getCombinedParams(Map<String, String> params, Map<String, Object> bodyParam) {
        Map<String, String> paramMap = new HashMap<>();
        if (params != null) {
            paramMap.putAll(params);
        }
        ObjectMapper mapper;
        if (bodyParam != null) {
            for (Map.Entry<String, Object> en : bodyParam.entrySet()) {
                mapper = new ObjectMapper();
                if (en.getValue().getClass().equals(String.class)) {
                    paramMap.put(en.getKey(), en.getValue().toString());
                } else {
                    try {
                        String json = mapper.writeValueAsString(en.getValue());
                        paramMap.put(en.getKey(), json);
                    } catch (IOException e) {
                        throw new SmsRuntimeException("invalid object", e);
                    }
                }
            }
        }
        return paramMap;
    }

    public String getSender(Map<String, String> params, Template template) {
        String sender = null;
        if (params.containsKey(template.getIncoming().getSenderKey())) {
            sender = params.get(template.getIncoming().getSenderKey());
            if (template.getIncoming().hasSenderRegex()) {
                sender = template.getIncoming().extractSender(sender);
            }
        }
        return sender;
    }

    public String getMessage(Map<String, String> params, Template template) {
        return params.get(template.getIncoming().getMessageKey());
    }

    public String getMsgId(Map<String, String> params, Template template) {
        return params.get(template.getIncoming().getMsgIdKey());
    }

    public String getStatus(Map<String, String> params, Template template) {
        return template.getStatus().hasStatusKey() && params.containsKey(template.getStatus().getStatusKey()) ? params.get(template.getStatus().getStatusKey()) : DeliveryStatuses.RECEIVED;
    }
}
