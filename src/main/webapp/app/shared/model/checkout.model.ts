import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IBookCopy } from 'app/shared/model/book-copy.model';

export interface ICheckout {
  id?: number;
  startTime?: string | null;
  endTime?: string | null;
  isReturned?: boolean | null;
  user?: IUser | null;
  bookCopy?: IBookCopy | null;
}

export interface ICheckoutBorrow {
  userId?: number;
  bookCopyId?: number;
}

export interface ICheckoutReturn {
  id?: number;
  success?: boolean | null;
}

export const defaultValue: Readonly<ICheckout> = {
  isReturned: false,
};
