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

import { getLogs } from '../../reducers/logs.reducer';
import { IRootState } from '../../reducers';
import Table from '../table';
import LogsFilters, { TimePeriod } from './logs-filter';

export interface ILogsTableProps extends StateProps, DispatchProps {};

export interface ILogsTableState {
  timePeriod: TimePeriod
};

export class LogsTable extends React.PureComponent<ILogsTableProps, ILogsTableState> {
  constructor(props) {
    super(props);
    this.state = {
      timePeriod: TimePeriod.ALL
    };
  }

  dateFilterChanged = (val: TimePeriod) => this.setState({timePeriod: val});

  render = () => {
    const columns = [
      {
        Header: 'ID',
        accessor: 'id',
      },
      {
        Header: 'Phone Number',
        accessor: 'phoneNumber'
      },
      {
        Header: 'Message Content',
        accessor: 'messageContent'
      },
      {
        Header: 'SMS Direction',
        accessor: 'smsDirection'
      },
      {
        Header: 'Timestamp',
        accessor: 'timestamp'
      },
      {
        Header: 'Provider ID',
        accessor: 'providerId'
      },
      {
        Header: 'Provider Status',
        accessor: 'providerStatus'
      },
      {
        Header: 'Config',
        accessor: 'config'
      },
      {
        Header: 'Creation Date',
        accessor: 'creationDate',
        id: 'dateCreated'
      },
      {
        Header: 'Delivery Status',
        accessor: 'deliveryStatus'
      },
      {
        Header: 'Error Message',
        accessor: 'errorMessage'
      },
      {
        Header: 'Modification Date',
        accessor: 'modificationDate',
        id: 'dateChanged'
      },
      {
        Header: 'Modified By',
        accessor: 'modifiedBy',
        id: 'changedBy'
      },
      {
        Header: 'OpenMRS ID',
        accessor: 'openMrsId'
      }
    ];

    return (
      <Table
        filtersComponent={LogsFilters}
        filterProps={{dateFilterChanged: this.dateFilterChanged, timePeriod: this.state.timePeriod}}
        data={this.props.data}
        columns={columns}
        loading={this.props.loading}
        pages={this.props.pages}
        fetchDataCallback={this.props.getLogs}
      />
    );
  }
}

const mapStateToProps = ({ logs }: IRootState) => ({
  pages: logs.pages,
  data: logs.data,
  loading: logs.loading
})

const mapDispatchToProps = {
  getLogs
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(LogsTable);