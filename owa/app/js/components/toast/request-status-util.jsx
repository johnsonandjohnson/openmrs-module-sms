import * as Default from './messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import { toast } from "react-toastify";
import '../../../css/toast.scss';
import { CloseButton } from './toast-builder-util';
import { ToastStatusContent } from './toast-builder-util'

// Propagates request send action and displays response notification 
export const handleRequest = async (dispatch, body, successMessage, errorMessage) => {

  const CLOSE_DELAY = 5000;
  const TOAST_CLASS = 'toast-item';

  var toastId = toast(
    <ToastStatusContent message={getIntl().formatMessage({ id: 'SMS_GENERIC_PROCESSING', defaultMessage: Default.GENERIC_PROCESSING })} type="notice"/>, 
    {
      autoClose: false,
      closeButton: false,
      className: TOAST_CLASS,
      hideProgressBar: true
    }
  );
  try {
    await dispatch(body);
    toast.update(toastId, {
      render: <ToastStatusContent message={successMessage} type={toast.TYPE.SUCCESS}/>,
      autoClose: CLOSE_DELAY,
      closeButton: <CloseButton />,
      className: TOAST_CLASS,
      hideProgressBar: true
    });
  } catch(e) {
    try {
      toast.update(toastId, {
        render: <ToastStatusContent message={[errorMessage, e.response.data.message].join(" ")} type={toast.TYPE.ERROR}/>,
        autoClose: CLOSE_DELAY,
        closeButton: <CloseButton />,
        className: TOAST_CLASS,
        hideProgressBar: true
      });
    } catch(e) {
      toast.update(toastId, {
        render: <ToastStatusContent message={getIntl().formatMessage({ id: 'SMS_GENERIC_FAILURE', defaultMessage: Default.GENERIC_FAILURE })} type={toast.TYPE.ERROR}/>,
        autoClose: CLOSE_DELAY,
        closeButton: <CloseButton />,
        className: TOAST_CLASS,
        hideProgressBar: true
      });
    }
  }
}
