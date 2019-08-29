package org.openmrs.module.sms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;

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
	}

	/**
	 * Using by the new OpenMRS Platforms
	 * @see #shutdown()
	 */
	public void shutdown() {
		LOGGER.info("Shutdown Sms");
	}

	@Override
	/**
	 * USed by the legacy OpenMRS Platforms
	 * @see #stopped()
	 */
	public void stopped() {
		LOGGER.info("Stopped Sms");
	}

}
