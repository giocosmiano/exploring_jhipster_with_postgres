import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './inventory.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const InventoryDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const inventoryEntity = useAppSelector(state => state.inventory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="inventoryDetailsHeading">
          <Translate contentKey="dvdrentalApp.inventory.detail.title">Inventory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{inventoryEntity.id}</dd>
          <dt>
            <span id="inventoryId">
              <Translate contentKey="dvdrentalApp.inventory.inventoryId">Inventory Id</Translate>
            </span>
          </dt>
          <dd>{inventoryEntity.inventoryId}</dd>
          <dt>
            <span id="storeId">
              <Translate contentKey="dvdrentalApp.inventory.storeId">Store Id</Translate>
            </span>
          </dt>
          <dd>{inventoryEntity.storeId}</dd>
          <dt>
            <span id="lastUpdate">
              <Translate contentKey="dvdrentalApp.inventory.lastUpdate">Last Update</Translate>
            </span>
          </dt>
          <dd>
            {inventoryEntity.lastUpdate ? <TextFormat value={inventoryEntity.lastUpdate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="dvdrentalApp.inventory.film">Film</Translate>
          </dt>
          <dd>{inventoryEntity.film ? inventoryEntity.film.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/inventory" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/inventory/${inventoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default InventoryDetail;
