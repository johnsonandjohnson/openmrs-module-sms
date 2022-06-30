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
import { connect } from 'react-redux';
import _ from 'lodash';

import {
  sendSms, getSmsConfigs, reset, handleMessageUpdate,
  handleConfigUpdate, handleDeliveryTimeUpdate,
  handleRecipientsUpdate, handleCustomParamsUpdate
} from '../../reducers/send.reducer';
import * as Yup from 'yup';
import { validateForm, validateField } from '../../utils/validation-util';
import ErrorDesc from '../ErrorDesc';
import * as Default from '../../utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import { IRootState } from '../../reducers';
import { ISendForm } from '../../shared/model/send-form.model';
import { ISendError } from '../../shared/model/send-error.model';
import Tooltip from '../tooltip';

export interface ISendProps extends StateProps, DispatchProps {
};

export interface ISendState extends ISendForm {
  errors?: ISendError,
};

export class Send extends React.PureComponent<ISendProps, ISendState> {

  constructor(props) {
    super(props);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleCancelButton = this.handleCancelButton.bind(this);
    this.configChange = this.configChange.bind(this);
    this.deliveryTimeChange = this.deliveryTimeChange.bind(this);
    this.recipientsChange = this.recipientsChange.bind(this);
    this.messageChange = this.messageChange.bind(this);
    this.customParamsChange = this.customParamsChange.bind(this);
    this.state = props.sendForm;
  }

  validationSchema = Yup.object().shape({
    recipients: Yup.string()
      .matches(new RegExp('^\\+?\\d{1,15}(,\\+?\\d{1,15})*$'), getIntl().formatMessage({ id: 'SMS_NUMBERS_OR_COMMAS_REQUIRED', defaultMessage: Default.NUMBERS_OR_COMMAS_REQUIRED }))
      .required(getIntl().formatMessage({ id: 'SMS_FIELD_REQUIRED', defaultMessage: Default.FIELD_REQUIRED })),
    message: Yup.string()
      .required(getIntl().formatMessage({ id: 'SMS_FIELD_REQUIRED', defaultMessage: Default.FIELD_REQUIRED })),
    customParams: Yup.string()
        .matches(new RegExp("^(((\\S+):(\\S*)){0,1}\\n{0,1})*$"), getIntl().formatMessage({ id: 'SMS_CUSTOM_PARAMS_FORMAT', defaultMessage: Default.CUSTOM_PARAMS_FORMAT }))
  });

  componentDidMount() {
    this.props.getSmsConfigs();
    this.props.handleRecipientsUpdate('');
    this.props.handleMessageUpdate('');
    this.props.handleCustomParamsUpdate(undefined);
    this.props.handleDeliveryTimeUpdate(undefined, undefined);
  }

  componentDidUpdate(prevProps) {
    if (this.props.configs !== prevProps.configs) {
      this.props.handleConfigUpdate(this.props.defaultConfigName,
        this.getProviderId(this.props.defaultConfigName)[0])
    }
  }

  handleCancelButton(event) {
    event.preventDefault();
    this.props.reset();
  }

  handleSubmit(event) {
    event.preventDefault();

    validateForm(this.props.sendForm, this.validationSchema)
      .then(() => {
        this.props.sendSms(this.props.sendForm);
      })
      .catch((errors) => {
        this.setState({
          ...this.state,
          errors
        });
      });
  }

  validateFormFields(formData?) {
    if (!formData) {
      formData = this.props.sendForm;
    }

    validateForm(formData, this.validationSchema)
        .then(() => {
          this.setState({
            errors: undefined
          });
        })
        .catch((errors) => {
          this.setState({
            ...this.state,
            errors
          });
        });
  }

  configChange(event) {
    this.props.handleConfigUpdate(event.target.value, this.getProviderId(event.target.value)[0]);
  }

  getProviderId(selectedConfig) {
    const providerIdArray = this.props.configs
      .map((config: any) => selectedConfig === config.name ? config.templateName : null);
    _.pull(providerIdArray, null);
    return providerIdArray;
  }

  deliveryTimeChange(event) {
    let newDate = null as unknown as string;
    if (event.target.value) {
      const currentDate = new Date();
      newDate = new Date(currentDate.getTime() + (event.target.value * 1000)).toISOString();
    }
    this.props.handleDeliveryTimeUpdate(newDate, event.target.value);
  }

  recipientsChange(event) {
    let recipientsArray = event.target.value;
    this.props.handleRecipientsUpdate(recipientsArray.split(','));
    let form = {
        ... this.props.sendForm, recipients: recipientsArray
    };
    this.validateFormFields(form);
  }

  messageChange(event) {
    let message = event.target.value;
    this.props.handleMessageUpdate(message);
    let form = {
      ... this.props.sendForm, message
    };
    this.validateFormFields(form);
  }

