import { IPublisher } from 'app/shared/model/publisher.model';
import { IAuthor } from 'app/shared/model/author.model';
import { ICategory } from 'app/shared/model/category.model';

export interface IBook {
  id?: number;
  title?: string;
  isDeleted?: boolean | null;
  publisher?: IPublisher | null;
  yearPublished?: number | string;
  authors?: IAuthor[] | null;
  categories?: ICategory[] | null;
  createdBy?: string;
  createdDate?: Date | null;
  lastModifiedBy?: string;
  lastModifiedDate?: Date | null;
}

export const defaultValue: Readonly<IBook> = {
  isDeleted: false,
};
