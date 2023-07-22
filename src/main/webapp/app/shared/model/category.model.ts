import { IBook } from 'app/shared/model/book.model';

export interface ICategory {
  id?: number;
  name?: string;
  isDeleted?: boolean | null;
  books?: IBook[] | null;
}

export const defaultValue: Readonly<ICategory> = {
  isDeleted: false,
};
