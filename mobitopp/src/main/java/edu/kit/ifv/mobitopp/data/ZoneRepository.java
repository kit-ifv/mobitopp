package edu.kit.ifv.mobitopp.data;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.kit.ifv.mobitopp.dataimport.ZonesReader;

public interface ZoneRepository extends ZonesReader {

  // TODO switch to ZoneId?
	public boolean hasZone(int id);
	
	// TODO switch to ZoneId?
	public Zone getZoneByOid(int oid) throws NoSuchElementException;
	
	public Zone getZoneById(ZoneId id);

  public List<ZoneId> getZoneIds();

	public Map<ZoneId, Zone> zones();


}
