package org.openmrs.module.sms.api.service.impl;

import org.openmrs.api.context.Context;
import org.openmrs.module.sms.api.adhocsms.AdHocSMSExcelFileProcessor;
import org.openmrs.module.sms.api.adhocsms.AdHocSMSJSONFileProcessor;
import org.openmrs.module.sms.api.adhocsms.AdHocSMSSQLDataSetProcessor;
import org.openmrs.module.sms.api.data.AdHocSMSData;
import org.openmrs.module.sms.api.service.AdHocSMSInputSourceProcessorService;

import java.io.InputStream;
import java.util.List;

public class AdHocSMSInputSourceProcessorServiceImpl implements
    AdHocSMSInputSourceProcessorService {

  @Override
  public List<AdHocSMSData> getSMSDataFromExcelFile(InputStream inputStream, String sheetName) {
    return new AdHocSMSExcelFileProcessor().getSMSDataFromExcelFile(inputStream, sheetName);
  }

  @Override
  public List<AdHocSMSData> getSMSDataFromJSONFile(InputStream inputStream) {
    return new AdHocSMSJSONFileProcessor().getSMSDataFromJSONFile(inputStream);
  }

  @Override
  public List<AdHocSMSData> getSMSDataFromSQLDataSet(String dataSetUuid) {
    return Context.getRegisteredComponent("sms.adHocSMSSQLDataSetProcessor",
        AdHocSMSSQLDataSetProcessor.class).getSMSDataFromSQLDataSet(dataSetUuid);
  }
}
