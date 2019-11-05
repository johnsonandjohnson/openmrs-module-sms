import React from 'react';
import '@openmrs/react-components/assets/css/accordion.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IconProp } from '@fortawesome/fontawesome-svg-core';

interface IProps {
  title: string; 
  border?: boolean;
  open?: boolean;
  isDefault?: boolean;
  setDefaultCallback: (configName: string) => void
}

interface IState {
  visible: boolean;
  isHovering: boolean;
 } 

export default class Accordion extends React.Component<IProps, IState> {

  defaultIcon: IconProp = ['fas', 'star'];
  nonDefaultIcon: IconProp = ['far', 'star'];

  constructor(props: IProps) {
    super(props);
    this.state = {
      visible: false,
      isHovering: false
    };
  }

  handleMouseHover = () => this.setState((prevState: IState) => ({
      ...prevState,
      isHovering: !prevState.isHovering
    }));

  handleOnEmptyStarClick = (event) => {
    event.stopPropagation();
    this.props.setDefaultCallback(this.props.title);
  };

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
          onMouseEnter={() => this.handleMouseHover()}
          onMouseLeave={() => this.handleMouseHover()}
        >
          <a>
            <span>
              <FontAwesomeIcon
                className={`${this.state.visible ? 'rotate90' : ''}`}
                size="1x"
                icon={['fas', 'chevron-right',]}/>
            </span>
            &nbsp;&nbsp;
            {this.props.title}
            &nbsp;&nbsp;
            {this.props.isDefault ? (
              <FontAwesomeIcon size="1x" icon={this.defaultIcon} />
            ) : (
              this.state.isHovering && 
                <span onClick={this.handleOnEmptyStarClick} title="Set as default">
                  <FontAwesomeIcon size="1x" icon={this.nonDefaultIcon} />
                </span>
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