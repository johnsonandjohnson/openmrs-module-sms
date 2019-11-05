/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import axiosInstance from '../config/axios';

import { REQUEST, SUCCESS, FAILURE } from './action-type.util'
import { LogModel } from '../shared/model/log.model';

export const ACTION_TYPES = {
  FETCH_LOGS: 'logsReducer/FETCH_LOGS',
  RESET: 'logsReducer/RESET',
  CHANGE_PAGE_SIZE: 'logsReducer/CHANGE_PAGE_SIZE',
  PAGE_CHANGE: 'logsReducer/PAGE_CHANGE'
};

const initialState = {
  pages: 0,
  loading: false,
  data: [],
};

export type LogsState = Readonly<typeof initialState>;

export default (state: LogsState = initialState, action): LogsState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_LOGS):
      return {
        ...state,
        loading: true
      };
    case FAILURE(ACTION_TYPES.FETCH_LOGS):
      return {
        ...state,
        loading: false,
        data: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_LOGS):
      return {
        ...state,
        data: action.payload.data.rows && action.payload.data.rows.map((log) => { return new LogModel(log) }),
        loading: false,
        pages: Math.ceil((action.payload.data.totalRecords / action.payload.data.pageSize))
      };
    case ACTION_TYPES.RESET: {
      return initialState;
    }
    default:
      return state;
  }
};

const apiUrl = 'ws/sms/log';

export const reset = () => ({
  type: ACTION_TYPES.RESET
});

export const getLogs = (page, size, sort, order, filters) => ({
  type: ACTION_TYPES.FETCH_LOGS,
  payload: axiosInstance.get(apiUrl, {
    params: {
      page: page + 1,
      rows: size,
      sortColumn: sort,
      sortDirection: order,
      ...filters
    }
  })
});
