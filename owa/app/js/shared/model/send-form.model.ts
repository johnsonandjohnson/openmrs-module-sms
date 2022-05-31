/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

export interface ISendForm {
  recipients?: ReadonlyArray<string>,
  message?: string,
  config?: string,
  configs?: ReadonlyArray<any>,
  deliveryTime?: any,
  deliveryOption?: number,
  providerId?: string,
  failureCount?: number,
  customParams?: any,
}

export const defaultValue: Readonly<ISendForm> = {
  recipients: [] as ReadonlyArray<string>,
  message: '',
  config: '',
  configs: [] as ReadonlyArray<any>,
  deliveryTime: null,
  deliveryOption: undefined,
  providerId: '',
  failureCount: 0,
  customParams: null,
};
