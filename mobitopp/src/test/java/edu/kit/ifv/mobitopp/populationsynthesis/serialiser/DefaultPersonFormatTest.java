package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
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
  private HouseholdForSetup household;
  private PersonBuilder personForDemand;
  private PersonBuilder emobilityPerson;
  private PopulationContext context;
  private Zone zone;
	private PersonChanger changer;

  @BeforeEach
  public void initialise() {
    context = mock(PopulationContext.class);
    changer = mock(PersonChanger.class);
    format = new DefaultPersonFormat(changer);
    household = mock(HouseholdForSetup.class);
    zone = mock(Zone.class);
    when(changer.attributesOf(any())).thenAnswer(i -> i.getArgument(0));
    when(zone.getId()).thenReturn(zoneId);
    when(household.getId()).thenReturn(new HouseholdId(householdOid, year, householdOid));
    personForDemand = ExampleSetup.personOf(household, personOid, zone);
    personForDemand.clearFixedDestinations();
    personForDemand.clearPatternActivityWeek();
    emobilityPerson = ExampleSetup.emobilityPersonOf(household, personOid, zone);
    emobilityPerson.clearFixedDestinations();
    emobilityPerson.clearPatternActivityWeek();
    when(context.activityScheduleFor(personOid)).thenReturn(ExampleSetup.activitySchedule());
  }

  @Test
  public void serialisePersonAttributes() throws IOException {
    List<String> prepared = format.prepare(personForDemand);

    assertThat(prepared).isEqualTo(personFormat());
  }

  @Test
  public void serialialiseEmobilityPerson() throws IOException {
    List<String> prepared = format.prepare(emobilityPerson);

    assertThat(prepared).isEqualTo(emobilityPerson());
  }

  @Test
  public void parseNormalPerson() {
    prepareExistingHoushold();
    Optional<PersonBuilder> parsedPerson = format.parse(personFormat(), context);

    assertPersons(parsedPerson.get(), personForDemand);
    verify(changer).attributesOf(parsedPerson.get());
    verify(household).addPerson(parsedPerson.get());
  }

  @Test
  public void parseEmobilityPerson() {
    prepareExistingHoushold();
    Optional<PersonBuilder> parsedPerson = format.parse(emobilityPerson(), context);

    assertPersons(parsedPerson.get(), emobilityPerson);
    verify(household).addPerson(parsedPerson.get());
  }

  private void prepareExistingHoushold() {
    when(context.getHouseholdForSetupByOid(householdOid)).thenReturn(Optional.of(household));
  }

  @Test
  public void parseMissingHousehold() {
    prepareMissingHousehold();

    Optional<PersonBuilder> parsedPerson = format.parse(personFormat(), context);

    assertThat(parsedPerson).isEmpty();
  }

  private void prepareMissingHousehold() {
    when(context.getHouseholdByOid(householdOid)).thenReturn(Optional.empty());
  }
  
  private void assertPersons(PersonBuilder person, PersonBuilder originalPerson) {
    assertValue(PersonBuilder::getId, person, originalPerson);
    assertValue(PersonBuilder::age, person, originalPerson);
    assertValue(PersonBuilder::employment, person, originalPerson);
    assertValue(PersonBuilder::gender, person, originalPerson);
    assertValue(PersonBuilder::graduation, person, originalPerson);
    assertValue(PersonBuilder::hasBike, person, originalPerson);
    assertValue(PersonBuilder::hasAccessToCar, person, originalPerson);
    assertValue(PersonBuilder::hasPersonalCar, person, originalPerson);
    assertValue(PersonBuilder::hasCommuterTicket, person, originalPerson);
    assertValue(PersonBuilder::getActivityPattern, person, originalPerson);
    assertValue(PersonBuilder::getMobilityProviderMembership, person, originalPerson);
    assertEmobilityPersons(person, originalPerson);
  }

  private void assertEmobilityPersons(PersonBuilder person, PersonBuilder originalPerson) {
    assertThat(person.getClass()).isEqualTo(originalPerson.getClass());
    if (originalPerson instanceof EmobilityPerson && person instanceof EmobilityPerson) {
      assertEmobilityValuesOf((EmobilityPerson) person, (EmobilityPerson) originalPerson);
    }
  }

  private void assertEmobilityValuesOf(EmobilityPerson person, EmobilityPerson originalPerson) {
    assertValue(EmobilityPerson::eMobilityAcceptance, person, originalPerson);
    assertValue(EmobilityPerson::chargingInfluencesDestinantionChoice, person, originalPerson);
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
