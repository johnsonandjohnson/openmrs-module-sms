/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React, { ChangeEvent } from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import {
  Modal,
  Button,
  Grid,
  Row,
  Col
} from 'react-bootstrap';
import _ from 'lodash';

import { IRootState } from '../reducers';
import { setFile, reset, uploadFileToImport } from '../reducers/file-import.reducer';
import * as Default from '../utils/messages'
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";

export interface IImportFileModalProps extends StateProps, DispatchProps, RouteComponentProps<{ entityName: string }> {
  title: string,
  description: string,
  reloadCallback: () => void;
}

export class UploadModal extends React.Component<IImportFileModalProps> {

  componentDidMount() {
    this.props.resetFiles();
  }

  handleClose = event => {
    event.stopPropagation();
    this.props.history.goBack();
  };

  clearFile = () => {
    this.props.setFile(undefined);
  };

  handleSelectFile = (event: ChangeEvent<HTMLInputElement>) => {
    const file: File = _.get(event, 'target.files[0]');
    file && this.props.setFile(file);
  }

  handleImport = () => {
    const { entityName } = this.props.match.params;
    const { file, reloadCallback } = this.props;
    if (file) {
      this.props.uploadFileToImport(file, entityName, reloadCallback);
      this.props.history.goBack();
    }
  };

  render() {
    return (
      <Modal show={true} onHide={() => {}}>
        {this.renderModalHeader()}
        {this.renderModalBody()}
        {this.renderModalFooter()}
      </Modal>
    );
  }

  renderModalHeader = () => (
      <Modal.Header>
        <Modal.Title>
          <i className='icon-upload-alt' />
          &nbsp;
          {this.props.title}
        </Modal.Title>
      </Modal.Header>
  );

  renderModalBody() {
    return (
      <Modal.Body>
        {this.renderDescription()}
        <Grid>
          {this.renderFileInput()}
        </Grid>
      </Modal.Body>
    );
  }

  renderModalFooter() {
    const { loading, file } = this.props;
    const isReadyToImport = !loading && !!file;

    return (
      <Modal.Footer>
        <Button className='button confirm right' onClick={this.handleImport} disabled={!isReadyToImport}>
          {getIntl().formatMessage({ id: 'SMS_IMPORT_FILE_MODAL_CONFIRM', defaultMessage: Default.IMPORT_FILE_MODAL_CONFIRM })}
        </Button>
        <Button className='button cancel left' onClick={this.handleClose}>
          {getIntl().formatMessage({ id: 'SMS_IMPORT_FILE_MODAL_CLOSE', defaultMessage: Default.IMPORT_FILE_MODAL_CLOSE })}
        </Button>
      </Modal.Footer>
    );
  }

  renderDescription = () => (
    <p className='modal-description'>
      {this.props.description}
    </p>
  );

  renderFileInput = () => (
    <Row className='u-mt-15'>
      <Col md={2} xs={2}>
        <p>{getIntl().formatMessage({ id: 'SMS_IMPORT_FILE_MODAL_CHOSEN_FILE', defaultMessage: Default.IMPORT_FILE_MODAL_CHOSEN_FILE })}</p>
      </Col>
      <Col>
        <input className='file-upload-input' type='file' onChange={this.handleSelectFile} accept='application/json' />
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ fileImport }: IRootState) => ({
  file: fileImport.file,
  loading: fileImport.loading
});

const mapDispatchToProps = {
  resetFiles: reset,
  setFile: setFile,
  uploadFileToImport
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(UploadModal);