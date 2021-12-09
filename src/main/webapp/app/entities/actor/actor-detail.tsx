import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './actor.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ActorDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const actorEntity = useAppSelector(state => state.actor.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="actorDetailsHeading">
          <Translate contentKey="dvdrentalApp.actor.detail.title">Actor</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{actorEntity.id}</dd>
          <dt>
            <span id="actorId">
              <Translate contentKey="dvdrentalApp.actor.actorId">Actor Id</Translate>
            </span>
          </dt>
          <dd>{actorEntity.actorId}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="dvdrentalApp.actor.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{actorEntity.firstName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="dvdrentalApp.actor.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{actorEntity.lastName}</dd>
          <dt>
            <span id="lastUpdate">
              <Translate contentKey="dvdrentalApp.actor.lastUpdate">Last Update</Translate>
            </span>
          </dt>
          <dd>{actorEntity.lastUpdate ? <TextFormat value={actorEntity.lastUpdate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/actor" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/actor/${actorEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ActorDetail;
