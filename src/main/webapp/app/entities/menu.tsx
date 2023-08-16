import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

const EntitiesMenu = () => {
  const hasAdminAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const hasLibrarianAuthority = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.LIBRARIAN]));
  const isReader = !hasAdminAuthority && !hasLibrarianAuthority;
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/book">
        <Translate contentKey="global.menu.entities.book"/>
      </MenuItem>
      <MenuItem icon="asterisk" to="/author">
        <Translate contentKey="global.menu.entities.author" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/category">
        <Translate contentKey="global.menu.entities.category" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/publisher">
        <Translate contentKey="global.menu.entities.publisher" />
      </MenuItem>
      {isReader ? null : (
        <MenuItem icon="asterisk" to="/book-copy">
          <Translate contentKey="global.menu.entities.bookCopy" />
        </MenuItem>
      )}
      <MenuItem icon="asterisk" to="/checkout">
        <Translate contentKey="global.menu.entities.checkout" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/reservation">
        <Translate contentKey="global.menu.entities.reservation" />
      </MenuItem>
      {isReader ? null : (
        <>
          <MenuItem icon="asterisk" to="/notification">
            <Translate contentKey="global.menu.entities.notification" />
          </MenuItem>
          <MenuItem icon="asterisk" to="/queue">
            <Translate contentKey="global.menu.entities.queue" />
          </MenuItem>
        </>
      )}
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
