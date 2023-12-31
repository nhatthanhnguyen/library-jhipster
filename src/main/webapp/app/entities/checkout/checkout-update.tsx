import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getReaderUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getAllEntities as getBookCopies } from 'app/entities/book-copy/book-copy.reducer';
import { createEntity, getEntity, reset, updateEntity } from './checkout.reducer';

export const CheckoutUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const bookCopies = useAppSelector(state => state.bookCopy.entities);
  const checkoutEntity = useAppSelector(state => state.checkout.entity);
  const loading = useAppSelector(state => state.checkout.loading);
  const updating = useAppSelector(state => state.checkout.updating);
  const updateSuccess = useAppSelector(state => state.checkout.updateSuccess);

  const handleClose = () => {
    navigate('/checkout' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getReaderUsers());
    dispatch(getBookCopies());
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.startTime = convertDateTimeToServer(values.startTime);
    values.endTime = convertDateTimeToServer(values.endTime);

    const entity = {
      ...checkoutEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user.toString()),
      bookCopy: bookCopies.find(it => it.id.toString() === values.bookCopy.toString()),
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
          startTime: displayDefaultDateTime(),
          endTime: displayDefaultDateTime(),
        }
      : {
          ...checkoutEntity,
          startTime: convertDateTimeFromServer(checkoutEntity.startTime),
          endTime: convertDateTimeFromServer(checkoutEntity.endTime),
          user: checkoutEntity?.user?.id,
          bookCopy: checkoutEntity?.bookCopy?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="libraryApp.checkout.home.createLabel" data-cy="CheckoutCreateUpdateHeading">
            <Translate contentKey="libraryApp.checkout.home.createLabel">Create or edit a Checkout</Translate>
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
                  id="checkout-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('libraryApp.checkout.startTime')}
                id="checkout-startTime"
                name="startTime"
                data-cy="startTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('libraryApp.checkout.endTime')}
                id="checkout-endTime"
                name="endTime"
                data-cy="endTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('libraryApp.checkout.isReturned')}
                id="checkout-isReturned"
                name="isReturned"
                data-cy="isReturned"
                check
                type="checkbox"
              />
              <ValidatedField id="checkout-user" name="user" data-cy="user" label={translate('libraryApp.checkout.user')} type="select">
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {`${otherEntity.login} - ${otherEntity.lastName} ${otherEntity.firstName}`}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="checkout-bookCopy"
                name="bookCopy"
                data-cy="bookCopy"
                label={translate('libraryApp.checkout.bookCopy')}
                type="select"
              >
                <option value="" key="0" />
                {bookCopies
                  ? bookCopies.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {`${otherEntity.id} - ${otherEntity.book.title} - ${otherEntity.book.publisher.name}`}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/checkout" replace color="info">
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

export default CheckoutUpdate;
