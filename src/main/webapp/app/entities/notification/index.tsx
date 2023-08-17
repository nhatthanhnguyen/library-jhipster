import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Notification from './notification';
import NotificationDeleteDialog from './notification-delete-dialog';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

const NotificationRoutes = () => {
  const librarianAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.LIBRARIAN]));
  const adminAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const isAdmin = adminAuthority && !librarianAuthority;
  return (
    <ErrorBoundaryRoutes>
      {isAdmin ? null : (
        <>
          {librarianAuthority ? (
            <>
              <Route index element={<Notification />} />
              {/* <Route path="new" element={<NotificationUpdate/>}/> */}
              <Route path=":id">
                {/* <Route index element={<NotificationDetail/>}/>
                <Route path="edit" element={<NotificationUpdate/>}/> */}
                <Route path="delete" element={<NotificationDeleteDialog />} />
              </Route>
            </>
          ) : null}
        </>
      )}
    </ErrorBoundaryRoutes>
  );
};

export default NotificationRoutes;
