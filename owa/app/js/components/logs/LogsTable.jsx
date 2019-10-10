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
import ReactTable from 'react-table';

import { getLogs, onPageSizeChange, onPageChange, reset } from '../../reducers/logsReducer';
import { Cell, FIELDS } from './LogsHelper';

export class LogsTable extends React.PureComponent {

  componentDidMount(prev) {
    this.props.getLogs(this.props);
  }

  componentDidUpdate(prev) {
    if (prev.page !== this.props.page) {
      this.props.getLogs(this.props);
    }
  }

  getTableData = () => {
    const { data } = this.props;
    return data;
  }

  selectedRowsClassName = (rowInfo) => {
    const { selectedRows } = this.props;
    let className = '';

    if (selectedRows && selectedRows.length) {
      if (rowInfo !== undefined && selectedRows.includes(rowInfo.row._id)) {
        className = 'selected-row';
      }
    }

    return className;
  }

  renderColumnFilter = () => {
    const { filterType } = this.props;

    if (filterType === 'both' || filterType === 'column') {
      return true;
    }

    return false;
  }

  renderData = () => {
    const { filters, data, getDataWithFilters } = this.props;

    if (typeof getDataWithFilters !== 'undefined') {
      return getDataWithFilters(filters, data);
    }
    return data;
  }

  renderColumns = () => {
    const columnMetadata = FIELDS.map(columnName => ({
      Header:
        <span id={columnName}>{columnName}</span>,
      accessor: '',
      filterAll: false,
      Cell: data => <Cell {...data} columnName={columnName} />,
      width: 100
    }));

    const displayColumns = columnMetadata.map(element => Object.assign({}, element, {
      minWidth: undefined,
    }));
    return displayColumns;
  }

  renderPaginationBottom = () => {
    if (this.getTableData() !== null && this.getTableData().length === 0) {
      return false;
    }

    return true;
  }


  render = () => {
    const defaultClassName = this.props.defaultClassName || '-striped -highlight';

    // See also https://github.com/tannerlinsley/react-table/blob/master/docs/api.md
    return (
      <div>
        <ReactTable
          className={this.props.tableClassName || defaultClassName}
          collapseOnDataChange={false}
          columns={this.renderColumns()}
          data={this.renderData()}
          defaultPageSize={this.props.pageSize}
          filterable={this.renderColumnFilter()}
          getPaginationProps={this.props.getPaginationProps}
          getTableProps={this.props.getTableProps}
          getTheadProps={this.props.getTheadProps}
          getTrGroupProps={this.props.getTrGroupProps}
          getTrProps={(state, rowInfo, column, instance) => {
            if (this.props.getTrProps && rowInfo) {
              this.props.getTrProps(rowInfo);
            }
            return {
              onClick: (e) => {
                const { rowOnClick } = this.props;
                if (typeof rowOnClick !== 'undefined') {
                  rowOnClick(rowInfo.original);
                }
              },
              className: this.selectedRowsClassName(rowInfo)
            };
          }}
          manual={true}
          loadingText={this.props.loadingText}
          loading={this.props.loading}
          minRows={this.props.minRows}
          nextText={this.props.nextText}
          noDataText={<span className='sortableTable-noDataText'>{this.props.noDataMessage}</span>}
          ofText={this.props.ofText}
          page={this.props.page}
          pages={this.props.pages}
          pageSizeOptions={this.props.pageSizeOptions}
          showPageSizeOptions={this.props.showPageSizeOptions}
          pageText={this.props.pageText}
          previousText={this.props.previousText}
          rowsText={this.props.rowsText}
          showPagination={this.props.showPagination}
          showPaginationBottom={this.renderPaginationBottom()}
          showPaginationTop={this.props.showPaginationTop}
          sortable={this.props.isSortable}
          onPageSizeChange={this.props.onPageSizeChange}
          onPageChange={this.props.onPageChange}
          SubComponent={this.props.subComponent}
        />
      </div>
    );
  }
}

const mapStateToProps = state => (state.logsReducer);

const mapDispatchToProps = {
  getLogs,
  reset,
  onPageSizeChange,
  onPageChange
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(LogsTable);