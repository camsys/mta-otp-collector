package com.camsys.shims.schedule.transformer;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.S3Object;
import com.camsys.shims.schedule.transformer.model.RouteBranchStop;
import com.csvreader.CsvReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Translate arbitrary CSV to JSON located at a URL.
 */
public class CsvToJsonTransformer {

    private List<Object> records = new ArrayList<>();
    private CsvRecordReader csvRecordReader;
    private AmazonS3 s3 = null;
    public CsvToJsonTransformer(CsvRecordReader csvRecordReader, String user, String pass) {
        this.csvRecordReader = csvRecordReader;
        BasicAWSCredentials credentials = new BasicAWSCredentials(user, pass);
        s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    /**
     * load the contents of the file at URL into memory.
     * @param url
     */
    public void loadUrl(String url) {
        InputStream input = null;
        if (url.startsWith("s3://")) {
            input = getViaS3(url);
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
     * retrieve an input stream representing the contents of the url at the s3 location.
     * @param url
     * @return
     */
    public InputStream getViaS3(String url) {
        AmazonS3URI uri = new AmazonS3URI(url);
        S3Object o = s3.getObject(uri.getBucket(), uri.getKey());

        if (o != null) {
            return o.getObjectContent();
        }
        return null;
    }

    /**
     * Coerce the input stream into objects per the csvRecordReader
     * @param input
     * @return
     * @throws IOException
     */
    private List<Object> getCSV(InputStream input) throws IOException {
        CsvReader reader = new CsvReader(input, ',', Charset.forName("UTF8"));
        List<Object> records = new ArrayList<>();
        reader.readRecord();  // discard header
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
    public List<Object> transform(String filterParamter) {
        List<Object> filtered = new ArrayList<>(records.size());
        for (Object obj : records) {
            if (!csvRecordReader.filter(obj, filterParamter)) {
                filtered.add(obj);
            }
        }
        return filtered;
    }
}
