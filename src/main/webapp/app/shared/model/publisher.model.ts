export interface IPublisher {
  id?: string;
  name?: string;
  isDeleted?: boolean | null;
}

export const defaultValue: Readonly<IPublisher> = {
  isDeleted: false,
};
