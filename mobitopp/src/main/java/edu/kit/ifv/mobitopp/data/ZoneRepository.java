package edu.kit.ifv.mobitopp.data;

import java.util.Map;
import java.util.NoSuchElementException;

import edu.kit.ifv.mobitopp.dataimport.ZonesReader;
import edu.kit.ifv.mobitopp.visum.IdToOidMapper;

public interface ZoneRepository extends ZonesReader, IdToOidMapper {

	public boolean hasZone(int oid);
	
	public Zone getZoneByOid(int oid) throws NoSuchElementException;

	public Map<Integer, Zone> zones();

}
