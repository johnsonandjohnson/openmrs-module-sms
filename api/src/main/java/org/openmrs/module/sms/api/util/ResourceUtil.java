package org.openmrs.module.sms.api.util;

import org.apache.commons.io.IOUtils;
import org.openmrs.module.sms.api.exception.SmsRuntimeException;

import java.io.IOException;
import java.io.InputStream;

public final class ResourceUtil {

    public static String readResourceFile(String fileName) throws SmsRuntimeException {
        try (InputStream in = ResourceUtil.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) {
                throw new SmsRuntimeException("Resource '" + fileName + "' doesn't exist");
            }
            return IOUtils.toString(in);
        } catch (IOException e) {
            throw new SmsRuntimeException(e);
        }
    }

    private ResourceUtil() { }

}
