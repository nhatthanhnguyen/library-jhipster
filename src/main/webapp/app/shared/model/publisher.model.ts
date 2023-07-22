import { IBook } from 'app/shared/model/book.model';

export interface IPublisher {
  id?: number;
  name?: string;
  isDeleted?: boolean | null;
  books?: IBook[] | null;
}

export const defaultValue: Readonly<IPublisher> = {
  isDeleted: false,
};
