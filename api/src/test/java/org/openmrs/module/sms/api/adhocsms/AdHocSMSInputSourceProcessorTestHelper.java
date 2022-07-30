package org.openmrs.module.sms.api.adhocsms;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import org.openmrs.module.sms.api.data.AdHocSMSData;

public class AdHocSMSInputSourceProcessorTestHelper {

  protected static final String AD_HOC_SMS_RESOURCE_BASE_PATH = "adhocsms/";

  protected void verifyResults(AdHocSMSData adHocSMSData, String phoneNumber, String smsText,
      String contactTime, String config) {
    assertEquals(phoneNumber, adHocSMSData.getPhone());
    assertEquals(smsText, adHocSMSData.getSmsText());
    assertEquals(contactTime, adHocSMSData.getContactTime());
    assertEquals(config, adHocSMSData.getConfig());
  }

  protected InputStream getInputStream(String fileName) {
    return this.getClass().getClassLoader().getResourceAsStream(fileName);
  }
}
