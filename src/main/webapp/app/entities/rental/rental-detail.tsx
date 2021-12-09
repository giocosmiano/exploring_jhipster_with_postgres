import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './rental.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const RentalDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const rentalEntity = useAppSelector(state => state.rental.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="rentalDetailsHeading">
          <Translate contentKey="dvdrentalApp.rental.detail.title">Rental</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{rentalEntity.id}</dd>
          <dt>
            <span id="rentalId">
              <Translate contentKey="dvdrentalApp.rental.rentalId">Rental Id</Translate>
            </span>
          </dt>
          <dd>{rentalEntity.rentalId}</dd>
          <dt>
            <span id="rentalDate">
              <Translate contentKey="dvdrentalApp.rental.rentalDate">Rental Date</Translate>
            </span>
          </dt>
          <dd>{rentalEntity.rentalDate ? <TextFormat value={rentalEntity.rentalDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="returnDate">
              <Translate contentKey="dvdrentalApp.rental.returnDate">Return Date</Translate>
            </span>
          </dt>
          <dd>{rentalEntity.returnDate ? <TextFormat value={rentalEntity.returnDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="lastUpdate">
              <Translate contentKey="dvdrentalApp.rental.lastUpdate">Last Update</Translate>
            </span>
          </dt>
          <dd>{rentalEntity.lastUpdate ? <TextFormat value={rentalEntity.lastUpdate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="dvdrentalApp.rental.inventory">Inventory</Translate>
          </dt>
          <dd>{rentalEntity.inventory ? rentalEntity.inventory.id : ''}</dd>
          <dt>
            <Translate contentKey="dvdrentalApp.rental.customer">Customer</Translate>
          </dt>
          <dd>{rentalEntity.customer ? rentalEntity.customer.id : ''}</dd>
          <dt>
            <Translate contentKey="dvdrentalApp.rental.staff">Staff</Translate>
          </dt>
          <dd>{rentalEntity.staff ? rentalEntity.staff.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/rental" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/rental/${rentalEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default RentalDetail;
