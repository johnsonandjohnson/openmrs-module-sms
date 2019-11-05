import * as Msg from './messages';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { CloseButton } from './toast-builder-util';
import { ToastStatusContent } from './toast-builder-util';

const CLOSE_DELAY = 5000;
const TOAST_CLASS = 'toast-item';

const displayError = (toastId, errorMessage) => {
  toast.update(toastId, {
    render: <ToastStatusContent message={errorMessage} type={toast.TYPE.ERROR} />,
    autoClose: CLOSE_DELAY,
    closeButton: <CloseButton />,
    className: TOAST_CLASS,
    hideProgressBar: true
  });

}

// Propagates request send action and displays response notification 
export const handleRequest = async (dispatch, body, successMessage, errorMessage) => {
  var toastId = toast(
    <ToastStatusContent message={Msg.GENERIC_PROCESSING} type="notice" />,
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
      render: <ToastStatusContent message={successMessage} type={toast.TYPE.SUCCESS} />,
      autoClose: CLOSE_DELAY,
      closeButton: <CloseButton />,
      className: TOAST_CLASS,
      hideProgressBar: true
    });
  } catch(e) {
    try {
      displayError(toastId, [errorMessage, e.response.data.message].join(' '));
    } catch(exc) {
      displayError(toastId, Msg.GENERIC_FAILURE);
    }
  }
}
