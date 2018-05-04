package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static java.lang.String.valueOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.ConventionalCarFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PopulationContext;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public class DefaultPrivateCarFormatTest {

	private static final int householdOid = 1;
	private static final int personOid = 2;
	private static final int mainUserOid = personOid;
	private static final int personalUserOid = personOid;
	private static final int zoneOid = 4;
	
	private Household household;
	private Person person;
	private Zone zone;
	private DefaultPrivateCarFormat format;
	private PrivateCar privateConventionalCar;
	private PopulationContext context;
	private ConventionalCar conventionalCar;
	private ConventionalCarFormat conventionalCarFormat;
	
	@Before
	public void initialise() {
		household = mock(Household.class);
		person = mock(Person.class);
		zone = mock(Zone.class);
		conventionalCar = Example.conventionalCar(zone);
		privateConventionalCar = Example.conventionalCar(household, person, zone);
		conventionalCarFormat = mock(ConventionalCarFormat.class);
		context = mock(PopulationContext.class);
		
		when(household.getOid()).thenReturn(householdOid);
		when(person.getOid()).thenReturn(personOid);
		when(zone.getOid()).thenReturn(zoneOid);
		when(context.getHouseholdByOid(householdOid)).thenReturn(household);
		when(context.getPersonByOid(personOid)).thenReturn(person);
		when(conventionalCarFormat.prepare(any())).thenReturn(conventionalCar());
		when(conventionalCarFormat.parse(conventionalCar())).thenReturn(conventionalCar);
		
		format = new DefaultPrivateCarFormat();
		format.register(CarType.conventional, conventionalCarFormat);
	}

	@Test
	public void prepareConventionalCar() {
		List<String> prepared = format.prepare(privateConventionalCar, context);
		
		assertThat(prepared, is(equalTo(privateCar())));
		verify(conventionalCarFormat).prepare(any());
	}
	
	@Test
	public void parseConventionalCar() {
		PrivateCar parsed = format.parse(privateCar(), context);
		
		assertCars(parsed, privateConventionalCar);
		verify(conventionalCarFormat).parse(conventionalCar());
	}

	private void assertCars(PrivateCar actual, PrivateCar original) {
		assertValue(PrivateCar::owner, actual, original);
		assertValue(PrivateCar::mainUser, actual, original);
		assertValue(PrivateCar::personalUser, actual, original);
		assertValue(PrivateCar::id, actual, original);
		assertCarPosition(actual, original);
		assertValue(PrivateCar::carSegment, actual, original);
		assertValue(PrivateCar::capacity, actual, original);
		assertValue(PrivateCar::currentMileage, actual, original);
		assertValue(PrivateCar::currentFuelLevel, actual, original);
		assertValue(PrivateCar::maxRange, actual, original);
	}

	private void assertCarPosition(PrivateCar actual, PrivateCar original) {
		CarPosition actualPosition = actual.position();
		CarPosition originalPosition = original.position();
		assertValue(CarPosition::zone, actualPosition, originalPosition);
		assertValue(CarPosition::location, actualPosition, originalPosition);
	}

	private List<String> privateCar() {
		ArrayList<String> attributes = new ArrayList<>();
		attributes.add(valueOf(householdOid));
		attributes.add(valueOf(mainUserOid));
		attributes.add(valueOf(personalUserOid));
		attributes.add(valueOf(CarType.conventional));
		attributes.addAll(conventionalCar());
		return attributes;
	}
	
	private List<String> conventionalCar() {
		ArrayList<String> attributes = new ArrayList<>();
		attributes.add(valueOf(Example.carId));
		attributes.add(valueOf(zoneOid));
		attributes.add(valueOf(Example.location));
		attributes.add(valueOf(Example.segment));
		attributes.add(valueOf(Example.capacity));
		attributes.add(valueOf(Example.initialMileage));
		attributes.add(valueOf(Example.fuelLevel));
		attributes.add(valueOf(Example.maxRange));
		return attributes;
	}
}
