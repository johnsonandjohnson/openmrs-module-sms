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
import 'react-toastify/dist/ReactToastify.css';
import { SmsModel } from '../shared/model/sms.model';
import * as Default from '../utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import { handleRequest } from '../utils/request-status-util';
import { defaultValue } from '../shared/model/send-form.model';

export const ACTION_TYPES = {
  RESET: 'sendReducer/RESET',
  SEND: 'sendReducer/SEND',
  GET_CONFIGS: 'sendReducer/GET_CONFIGS',
  SMS_MESSAGE_CHANGE: 'sendReducer/SMS_MESSAGE_CHANGE',
  SMS_RECIPIENTS_CHANGE: 'sendReducer/SMS_RECIPIENTS_CHANGE',
  SMS_DELIVERY_TIME_CHANGE: 'sendReducer/SMS_DELIVERY_TIME_CHANGE',
  SMS_CONFIG_CHANGE: 'sendReducer/SMS_CONFIG_CHANGE',
  SMS_CUSTOM_PARAMS_CHANGE: 'sendReducer/SMS_CUSTOM_PARAMS_CHANGE'
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
      return {
        ...state,
        sendForm: {
          ...defaultValue,
          config: state.defaultConfigName
        }
      };
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
    case ACTION_TYPES.SMS_DELIVERY_TIME_CHANGE: {
      return {
        ...state,
        sendForm: {
          ...state.sendForm,
          deliveryTime: action.payload.deliveryTime,
          deliveryOption: action.payload.deliveryOption ? Number(action.payload.deliveryOption) : undefined
        }
      };
    };
    case ACTION_TYPES.SMS_RECIPIENTS_CHANGE: {
      return {
        ...state,
        sendForm: {
          ...state.sendForm,
          recipients: action.payload
        }
      };
    };
    case ACTION_TYPES.SMS_CONFIG_CHANGE: {
      return {
        ...state,
        sendForm: {
          ...state.sendForm,
          config: action.payload.config,
          providerId: action.payload.providerId
        }
      };
    };
    case ACTION_TYPES.SMS_CUSTOM_PARAMS_CHANGE: {
      return {
        ...state,
        sendForm: {
          ...state.sendForm,
          customParams: action.payload
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

  handleRequest(dispatch, body,
    getIntl().formatMessage({ id: 'SMS_SEND_SMS_SENDING_SUCCESS', defaultMessage: Default.SEND_SMS_SENDING_SUCCESS }),
    getIntl().formatMessage({ id: 'SMS_SEND_SMS_SENDING_FAILURE', defaultMessage: Default.SEND_SMS_SENDING_FAILURE }));
};

export const handleMessageUpdate = (message: string) => ({
  type: ACTION_TYPES.SMS_MESSAGE_CHANGE,
  payload: message
});

export const handleRecipientsUpdate = (recipients: string) => ({
  type: ACTION_TYPES.SMS_RECIPIENTS_CHANGE,
  payload: recipients
});

export const handleDeliveryTimeUpdate = (deliveryTime?: string, deliveryOption?: number) => ({
  type: ACTION_TYPES.SMS_DELIVERY_TIME_CHANGE,
  payload: {
    deliveryTime,
    deliveryOption
  }
});

export const handleConfigUpdate = (config: string, providerId: string) => ({
  type: ACTION_TYPES.SMS_CONFIG_CHANGE,
  payload: {
    config,
    providerId
  }
});

export const handleCustomParamsUpdate = (customParams?: string) => ({
  type: ACTION_TYPES.SMS_CUSTOM_PARAMS_CHANGE,
  payload: customParams
});
