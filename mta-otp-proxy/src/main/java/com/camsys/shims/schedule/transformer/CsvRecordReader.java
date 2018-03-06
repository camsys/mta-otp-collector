package com.camsys.shims.schedule.transformer;

import com.csvreader.CsvReader;

import java.io.IOException;

/**
 * Contract for reading a CSV row into a model and subsequently filtering it.
 */
public interface CsvRecordReader {

    Object readRecord(CsvReader reader) throws IOException;

    boolean filter(Object record, String filter);
}
