export interface IPublisher {
  id?: number;
  name?: string;
  isDeleted?: boolean | null;
}

export const defaultValue: Readonly<IPublisher> = {
  isDeleted: false,
};
