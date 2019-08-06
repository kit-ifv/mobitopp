package edu.kit.ifv.mobitopp.populationsynthesis;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Person;

public class DefaultPersonForSetupTest {

  private PersonBuilder setupPerson;
  private HouseholdId householdId;
  private Zone zone;

  @Before
  public void initialise() {
    HouseholdForSetup setupHousehold = mock(HouseholdForSetup.class);
    int householdOid = 1;
    short year = 2000;
    long householdNumber = 1;
    householdId = new HouseholdId(householdOid, year, householdNumber);
    when(setupHousehold.getId()).thenReturn(householdId);
    int personNumber = 1;
    zone = ExampleZones.create().someZone();

    setupPerson = ExampleSetup.personOf(setupHousehold, personNumber, zone);
  }

  @Test
  public void createPerson() {
    Household household = mock(Household.class);
    when(household.getId()).thenReturn(householdId);

    Person person = setupPerson.toPerson(household);

    assertThat(setupPerson.getId(), is(equalTo(person.getId())));
  }
  
  @Test
  public void hasFixedDestinations() {
    setupPerson.setFixedDestination(fixedDestinationFor(ActivityType.WORK));
    setupPerson.setFixedDestination(fixedDestinationFor(ActivityType.EDUCATION));
   
    assertTrue(setupPerson.hasFixedZoneFor(ActivityType.WORK));
    assertTrue(setupPerson.hasFixedZoneFor(ActivityType.EDUCATION));
    assertTrue(setupPerson.hasFixedActivityZone());
    assertFalse(setupPerson.hasFixedZoneFor(ActivityType.BUSINESS));
  }
  
  @Test
  public void getFixedZoneForActivityType() {
    setupPerson.setFixedDestination(fixedDestinationFor(ActivityType.WORK));
    
    Optional<Zone> fixedZone = setupPerson.fixedZoneFor(ActivityType.WORK);
    
    assertThat(fixedZone, hasValue(zone));
  }

  @Test
  public void fixedActivityZone() {
    ActivityType activityType = ActivityType.WORK;
    FixedDestination destination = fixedDestinationFor(activityType);
    
    setupPerson.setFixedDestination(destination);
    Zone fixedActivityZone = setupPerson.fixedActivityZone();
    
    assertThat(fixedActivityZone, is(equalTo(zone)));
  }

  private FixedDestination fixedDestinationFor(ActivityType activityType) {
    Location location = zone.centroidLocation();
    return new FixedDestination(activityType, zone, location);
  }
}
