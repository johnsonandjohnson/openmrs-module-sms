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
import {
  Col,
  Row
} from 'react-bootstrap';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import Tile from './Tile';

// Enable notification mechanism
toast.configure();

class App extends React.Component {
  render() {
    return (
      <div className="body-wrapper">
        <Row>
          <Col md="12" xs="12">
            <h2>SMS</h2>
          </Col>
        </Row>
        <div className="panel-body">
          <Tile name='Send' href='#/send' icon={['fas', 'paper-plane']} />
          <Tile name='Settings' href='#/settings' icon={['fas', 'cog']} />
          <Tile name='Logs' href='#/logs' icon={['fas', 'clipboard-list']} />
        </div>
      </div>
    )
  }
}

export default connect()(App);
