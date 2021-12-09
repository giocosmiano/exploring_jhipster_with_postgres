import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ILanguage } from 'app/shared/model/language.model';
import { getEntities as getLanguages } from 'app/entities/language/language.reducer';
import { getEntity, updateEntity, createEntity, reset } from './film.reducer';
import { IFilm } from 'app/shared/model/film.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const FilmUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const languages = useAppSelector(state => state.language.entities);
  const filmEntity = useAppSelector(state => state.film.entity);
  const loading = useAppSelector(state => state.film.loading);
  const updating = useAppSelector(state => state.film.updating);
  const updateSuccess = useAppSelector(state => state.film.updateSuccess);
  const handleClose = () => {
    props.history.push('/film');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getLanguages({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.lastUpdate = convertDateTimeToServer(values.lastUpdate);

    const entity = {
      ...filmEntity,
      ...values,
      language: languages.find(it => it.id.toString() === values.language.toString()),
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
          lastUpdate: displayDefaultDateTime(),
        }
      : {
          ...filmEntity,
          lastUpdate: convertDateTimeFromServer(filmEntity.lastUpdate),
          language: filmEntity?.language?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="dvdrentalApp.film.home.createOrEditLabel" data-cy="FilmCreateUpdateHeading">
            <Translate contentKey="dvdrentalApp.film.home.createOrEditLabel">Create or edit a Film</Translate>
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
                  id="film-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('dvdrentalApp.film.filmId')}
                id="film-filmId"
                name="filmId"
                data-cy="filmId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('dvdrentalApp.film.title')}
                id="film-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('dvdrentalApp.film.description')}
                id="film-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('dvdrentalApp.film.releaseYear')}
                id="film-releaseYear"
                name="releaseYear"
                data-cy="releaseYear"
                type="text"
              />
              <ValidatedField
                label={translate('dvdrentalApp.film.rentalDuration')}
                id="film-rentalDuration"
                name="rentalDuration"
                data-cy="rentalDuration"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('dvdrentalApp.film.rentalRate')}
                id="film-rentalRate"
                name="rentalRate"
                data-cy="rentalRate"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField label={translate('dvdrentalApp.film.length')} id="film-length" name="length" data-cy="length" type="text" />
              <ValidatedField
                label={translate('dvdrentalApp.film.replacementCost')}
                id="film-replacementCost"
                name="replacementCost"
                data-cy="replacementCost"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('dvdrentalApp.film.rating')}
                id="film-rating"
                name="rating"
                data-cy="rating"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('dvdrentalApp.film.lastUpdate')}
                id="film-lastUpdate"
                name="lastUpdate"
                data-cy="lastUpdate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('dvdrentalApp.film.specialFeatures')}
                id="film-specialFeatures"
                name="specialFeatures"
                data-cy="specialFeatures"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('dvdrentalApp.film.fulltext')}
                id="film-fulltext"
                name="fulltext"
                data-cy="fulltext"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                id="film-language"
                name="language"
                data-cy="language"
                label={translate('dvdrentalApp.film.language')}
                type="select"
                required
              >
                <option value="" key="0" />
                {languages
                  ? languages.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/film" replace color="info">
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

export default FilmUpdate;
