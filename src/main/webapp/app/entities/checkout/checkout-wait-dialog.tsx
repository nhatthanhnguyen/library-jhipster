import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity } from '../book/book.reducer';
import { addToQueue } from './checkout.reducer';

export const CheckoutWaitDialog = () => {
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
  const updateSuccess = useAppSelector(state => state.checkout.updateSuccess);
  const errorMessage = useAppSelector(state => state.checkout.errorMessage);

  const handleClose = () => {
    navigate('/checkout' + location.search);
  };

  useEffect(() => {
    if ((updateSuccess && loadModal) || errorMessage !== null) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess, errorMessage]);

  const confirmWait = () => {
    dispatch(addToQueue(bookEntity.id));
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="checkoutDeleteDialogHeading">
        <Translate contentKey="libraryApp.book.wait.title">Confirm add to queue operation</Translate>
      </ModalHeader>
      <ModalBody id="libraryApp.book.wait.question">
        <Translate contentKey="libraryApp.book.wait.question" interpolate={{ title: bookEntity.title }}>
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
  );
};

export default CheckoutWaitDialog;
