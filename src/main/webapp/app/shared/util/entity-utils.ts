import pick from 'lodash/pick';
import { getUrlParameter, IPaginationBaseState } from 'react-jhipster';
import { STATE_CHECKOUT_VALUES } from 'app/config/constants';

/**
 * Removes fields with an 'id' field that equals ''.
 * This function was created to prevent entities to be sent to
 * the server with an empty id and thus resulting in a 500.
 *
 * @param entity Object to clean.
 */
export const cleanEntity = entity => {
  const keysToKeep = Object.keys(entity).filter(k => !(entity[k] instanceof Object) || (entity[k]['id'] !== '' && entity[k]['id'] !== -1));

  return pick(entity, keysToKeep);
};

/**
 * Simply map a list of element to a list a object with the element as id.
 *
 * @param idList Elements to map.
 * @returns The list of objects with mapped ids.
 */
export const mapIdList = (idList: ReadonlyArray<any>) => idList.filter((id: any) => id !== '').map((id: any) => ({ id }));

export interface IPaginationSearchState extends IPaginationBaseState {
  search: string;
}

export interface IPaginationFilterState extends IPaginationBaseState {
  user: string;
  bookCopy: string;
  state: string;
}

export const getSortStateWithSearch = (
  location: { search: string },
  itemsPerPage: number,
  sortField = 'id',
  sortOrder = 'asc',
  searchText = ''
): IPaginationSearchState => {
  const pageParam = getUrlParameter('page', location.search);
  const sortParam = getUrlParameter('sort', location.search);
  const searchParam = getUrlParameter('search', location.search);
  let sort = sortField;
  let order = sortOrder;
  let query = searchText;
  let activePage = 1;
  if (pageParam !== '' && !isNaN(parseInt(pageParam, 10))) {
    activePage = parseInt(pageParam, 10);
  }
  if (sortParam !== '') {
    sort = sortParam.split(',')[0];
    order = sortParam.split(',')[1];
  }
  if (searchParam !== '') {
    query = searchParam;
  }
  return { itemsPerPage, sort, order, activePage, search: query };
};

export const getSortStateWithFilter = (
  location: { search: string },
  itemsPerPage: number,
  sortField = 'id',
  sortOrder = 'asc',
  userFilter = '',
  bookCopyFilter = '',
  stateFilter = 'ALL'
): IPaginationFilterState => {
  const pageParam = getUrlParameter('page', location.search);
  const sortParam = getUrlParameter('sort', location.search);
  const userFilterParam = getUrlParameter('user', location.search);
  const bookCopyFilterParam = getUrlParameter('bookCopy', location.search);
  const stateFilterParam = getUrlParameter('state', location.search);
  let sort = sortField;
  let order = sortOrder;
  let user = userFilter;
  let bookCopy = bookCopyFilter;
  let state = stateFilter;
  let activePage = 1;
  if (pageParam !== '' && !isNaN(parseInt(pageParam, 10))) {
    activePage = parseInt(pageParam, 10);
  }
  if (sortParam !== '') {
    sort = sortParam.split(',')[0];
    order = sortParam.split(',')[1];
  }
  if (userFilterParam !== '') {
    user = userFilterParam;
  }
  if (bookCopyFilterParam !== '') {
    bookCopy = bookCopyFilterParam;
  }
  if (stateFilterParam !== '' && STATE_CHECKOUT_VALUES.find(it => it === stateFilterParam)) {
    state = stateFilterParam;
  }
  return {
    itemsPerPage,
    sort,
    order,
    activePage,
    user,
    bookCopy,
    state,
  };
};

export const overridePaginationStateWithQueryParamsAndSearch = (paginationState: IPaginationSearchState, locationSearch: string) => {
  const params = new URLSearchParams(locationSearch);
  const page = params.get('page');
  const sort = params.get('sort');
  const search = params.get('search');
  if (page && sort) {
    const sortSplit = sort.split(',');
    paginationState.activePage = +page;
    paginationState.sort = sortSplit[0];
    paginationState.order = sortSplit[1];
    paginationState.search = search ?? '';
  }
  return paginationState;
};

export const overridePaginationStateWithQueryParams = (paginationState: IPaginationBaseState, locationSearch: string) => {
  const params = new URLSearchParams(locationSearch);
  const page = params.get('page');
  const sort = params.get('sort');
  if (page && sort) {
    const sortSplit = sort.split(',');
    paginationState.activePage = +page;
    paginationState.sort = sortSplit[0];
    paginationState.order = sortSplit[1];
  }
  return paginationState;
};

export const overridePaginationStateWithQueryParamsAndFilter = (paginationState: IPaginationFilterState, locationSearch: string) => {
  const params = new URLSearchParams(locationSearch);
  const page = params.get('page');
  const sort = params.get('sort');
  const user = params.get('user');
  const bookCopy = params.get('bookCopy');
  const state = params.get('state');
  if (page && sort) {
    const sortSplit = sort.split(',');
    paginationState.activePage = +page;
    paginationState.sort = sortSplit[0];
    paginationState.order = sortSplit[1];
    paginationState.user = user ?? '';
    paginationState.bookCopy = bookCopy ?? '';
    paginationState.state = STATE_CHECKOUT_VALUES.find(it => it === state) ?? 'ALL';
  }
  return paginationState;
};
