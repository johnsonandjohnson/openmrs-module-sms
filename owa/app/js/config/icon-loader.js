/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

import {
  faClipboardList,
  faShareSquare, 
  faPaperPlane,
  faHome,
  faChevronRight,
  faStar,
  faCog,
  faFileImport
} from '@fortawesome/free-solid-svg-icons';
import {
  faStar as farStar
} from '@fortawesome/free-regular-svg-icons';
import { library } from '@fortawesome/fontawesome-svg-core';


export const loadIcons = () => {
  library.add(
    faClipboardList,
    faShareSquare,
    faPaperPlane,
    faHome,
    faChevronRight,
    faStar,
    farStar,
    faCog,
    faFileImport
  );
};
