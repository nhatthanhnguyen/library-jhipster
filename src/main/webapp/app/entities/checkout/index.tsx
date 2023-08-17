import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Checkout from './checkout';
import CheckoutUpdate from './checkout-update';
import CheckoutDeleteDialog from './checkout-delete-dialog';
import CheckoutBorrow from 'app/entities/checkout/checkout-borrow';
import CheckoutReturnDialog from 'app/entities/checkout/checkout-return-dialog';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

const CheckoutRoutes = () => {
  const librarianAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.LIBRARIAN]));
  const adminAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const isAdmin = adminAuthority && !librarianAuthority;
  return (
    <ErrorBoundaryRoutes>
      {isAdmin ? null : (
        <>
          <Route index element={<Checkout />} />
          {librarianAuthority ? (
            <>
              <Route path="borrow" element={<CheckoutBorrow />} />
              <Route path="new" element={<CheckoutUpdate />} />
              <Route path=":id">
                {/* <Route index element={<CheckoutDetail />} /> */}
                {/* <Route path="edit" element={<CheckoutUpdate />} /> */}
                <Route path="delete" element={<CheckoutDeleteDialog />} />
                <Route path="return" element={<CheckoutReturnDialog />} />
              </Route>
            </>
          ) : null}
        </>
      )}
    </ErrorBoundaryRoutes>
  );
};

export default CheckoutRoutes;
