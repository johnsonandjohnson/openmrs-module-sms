package org.openmrs.module.sms.api.adhocsms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.List;
import org.junit.Test;
import org.openmrs.module.sms.api.data.AdHocSMSData;

public class AdHocSMSJSONFileProcessorTest extends AdHocSMSInputSourceProcessorTestHelper {

  private static final String TEST_JSON_FILE_NAME = "testAdHocSMSJSONFile.json";

  @Test
  public void shouldGetAdHocSMSDataFromJSONFile() {
    InputStream inputStream = getInputStream(AD_HOC_SMS_RESOURCE_BASE_PATH + TEST_JSON_FILE_NAME);

    List<AdHocSMSData> actual = new AdHocSMSJSONFileProcessor().getSMSDataFromJSONFile(inputStream);

    assertNotNull(actual);
    assertEquals(3, actual.size());
    verifyResults(actual.get(0), "123456789", "test json msg1", "18:20", "nexmoWhatsapp1");
    verifyResults(actual.get(1), "987654321", "test json msg2", "18:21", "nexmoWhatsapp2");
    verifyResults(actual.get(2), "1122334455", "test json msg3", "18:22", "nexmoWhatsapp3");
  }
}
