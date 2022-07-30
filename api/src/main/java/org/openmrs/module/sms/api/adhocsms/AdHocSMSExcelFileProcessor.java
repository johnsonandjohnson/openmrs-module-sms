package org.openmrs.module.sms.api.adhocsms;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.module.sms.api.data.AdHocSMSData;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdHocSMSExcelFileProcessor {

  public static final List<String> EXCEL_FILES_EXTENSION = Arrays.asList("xls", "xlsx");

  private static final Log LOGGER = LogFactory.getLog(AdHocSMSExcelFileProcessor.class);

  private static final int NUMBER_OF_COLUMNS = 5;

  private static final int PHONE_NUMBER_COLUMN_INDEX = 0;

  private static final int SMS_TEXT_COLUMN_INDEX = 1;

  private static final int PARAMETERS_COLUMN_INDEX = 2;

  private static final int CONTACT_TIME_COLUMN_INDEX = 3;

  private static final int PROVIDER_COLUMN_INDEX = 4;

  public List<AdHocSMSData> getSMSDataFromExcelFile(InputStream inputStream, String sheetName) {
    List<AdHocSMSData> smsDataList = new ArrayList<>();
    try (Workbook workbook = WorkbookFactory.create(inputStream)) {
      Sheet sheet = getSheet(workbook, sheetName);
      for (Row row : sheet) {
        if (isRowInvalid(row)) {
          LOGGER.warn(String.format(
              "Row number %d has invalid format. It will be skipped during processing",
              row.getRowNum() + 1));
          continue;
        }
        smsDataList.add(buildAdHocSMSObject(row));
      }
    } catch (IOException ex) {
      LOGGER.error("Error while processing Excel file");
    }

    return smsDataList;
  }

  private Sheet getSheet(Workbook workbook, String sheetName) {
    Sheet sheet;
    if (StringUtils.isNotBlank(sheetName)) {
      sheet = workbook.getSheet(sheetName);
    } else {
      sheet = workbook.getSheetAt(0);
    }

    return sheet;
  }

  private boolean isRowInvalid(Row row) {
    if (row.getLastCellNum() != NUMBER_OF_COLUMNS) {
      return true;
    }

    return isHeaderRow(row);
  }

  private boolean isHeaderRow(Row row) {
    for (Cell cell : row) {
      if (AdHocSMSData.COLUMN_NAMES.contains(new DataFormatter().formatCellValue(cell))) {
        return true;
      }
    }

    return false;
  }

  private AdHocSMSData buildAdHocSMSObject(Row row) {
    DataFormatter dataFormatter = new DataFormatter();
    AdHocSMSData adHocSMSData = new AdHocSMSData();
    for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
      if (i == PHONE_NUMBER_COLUMN_INDEX) {
        adHocSMSData.setPhone(dataFormatter.formatCellValue(row.getCell(i)));
      }
      if (i == SMS_TEXT_COLUMN_INDEX) {
        adHocSMSData.setSmsText(dataFormatter.formatCellValue(row.getCell(i)));
      }
      if (i == PARAMETERS_COLUMN_INDEX) {
        adHocSMSData.setParameters(
            convertParametersStringToMap(dataFormatter.formatCellValue(row.getCell(i))));
      }
      if (i == CONTACT_TIME_COLUMN_INDEX) {
        adHocSMSData.setContactTime(dataFormatter.formatCellValue(row.getCell(i)));
      }
      if (i == PROVIDER_COLUMN_INDEX) {
        adHocSMSData.setConfig(dataFormatter.formatCellValue(row.getCell(i)));
      }
    }

    return adHocSMSData;
  }

  private Map<String, Object> convertParametersStringToMap(String value) {
    Map<String, Object> parametersMap = new HashMap<>();
    try {
      if (StringUtils.isNotBlank(value)) {
        parametersMap = new ObjectMapper().readValue(value, Map.class);
      }
    } catch (IOException ex) {
      LOGGER.error("Error occurred while converting String value into map");
    }

    return parametersMap;
  }
}
