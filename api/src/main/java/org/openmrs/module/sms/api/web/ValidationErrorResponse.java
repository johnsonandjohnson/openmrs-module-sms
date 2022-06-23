/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.sms.api.web;

import java.util.Map;

/**
 * Error Response Contract extended with constraint validation details
 */
public class ValidationErrorResponse extends ErrorResponse {

    /**
     * The cause of not valid request.
     */
    private Map<String, String> constraintViolations;

    public <T> ValidationErrorResponse(String code, String message, Map<String, String> constraintViolations) {
        super(code, message);
        this.constraintViolations = constraintViolations;
    }

    public Map<String, String> getConstraintViolations() {
        return constraintViolations;
    }
}
