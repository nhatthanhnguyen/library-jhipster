import React, { KeyboardEvent, useEffect, useRef, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Input, InputGroup, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, translate, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { getCurrentSortState, overridePaginationStateWithQueryParamsWithSearch } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from './book.reducer';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

export const Book = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParamsWithSearch(getCurrentSortState(location, ITEMS_PER_PAGE, 'id'), location.search)
  );
  const [searchText, setSearchText] = useState<string>(paginationState.search ?? '');

  const bookList = useAppSelector(state => state.book.entities);
  const loading = useAppSelector(state => state.book.loading);
  const totalItems = useAppSelector(state => state.book.totalItems);
  const isLibrarian = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.LIBRARIAN]));

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
        query: paginationState.search ?? '',
      })
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}${
      paginationState.search ? `&search=${paginationState.search}` : ''
    }`;
    if (location.search !== endURL) {
      navigate(`${location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort, paginationState.search]);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    const search = params.get('search');
    if (page && sort && search) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
        search,
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

  const handleSearch = search =>
    setPaginationState({
      ...paginationState,
      search,
    });

  const handlePressSearch = (e: KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSearch(searchText);
    }
  };

  const handleButtonSearchPressed = () => {
    handleSearch(searchText);
  };

  const handleButtonClearSearchPressed = () => {
    setPaginationState({
      ...paginationState,
      search: '',
    });
    setSearchText('');
  };

  return (
    <div>
      <h2 id="book-heading" data-cy="BookHeading">
        <Translate contentKey="libraryApp.book.home.title">Books</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="libraryApp.book.home.refreshListLabel">Refresh List</Translate>
          </Button>
          {isLibrarian ? (
            <Link to="/book/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
              <FontAwesomeIcon icon="plus" />
              &nbsp;
              <Translate contentKey="libraryApp.book.home.createLabel">Create new Book</Translate>
            </Link>
          ) : undefined}
        </div>
      </h2>
      <InputGroup>
        <Input
          type="text"
          onKeyDown={handlePressSearch}
          value={searchText}
          onChange={e => setSearchText(e.currentTarget.value)}
          placeholder={translate('libraryApp.book.home.searchLabel')}
        />
        <Button onClick={handleButtonClearSearchPressed}>
          <FontAwesomeIcon icon="xmark" />
        </Button>
        <Button onClick={handleButtonSearchPressed}>
          <FontAwesomeIcon icon="search" />
        </Button>
      </InputGroup>
      <div className="table-responsive">
        {bookList && bookList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="libraryApp.book.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="libraryApp.book.title">Title</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="libraryApp.book.publisher">Publisher</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                {isLibrarian ? (
                  <>
                    <th className="hand" onClick={sort('createdBy')}>
                      <Translate contentKey="libraryApp.book.createdBy">Created By</Translate>
                      <FontAwesomeIcon icon="sort" />
                    </th>
                    <th className="hand" onClick={sort('createdDate')}>
                      <Translate contentKey="libraryApp.book.createdDate">Created Date</Translate>
                      <FontAwesomeIcon icon="sort" />
                    </th>
                    <th className="hand" onClick={sort('lastModifiedBy')}>
                      <Translate contentKey="libraryApp.book.lastModifiedBy">Last Modified By</Translate>
                      <FontAwesomeIcon icon="sort" />
                    </th>
                    <th className="hand" onClick={sort('lastModifiedDate')}>
                      <Translate contentKey="libraryApp.book.lastModifiedDate">Last Modified Date</Translate>
                      <FontAwesomeIcon icon="sort" />
                    </th>
                  </>
                ) : undefined}
                <th />
              </tr>
            </thead>
            <tbody>
              {bookList.map((book, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/book/${book.id}`} color="link" size="sm">
                      {book.id}
                    </Button>
                  </td>
                  <td>{book.title}</td>
                  <td>{book.publisher ? <Link to={`/publisher/${book.publisher.id}`}>{book.publisher.name}</Link> : ''}</td>
                  {isLibrarian ? (
                    <>
                      <td>{book.createdBy}</td>
                      <td>
                        {book.createdDate ? (
                          <TextFormat value={book.createdDate} type="date" format={APP_DATE_FORMAT} blankOnInvalid />
                        ) : null}
                      </td>
                      <td>{book.lastModifiedBy}</td>
                      <td>
                        {book.lastModifiedDate ? (
                          <TextFormat value={book.lastModifiedDate} type="date" format={APP_DATE_FORMAT} blankOnInvalid />
                        ) : null}
                      </td>
                    </>
                  ) : undefined}
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      {isLibrarian ? (
                        <>
                          <Button tag={Link} to={`/book/${book.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                            <FontAwesomeIcon icon="eye" />{' '}
                            <span className="d-none d-md-inline">
                              <Translate contentKey="entity.action.view">View</Translate>
                            </span>
                          </Button>
                          <Button
                            tag={Link}
                            to={`/book/${book.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                            color="primary"
                            size="sm"
                            data-cy="entityEditButton"
                          >
                            <FontAwesomeIcon icon="pencil-alt" />{' '}
                            <span className="d-none d-md-inline">
                              <Translate contentKey="entity.action.edit">Edit</Translate>
                            </span>
                          </Button>
                          {!book.isDeleted ? (
                            <Button
                              tag={Link}
                              to={`/book/${book.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                              color="danger"
                              size="sm"
                              data-cy="entityDeleteButton"
                            >
                              <FontAwesomeIcon icon="trash" />{' '}
                              <span className="d-none d-md-inline">
                                <Translate contentKey="entity.action.delete">Delete</Translate>
                              </span>
                            </Button>
                          ) : (
                            <Button
                              tag={Link}
                              to={`/book/${book.id}/restore?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                              color="success"
                              size="sm"
                              data-cy="entityRestoreButton"
                            >
                              <FontAwesomeIcon icon="rotate-left" />{' '}
                              <span className="d-none d-md-inline">
                                <Translate contentKey="entity.action.restore">Restore</Translate>
                              </span>
                            </Button>
                          )}
                        </>
                      ) : (
                        <>
                          <Button tag={Link} color="primary" size="sm" data-cy="HoldBookButton" to={`/book/${book.id}/hold`}>
                            <FontAwesomeIcon icon="book-bookmark" /> <Translate contentKey="entity.action.hold">Hold this book</Translate>
                          </Button>
                          <Button tag={Link} to={`/book/${book.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                            <FontAwesomeIcon icon="eye" />{' '}
                            <span className="d-none d-md-inline">
                              <Translate contentKey="entity.action.view">View</Translate>
                            </span>
                          </Button>
                        </>
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
              <Translate contentKey="libraryApp.book.home.notFound">No Books found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={bookList && bookList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
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
    </div>
  );
};

export default Book;
