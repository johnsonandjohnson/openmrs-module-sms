package org.openmrs.module.sms.api.util;

/**
 * Class holding constant String values related to permissions and roles.
 */
public final class Constants {

    public static final String VIEW_SMS_LOGS_PERMISSION = "viewSMSLogs";

    public static final String UI_CONFIG = "custom-ui.js";

    private static final String SMS_CONFIGS_FILE_NAME = "sms-configs.json";

    public static final String CONFIG_DIR = "cfl_config";

    public static final String CONFIG_FILE_PATH = CONFIG_DIR + "/" + SMS_CONFIGS_FILE_NAME;

    private Constants() {
    }

}
