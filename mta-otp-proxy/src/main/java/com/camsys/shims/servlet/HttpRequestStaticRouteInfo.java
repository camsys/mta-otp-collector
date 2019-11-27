package com.camsys.shims.servlet;

import com.camsys.shims.schedule.transformer.CsvRecordReader;
import com.camsys.shims.schedule.transformer.CsvToJsonTransformer;
import com.camsys.shims.schedule.transformer.GeojsonProvider;
import com.camsys.shims.schedule.transformer.model.ExtendedRouteBranchStop;
import com.camsys.shims.schedule.transformer.model.RouteBranchStop;
import com.camsys.shims.schedule.transformer.model.RouteInfo;
import com.camsys.shims.schedule.transformer.model.RouteShapePoint;
import com.camsys.shims.util.gtfs_provider.GtfsDataServiceProvider;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.services.GtfsDataService;

import javax.servlet.ServletException;
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

    private GtfsDataServiceProvider _provider;

    public void setGtfsProvider(GtfsDataServiceProvider provider) {
        _provider = provider;
    }

    private String _lirrSystemMapUrl;

    public void setLirrSystemMapUrl(String url) {
        _lirrSystemMapUrl = url;
    }

    private CsvToJsonTransformer<RouteBranchStop> _stopTransformer = null;

    private CsvToJsonTransformer<RouteShapePoint> _shapeTransformer = null;

    private GeojsonProvider _geojsonProvider = null;

    @Override
    protected RouteInfo getData(String routeId) {
        if (routeId == null )
            throw new RuntimeException("Supply routeId");
        if (!routeId.contains(":"))
            throw new RuntimeException("routeId format expected of <feedId>:<routeId>");
        // lookup injected source file
        // download and load
        getShapeTransformer().loadUrl(_shapeUrl);
        getStopTransformer().loadUrl(_stopsUrl);
        List<RouteShapePoint> points = getShapeTransformer().transform(routeId);
        List<RouteBranchStop> stopsNoLocation = getStopTransformer().transform(routeId);
        List<ExtendedRouteBranchStop> stops = new ArrayList<>();
        String agency = routeId.split(":")[0];
        GtfsDataService gtfs = _provider.getGtfsDataService(agency);
        for (RouteBranchStop s : stopsNoLocation) {
            ExtendedRouteBranchStop stop = new ExtendedRouteBranchStop(s);
            if (gtfs != null) {
                Stop gtfsStop = gtfs.getStopForId(AgencyAndId.convertFromString(stop.getId(), ':'));
                if (gtfsStop != null) {
                    stop.setLat(gtfsStop.getLat());
                    stop.setLon(gtfsStop.getLon());
                }
            }
            stops.add(stop);
        }
        AgencyAndId aid = AgencyAndId.convertFromString(routeId, ':');
        Route route = gtfs != null ? gtfs.getRouteForId(aid) : null;
        RouteInfo info = new RouteInfo(stops, route);
        if (!points.isEmpty()) {
            info.addGeometry(points);
        }
        if (aid.getAgencyId().equals("LI")) {
            addLirrSystemMap(info);
        }
        return info;
    }

    protected CsvToJsonTransformer<RouteBranchStop> getStopTransformer() {
        if (_stopTransformer == null) {
            _stopTransformer = new CsvToJsonTransformer<>(_stopReader);
        }
        return _stopTransformer;
    }

    protected CsvToJsonTransformer<RouteShapePoint> getShapeTransformer() {
        if (_shapeTransformer == null) {
            _shapeTransformer = new CsvToJsonTransformer<>(_shapeReader);
        }
        return _shapeTransformer;
    }

    private GeojsonProvider getGeojsonProvider() {
        if (_geojsonProvider == null) {
            _geojsonProvider = new GeojsonProvider(_lirrSystemMapUrl);
        }
        return _geojsonProvider;
    }

    private void addLirrSystemMap(RouteInfo info) {
        FeatureCollection collection = getGeojsonProvider().getGeojson();
        for (Feature feature : collection.getFeatures()) {
            info.addGeometry(feature);
        }
    }
}
