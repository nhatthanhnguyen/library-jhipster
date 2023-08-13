import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { borrowBook, getEntity } from './reservation.reducer';

export const ReservationBorrowDialog = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();
  const { bookCopyId } = useParams<'bookCopyId'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const reservationEntity = useAppSelector(state => state.reservation.entity);
  const updateSuccess = useAppSelector(state => state.reservation.updateSuccess);

  const handleClose = () => {
    navigate('/reservation' + location.search);
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmBorrow = () => {
    dispatch(borrowBook(reservationEntity.id));
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="reservationBorrowDialogHeading">
        <Translate contentKey="libraryApp.reservation.borrow.title">Confirm borrow operation</Translate>
      </ModalHeader>
      <ModalBody id="libraryApp.reservation.borrow.question">
        <Translate contentKey="libraryApp.reservation.borrow.question" interpolate={{ id: bookCopyId }}>
          Are you sure you want to borrow Book Copy?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-borrow" data-cy="entityConfirmBorrowBook" color="success" onClick={confirmBorrow}>
          <FontAwesomeIcon icon="book-bookmark" />
          &nbsp;
          <Translate contentKey="entity.action.borrow">Borrow</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default ReservationBorrowDialog;
