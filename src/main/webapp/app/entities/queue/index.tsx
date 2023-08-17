import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Queue from './queue';
import QueueDeleteDialog from './queue-delete-dialog';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

const QueueRoutes = () => {
  const librarianAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.LIBRARIAN]));
  const adminAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const isAdmin = adminAuthority && !librarianAuthority;
  return (
    <ErrorBoundaryRoutes>
      {isAdmin ? null : (
        <>
          {librarianAuthority ? (
            <>
              <Route index element={<Queue />} />
              {/* <Route path="new" element={<QueueUpdate />} /> */}
              <Route path=":id">
                {/* <Route index element={<QueueDetail />} />
                <Route path="edit" element={<QueueUpdate />} /> */}
                <Route path="delete" element={<QueueDeleteDialog />} />
              </Route>
            </>
          ) : null}
        </>
      )}
    </ErrorBoundaryRoutes>
  );
};

export default QueueRoutes;
