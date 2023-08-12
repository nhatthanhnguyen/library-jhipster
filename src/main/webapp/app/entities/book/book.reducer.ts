import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { createEntitySlice, EntityState, IQueryParams, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { defaultValue, IBook } from 'app/shared/model/book.model';

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
    const result = await axios.delete<IBook>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const restoreEntity = createAsyncThunk(
  'book/restore_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}/restore`;
    const result = await axios.put<IBook>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const holdBook = createAsyncThunk(
  'book/hold',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}/hold`;
    const result = await axios.post<IBook>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const addToQueue = createAsyncThunk(
  'book/add_to_queue',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}/wait`;
    const result = await axios.post<IBook>(requestUrl);
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
        state.errorMessage = 'Cannot';
      })
      .addCase(addToQueue.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addMatcher(isFulfilled(getEntities), (state, action) => {
        const { data, headers } = action.payload;

        return {
          ...state,
          loading: false,
          entities: data,
          totalItems: parseInt(headers['x-total-count'], 10),
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(isPending(getEntities, getEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
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
