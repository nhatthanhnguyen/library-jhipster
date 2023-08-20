import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { createEntityFromBook as createBookCopy } from 'app/entities/book-copy/book-copy.reducer';
import { toNumber } from 'lodash';

export const BookCopyCreateDialog = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    setLoadModal(true);
  }, []);

  const updateSuccess = useAppSelector(state => state.bookCopy.updateSuccess);

  const handleClose = () => {
    navigate(`/book/${id}` + location.search);
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDelete = () => {
    dispatch(
      createBookCopy({
        book: {
          id: toNumber(id),
        },
      })
    );
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="bookCopyCreateDialogHeading">
        <Translate contentKey="entity.create.title">Confirm delete operation</Translate>
      </ModalHeader>
      <ModalBody id="libraryApp.bookCopy.create.question">
        <Translate contentKey="libraryApp.bookCopy.create.question" interpolate={{ id }}>
          Are you sure you want to create a BookCopy for this Book?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-create-bookCopy" data-cy="entityConfirmCreateButton" color="success" onClick={confirmDelete}>
          <FontAwesomeIcon icon="check" />
          &nbsp;
          <Translate contentKey="entity.action.create">Create</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default BookCopyCreateDialog;
