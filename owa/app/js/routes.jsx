/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { Header } from '@openmrs/react-components';
import PrivateRoute from "@bit/soldevelo-omrs.cfl-components.private-route/private-route";

import App from './components/app';
import Logs from './components/logs/logs';
import BreadCrumb from './components/bread-crumb';
import Send from './components/send/send';
import Settings from './components/settings/index';
import { SMS_PRIVILEGE } from "./config/privileges";
import Customize from '@bit/soldevelo-omrs.cfl-components.customize'
import { initializeLocalizationWrapper } from '@bit/soldevelo-omrs.cfl-components.localization-wrapper';
import messagesEN from "./translations/en.json";

initializeLocalizationWrapper({
  en: messagesEN,
});

export default (store) => (<div>
  <Customize />
  <Header />
  <BreadCrumb />
  <Switch>
    <PrivateRoute path='/logs' component={Logs} requiredPrivilege={SMS_PRIVILEGE} />
    <PrivateRoute path='/send' component={Send} requiredPrivilege={SMS_PRIVILEGE} />
    <PrivateRoute path='/settings' component={Settings} requiredPrivilege={SMS_PRIVILEGE} />
    <PrivateRoute exact path='/' component={App} requiredPrivilege={SMS_PRIVILEGE} />
  </Switch>
</div>);
