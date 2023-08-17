import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import BookCopy from './book-copy';
import BookCopyDetail from './book-copy-detail';
import BookCopyUpdate from './book-copy-update';
import BookCopyDeleteDialog from './book-copy-delete-dialog';
import BookCopyRestoreDialog from 'app/entities/book-copy/book-copy-restore-dialog';

const BookCopyRoutes = () => {
  return (
    <ErrorBoundaryRoutes>
      <Route index element={<BookCopy />} />
      <Route path="new" element={<BookCopyUpdate />} />
      <Route path=":id">
        <Route index element={<BookCopyDetail />} />
        <Route path="edit" element={<BookCopyUpdate />} />
        <Route path="restore" element={<BookCopyRestoreDialog />} />
        <Route path="delete" element={<BookCopyDeleteDialog />} />
      </Route>
    </ErrorBoundaryRoutes>
  );
};

export default BookCopyRoutes;
