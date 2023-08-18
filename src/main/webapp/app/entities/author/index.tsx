import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Author from './author';
import AuthorUpdate from './author-update';
import AuthorDeleteDialog from './author-delete-dialog';
import AuthorDetail from './author-detail';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

const AuthorRoutes = () => {
  const librarianAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.LIBRARIAN]));
  const adminAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const isAdmin = adminAuthority && !librarianAuthority;
  return (
    <ErrorBoundaryRoutes>
      {isAdmin ? null : (
        <>
          <Route index element={<Author />} />
          {librarianAuthority ? <Route path="new" element={<AuthorUpdate />} /> : null}
          <Route path=":id">
            <Route index element={<AuthorDetail />} />
            {librarianAuthority ? (
              <>
                <Route path="edit" element={<AuthorUpdate />} />
                <Route path="delete" element={<AuthorDeleteDialog />} />
              </>
            ) : null}
          </Route>
        </>
      )}
    </ErrorBoundaryRoutes>
  );
};

export default AuthorRoutes;
