import React from 'react';
import PropTypes from 'prop-types';

const ErrorDesc = (props) => {
  const { field } = props;

  return (!!field && (
    <div className="error-description">
      {field}
    </div>
  ));
};

export default ErrorDesc;

ErrorDesc.propTypes = {
  field: PropTypes.string
};
