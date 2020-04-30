import React from 'react';
import { connect } from 'react-redux';
import {ConfigUI, IConfig, IProp} from '../../shared/model/config.model';
import {
  getConfigs,
  getTemplates,
  updateConfigs,
  updateState,
  openModal,
  closeModal,
  reset,
  addNewConfig
} from '../../reducers/settings.reducer';
import * as Yup from 'yup';
import { validateForm, validateField } from '../../utils/validation-util';
import { Form, FormGroup, ControlLabel, FormControl, Checkbox, Button, Row, Col } from 'react-bootstrap';
import _ from 'lodash';
import Accordion from '../cfl-accordion';
import { ITemplate } from '../../shared/model/template.model';
import RemoveButton from '../remove-button';
import OpenMRSModal from '../open-mrs-modal';
import ErrorDesc from '../ErrorDesc';
import * as Msg from '../../utils/messages'
import 'bootstrap/dist/css/bootstrap.min.css';
import { errorToast } from '../../utils/toast-display-util';

interface ISettingsProps extends StateProps, DispatchProps {}

interface ISettingsState {
  configs: Array<ConfigUI>,
  errors?: IConfig
};

class Settings extends React.PureComponent <ISettingsProps, ISettingsState> {

  constructor(props: ISettingsProps) {
    super(props);
    this.handleSubmitConfigs = this.handleSubmitConfigs.bind(this);
    this.state = {
      configs: props.configs
    }
  };

  validationSchema = Yup.object().shape({
    name: Yup.string()
      .required(Msg.FIELD_REQUIRED)
      .test('unique check', Msg.CONFIG_FORM_NAME_IS_NOT_UNIQUE, nameToValidate => {
          const { configs } = this.props;
          return _(configs)
              .filter(configForm => configForm.name === nameToValidate)
              .size() === 1;
        }),
    templateName: Yup.string()
      .required(Msg.FIELD_REQUIRED),
    maxRetries: Yup.string()
      .matches(new RegExp('^[0-9]\\d*$'), Msg.POSITIVE_NUMBERS_REQUIRED)
      .required(Msg.FIELD_REQUIRED),
    splitHeader: Yup.string()
      .required(Msg.FIELD_REQUIRED),
    splitFooter: Yup.string()
      .required(Msg.FIELD_REQUIRED)
  });

  componentDidMount = () => {
    this.props.reset();
    this.props.getConfigs();
    this.props.getTemplates();
  };

  handleSubmitConfigs = (event) => {
    event.preventDefault();

    const { updateConfigs, configs } = this.props;
    const newConfigs = _.clone(configs);

    const validationPromises = newConfigs.map(configFormData => {
      return validateForm(configFormData, this.validationSchema)
          .then(() => {
            configFormData.errors = null;
          })
          .catch((errors) => {
            configFormData.errors = errors;
            this.setState({
              errors: errors
            })
            return Promise.reject()
          })
    });
    Promise.all(validationPromises)
        .then(() => {
          updateConfigs(newConfigs);
        })
        .catch(() => {
          errorToast(Msg.GENERIC_INVALID_FORM);
        })
  }

  handleChange = (localId: string, fieldName: string, value: any, isDefault: boolean) => {
    const newConfigs: Array<ConfigUI> = this.props.configs.map((config: ConfigUI) => {
      if (config.localId === localId) {
        config[fieldName] = value;
        config.isDefault = isDefault;
        if (fieldName === 'templateName') {
          config.props = this.getPropsForTemplate(value);
          config.props.map((prop: IProp) => {
            prop.value = "";
          });
        }
        return config;
      }
      return config;
    });

    let form = {};
    form[fieldName] = value;

    validateField(form, fieldName, this.validationSchema)
        .then(() => {
          this.props.updateState(newConfigs);
          this.setState({
            errors: undefined
          });
        })
        .catch((errors) => {
          this.setState({
            errors
          });
        });
  };

  handlePropChange = (localId: string, propName: string, value: string, isDefault: boolean) => {
    const newConfigs: Array<ConfigUI> = this.props.configs.map((config: ConfigUI) => {
      if (config && config.localId === localId) {
        config.props = config.props && config.props.map((prop: IProp) => {
          if (prop && prop.name === propName) {
            prop.value = value;
            return prop;
          }
          return prop;
        });
        config.isDefault = isDefault;
        return config;
      }
      return config;
    });
    this.props.updateState(newConfigs);
  };

  handleRemove = (configLocalId: string) => this.props.openModal(configLocalId);

  handleClose = () => this.props.closeModal();

  handleConfirm = () => this.deleteConfig(this.props.configLocalIdToDelete);

  getPropsForTemplate = (templateName: string): Array<IProp> => {
    const template = this.findTemplate(templateName)
    return template && template.configurables ? template.configurables.map(conf => ({
      name: conf,
      value: null
    })) : [];
  };

  findTemplate = (name: string | undefined): ITemplate => this.props.templates.find(template => template.name === name);

  setAsDefault = (newDefaultConfig: ConfigUI) =>  {
    const newConfigs: Array<ConfigUI> = this.props.configs.map((configUI: ConfigUI) => {
      configUI.isDefault = configUI.localId === newDefaultConfig.localId;
      return configUI;
    });
    this.props.updateConfigs(newConfigs);
  }

