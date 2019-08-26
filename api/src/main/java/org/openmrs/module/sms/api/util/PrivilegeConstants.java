package org.openmrs.module.sms.api.util;

import org.openmrs.annotation.AddOnStartup;
import org.openmrs.annotation.HasAddOnStartupPrivileges;

/**
 * Contains module's privilege constants.
 */
@HasAddOnStartupPrivileges
public final class PrivilegeConstants {

	@AddOnStartup(description = "Allows user to manage SMS module")
	public static final String SMS_MODULE_PRIVILEGE = "SMS module Privilege";

	private PrivilegeConstants() {
	}
}
