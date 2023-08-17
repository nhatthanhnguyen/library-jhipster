import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { createEntitySlice, EntityState, IFilterCheckoutParams, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { defaultValue, ICheckout, ICheckoutBorrow, ICheckoutReturn } from 'app/shared/model/checkout.model';
import { IWaitBook } from 'app/shared/model/queue.model';

const initialState: EntityState<ICheckout> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'api/checkouts';

// Actions

export const getEntities = createAsyncThunk(
  'checkout/fetch_entity_list',
  async ({ page, size, sort, user, bookCopy, state }: IFilterCheckoutParams) => {
    const filterRequest = `${user ? `&user=${user}` : ''}${bookCopy ? `&bookCopy=${bookCopy}` : ''}${state ? `&state=${state}` : ''}`;
    const requestUrl = `${apiUrl}${
      sort ? `?page=${page}&size=${size}&sort=${sort}${filterRequest}&` : '?'
    }cacheBuster=${new Date().getTime()}`;
    return axios.get<ICheckout[]>(requestUrl);
  }
);

export const getEntity = createAsyncThunk(
  'checkout/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<ICheckout>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const createEntity = createAsyncThunk(
  'checkout/create_entity',
  async (entity: ICheckout, thunkAPI) => {
    const result = await axios.post<ICheckout>(apiUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const updateEntity = createAsyncThunk(
  'checkout/update_entity',
  async (entity: ICheckout, thunkAPI) => {
    const result = await axios.put<ICheckout>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const partialUpdateEntity = createAsyncThunk(
  'checkout/partial_update_entity',
  async (entity: ICheckout, thunkAPI) => {
    const result = await axios.patch<ICheckout>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const deleteEntity = createAsyncThunk(
  'checkout/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await axios.delete<ICheckout>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const borrowBook = createAsyncThunk(
  'checkout/borrow_book',
  async (entity: ICheckoutBorrow, thunkAPI) => {
    const result = await axios.post<ICheckout>(`${apiUrl}/borrow`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const returnBook = createAsyncThunk(
  'checkout/return_book',
  async (entity: ICheckoutReturn, thunkAPI) => {
    const result = await axios.put<ICheckout>(`${apiUrl}/return`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

// slice

export const CheckoutSlice = createEntitySlice({
  name: 'checkout',
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
      .addCase(borrowBook.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
        state.errorMessage = null;
      })
      .addCase(returnBook.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
        state.errorMessage = null;
      })
      .addCase(borrowBook.rejected, state => {
        state.updating = false;
        state.updateSuccess = false;
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
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity, borrowBook, returnBook), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = CheckoutSlice.actions;

// Reducer
export default CheckoutSlice.reducer;
