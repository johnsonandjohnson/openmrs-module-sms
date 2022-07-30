package org.openmrs.module.sms.api.adhocsms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.List;
import org.junit.Test;
import org.openmrs.module.sms.api.data.AdHocSMSData;

public class AdHocSMSExcelFileProcessorTest extends AdHocSMSInputSourceProcessorTestHelper {

  private static final String TEST_EXCEL_FILE_NAME = "testAdHocSMSExcelFile.xlsx";

  @Test
  public void shouldGetAdHocSMSDataFromExcelFile() {
    InputStream inputStream = getInputStream(AD_HOC_SMS_RESOURCE_BASE_PATH + TEST_EXCEL_FILE_NAME);

    List<AdHocSMSData> actual = new AdHocSMSExcelFileProcessor().getSMSDataFromExcelFile(
        inputStream, null);

    assertNotNull(actual);
    assertEquals(3, actual.size());
    verifyResults(actual.get(0), "111222333", "test text msg first", "10:30", "textConfig1");
    verifyResults(actual.get(1), "444555666", "test text msg second", "12:30", "textConfig2");
    verifyResults(actual.get(2), "777888999", "test text third", "14:30", "textConfig3");
  }
}
