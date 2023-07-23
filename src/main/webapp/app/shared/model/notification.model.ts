import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IBookCopy } from 'app/shared/model/book-copy.model';
import { Type } from 'app/shared/model/enumerations/type.model';

export interface INotification {
  id?: string;
  sentAt?: string | null;
  type?: Type | null;
  user?: IUser | null;
  bookCopy?: IBookCopy | null;
}

export const defaultValue: Readonly<INotification> = {};
