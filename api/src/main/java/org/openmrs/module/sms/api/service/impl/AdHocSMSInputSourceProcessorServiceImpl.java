package org.openmrs.module.sms.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.api.context.Context;
import org.openmrs.module.sms.api.adhocsms.AdHocSMSInputSourceProcessor;
import org.openmrs.module.sms.api.adhocsms.AdHocSMSInputSourceProcessorContext;
import org.openmrs.module.sms.api.data.AdHocSMSData;
import org.openmrs.module.sms.api.service.AdHocSMSInputSourceProcessorService;

public class AdHocSMSInputSourceProcessorServiceImpl implements
    AdHocSMSInputSourceProcessorService {

  @Override
  public List<AdHocSMSData> getAdHocSMSData(AdHocSMSInputSourceProcessorContext context) {
    List<AdHocSMSData> result = new ArrayList<>();
    List<AdHocSMSInputSourceProcessor> inputSourceProcessors = Context.getRegisteredComponents(
        AdHocSMSInputSourceProcessor.class);
    for (AdHocSMSInputSourceProcessor processor : inputSourceProcessors) {
      if (processor.shouldProcessData(context)) {
        result = processor.getAdHocSMSData(context);
      }
    }
    return result;
  }
}
