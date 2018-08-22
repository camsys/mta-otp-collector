package com.camsys.shims.servlet;

import com.camsys.shims.schedule.transformer.CsvRecordReader;
import com.camsys.shims.schedule.transformer.CsvToJsonTransformer;
import com.camsys.shims.schedule.transformer.model.ExtendedRouteBranchStop;
import com.camsys.shims.schedule.transformer.model.RouteBranchStop;
import com.camsys.shims.schedule.transformer.model.RouteInfo;
import com.camsys.shims.schedule.transformer.model.RouteShapePoint;
import com.camsys.shims.util.gtfs_provider.GtfsDaoProvider;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.services.GtfsRelationalDao;

import java.util.ArrayList;
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

    private GtfsDaoProvider _provider;

    public void setGtfsProvider(GtfsDaoProvider provider) {
        _provider = provider;
    }

    @Override
    protected RouteInfo getData(String routeId) {
        // lookup injected source file
        // download and load
        getShapeTransformer().loadUrl(_shapeUrl);
        getStopTransformer().loadUrl(_stopsUrl);
        List<RouteShapePoint> points = getShapeTransformer().transform(routeId);
        List<RouteBranchStop> stopsNoLocation = getStopTransformer().transform(routeId);
        List<ExtendedRouteBranchStop> stops = new ArrayList<>();
        String agency = routeId.split(":")[0];
        GtfsRelationalDao dao = _provider.getDaoForAgency(agency);
        for (RouteBranchStop s : stopsNoLocation) {
            ExtendedRouteBranchStop stop = new ExtendedRouteBranchStop(s);
            if (dao != null) {
                Stop gtfsStop = dao.getStopForId(AgencyAndId.convertFromString(stop.getId(), ':'));
                if (gtfsStop != null) {
                    stop.setLat(gtfsStop.getLat());
                    stop.setLon(gtfsStop.getLon());
                }
            }
            stops.add(stop);
        }
        Route route = dao != null ? dao.getRouteForId(AgencyAndId.convertFromString(routeId, ':')) : null;
        return new RouteInfo(stops, points, route);
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
