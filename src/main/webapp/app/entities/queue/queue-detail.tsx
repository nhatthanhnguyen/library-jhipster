import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './queue.reducer';

export const QueueDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const queueEntity = useAppSelector(state => state.queue.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="queueDetailsHeading">
          <Translate contentKey="libraryApp.queue.detail.title">Queue</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{queueEntity.id}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="libraryApp.queue.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{queueEntity.createdAt ? <TextFormat value={queueEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="libraryApp.queue.user">User</Translate>
          </dt>
          <dd>{queueEntity.user ? queueEntity.user.id : ''}</dd>
          <dt>
            <Translate contentKey="libraryApp.queue.book">Book</Translate>
          </dt>
          <dd>{queueEntity.book ? queueEntity.book.title : ''}</dd>
        </dl>
        <Button tag={Link} to="/queue" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/queue/${queueEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default QueueDetail;
