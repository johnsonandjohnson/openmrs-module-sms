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
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import { sendSms, getSmsConfigs, reset } from '../reducers/sendReducer';

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
  componentDidMount() {
    this.props.getSmsConfigs(this.props);
  }

  componentDidUpdate(prevProps) {
    if (this.props.configs !== prevProps.configs) {
      this.setState({
        config: this.props.defaultConfigName,
        providerId: this.props.configs[0].templateName,
        deliveryTime: 0
      });
    }
  }

  handleCancelButton() {
    this.form.reset();
    this.setState(this.props.sendForm);
  }

  handleSubmit() {
    this.props.sendSms(this.state);
  }

  configChange(event) {
    this.setState({
      config: event.target.value
    });
  }

  deliveryTimeChange() {
    this.setState({
      deliveryTime: event.target.value
    });
  }

  recipientsChange() {
    this.setState({
      recipients: event.target.value.split(',')
    });
  }

  messageChange() {
    this.setState({
      message: event.target.value
    });
  }

  customParamsChange() {
    this.setState({
      customParams: event.target.value
    });
  }

  render() {
    return (
      <div>
        <ToastContainer
        position="top-right"
        autoClose={5000}
        hideProgressBar={false}
        closeOnClick
        rtl={false}
        pauseOnVisibilityChange
        draggable
        pauseOnHover
        />
        <h1>Send SMS</h1>
        <form ref={form => this.form = form}>
          <h3>Select configuration</h3>
          {this.props.configs.map(config => <label className='inline'>
          <input key={config.name} type='radio' name='configs' onChange={this.configChange} value={config.name} defaultChecked={this.props.defaultConfigName === config.name}/>
          {config.name}</label>)}
          <br />
          <h3>Select delivery time</h3>
            <label className='inline'><input type='radio' name='deliveryTimeOptions' onChange={this.deliveryTimeChange} value={0} defaultChecked={true}/>Immediately</label>
            <label className='inline'><input type='radio' name='deliveryTimeOptions' onChange={this.deliveryTimeChange} value={10}/>10s</label>
            <label className='inline'><input type='radio' name='deliveryTimeOptions' onChange={this.deliveryTimeChange} value={60}/>1m</label>
            <label className='inline'><input type='radio' name='deliveryTimeOptions' onChange={this.deliveryTimeChange} value={3600}/>1h</label>
          <br />
          <h3>Add recipients' phone number</h3>
          <label>
            Separate multiple phone numbers with comma
            <br />
            <textarea rows='1' cols='50' onChange={this.recipientsChange} />
          </label>
          <br />
          <h3>Type the message</h3>
          <label>
            <textarea multiline='true' rows='7' cols='50' onChange={this.messageChange} />
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
