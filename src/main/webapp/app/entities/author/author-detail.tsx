import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './author.reducer';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

export const AuthorDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const authorEntity = useAppSelector(state => state.author.entity);
  const isLibrarian = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.LIBRARIAN]));

  return (
    <div>
      <h2 data-cy="authorDetailsHeading">
        <Translate contentKey="libraryApp.author.detail.title">Author</Translate>
      </h2>
      <Row xs="1" sm="2">
        <Col>
          <dl className="jh-entity-details">
            <dt>
              <span id="fullName">
                <Translate contentKey="libraryApp.author.fullName">Full Name</Translate>
              </span>
            </dt>
            <dd>{`${authorEntity.lastName} ${authorEntity.firstName}`}</dd>
          </dl>
          <Button tag={Link} to="/author" replace color="info" data-cy="entityDetailsBackButton">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          {isLibrarian ? (
            <>
              &nbsp;
              <Button tag={Link} to={`/author/${authorEntity.id}/edit`} replace color="primary">
                <FontAwesomeIcon icon="pencil-alt" />{' '}
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.edit">Edit</Translate>
                </span>
              </Button>
            </>
          ) : undefined}
        </Col>
      </Row>
    </div>
  );
};

export default AuthorDetail;
