package edu.kit.ifv.mobitopp.visum;

import java.util.Map;

import edu.kit.ifv.mobitopp.data.ZoneId;

public interface IdToOidMapper {

  /**
   * Maps a given zone id of visum to the oid used in mobiTopp
   * 
   * @param id
   *          of the visum zone
   * @return oid used in mobiTopp
   */
  Integer map(String id);
  
  /**
   * Maps a given zone id of visum to the {@link ZoneId} used in mobiTopp
   * 
   * @param id
   *          of the visum zone
   * @return {@link ZoneId} used in mobiTopp
   */
  ZoneId mapToZoneId(String id);

	static IdToOidMapper createFrom(Map<String, Integer> map) {
		return new MapBasedMapper(map);
	}
}
