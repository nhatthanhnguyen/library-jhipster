import { IBook } from 'app/shared/model/book.model';

export interface ICategory {
  id?: string;
  name?: string;
  isDeleted?: boolean | null;
  books?: IBook[] | null;
}

export const defaultValue: Readonly<ICategory> = {
  isDeleted: false,
};
