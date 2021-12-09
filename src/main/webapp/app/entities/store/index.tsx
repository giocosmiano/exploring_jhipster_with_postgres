import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Store from './store';
import StoreDetail from './store-detail';
import StoreUpdate from './store-update';
import StoreDeleteDialog from './store-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={StoreUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={StoreUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={StoreDetail} />
      <ErrorBoundaryRoute path={match.url} component={Store} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={StoreDeleteDialog} />
  </>
);

export default Routes;
