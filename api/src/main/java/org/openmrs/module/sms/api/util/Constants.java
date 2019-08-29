package org.openmrs.module.sms.api.util;

/**
 * Class holding constant String values related to permissions and roles.
 */
public final class Constants {

    public static final String VIEW_SMS_LOGS_PERMISSION = "viewSMSLogs";

    public static final String UI_CONFIG = "custom-ui.js";

    public static final String SMS_CONFIGS_FILE_NAME = "sms-configs.json";

    public static final String CONFIG_DIR = "cfl_config";

    public static final String CONFIG_FILE_PATH = CONFIG_DIR + "/" + SMS_CONFIGS_FILE_NAME;

    public static final String FILE_PATH = "file.path";

    public static final String SMS_SERVER_URL = "sms_server_url";

    public static final String DEFAULT_SMS_SERVER_URL = "";

    private Constants() {
    }

}
