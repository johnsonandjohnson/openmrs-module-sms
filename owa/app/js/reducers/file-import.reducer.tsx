/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import { REQUEST, SUCCESS, FAILURE } from './action-type.util'
import axiosInstance from '@bit/soldevelo-omrs.cfl-components.shared/axios'
import * as Default from '../utils/messages'
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import { handleRequest } from '../utils/request-status-util';

export const ACTION_TYPES = {
  IMPORT: 'fileImport/IMPORT',
  SET_FILE: 'fileImport/SET_FILE',
  RESET: 'fileImport/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  file: null as File | null,
};

export type FileImportState = Readonly<typeof initialState>;

export default (state: FileImportState = initialState, action): FileImportState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.IMPORT):
      return {
        ...state,
        errorMessage: null,
        loading: true
      };
    case FAILURE(ACTION_TYPES.IMPORT):
      return {
        ...state,
        loading: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.IMPORT):
      return {
        ...state,
        loading: false
      };
    case ACTION_TYPES.SET_FILE:
      const { file } = action.payload;
      return {
        ...state,
        file
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

export const setFile = (file?: File) => ({
  type: ACTION_TYPES.SET_FILE,
  payload: {
    file
  }
});

export const reset = () => ({
  type: ACTION_TYPES.RESET
});

export const uploadFileToImport = (file: File, entityName: string, reloadCallback: () => void) => async dispatch => {
  var formData = new FormData();
  formData.append('file', file);

  const requestUrl = 'ws/sms/';
  const config = { headers: { 'Content-Type': 'multipart/form-data' } };

  const body = {
    type: ACTION_TYPES.IMPORT,
    payload: axiosInstance.post(`${requestUrl}${entityName}/import`,
      formData,
      config)
  };
  return await handleRequest(dispatch, body,
    getIntl().formatMessage({ id: 'SMS_IMPORT_FILE_MODAL_SUCCESS', defaultMessage: Default.IMPORT_FILE_MODAL_SUCCESS }),
    getIntl().formatMessage({ id: 'SMS_IMPORT_FILE_MODAL_FAILURE', defaultMessage: Default.IMPORT_FILE_MODAL_FAILURE }))
    .then((result) => {
      dispatch(reloadCallback())
      return result;
    });
};
