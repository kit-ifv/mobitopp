package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;


public class VisumChargingFacility
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	public final int id;
	public final VisumPoint2 coord;
	public final String type;
	public final String vehicleType;
	public final String publicType;

	public final double geo_lat;
	public final double geo_long;
	public final String address;


	public VisumChargingFacility(
		int id,
		float coord_x,
		float coord_y,
		String type,
		String vehicleType,
		String publicType,
		double geo_lat,
		double geo_long,
		String address
	) {
		this.id = id;
		this.coord = new VisumPoint2(coord_x,coord_y);
		this.type = type;
		this.vehicleType = vehicleType;
		this.publicType = publicType;
		this.geo_lat = geo_lat;
		this.geo_long = geo_long;
		this.address = address;
	}

	public String toString() {
		return "(" + id + ": " + coord + ")";
	}



}
