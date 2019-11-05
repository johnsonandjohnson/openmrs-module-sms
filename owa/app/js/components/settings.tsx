import React from 'react';
import { connect } from 'react-redux';
import { IConfig, IProp } from '../shared/model/config.model';
import {
  getConfigs,
  getTemplates,
  updateConfigs,
  updateState,
  openModal,
  closeModal,
  reset
} from '../reducers/settings.reducer';
import { Form, FormGroup, ControlLabel, FormControl, Checkbox, Button, Row, Col } from 'react-bootstrap';
import _ from 'lodash';
import Accordion from './cfl-accordion';
import { ITemplate } from '../shared/model/template.model';
import RemoveButton from './remove-button';
import OpenMRSModal from './open-mrs-modal';

interface ISettingsProps extends StateProps, DispatchProps {}

interface ISettingsState {
  configs: Array<IConfig>;
}

class Settings extends React.Component <ISettingsProps, ISettingsState> {

  constructor(props: ISettingsProps) {
    super(props);
    this.state = {
      configs: props.configs
    }
  };

  componentDidMount = () => {
    this.props.reset();
    this.props.getConfigs();
    this.props.getTemplates();
  };

  handleChange = (configName: string, fieldName: string, value: any, isDefault: boolean) => {
    const newConfigs: Array<IConfig> = this.props.configs.map((config: IConfig) => {
      if (config.name === configName) {
        config[fieldName] = value;
        if (fieldName === 'templateName') {
          config.props = this.getPropsForTemplate(value);
        }
        return config;
      }
      return config;
    });
    const shouldChangeDefaultName = isDefault && fieldName === 'name';
    this.props.updateState(newConfigs, shouldChangeDefaultName ? value : this.props.defaultConfigName);
  };

  handlePropChange = (configName: string, propName: string, value: string, isDefault: boolean) => {
    const newConfigs: Array<IConfig> = this.props.configs.map((config: IConfig) => {
      if (config && config.name === configName) {
        config.props = config.props && config.props.map((prop: IProp) => {
          if (prop && prop.name === propName) {
            prop.value = value;
            return prop;
          }
          return prop;
        });
        return config;
      }
      return config;
    });
    this.props.updateState(newConfigs, this.props.defaultConfigName);
  }

  handleRemove = (configName: string) => this.props.openModal(configName);

  handleClose = () => this.props.closeModal();

  handleConfirm = () => this.deleteConfig(this.props.configNameToDelete);

  getPropsForTemplate = (templateName: string): Array<IProp> => {
    const template = this.findTemplate(templateName)
    return template && template.configurables ? template.configurables.map(conf => ({
      name: conf,
      value: null
    })) : [];
  }

  findTemplate = (name: string | undefined): ITemplate => this.props.templates.find(template => template.name === name);

  setDefaultConfigName = (configName: string) => this.props.updateConfigs(this.props.configs, configName);

  deleteConfig = (name: string) => {
    const newConfigs = _.filter(this.props.configs, (config: IConfig) => config.name !== name);
    const defaultConfigName = this.props.defaultConfigName !== name ? this.props.defaultConfigName :
      newConfigs.length > 0 ? newConfigs[0].name : null;
    this.props.updateState(newConfigs, defaultConfigName);
  }

  saveConfigs = () => this.props.updateConfigs(this.props.configs, this.props.defaultConfigName);

  renderInput = (config: IConfig, fieldName: string, inputType: string, label: string, isDefault: boolean, index: number) => (
      <FormGroup controlId={`${fieldName}_${index}`}>
        <ControlLabel>{label}</ControlLabel>
        <FormControl type={inputType} name={fieldName} value={config[fieldName]} 
          onChange={e => this.handleChange(config.name, fieldName, e.target.value, isDefault)}/>
      </FormGroup>
  );

  renderCheckbox = (config: IConfig, fieldName: string, label: string, isDefault: boolean, index: number) => (
      <FormGroup controlId={`${fieldName}_${index}`}>
        <ControlLabel>{label}</ControlLabel>
        <Checkbox name={fieldName} checked={config[fieldName]} 
          onChange={e => this.handleChange(config.name, fieldName, e.target.checked, isDefault)} />
      </FormGroup>
  );

