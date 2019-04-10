package edu.kit.ifv.mobitopp.visum.routes;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ZoneRoute {

  private final List<ZoneIdTime> zones;

  public ZoneRoute(List<ZoneIdTime> zones) {
    super();
    this.zones = zones;
  }
  
  public ZoneRoute(ZoneIdTime... zones) {
    this(asList(zones));
  }

  public List<ZoneIdTime> zones() {
    return Collections.unmodifiableList(zones);
  }

  @Override
  public int hashCode() {
    return Objects.hash(zones);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ZoneRoute other = (ZoneRoute) obj;
    return Objects.equals(zones, other.zones);
  }

  @Override
  public String toString() {
    return zones.stream().map(ZoneIdTime::toString).collect(joining(","));
  }

}
