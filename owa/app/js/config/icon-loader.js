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
