import React, { KeyboardEvent, useEffect, useState } from 'react';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Input, InputGroup, Row, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, translate, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './category.reducer';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { getSortStateWithSearch, overridePaginationStateWithQueryParamsAndSearch } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { getEntitiesByCategory } from 'app/entities/book/book.reducer';
import { IBook } from 'app/shared/model/book.model';

export const CategoryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  const location = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParamsAndSearch(getSortStateWithSearch(location, ITEMS_PER_PAGE, 'id'), location.search)
  );
  const [searchText, setSearchText] = useState<string>(paginationState.search ?? '');

  const categoryEntity = useAppSelector(state => state.category.entity);
  const bookList = useAppSelector(state => state.book.entities);
  const loading = useAppSelector(state => state.book.loading);
  const totalItems = useAppSelector(state => state.book.totalItems);
  const isLibrarian = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.LIBRARIAN]));

  const getAllEntities = () => {
    dispatch(
      getEntitiesByCategory({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
        query: paginationState.search ?? '',
        categoryId: id,
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
    dispatch(getEntity(id));
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
      <Row>
        <Col md="8">
          <h2 data-cy="categoryDetailsHeading">
            <Translate contentKey="libraryApp.category.detail.title">Category</Translate>
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="name">
                <Translate contentKey="libraryApp.category.name">Name</Translate>
              </span>
            </dt>
            <dd>{categoryEntity.name}</dd>
          </dl>
          <Button tag={Link} to="/category" replace color="info" data-cy="entityDetailsBackButton">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          {isLibrarian ? (
            <>
              &nbsp;
              <Button tag={Link} to={`/category/${categoryEntity.id}/edit`} replace color="primary">
                <FontAwesomeIcon icon="pencil-alt" />{' '}
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.edit">Edit</Translate>
                </span>
              </Button>
            </>
          ) : undefined}
        </Col>
      </Row>
      <h4 id="book-heading" data-cy="BookHeading">
        <Translate contentKey="libraryApp.book.home.title">Books</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="libraryApp.book.home.refreshListLabel">Refresh List</Translate>
          </Button>
        </div>
      </h4>
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
          <Table responsive striped>
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
                <th className="hand" onClick={sort('yearPublished')}>
                  <Translate contentKey="libraryApp.book.yearPublished">Year published</Translate>
                  <FontAwesomeIcon icon="sort" />
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
              {bookList.map((book: IBook, i: number) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/book/${book.id}`} color="link" size="sm">
                      {book.id}
                    </Button>
                  </td>
                  <td>{book.title}</td>
                  <td>{book?.publisher?.name}</td>
                  <td>{book?.yearPublished}</td>
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
                      <Button tag={Link} to={`/book/${book.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
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

export default CategoryDetail;
