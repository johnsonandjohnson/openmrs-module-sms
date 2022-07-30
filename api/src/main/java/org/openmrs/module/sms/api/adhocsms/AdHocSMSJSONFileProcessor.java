package org.openmrs.module.sms.api.adhocsms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.openmrs.module.sms.api.data.AdHocSMSData;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AdHocSMSJSONFileProcessor {

  public static final String JSON_FILE_EXTENSION = "json";

  private static final Log LOGGER = LogFactory.getLog(AdHocSMSJSONFileProcessor.class);

  public List<AdHocSMSData> getSMSDataFromJSONFile(InputStream inputStream) {
    List<AdHocSMSData> adHocSMSData = new ArrayList<>();
    try {
      adHocSMSData = new ObjectMapper().readValue(inputStream,
          new TypeReference<List<AdHocSMSData>>() {
          });
    } catch (IOException ex) {
      LOGGER.error("Error while processing JSON file");
    }

    return adHocSMSData;
  }
}
