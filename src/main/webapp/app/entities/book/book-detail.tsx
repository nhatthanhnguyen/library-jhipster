import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
import { getSortState, JhiItemCount, JhiPagination, TextFormat, translate, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './book.reducer';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { getEntitiesByBook as getBookCopiesByBook } from 'app/entities/book-copy/book-copy.reducer';

export const BookDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  const location = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getSortState(location, ITEMS_PER_PAGE, 'id'), location.search)
  );

  const bookCopyList = useAppSelector(state => state.bookCopy.entities);
  const loading = useAppSelector(state => state.bookCopy.loading);
  const totalItems = useAppSelector(state => state.bookCopy.totalItems);
  const bookEntity = useAppSelector(state => state.book.entity);
  const isLibrarian = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.LIBRARIAN]));

  const getAllEntities = () => {
    dispatch(
      getBookCopiesByBook({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
        bookId: id,
      })
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (location.search !== endURL) {
      navigate(`${location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    dispatch(getEntity(id));
    if (isLibrarian) {
      sortEntities();
    }
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [location.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  return (
    <div>
      <h2 data-cy="bookDetailsHeading">
        <Translate contentKey="libraryApp.book.detail.title">Book</Translate>
      </h2>
      <Row>
        <Col md="8">
          <dl className="jh-entity-details">
            <dt>
              <span id="id">
                <Translate contentKey="global.field.id">ID</Translate>
              </span>
            </dt>
            <dd>{bookEntity.id}</dd>
            <dt>
              <span id="title">
                <Translate contentKey="libraryApp.book.title">Title</Translate>
              </span>
            </dt>
            <dd>{bookEntity.title}</dd>
            <dt>
              <Translate contentKey="libraryApp.book.publisher">Publisher</Translate>
            </dt>
            <dd>{bookEntity.publisher ? bookEntity.publisher.name : ''}</dd>
            <dt>
              <Translate contentKey="libraryApp.book.yearPublished">Year published</Translate>
            </dt>
            <dd>{bookEntity.yearPublished}</dd>
            <dt>
              <Translate contentKey="libraryApp.book.author">Author</Translate>
            </dt>
            <dd>
              {bookEntity.authors
                ? bookEntity.authors.map((val, i) => (
                    <span key={val.id}>
                      <a>{`${val.lastName} ${val.firstName}`}</a>
                      {bookEntity.authors && i === bookEntity.authors.length - 1 ? '' : ', '}
                    </span>
                  ))
                : null}
            </dd>
            <dt>
              <Translate contentKey="libraryApp.book.category">Category</Translate>
            </dt>
            <dd>
              {bookEntity.categories
                ? bookEntity.categories.map((val, i) => (
                    <span key={val.id}>
                      <a>{val.name}</a>
                      {bookEntity.categories && i === bookEntity.categories.length - 1 ? '' : ', '}
                    </span>
                  ))
                : null}
            </dd>
          </dl>
          <Button tag={Link} to="/book" replace color="info" data-cy="entityDetailsBackButton">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          {isLibrarian ? (
            <>
              &nbsp;
              <Button tag={Link} to={`/book/${bookEntity.id}/edit`} replace color="primary">
                <FontAwesomeIcon icon="pencil-alt" />{' '}
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.edit">Edit</Translate>
                </span>
              </Button>
            </>
          ) : undefined}
        </Col>
      </Row>
      {!isLibrarian ? null : (
        <>
          <h2 id="book-copy-heading" data-cy="BookCopyHeading">
            <Translate contentKey="libraryApp.bookCopy.home.title">Book Copies</Translate>
            <div className="d-flex justify-content-end">
              <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
                <FontAwesomeIcon icon="sync" spin={loading} />{' '}
                <Translate contentKey="libraryApp.bookCopy.home.refreshListLabel">Refresh List</Translate>
              </Button>
              <Link
                to={`/book/${id}/create`}
                className="btn btn-primary jh-create-entity"
                id="jh-create-entity"
                data-cy="entityCreateButton"
              >
                <FontAwesomeIcon icon="plus" />
                &nbsp;
                <Translate contentKey="libraryApp.bookCopy.home.createLabel">Create new Book Copy</Translate>
              </Link>
            </div>
          </h2>
          <div className="table-responsive">
            {bookCopyList && bookCopyList.length > 0 ? (
              <Table responsive>
                <thead>
                  <tr>
                    <th className="hand" onClick={sort('id')}>
                      <Translate contentKey="libraryApp.bookCopy.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                    </th>
                    <th className="hand" onClick={sort('createdBy')}>
                      <Translate contentKey="libraryApp.bookCopy.createdBy">Created By</Translate>
                      <FontAwesomeIcon icon="sort" />
                    </th>
                    <th className="hand" onClick={sort('createdDate')}>
                      <Translate contentKey="libraryApp.bookCopy.createdDate">Created Date</Translate>
                      <FontAwesomeIcon icon="sort" />
                    </th>
                    <th className="hand" onClick={sort('lastModifiedBy')}>
                      <Translate contentKey="libraryApp.bookCopy.lastModifiedBy">Last Modified By</Translate>
                      <FontAwesomeIcon icon="sort" />
                    </th>
                    <th className="hand" onClick={sort('lastModifiedDate')}>
                      <Translate contentKey="libraryApp.bookCopy.lastModifiedDate">Last Modified Date</Translate>
                      <FontAwesomeIcon icon="sort" />
                    </th>
                    <th />
                  </tr>
                </thead>
                <tbody>
                  {bookCopyList.map((bookCopy, i) => (
                    <tr key={`entity-${i}`} data-cy="entityTable">
                      <td>{bookCopy.id}</td>
                      <td>{bookCopy.createdBy}</td>
                      <td>
                        {bookCopy.createdDate ? (
                          <TextFormat value={bookCopy.createdDate} type="date" format={APP_DATE_FORMAT} blankOnInvalid />
                        ) : null}
                      </td>
                      <td>{bookCopy.lastModifiedBy}</td>
                      <td>
                        {bookCopy.lastModifiedDate ? (
                          <TextFormat value={bookCopy.lastModifiedDate} type="date" format={APP_DATE_FORMAT} blankOnInvalid />
                        ) : null}
                      </td>
                      <td className="text-end">
                        <div className="btn-group flex-btn-group-container">
                          {bookCopy.isDeleted ? (
                            <Button
                              tag={Link}
                              to={`/book/${id}/book-copy/${bookCopy.id}/restore?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                              color="success"
                              size="sm"
                              data-cy="entityRestoreButton"
                            >
                              <FontAwesomeIcon icon="rotate-left" />{' '}
                              <span className="d-none d-md-inline">{translate('entity.action.restore')}</span>
                            </Button>
                          ) : (
                            <Button
                              tag={Link}
                              to={`/book/${id}/book-copy/${bookCopy.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                              color="danger"
                              size="sm"
                              data-cy="entityDeleteButton"
                            >
                              <FontAwesomeIcon icon="trash" />{' '}
                              <span className="d-none d-md-inline">{translate('entity.action.delete')}</span>
                            </Button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            ) : (
              !loading && (
                <div className="alert alert-warning">
                  <Translate contentKey="libraryApp.bookCopy.home.notFound">No Book Copies found</Translate>
                </div>
              )
            )}
          </div>
          {totalItems ? (
            <div className={bookCopyList && bookCopyList.length > 0 ? '' : 'd-none'}>
              <div className="justify-content-center d-flex">
                <JhiItemCount
                  page={paginationState.activePage}
                  total={totalItems}
                  itemsPerPage={paginationState.itemsPerPage}
                  i18nEnabled
                />
              </div>
              <div className="justify-content-center d-flex">
                <JhiPagination
                  activePage={paginationState.activePage}
                  onSelect={handlePagination}
                  maxButtons={5}
                  itemsPerPage={paginationState.itemsPerPage}
                  totalItems={totalItems}
                />
              </div>
            </div>
          ) : (
            ''
          )}
        </>
      )}
    </div>
  );
};

export default BookDetail;
