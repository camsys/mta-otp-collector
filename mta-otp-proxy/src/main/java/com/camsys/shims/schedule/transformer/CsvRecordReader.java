package com.camsys.shims.schedule.transformer;

import com.csvreader.CsvReader;

import java.io.IOException;

/**
 * Contract for reading a CSV row into a model and subsequently filtering it.
 *
 */
public interface CsvRecordReader<T> {

    /**
     * read a CSV row and return as an object.
     *
     * @param reader a {@link com.csvreader.CsvReader} object.
     * @throws java.io.IOException
     * @return a T object.
     */
    T readRecord(CsvReader reader) throws IOException;

    /**
     * filter the object, where filter means return false if filter param == route id
     *
     * @param filter a {@link java.lang.String} object.
     * @return false if filter param == route id
     * @param record a T object.
     */
    boolean filter(T record, String filter);
}
