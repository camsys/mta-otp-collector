package com.camsys.shims.servlet;

import com.camsys.shims.schedule.transformer.CsvRecordReader;
import com.camsys.shims.schedule.transformer.CsvToJsonTransformer;
import com.camsys.shims.schedule.transformer.GeojsonProvider;
import com.camsys.shims.schedule.transformer.model.ExtendedRouteBranchStop;
import com.camsys.shims.schedule.transformer.model.RouteBranchStop;
import com.camsys.shims.schedule.transformer.model.RouteInfo;
import com.camsys.shims.schedule.transformer.model.RouteShapePoint;
import com.camsys.shims.util.gtfs_provider.GtfsDaoProvider;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.services.GtfsRelationalDao;

import java.util.ArrayList;
import java.util.List;

/**
 * get RouteInfo - shapes and points for a route
 *
 */
public class HttpRequestStaticRouteInfo extends AbstractHttpRequestStaticData<RouteInfo> {

    private String _stopsUrl = null;

    /**
     * <p>setStopsUrl.</p>
     *
     * @param url a {@link java.lang.String} object.
     */
    public void setStopsUrl(String url) {
        _stopsUrl = url;
    }

    private String _shapeUrl = null;

    /**
     * <p>setShapeUrl.</p>
     *
     * @param url a {@link java.lang.String} object.
     */
    public void setShapeUrl(String url) {
        _shapeUrl = url;
    }

    private CsvRecordReader<RouteBranchStop> _stopReader = null;

    /**
     * <p>setStopReader.</p>
     *
     * @param reader a {@link com.camsys.shims.schedule.transformer.CsvRecordReader} object.
     */
    public void setStopReader(CsvRecordReader<RouteBranchStop>reader) {
        _stopReader = reader;
    }

    private CsvRecordReader<RouteShapePoint> _shapeReader = null;

    /**
     * <p>setShapeReader.</p>
     *
     * @param reader a {@link com.camsys.shims.schedule.transformer.CsvRecordReader} object.
     */
    public void setShapeReader(CsvRecordReader<RouteShapePoint>reader) {
        _shapeReader = reader;
    }

    private GtfsDaoProvider _provider;

    /**
     * <p>setGtfsProvider.</p>
     *
     * @param provider a {@link com.camsys.shims.util.gtfs_provider.GtfsDaoProvider} object.
     */
    public void setGtfsProvider(GtfsDaoProvider provider) {
        _provider = provider;
    }

    private String _lirrSystemMapUrl;

    /**
     * <p>setLirrSystemMapUrl.</p>
     *
     * @param url a {@link java.lang.String} object.
     */
    public void setLirrSystemMapUrl(String url) {
        _lirrSystemMapUrl = url;
    }

    private CsvToJsonTransformer<RouteBranchStop> _stopTransformer = null;

    private CsvToJsonTransformer<RouteShapePoint> _shapeTransformer = null;

    private GeojsonProvider _geojsonProvider = null;

    /** {@inheritDoc} */
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
        AgencyAndId aid = AgencyAndId.convertFromString(routeId, ':');
        Route route = dao != null ? dao.getRouteForId(aid) : null;
        RouteInfo info = new RouteInfo(stops, route);
        if (!points.isEmpty()) {
            info.addGeometry(points);
        }
        if (aid.getAgencyId().equals("LI")) {
            addLirrSystemMap(info);
        }
        return info;
    }

    /**
     * <p>getStopTransformer.</p>
     *
     * @return a {@link com.camsys.shims.schedule.transformer.CsvToJsonTransformer} object.
     */
    protected CsvToJsonTransformer<RouteBranchStop> getStopTransformer() {
        if (_stopTransformer == null) {
            _stopTransformer = new CsvToJsonTransformer<>(_stopReader, s3key, s3pass);
        }
        return _stopTransformer;
    }

    /**
     * <p>getShapeTransformer.</p>
     *
     * @return a {@link com.camsys.shims.schedule.transformer.CsvToJsonTransformer} object.
     */
    protected CsvToJsonTransformer<RouteShapePoint> getShapeTransformer() {
        if (_shapeTransformer == null) {
            _shapeTransformer = new CsvToJsonTransformer<>(_shapeReader, s3key, s3pass);
        }
        return _shapeTransformer;
    }

    private GeojsonProvider getGeojsonProvider() {
        if (_geojsonProvider == null) {
            _geojsonProvider = new GeojsonProvider(_lirrSystemMapUrl, s3key, s3pass);
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
