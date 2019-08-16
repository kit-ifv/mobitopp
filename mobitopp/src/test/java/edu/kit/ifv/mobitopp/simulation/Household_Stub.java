package edu.kit.ifv.mobitopp.simulation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public class Household_Stub
	implements  Household
{

	private final int oid;
	private final int income;
	private final Zone zone;

	public Household_Stub(final int oid, final int income) {
		this.oid = oid;
		this.income = income;
		zone = mock(Zone.class);
		when(zone.getId()).thenReturn(new ZoneId("0", 0));
		when(zone.getName()).thenReturn("0");
	}

	public Household_Stub(final int oid) {
		this(oid, 2000);
	}

	public void addPerson(Person person) {}
	public List<Person> getPersons() { return null; }
	public Person getPerson(PersonId id) { return null; }

	public void ownCars(Collection<PrivateCar> cars) {}

	public PrivateCar takeAvailableCar(Person person, float tripDistanceKm) { return null; }
	public PrivateCar nextAvailableCar(Person person, float tripDistanceKm) { return null; }

	public int getTotalNumberOfCars() { return -1; }
	public int getNumberOfAvailableCars() { return -1; }
	public boolean canChargePrivately() { return false; }

	public int getOid() { return this.oid; }
	public HouseholdId getId() { return null; }

	public Zone homeZone() { return zone; }

	public int getSize() { return -1; }
	public int numberOfNotSimulatedChildren() { return 0; }
	public int getDomCodeType() { return -1; }

	public  Collection<PrivateCar> whichCars() {return null;}
	public void returnCar(PrivateCar car) {};

	public int monthlyIncomeEur() { return this.income; }
	public  Location homeLocation() {return null;}

	public int nominalNumberOfCars() { return -1; }
	public int nominalSize() { return -1; }
	public int domCode() { return -1; }

	public int income() { return -1; }
	
	public int incomeClass() { return -1; }

	@Override
	public HouseholdAttributes attributes() {
		return null;
	}

}
