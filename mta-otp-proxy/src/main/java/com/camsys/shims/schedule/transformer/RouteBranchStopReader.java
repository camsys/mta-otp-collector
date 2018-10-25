package com.camsys.shims.schedule.transformer;

import com.camsys.shims.schedule.transformer.model.RouteBranchStop;
import com.csvreader.CsvReader;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * Map a CSV record to a model that represents it.
 *
 */
public class RouteBranchStopReader implements CsvRecordReader<RouteBranchStop> {


    /**
     * {@inheritDoc}
     *
     * read a CSV row and return as an object.
     */
    @Override
    public RouteBranchStop readRecord(CsvReader reader) throws IOException {
        if (reader.getColumnCount() > 4 && StringUtils.isNotBlank(reader.get(4)))
            return new RouteBranchStop(reader.get(0), reader.get(1), reader.get(2), reader.get(3), reader.get(4));
        return new RouteBranchStop(reader.get(0), reader.get(1), reader.get(2), reader.get(3));
    }

    /**
     * {@inheritDoc}
     *
     * filter the object, where filter means return false if filter param == route id
     */
    @Override
    public boolean filter(RouteBranchStop rbs, String filter) {
        if (filter == null) return false;
        if (filter.equalsIgnoreCase(rbs.getRouteId())) return false;
        return true;
    }
}
