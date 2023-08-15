import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button, Col, Modal, ModalBody, ModalFooter, ModalHeader, Row } from 'reactstrap';
import { Translate, translate, ValidatedField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { addToQueue, getAllEntities as getBooks } from 'app/entities/book/book.reducer';
import { createEntity, reset } from './reservation.reducer';
import { toNumber } from 'lodash';

export const ReservationCreate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const users = useAppSelector(state => state.userManagement.users);
  const books = useAppSelector(state => state.book.entities);
  const loading = useAppSelector(state => state.reservation.loading);
  const updating = useAppSelector(state => state.reservation.updating);
  const [userId, setUserId] = useState<string>('');
  const [bookId, setBookId] = useState<string>('');
  const [modal, setModal] = useState<boolean>(false);

  const handleClose = () => {
    navigate('/reservation' + location.search);
  };

  useEffect(() => {
    dispatch(reset());

    dispatch(getUsers({}));
    dispatch(getBooks());
  }, []);

  const saveEntity = () => {
    const entity = {
      userId: toNumber(userId),
      bookId: toNumber(bookId),
    };
    dispatch(createEntity(entity)).then(response => {
      if (response.type.includes('rejected')) {
        setModal(true);
      } else {
        navigate('/reservation' + location.search);
      }
    });
  };

  const confirmWait = () => {
    const entity = {
      userId: toNumber(userId),
      bookId: toNumber(books.find(it => it.id.toString() === bookId)?.id),
    };
    dispatch(addToQueue(entity)).then(() => navigate('/reservation' + location.search));
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="libraryApp.reservation.home.createOrEditLabel" data-cy="ReservationCreateUpdateHeading">
            <Translate contentKey="libraryApp.reservation.home.createOrEditLabel">Create a Reservation</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <>
              <ValidatedField
                id="reservation-user"
                name="user"
                data-cy="user"
                onChange={e => setUserId(e.target.value)}
                label={translate('libraryApp.reservation.user')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="reservation-bookCopy"
                name="bookCopy"
                data-cy="bookCopy"
                onChange={e => setBookId(e.target.value)}
                label={translate('libraryApp.reservation.book')}
                type="select"
              >
                <option value="" key="0" />
                {books
                  ? books.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/reservation" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" onClick={saveEntity} disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </>
          )}
        </Col>
      </Row>
      <Modal isOpen={modal} toggle={handleClose}>
        <ModalHeader toggle={handleClose} data-cy="reservationWaitDialogHeading">
          <Translate contentKey="libraryApp.book.wait.title">Confirm add to queue operation</Translate>
        </ModalHeader>
        <ModalBody id="libraryApp.book.wait.question">
          <Translate
            contentKey="libraryApp.book.wait.question"
            interpolate={{
              title: books ? (bookId !== '' ? books.find(it => it.id.toString() === bookId)?.title : 'bookTitle') : 'bookTitle',
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

export default ReservationCreate;
