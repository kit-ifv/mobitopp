package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;

public class HouseholdAttributes {

	public final int oid;
	public final HouseholdId id;
	public final int nominalSize;
	public final int domCode;
	public final Zone homeZone;
	public final Location homeLocation;
	public final int numberOfNotSimulatedChildren;
	public final int totalNumberOfCars;
	public final int monthlyIncomeEur;
	public final boolean canChargePrivately;

	public HouseholdAttributes(
			int oid, HouseholdId id, int nominalSize, int domCode, Zone homeZone, Location homeLocation,
			int numberOfNotSimulatedChildren, int totalNumberOfCars, int monthlyIncomeEur,
			boolean canChargePrivately) {
		super();
		this.oid = oid;
		this.id = id;
		this.nominalSize = nominalSize;
		this.domCode = domCode;
		this.homeZone = homeZone;
		this.homeLocation = homeLocation;
		this.numberOfNotSimulatedChildren = numberOfNotSimulatedChildren;
		this.totalNumberOfCars = totalNumberOfCars;
		this.monthlyIncomeEur = monthlyIncomeEur;
		this.canChargePrivately = canChargePrivately;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (canChargePrivately ? 1231 : 1237);
		result = prime * result + domCode;
		result = prime * result + ((homeLocation == null) ? 0 : homeLocation.hashCode());
		result = prime * result + ((homeZone == null) ? 0 : homeZone.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + monthlyIncomeEur;
		result = prime * result + nominalSize;
		result = prime * result + numberOfNotSimulatedChildren;
		result = prime * result + oid;
		result = prime * result + totalNumberOfCars;
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
		HouseholdAttributes other = (HouseholdAttributes) obj;
		if (canChargePrivately != other.canChargePrivately)
			return false;
		if (domCode != other.domCode)
			return false;
		if (homeLocation == null) {
			if (other.homeLocation != null)
				return false;
		} else if (!homeLocation.equals(other.homeLocation))
			return false;
		if (homeZone == null) {
			if (other.homeZone != null)
				return false;
		} else if (!homeZone.equals(other.homeZone))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (monthlyIncomeEur != other.monthlyIncomeEur)
			return false;
		if (nominalSize != other.nominalSize)
			return false;
		if (numberOfNotSimulatedChildren != other.numberOfNotSimulatedChildren)
			return false;
		if (oid != other.oid)
			return false;
		if (totalNumberOfCars != other.totalNumberOfCars)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HouseholdAttributes [oid=" + oid + ", id=" + id + ", nominalSize=" + nominalSize
				+ ", domCode=" + domCode + ", homeZone=" + homeZone + ", homeLocation=" + homeLocation
				+ ", numberOfNotSimulatedChildren=" + numberOfNotSimulatedChildren + ", totalNumberOfCars="
				+ totalNumberOfCars + ", monthlyIncomeEur=" + monthlyIncomeEur + ", canChargePrivately="
				+ canChargePrivately + "]";
	}

}
