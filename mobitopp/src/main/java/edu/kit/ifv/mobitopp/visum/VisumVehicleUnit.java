package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("serial")
public class VisumVehicleUnit implements Serializable {

  public final int id;

  public final String code;
  public final String name;

  public final VisumTransportSystemSet transportSystems;

  public final int capacity;
  public final int seats;

  public VisumVehicleUnit(
      int id, String code, String name, VisumTransportSystemSet transportSystems, int capacity,
      int seats) {
    this.id = id;
    this.code = code;
    this.name = name;

    this.capacity = capacity;
    this.seats = seats;

    this.transportSystems = transportSystems;
  }

  public float getLength() {
    return 50.0f;
  }

  @Override
  public int hashCode() {
    return Objects.hash(capacity, code, id, name, seats, transportSystems);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VisumVehicleUnit other = (VisumVehicleUnit) obj;
    return capacity == other.capacity && Objects.equals(code, other.code) && id == other.id
        && Objects.equals(name, other.name) && seats == other.seats
        && Objects.equals(transportSystems, other.transportSystems);
  }

  @Override
  public String toString() {
    return "VisumVehicleUnit [id=" + id + ", code=" + code + ", name=" + name
        + ", transportSystems=" + transportSystems + ", capacity=" + capacity + ", seats=" + seats
        + "]";
  }
}
