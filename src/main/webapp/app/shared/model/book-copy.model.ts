import { ICheckout } from 'app/shared/model/checkout.model';
import { IReservation } from 'app/shared/model/reservation.model';
import { INotification } from 'app/shared/model/notification.model';
import { IBook } from 'app/shared/model/book.model';

export interface IBookCopy {
  id?: number;
  yearPublished?: number;
  isDeleted?: boolean | null;
  checkouts?: ICheckout[] | null;
  reservations?: IReservation[] | null;
  notifications?: INotification[] | null;
  book?: IBook | null;
}

export const defaultValue: Readonly<IBookCopy> = {
  isDeleted: false,
};
