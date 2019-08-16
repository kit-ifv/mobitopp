package edu.kit.ifv.mobitopp.simulation;

import java.util.Objects;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;

public class HouseholdAttributes {

	public final int oid;
	public final HouseholdId id;
	public final int nominalSize;
	public final int domCode;
	public final Zone homeZone;
	public final Location homeLocation;
	public final int numberOfMinors;
	public final int numberOfNotSimulatedChildren;
	public final int totalNumberOfCars;
	public final int monthlyIncomeEur;
	public final int incomeClass;
	public final boolean canChargePrivately;

	public HouseholdAttributes(
			int oid, HouseholdId id, int nominalSize, int domCode, Zone homeZone, Location homeLocation,
			int numberOfMinors, int numberOfNotSimulatedChildren, int totalNumberOfCars,
			int monthlyIncomeEur, int incomeClass, boolean canChargePrivately) {
		super();
		this.oid = oid;
		this.id = id;
		this.nominalSize = nominalSize;
		this.domCode = domCode;
		this.homeZone = homeZone;
		this.homeLocation = homeLocation;
		this.numberOfMinors = numberOfMinors;
		this.numberOfNotSimulatedChildren = numberOfNotSimulatedChildren;
		this.totalNumberOfCars = totalNumberOfCars;
		this.monthlyIncomeEur = monthlyIncomeEur;
		this.incomeClass = incomeClass;
		this.canChargePrivately = canChargePrivately;
	}

	@Override
	public int hashCode() {
		return Objects
				.hash(canChargePrivately, domCode, homeLocation, homeZone, id, incomeClass,
						monthlyIncomeEur, nominalSize, numberOfMinors, numberOfNotSimulatedChildren, oid,
						totalNumberOfCars);
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
		return canChargePrivately == other.canChargePrivately && domCode == other.domCode
				&& Objects.equals(homeLocation, other.homeLocation)
				&& Objects.equals(homeZone, other.homeZone) && Objects.equals(id, other.id)
				&& incomeClass == other.incomeClass && monthlyIncomeEur == other.monthlyIncomeEur
				&& nominalSize == other.nominalSize && numberOfMinors == other.numberOfMinors
				&& numberOfNotSimulatedChildren == other.numberOfNotSimulatedChildren && oid == other.oid
				&& totalNumberOfCars == other.totalNumberOfCars;
	}

	@Override
	public String toString() {
		return "HouseholdAttributes [oid=" + oid + ", id=" + id + ", nominalSize=" + nominalSize
				+ ", domCode=" + domCode + ", homeZone=" + homeZone + ", homeLocation=" + homeLocation
				+ ", numberOfMinors=" + numberOfMinors + ", numberOfNotSimulatedChildren="
				+ numberOfNotSimulatedChildren + ", totalNumberOfCars=" + totalNumberOfCars
				+ ", monthlyIncomeEur=" + monthlyIncomeEur + ", incomeClass=" + incomeClass
				+ ", canChargePrivately=" + canChargePrivately + "]";
	}

}
