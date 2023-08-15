import { IUser } from 'app/shared/model/user.model';
import { IBookCopy } from 'app/shared/model/book-copy.model';

export interface IReservation {
  id?: number;
  startTime?: string | null;
  endTime?: string | null;
  user?: IUser | null;
  bookCopy?: IBookCopy | null;
}

export interface IHoldBook {
  userId?: number;
  bookId?: number;
}

export interface IHoldSpecificBook {
  userId?: number;
  bookCopyId?: number;
}

export const defaultValue: Readonly<IReservation> = {};
