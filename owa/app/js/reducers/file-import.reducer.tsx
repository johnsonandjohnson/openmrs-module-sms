import { REQUEST, SUCCESS, FAILURE } from './action-type.util'
import axiosInstance from '../config/axios';
import * as Msg from '../utils/messages'
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
  return await handleRequest(dispatch, body, Msg.IMPORT_FILE_MODAL_SUCCESS, Msg.IMPORT_FILE_MODAL_FAILURE)
    .then((result) => {
      dispatch(reloadCallback())
      return result;
    });
};
