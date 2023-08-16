import { Translate } from 'react-jhipster';
import React from 'react';
import { NavItem, NavLink } from 'reactstrap';
import { NavLink as Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const LibrarianMenu = () => (
  <NavItem>
    <NavLink tag={Link} to="/librarian/reader-management" className="d-flex align-items-center">
      <FontAwesomeIcon icon="users-cog" />
      <span>
        <Translate contentKey="global.menu.readerManagement">Reader Management</Translate>
      </span>
    </NavLink>
  </NavItem>
);

export default LibrarianMenu;
