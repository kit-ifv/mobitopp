package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.visum.VisumChargingFacility;

public class VisumChargingFacilityBuilder {

	private static final int defaultId = 1;
	private static final float defaultX = 1.0f;
	private static final float defaultY = 1.0f;
	private static final String defaultType = "type";
	private static final String defaultVehicleType = "vehicleType";
	private static final String defaultPublicType = "publicType";
	private static final double defaultGeoLat = 1.0d;
	private static final double defaultGeoLong = 1.0d;
	private static final String defaultAddress = "address";

	private int id;
	private float x;
	private float y;
	private String type;
	private String vehicleType;
	private String publicType;
	private double geo_lat;
	private double geo_long;
	private String address;

	public VisumChargingFacilityBuilder() {
		id = defaultId;
		x = defaultX;
		y = defaultY;
		type = defaultType;
		vehicleType = defaultVehicleType;
		publicType = defaultPublicType;
		geo_lat = defaultGeoLat;
		geo_long = defaultGeoLong;
		address = defaultAddress;
	}

	public VisumChargingFacility build() {
		return new VisumChargingFacility(id, x, y, type, vehicleType, publicType, geo_lat, geo_long,
				address);
	}

	public VisumChargingFacilityBuilder withId(int id) {
		this.id = id;
		return this;
	}

}
