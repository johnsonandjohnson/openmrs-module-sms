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

import LogsTable from './logs-table';
import Timezone from '../timezone/timezone';
import * as Default from '../../shared/utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";

export class Logs extends React.Component {
  render() {
    return (
      <div className="body-wrapper">
        <h2>{getIntl().formatMessage({ id: 'SMS_LOGS_BREADCRUMB', defaultMessage: Default.LOGS_BREADCRUMB })}</h2>
        <Timezone />
        <LogsTable />
      </div>
    )
  }
}

export const mapStateToProps = state => ({});

export default connect(mapStateToProps)(Logs);