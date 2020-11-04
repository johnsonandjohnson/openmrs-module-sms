package org.openmrs.module.sms.util;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public final class TestUtil {

    public static final String APPLICATION_JSON = "application/json";

    public static final String UTF_8 = "UTF-8";

    public static byte[] encodeString() throws Exception {
        return getBodyParamsJsonAsString().getBytes(UTF_8);
    }

    private static String getBodyParamsJsonAsString() throws Exception {
        Map<String, Object> bodyParams = new HashMap<>();
        bodyParams.put("message_uuid", "7f8c2803-687e-4364-91b205253e0527fd6");
        bodyParams.put("status", "delivered");
        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(bodyParams);
    }
}
