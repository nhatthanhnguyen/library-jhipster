import { IBook } from 'app/shared/model/book.model';

export interface IAuthor {
  id?: string;
  firstName?: string;
  lastName?: string;
  isDeleted?: boolean | null;
  books?: IBook[] | null;
}

export const defaultValue: Readonly<IAuthor> = {
  isDeleted: false,
};
