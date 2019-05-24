package edu.kit.ifv.mobitopp.visum;

import java.util.Map;
import java.util.Objects;
import java.io.Serializable;
import java.util.Collections;

@SuppressWarnings("serial")
public class VisumVehicleCombination 
	implements Serializable
{

	public final int id;
	public final String code;
	public final String name;

	public final Map<VisumVehicleUnit,Integer> vehicles;


	public VisumVehicleCombination(
		int id,
		String code,
		String name,
		Map<VisumVehicleUnit,Integer> vehicles
	) {

		this.id = id;
		this.code = code;
		this.name = name;

		this.vehicles = Collections.unmodifiableMap(vehicles);
	}


	public int getCapacity() {

		int total = 0;

		for (VisumVehicleUnit unit : vehicles.keySet()) {

			Integer quantity = vehicles.get(unit);
			total += quantity * unit.capacity;
		}

		return total;
	}

	public int getNumberOfSeats() {
		int total = 0;

		for (VisumVehicleUnit unit : vehicles.keySet()) {

			Integer quantity = vehicles.get(unit);
			total += quantity * unit.seats;
		}

		return total;
	}

	public float getLength() {
		float total = 0;

		for (VisumVehicleUnit unit : vehicles.keySet()) {

			Integer quantity = vehicles.get(unit);
			total += quantity * unit.getLength();
		}

		return total;
	}

  @Override
  public int hashCode() {
    return Objects.hash(code, id, name, vehicles);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VisumVehicleCombination other = (VisumVehicleCombination) obj;
    return Objects.equals(code, other.code) && id == other.id && Objects.equals(name, other.name)
        && Objects.equals(vehicles, other.vehicles);
  }

  @Override
  public String toString() {
    return "VisumVehicleCombination [id=" + id + ", code=" + code + ", name=" + name + ", vehicles="
        + vehicles + "]";
  }

}