  deleteConfig = (localId: string) => {
    const newConfigs = _.filter(this.props.configs, (config: ConfigUI) => config.localId !== localId);
    this.props.updateState(newConfigs);
    this.props.updateConfigs(newConfigs);
  };

  saveConfigs = () => this.props.updateConfigs(this.props.configs);

  addConfig = () => this.props.addNewConfig();

  renderInput = (config: ConfigUI, fieldName: string, inputType: string, label: string, isDefault: boolean, index: number) => {
    return (
    <FormGroup controlId={`${fieldName}_${index}`}>
        <ControlLabel>{label}</ControlLabel>
        <FormControl
            type={inputType}
            name={fieldName}
            value={config[fieldName]}
            onChange={e => this.handleChange(config.localId, fieldName, e.target.value, isDefault)} />
      {this.renderError(fieldName)}
    </FormGroup>
    );
  };

  renderCheckbox = (config: ConfigUI, fieldName: string, label: string, isDefault: boolean, index: number) => (
      <FormGroup controlId={`${fieldName}_${index}`}>
        <ControlLabel>{label}</ControlLabel>
        <Checkbox name={fieldName} checked={config[fieldName]}
          onChange={e => this.handleChange(config.localId, fieldName, e.target.checked, isDefault)} />
      </FormGroup>
  );

  renderConfigForm = (config: ConfigUI, isDefault: boolean, index: number) => (
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

  renderProps = (config: ConfigUI, isDefault: boolean, index: number) => {
    const selectedTemplate = this.findTemplate(config.templateName);
    return selectedTemplate && selectedTemplate.configurables &&
      selectedTemplate.configurables.map(conf => this.renderProp(conf, config, isDefault, index));
  };

  renderProp = (propName: string, config: ConfigUI, isDefault: boolean, index: number) => {
    const prop: IProp | undefined = config && config.props && config.props.find(p => p.name === propName);
    return (
      <FormGroup controlId={`${propName}_${index}`}>
        <ControlLabel>{`${propName}:`}</ControlLabel>
        <FormControl type="text" name={propName} value={prop ? prop.value : ''}
          onChange={e => this.handlePropChange(config.localId, propName, e.target.value, isDefault)} />
      </FormGroup>
    );
  };

  renderDropdown = (config: ConfigUI, fieldName: string, label: string, index: number, isDefault: boolean) => {
    const templates: ReadonlyArray<ITemplate> = this.props.templates;
    const formClass = 'form-control openmrs-textarea';
    const errorFormClass = formClass + ' error-field';
    const errors = this.state ? this.state.errors : null;
    return (
      <FormGroup controlId={`${fieldName}_${index}`}>
        <ControlLabel>{label}</ControlLabel>
        <FormControl type="select"
          componentClass="select"
          name={fieldName}
          value={config.templateName}
          onChange={e => this.handleChange(config.localId, fieldName, e.target.value, isDefault)} >
          <option key='empty'></option>
          {templates.map(template => <option key={template.name}>{template.name}</option>)}
          className={errors ? errorFormClass : formClass}
        </FormControl>
        {this.renderError(fieldName)}
      </FormGroup>
    );
  };

  renderConfig = (config: ConfigUI, index: number) => {
    const isDefault = config.isDefault;
    return (
      <Row key={index}>
        <Col sm={11}>
          <Accordion key={index} title={config.name} config={config} border
            setDefaultCallback={this.setAsDefault}
          >
            {this.renderConfigForm(config, isDefault, index)}
          </Accordion>
        </Col>
        <Col sm={1}>
          <RemoveButton
            handleRemove={e => this.handleRemove(config.localId)}
            localId={config.localId}
            tooltip="Delete SMS configuration" />
        </Col>
      </Row>
    );
  };

  renderConfigs = () => this.props.configs.map((config: ConfigUI, index: number) => this.renderConfig(config, index));

  renderSaveButton = () => (
    <Row className="col-sm-11 u-mt-15">
      <Button
        className="btn confirm btn-xs"
        onClick={this.handleSubmitConfigs}>
        Save
      </Button>
    </Row>
  );

  renderHeaderButtons = () => (
    <Row className="col-sm-11 u-mb-15 d-flex flex-row align-items-stretch">
      <span>{this.renderAddButton()}</span>
      <span>{this.renderImportButton()}</span>
    </Row>
  );

  renderAddButton = () => (
    <Button
      className="btn confirm btn-xs"
      onClick={this.addConfig}>
        Add Configuration
    </Button>
  );

  renderImportButton = () => (
    <a className="button import-templates" href="#/settings/templates/import">
      <i className="icon-upload-alt" />
      &nbsp;
      {Msg.SMS_SETTINGS_IMPORT_ADDITIONAL_TEMPLATES}
    </a>
  );

  renderError(fieldName: string) {
    if (this.state.errors) {
      return <ErrorDesc field={this.state.errors[fieldName]} />
    }
  }

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
          <Col xs="12" md="12">
            <h2>SMS Configurations</h2>
          </Col>
        </Row>
        <div className="panel-body">
          {this.renderHeaderButtons()}
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
  defaultConfigLocalId: state.settings.defaultConfigLocalId,
  loading: state.settings.loading,
  showModal: state.settings.showModal,
  configLocalIdToDelete: state.settings.configLocalIdToDelete
});

const mapDispatchToProps = {
  getConfigs,
  getTemplates,
  updateConfigs,
  updateState,
  openModal,
  closeModal,
  reset,
  addNewConfig
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Settings);