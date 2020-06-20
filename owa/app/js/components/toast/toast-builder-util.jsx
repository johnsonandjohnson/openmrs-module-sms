// Component used for building toasts (notification) with OpenMRS style guideline matching

export const ToastStatusContent = ({ message, type }) => {
  var customClass = "toast-item-image toast-item-image-" + type;
  return (
    <div className="toast-item-wrapper sms-toast">
      <div className={customClass}></div>
      <p>{message}</p>
    </div>
  );
}

export const CloseButton = ({ closeToast }) => (
  <div className="toast-item-close" onClick={closeToast} />
);
