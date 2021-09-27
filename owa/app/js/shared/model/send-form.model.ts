export interface ISendForm {
  recipients?: ReadonlyArray<string>,
  message?: string,
  config?: string,
  configs?: ReadonlyArray<any>,
  deliveryTime?: any,
  deliveryOption?: number,
  providerId?: string,
  failureCount?: number,
  customParams?: any,
}

export const defaultValue: Readonly<ISendForm> = {
  recipients: [] as ReadonlyArray<string>,
  message: '',
  config: '',
  configs: [] as ReadonlyArray<any>,
  deliveryTime: null,
  deliveryOption: undefined,
  providerId: '',
  failureCount: 0,
  customParams: null,
};
