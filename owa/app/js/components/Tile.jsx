/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import _ from 'lodash';

const Tile = (props) => {
  const { name, href, icon } = props;
  return (
    <a href={href} className="button app big" >
      {_.isEmpty(icon) ?
        <i className='icon-align-justify big' /> :
        <FontAwesomeIcon size='3x' icon={icon} />}
      <h5>{name}</h5>
    </a>
  );
}

Tile.defaultProps = {
  name: 'New App',
  href: '#/',
  icon: []
}

export default Tile;