import { IPublisher } from 'app/shared/model/publisher.model';
import { IAuthor } from 'app/shared/model/author.model';
import { ICategory } from 'app/shared/model/category.model';

export interface IBook {
  id?: string;
  title?: string;
  isDeleted?: boolean | null;
  publisher?: IPublisher | null;
  authors?: IAuthor[] | null;
  categories?: ICategory[] | null;
}

export const defaultValue: Readonly<IBook> = {
  isDeleted: false,
};
