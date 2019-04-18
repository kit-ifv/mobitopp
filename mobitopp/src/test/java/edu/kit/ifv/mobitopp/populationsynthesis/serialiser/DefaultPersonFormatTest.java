package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleSetup;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;

public class DefaultPersonFormatTest {

  private static final short year = 2011;
  private static final int zoneOid = 1;
  private static final ZoneId zoneId = new ZoneId("1", zoneOid);
  private static final int personOid = 1;
  private static final int personNumber = personOid;
  private static final int householdOid = 0;

  private DefaultPersonFormat format;
  private Household household;
  private Person personForDemand;
  private Person emobilityPerson;
  private PopulationContext context;
  private Zone zone;
  private FixedDestination destination;

  @Before
  public void initialise() {
    context = mock(PopulationContext.class);
    format = new DefaultPersonFormat();
    household = mock(Household.class);
    zone = mock(Zone.class);
    when(zone.getId()).thenReturn(zoneId);
    when(household.getId()).thenReturn(new HouseholdId(householdOid, year, householdOid));
    when(household.getOid()).thenReturn(householdOid);
    personForDemand = Example.personOf(household, personOid, zone);
    emobilityPerson = Example.emobilityPersonOf(household, personOid, zone);
    destination = new FixedDestination(ActivityType.HOME, zone, Example.location);
    when(context.activityScheduleFor(personOid)).thenReturn(ExampleSetup.activitySchedule());
  }

  @Test
  public void serialisePersonAttributes() throws IOException {
    List<String> prepared = format.prepare(personForDemand);

    assertThat(prepared, is(equalTo(personFormat())));
  }

  @Test
  public void serialialiseEmobilityPerson() throws IOException {
    List<String> prepared = format.prepare(emobilityPerson);

    assertThat(prepared, is(equalTo(emobilityPerson())));
  }

  @Test
  public void parseNormalPerson() {
    prepareExistingHoushold();
    prepareFixedLocations();
    Optional<Person> parsedPerson = format.parse(personFormat(), context);

    assertPersons(parsedPerson.get(), personForDemand);
    assertThat(parsedPerson.map(p -> p.fixedDestinationFor(destination.activityType())),
        hasValue(destination.location()));
    verify(household).addPerson(parsedPerson.get());
  }

  private void prepareFixedLocations() {
    when(context.destinations(any())).thenReturn(Stream.of(destination));
  }

  @Test
  public void parseEmobilityPerson() {
    prepareExistingHoushold();
    Optional<Person> parsedPerson = format.parse(emobilityPerson(), context);

    assertPersons(parsedPerson.get(), emobilityPerson);
    verify(household).addPerson(parsedPerson.get());
  }

  private void prepareExistingHoushold() {
    when(context.getHouseholdByOid(householdOid)).thenReturn(Optional.of(household));
  }

  @Test
  public void parseMissingHousehold() {
    prepareMissingHousehold();

    Optional<Person> parsedPerson = format.parse(personFormat(), context);

    assertThat(parsedPerson, isEmpty());
  }

  private void prepareMissingHousehold() {
    when(context.getHouseholdByOid(householdOid)).thenReturn(Optional.empty());
  }

  private void assertPersons(Person person, Person originalPerson) {
    assertValue(Person::getOid, person, originalPerson);
    assertValue(Person::getId, person, originalPerson);
    assertValue(Person::age, person, originalPerson);
    assertValue(Person::employment, person, originalPerson);
    assertValue(Person::gender, person, originalPerson);
    assertValue(Person::graduation, person, originalPerson);
    assertValue(Person::hasBike, person, originalPerson);
    assertValue(Person::hasAccessToCar, person, originalPerson);
    assertValue(Person::hasPersonalCar, person, originalPerson);
    assertValue(Person::hasCommuterTicket, person, originalPerson);
    assertValue(Person::getPatternActivityWeek, person, originalPerson);
    assertEmobilityPersons(person, originalPerson);
  }

  private void assertEmobilityPersons(Person person, Person originalPerson) {
    assertThat(person.getClass(), is(equalTo(originalPerson.getClass())));
    if (originalPerson instanceof EmobilityPerson && person instanceof EmobilityPerson) {
      assertEmobilityValuesOf((EmobilityPerson) person, (EmobilityPerson) originalPerson);
    }
  }

  private void assertEmobilityValuesOf(EmobilityPerson person, EmobilityPerson originalPerson) {
    assertValue(EmobilityPerson::eMobilityAcceptance, person, originalPerson);
    assertValue(EmobilityPerson::chargingInfluencesDestinantionChoice, person, originalPerson);
    assertValue(EmobilityPerson::carSharingCustomership, person, originalPerson);
  }

  private List<String> personFormat() {
    return asList(valueOf(personOid), valueOf(personNumber), valueOf(householdOid),
        valueOf(ExampleSetup.age), valueOf(ExampleSetup.employment), valueOf(ExampleSetup.gender),
        valueOf(ExampleSetup.graduation.getNumeric()), valueOf(ExampleSetup.income),
        valueOf(ExampleSetup.hasBike), valueOf(ExampleSetup.hasAccessToCar),
        valueOf(ExampleSetup.hasPersonalCar), valueOf(ExampleSetup.hasCommuterTicket),
        valueOf(ExampleSetup.hasLicense), valueOf(ModeChoicePreferences.NOPREFERENCES),
        valueOf(ModeChoicePreferences.NOPREFERENCES));
  }

  private List<String> emobilityPerson() {
    List<String> normalPerson = personFormat();
    List<String> emobilityPerson = new ArrayList<>(normalPerson);
    emobilityPerson.add(valueOf(ExampleSetup.eMobilityAcceptance));
    emobilityPerson.add(valueOf(ExampleSetup.chargingInfluencesDestinationChoice));
    emobilityPerson.add(ExampleSetup.carSharingCustomership().toString());
    return emobilityPerson;
  }
}
