package edu.kit.ifv.mobitopp.visum;

@FunctionalInterface
public interface IdToOidMapper {

  /**
   * Maps a given zone id of visum to the oid used in mobiTopp
   * 
   * @param id
   *          of the visum zone
   * @return oid used in mobiTopp
   */
  Integer map(String id);
}