  renderConfigForm = (config: IConfig, isDefault: boolean, index: number) => (
    <Form className="form" onSubmit={e => {}}>
      {this.renderInput(config, 'name', 'text', 'Name:', isDefault, index)}
      {this.renderDropdown(config, 'templateName', 'Template:', index, isDefault)}
      {this.renderInput(config, 'maxRetries', 'number', 'Maximum Retries:', isDefault, index)}
      {this.renderInput(config, 'splitHeader', 'text', 'Split Message Header:', isDefault, index)}
      {this.renderInput(config, 'splitFooter', 'text', 'Split Message Footer:', isDefault, index)}
      {this.renderCheckbox(config, 'excludeLastFooter', 'Exclude footer from last split message', isDefault, index)}
      <br/>
      {this.renderProps(config, isDefault, index)}
    </Form>
  );

  renderProps = (config: IConfig, isDefault: boolean, index: number) => {
    const selectedTemplate = this.findTemplate(config.templateName);
    return selectedTemplate && selectedTemplate.configurables &&
      selectedTemplate.configurables.map(conf => this.renderProp(conf, config, isDefault, index));
  };

  renderProp = (propName: string, config: IConfig, isDefault: boolean, index: number) => {
    const prop: IProp | undefined = config && config.props && config.props.find(p => p.name === propName);
    return (
      <FormGroup controlId={`${propName}_${index}`}>
        <ControlLabel>{`${propName}:`}</ControlLabel>
        <FormControl type="text" name={propName} value={prop ? prop.value : ''} 
          onChange={e => this.handlePropChange(config.name, propName, e.target.value, isDefault)} />
      </FormGroup>
    );
  };

  renderDropdown = (config: IConfig, fieldName: string, label: string, index: number, isDefault: boolean) => {
    const templates: ReadonlyArray<ITemplate> = this.props.templates;
    return (
      <FormGroup controlId={`${fieldName}_${index}`}>
        <ControlLabel>{label}</ControlLabel>
        <FormControl type="select" 
          componentClass="select"
          name={fieldName}
          value={config.templateName}
          onChange={e => this.handleChange(config.name, fieldName, e.target.value, isDefault)} >
          <option key='empty'></option>
          {templates.map(template => <option key={template.name}>{template.name}</option>)}
        </FormControl>
      </FormGroup>
    );
  };

  renderConfig = (config: IConfig, index: number) => {
    const isDefault = config.name === this.props.defaultConfigName;
    return (
      <Row key={index}>
        <Col sm={11}>
          <Accordion key={index} title={config.name} isDefault={isDefault} border
            setDefaultCallback={this.setDefaultConfigName}
          >
            {this.renderConfigForm(config, isDefault, index)}
          </Accordion>
        </Col>
        <Col sm={1}>
          <RemoveButton
            handleRemove={e => this.handleRemove(config.name)}
            localId={config.name}
            tooltip="Delete SMS configuration" />
        </Col>
      </Row>
    );
  };

  renderConfigs = () => this.props.configs.map((config: IConfig, index: number) => this.renderConfig(config, index));

  renderSaveButton = () => (
    <div className="u-mt-15">
      <Button
        className="btn confirm btn-xs" 
        onClick={this.saveConfigs}>
          Save
      </Button>
    </div>
  );

  render() {
    const { loading } = this.props;
    return (
      <div className="body-wrapper">
        <OpenMRSModal
          deny={this.handleClose}
          confirm={this.handleConfirm}
          show={this.props.showModal}
          title="Delete SMS configuration"
          txt="Are you sure you want to delete this configuration?" />
        <Row>
          <Col xs={12} md={12}>
            <h2>SMS Configurations</h2>
          </Col>
        </Row>
        <div className="panel-body">
          {!loading && this.renderConfigs()}
          {this.renderSaveButton()}
        </div>
      </div>  
    );
  };
}

const mapStateToProps = state => ({
  configs: state.settings.configs,
  templates: state.settings.templates,
  defaultConfigName: state.settings.defaultConfigName,
  loading: state.settings.loading,
  showModal: state.settings.showModal,
  configNameToDelete: state.settings.configNameToDelete
});

const mapDispatchToProps = {
  getConfigs,
  getTemplates,
  updateConfigs,
  updateState,
  openModal,
  closeModal,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Settings);