import pick from 'lodash/pick';
import { getUrlParameter, IPaginationBaseState } from 'react-jhipster';

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

export interface IPaginationState extends IPaginationBaseState {
  search: string;
}

export const getCurrentSortState = (
  location: { search: string },
  itemsPerPage: number,
  sortField = 'id',
  sortOrder = 'asc',
  searchText = ''
): IPaginationState => {
  const pageParam = getUrlParameter('page', location.search);
  const sortParam = getUrlParameter('sort', location.search);
  const searchParam = getUrlParameter('search', location.search);
  let sort = sortField;
  let order = sortOrder;
  let activePage = 1;
  if (pageParam !== '' && !isNaN(parseInt(pageParam, 10))) {
    activePage = parseInt(pageParam, 10);
  }
  if (sortParam !== '') {
    sort = sortParam.split(',')[0];
    order = sortParam.split(',')[1];
  }
  return { itemsPerPage, sort, order, activePage, search: searchParam };
};

export const overridePaginationStateWithQueryParamsWithSearch = (paginationState: IPaginationState, locationSearch: string) => {
  const params = new URLSearchParams(locationSearch);
  const page = params.get('page');
  const sort = params.get('sort');
  const search = params.get('search');
  if (page && sort && search) {
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
