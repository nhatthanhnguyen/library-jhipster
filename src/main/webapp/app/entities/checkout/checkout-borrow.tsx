import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Modal, ModalBody, ModalFooter, ModalHeader, Row } from 'reactstrap';
import { Translate, translate, ValidatedField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getReaderUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getAllEntities as getBookCopies } from 'app/entities/book-copy/book-copy.reducer';
import { borrowBook, getEntity, reset } from './checkout.reducer';
import { addToQueue } from 'app/entities/book/book.reducer';
import { toNumber } from 'lodash';

export const CheckoutBorrow = () => {
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
  const [modal, setModal] = useState<boolean>(false);
  const [userId, setUserId] = useState<string>('');
  const [bookCopyId, setBookCopyId] = useState<string>('');

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

  const saveEntity = () => {
    const entity = {
      userId: toNumber(userId),
      bookCopyId: toNumber(bookCopyId),
    };

    if (isNew) {
      dispatch(borrowBook(entity)).then(response => {
        if (response.type.includes('rejected')) {
          setModal(true);
        } else {
          navigate('/checkout' + location.search);
        }
      });
    }
  };

  const confirmWait = () => {
    const entity = {
      userId: toNumber(userId),
      bookId: toNumber(bookCopies.find(it => it.id.toString() === bookCopyId)?.book.id),
    };
    dispatch(addToQueue(entity)).then(() => navigate('/checkout' + location.search));
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="libraryApp.checkout.home.createOrEditLabel" data-cy="CheckoutCreateUpdateHeading">
            <Translate contentKey="libraryApp.checkout.home.createOrEditLabel">Create a Checkout</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <>
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
                id="checkout-user"
                onChange={e => setUserId(e.target.value)}
                name="user"
                data-cy="user"
                label={translate('libraryApp.checkout.user')}
                type="select"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              >
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
                onChange={e => setBookCopyId(e.target.value)}
                name="bookCopy"
                data-cy="bookCopy"
                label={translate('libraryApp.checkout.bookCopy')}
                type="select"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
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
              <Button color="primary" id="save-entity" onClick={saveEntity} data-cy="entityCreateSaveButton" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </>
          )}
        </Col>
      </Row>
      <Modal isOpen={modal} toggle={handleClose}>
        <ModalHeader toggle={handleClose} data-cy="checkoutWaitDialogHeading">
          <Translate contentKey="libraryApp.book.wait.title">Confirm add to queue operation</Translate>
        </ModalHeader>
        <ModalBody id="libraryApp.book.wait.question">
          <Translate
            contentKey="libraryApp.book.wait.question"
            interpolate={{
              title: bookCopies
                ? bookCopyId !== ''
                  ? bookCopies.find(it => it.id.toString() === bookCopyId)?.book.title
                  : 'bookTitle'
                : 'bookTitle',
            }}
          >
            Are you sure you want to add to Queue?
          </Translate>
        </ModalBody>
        <ModalFooter>
          <Button color="secondary" onClick={handleClose}>
            <FontAwesomeIcon icon="ban" />
            &nbsp;
            <Translate contentKey="entity.action.cancel">Cancel</Translate>
          </Button>
          <Button id="jhi-confirm-wait-checkout" data-cy="entityConfirmWaitButton" color="success" onClick={confirmWait}>
            <FontAwesomeIcon icon="check" />
            &nbsp;
            <Translate contentKey="entity.action.wait">Add to queue</Translate>
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
};

export default CheckoutBorrow;
