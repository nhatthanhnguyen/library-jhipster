import { useAppDispatch, useAppSelector } from 'app/config/store';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import React, { useEffect, useState } from 'react';
import { getEntity, restoreEntity } from 'app/entities/author/author.reducer';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const AuthorRestoreDialog = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const authorEntity = useAppSelector(state => state.author.entity);
  const updateSuccess = useAppSelector(state => state.author.updateSuccess);

  const handleClose = () => {
    navigate('/author' + location.search);
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmRestore = () => {
    dispatch(restoreEntity(authorEntity.id));
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="authorRestoreDialogHeading">
        <Translate contentKey="entity.restore.title">Confirm restore operation</Translate>
      </ModalHeader>
      <ModalBody id="libraryApp.author.restore.question">
        <Translate contentKey="libraryApp.author.restore.question" interpolate={{ id: authorEntity.id }}>
          Are you sure you want to restore this Author?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-restore-author" data-cy="entityConfirmRestoreButton" color="success" onClick={confirmRestore}>
          <FontAwesomeIcon icon="sync" />
          &nbsp;
          <Translate contentKey="entity.action.restore">Restore</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};
