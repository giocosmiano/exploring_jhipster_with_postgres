import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './language.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const LanguageDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const languageEntity = useAppSelector(state => state.language.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="languageDetailsHeading">
          <Translate contentKey="dvdrentalApp.language.detail.title">Language</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{languageEntity.id}</dd>
          <dt>
            <span id="languageId">
              <Translate contentKey="dvdrentalApp.language.languageId">Language Id</Translate>
            </span>
          </dt>
          <dd>{languageEntity.languageId}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="dvdrentalApp.language.name">Name</Translate>
            </span>
          </dt>
          <dd>{languageEntity.name}</dd>
          <dt>
            <span id="lastUpdate">
              <Translate contentKey="dvdrentalApp.language.lastUpdate">Last Update</Translate>
            </span>
          </dt>
          <dd>
            {languageEntity.lastUpdate ? <TextFormat value={languageEntity.lastUpdate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/language" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/language/${languageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default LanguageDetail;
