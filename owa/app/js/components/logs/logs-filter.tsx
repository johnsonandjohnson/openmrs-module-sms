/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import moment from 'moment';
import React from 'react';
import {
  Col,
  Row
} from 'react-bootstrap';
import { MIN_DATE_TIME, DATE_TIME_PATTERN } from '../../constants';

export enum TimePeriod {
  ALL = 'All',
  TODAY = 'Today',
  PAST_7_DAYS = 'Past 7 days',
  THIS_MONTH = 'This month',
  THIS_YEAR = 'This year'
}

export interface ILogsFiltersProps {
  filtersChangedCallback?(filters: {}): void;
};

export interface ILogsFiltersState {
  phoneNumber?: string
  timePeriod?: TimePeriod;
}

export default class LogsFilters extends React.PureComponent<ILogsFiltersProps, ILogsFiltersState> {
  constructor(props) {
    super(props);
    this.state = {
      phoneNumber: '',
      timePeriod: TimePeriod.ALL
    };
  }

  onPhoneNumberFilterChange = (event) => {
    this.setState({ phoneNumber: event.target.value })
    this.props.filtersChangedCallback && this.props.filtersChangedCallback({ phoneNumber: event.target.value });
  }

  onDateFilterFilterChange = (event) => {
    this.setState({ timePeriod: event.target.value })
    let startDate = moment(moment.now());
    switch (event.target.value as TimePeriod) {
      case TimePeriod.ALL:
        startDate = moment(MIN_DATE_TIME);
        break;
      case TimePeriod.TODAY:
        startDate = startDate.startOf('day');
        break;
      case TimePeriod.PAST_7_DAYS:
        startDate = startDate.subtract(7, 'days');
        break;
      case TimePeriod.THIS_MONTH:
        startDate = startDate.startOf('month');
        break;
      case TimePeriod.THIS_YEAR:
        startDate = startDate.startOf('year');
        break;
    }
    this.props.filtersChangedCallback && this.props.filtersChangedCallback({
      timeFrom: startDate.format(DATE_TIME_PATTERN),
      timeTo: moment(moment.now()).format(DATE_TIME_PATTERN)
    });
  }

  render = () =>
    <Row>
      <Col sm={7}>
        <Row>
          <Col sm={6}>
            Search Recipient phone number
          </Col>
          <Col sm={6}>
            Select Timestamp
          </Col>
        </Row>
        <Row className="u-mb-10">
          <Col sm={6}>
            <input className="search-field u-fill-col" type="text" onChange={this.onPhoneNumberFilterChange} value={this.state.phoneNumber} />
          </Col>
          <Col sm={6}>
            <select className="u-fill-col" onChange={this.onDateFilterFilterChange} value={this.state.timePeriod}>
              <option>{TimePeriod.ALL}</option>
              <option>{TimePeriod.TODAY}</option>
              <option>{TimePeriod.PAST_7_DAYS}</option>
              <option>{TimePeriod.THIS_MONTH}</option>
              <option>{TimePeriod.THIS_YEAR}</option>
            </select>
          </Col>
        </Row>
      </Col>
    </Row>;
}
