import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Book from './book';
import Author from './author';
import Category from './category';
import Publisher from './publisher';
import BookCopy from './book-copy';
import Checkout from './checkout';
import Reservation from './reservation';
import Notification from './notification';
import Queue from './queue';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="book/*" element={<Book />} />
        <Route path="author/*" element={<Author />} />
        <Route path="category/*" element={<Category />} />
        <Route path="publisher/*" element={<Publisher />} />
        <Route path="book-copy/*" element={<BookCopy />} />
        <Route path="checkout/*" element={<Checkout />} />
        <Route path="reservation/*" element={<Reservation />} />
        <Route path="notification/*" element={<Notification />} />
        <Route path="queue/*" element={<Queue />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
