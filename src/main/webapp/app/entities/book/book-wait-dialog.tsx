import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { addToQueue, getEntity, restoreEntity } from './book.reducer';

export const BookWaitDialog = () => {
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

  const handleClose = () => {
    navigate('/book' + location.search);
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmAddToQueue = () => {
    dispatch(addToQueue(bookEntity.id));
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="bookWaitDialogHeading">
        <Translate contentKey="libraryApp.book.wait.title">Confirm add to queue operation</Translate>
      </ModalHeader>
      <ModalBody id="libraryApp.book.wait.question">
        <Translate contentKey="libraryApp.book.wait.question" interpolate={{ title: bookEntity.title }}>
          Are you sure you want to add to Queue of this Book?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-restore-book" data-cy="bookWaitButton" color="success" onClick={confirmAddToQueue}>
          <FontAwesomeIcon icon="check" />
          &nbsp;
          <Translate contentKey="entity.action.wait">Add to queue</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default BookWaitDialog;
