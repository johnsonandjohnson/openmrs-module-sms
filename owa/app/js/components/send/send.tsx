/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
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
import * as Msg from '../../utils/messages';
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
      .matches(new RegExp('^\\d{1,15}(,\\d{1,15})*$'), Msg.NUMBERS_OR_COMMAS_REQUIRED)
      .required(Msg.FIELD_REQUIRED),
    message: Yup.string()
      .required(Msg.FIELD_REQUIRED)
  });

  componentDidMount() {
    this.props.getSmsConfigs();
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
    let form = {
      recipients: recipientsArray
    };

    this.props.handleRecipientsUpdate(recipientsArray.split(','));

    validateField(form, 'recipients', this.validationSchema)
      .then(() => {
        event.preventDefault();
        this.setState({
          errors: undefined
        });
      })
      .catch((errors) => {
        this.setState({
          errors
        });
      });
  }

  messageChange(event) {
    let message = event.target.value;
    let form = {
      message
    };
    this.props.handleMessageUpdate(message);

    validateField(form, 'message', this.validationSchema)
      .then(() => {
        this.setState({
          errors: undefined
        });
      })
      .catch((errors) => {
        this.setState({
          errors
        });
      });
  }

  customParamsChange(event) {
    this.props.handleCustomParamsUpdate(event.target.value);
  }

  renderConfigs() {
    return (
      <div>
        {this.props.configs.map((config: any) =>
          <span className='inline'>
            <input key={config.name}
              type='radio'
              name='configs'
              onChange={this.configChange}
              value={config.name}
              checked={this.props.sendForm.config === config.name} />
            {config.name}
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
          type="radio"
          name="deliveryTimeOptions"
          onChange={this.deliveryTimeChange}
          value={value}
          checked={this.props.sendForm.deliveryOption === value} />
        {label}
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
          <label>Select configuration</label>
          {this.props.configs && this.renderConfigs()}
        </div>
        <div className="panel-body">
          <label>Select delivery time</label>
          <div>
            {this.renderDeliveryTimeOption('Immediately', undefined)}
            {this.renderDeliveryTimeOption('10s', 10)}
            {this.renderDeliveryTimeOption('1m', 60)}
            {this.renderDeliveryTimeOption('1h', 3600)}
          </div>
        </div>
        <div className="panel-body">
          <label>Add recipients' phone number</label>
          <Tooltip message="Separate multiple phone numbers with comma." />
          <textarea
            value={this.props.sendForm.recipients ? this.props.sendForm.recipients.join(',') : ''}
            rows={1}
            cols={50}
            onChange={this.recipientsChange}
            className={errors && errors.recipients ? errorFormClass : formClass} />
          {this.renderError('recipients')}
        </div>
        <div className="panel-body">
          <label>Type the message</label>
          <textarea
            value={this.props.sendForm.message}
            rows={7}
            cols={50}
            onChange={this.messageChange}
            className={errors && errors.message ? errorFormClass : formClass} />
          {this.renderError('message')}
        </div>
        <div className="panel-body">
          <label>Add custom parameters (optional)</label>
          <Tooltip message="Map custom parameters in key:value format. Use new line as a separator." />
          <textarea
            value={this.props.sendForm.customParams ? this.props.sendForm.customParams : ''}
            rows={7}
            cols={50}
            onChange={this.customParamsChange}
            className={formClass} />
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
