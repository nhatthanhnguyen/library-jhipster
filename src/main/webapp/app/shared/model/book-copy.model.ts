import { IBook } from 'app/shared/model/book.model';

export interface IBookCopy {
  id?: string;
  yearPublished?: number;
  isDeleted?: boolean | null;
  book?: IBook | null;
}

export const defaultValue: Readonly<IBookCopy> = {
  isDeleted: false,
};
