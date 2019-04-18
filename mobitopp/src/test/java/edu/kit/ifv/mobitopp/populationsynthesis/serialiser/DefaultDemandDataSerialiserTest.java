package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.OpportunityLocations;
import edu.kit.ifv.mobitopp.populationsynthesis.Population;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;
import edu.kit.ifv.mobitopp.util.ReflectionHelper;

public class DefaultDemandDataSerialiserTest {

  private static final int zoneOid = 1;
  private static final ZoneId zoneId = new ZoneId("1", zoneOid);
  private static Population population;

  private Serialiser<Household> householdSerialiser;
  private ForeignKeySerialiser<Person> personSerialiser;
  private Serialiser<PersonPatternActivity> activitySerialiser;
  private ForeignKeySerialiser<PrivateCar> carSerialiser;
  private Serialiser<PersonFixedDestination> fixedDestinationSerialiser;
  private DefaultDemandDataSerialiser serialiser;
  private Serialiser<Opportunity> opportunitySerialiser;

  @BeforeClass
  public static void resetHouseholdIdSequence() throws ReflectiveOperationException {
    ReflectionHelper.resetHouseholdIdSequence();
    Zone zone = mock(Zone.class);
    population = ExampleSetup.population(zone);
    when(zone.getInternalId()).thenReturn(zoneId);
  }

  @SuppressWarnings("unchecked")
  @Before
  public void initialise() {
    householdSerialiser = mock(Serialiser.class);
    activitySerialiser = mock(Serialiser.class);
    carSerialiser = mock(ForeignKeySerialiser.class);
    personSerialiser = mock(ForeignKeySerialiser.class);
    fixedDestinationSerialiser = mock(Serialiser.class);
    opportunitySerialiser = mock(Serialiser.class);
    serialiser = new DefaultDemandDataSerialiser(householdSerialiser, personSerialiser,
        activitySerialiser, carSerialiser, fixedDestinationSerialiser, opportunitySerialiser);
  }

  @Test
  public void closesWriter() throws Exception {
    serialiser.close();

    verify(householdSerialiser).close();
    verify(activitySerialiser).close();
    verify(personSerialiser).close();
    verify(carSerialiser).close();
    verify(fixedDestinationSerialiser).close();
    verify(opportunitySerialiser).close();
  }

  @Test
  public void serialiseHeaderOfPopulation() {
    serialiser.writeHeader();

    verifyWrittenHeader();
  }

  private void verifyWrittenHeader() {
    verify(householdSerialiser).writeHeader();
    verify(activitySerialiser).writeHeader();
    verify(personSerialiser).writeHeader();
    verify(carSerialiser).writeHeader();
    verify(fixedDestinationSerialiser).writeHeader();
  }

  @Test
  public void serialiseHouseholdAttributes() throws IOException {
    serialisePopulation();

    verifyWrittenHousehold();
  }

  private void serialisePopulation() {
    population.households().forEach(serialiser::serialise);
  }

  private void verifyWrittenHousehold() throws IOException {
    population.households().forEach(verify(householdSerialiser)::write);
  }

  @Test
  public void serialisePersonAttributes() throws IOException {
    serialisePopulation();

    verifyWrittenPersonOfHousehold();
  }

  @Test
  public void serialiseActivityAttributes() throws IOException {
    serialisePopulation();

    verifyWrittenPatternActivityWeek();
  }

  @Test
  public void serialiseCarAttributes() throws IOException {
    serialisePopulation();

    verifyWrittenCars();
  }

  @Test
  public void serialiseFixedDestinations() throws IOException {
    serialisePopulation();

    verifyWrittenFixedDestinations();
  }

  private void verifyWrittenPersonOfHousehold() throws IOException {
    population.households().flatMap(Household::persons).forEach(verify(personSerialiser)::write);
  }

  private void verifyWrittenPatternActivityWeek() throws IOException {
    population
        .households()
        .flatMap(Household::persons)
        .flatMap(DefaultDemandDataSerialiserTest::toPersonPattern)
        .forEach(verify(activitySerialiser)::write);
  }

  private static Stream<PersonPatternActivity> toPersonPattern(Person person) {
    Function<ExtendedPatternActivity, PersonPatternActivity> toPattern = activity -> new PersonPatternActivity(
        person.getOid(), activity);
    return person.tourBasedActivityPattern().asPatternActivities().stream().map(toPattern);
  }

  private void verifyWrittenCars() throws IOException {
    population.households().flatMap(Household::cars).forEach(verify(carSerialiser)::write);
  }

  private void verifyWrittenFixedDestinations() throws IOException {
    population
        .households()
        .flatMap(Household::persons)
        .flatMap(DefaultDemandDataSerialiserTest::toPersonDestination)
        .peek(System.out::println)
        .forEach(verify(fixedDestinationSerialiser)::write);
  }

  private static Stream<PersonFixedDestination> toPersonDestination(Person person) {
    Function<FixedDestination, PersonFixedDestination> toPattern = destination -> new PersonFixedDestination(
        person.getId(), destination);
    return person.getFixedDestinations().map(toPattern);
  }

  @Test
  public void serialisesOpportunities() {
    OpportunityLocations opportunities = mock(OpportunityLocations.class);

    serialiser.serialise(opportunities);

    verify(opportunities).forEach(any());
  }
}
