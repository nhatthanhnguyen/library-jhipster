import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Col, Input, Row, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, translate, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, AUTHORITIES, STATE_CHECKOUT_VALUES, STATE_I18_VALUES } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { getSortStateWithFilter, overridePaginationStateWithQueryParamsAndFilter } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from './checkout.reducer';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

export const Checkout = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParamsAndFilter(getSortStateWithFilter(location, ITEMS_PER_PAGE, 'id'), location.search)
  );
  const [userFilter, setUserFilter] = useState<string>(paginationState.user);
  const [bookCopyFilter, setBookCopyFilter] = useState<string>(paginationState.bookCopy);
  const [stateFilter, setStateFilter] = useState<string>(paginationState.state);

  const checkoutList = useAppSelector(state => state.checkout.entities);
  const loading = useAppSelector(state => state.checkout.loading);
  const totalItems = useAppSelector(state => state.checkout.totalItems);
  const librarianAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.LIBRARIAN]));
  const adminAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const isReader = !librarianAuthority && !adminAuthority;

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
        user: paginationState.user,
        bookCopy: paginationState.bookCopy,
        state: paginationState.state,
      })
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const filterRequest = `${paginationState.user ? `&user=${paginationState.user}` : ''}${
      paginationState.bookCopy ? `&bookCopy=${paginationState.bookCopy}` : ''
    }${paginationState.state ? `&state=${paginationState.state}` : ''}`;
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}${filterRequest}`;
    if (location.search !== endURL) {
      navigate(`${location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [
    paginationState.activePage,
    paginationState.order,
    paginationState.sort,
    paginationState.user,
    paginationState.bookCopy,
    paginationState.state,
  ]);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    const user = params.get('user');
    const bookCopy = params.get('bookCopy');
    const state = params.get('state');
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
        user: user ?? '',
        bookCopy: bookCopy ?? '',
        state: STATE_CHECKOUT_VALUES.find(it => it === state) ?? 'ALL',
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

  const handleFilter = () => {
    setPaginationState({
      ...paginationState,
      user: userFilter,
      bookCopy: bookCopyFilter,
      state: stateFilter,
    });
  };

  return (
    <div>
      <h2 id="checkout-heading" data-cy="CheckoutHeading">
        <Translate contentKey="libraryApp.checkout.home.title">Checkouts</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="libraryApp.checkout.home.refreshListLabel">Refresh List</Translate>
          </Button>
          {librarianAuthority ? (
            <Link to="/checkout/borrow" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
              <FontAwesomeIcon icon="plus" />
              &nbsp;
              <Translate contentKey="libraryApp.checkout.home.createLabel">Create new Checkout</Translate>
            </Link>
          ) : undefined}
        </div>
      </h2>
      <Row>
        {isReader ? null : (
          <Col>
            <Input
              type="text"
              value={userFilter}
              onChange={e => setUserFilter(e.target.value)}
              placeholder={translate('libraryApp.checkout.filter.user.title')}
            />
          </Col>
        )}
        <Col>
          <Input
            type="text"
            value={bookCopyFilter}
            onChange={e => setBookCopyFilter(e.target.value)}
            placeholder={translate('libraryApp.checkout.filter.bookCopy.title')}
          />
        </Col>
        <Col>
          <Input
            type="select"
            value={stateFilter}
            onChange={e => setStateFilter(e.target.value)}
            placeholder={translate('libraryApp.checkout.filter.state.title')}
          >
            {STATE_CHECKOUT_VALUES.map((state, i) => (
              <option key={i} value={state}>
                {translate(STATE_I18_VALUES[i])}
              </option>
            ))}
          </Input>
        </Col>
        <Col className="text-end">
          <Button onClick={handleFilter}>
            <FontAwesomeIcon icon="filter" /> Filter
          </Button>
        </Col>
      </Row>
      <div className="table-responsive">
        {checkoutList && checkoutList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="libraryApp.checkout.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('startTime')}>
                  <Translate contentKey="libraryApp.checkout.startTime">Start Time</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('endTime')}>
                  <Translate contentKey="libraryApp.checkout.endTime">End Time</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('isReturned')}>
                  <Translate contentKey="libraryApp.checkout.isReturned">Is Returned</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="libraryApp.checkout.user">User</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="libraryApp.checkout.bookCopy">Book Copy</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {checkoutList.map((checkout, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/checkout/${checkout.id}`} color="link" size="sm">
                      {checkout.id}
                    </Button>
                  </td>
                  <td>{checkout.startTime ? <TextFormat type="date" value={checkout.startTime} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{checkout.endTime ? <TextFormat type="date" value={checkout.endTime} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{checkout.isReturned ? 'true' : 'false'}</td>
                  <td>{checkout.user ? `${checkout.user.lastName} ${checkout.user.firstName}` : ''}</td>
                  <td>
                    {checkout.bookCopy ? (
                      <Link
                        to={`/book-copy/${checkout.bookCopy.id}`}
                      >{`${checkout.bookCopy.id} - ${checkout.bookCopy.book.title} - ${checkout.bookCopy.book.publisher.name}`}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      {checkout.endTime ? undefined : (
                        <Button
                          tag={Link}
                          to={`/checkout/${checkout.id}/return?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                          color="info"
                          size="sm"
                          data-cy="bookReturnButton"
                        >
                          <FontAwesomeIcon icon="rotate-left" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.return">Return</Translate>
                          </span>
                        </Button>
                      )}
                      <Button
                        tag={Link}
                        to={`/checkout/${checkout.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                        to={`/checkout/${checkout.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
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
              <Translate contentKey="libraryApp.checkout.home.notFound">No Checkouts found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={checkoutList && checkoutList.length > 0 ? '' : 'd-none'}>
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

export default Checkout;
