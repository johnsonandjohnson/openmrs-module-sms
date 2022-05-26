/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import uuid from "uuid";

export interface IConfig {
  name: string;
  maxRetries?: number;
  excludeLastFooter?: boolean;
  splitHeader?: string;
  splitFooter?: string;
  templateName?: string;
  automaticResponseScript?: string;
  props?: Array<IProp>;
}

export interface IProp {
  name?: string;
  value?: any;
}

export class ConfigUI {
  isDefault: boolean;
  localId: string;
  name: string;
  maxRetries?: number;
  excludeLastFooter?: boolean;
  splitHeader?: string;
  splitFooter?: string;
  templateName?: string;
  automaticResponseScript?: string;
  props?: Array<IProp>;

  constructor(model?: IConfig, defaultName?: string) {
    this.init();
    if (!!model) {
      this.mergeWithModel(model);
    }
    this.isDefault = !!defaultName ? this.name === defaultName : false;
  }

  init = () => {
    this.localId = uuid.v4();
  }

  mergeWithModel = (model: IConfig) => {
    if (!!model) {
      if (!!model.name) {
        this.name = model.name;
      }
      if (!!model.maxRetries) {
        this.maxRetries = model.maxRetries;
      }
      if (!!model.excludeLastFooter) {
        this.excludeLastFooter = model.excludeLastFooter;
      }
      if (!!model.splitHeader) {
        this.splitHeader = model.splitHeader;
      }
      if (!!model.splitFooter) {
        this.splitFooter = model.splitFooter;
      }
      if (!!model.templateName) {
        this.templateName = model.templateName;
      }
      if(!!model.automaticResponseScript) {
        this.automaticResponseScript = model.automaticResponseScript;
      }
      if (!!model.props) {
        this.props = model.props;
      }
    }
  }
}
