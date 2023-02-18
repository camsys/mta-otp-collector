package com.camsys.shims.util.deserializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface Deserializer<T> {
    T deserialize(InputStream inputStream) throws IOException;
    String getMimeType();
    Map<String, String> getApiHeaders();
}