  customParamsChange(event) {
    let customParams = event.target.value;
    this.props.handleCustomParamsUpdate(customParams);
    let form = {
      ... this.props.sendForm, customParams
    };
    this.validateFormFields(form);
  }

  renderConfigs() {
    return (
      <div>
        {this.props.configs.map((config: any) =>
          <span className='inline'>
            <input
              id={"radio-" + config.name}
              key={config.name}
              type='radio'
              name='configs'
              onChange={this.configChange}
              value={config.name}
              checked={this.props.sendForm.config === config.name} />
              <label
                htmlFor={"radio-" + config.name}
                className="radio-label">{config.name}
              </label>
          </span>
        )}
      </div>
    );
  }

  renderError(fieldName: string) {
    if (this.state.errors) {
      return <ErrorDesc field={this.state.errors[fieldName]} />
    }
  }

  renderDeliveryTimeOption(label: string, value?: number) {
    return (
      <span className="inline">
        <input
          id={"radio-" + value}
          type="radio"
          name="deliveryTimeOptions"
          onChange={this.deliveryTimeChange}
          value={value}
          checked={this.props.sendForm.deliveryOption === value} />
          <label
            htmlFor={"radio-" + value}
            className="radio-label">{label}
          </label>
      </span>
    );
  }

  render() {
    const formClass = 'form-control openmrs-textarea';
    const errorFormClass = formClass + ' error-field';
    const errors = this.state ? this.state.errors : null;
    return (
      <div className="body-wrapper">
        <h2>Send SMS</h2>
        <div className="panel-body">
          <label>{getIntl().formatMessage({ id: 'SMS_SELECT_CONFIGURATION', defaultMessage: Default.SELECT_CONFIGURATION })}</label>
          {this.props.configs && this.renderConfigs()}
        </div>
        <div className="panel-body">
          <label>{getIntl().formatMessage({ id: 'SMS_SELECT_DELIVERY_TIME', defaultMessage: Default.SELECT_DELIVERY_TIME })}</label>
          <div>
            {this.renderDeliveryTimeOption('Immediately', undefined)}
            {this.renderDeliveryTimeOption('10s', 10)}
            {this.renderDeliveryTimeOption('1m', 60)}
            {this.renderDeliveryTimeOption('1h', 3600)}
          </div>
        </div>
        <div className="panel-body">
          <label>{getIntl().formatMessage({ id: 'SMS_ADD_RECIPIENT_NUMBER', defaultMessage: Default.ADD_RECIPIENT_NUMBER })}</label>
          <Tooltip message={getIntl().formatMessage({ id: 'SMS_ADD_RECIPIENT_TOOLTIP', defaultMessage: Default.ADD_RECIPIENT_TOOLTIP })} />
          <textarea
            value={this.props.sendForm.recipients ? this.props.sendForm.recipients.join(',') : ''}
            rows={1}
            cols={50}
            onChange={this.recipientsChange}
            className={errors && errors.recipients ? errorFormClass : formClass} />
          {this.renderError('recipients')}
        </div>
        <div className="panel-body">
          <label>{getIntl().formatMessage({ id: 'SMS_TYPE_MESSAGE', defaultMessage: Default.TYPE_MESSAGE })}</label>
          <textarea
            value={this.props.sendForm.message}
            rows={7}
            cols={50}
            onChange={this.messageChange}
            className={errors && errors.message ? errorFormClass : formClass} />
          {this.renderError('message')}
        </div>
        <div className="panel-body">
          <label>{getIntl().formatMessage({ id: 'SMS_ADD_CUSTOM_PARAMETERS', defaultMessage: Default.ADD_CUSTOM_PARAMETERS })}</label>
          <Tooltip message={getIntl().formatMessage({ id: 'SMS_ADD_CUSTOM_PARAMETERS_TOOLTIP', defaultMessage: Default.ADD_CUSTOM_PARAMETERS_TOOLTIP })} />
          <textarea
            value={this.props.sendForm.customParams ? this.props.sendForm.customParams : ''}
            rows={7}
            cols={50}
            onChange={this.customParamsChange}
            className={errors && errors.customParams ? errorFormClass : formClass} />
          {this.renderError('customParams')}
        </div>
        <div className="panel-body">
          <button className="cancel" onClick={this.handleCancelButton}> Cancel </button>
          <button className="confirm" onClick={this.handleSubmit}> Send </button>
        </div>
      </div>
    )
  }
}

const mapStateToProps = ({ send }: IRootState) => (send);

const mapDispatchToProps = {
  reset,
  sendSms,
  getSmsConfigs,
  handleMessageUpdate,
  handleRecipientsUpdate,
  handleConfigUpdate,
  handleDeliveryTimeUpdate,
  handleCustomParamsUpdate
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Send);
