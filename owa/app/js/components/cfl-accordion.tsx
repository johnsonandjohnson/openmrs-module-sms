import React from 'react';
import '@openmrs/react-components/assets/css/accordion.css';
import Arrow from '@openmrs/react-components/assets/images/arr-right.svg';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IconProp } from '@fortawesome/fontawesome-svg-core';

interface IProps {
  title: string; 
  border?: boolean;
  open?: boolean;
  fasIcon?: IconProp;
}

interface IState {
  visible: boolean;
 } 

export default class Accordion extends React.Component<IProps, IState> {

  constructor(props: IProps) {
    super(props);
    this.state = {
      visible: props.open
    };
  }

  render = () => {
    return (
      <div className={`accordion ${this.props.border ? 'border' : ''}`}>
        <div
          className="header"
          onClick={() => {
            this.setState(() => ({ visible: !this.state.visible }));
          }}
          role="button"
          tabIndex={0}
        >
          <a>
            <span>
              <img
                className={`${this.state.visible ? 'rotate90' : ''}`}
                height="12px"
                src={Arrow}
                width="12px"
              />
            </span>
            &nbsp;&nbsp;
            {this.props.title}
            &nbsp;&nbsp;
            {this.props.fasIcon && (
              <FontAwesomeIcon size="1x" icon={this.props.fasIcon} />
            )}
          </a>
        </div>
        <div className={`content ${!this.state.visible ? 'close' : 'open'}`}>
          {this.props.children}
        </div>
      </div>
    );
  }
}