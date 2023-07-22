import { IBookCopy } from 'app/shared/model/book-copy.model';
import { IQueue } from 'app/shared/model/queue.model';
import { IAuthor } from 'app/shared/model/author.model';
import { ICategory } from 'app/shared/model/category.model';
import { IPublisher } from 'app/shared/model/publisher.model';

export interface IBook {
  id?: number;
  title?: string;
  isDeleted?: boolean | null;
  bookCopies?: IBookCopy[] | null;
  queues?: IQueue[] | null;
  authors?: IAuthor[] | null;
  categories?: ICategory[] | null;
  publisher?: IPublisher | null;
}

export const defaultValue: Readonly<IBook> = {
  isDeleted: false,
};
