package edu.kit.ifv.mobitopp.visum;

@FunctionalInterface
public interface OidToIdMapper {

  /**
   * Maps a given oid used in mobiTopp to the corresponding zone id used in visum
   * 
   * @param oid
   *          oid used in mobiTopp
   * @return zone id used by visum
   */
  String map(int oid);
}
