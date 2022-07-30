package org.openmrs.module.sms.api.service;

import org.openmrs.module.sms.api.data.AdHocSMSData;

import java.util.List;

/**
 * SendAdHocSMSesService API
 * <p>
 * Used for actions related to scheduling AdHoc SMSes.
 */
public interface ScheduleAdHocSMSesService {

  /**
   * Schedules AdHoc SMSes based on list of {@link AdHocSMSData} objects.
   *
   * @param smsData list of {@link AdHocSMSData} objects
   */
  void scheduleAdHocSMSes(List<AdHocSMSData> smsData);
}
