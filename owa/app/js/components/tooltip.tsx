import React from 'react';

export interface ITooltipProps {
  message: string
}

const Tooltip = (props: ITooltipProps) => {
  const { message } = props;

  if (!!message) {
    return (
      <p className="form-tooltip">
        {message}
      </p>
    );
  } else return null;
};

export default Tooltip;
