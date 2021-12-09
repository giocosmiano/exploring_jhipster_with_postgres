import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import FilmActor from './film-actor';
import FilmActorDetail from './film-actor-detail';
import FilmActorUpdate from './film-actor-update';
import FilmActorDeleteDialog from './film-actor-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={FilmActorUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={FilmActorUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={FilmActorDetail} />
      <ErrorBoundaryRoute path={match.url} component={FilmActor} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={FilmActorDeleteDialog} />
  </>
);

export default Routes;
