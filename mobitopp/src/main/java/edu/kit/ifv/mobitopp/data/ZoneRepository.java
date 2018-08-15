package edu.kit.ifv.mobitopp.data;

import java.util.Map;
import java.util.NoSuchElementException;

import edu.kit.ifv.mobitopp.dataimport.ZonesReader;

public interface ZoneRepository extends ZonesReader {

	public boolean hasZone(int oid);
	
	public Zone getZoneByOid(int oid) throws NoSuchElementException;

	public Map<Integer, Zone> zones();


}
