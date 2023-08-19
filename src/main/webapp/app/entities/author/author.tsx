import React, { KeyboardEvent, useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Input, InputGroup, Table } from 'reactstrap';
import { getSortState, JhiItemCount, JhiPagination, TextFormat, translate, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import {
  getSortStateWithSearch,
  overridePaginationStateWithQueryParams,
  overridePaginationStateWithQueryParamsAndSearch,
} from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IAuthor } from 'app/shared/model/author.model';
import { getEntities } from './author.reducer';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

export const Author = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParamsAndSearch(getSortStateWithSearch(location, ITEMS_PER_PAGE, 'id'), location.search)
  );
  const [searchText, setSearchText] = useState<string>(paginationState.search ?? '');

  const authorList = useAppSelector(state => state.author.entities);
  const loading = useAppSelector(state => state.author.loading);
  const totalItems = useAppSelector(state => state.author.totalItems);
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
    const endURL =
      `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}` +
      `${paginationState.search ? `&search=${paginationState.search}` : ''}`;
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
    const query = params.get('search');
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
        search: query ?? '',
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
      <h2 id="author-heading" data-cy="AuthorHeading">
        <Translate contentKey="libraryApp.author.home.title">Authors</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="libraryApp.author.home.refreshListLabel">Refresh List</Translate>
          </Button>
          {isLibrarian ? (
            <Link to="/author/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
              <FontAwesomeIcon icon="plus" />
              &nbsp;
              <Translate contentKey="libraryApp.author.home.createLabel">Create new Author</Translate>
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
        {authorList && authorList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="libraryApp.author.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('lastName')}>
                  <Translate contentKey="libraryApp.author.lastName">Last Name</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('firstName')}>
                  <Translate contentKey="libraryApp.author.firstName">First Name</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                {isLibrarian ? (
                  <>
                    <th className="hand" onClick={sort('createdBy')}>
                      <Translate contentKey="libraryApp.author.createdBy">Created By</Translate> <FontAwesomeIcon icon="sort" />
                    </th>
                    <th className="hand" onClick={sort('createdDate')}>
                      <Translate contentKey="libraryApp.author.createdDate">Created Date</Translate>
                      <FontAwesomeIcon icon="sort" />
                    </th>
                    <th className="hand" onClick={sort('lastModifiedBy')}>
                      <Translate contentKey="libraryApp.author.lastModifiedBy">Last Modified By</Translate>
                      <FontAwesomeIcon icon="sort" />
                    </th>
                    <th className="hand" onClick={sort('lastModifiedDate')}>
                      <Translate contentKey="libraryApp.author.lastModifiedDate">Last Modified Date</Translate>
                      <FontAwesomeIcon icon="sort" />
                    </th>
                  </>
                ) : undefined}
                <th />
              </tr>
            </thead>
            <tbody>
              {authorList.map((author: IAuthor, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>{author.id}</td>
                  <td>{author.lastName}</td>
                  <td>{author.firstName}</td>
                  {isLibrarian ? (
                    <>
                      <td>{author.createdBy}</td>
                      <td>
                        {author.createdDate ? (
                          <TextFormat value={author.createdDate} type="date" format={APP_DATE_FORMAT} blankOnInvalid />
                        ) : null}
                      </td>
                      <td>{author.lastModifiedBy}</td>
                      <td>
                        {author.lastModifiedDate ? (
                          <TextFormat value={author.lastModifiedDate} type="date" format={APP_DATE_FORMAT} blankOnInvalid />
                        ) : null}
                      </td>
                    </>
                  ) : undefined}
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/author/${author.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      {isLibrarian ? (
                        <>
                          <Button
                            tag={Link}
                            to={`/author/${author.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                            color="primary"
                            size="sm"
                            data-cy="entityEditButton"
                          >
                            <FontAwesomeIcon icon="pencil-alt" />{' '}
                            <span className="d-none d-md-inline">
                              <Translate contentKey="entity.action.edit">Edit</Translate>
                            </span>
                          </Button>
                          <Button
                            tag={Link}
                            to={`/author/${author.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                            color="danger"
                            size="sm"
                            data-cy="entityDeleteButton"
                          >
                            <FontAwesomeIcon icon="trash" />{' '}
                            <span className="d-none d-md-inline">
                              <Translate contentKey="entity.action.delete">Delete</Translate>
                            </span>
                          </Button>
                        </>
                      ) : undefined}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="libraryApp.author.home.notFound">No Authors found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={authorList && authorList.length > 0 ? '' : 'd-none'}>
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

export default Author;
