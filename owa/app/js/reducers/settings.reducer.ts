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
import { ConfigUI, IConfig } from '../shared/model/config.model';
import { ITemplate } from '../shared/model/template.model';
import { handleRequest } from '../components/toast/request-status-util';
import { SMS_UPDATE_SUCCESS, SMS_UPDATE_FAILURE } from '../components/toast/messages';
import _ from 'lodash';

export const ACTION_TYPES = {
  RESET: "settings/RESET",
  GET_CONFIGS: "settings/GET_CONFIGS",
  GET_TEMPLATES: "settings/GET_TEMPLATES",
  UPLOAD_CONFIGS: "settings/UPLOAD_CONFIGS",
  UPDATE_CONFIG_STATE: "settings/UPDATE_CONFIG_STATE",
  OPEN_MODAL: 'settings/OPEN_MODAL',
  CLOSE_MODAL: 'settings/CLOSE_MODAL',
  ADD_EMPTY_CONFIG: 'settings/ADD_EMPTY_CONFIG'
};

export interface ISettingsState {
  configs: ReadonlyArray<ConfigUI>;
  templates: ReadonlyArray<ITemplate>;
  defaultConfigName: string;
  loading: boolean;
  showModal: boolean;
  configLocalIdToDelete: string;
}

const initialState: ISettingsState = {
  configs: [] as ReadonlyArray<ConfigUI>,
  templates: [] as ReadonlyArray<ITemplate>,
  defaultConfigName: '',
  loading: false,
  showModal: false,
  configLocalIdToDelete: ''
};

export default (state: ISettingsState = initialState, action): ISettingsState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.GET_CONFIGS):
    case REQUEST(ACTION_TYPES.GET_TEMPLATES):
    case REQUEST(ACTION_TYPES.UPLOAD_CONFIGS):
      return {
        ...state,
        loading: true
      };
    case FAILURE(ACTION_TYPES.GET_CONFIGS):
    case FAILURE(ACTION_TYPES.GET_TEMPLATES):
      return {
        ...state,
        loading: false
      };
    case FAILURE(ACTION_TYPES.UPLOAD_CONFIGS):
      return {
        ...state,
        loading: false
      };
    case SUCCESS(ACTION_TYPES.GET_TEMPLATES):
      return {
        ...state,
        loading: false,
        templates: mapTemplatesToArray(action.payload.data)
      }
    case SUCCESS(ACTION_TYPES.GET_CONFIGS):
      return {
        ...state,
        loading: false,
        configs: action.payload.data.configs.map((config: IConfig) => { return new ConfigUI(config); }),
        defaultConfigName: action.payload.data.defaultConfigName
      };
    case SUCCESS(ACTION_TYPES.UPLOAD_CONFIGS):
      return {
        ...state,
        loading: false,
        configs: action.payload.data.configs.map((config: IConfig) => { return new ConfigUI(config); }),
        defaultConfigName: action.payload.data.defaultConfigName
      }
    case ACTION_TYPES.RESET: {
      return initialState;
    };
    case ACTION_TYPES.UPDATE_CONFIG_STATE:
      return {
        ...state,
        configs: action.configs,
        defaultConfigName: action.defaultConfigName,
        showModal: false,
        configLocalIdToDelete: ''
      }
    case ACTION_TYPES.OPEN_MODAL: 
      return {
        ...state,
        showModal: true,
        configLocalIdToDelete: action.payload
      };
    case ACTION_TYPES.CLOSE_MODAL: 
      return {
        ...state,
        showModal: false,
        configLocalIdToDelete: ''
      };
    case ACTION_TYPES.ADD_EMPTY_CONFIG:
      return {
        ...state,
        configs: addEmptyConfig(state.configs)
      };
    default:
      return state;
  }
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});

export const getConfigs = () => async (dispatch) => {
  const requestUrl = 'ws/sms/configs';
  await dispatch({
    type: ACTION_TYPES.GET_CONFIGS,
    payload: axiosInstance.get(requestUrl)
  });
};

export const getTemplates = () => async (dispatch) => {
  const requestUrl = 'ws/sms/templates';
  await dispatch({
    type: ACTION_TYPES.GET_TEMPLATES,
    payload: axiosInstance.get(requestUrl)
  });
};

export const updateConfigs = (configsUiModel: ReadonlyArray<ConfigUI>, defaultConfigName: string) => async (dispatch) => {
  const requestUrl = 'ws/sms/configs';
  let configs: IConfig[] = configsUiModel.map((config: ConfigUI) => { return toConfigRequest(config); });
  const body = {
    type: ACTION_TYPES.UPLOAD_CONFIGS,
    payload: axiosInstance.post(requestUrl, {
      defaultConfigName,
      configs
    }),
  };
  handleRequest(dispatch, body, SMS_UPDATE_SUCCESS, SMS_UPDATE_FAILURE);
};

export const toConfigRequest = (config: ConfigUI): IConfig => {
  return <IConfig>{
  name: config.name,
  maxRetries: config.maxRetries,
  excludeLastFooter: config.excludeLastFooter,
  splitHeader: config.splitHeader,
  splitFooter: config.splitFooter,
  templateName: config.templateName,
  props: config.props
  }
};

export const addEmptyConfig = (currentConfig: ReadonlyArray<ConfigUI>) => {
  let configs = _.cloneDeep(currentConfig);
  configs = configs.concat(new ConfigUI());
  return configs;
};

export const updateState = (configs: ReadonlyArray<ConfigUI>, defaultConfigName: string) => ({
  type: ACTION_TYPES.UPDATE_CONFIG_STATE,
  configs,
  defaultConfigName
});

export const openModal = (configLocalId: string) => ({
  type: ACTION_TYPES.OPEN_MODAL,
  payload: configLocalId
});

export const closeModal = () => ({
  type: ACTION_TYPES.CLOSE_MODAL
});

export const addNewConfig = () => ({
  type: ACTION_TYPES.ADD_EMPTY_CONFIG
});

const mapTemplatesToArray = payloadData =>  Object.keys(payloadData).map(key => payloadData[key]);
