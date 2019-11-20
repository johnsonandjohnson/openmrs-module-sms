
import React from 'react';
import { Route, Switch } from 'react-router-dom';
import Settings from './settings';
import ImportFileModal from '../import-file-modal';
import { getTemplates } from '../../reducers/settings.reducer';
import * as Msg from '../../utils/messages'

const Routes = ({ match }) => (
  <>
    <Switch>
      <Route path={`${match.url}`} component={Settings} />
    </Switch>
    <Route exact path={`${match.url}/:entityName/import`}
      render={(props) =>
        <ImportFileModal {...props}
          title={Msg.IMPORT_CONFIG_TEMPLATES_MODAL_TITLE}
          description={Msg.IMPORT_CONFIG_TEMPLATES_MODAL_DESCRIPTION}
          reloadCallback={getTemplates} />} />
  </>
);

export default Routes;
