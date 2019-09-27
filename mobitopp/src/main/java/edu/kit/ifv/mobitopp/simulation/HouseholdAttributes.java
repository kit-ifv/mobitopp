package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.populationsynthesis.EconomicalStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
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
	public final EconomicalStatus economicalStatus;
	public final boolean canChargePrivately;

}
