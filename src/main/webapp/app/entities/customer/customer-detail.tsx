import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './customer.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CustomerDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const customerEntity = useAppSelector(state => state.customer.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="customerDetailsHeading">
          <Translate contentKey="dvdrentalApp.customer.detail.title">Customer</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{customerEntity.id}</dd>
          <dt>
            <span id="customerId">
              <Translate contentKey="dvdrentalApp.customer.customerId">Customer Id</Translate>
            </span>
          </dt>
          <dd>{customerEntity.customerId}</dd>
          <dt>
            <span id="storeId">
              <Translate contentKey="dvdrentalApp.customer.storeId">Store Id</Translate>
            </span>
          </dt>
          <dd>{customerEntity.storeId}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="dvdrentalApp.customer.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{customerEntity.firstName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="dvdrentalApp.customer.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{customerEntity.lastName}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="dvdrentalApp.customer.email">Email</Translate>
            </span>
          </dt>
          <dd>{customerEntity.email}</dd>
          <dt>
            <span id="activebool">
              <Translate contentKey="dvdrentalApp.customer.activebool">Activebool</Translate>
            </span>
          </dt>
          <dd>{customerEntity.activebool ? 'true' : 'false'}</dd>
          <dt>
            <span id="createDate">
              <Translate contentKey="dvdrentalApp.customer.createDate">Create Date</Translate>
            </span>
          </dt>
          <dd>
            {customerEntity.createDate ? <TextFormat value={customerEntity.createDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="lastUpdate">
              <Translate contentKey="dvdrentalApp.customer.lastUpdate">Last Update</Translate>
            </span>
          </dt>
          <dd>
            {customerEntity.lastUpdate ? <TextFormat value={customerEntity.lastUpdate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="active">
              <Translate contentKey="dvdrentalApp.customer.active">Active</Translate>
            </span>
          </dt>
          <dd>{customerEntity.active}</dd>
          <dt>
            <Translate contentKey="dvdrentalApp.customer.address">Address</Translate>
          </dt>
          <dd>{customerEntity.address ? customerEntity.address.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/customer" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/customer/${customerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CustomerDetail;
