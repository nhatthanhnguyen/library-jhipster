import { IBook } from 'app/shared/model/book.model';

export interface IAuthor {
  id?: number;
  firstName?: string;
  lastName?: string;
  createdBy?: string;
  createdDate?: Date | null;
  lastModifiedBy?: string;
  lastModifiedDate?: Date | null;
  books?: IBook[] | null;
}

export const defaultValue: Readonly<IAuthor> = {};
