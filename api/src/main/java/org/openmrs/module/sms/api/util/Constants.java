package org.openmrs.module.sms.api.util;

public final class Constants {

    public static final String VIEW_SMS_LOGS_PERMISSION = "viewSMSLogs";

    public static final String UI_CONFIG = "custom-ui.js";

    public static final String SMS_CONFIGS_FILE_NAME = "sms-configs.json";

    public static final String CONFIG_DIR = "cfl_config";

    public static final String CONFIG_FILE_PATH = CONFIG_DIR + "/" + SMS_CONFIGS_FILE_NAME;

    public static final String FILE_PATH = "file.path";

    public static final String SMS_SERVER_URL = "sms_server_url";

    public static final String DEFAULT_SMS_SERVER_URL = "http://localhost:8080/openmrs";

    public static final String PARAM_JOB_ID = "JobID";

    public static final String SMS_DEFAULT_MILLISECONDS_BETWEEN_MESSAGES_KEY = "sms.default.millisecond_between_messages";

    public static final String SMS_DEFAULT_MAX_SMS_SIZE_KEY = "sms.default.max_sms_size";

    public static final String SMS_DEFAULT_MAX_RECIPIENT_KEY = "sms.default.max_recipient";

    public static final String SMS_DEFAULT_RECIPIENT_SEPARATOR_KEY = "sms.default.recipient_separator";

    public static final String SMS_DEFAULT_MILLISECONDS_BETWEEN_MESSAGES_VALUE = "1";

    public static final String SMS_DEFAULT_MAX_SMS_SIZE_VALUE = "160";

    public static final String SMS_DEFAULT_MAX_RECIPIENT_VALUE = "1";

    public static final String SMS_DEFAULT_RECIPIENT_SEPARATOR_VALUE = ",";

    private Constants() {
    }

}
