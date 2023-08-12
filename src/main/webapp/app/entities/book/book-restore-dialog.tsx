import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity, restoreEntity } from './book.reducer';

export const BookRestoreDialog = () => {
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

  const confirmRestore = () => {
    dispatch(restoreEntity(bookEntity.id));
    navigate('/book' + location.search);
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="bookRestoreDialogHeading">
        <Translate contentKey="entity.restore.title">Confirm restore operation</Translate>
      </ModalHeader>
      <ModalBody id="libraryApp.book.restore.question">
        <Translate contentKey="libraryApp.book.restore.question" interpolate={{ id: bookEntity.id }}>
          Are you sure you want to restore this Book?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-restore-book" data-cy="entityConfirmRestoreButton" color="success" onClick={confirmRestore}>
          <FontAwesomeIcon icon="rotate-left" />
          &nbsp;
          <Translate contentKey="entity.action.restore">Restore</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default BookRestoreDialog;
