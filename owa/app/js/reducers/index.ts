/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import { combineReducers } from "redux";
import { reducers as openmrs } from '@openmrs/react-components';

import logs, { LogsState } from './logs.reducer';
import send, { SendState } from './send.reducer';
import settings, { ISettingsState } from './settings.reducer';
import fileImport, { FileImportState }from './file-import.reducer';
import timezone, {TimezoneState} from "./timezone.reducer";

export interface IRootState {
  readonly logs: LogsState;
  readonly send: SendState;
  readonly openmrs: any;
  readonly settings: ISettingsState;
  readonly fileImport: FileImportState;
  readonly timezone: TimezoneState;
}

export default combineReducers<IRootState>({
  openmrs,
  logs,
  send,
  settings,
  fileImport,
  timezone
} as any); //TODO: https://github.com/reduxjs/redux/issues/2709
