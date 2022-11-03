package com.camsys.shims.schedule.transformer;

import com.camsys.shims.schedule.transformer.model.RouteBranchStop;
import com.csvreader.CsvReader;
import com.google.common.base.CaseFormat;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Map a CSV record to any map
 */
public class CsvMapReader implements CsvRecordReader<Map<String, String>> {

    private String[] _headers;

    private String _filterColumn = "routeId";

    private boolean _useCamelCase = true;

    public void setFilterColumn(String filterColumn) {
        _filterColumn = filterColumn;
    }

    public void setUseCamelCase(boolean camelCase) {
        _useCamelCase = camelCase;
    }

    @Override
    public void readHeaders(String[] headers) {
        _headers = headers;
    }
    /**
     * read a CSV row and return as an object.
     */
    @Override
    public Map<String, String> readRecord(CsvReader reader) throws IOException {
        Map<String, String> map = new HashMap<>();
        for (String header : _headers) {
            String value = reader.get(header);
            String key = _useCamelCase ? CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, header) : header;
            map.put(key, value);
        }
        return map;
    }

    /**
     * filter the object, where filter means return false if filter param == route id
     */
    @Override
    public boolean filter(Map<String, String> map, String filter) {
        if (filter == null) return false;
        String value = map.get(_filterColumn);
        if (filter.equalsIgnoreCase(value)) {
            return false;
        }
        return true;
    }
}
