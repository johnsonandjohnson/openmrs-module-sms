import uuid from "uuid";

export interface IConfig {
  name: string;
  maxRetries?: number;
  excludeLastFooter?: boolean;
  splitHeader?: string;
  splitFooter?: string;
  templateName?: string;
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
      if (!!model.props) {
        this.props = model.props;
      }
    }
  }
}
