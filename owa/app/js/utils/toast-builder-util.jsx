import PropTypes from 'prop-types';
// Component used for building toasts (notification) with OpenMRS style guideline matching

export const ToastStatusContent = ({ message, type }) => {
  var customClass = "toast-item-image toast-item-image-" + type;
  return (
    <div className="toast-item-wrapper">
      <div className={customClass}></div>
      <p>{message}</p>
    </div>
  );
}

ToastStatusContent.propTypes = {
  message: PropTypes.string.isRequired,
  type: PropTypes.string.isRequired,
};

export const CloseButton = ({ onToastClose = function(){} }) => (
  <div className="toast-item-close" onClick={onToastClose} />
);

CloseButton.propTypes = {
  onToastClose: PropTypes.func,
};
