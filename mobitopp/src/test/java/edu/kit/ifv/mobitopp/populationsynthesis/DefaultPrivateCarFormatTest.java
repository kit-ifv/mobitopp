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
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public class DefaultPrivateCarFormatTest {

  private static final int householdOid = 1;
  private static final int mainUserOid = 2;
  private static final int personalUserOid = 3;
  private static final int zoneOid = 4;
  private static final ZoneId zoneId = new ZoneId("4", zoneOid);
  private static final short year = 2000;

  private HouseholdId householdId;
  private Household household;
  private Person mainUser;
  private Person personalUser;
  private Zone zone;
  private DefaultPrivateCarFormat format;
  private PrivateCar privateConventionalCar;
  private PopulationContext context;
  private ConventionalCar conventionalCar;
  private ConventionalCarFormat conventionalCarFormat;

  @Before
  public void initialise() {
    householdId = new HouseholdId(householdOid, year, householdOid);
    household = mock(Household.class);
    mainUser = mock(Person.class);
    personalUser = mock(Person.class);
    zone = mock(Zone.class);
    conventionalCar = ExampleSetup.conventionalCar(zone);
    conventionalCarFormat = mock(ConventionalCarFormat.class);
    context = mock(PopulationContext.class);
    PersonId mainUserId = new PersonId(mainUserOid, householdId, mainUserOid);
    PersonId personalUserId = new PersonId(personalUserOid, householdId, personalUserOid);

    when(household.getId()).thenReturn(householdId);
    when(household.getOid()).thenReturn(householdOid);
    when(mainUser.getOid()).thenReturn(mainUserOid);
    when(mainUser.getId()).thenReturn(mainUserId);
    when(personalUser.getOid()).thenReturn(personalUserOid);
    when(personalUser.getId()).thenReturn(personalUserId);
    when(zone.getId()).thenReturn(zoneId);
    when(context.getPersonByOid(mainUserOid)).thenReturn(Optional.of(mainUser));
    when(context.getPersonByOid(personalUserOid)).thenReturn(Optional.of(personalUser));
    when(conventionalCarFormat.prepare(any())).thenReturn(conventionalCar());
    when(conventionalCarFormat.parse(conventionalCar())).thenReturn(Optional.of(conventionalCar));
    
    privateConventionalCar = ExampleSetup.conventionalCar(household, mainUser, personalUser, zone);
    format = new DefaultPrivateCarFormat();
    format.register(CarType.conventional, conventionalCarFormat);
  }

  @Test
  public void parseCarForMissingHousehold() {
    prepareMissingHousehold();

    Optional<PrivateCar> parsed = format.parse(privateCar(), context);

    assertThat(parsed, isEmpty());
    verify(context).getHouseholdByOid(householdOid);
  }

  private void prepareMissingHousehold() {
    when(context.getHouseholdByOid(householdOid)).thenReturn(Optional.empty());
  }

  private void prepareExistingHousehold() {
    when(context.getHouseholdByOid(householdOid)).thenReturn(Optional.of(household));
  }

  @Test
  public void parseCarWithMissingPersonalUser() {
    prepareExistingHousehold();
    prepareExistingMainUser();
    prepareMissingPersonalUser();
    PrivateCar carMissingPersonalUser = ExampleSetup
        .conventionalCar(household, mainUser, null, zone);

    Optional<PrivateCar> parsed = format.parse(privateCar(), context);

    assertCars(parsed.get(), carMissingPersonalUser);
  }

  private void prepareExistingMainUser() {
    when(context.getPersonByOid(mainUserOid)).thenReturn(Optional.of(mainUser));
  }

  private void prepareMissingPersonalUser() {
    when(context.getPersonByOid(personalUserOid)).thenReturn(Optional.empty());
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

    Optional<PrivateCar> parsed = format.parse(privateCar(), context);

    assertCars(parsed.get(), privateConventionalCar);
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
