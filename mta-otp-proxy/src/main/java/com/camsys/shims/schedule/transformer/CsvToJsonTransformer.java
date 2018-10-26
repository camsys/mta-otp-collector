package com.camsys.shims.schedule.transformer;

import com.amazonaws.services.s3.AmazonS3;
import com.camsys.shims.s3.S3Utils;
import com.csvreader.CsvReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Translate arbitrary CSV to JSON located at a URL.
 */
public class CsvToJsonTransformer<T> {

    private List<T> records = new ArrayList<>();
    private CsvRecordReader<T> csvRecordReader;
    private AmazonS3 s3 = null;
    public CsvToJsonTransformer(CsvRecordReader<T> csvRecordReader, String user, String pass) {
        this.csvRecordReader = csvRecordReader;
        if (user != null && pass != null){
            s3 = S3Utils.getS3Client(user, pass);
        }
    }

    /**
     * load the contents of the file at URL into memory.
     * @param url
     */
    public void loadUrl(String url) {
        InputStream input = null;
        if (url.startsWith("s3://")) {
            input = S3Utils.getViaS3(s3, url);
        } else if (url.startsWith("http://") || url.startsWith("file://")) {
            try {
                URL resource = new URL(url);
                input = resource.openStream();
            } catch (IOException e) {
                throw new UnsupportedOperationException("Bad url: " + url);
            }
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
     * @return
     * @throws IOException
     */
    private List<T> getCSV(InputStream input) throws IOException {
        CsvReader reader = new CsvReader(input, ',', Charset.forName("UTF8"));
        List<T> records = new ArrayList<>();
        reader.readHeaders();
        csvRecordReader.readHeaders(reader.getHeaders());
        while (reader.readRecord()) {
            records.add(csvRecordReader.readRecord(reader));
        }

        return records;
    }

    /**
     * return the internal contents as a list of objects while filtering on the given param.
     * @param filterParamter
     * @return
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
