import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import FilmCategory from './film-category';
import FilmCategoryDetail from './film-category-detail';
import FilmCategoryUpdate from './film-category-update';
import FilmCategoryDeleteDialog from './film-category-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={FilmCategoryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={FilmCategoryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={FilmCategoryDetail} />
      <ErrorBoundaryRoute path={match.url} component={FilmCategory} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={FilmCategoryDeleteDialog} />
  </>
);

export default Routes;
