import React from 'react';
import { connect } from 'react-redux';
import { IConfig, IProp } from '../shared/model/config.model';
import { getConfigs, getTemplates, updateConfigs, updateState, reset } from '../reducers/settings.reducer';
import { Form, FormGroup, ControlLabel, FormControl, Checkbox, Button } from 'react-bootstrap';
import _ from 'lodash';
import Accordion from './cfl-accordion';
import { ITemplate } from '../shared/model/template.model';

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
      if (config.name === configName) {
        config.props = config.props.map((prop: IProp) => {
          if (prop.name === propName) {
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

  getPropsForTemplate = (templateName: string): Array<IProp> => {
    const template = this.findTemplate(templateName)
    return template && template.configurables.map(conf => ({
      name: conf,
      value: null
    }));
  }

  findTemplate = (name: string): ITemplate => this.props.templates.find(template => template.name === name);

  setDefaultConfigName = (configName: string) => this.props.updateConfigs(this.props.configs, configName);

  deleteConfig = (name: string) => {
    const newConfigs: Array<IConfig> = this.props.configs;
    const index = newConfigs.indexOf(newConfigs.find(config => config.name === name));
    if (index > -1) {
      newConfigs.splice(index, 1);
    }
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
    return selectedTemplate && selectedTemplate.configurables.map(conf => this.renderProp(conf, config, isDefault, index));
  };

  renderProp = (propName: string, config: IConfig, isDefault: boolean, index: number) => {
    const prop: IProp = config.props.find(p => p.name === propName);
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
      <Accordion key={index} title={config.name} fasIcon={isDefault && ['fas', 'star']}>
        {this.renderConfigForm(config, isDefault, index)}
        <Button 
          className="btn confirm btn-xs" 
          onClick={e => this.setDefaultConfigName(config.name)}
          disabled={isDefault}
        >
          Set Default
        </Button>
        <Button 
          className="btn cancel btn-xs" 
          onClick={e => this.deleteConfig(config.name)}
        >
          Delete
        </Button>
      </Accordion>
    );
  };

  renderConfigs = () => this.props.configs.map((config: IConfig, index: number) => this.renderConfig(config, index));

  renderControlButtons = () => (
    <div className="u-mt-15">
      <Button
        className="btn btn-xs" 
        onClick={this.componentDidMount}>
          Cancel
      </Button>
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
    <>
      {loading ? (
        <div>LOADING</div>
      ) : (
        <>
          {this.renderConfigs()}
          {this.renderControlButtons()}
        </>
      )}
    </>
    )
  };
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
  updateConfigs,
  updateState,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Settings);