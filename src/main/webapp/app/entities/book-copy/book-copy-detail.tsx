import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './book-copy.reducer';

export const BookCopyDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const bookCopyEntity = useAppSelector(state => state.bookCopy.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="bookCopyDetailsHeading">
          <Translate contentKey="libraryApp.bookCopy.detail.title">BookCopy</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{bookCopyEntity.id}</dd>
          <dt>
            <span id="isDeleted">
              <Translate contentKey="libraryApp.bookCopy.isDeleted">Is Deleted</Translate>
            </span>
          </dt>
          <dd>{bookCopyEntity.isDeleted ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="libraryApp.bookCopy.book">Book</Translate>
          </dt>
          <dd>{bookCopyEntity.book ? bookCopyEntity.book.title : ''}</dd>
        </dl>
        <Button tag={Link} to="/book-copy" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/book-copy/${bookCopyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BookCopyDetail;
