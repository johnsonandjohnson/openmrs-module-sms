package org.openmrs.module.sms.api.service;

import org.openmrs.module.sms.api.data.AdHocSMSData;

import java.io.InputStream;
import java.util.List;

/**
 * AdHocSMSInputSourceProcessorService API
 * <p>
 * It is used for fetching and converting data from different input sources like files or SQL data
 * sets. Mainly needed for scheduling AdHoc SMSes.
 */
public interface AdHocSMSInputSourceProcessorService {

  /**
   * Gets data from Excel sheet and converts into {@link AdHocSMSData} class. If sheetName is blank
   * then data from first sheet from file is fetched by default.
   *
   * @param inputStream stream of input source file
   * @param sheetName   name of sheet from Excel file
   * @return list of {@link AdHocSMSData} objects
   */
  List<AdHocSMSData> getSMSDataFromExcelFile(InputStream inputStream, String sheetName);

  /**
   * Gets data from JSON file and converts into {@link AdHocSMSData} class.
   *
   * @param inputStream stream of input source file
   * @return list of {@link AdHocSMSData} objects
   */
  List<AdHocSMSData> getSMSDataFromJSONFile(InputStream inputStream);

  /**
   * Gets data from SQL data set and converts into {@link AdHocSMSData} class.
   *
   * @param dataSetUuid uuid of OpenMRS SQL data set
   * @return list of {@link AdHocSMSData} objects
   */
  List<AdHocSMSData> getSMSDataFromSQLDataSet(String dataSetUuid);
}
