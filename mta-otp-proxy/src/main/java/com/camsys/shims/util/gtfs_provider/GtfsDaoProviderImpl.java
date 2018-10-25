package com.camsys.shims.util.gtfs_provider;

import org.onebusaway.gtfs.services.GtfsRelationalDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GtfsDaoProviderImpl implements GtfsDaoProvider {

    private Map<String, GtfsRelationalDao> _registry = new HashMap<>();

    @Override
    public GtfsRelationalDao getDaoForAgency(String agency) {
        return _registry.get(agency);
    }

    public void setDaos(List<GtfsRelationalDao> daos) {
        for (GtfsRelationalDao dao : daos) {
            addDao(dao);
        }
    }

    public void addDao(GtfsRelationalDao dao) {
        String agencyId = dao.getAllAgencies().iterator().next().getId();
        _registry.put(agencyId, dao);
    }
}
