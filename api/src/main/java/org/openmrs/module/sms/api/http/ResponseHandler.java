package org.openmrs.module.sms.api.http;

import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.configs.Config;
import org.openmrs.module.sms.api.event.SmsEvent;
import org.openmrs.module.sms.api.service.OutgoingSms;
import org.openmrs.module.sms.api.templates.Response;
import org.openmrs.module.sms.api.templates.Template;
import org.openmrs.notification.AlertService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Figures out success or failure from an sms provider response. It handles response and updates its internal
 * collections of audit records and events, which should be then retrieved and handled by the code using the handler.
 */
public abstract class ResponseHandler {
    private static final String SMS_MODULE = "motech-sms";
    private static final Log LOGGER = LogFactory.getLog(ResponseHandler.class);
    private Template template;
    private Config config;
    private Response templateOutgoingResponse;
    private List<SmsEvent> events = new ArrayList<>();
    private List<SmsRecord> auditRecords = new ArrayList<>();

    @Autowired
    private AlertService alertService;

    /**
     * Constructs an instance using the provided template and configuration.
     * @param template the template to use
     * @param config the configuration to use
     */
    ResponseHandler(Template template, Config config) {
        this.template = template;
        this.config = config;
        templateOutgoingResponse = template.getOutgoing().getResponse();
    }

    /**
     * Handles the response for an outgoing sms.
     * @param sms the outgoing sms
     * @param response the response from the provider, as string
     * @param headers the response headers
     */
    public abstract void handle(OutgoingSms sms, String response, Header[] headers);

    /**
     * Formats the SMS message so that it can get logged, by escaping all newline characters.
     * @param sms the outgoing sms
     * @return the message from the SMS, with newline characters escaped
     */
    public String messageForLog(OutgoingSms sms) {
        return sms.getMessage().replace("\n", "\\n");
    }

    /**
     * Returns SmsEvents to be sent. These events are created during parsing of responses.
     * @return the list of events to publish
     */
    public List<SmsEvent> getEvents() {
        return events;
    }

    /**
     * Returns audit records to be saved. These records are created during parsing of responses.
     * @return the list of events to publish
     */
    public List<SmsRecord> getAuditRecords() {
        return auditRecords;
    }

    /**
     * @return the template that will be used when handling the responses
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * @param template the template that will be used when handling the responses
     */
    protected void setTemplate(Template template) {
        this.template = template;
    }

    /**
     * @return the configuration that will be used when handling the responses
     */
    protected Config getConfig() {
        return config;
    }

    /**
     * @param config the template that will be used when handling the responses
     */
    protected void setConfig(Config config) {
        this.config = config;
    }

    /**
     * @return the ougoing response from the template used for handling the responses
     */
    protected Response getTemplateOutgoingResponse() {
        return templateOutgoingResponse;
    }

    /**
     * @return the logger instance for this class
     */
    protected Log getLogger() {
        return LOGGER;
    }

    /**
     * Creates a warning message using {@link AlertService}.
     * @param message the message to log
     */
    public void warn(String message) {
        alertService.notifySuperUsers(String.format("%s - %s", SMS_MODULE, message), null);
    }
}
