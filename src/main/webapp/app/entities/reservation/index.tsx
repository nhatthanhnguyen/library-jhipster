import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Reservation from './reservation';
import ReservationDetail from './reservation-detail';
import ReservationUpdate from './reservation-update';
import ReservationDeleteDialog from './reservation-delete-dialog';
import ReservationBorrowDialog from 'app/entities/reservation/reservation-borrow-dialog';

const ReservationRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Reservation />} />
    <Route path="new" element={<ReservationUpdate />} />
    <Route path=":id">
      <Route index element={<ReservationDetail />} />
      <Route path="edit" element={<ReservationUpdate />} />
      <Route path="borrow/:bookCopyId" element={<ReservationBorrowDialog />} />
      <Route path="delete" element={<ReservationDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ReservationRoutes;
