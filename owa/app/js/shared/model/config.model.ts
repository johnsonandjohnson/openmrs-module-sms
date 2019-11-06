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