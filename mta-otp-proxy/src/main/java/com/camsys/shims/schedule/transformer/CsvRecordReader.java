package com.camsys.shims.schedule.transformer;

import com.csvreader.CsvReader;

import java.io.IOException;

/**
 * Contract for reading a CSV row into a model and subsequently filtering it.
 */
public interface CsvRecordReader<T> {

    /**
     * read a CSV row and return as an object.
     * @param reader
     * @return
     * @throws IOException
     */
    T readRecord(CsvReader reader) throws IOException;

    /**
     * filter the object, where filter means return false if filter param == route id
     * @param rbs
     * @param filter
     * @return
     */
    boolean filter(T record, String filter);

    /**
     * Read in headers
     */
    void readHeaders(String[] headers);
}
