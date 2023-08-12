import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Book from './book';
import BookDetail from './book-detail';
import BookUpdate from './book-update';
import BookDeleteDialog from './book-delete-dialog';
import BookRestoreDialog from 'app/entities/book/book-restore-dialog';
import PrivateRoute from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';
import BookReservationDialog from 'app/entities/book/book-reservation-dialog';
import BookWaitDialog from 'app/entities/book/book-wait-dialog';

const BookRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Book />} />
    <Route
      path="new"
      element={
        <PrivateRoute hasAnyAuthorities={[AUTHORITIES.LIBRARIAN]}>
          <BookUpdate />
        </PrivateRoute>
      }
    />
    <Route path=":id">
      <Route index element={<BookDetail />} />
      <Route
        path="edit"
        element={
          <PrivateRoute hasAnyAuthorities={[AUTHORITIES.LIBRARIAN]}>
            <BookUpdate />
          </PrivateRoute>
        }
      />
      <Route
        path="delete"
        element={
          <PrivateRoute hasAnyAuthorities={[AUTHORITIES.LIBRARIAN]}>
            <BookDeleteDialog />
          </PrivateRoute>
        }
      />
      <Route
        path="restore"
        element={
          <PrivateRoute hasAnyAuthorities={[AUTHORITIES.LIBRARIAN]}>
            <BookRestoreDialog />
          </PrivateRoute>
        }
      />
      <Route
        path="hold"
        element={
          <PrivateRoute hasAnyAuthorities={[AUTHORITIES.USER]}>
            <BookReservationDialog />
          </PrivateRoute>
        }
      />
      <Route
        path="wait"
        element={
          <PrivateRoute hasAnyAuthorities={[AUTHORITIES.USER]}>
            <BookWaitDialog />
          </PrivateRoute>
        }
      />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BookRoutes;
