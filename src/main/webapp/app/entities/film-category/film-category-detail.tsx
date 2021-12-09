import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './film-category.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const FilmCategoryDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const filmCategoryEntity = useAppSelector(state => state.filmCategory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="filmCategoryDetailsHeading">
          <Translate contentKey="dvdrentalApp.filmCategory.detail.title">FilmCategory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{filmCategoryEntity.id}</dd>
          <dt>
            <span id="lastUpdate">
              <Translate contentKey="dvdrentalApp.filmCategory.lastUpdate">Last Update</Translate>
            </span>
          </dt>
          <dd>
            {filmCategoryEntity.lastUpdate ? (
              <TextFormat value={filmCategoryEntity.lastUpdate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="dvdrentalApp.filmCategory.film">Film</Translate>
          </dt>
          <dd>{filmCategoryEntity.film ? filmCategoryEntity.film.id : ''}</dd>
          <dt>
            <Translate contentKey="dvdrentalApp.filmCategory.category">Category</Translate>
          </dt>
          <dd>{filmCategoryEntity.category ? filmCategoryEntity.category.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/film-category" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/film-category/${filmCategoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FilmCategoryDetail;
