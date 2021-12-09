import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './store.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const StoreDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const storeEntity = useAppSelector(state => state.store.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="storeDetailsHeading">
          <Translate contentKey="dvdrentalApp.store.detail.title">Store</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{storeEntity.id}</dd>
          <dt>
            <span id="storeId">
              <Translate contentKey="dvdrentalApp.store.storeId">Store Id</Translate>
            </span>
          </dt>
          <dd>{storeEntity.storeId}</dd>
          <dt>
            <span id="lastUpdate">
              <Translate contentKey="dvdrentalApp.store.lastUpdate">Last Update</Translate>
            </span>
          </dt>
          <dd>{storeEntity.lastUpdate ? <TextFormat value={storeEntity.lastUpdate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="dvdrentalApp.store.managerStaff">Manager Staff</Translate>
          </dt>
          <dd>{storeEntity.managerStaff ? storeEntity.managerStaff.id : ''}</dd>
          <dt>
            <Translate contentKey="dvdrentalApp.store.address">Address</Translate>
          </dt>
          <dd>{storeEntity.address ? storeEntity.address.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/store" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/store/${storeEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StoreDetail;
