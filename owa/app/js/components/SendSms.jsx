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

import { sendSms, getSmsConfigs, reset } from '../reducers/send.reducer';
import * as Yup from 'yup';
import { validateForm, validateField } from '../utils/validation-util';
import ErrorDesc from './ErrorDesc';
import * as Msg from '../utils/messages';

export class SendSms extends React.Component {
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
      .matches(new RegExp('^\\d+(,\\d+)*$'), Msg.NUMBERS_OR_COMMAS_REQUIRED)
      .required(Msg.FIELD_REQUIRED),
    message: Yup.string()
      .required(Msg.FIELD_REQUIRED)
  });

  componentDidMount() {
    this.props.getSmsConfigs();
  }

  componentDidUpdate(prevProps) {
    if (this.props.configs !== prevProps.configs) {
      this.setState({
        config: this.props.defaultConfigName,
        providerId: this.getProviderId(this.props.defaultConfigName)[0]
      });
    }
  }

  handleCancelButton() {
    this.form.reset();
    this.setState(this.props.sendForm);
  }

  handleSubmit(event) {
    validateForm(this.state, this.validationSchema)
      .then(() => {
        event.preventDefault();
        this.props.sendSms(this.state);
      })
      .catch((errors) => {
        this.setState({
          ...this.state,
          errors
        });
      });
  }

  configChange(event) {
    this.setState({
      config: event.target.value,
      providerId: this.getProviderId(event.target.value)[0]
    });
  }

  getProviderId(selectedConfig) {
    const providerIdArray = this.props.configs.map(config => selectedConfig === config.name ? config.templateName : null);
    _.pull(providerIdArray, null);
    return providerIdArray;
  } 

  deliveryTimeChange(event) {
    let newDate = null;
    if (event.target.value) {
      const currentDate = new Date();
      newDate = new Date(currentDate.getTime() + (event.target.value*1000)).toISOString();
    }
    this.setState({ deliveryTime: newDate });
  }

  recipientsChange(event) {
    let recipientsArray = event.target.value;
    let form = {
      recipients: recipientsArray
    };

    validateField(form, 'recipients', this.validationSchema)
      .then(() => {
        event.preventDefault();
        this.setState({
          recipients: recipientsArray.split(','),
          errors: null
        });
      })
      .catch((errors) => {
        this.setState({
          recipients: recipientsArray.split(','),
          errors
        });
      });
  }

  messageChange(event) {
    let message = event.target.value;
    let form = {
      message
    };

    validateField(form, 'message', this.validationSchema)
      .then(() => {
        event.preventDefault();
        this.setState({
          message,
          errors: null
        });
      })
      .catch((errors) => {
        this.setState({
          message,
          errors
        });
      });
  }

  customParamsChange(event) {
    this.setState({
      customParams: event.target.value
    });
  }

  render() {
    const formClass = 'form-control';
    const errorFormClass = formClass + ' error-field';
    const errors = this.state ? this.state.errors : null;

    return (
      <div className="body-wrapper">
        <h1>Send SMS</h1>
        <form ref={form => this.form = form}>
          <h3>Select configuration</h3>
          {this.props.configs && this.props.configs.map(config => <label className='inline'>
          <input key={config.name} type='radio' name='configs' onChange={this.configChange} value={config.name} defaultChecked={this.props.defaultConfigName === config.name}/>
          {config.name}</label>)}
          <br />
          <h3>Select delivery time</h3>
            <label className='inline'><input type='radio' name='deliveryTimeOptions' onChange={this.deliveryTimeChange} value={null} defaultChecked={true}/>Immediately</label>
            <label className='inline'><input type='radio' name='deliveryTimeOptions' onChange={this.deliveryTimeChange} value={10}/>10s</label>
            <label className='inline'><input type='radio' name='deliveryTimeOptions' onChange={this.deliveryTimeChange} value={60}/>1m</label>
            <label className='inline'><input type='radio' name='deliveryTimeOptions' onChange={this.deliveryTimeChange} value={3600}/>1h</label>
          <br />
          <h3>Add recipients' phone number</h3>
          <label>
            Separate multiple phone numbers with comma
            <br />
            <textarea
              rows='1'
              cols='50'
              onChange={this.recipientsChange}
              className={errors && errors.recipients ? errorFormClass : formClass} />
            {errors && <ErrorDesc field={errors.recipients}/> }
          </label>
          <br />
          <h3>Type the message</h3>
          <label>
            <textarea
              multiline='true'
              rows='7'
              cols='50'
              onChange={this.messageChange}
              className={errors && errors.message ? errorFormClass : formClass} />
            {errors && <ErrorDesc field={errors.message}/> }
          </label>
          <br />
          <h3>Add custom parameters (optional)</h3>
          <label>
            Map custom parameters in key:value format. Use new line as a separator
            <br />
            <textarea multiline='true' rows='7' cols='50' onChange={this.customParamsChange} />
          </label>
          <br />
          <button className="cancel" onClick={this.handleCancelButton}>Cancel</button>
          <button className="confirm" onClick={this.handleSubmit}>
            Send
          </button>
        </form>
      </div>
    )
  }
}

const mapStateToProps = state => (state.sendReducer);

const mapDispatchToProps = {
  reset,
  sendSms,
  getSmsConfigs
};

export default connect(mapStateToProps, mapDispatchToProps)(SendSms);
