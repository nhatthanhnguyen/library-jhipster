import { IBook } from 'app/shared/model/book.model';

export interface ICategory {
  id?: number;
  name?: string;
  createdBy?: string;
  createdDate?: Date | null;
  lastModifiedBy?: string;
  lastModifiedDate?: Date | null;
  books?: IBook[] | null;
}

export const defaultValue: Readonly<ICategory> = {};
