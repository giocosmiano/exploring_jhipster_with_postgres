import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IInventory } from 'app/shared/model/inventory.model';
import { getEntities as getInventories } from 'app/entities/inventory/inventory.reducer';
import { ICustomer } from 'app/shared/model/customer.model';
import { getEntities as getCustomers } from 'app/entities/customer/customer.reducer';
import { IStaff } from 'app/shared/model/staff.model';
import { getEntities as getStaff } from 'app/entities/staff/staff.reducer';
import { getEntity, updateEntity, createEntity, reset } from './rental.reducer';
import { IRental } from 'app/shared/model/rental.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const RentalUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const inventories = useAppSelector(state => state.inventory.entities);
  const customers = useAppSelector(state => state.customer.entities);
  const staff = useAppSelector(state => state.staff.entities);
  const rentalEntity = useAppSelector(state => state.rental.entity);
  const loading = useAppSelector(state => state.rental.loading);
  const updating = useAppSelector(state => state.rental.updating);
  const updateSuccess = useAppSelector(state => state.rental.updateSuccess);
  const handleClose = () => {
    props.history.push('/rental');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getInventories({}));
    dispatch(getCustomers({}));
    dispatch(getStaff({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.rentalDate = convertDateTimeToServer(values.rentalDate);
    values.returnDate = convertDateTimeToServer(values.returnDate);
    values.lastUpdate = convertDateTimeToServer(values.lastUpdate);

    const entity = {
      ...rentalEntity,
      ...values,
      inventory: inventories.find(it => it.id.toString() === values.inventory.toString()),
      customer: customers.find(it => it.id.toString() === values.customer.toString()),
      staff: staff.find(it => it.id.toString() === values.staff.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          rentalDate: displayDefaultDateTime(),
          returnDate: displayDefaultDateTime(),
          lastUpdate: displayDefaultDateTime(),
        }
      : {
          ...rentalEntity,
          rentalDate: convertDateTimeFromServer(rentalEntity.rentalDate),
          returnDate: convertDateTimeFromServer(rentalEntity.returnDate),
          lastUpdate: convertDateTimeFromServer(rentalEntity.lastUpdate),
          inventory: rentalEntity?.inventory?.id,
          customer: rentalEntity?.customer?.id,
          staff: rentalEntity?.staff?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="dvdrentalApp.rental.home.createOrEditLabel" data-cy="RentalCreateUpdateHeading">
            <Translate contentKey="dvdrentalApp.rental.home.createOrEditLabel">Create or edit a Rental</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="rental-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('dvdrentalApp.rental.rentalId')}
                id="rental-rentalId"
                name="rentalId"
                data-cy="rentalId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('dvdrentalApp.rental.rentalDate')}
                id="rental-rentalDate"
                name="rentalDate"
                data-cy="rentalDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('dvdrentalApp.rental.returnDate')}
                id="rental-returnDate"
                name="returnDate"
                data-cy="returnDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('dvdrentalApp.rental.lastUpdate')}
                id="rental-lastUpdate"
                name="lastUpdate"
                data-cy="lastUpdate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="rental-inventory"
                name="inventory"
                data-cy="inventory"
                label={translate('dvdrentalApp.rental.inventory')}
                type="select"
                required
              >
                <option value="" key="0" />
                {inventories
                  ? inventories.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="rental-customer"
                name="customer"
                data-cy="customer"
                label={translate('dvdrentalApp.rental.customer')}
                type="select"
                required
              >
                <option value="" key="0" />
                {customers
                  ? customers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="rental-staff"
                name="staff"
                data-cy="staff"
                label={translate('dvdrentalApp.rental.staff')}
                type="select"
                required
              >
                <option value="" key="0" />
                {staff
                  ? staff.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/rental" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default RentalUpdate;
