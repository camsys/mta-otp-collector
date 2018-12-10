package com.camsys.shims.schedule.transformer;

import com.csvreader.CsvReader;
import org.onebusaway.cloud.api.ExternalServices;
import org.onebusaway.cloud.api.ExternalServicesBridgeFactory;

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
    private String _profile;
    private ExternalServices _externalServices = new ExternalServicesBridgeFactory().getExternalServices();

    public CsvToJsonTransformer(CsvRecordReader<T> csvRecordReader, String profile) {
        this.csvRecordReader = csvRecordReader;
        _profile = profile;
    }

    public CsvToJsonTransformer(CsvRecordReader<T> csvRecordReader) {
        this(csvRecordReader, null);
    }

    /**
     * load the contents of the file at URL into memory.
     * @param url
     */
    public void loadUrl(String url) {
        if (url.startsWith("s3://")) {
            _externalServices.getFileAsStream(url, stream -> records = getCSV(stream), _profile);
        } else if (url.startsWith("http://") || url.startsWith("file://")) {
            try {
                URL resource = new URL(url);
                InputStream input = resource.openStream();
                records = getCSV(input);
                input.close();
            } catch (IOException e) {
                throw new UnsupportedOperationException("Bad url: " + url);
            }
        } else {
            throw new UnsupportedOperationException("protocol in url " + url + " no supported!");
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
