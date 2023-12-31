import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Publisher from './publisher';
import PublisherUpdate from './publisher-update';
import PublisherDeleteDialog from './publisher-delete-dialog';
import PublisherDetail from './publisher-detail';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

const PublisherRoutes = () => {
  const librarianAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.LIBRARIAN]));
  const adminAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const isAdmin = adminAuthority && !librarianAuthority;
  return (
    <ErrorBoundaryRoutes>
      {isAdmin ? null : (
        <>
          <Route index element={<Publisher />} />
          {librarianAuthority ? <Route path="new" element={<PublisherUpdate />} /> : null}
          <Route path=":id">
            <Route index element={<PublisherDetail />} />
            {librarianAuthority ? (
              <>
                <Route path="edit" element={<PublisherUpdate />} />
                <Route path="delete" element={<PublisherDeleteDialog />} />
              </>
            ) : null}
          </Route>
        </>
      )}
    </ErrorBoundaryRoutes>
  );
};

export default PublisherRoutes;
