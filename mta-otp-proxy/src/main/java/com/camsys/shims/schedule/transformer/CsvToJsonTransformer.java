package com.camsys.shims.schedule.transformer;

import com.amazonaws.services.s3.AmazonS3;
import com.camsys.shims.s3.S3Utils;
import com.csvreader.CsvReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Translate arbitrary CSV to JSON located at a URL.
 *
 */
public class CsvToJsonTransformer<T> {

    private List<T> records = new ArrayList<>();
    private CsvRecordReader<T> csvRecordReader;
    private AmazonS3 s3 = null;
    /**
     * <p>Constructor for CsvToJsonTransformer.</p>
     *
     * @param csvRecordReader a {@link com.camsys.shims.schedule.transformer.CsvRecordReader} object.
     * @param user a {@link java.lang.String} object.
     * @param pass a {@link java.lang.String} object.
     */
    public CsvToJsonTransformer(CsvRecordReader<T> csvRecordReader, String user, String pass) {
        this.csvRecordReader = csvRecordReader;
        s3 = S3Utils.getS3Client(user, pass);
    }

    /**
     * load the contents of the file at URL into memory.
     *
     * @param url a {@link java.lang.String} object.
     */
    public void loadUrl(String url) {
        InputStream input = null;
        if (url.startsWith("s3://")) {
            input = S3Utils.getViaS3(s3, url);
        } else {
            throw new UnsupportedOperationException("protocol in url " + url + " no supported!");
        }

        try {
            records = getCSV(input);
        } catch (IOException ioe) {
            //
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException bury) {

                }
            }
        }
    }

    /**
     * Coerce the input stream into objects per the csvRecordReader
     * @param input
     * @return objects representing the csv
     * @throws IOException
     */
    private List<T> getCSV(InputStream input) throws IOException {
        CsvReader reader = new CsvReader(input, ',', Charset.forName("UTF8"));
        List<T> records = new ArrayList<>();
        reader.readRecord();  // discard header
        while (reader.readRecord()) {
            records.add(csvRecordReader.readRecord(reader));
        }

        return records;
    }

    /**
     * return the internal contents as a list of objects while filtering on the given param.
     *
     * @param filterParamter a {@link java.lang.String} object.
     * @return list of objects
     */
    public List<T> transform(String filterParamter) {
        List<T> filtered = new ArrayList<>(records.size());
        for (T obj : records) {
            if (!csvRecordReader.filter(obj, filterParamter)) {
                filtered.add(obj);
            }
        }
        return filtered;
    }
}
