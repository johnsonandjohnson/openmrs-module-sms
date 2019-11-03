import React from 'react';
import { connect } from 'react-redux';
import { IConfig, IProp } from '../shared/model/config.model';
import { getConfigs, getTemplates, reset } from '../reducers/settings.reducer';
import { Form, FormGroup, ControlLabel, FormControl, Checkbox } from 'react-bootstrap';
import _ from 'lodash';
import Accordion from './cfl-accordion';

interface ISettingsProps extends StateProps, DispatchProps {}

class Settings extends React.Component <ISettingsProps> {

  componentDidMount = () => {
    this.props.reset();
    this.props.getConfigs();
    this.props.getTemplates();
  }

  handleChange = e => { }

  renderConfigForm = (config: IConfig) => {
    return (
      <Form className="form" onSubmit={e => {}}>
        <FormGroup controlId={`name_${config.name}`}>
          <ControlLabel>Name:</ControlLabel>
          <FormControl type="text" name="name" value={config.name} onChange={this.handleChange} />
        </FormGroup>
        {this.renderTemplateDropdown(config)}
        <FormGroup controlId={`maxRetries_${config.name}`}>
          <ControlLabel>Maximum Retries:</ControlLabel>
          <FormControl type="number" name="maxRetries" value={config.maxRetries} onChange={this.handleChange} />
        </FormGroup>
        <FormGroup controlId={`splitHeader_${config.name}`}>
          <ControlLabel>Split Message Header:</ControlLabel>
          <FormControl type="text" name="splitHeader" value={config.splitHeader} onChange={this.handleChange} />
        </FormGroup>
        <FormGroup controlId={`splitFooter_${config.name}`}>
          <ControlLabel>Split Message Footer:</ControlLabel>
          <FormControl type="text" name="splitFooter" value={config.splitFooter} onChange={this.handleChange} />
        </FormGroup>
        <FormGroup controlId={`excludeLastFooter_${config.name}`}>
          <ControlLabel>Exclude footer from last split message</ControlLabel>
          <Checkbox name="excludeLastFooter" checked={config.excludeLastFooter} onChange={this.handleChange} />
        </FormGroup>
        <br/>
        {this.renderProp('user', 'Username:', config)}
        {this.renderProp('password', 'Password:', config)}
        {this.renderProp('senderID', 'API ID:', config)}
        {this.renderProp('dcs', 'Form:', config)}
      </Form>
    );
  }

  renderProp = (propName: string, label: string, config: IConfig) => {
    const prop: IProp = config.props.find(p => p.name === propName);
    return (
      <>
        {prop && (
          <FormGroup controlId={`${prop.name}_${config.name}`}>
            <ControlLabel>{label}</ControlLabel>
            <FormControl type="text" name={prop.name} value={prop.value} onChange={this.handleChange} />
          </FormGroup>
        )}
      </>
    )
  }

  renderTemplateDropdown = (config: IConfig) => {
    const { templates } = this.props;
    return (
      <FormGroup controlId={`templateName_${config.name}`}>
        <ControlLabel>Template:</ControlLabel>
        <FormControl type="select" 
          componentClass="select"
          name="templateName"
          value={config.templateName}
          onChange={this.handleChange} >
          {Object.keys(templates).map(key => <option key={key}>{templates[key].name}</option>)}
        </FormControl>
      </FormGroup>
    );
  }

  renderConfig = (config: IConfig) => {
    const isDefault = config.name === this.props.defaultConfigName;
    return (
      <Accordion key={config.name} title={config.name} open={isDefault} fasIcon={isDefault && ['fas', 'star']}>
        {this.renderConfigForm(config)}
      </Accordion>
    );
  }

  renderConfigs = () => {
    return this.props.configs.map(config => this.renderConfig(config));
  }

  render() {
    const { loading } = this.props;
    return (
    <>
      {loading ? (
        <div>LOADING</div>
      ) : (
        <>
          {this.renderConfigs()}
        </>
      )}
    </>
    )
  }
}

const mapStateToProps = state => ({
  configs: state.settings.configs,
  templates: state.settings.templates,
  defaultConfigName: state.settings.defaultConfigName,
  loading: state.settings.loading
});

const mapDispatchToProps = {
  getConfigs,
  getTemplates,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Settings);