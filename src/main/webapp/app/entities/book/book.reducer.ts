import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';

import { cleanEntity } from 'app/shared/util/entity-utils';
import {
  createEntitySlice,
  EntityState,
  IQueryParams,
  IQueryParamsAuthor,
  IQueryParamsCategory,
  IQueryParamsPublisher,
  serializeAxiosError,
} from 'app/shared/reducers/reducer.utils';
import { defaultValue, IBook } from 'app/shared/model/book.model';
import { IHoldBook } from 'app/shared/model/reservation.model';
import { IWaitBook } from 'app/shared/model/queue.model';

const initialState: EntityState<IBook> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'api/books';

// Actions

export const getEntities = createAsyncThunk('book/fetch_entity_list', async ({ page, size, sort, query }: IQueryParams) => {
  const requestUrl = `${apiUrl}${
    sort ? `?page=${page}&size=${size}&sort=${sort}${query ? `&search=${query}` : ''}&` : '?'
  }cacheBuster=${new Date().getTime()}`;
  return axios.get<IBook[]>(requestUrl);
});

export const getEntitiesByCategory = createAsyncThunk(
  'book/fetch_entity_list_by_category',
  async ({ page, size, sort, query, categoryId }: IQueryParamsCategory) => {
    const requestUrl =
      `${apiUrl}/category/${categoryId}` +
      `${sort ? `?page=${page}&size=${size}&sort=${sort}${query ? `&search=${query}` : ''}&` : '?'}cacheBuster=${new Date().getTime()}`;
    return axios.get<IBook[]>(requestUrl);
  }
);

export const getEntitiesByPublisher = createAsyncThunk(
  'book/fetch_entity_list_by_publisher',
  async ({ page, size, sort, query, publisherId }: IQueryParamsPublisher) => {
    const requestUrl =
      `${apiUrl}/publisher/${publisherId}` +
      `${sort ? `?page=${page}&size=${size}&sort=${sort}${query ? `&search=${query}` : ''}&` : '?'}cacheBuster=${new Date().getTime()}`;
    return axios.get<IBook[]>(requestUrl);
  }
);

export const getEntitiesByAuthor = createAsyncThunk(
  'book/fetch_entity_list_by_author',
  async ({ page, size, sort, query, authorId }: IQueryParamsAuthor) => {
    const requestUrl =
      `${apiUrl}/author/${authorId}` +
      `${sort ? `?page=${page}&size=${size}&sort=${sort}${query ? `&search=${query}` : ''}&` : '?'}cacheBuster=${new Date().getTime()}`;
    return axios.get<IBook[]>(requestUrl);
  }
);

export const getAllEntities = createAsyncThunk('book/fetch_all_entities', async () => {
  const requestUrl = `${apiUrl}/all`;
  return axios.get<IBook[]>(requestUrl);
});

export const getEntity = createAsyncThunk(
  'book/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IBook>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const createEntity = createAsyncThunk(
  'book/create_entity',
  async (entity: IBook, thunkAPI) => {
    const result = await axios.post<IBook>(apiUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const updateEntity = createAsyncThunk(
  'book/update_entity',
  async (entity: IBook, thunkAPI) => {
    const result = await axios.put<IBook>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const partialUpdateEntity = createAsyncThunk(
  'book/partial_update_entity',
  async (entity: IBook, thunkAPI) => {
    const result = await axios.patch<IBook>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const deleteEntity = createAsyncThunk(
  'book/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    return await axios.delete<IBook>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const restoreEntity = createAsyncThunk(
  'book/restore_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}/restore`;
    return await axios.put<IBook>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const holdBook = createAsyncThunk(
  'book/hold',
  async (entity: IHoldBook, thunkAPI) => {
    const requestUrl = `${apiUrl}/hold`;
    const result = await axios.post<IBook>(requestUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const addToQueue = createAsyncThunk(
  'book/add_to_queue',
  async (entity: IWaitBook, thunkAPI) => {
    const requestUrl = `${apiUrl}/wait`;
    const result = await axios.post<IBook>(requestUrl, entity);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

// slice

export const BookSlice = createEntitySlice({
  name: 'book',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addCase(restoreEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addCase(holdBook.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
        state.errorMessage = null;
      })
      .addCase(holdBook.rejected, state => {
        state.updating = false;
        state.updateSuccess = false;
        state.entity = {};
      })
      .addCase(addToQueue.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
        state.errorMessage = null;
      })
      .addCase(addToQueue.rejected, state => {
        state.updating = false;
        state.updateSuccess = false;
        state.entity = {};
      })
      .addMatcher(isFulfilled(getEntities, getEntitiesByCategory, getEntitiesByPublisher, getEntitiesByAuthor), (state, action) => {
        const { data, headers } = action.payload;

        return {
          ...state,
          loading: false,
          entities: data,
          totalItems: parseInt(headers['x-total-count'], 10),
        };
      })
      .addMatcher(isFulfilled(getAllEntities), (state, action) => {
        const { data, headers } = action.payload;
        return {
          ...state,
          loading: false,
          entities: data,
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(
        isPending(getEntities, getEntity, getAllEntities, getEntitiesByCategory, getEntitiesByPublisher, getEntitiesByAuthor),
        state => {
          state.errorMessage = null;
          state.updateSuccess = false;
          state.loading = true;
        }
      )
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity, restoreEntity, holdBook, addToQueue), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = BookSlice.actions;

// Reducer
export default BookSlice.reducer;
