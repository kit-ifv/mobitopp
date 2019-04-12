package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class VisumZone 
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	public final int id;
	public final String name;
	public final int mainZoneId;
	public final int type;
	public final int parkingPlaces;
	public final VisumPoint2 coord;
	public final int areaId;

	public final int chargingFacilities;

	public final String freeFloatingCarSharingCompany;
	public final int freeFloatingCarSharingArea;
	public final int freeFloatingCarSharingCars;

	public final Map<String, Float> carsharingcarDensities;
	public final double privateChargingProbability;
	public final float innerZonePublicTransport;


	public VisumZone(
		int id,
		String name,
		int mainZoneId,
		int type,
		int parkingPlaces,
		float coord_x,
		float coord_y,
		int areaId,
		int chargingFacilities,
		String freeFloatingCarSharingCompany,
		int freeFloatingCarSharingArea,
		int freeFloatingCarSharingCars, 
		double privateChargingProbability,
		Map<String, Float> carsharingcarDensities, 
		float innerZonePublicTransport
	) {
		assert freeFloatingCarSharingArea == 0 || freeFloatingCarSharingArea == 1; 
		assert freeFloatingCarSharingCars >= 0;

		this.id = id;
		this.name = name;
		this.mainZoneId = mainZoneId;
		this.type = type;
		this.parkingPlaces = parkingPlaces;
		this.coord = new VisumPoint2(coord_x,coord_y);
		this.areaId = areaId;
		this.chargingFacilities = chargingFacilities;
		this.freeFloatingCarSharingCompany = freeFloatingCarSharingCompany;
		this.freeFloatingCarSharingArea = freeFloatingCarSharingArea;
		this.freeFloatingCarSharingCars = freeFloatingCarSharingCars;
		this.privateChargingProbability = privateChargingProbability;
		this.innerZonePublicTransport = innerZonePublicTransport;

		this.carsharingcarDensities = Collections.unmodifiableMap(carsharingcarDensities);
	}

	public String toString() {

		return "VisumZone(" + id + "," 
												+ name + "," 
												+ mainZoneId + "," 
												+ type + "," 
												+ coord + "," 
												+ areaId + ")";
	}



}
