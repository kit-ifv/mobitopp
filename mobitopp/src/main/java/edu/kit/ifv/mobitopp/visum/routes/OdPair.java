package edu.kit.ifv.mobitopp.visum.routes;

import java.util.Comparator;
import java.util.Objects;

public class OdPair implements Comparable<OdPair> {

  private static final Comparator<OdPair> comparator = createComparator();
  private final String origin;
  private final String destination;

  public OdPair(String origin, String destination) {
    super();
    this.origin = origin;
    this.destination = destination;
  }

  public String origin() {
    return origin;
  }
  
  public String destination() {
    return destination;
  }

  @Override
  public int hashCode() {
    return Objects.hash(destination, origin);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    OdPair other = (OdPair) obj;
    return Objects.equals(destination, other.destination) && Objects.equals(origin, other.origin);
  }

  @Override
  public String toString() {
    return "OdPair [origin=" + origin + ", destination=" + destination + "]";
  }

  @Override
  public int compareTo(OdPair other) {
    return comparator.compare(this, other);
  }

  private static Comparator<OdPair> createComparator() {
    Comparator<OdPair> origin = Comparator.comparing(OdPair::origin);
    return origin.thenComparing(OdPair::destination);
  }

}
