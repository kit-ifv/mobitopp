package edu.kit.ifv.mobitopp.simulation.carsharing;

import java.io.Serializable;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Location;

public class CarSharingStation implements Serializable {

	private static final long serialVersionUID = 1L;

	public final StationBasedCarSharingOrganization carSharingCompany;
	public final Zone zone;
	public final Integer id;
	public final String name;
	public final String parkingSpace;
	public final Location location;
	public final Integer numberOfCars;

	public CarSharingStation(
		StationBasedCarSharingOrganization carSharingCompany,
		Zone zone,
		Integer id,
		String name,
		String parkingSpace,
		Location location, 
		Integer numberOfCars
	) {
		assert numberOfCars != null : ("numberofcars=" + numberOfCars);

		this.carSharingCompany=carSharingCompany;
		this.zone=zone;
		this.id=id;
		this.name=name;
		this.parkingSpace=parkingSpace;
		this.location=location;
		this.numberOfCars=numberOfCars;

		carSharingCompany.addStation(this);
	}

}
