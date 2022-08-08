package org.openmrs.module.sms.api.adhocsms;

import java.io.InputStream;
import java.util.Map;

/**
 * Context class for ad hoc SMS input source processors
 */
public class AdHocSMSInputSourceProcessorContext {

  private InputStream file;

  private Map<String, String> options;

  public AdHocSMSInputSourceProcessorContext(InputStream file, Map<String, String> options) {
    this.file = file;
    this.options = options;
  }

  public InputStream getFile() {
    return file;
  }

  public void setFile(InputStream file) {
    this.file = file;
  }

  public Map<String, String> getOptions() {
    return options;
  }

  public void setOptions(Map<String, String> options) {
    this.options = options;
  }
}
