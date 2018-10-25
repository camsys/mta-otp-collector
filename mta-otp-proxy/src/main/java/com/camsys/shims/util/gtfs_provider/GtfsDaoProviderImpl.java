package com.camsys.shims.util.gtfs_provider;

import org.onebusaway.gtfs.services.GtfsRelationalDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>GtfsDaoProviderImpl class.</p>
 *
 */
public class GtfsDaoProviderImpl implements GtfsDaoProvider {

    private Map<String, GtfsRelationalDao> _registry = new HashMap<>();

    /** {@inheritDoc} */
    @Override
    public GtfsRelationalDao getDaoForAgency(String agency) {
        return _registry.get(agency);
    }

    /**
     * <p>setDaos.</p>
     *
     * @param daos a {@link java.util.List} object.
     */
    public void setDaos(List<GtfsRelationalDao> daos) {
        for (GtfsRelationalDao dao : daos) {
            addDao(dao);
        }
    }

    /**
     * <p>addDao.</p>
     *
     * @param dao a {@link org.onebusaway.gtfs.services.GtfsRelationalDao} object.
     */
    public void addDao(GtfsRelationalDao dao) {
        String agencyId = dao.getAllAgencies().iterator().next().getId();
        _registry.put(agencyId, dao);
    }
}
