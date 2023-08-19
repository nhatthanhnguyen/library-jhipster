import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Book from './book';
import BookDetail from './book-detail';
import BookUpdate from './book-update';
import BookDeleteDialog from './book-delete-dialog';
import BookRestoreDialog from 'app/entities/book/book-restore-dialog';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';
import BookReservationDialog from 'app/entities/book/book-reservation-dialog';
import BookWaitDialog from 'app/entities/book/book-wait-dialog';
import { useAppSelector } from 'app/config/store';
import BookCopyDeleteDialog from 'app/entities/book/book-detail-book-copy-delete-dialog';
import BookCopyRestoreDialog from 'app/entities/book/book-detail-book-copy-restore-dialog';

const BookRoutes = () => {
  const librarianAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.LIBRARIAN]));
  const adminAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const isAdmin = adminAuthority && !librarianAuthority;
  return (
    <ErrorBoundaryRoutes>
      {isAdmin ? null : (
        <>
          <Route index element={<Book />} />
          <Route path="new" element={<BookUpdate />} />
          <Route path=":id">
            <Route index element={<BookDetail />} />
            {librarianAuthority ? (
              <>
                <Route path="edit" element={<BookUpdate />} />
                <Route path="delete" element={<BookDeleteDialog />} />
                <Route path="restore" element={<BookRestoreDialog />} />
                <Route path="book-copy/:bookCopyId/delete" element={<BookCopyDeleteDialog />} />
                <Route path="book-copy/:bookCopyId/restore" element={<BookCopyRestoreDialog />} />
              </>
            ) : null}
            <Route path="hold" element={<BookReservationDialog />} />
            <Route path="wait" element={<BookWaitDialog />} />
          </Route>
        </>
      )}
    </ErrorBoundaryRoutes>
  );
};

export default BookRoutes;
