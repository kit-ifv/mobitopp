package edu.kit.ifv.mobitopp.data;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.kit.ifv.mobitopp.dataimport.ZonesReader;
import edu.kit.ifv.mobitopp.visum.IdToOidMapper;

public interface ZoneRepository extends ZonesReader {

  // TODO switch to ZoneId?
  public boolean hasZone(int id);

  // TODO switch to ZoneId?
  public Zone getZoneByOid(int oid) throws NoSuchElementException;

  public Zone getZoneById(ZoneId id);

  public Zone getByExternalId(String externalId);

  public List<ZoneId> getZoneIds();

  public Map<ZoneId, Zone> zones();

	public IdToOidMapper idMapper();

	/**
	 * Resolves an external zone id to a {@link ZoneId} instance.
	 */
	public ZoneId getId(String externalId);

}
