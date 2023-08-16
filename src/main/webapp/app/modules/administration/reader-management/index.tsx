import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import ReaderManagementUpdate from 'app/modules/administration/reader-management/reader-management-update';
import ReaderManagementDetail from 'app/modules/administration/reader-management/reader-management-detail';
import ReaderManagementDeleteDialog from 'app/modules/administration/reader-management/reader-management-delete-dialog';
import ReaderManagement from 'app/modules/administration/reader-management/reader-management';

const ReaderManagementRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ReaderManagement />} />
    <Route path="new" element={<ReaderManagementUpdate />} />
    <Route path=":login">
      <Route index element={<ReaderManagementDetail />} />
      <Route path="edit" element={<ReaderManagementUpdate />} />
      <Route path="delete" element={<ReaderManagementDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ReaderManagementRoutes;
