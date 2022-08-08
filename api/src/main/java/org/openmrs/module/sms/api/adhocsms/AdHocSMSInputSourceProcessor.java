package org.openmrs.module.sms.api.adhocsms;

import java.util.List;
import org.openmrs.module.sms.api.data.AdHocSMSData;

/**
 * Common interface for ad hoc SMS input source processors
 */
public interface AdHocSMSInputSourceProcessor {

  String EXTENSION_FILE_PROP_NAME = "fileExtension";

  /**
   * Checks if ad hoc SMS data should be fetched based on context data
   *
   * @param context input source context data
   * @return true/false
   */
  boolean shouldProcessData(AdHocSMSInputSourceProcessorContext context);

  /**
   * Gets ad hoc SMS data based on context data
   *
   * @param context input source context data
   * @return list of {@link AdHocSMSData} objects
   */
  List<AdHocSMSData> getAdHocSMSData(AdHocSMSInputSourceProcessorContext context);

}
