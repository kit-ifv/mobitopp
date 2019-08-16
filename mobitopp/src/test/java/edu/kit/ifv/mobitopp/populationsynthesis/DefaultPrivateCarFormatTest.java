package edu.kit.ifv.mobitopp.populationsynthesis;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
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
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.ConventionalCarFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PopulationContext;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;

public class DefaultPrivateCarFormatTest {

  private static final int householdOid = 1;
  private static final int mainUserOid = 2;
  private static final int personalUserOid = 3;
  private static final int zoneOid = 4;
  private static final ZoneId zoneId = new ZoneId("4", zoneOid);
  private static final short year = 2000;

  private HouseholdId householdId;
  private HouseholdForSetup household;
  private PersonBuilder mainUser;
  private PersonBuilder personalUser;
  private Zone zone;
  private DefaultPrivateCarFormat format;
  private PrivateCarForSetup privateConventionalCar;
  private PopulationContext context;
  private ConventionalCar conventionalCar;
  private ConventionalCarFormat conventionalCarFormat;

  @Before
  public void initialise() {
    householdId = new HouseholdId(householdOid, year, householdOid);
    household = mock(HouseholdForSetup.class);
    mainUser = mock(PersonBuilder.class);
    personalUser = mock(PersonBuilder.class);
    zone = mock(Zone.class);
    conventionalCar = ExampleSetup.conventionalCar(zone);
    conventionalCarFormat = mock(ConventionalCarFormat.class);
    context = mock(PopulationContext.class);
    PersonId mainUserId = new PersonId(mainUserOid, householdId, mainUserOid);
    PersonId personalUserId = new PersonId(personalUserOid, householdId, personalUserOid);

    when(household.getId()).thenReturn(householdId);
    when(mainUser.getId()).thenReturn(mainUserId);
    when(personalUser.getId()).thenReturn(personalUserId);
    when(zone.getId()).thenReturn(zoneId);
    when(context.getPersonBuilderByOid(mainUserOid)).thenReturn(Optional.of(mainUser));
    when(context.getPersonBuilderByOid(personalUserOid)).thenReturn(Optional.of(personalUser));
    when(conventionalCarFormat.prepare(any())).thenReturn(conventionalCar());
    when(conventionalCarFormat.parse(conventionalCar())).thenReturn(Optional.of(conventionalCar));
    
		privateConventionalCar = ExampleSetup.conventionalCar(household, mainUser, personalUser, zone);
    format = new DefaultPrivateCarFormat();
    format.register(CarType.conventional, conventionalCarFormat);
  }

  @Test
  public void parseCarForMissingHousehold() {
    prepareMissingHousehold();

    Optional<PrivateCarForSetup> parsed = format.parse(privateCar(), context);

    assertThat(parsed, isEmpty());
    verify(context).getHouseholdForSetupByOid(householdOid);
  }

  private void prepareMissingHousehold() {
    when(context.getHouseholdForSetupByOid(householdOid)).thenReturn(Optional.empty());
  }

  private void prepareExistingHousehold() {
    when(context.getHouseholdForSetupByOid(householdOid)).thenReturn(Optional.of(household));
  }

  @Test
  public void parseCarWithMissingPersonalUser() {
    prepareExistingHousehold();
    prepareExistingMainUser();
    prepareMissingPersonalUser();
    PrivateCarForSetup carMissingPersonalUser = ExampleSetup
        .conventionalCar(household, mainUser, null, zone);

    Optional<PrivateCarForSetup> parsed = format.parse(privateCar(), context);

    assertCars(parsed.get(), carMissingPersonalUser);
  }

  private void prepareExistingMainUser() {
    when(context.getPersonBuilderByOid(mainUserOid)).thenReturn(Optional.of(mainUser));
  }

  private void prepareMissingPersonalUser() {
    when(context.getPersonBuilderByOid(personalUserOid)).thenReturn(Optional.empty());
  }

  @Test
  public void prepareConventionalCar() {
    List<String> prepared = format.prepare(privateConventionalCar);

    assertThat(prepared, is(equalTo(privateCar())));
    verify(conventionalCarFormat).prepare(any());
  }

  @Test
  public void parseConventionalCar() {
    prepareExistingHousehold();

    Optional<PrivateCarForSetup> parsed = format.parse(privateCar(), context);

    assertCars(parsed.get(), privateConventionalCar);
    verify(conventionalCarFormat).parse(conventionalCar());
  }

  private void assertCars(PrivateCarForSetup actual, PrivateCarForSetup original) {
    assertValue(PrivateCarForSetup::getOwner, actual, original);
    assertValue(PrivateCarForSetup::getMainUser, actual, original);
    assertValue(PrivateCarForSetup::getPersonalUser, actual, original);
    assertCarType(actual.getCar(), original.getCar());
  }

	private void assertCarType(Car actual, Car expected) {
		assertValue(Car::id, actual, expected);
		assertValue(Car::carSegment, actual, expected);
    assertValue(Car::capacity, actual, expected);
    assertValue(Car::currentMileage, actual, expected);
    assertValue(Car::currentFuelLevel, actual, expected);
    assertValue(Car::maxRange, actual, expected);
    CarPosition actualPosition = actual.position();
    CarPosition expectedPosition = expected.position();
    assertValue(CarPosition::zone, actualPosition, expectedPosition);
    assertValue(CarPosition::location, actualPosition, expectedPosition);
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
    attributes.add(valueOf(ExampleSetup.carId));
    attributes.add(valueOf(zoneOid));
    attributes.add(valueOf(ExampleSetup.location));
    attributes.add(valueOf(ExampleSetup.segment));
    attributes.add(valueOf(ExampleSetup.capacity));
    attributes.add(valueOf(ExampleSetup.initialMileage));
    attributes.add(valueOf(ExampleSetup.fuelLevel));
    attributes.add(valueOf(ExampleSetup.maxRange));
    return attributes;
  }
}
