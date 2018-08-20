package edu.kit.ifv.mobitopp.data.local;

import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility.Type;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingPower;

public class ZoneChargingFacility {

	private final int zoneId;
	private final ChargingFacility facility;

	public ZoneChargingFacility(int zoneId, ChargingFacility facility) {
		super();
		this.zoneId = zoneId;
		this.facility = facility;
	}

	public int zoneId() {
		return zoneId;
	}

	public ChargingFacility facility() {
		return facility;
	}

	public int id() {
		return facility.id();
	}

	public int stationId() {
		return facility.stationId();
	}

	public Location location() {
		return facility.location();
	}

	public Type type() {
		return facility.type();
	}

	public ChargingPower power() {
		return facility.power();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((facility == null) ? 0 : facility.hashCode());
		result = prime * result + zoneId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZoneChargingFacility other = (ZoneChargingFacility) obj;
		if (facility == null) {
			if (other.facility != null)
				return false;
		} else if (!facility.equals(other.facility))
			return false;
		if (zoneId != other.zoneId)
			return false;
		return true;
	}

}
