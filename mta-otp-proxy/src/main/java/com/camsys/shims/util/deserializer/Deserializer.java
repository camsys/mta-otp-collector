package com.camsys.shims.util.deserializer;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Deserializer interface.</p>
 *
 */
public interface Deserializer<T> {
    /**
     * <p>deserialize.</p>
     *
     * @param inputStream a {@link java.io.InputStream} object.
     * @return a T object.
     * @throws java.io.IOException if any.
     */
    T deserialize(InputStream inputStream) throws IOException;
    /**
     * <p>getMimeType.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getMimeType();
}
