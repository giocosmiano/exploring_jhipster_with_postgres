import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './film-actor.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const FilmActorDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const filmActorEntity = useAppSelector(state => state.filmActor.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="filmActorDetailsHeading">
          <Translate contentKey="dvdrentalApp.filmActor.detail.title">FilmActor</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{filmActorEntity.id}</dd>
          <dt>
            <span id="lastUpdate">
              <Translate contentKey="dvdrentalApp.filmActor.lastUpdate">Last Update</Translate>
            </span>
          </dt>
          <dd>
            {filmActorEntity.lastUpdate ? <TextFormat value={filmActorEntity.lastUpdate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="dvdrentalApp.filmActor.actor">Actor</Translate>
          </dt>
          <dd>{filmActorEntity.actor ? filmActorEntity.actor.id : ''}</dd>
          <dt>
            <Translate contentKey="dvdrentalApp.filmActor.film">Film</Translate>
          </dt>
          <dd>{filmActorEntity.film ? filmActorEntity.film.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/film-actor" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/film-actor/${filmActorEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FilmActorDetail;
