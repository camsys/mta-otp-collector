package com.camsys.shims.servlet;

import com.camsys.shims.schedule.transformer.CsvRecordReader;
import com.camsys.shims.schedule.transformer.CsvToJsonTransformer;
import com.camsys.shims.schedule.transformer.model.RouteBranchStop;
import com.camsys.shims.schedule.transformer.model.RouteInfo;
import com.camsys.shims.schedule.transformer.model.RouteShapePoint;
import org.onebusaway.gtfs.model.ShapePoint;

import java.util.List;

/**
 * get RouteInfo - shapes and points for a route
 */
public class HttpRequestStaticRouteInfo extends AbstractHttpRequestStaticData<RouteInfo> {

    private String _stopsUrl = null;

    public void setStopsUrl(String url) {
        _stopsUrl = url;
    }

    private String _shapeUrl = null;

    public void setShapeUrl(String url) {
        _shapeUrl = url;
    }

    private CsvRecordReader<RouteBranchStop> _stopReader = null;

    public void setStopReader(CsvRecordReader<RouteBranchStop>reader) {
        _stopReader = reader;
    }

    private CsvRecordReader<RouteShapePoint> _shapeReader = null;

    public void setShapeReader(CsvRecordReader<RouteShapePoint>reader) {
        _shapeReader = reader;
    }

    private CsvToJsonTransformer<RouteBranchStop> _stopTransformer = null;

    private CsvToJsonTransformer<RouteShapePoint> _shapeTransformer = null;

    @Override
    protected RouteInfo getData(String routeId) {
        // lookup injected source file
        // download and load
        getShapeTransformer().loadUrl(_shapeUrl);
        getStopTransformer().loadUrl(_stopsUrl);
        List<RouteBranchStop> stops = getStopTransformer().transform(routeId);
        List<RouteShapePoint> points = getShapeTransformer().transform(routeId);
        return new RouteInfo(stops, points);
    }

    protected CsvToJsonTransformer<RouteBranchStop> getStopTransformer() {
        if (_stopTransformer == null) {
            _stopTransformer = new CsvToJsonTransformer<>(_stopReader, s3key, s3pass);
        }
        return _stopTransformer;
    }

    protected CsvToJsonTransformer<RouteShapePoint> getShapeTransformer() {
        if (_shapeTransformer == null) {
            _shapeTransformer = new CsvToJsonTransformer<>(_shapeReader, s3key, s3pass);
        }
        return _shapeTransformer;
    }

}
