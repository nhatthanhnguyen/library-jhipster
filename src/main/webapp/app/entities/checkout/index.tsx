import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Checkout from './checkout';
import CheckoutUpdate from './checkout-update';
import CheckoutDeleteDialog from './checkout-delete-dialog';
import CheckoutBorrow from 'app/entities/checkout/checkout-borrow';
import CheckoutReturnDialog from 'app/entities/checkout/checkout-return-dialog';

const CheckoutRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Checkout />} />
    <Route path="borrow" element={<CheckoutBorrow />} />
    <Route path="new" element={<CheckoutUpdate />} />
    <Route path=":id">
      {/* <Route index element={<CheckoutDetail />} /> */}
      {/* <Route path="edit" element={<CheckoutUpdate />} /> */}
      <Route path="delete" element={<CheckoutDeleteDialog />} />
      <Route path="return" element={<CheckoutReturnDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CheckoutRoutes;
