package edu.kit.ifv.mobitopp.data;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public interface ZoneRepository {

	public Zone getZoneByOid(int oid) throws NoSuchElementException;

	public List<Zone> getZones();

	public Map<Integer, Zone> zones();

}
