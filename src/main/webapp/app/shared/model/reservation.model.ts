import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IBookCopy } from 'app/shared/model/book-copy.model';

export interface IReservation {
  id?: string;
  startTime?: string | null;
  endTime?: string | null;
  user?: IUser | null;
  bookCopy?: IBookCopy | null;
}

export const defaultValue: Readonly<IReservation> = {};
