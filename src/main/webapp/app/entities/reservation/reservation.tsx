import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Col, Input, Row, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, translate, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import {
  getSortStateWithReservationFilter,
  overridePaginationStateWithQueryParamsAndReservationFilter,
} from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from './reservation.reducer';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

export const Reservation = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParamsAndReservationFilter(
      getSortStateWithReservationFilter(location, ITEMS_PER_PAGE, 'id'),
      location.search
    )
  );
  const [userFilter, setUserFilter] = useState<string>(paginationState.user);
  const [bookCopyFilter, setBookCopyFilter] = useState<string>(paginationState.bookCopy);

  const reservationList = useAppSelector(state => state.reservation.entities);
  const loading = useAppSelector(state => state.reservation.loading);
  const totalItems = useAppSelector(state => state.reservation.totalItems);
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
      })
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const filterRequest =
      `${paginationState.user ? `&user=${paginationState.user}` : ''}` +
      `${paginationState.bookCopy ? `&bookCopy=${paginationState.bookCopy}` : ''}`;
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}${filterRequest}`;
    if (location.search !== endURL) {
      navigate(`${location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort, paginationState.user, paginationState.bookCopy]);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    const user = params.get('user');
    const bookCopy = params.get('bookCopy');
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
        user: user ?? '',
        bookCopy: bookCopy ?? '',
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
    });
  };

  return (
    <div>
      <h2 id="reservation-heading" data-cy="ReservationHeading">
        <Translate contentKey="libraryApp.reservation.home.title">Reservations</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="libraryApp.reservation.home.refreshListLabel">Refresh List</Translate>
          </Button>
          {librarianAuthority ? (
            <Link to="/reservation/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
              <FontAwesomeIcon icon="plus" />
              &nbsp;
              <Translate contentKey="libraryApp.reservation.home.createLabel">Create new Reservation</Translate>
            </Link>
          ) : null}
        </div>
      </h2>
      <Row>
        {isReader ? null : (
          <Col>
            <Input
              type="text"
              value={userFilter}
              onChange={e => setUserFilter(e.currentTarget.value)}
              placeholder={translate('libraryApp.reservation.filter.user.title')}
            />
          </Col>
        )}
        <Col>
          <Input
            type="text"
            value={bookCopyFilter}
            onChange={e => setBookCopyFilter(e.currentTarget.value)}
            placeholder={translate('libraryApp.reservation.filter.bookCopy.title')}
          />
        </Col>
        <Col className="text-end">
          <Button onClick={handleFilter}>
            <FontAwesomeIcon icon="filter" /> Filter
          </Button>
        </Col>
      </Row>
      <div className="table-responsive">
        {reservationList && reservationList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="libraryApp.reservation.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('startTime')}>
                  <Translate contentKey="libraryApp.reservation.startTime">Start Time</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('endTime')}>
                  <Translate contentKey="libraryApp.reservation.endTime">End Time</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                {isReader ? null : (
                  <th>
                    <Translate contentKey="libraryApp.reservation.user">User</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                )}
                <th>
                  <Translate contentKey="libraryApp.reservation.bookCopy">Book Copy</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {reservationList.map((reservation, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>{reservation.id}</td>
                  <td>
                    {reservation.startTime ? <TextFormat type="date" value={reservation.startTime} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{reservation.endTime ? <TextFormat type="date" value={reservation.endTime} format={APP_DATE_FORMAT} /> : null}</td>
                  {isReader ? null : (
                    <td>{reservation.user ? `${reservation.user.id} - ${reservation.user.lastName} ${reservation.user.firstName}` : ''}</td>
                  )}
                  <td>
                    {reservation.bookCopy
                      ? `${reservation.bookCopy.id} - ${reservation.bookCopy.book.title} - ${reservation.bookCopy.book.publisher.name}`
                      : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      {isReader ? null : reservation.endTime ? null : (
                        <Button
                          tag={Link}
                          to={`/reservation/${reservation.id}/borrow/${reservation.bookCopy.id}?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                          color="primary"
                          size="sm"
                          data-cy="borrowButton"
                        >
                          <FontAwesomeIcon icon="book-bookmark" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.borrow">Borrow</Translate>
                          </span>
                        </Button>
                      )}
                      {/* <Button
                        tag={Link}
                        to={`/reservation/${reservation.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        size="sm"
                        data-cy="editButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt"/>{' '}
                        <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                      </Button> */}
                      <Button
                        tag={Link}
                        to={`/reservation/${reservation.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
              <Translate contentKey="libraryApp.reservation.home.notFound">No Reservations found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={reservationList && reservationList.length > 0 ? '' : 'd-none'}>
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

export default Reservation;
