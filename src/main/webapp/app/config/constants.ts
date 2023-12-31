export const AUTHORITIES = {
  ADMIN: 'ROLE_ADMIN',
  LIBRARIAN: 'ROLE_LIBRARIAN',
  USER: 'ROLE_USER',
};

export const messages = {
  DATA_ERROR_ALERT: 'Internal Error',
};

export const APP_DATE_FORMAT = 'DD/MM/YY HH:mm';
export const APP_TIMESTAMP_FORMAT = 'DD/MM/YY HH:mm:ss';
export const APP_LOCAL_DATE_FORMAT = 'DD/MM/YYYY';
export const APP_LOCAL_DATETIME_FORMAT = 'YYYY-MM-DDTHH:mm';
export const APP_WHOLE_NUMBER_FORMAT = '0,0';
export const APP_TWO_DIGITS_AFTER_POINT_NUMBER_FORMAT = '0,0.[00]';

export const STATE_CHECKOUT_VALUES = ['ALL', 'BORROWING', 'RETURNSUCCESS', 'RETURNFAILED'];
export const STATE_I18_VALUES = [
  'libraryApp.checkout.filter.state.all',
  'libraryApp.checkout.filter.state.borrowing',
  'libraryApp.checkout.filter.state.returnSuccess',
  'libraryApp.checkout.filter.state.returnFailed',
];
