
import React from 'react';
import { Route, Switch } from 'react-router-dom';
import Settings from './settings';
import ImportFileModal from '../import-file-modal';
import { getTemplates } from '../../reducers/settings.reducer';
import * as Default from '../../utils/messages'
import { getIntl } from "@openmrs/react-components/lib/components/localization/withLocalization";

const Routes = ({ match }) => (
  <>
    <Switch>
      <Route path={`${match.url}`} component={Settings} />
    </Switch>
    <Route exact path={`${match.url}/:entityName/import`}
      render={(props) =>
        <ImportFileModal {...props}
          title={getIntl().formatMessage({ id: 'SMS_IMPORT_CONFIG_TEMPLATES_MODAL_TITLE', defaultMessage: Default.IMPORT_CONFIG_TEMPLATES_MODAL_TITLE })}
          description={getIntl().formatMessage({ id: 'SMS_IMPORT_CONFIG_TEMPLATES_MODAL_DESCRIPTION', defaultMessage: Default.IMPORT_CONFIG_TEMPLATES_MODAL_DESCRIPTION })}
          reloadCallback={getTemplates} />} />
  </>
);

export default Routes;
