/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import axios from 'axios';
import { LocalizedMessage } from '@openmrs/react-components';

import { REQUEST, SUCCESS, FAILURE } from './action-type.util'
import { LogModel } from '../shared/model/log.model';

export const ACTION_TYPES = {
  FETCH_LOGS: 'logsReducer/FETCH_LOGS',
  RESET: 'logsReducer/RESET',
  CHANGE_PAGE_SIZE: 'logsReducer/CHANGE_PAGE_SIZE',
  PAGE_CHANGE: 'logsReducer/PAGE_CHANGE'
};

const initialState = {
  filterType: 'table',
  isFilterable: false,
  isResizeable: true,
  isSortable: true,
  minRows: 0,
  pageSizeOptions: [10, 20, 25, 50, 100],
  showPageSizeOptions: false,
  pages: 0,
  loading: false,
  noDataMessage: <LocalizedMessage
    id='reactcomponents.table.noDataText'
    defaultMessage='No results found' />,
  previousText: <LocalizedMessage
    id='reactcomponents.table.previous'
    defaultMessage='Previous' />,
  nextText: <LocalizedMessage
    id='reactcomponents.table.next'
    defaultMessage='Next' />,
  loadingText: <LocalizedMessage
    id='reactcomponents.table.loading'
    defaultMessage='Loading...' />,
  noDataText: <LocalizedMessage
    id='reactcomponents.table.noDataText'
    defaultMessage='No results found' />,
  pageText: <LocalizedMessage
    id='reactcomponents.table.page'
    defaultMessage='Page' />,
  ofText: <LocalizedMessage
    id='reactcomponents.table.of'
    defaultMessage='of' />,
  rowsText: <LocalizedMessage
    id='reactcomponents.table.rows'
    defaultMessage='rows' />,
  selectedRows: [],
  data: [],
  pageSize: 10,
  page: 0
};

export default (state = initialState, action) => {
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
        data: action.payload.data.rows.map((log) => { return new LogModel(log) }),
        loading: false,
        pages: Math.ceil((action.payload.data.totalRecords / state.pageSize))
      };
    case ACTION_TYPES.CHANGE_PAGE_SIZE:
      return {
        ...state,
        pageSize: action.payload
      };
    case ACTION_TYPES.PAGE_CHANGE:
      return {
        ...state,
        page: action.payload
      };
    case ACTION_TYPES.RESET: {
      return initialState;
    }
    default:
      return state;
  }
};

// ToDo add as interceptors
const logsUrl = 'ws/sms/log';
const localhost = axios.create({
  baseURL: 'http://localhost:3000/openmrs/',
  headers: {
    Authorization: 'Basic YWRtaW46QWRtaW4xMjM='
  }
});

export const reset = () => ({
  type: ACTION_TYPES.RESET
});

export const getLogs = (state) => async (dispatch) => {
  const requestUrl = `${logsUrl}`;
  await dispatch({
    type: ACTION_TYPES.FETCH_LOGS,
    payload: localhost.get(requestUrl, {
      params: {
        page: state.page + 1,
        rows: state.pageSize
      }
    })
  });
};

export const onPageSizeChange = (size) => ({
  type: ACTION_TYPES.CHANGE_PAGE_SIZE,
  payload: size
});

export const onPageChange = (page) => ({
  type: ACTION_TYPES.PAGE_CHANGE,
  payload: page
});