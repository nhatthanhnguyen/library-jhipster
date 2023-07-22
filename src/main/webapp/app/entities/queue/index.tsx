import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Queue from './queue';
import QueueDetail from './queue-detail';
import QueueUpdate from './queue-update';
import QueueDeleteDialog from './queue-delete-dialog';

const QueueRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Queue />} />
    <Route path="new" element={<QueueUpdate />} />
    <Route path=":id">
      <Route index element={<QueueDetail />} />
      <Route path="edit" element={<QueueUpdate />} />
      <Route path="delete" element={<QueueDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default QueueRoutes;
