package edu.kit.ifv.mobitopp.simulation.person;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.populationsynthesis.FixedDestinations;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Graduation;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.PersonAttributes;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityStartAndDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;
import edu.kit.ifv.mobitopp.simulation.tour.DefaultTourFactory;
import edu.kit.ifv.mobitopp.simulation.tour.TourFactory;
import edu.kit.ifv.mobitopp.time.Time;

public class PersonForDemandTest {

	private int oid;
	private PersonId id;
	private Household household;
	private int age;
	private Employment employment;
	private Gender gender;
	private Graduation graduation;
	private int income;
	private boolean hasBike;
	private boolean hasAccessToCar;
	private boolean hasPersonalCar;
	private boolean hasCommuterTicket;
	private boolean hasLicense;
	private TourBasedActivityPattern activityPattern;
	private Car car;
	private FixedDestinations fixedDestinations;
	private ModeChoicePreferences prefSurvey;
	private ModeChoicePreferences prefSimulation;
	private ModeChoicePreferences travelTimeSensitivity;
	private PersonForDemand person;

	@BeforeEach
	public void initialise() {
		oid = 1;
		short year = 2000;
		int householdOid = 1;
    id = new PersonId(oid, new HouseholdId(householdOid, year, 2), 3);
		household = mock(Household.class);
		age = 4;
		employment = Employment.EDUCATION;
		gender = Gender.FEMALE;
		graduation = Graduation.undefined;
		income = 5;
		hasBike = true;
		hasAccessToCar = false;
		hasPersonalCar = true;
		hasCommuterTicket = false;
		hasLicense = false;
		activityPattern = mock(TourBasedActivityPattern.class);
		car = mock(Car.class);
		fixedDestinations = new FixedDestinations();
		prefSurvey = ModeChoicePreferences.NOPREFERENCES;
		prefSimulation = ModeChoicePreferences.NOPREFERENCES;
		travelTimeSensitivity = ModeChoicePreferences.NOPREFERENCES;
		person = newPerson();
	}

	private PersonForDemand newPerson(Map<String, Boolean> carSharingCustomership) {
		return new PersonForDemand(id, household, age, employment, gender, graduation, income, hasBike,
				hasAccessToCar, hasPersonalCar, hasCommuterTicket, hasLicense, activityPattern,
				fixedDestinations, carSharingCustomership, prefSurvey, prefSimulation, travelTimeSensitivity);
	}
	
	private PersonForDemand newPerson() {
		return newPerson(emptyMap());
	}

	@Test
	public void usesCar() {
		person.useCar(car, someDate());
		
		Car usedCar = person.whichCar();
		
    assertThat(usedCar).isEqualTo(car); 
    verify(car).use(person, someDate());
	}
	
	@Test
	public void parksCar() {
		Zone zone = mock(Zone.class);
		Location location = new Example().location();
		person.useCar(car, someDate());
		person.parkCar(zone, location, someDate());
		
		Car parkedCar = person.whichCar();
		
		assertThat(parkedCar).isEqualTo(car);
	}
	
	@Test
	public void usesCarAsPassenger() {
		when(car.canCarryPassengers()).thenReturn(true);
		person.useCarAsPassenger(car);
		
		Car usedCar = person.whichCar();
		
		assertThat(usedCar).isEqualTo(car);
	}

	private Time someDate() {
		return Data.someTime();
	}

	@Test
	public void providesAttributes() {
		PersonAttributes attributes = person.attributes();

		assertThat(attributes).isEqualTo(personAttributes());
	}

	private PersonAttributes personAttributes() {
		return new PersonAttributes(id, household, age, employment, gender, income, hasBike,
				hasAccessToCar, hasPersonalCar, hasCommuterTicket, hasLicense);
	}
	
	@Test
  void removeTourPatternAfterInitialisation() throws Exception {
	  when(activityPattern.asPatternActivities()).thenReturn(singletonList(somePattern()));
    Optional<TourBasedActivityPattern> before = person.tourBasedActivityPattern();
    
    TourFactory factory = new DefaultTourFactory();
    ActivityStartAndDurationRandomizer randomizer = a -> a;
    List<Time> days = singletonList(Time.start);
    person.initSchedule(factory, randomizer, days);
    
    Optional<TourBasedActivityPattern> after = person.tourBasedActivityPattern();
    
		assertAll(() -> assertThat(before).hasValue(activityPattern),
				() -> assertThat(after).isEmpty());
	}

  private ExtendedPatternActivity somePattern() {
    return ExtendedPatternActivity.STAYATHOME_ACTIVITY;
  }
  

	@Test
	public void hasNoAssignedCarSharingCompanies() {
		Map<String, Boolean> carSharingCustomership = Collections.emptyMap();
		PersonForDemand person = newPerson(carSharingCustomership);
		
		assertFalse(person.isMobilityProviderCustomer("Stadtmobil"));
	}
	
	@Test
	public void isCarSharingCustomer() {
		String company = "Stadtmobil";
		Map<String, Boolean> carSharingCustomership = Collections.singletonMap(company, true);
		PersonForDemand person = newPerson(carSharingCustomership);
		
		assertTrue(person.isMobilityProviderCustomer(company));
	}
	
	@Test
	public void isNoCarSharingCustomer() {
		String company = "Stadtmobil";
		Map<String, Boolean> carSharingCustomership = Collections.singletonMap(company, false);
		PersonForDemand person = newPerson(carSharingCustomership);
		
		assertFalse(person.isMobilityProviderCustomer(company));
	}
}
