package org.openmrs.module.sms.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.module.sms.api.adhocsms.AdHocSMSExcelFileProcessor;
import org.openmrs.module.sms.api.adhocsms.AdHocSMSJSONFileProcessor;
import org.openmrs.module.sms.api.data.AdHocSMSData;
import org.openmrs.module.sms.api.service.AdHocSMSInputSourceProcessorService;
import org.openmrs.module.sms.api.service.ScheduleAdHocSMSesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

@Api(value = "Schedules AdHoc SMSes based on data from different input sources",
    tags = "REST API to schedule AdHoc SMSes based on data from different input sources")
@RestController("sms.scheduleAdHocSMSController")
@RequestMapping(value = "/scheduleAdHocSMS")
public class ScheduleAdHocSMSController {

  @Autowired
  @Qualifier("sms.adHocSMSInputSourceProcessorService")
  private AdHocSMSInputSourceProcessorService adHocSMSInputSourceProcessorService;

  @Autowired
  @Qualifier("sms.scheduleAdHocSMSesService")
  private ScheduleAdHocSMSesService scheduleAdHocSMSesService;

  @ApiOperation(value = "None", notes = "Schedules AdHoc SMSes based on data from file")
  @ApiResponses(value = {
      @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "AdHoc SMSes have been successfully scheduled"),
      @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failed to schedule AdHoc SMSes")
  })
  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public void scheduleAdHocSMSesFromFile(
      @ApiParam(name = "file", value = "file") @RequestParam(value = "file") MultipartFile file,
      @ApiParam(name = "sheetName", value = "sheetName") @RequestParam(value = "sheetName", required = false) String sheetName)
      throws IOException {
    String extensionFile = FilenameUtils.getExtension(file.getOriginalFilename());
    InputStream inputStream = file.getInputStream();

    List<AdHocSMSData> smsData = new ArrayList<>();
    if (StringUtils.equalsIgnoreCase(extensionFile,
        AdHocSMSJSONFileProcessor.JSON_FILE_EXTENSION)) {
      smsData = adHocSMSInputSourceProcessorService.getSMSDataFromJSONFile(inputStream);
    } else if (AdHocSMSExcelFileProcessor.EXCEL_FILES_EXTENSION.contains(extensionFile)) {
      smsData = adHocSMSInputSourceProcessorService.getSMSDataFromExcelFile(inputStream, sheetName);
    }

    scheduleAdHocSMSesService.scheduleAdHocSMSes(smsData);
  }

  @ApiOperation(value = "None", notes = "Schedules AdHoc SMSes based on data from SQL data set")
  @ApiResponses(value = {
      @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "AdHoc SMSes have been successfully scheduled"),
      @ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Failed to schedule AdHoc SMSes")
  })
  @RequestMapping(value = "/{dataSetUuid}", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public void scheduleAdHocSMSesFromDataSet(
      @ApiParam(name = "dataSetUuid", value = "dataSetUuid") @PathVariable String dataSetUuid) {
    List<AdHocSMSData> smsData = adHocSMSInputSourceProcessorService.getSMSDataFromSQLDataSet(
        dataSetUuid);

    scheduleAdHocSMSesService.scheduleAdHocSMSes(smsData);
  }
}
