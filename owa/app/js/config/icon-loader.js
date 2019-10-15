import { 
  faClipboardList,
  faShareSquare, 
  faPaperPlane,
  faHome,
  faChevronRight
} from '@fortawesome/free-solid-svg-icons';
import { library } from '@fortawesome/fontawesome-svg-core';


export const loadIcons = () => {
  library.add(
    faClipboardList,
    faShareSquare,
    faPaperPlane,
    faHome,
    faChevronRight
  );
};
