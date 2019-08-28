package org.openmrs.module.sms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.sms.api.event.SmsEventListenerFactory;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class SmsActivator extends BaseModuleActivator {

	private static final Log LOGGER = LogFactory.getLog(SmsActivator.class);

	/**
	 * @see #started()
	 */
	@Override
	public void started() {
		LOGGER.info("Started Sms");
		SmsEventListenerFactory.registerEventListeners();
	}

	/**
	 * @see #shutdown()
	 */
	public void shutdown() {
		LOGGER.info("Shutdown Sms");
	}

}
