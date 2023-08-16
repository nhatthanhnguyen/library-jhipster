import React from 'react';

import { Route } from 'react-router-dom';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import UserManagement from './user-management';
import Logs from './logs/logs';
import Health from './health/health';
import Metrics from './metrics/metrics';
import Configuration from './configuration/configuration';
import Docs from './docs/docs';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';
import ReaderManagementRoutes from './reader-management';

const AdministrationRoutes = () => {
  const hasAdminAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const hasLibrarianAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.LIBRARIAN]));
  const isAdmin = hasAdminAuthority && !hasLibrarianAuthority;
  return (
    <div>
      <ErrorBoundaryRoutes>
        {isAdmin ? (
          <>
            <Route path="librarian-management/*" element={<UserManagement />} />
            <Route path="health" element={<Health />} />
            <Route path="metrics" element={<Metrics />} />
            <Route path="configuration" element={<Configuration />} />
            <Route path="logs" element={<Logs />} />
            <Route path="docs" element={<Docs />} />
          </>
        ) : (
          <Route path="reader-management/*" element={<ReaderManagementRoutes />} />
        )}
      </ErrorBoundaryRoutes>
    </div>
  );
};

export default AdministrationRoutes;
