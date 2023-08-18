import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Category from './category';
import CategoryUpdate from './category-update';
import CategoryDeleteDialog from './category-delete-dialog';
import CategoryDetail from './category-detail';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

const CategoryRoutes = () => {
  const librarianAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.LIBRARIAN]));
  const adminAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const isAdmin = adminAuthority && !librarianAuthority;
  return (
    <ErrorBoundaryRoutes>
      {isAdmin ? null : (
        <>
          <Route index element={<Category />} />
          <Route path="new" element={<CategoryUpdate />} />
          <Route path=":id">
            <Route index element={<CategoryDetail />} />
            {librarianAuthority ? (
              <>
                <Route path="edit" element={<CategoryUpdate />} />
                <Route path="delete" element={<CategoryDeleteDialog />} />
              </>
            ) : null}
          </Route>
        </>
      )}
    </ErrorBoundaryRoutes>
  );
};

export default CategoryRoutes;
