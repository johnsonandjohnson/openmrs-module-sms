/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import { REQUEST, SUCCESS, FAILURE } from './action-type.util'
import axiosInstance from '../config/axios';
import 'react-toastify/dist/ReactToastify.css';
import { SmsModel } from '../shared/model/sms.model';
import * as Msg from '../utils/messages';
import { handleRequest } from '../utils/request-status-util';
import { defaultValue } from '../shared/model/send-form.model';

export const ACTION_TYPES = {
  RESET: 'sendReducer/RESET',
  SEND: 'sendReducer/SEND',
  GET_CONFIGS: 'sendReducer/GET_CONFIGS',
  SMS_MESSAGE_CHANGE: 'sendReducer/GET_CONFIGS'
};

const initialState = {
  configs: [],
  sendForm: defaultValue,
  defaultConfigName: '',
  form: null
};

export type SendState = Readonly<typeof initialState>;

export default (state = initialState, action) => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEND):
      return {
        ...state,
        loading: true
      };
    case FAILURE(ACTION_TYPES.SEND):
      return {
        ...state,
        loading: false
      };
    case SUCCESS(ACTION_TYPES.SEND):
      return {
        ...state,
        sendForm: {
          ...state.sendForm,
          message: ''
        }
      };
    case REQUEST(ACTION_TYPES.GET_CONFIGS):
      return {
        ...state,
        loading: true
      };
    case FAILURE(ACTION_TYPES.GET_CONFIGS):
      return {
        ...state,
        loading: false,
        configs: []
      };
    case SUCCESS(ACTION_TYPES.GET_CONFIGS):
      return {
        ...state,
        loading: false,
        configs: action.payload.data.configs,
        defaultConfigName: action.payload.data.defaultConfigName
      };
    case ACTION_TYPES.RESET: {
      return initialState;
    };
    case ACTION_TYPES.SMS_MESSAGE_CHANGE: {
      return {
        ...state,
        sendForm: {
          ...state.sendForm,
          message: action.payload
        }
      };
    };
    default:
      return state;
  }
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});

export const getSmsConfigs = () => async (dispatch) => {
  const requestUrl = 'ws/sms/configs';
  await dispatch({
    type: ACTION_TYPES.GET_CONFIGS,
    payload: axiosInstance.get(requestUrl)
  });
};

export const sendSms = (sms) => async (dispatch) => {
  const requestUrl = 'ws/sms/send';
  const outgoingSms = new SmsModel(sms);
  const body = {
    type: ACTION_TYPES.SEND,
    payload: axiosInstance.post(requestUrl, outgoingSms)
  };

  handleRequest(dispatch, body, Msg.SEND_SMS_SENDING_SUCCESS, Msg.SEND_SMS_SENDING_FAILURE);
};

export const handleMessageUpdate = (message) => ({
  type: ACTION_TYPES.SMS_MESSAGE_CHANGE,
  payload: message
});
