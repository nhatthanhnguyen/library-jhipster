import book from 'app/entities/book/book.reducer';
import author from 'app/entities/author/author.reducer';
import category from 'app/entities/category/category.reducer';
import publisher from 'app/entities/publisher/publisher.reducer';
import bookCopy from 'app/entities/book-copy/book-copy.reducer';
import checkout from 'app/entities/checkout/checkout.reducer';
import reservation from 'app/entities/reservation/reservation.reducer';
import notification from 'app/entities/notification/notification.reducer';
import queue from 'app/entities/queue/queue.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  book,
  author,
  category,
  publisher,
  bookCopy,
  checkout,
  reservation,
  notification,
  queue,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
