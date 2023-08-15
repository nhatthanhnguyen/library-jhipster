import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IBook } from 'app/shared/model/book.model';

export interface IQueue {
  id?: number;
  createdAt?: string | null;
  user?: IUser | null;
  book?: IBook | null;
}

export interface IWaitBook {
  userId?: number;
  bookId?: number;
}

export const defaultValue: Readonly<IQueue> = {};
