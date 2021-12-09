import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Film from './film';
import FilmDetail from './film-detail';
import FilmUpdate from './film-update';
import FilmDeleteDialog from './film-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={FilmUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={FilmUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={FilmDetail} />
      <ErrorBoundaryRoute path={match.url} component={Film} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={FilmDeleteDialog} />
  </>
);

export default Routes;
