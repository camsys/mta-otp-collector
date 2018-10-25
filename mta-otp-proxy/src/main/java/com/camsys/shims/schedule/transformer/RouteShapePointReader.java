package com.camsys.shims.schedule.transformer;

import com.camsys.shims.schedule.transformer.model.RouteShapePoint;
import com.csvreader.CsvReader;

import java.io.IOException;

public class RouteShapePointReader implements CsvRecordReader<RouteShapePoint> {
    @Override
    public RouteShapePoint readRecord(CsvReader reader) throws IOException {
        return new RouteShapePoint(reader.get(0), Integer.parseInt(reader.get(1)),
                Double.parseDouble(reader.get(2)), Double.parseDouble(reader.get(3)));
    }

    @Override
    public boolean filter(RouteShapePoint rbs, String filter) {
        if (filter == null) return false;
        if (filter.equalsIgnoreCase(rbs.getRouteId())) return false;
        return true;
    }
}
