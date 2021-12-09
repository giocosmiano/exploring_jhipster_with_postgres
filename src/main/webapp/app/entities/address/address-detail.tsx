import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './address.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const AddressDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const addressEntity = useAppSelector(state => state.address.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="addressDetailsHeading">
          <Translate contentKey="dvdrentalApp.address.detail.title">Address</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{addressEntity.id}</dd>
          <dt>
            <span id="addressId">
              <Translate contentKey="dvdrentalApp.address.addressId">Address Id</Translate>
            </span>
          </dt>
          <dd>{addressEntity.addressId}</dd>
          <dt>
            <span id="address">
              <Translate contentKey="dvdrentalApp.address.address">Address</Translate>
            </span>
          </dt>
          <dd>{addressEntity.address}</dd>
          <dt>
            <span id="address2">
              <Translate contentKey="dvdrentalApp.address.address2">Address 2</Translate>
            </span>
          </dt>
          <dd>{addressEntity.address2}</dd>
          <dt>
            <span id="district">
              <Translate contentKey="dvdrentalApp.address.district">District</Translate>
            </span>
          </dt>
          <dd>{addressEntity.district}</dd>
          <dt>
            <span id="postalCode">
              <Translate contentKey="dvdrentalApp.address.postalCode">Postal Code</Translate>
            </span>
          </dt>
          <dd>{addressEntity.postalCode}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="dvdrentalApp.address.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{addressEntity.phone}</dd>
          <dt>
            <span id="lastUpdate">
              <Translate contentKey="dvdrentalApp.address.lastUpdate">Last Update</Translate>
            </span>
          </dt>
          <dd>{addressEntity.lastUpdate ? <TextFormat value={addressEntity.lastUpdate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="dvdrentalApp.address.city">City</Translate>
          </dt>
          <dd>{addressEntity.city ? addressEntity.city.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/address" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/address/${addressEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AddressDetail;
