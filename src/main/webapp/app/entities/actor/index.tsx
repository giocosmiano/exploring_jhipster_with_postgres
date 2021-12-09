import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Actor from './actor';
import ActorDetail from './actor-detail';
import ActorUpdate from './actor-update';
import ActorDeleteDialog from './actor-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ActorUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ActorUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ActorDetail} />
      <ErrorBoundaryRoute path={match.url} component={Actor} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ActorDeleteDialog} />
  </>
);

export default Routes;
