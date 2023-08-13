import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity, returnBook } from './checkout.reducer';

export const CheckoutReturnDialog = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const checkoutEntity = useAppSelector(state => state.checkout.entity);
  const updateSuccess = useAppSelector(state => state.checkout.updateSuccess);

  const handleClose = () => {
    navigate('/checkout' + location.search);
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmReturn = (success: boolean) => {
    dispatch(
      returnBook({
        id: checkoutEntity.bookCopy.id,
        success,
      })
    );
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="checkoutDeleteDialogHeading">
        <Translate contentKey="entity.return.title">Confirm delete operation</Translate>
      </ModalHeader>
      <ModalBody id="libraryApp.checkout.return.question">
        <Translate contentKey="libraryApp.checkout.return.question" interpolate={{ id: checkoutEntity.bookCopy.id }}>
          Are you sure you want to borrow Book Copy?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button
          id="jhi-confirm-return-successfully"
          data-cy="entityConfirmReturnSuccessButton"
          color="success"
          onClick={() => confirmReturn(true)}
        >
          <FontAwesomeIcon icon="rotate-left" />
          &nbsp;
          <Translate contentKey="libraryApp.checkout.return.success">Return</Translate>
        </Button>
        <Button
          id="jhi-confirm-return-failed"
          data-cy="entityConfirmReturnFailedButton"
          color="danger"
          onClick={() => confirmReturn(false)}
        >
          <FontAwesomeIcon icon="eye-slash" />
          &nbsp;
          <Translate contentKey="libraryApp.checkout.return.failed">Lost</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default CheckoutReturnDialog;
