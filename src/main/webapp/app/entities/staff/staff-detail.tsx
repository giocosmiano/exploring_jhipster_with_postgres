import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './staff.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const StaffDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const staffEntity = useAppSelector(state => state.staff.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="staffDetailsHeading">
          <Translate contentKey="dvdrentalApp.staff.detail.title">Staff</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{staffEntity.id}</dd>
          <dt>
            <span id="staffId">
              <Translate contentKey="dvdrentalApp.staff.staffId">Staff Id</Translate>
            </span>
          </dt>
          <dd>{staffEntity.staffId}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="dvdrentalApp.staff.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{staffEntity.firstName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="dvdrentalApp.staff.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{staffEntity.lastName}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="dvdrentalApp.staff.email">Email</Translate>
            </span>
          </dt>
          <dd>{staffEntity.email}</dd>
          <dt>
            <span id="storeId">
              <Translate contentKey="dvdrentalApp.staff.storeId">Store Id</Translate>
            </span>
          </dt>
          <dd>{staffEntity.storeId}</dd>
          <dt>
            <span id="active">
              <Translate contentKey="dvdrentalApp.staff.active">Active</Translate>
            </span>
          </dt>
          <dd>{staffEntity.active ? 'true' : 'false'}</dd>
          <dt>
            <span id="username">
              <Translate contentKey="dvdrentalApp.staff.username">Username</Translate>
            </span>
          </dt>
          <dd>{staffEntity.username}</dd>
          <dt>
            <span id="password">
              <Translate contentKey="dvdrentalApp.staff.password">Password</Translate>
            </span>
          </dt>
          <dd>{staffEntity.password}</dd>
          <dt>
            <span id="lastUpdate">
              <Translate contentKey="dvdrentalApp.staff.lastUpdate">Last Update</Translate>
            </span>
          </dt>
          <dd>{staffEntity.lastUpdate ? <TextFormat value={staffEntity.lastUpdate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="picture">
              <Translate contentKey="dvdrentalApp.staff.picture">Picture</Translate>
            </span>
          </dt>
          <dd>
            {staffEntity.picture ? (
              <div>
                {staffEntity.pictureContentType ? (
                  <a onClick={openFile(staffEntity.pictureContentType, staffEntity.picture)}>
                    <Translate contentKey="entity.action.open">Open</Translate>&nbsp;
                  </a>
                ) : null}
                <span>
                  {staffEntity.pictureContentType}, {byteSize(staffEntity.picture)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="dvdrentalApp.staff.address">Address</Translate>
          </dt>
          <dd>{staffEntity.address ? staffEntity.address.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/staff" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/staff/${staffEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StaffDetail;
