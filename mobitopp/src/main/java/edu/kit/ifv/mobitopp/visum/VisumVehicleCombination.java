package edu.kit.ifv.mobitopp.visum;

import java.util.Map;
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

	public String toString() {
		return "VisumVehicleCombination("
					+ id + ", "
					+ code + ", "
					+ name + ", "
					+ getCapacity() + ", "
					+ getNumberOfSeats() 
					+ ")";
	}

	public String asMATSimXML() {


		String xml = ""
			+ "<vehicleType id=\"" + this.id + "\">\n" 
			+ 	"\t<description>" + this.name + "</description>\n"
			+ 	"\t<capacity>\n"
			+ 		"\t\t<seats persons=\"" + getNumberOfSeats() + "\" />\n"
			+ 		"\t\t<standingRoom persons=\"" + (getCapacity()-getNumberOfSeats()) + "\" />\n"
			+ 		"\t\t<length meters=\"" + getLength() + "\" />\n"
			+ 	"\t</capacity>\n"
			+ "</vehicleType>";


		return xml;
	}

}
