import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity, holdBook } from './book.reducer';

export const BookReservationDialog = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const bookEntity = useAppSelector(state => state.book.entity);
  const updateSuccess = useAppSelector(state => state.book.updateSuccess);
  const errorMessage = useAppSelector(state => state.book.errorMessage);

  const handleSuccess = () => {
    navigate(`/book` + location.search);
  };

  const handleError = (bookId: string) => {
    navigate(`/book/${bookId}/wait`);
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleSuccess();
      setLoadModal(false);
    } else if (errorMessage !== null && !updateSuccess && loadModal) {
      handleError(id);
      setLoadModal(false);
    }
  }, [updateSuccess, errorMessage]);

  const confirmHold = () => {
    dispatch(holdBook(bookEntity.id));
  };

  return (
    <Modal isOpen toggle={handleSuccess}>
      <ModalHeader toggle={handleSuccess} data-cy="bookReservationDialogHeading">
        <Translate contentKey="libraryApp.book.hold.title">Confirm hold operation</Translate>
      </ModalHeader>
      <ModalBody id="libraryApp.book.hold.question">
        <Translate contentKey="libraryApp.book.hold.question" interpolate={{ title: bookEntity.title }}>
          Are you sure you want to hold this Book?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleSuccess}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-hold-book" data-cy="entityConfirmHoldButton" color="success" onClick={confirmHold}>
          <FontAwesomeIcon icon="book-bookmark" />
          &nbsp;
          <Translate contentKey="entity.action.hold">Hold</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default BookReservationDialog;
