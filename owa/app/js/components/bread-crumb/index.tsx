/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React, { ReactFragment } from 'react';
import { connect } from 'react-redux';
import { UnregisterCallback } from 'history';
import { Link, withRouter, RouteComponentProps } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import UrlPattern from 'url-pattern';
import { IRootState } from '../../reducers';
import * as Default from '../../shared/utils/messages';
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";
import './bread-crumb.scss';

const MODULE_ROUTE = '/';
const OMRS_ROUTE = '../../';

const LOGS_ROUTE = new UrlPattern('/logs*');
const SEND_ROUTE = new UrlPattern('/send*');
const SETTINGS_ROUTE = new UrlPattern('/settings*');
const SYSTEM_ADMINISTRATION_ROUTE = `${OMRS_ROUTE}coreapps/systemadministration/systemAdministration.page`;

interface IBreadCrumbProps extends DispatchProps, StateProps, RouteComponentProps {
};

interface IBreadCrumbState {
  current: string
};

class BreadCrumb extends React.PureComponent<IBreadCrumbProps, IBreadCrumbState> {
  unlisten: UnregisterCallback;

  constructor(props) {
    super(props);

    const { history } = this.props;
    this.state = {
      current: history.location.pathname.toLowerCase()
    };
  }

  componentDidMount = () => {
    const { history } = this.props;
    this.unlisten = history.listen((location) => {
      const current = location.pathname.toLowerCase();
      this.setState({ current });
    });
  }

  componentWillUnmount = () => {
    this.unlisten();
  }

  renderDelimiter = () => {
    return (
      <span className="breadcrumb-link-item breadcrumb-delimiter">
        <FontAwesomeIcon size="sm" icon={['fas', 'chevron-right']} />
      </span>);
  }

  renderHomeCrumb = () => {
    return (
      <a href={OMRS_ROUTE} className="breadcrumb-link-item home-crumb">
        <FontAwesomeIcon icon={['fas', 'home']} />
      </a>);
  }

  renderCrumb = (link: string, txt: string, isAbsolute?: boolean) => {
    if (isAbsolute) {
      return (
        <a href={link} className="breadcrumb-link-item" >{txt}</a>
      );
    } else {
      return <Link to={link} className="breadcrumb-link-item">{txt}</Link>;
    }
  }

  renderLastCrumb = (txt: string) => {
    return <span className="breadcrumb-last-item">{txt}</span>;
  }

  getLogsCrumbs = () => {
    return [
      this.renderCrumb(SYSTEM_ADMINISTRATION_ROUTE, getIntl().formatMessage({ id: 'SMS_SYSTEM_ADMINISTRATION_BREADCRUMB', defaultMessage: Default.SYSTEM_ADMINISTRATION_BREADCRUMB }), true),
      this.renderCrumb(MODULE_ROUTE, getIntl().formatMessage({ id: 'SMS_GENERAL_MODULE_BREADCRUMB', defaultMessage: Default.GENERAL_MODULE_BREADCRUMB })),
      this.renderLastCrumb(getIntl().formatMessage({ id: 'SMS_LOGS_BREADCRUMB', defaultMessage: Default.LOGS_BREADCRUMB }))
    ];
  }

  getSendCrumbs = () => {
    return [
      this.renderCrumb(SYSTEM_ADMINISTRATION_ROUTE, getIntl().formatMessage({ id: 'SMS_SYSTEM_ADMINISTRATION_BREADCRUMB', defaultMessage: Default.SYSTEM_ADMINISTRATION_BREADCRUMB }), true),
      this.renderCrumb(MODULE_ROUTE, getIntl().formatMessage({ id: 'SMS_GENERAL_MODULE_BREADCRUMB', defaultMessage: Default.GENERAL_MODULE_BREADCRUMB })),
      this.renderLastCrumb(getIntl().formatMessage({ id: 'SMS_SEND_BREADCRUMB', defaultMessage: Default.SEND_BREADCRUMB }))
    ];
  }

  getSettingsCrumbs = () => {
    return [
      this.renderCrumb(SYSTEM_ADMINISTRATION_ROUTE, getIntl().formatMessage({ id: 'SMS_SYSTEM_ADMINISTRATION_BREADCRUMB', defaultMessage: Default.SYSTEM_ADMINISTRATION_BREADCRUMB }), true),
      this.renderCrumb(MODULE_ROUTE, getIntl().formatMessage({ id: 'SMS_GENERAL_MODULE_BREADCRUMB', defaultMessage: Default.GENERAL_MODULE_BREADCRUMB })),
      this.renderLastCrumb(getIntl().formatMessage({ id: 'SMS_SETTINGS_BREADCRUMB', defaultMessage: Default.SETTINGS_BREADCRUMB }))
    ];
  }

  getCrumbs = (path: string): Array<ReactFragment> => {
    if (!!LOGS_ROUTE.match(path.toLowerCase())) {
      return this.getLogsCrumbs();
    } else if (!!SEND_ROUTE.match(path)) {
      return this.getSendCrumbs();
    } else if (!!SETTINGS_ROUTE.match(path)) {
      return this.getSettingsCrumbs();
    } else {
      return [
        this.renderCrumb(SYSTEM_ADMINISTRATION_ROUTE, getIntl().formatMessage({ id: 'SMS_SYSTEM_ADMINISTRATION_BREADCRUMB', defaultMessage: Default.SYSTEM_ADMINISTRATION_BREADCRUMB }), true),
        this.renderLastCrumb(getIntl().formatMessage({ id: 'SMS_GENERAL_MODULE_BREADCRUMB', defaultMessage: Default.GENERAL_MODULE_BREADCRUMB }))
      ];
    }
  }

  renderCrumbs = (elements: Array<ReactFragment>) => {
    const delimiter = this.renderDelimiter();

    return (
      <React.Fragment>
        {this.renderHomeCrumb()}
        {elements.map((e, i) =>
          <React.Fragment key={`crumb-${i}`}>
            {delimiter}
            {e}
          </React.Fragment>)}
      </React.Fragment>
    );
  }

  buildBreadCrumb = () => {
    const { current } = this.state;
    return (
      <div id="breadcrumbs" className="breadcrumb">
        {this.renderCrumbs(this.getCrumbs(current))}
      </div>
    );
  }

  render = () => {
    return this.buildBreadCrumb();
  }
}

const mapStateToProps = ({ }: IRootState) => ({
});

const mapDispatchToProps = ({
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default withRouter(connect(
  mapStateToProps,
  mapDispatchToProps
)(BreadCrumb));
