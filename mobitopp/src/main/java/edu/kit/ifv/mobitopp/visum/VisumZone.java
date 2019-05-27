package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

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

	@Override
  public int hashCode() {
    return Objects
        .hash(areaId, carsharingcarDensities, chargingFacilities, coord, freeFloatingCarSharingArea,
            freeFloatingCarSharingCars, freeFloatingCarSharingCompany, id, innerZonePublicTransport,
            mainZoneId, name, parkingPlaces, privateChargingProbability, type);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VisumZone other = (VisumZone) obj;
    return areaId == other.areaId
        && Objects.equals(carsharingcarDensities, other.carsharingcarDensities)
        && chargingFacilities == other.chargingFacilities && Objects.equals(coord, other.coord)
        && freeFloatingCarSharingArea == other.freeFloatingCarSharingArea
        && freeFloatingCarSharingCars == other.freeFloatingCarSharingCars
        && Objects.equals(freeFloatingCarSharingCompany, other.freeFloatingCarSharingCompany)
        && id == other.id
        && Float.floatToIntBits(innerZonePublicTransport) == Float
            .floatToIntBits(other.innerZonePublicTransport)
        && mainZoneId == other.mainZoneId && Objects.equals(name, other.name)
        && parkingPlaces == other.parkingPlaces
        && Double.doubleToLongBits(privateChargingProbability) == Double
            .doubleToLongBits(other.privateChargingProbability)
        && type == other.type;
  }

  @Override
  public String toString() {
    return "VisumZone [id=" + id + ", name=" + name + ", mainZoneId=" + mainZoneId + ", type="
        + type + ", parkingPlaces=" + parkingPlaces + ", coord=" + coord + ", areaId=" + areaId
        + ", chargingFacilities=" + chargingFacilities + ", freeFloatingCarSharingCompany="
        + freeFloatingCarSharingCompany + ", freeFloatingCarSharingArea="
        + freeFloatingCarSharingArea + ", freeFloatingCarSharingCars=" + freeFloatingCarSharingCars
        + ", carsharingcarDensities=" + carsharingcarDensities + ", privateChargingProbability="
        + privateChargingProbability + ", innerZonePublicTransport=" + innerZonePublicTransport
        + "]";
  }

}
