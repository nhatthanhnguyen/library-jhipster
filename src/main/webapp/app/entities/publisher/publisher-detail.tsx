import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { AUTHORITIES } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './publisher.reducer';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

export const PublisherDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const publisherEntity = useAppSelector(state => state.publisher.entity);
  const isLibrarian = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.LIBRARIAN]));

  return (
    <Row>
      <Col md="8">
        <h2 data-cy="publisherDetailsHeading">
          <Translate contentKey="libraryApp.publisher.detail.title">Publisher</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{publisherEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="libraryApp.publisher.name">Name</Translate>
            </span>
          </dt>
          <dd>{publisherEntity.name}</dd>
        </dl>
        <Button tag={Link} to="/publisher" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        {isLibrarian ? (
          <>
            &nbsp;
            <Button tag={Link} to={`/publisher/${publisherEntity.id}/edit`} replace color="primary">
              <FontAwesomeIcon icon="pencil-alt" />{' '}
              <span className="d-none d-md-inline">
                <Translate contentKey="entity.action.edit">Edit</Translate>
              </span>
            </Button>
          </>
        ) : undefined}
      </Col>
    </Row>
  );
};

export default PublisherDetail;
