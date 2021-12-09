import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './film.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const FilmDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const filmEntity = useAppSelector(state => state.film.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="filmDetailsHeading">
          <Translate contentKey="dvdrentalApp.film.detail.title">Film</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{filmEntity.id}</dd>
          <dt>
            <span id="filmId">
              <Translate contentKey="dvdrentalApp.film.filmId">Film Id</Translate>
            </span>
          </dt>
          <dd>{filmEntity.filmId}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="dvdrentalApp.film.title">Title</Translate>
            </span>
          </dt>
          <dd>{filmEntity.title}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="dvdrentalApp.film.description">Description</Translate>
            </span>
          </dt>
          <dd>{filmEntity.description}</dd>
          <dt>
            <span id="releaseYear">
              <Translate contentKey="dvdrentalApp.film.releaseYear">Release Year</Translate>
            </span>
          </dt>
          <dd>{filmEntity.releaseYear}</dd>
          <dt>
            <span id="rentalDuration">
              <Translate contentKey="dvdrentalApp.film.rentalDuration">Rental Duration</Translate>
            </span>
          </dt>
          <dd>{filmEntity.rentalDuration}</dd>
          <dt>
            <span id="rentalRate">
              <Translate contentKey="dvdrentalApp.film.rentalRate">Rental Rate</Translate>
            </span>
          </dt>
          <dd>{filmEntity.rentalRate}</dd>
          <dt>
            <span id="length">
              <Translate contentKey="dvdrentalApp.film.length">Length</Translate>
            </span>
          </dt>
          <dd>{filmEntity.length}</dd>
          <dt>
            <span id="replacementCost">
              <Translate contentKey="dvdrentalApp.film.replacementCost">Replacement Cost</Translate>
            </span>
          </dt>
          <dd>{filmEntity.replacementCost}</dd>
          <dt>
            <span id="rating">
              <Translate contentKey="dvdrentalApp.film.rating">Rating</Translate>
            </span>
          </dt>
          <dd>{filmEntity.rating}</dd>
          <dt>
            <span id="lastUpdate">
              <Translate contentKey="dvdrentalApp.film.lastUpdate">Last Update</Translate>
            </span>
          </dt>
          <dd>{filmEntity.lastUpdate ? <TextFormat value={filmEntity.lastUpdate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="specialFeatures">
              <Translate contentKey="dvdrentalApp.film.specialFeatures">Special Features</Translate>
            </span>
          </dt>
          <dd>{filmEntity.specialFeatures}</dd>
          <dt>
            <span id="fulltext">
              <Translate contentKey="dvdrentalApp.film.fulltext">Fulltext</Translate>
            </span>
          </dt>
          <dd>{filmEntity.fulltext}</dd>
          <dt>
            <Translate contentKey="dvdrentalApp.film.language">Language</Translate>
          </dt>
          <dd>{filmEntity.language ? filmEntity.language.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/film" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/film/${filmEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FilmDetail;
