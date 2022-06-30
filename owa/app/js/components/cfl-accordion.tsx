/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import '@openmrs/react-components/assets/css/accordion.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IconProp } from '@fortawesome/fontawesome-svg-core';
import { ConfigUI } from "../shared/model/config.model";

interface IProps {
  title: string;
  config: ConfigUI
  border?: boolean;
  open?: boolean;
  setDefaultCallback: (config: ConfigUI) => void
}

interface IState {
  visible: boolean;
  hovering: boolean;
 } 

export default class Accordion extends React.PureComponent<IProps, IState> {

  defaultIcon: IconProp = ['fas', 'star'];
  nonDefaultIcon: IconProp = ['far', 'star'];

  constructor(props: IProps) {
    super(props);
    this.state = {
      visible: !this.props.title,
      hovering: false
    };
  }

  handleMouseHover = () => this.setState((prevState: IState) => ({
      ...prevState,
      hovering: !prevState.hovering
    }));

  handleOnEmptyStarClick = (event) => {
    event.stopPropagation();
    this.props.setDefaultCallback(this.props.config);
  };

  render = () => {
    return (
      <div className={`accordion ${this.props.border ? 'border' : ''}`}>
        <div
          className="header"
          onClick={() => {
            this.setState((prevState: IState) => ({ visible: !prevState.visible }));
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
            {this.props.config.isDefault ? (
              <FontAwesomeIcon size="1x" icon={this.defaultIcon} />
            ) : (
              this.state.hovering && 
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