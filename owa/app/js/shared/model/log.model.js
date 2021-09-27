/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

export class LogModel {
  constructor(modelDTO) {
    this.id = modelDTO.id;
    this.errorMessage = modelDTO.errorMessage;
    this.providerStatus = modelDTO.providerStatus;
    this.openMrsId = modelDTO.openMrsId;
    this.providerId = modelDTO.providerId;
    this.deliveryStatus = modelDTO.deliveryStatus;
    this.messageContent = modelDTO.messageContent;
    this.timestamp = modelDTO.timestamp;
    this.config = modelDTO.config;
    this.smsDirection = modelDTO.smsDirection;
    this.phoneNumber = modelDTO.phoneNumber;
    this.modificationDate = modelDTO.modificationDate;
    this.creationDate = modelDTO.creationDate;
    this.modifiedBy = modelDTO.modifiedBy;
    this.creator = modelDTO.creator;
  }
}
