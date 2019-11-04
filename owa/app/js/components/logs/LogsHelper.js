/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

const CONFIG = 'CONFIG';
const CRATION_DATE = 'CRATION_DATE';
const DELIVERY_STATUS = 'DELIVERY_STATUS';
const ERROR_MESSAGE = 'ERROR_MESSAGE';
const ID = 'ID';
const MESSAGE_CONTENT = 'MESSAGE_CONTENT';
const MODIFICATION_DATE = 'MODIFICATION_DATE';
const MODIFIED_BY = 'MODIFIED_BY';
const MOTECH_ID = 'MOTECH_ID';
const PHONE_NUMBER = 'PHONE_NUMBER';
const PROVIDER_ID = 'PROVIDER_ID';
const PROVIDER_STATUS = 'PROVIDER_STATUS';
const SMS_DIRECTION = 'SMS_DIRECTION';
const TIMESTAMP = 'TIMESTAMP';

export const FIELDS = [ID, PHONE_NUMBER, MESSAGE_CONTENT, SMS_DIRECTION, TIMESTAMP,
  PROVIDER_ID, PROVIDER_STATUS, CONFIG, CRATION_DATE, DELIVERY_STATUS, ERROR_MESSAGE,
  MODIFICATION_DATE, MODIFIED_BY, MOTECH_ID];

export const Cell = ({ columnName, value }) => {
  switch (columnName) {
    case ID: {
      return getColumnHeader(value.id);
    }
    case PHONE_NUMBER: {
      return getColumnHeader(value.phoneNumber);
    }
    case MESSAGE_CONTENT: {
      return getColumnHeader(value.messageContent);
    }
    case SMS_DIRECTION: {
      return getColumnHeader(value.smsDirection);
    }
    case TIMESTAMP: {
      return getColumnHeader(value.timestamp);
    }
    case PROVIDER_ID: {
      return getColumnHeader(value.providerId);
    }
    case PROVIDER_STATUS: {
      return getColumnHeader(value.providerStatus);
    }
    case CONFIG: {
      return getColumnHeader(value.config);
    }
    case CRATION_DATE: {
      return getColumnHeader(value.creationDate);
    }
    case DELIVERY_STATUS: {
      return getColumnHeader(value.deliveryStatus);
    }
    case ERROR_MESSAGE: {
      return getColumnHeader(value.errorMessage);
    }
    case MODIFICATION_DATE: {
      return getColumnHeader(value.modificationDate);
    }
    case MODIFIED_BY: {
      return getColumnHeader(value.modifiedBy);
    }
    case MOTECH_ID: {
      return getColumnHeader(value.motechId);
    }
    default:
      return getColumnHeader(value);
  }
};

function getColumnHeader(value) {
  return (<span>{`${value}`}</span>);
};