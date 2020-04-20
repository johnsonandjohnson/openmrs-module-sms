package org.openmrs.module.sms.extension.html;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

import java.util.HashMap;
import java.util.Map;

/**
 * This class defines the links that will appear on the administration page under the "sms.title"
 * heading. This extension is enabled by defining (uncommenting) it in the config.xml file.
 */
public class AdminList extends AdministrationSectionExt {

    /**
     * @see org.openmrs.module.web.extension.AdministrationSectionExt#getMediaType()
     */
    public Extension.MEDIA_TYPE getMediaType() {
        return Extension.MEDIA_TYPE.html;
    }

    /**
     * @see org.openmrs.module.web.extension.AdministrationSectionExt#getTitle()
     */
    public String getTitle() {
        return "sms.title";
    }

    /**
     * @see org.openmrs.module.web.extension.AdministrationSectionExt#getLinks()
     */
    public Map<String, String> getLinks() {

        Map<String, String> map = new HashMap<String, String>();

        map.put("module/sms/sms.form", "sms.title");
        map.put("sms/sms.page", "sms.refapp.title");

        return map;
    }

}
