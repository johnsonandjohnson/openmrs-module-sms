package org.openmrs.module.sms.api.util;

import org.apache.commons.io.IOUtils;
import org.openmrs.module.sms.api.exception.SmsRuntimeException;

import java.io.IOException;
import java.io.InputStream;

public final class ResourceUtil {

    public static boolean resourceFileExists(String filename) {
        return ResourceUtil.class.getClassLoader().getResource(filename) != null;
    }

    public static String readResourceFile(String filename) throws SmsRuntimeException {
        try (InputStream in = ResourceUtil.class.getClassLoader().getResourceAsStream(filename)) {
            if (in == null) {
                throw new SmsRuntimeException("Resource '" + filename + "' doesn't exist");
            }
            return IOUtils.toString(in);
        } catch (IOException e) {
            throw new SmsRuntimeException(e);
        }
    }

    private ResourceUtil() { }

}
