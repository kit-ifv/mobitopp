package edu.kit.ifv.mobitopp.simulation.person;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.PersonAttributes;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;
import edu.kit.ifv.mobitopp.time.Time;

public class PersonForDemandTest {

	private int oid;
	private PersonId id;
	private Household household;
	private int age;
	private Employment employment;
	private Gender gender;
	private int income;
	private boolean hasBike;
	private boolean hasAccessToCar;
	private boolean hasPersonalCar;
	private boolean hasCommuterTicket;
	private boolean hasLicense;
	private PatternActivityWeek activitySchedule;
	private PersonForDemand person;
	private Car car;
	private ModeChoicePreferences prefSurvey;
	private ModeChoicePreferences prefSimulation;

	@Before
	public void initialise() {
		oid = 1;
		id = new PersonId(new HouseholdId(2000, 2), 3);
		household = mock(Household.class);
		age = 4;
		employment = Employment.EDUCATION;
		gender = Gender.FEMALE;
		income = 5;
		hasBike = true;
		hasAccessToCar = false;
		hasPersonalCar = true;
		hasCommuterTicket = false;
		hasLicense = false;
		activitySchedule = mock(PatternActivityWeek.class);
		car = mock(Car.class);
		person = newPerson();
		prefSurvey = ModeChoicePreferences.NOPREFERENCES;
		prefSimulation = ModeChoicePreferences.NOPREFERENCES;
	}

	private PersonForDemand newPerson() {
		return new PersonForDemand(oid, id, household, age, employment, gender, income, hasBike,
				hasAccessToCar, hasPersonalCar, hasCommuterTicket, hasLicense, activitySchedule, prefSurvey, prefSimulation);
	}

	@Test
	public void usesCar() {
		person.useCar(car, someDate());
		
		Car usedCar = person.whichCar();
		
		assertThat(usedCar, is(equalTo(car)));
	}
	
	@Test
	public void parksCar() {
		Zone zone = mock(Zone.class);
		Location location = new Example().location();
		person.useCar(car, someDate());
		person.parkCar(zone, location, someDate());
		
		Car parkedCar = person.whichCar();
		
		assertThat(parkedCar, is(equalTo(car)));
	}
	
	@Test
	public void usesCarAsPassenger() {
		when(car.isUsed()).thenReturn(true);
		person.useCarAsPassenger(car);
		
		Car usedCar = person.whichCar();
		
		assertThat(usedCar, is(equalTo(car)));
	}

	private Time someDate() {
		return Data.someTime();
	}

	@Test
	public void providesAttributes() {
		PersonAttributes attributes = person.attributes();

		assertThat(attributes, is(equalTo(personAttributes())));
	}

	private PersonAttributes personAttributes() {
		return new PersonAttributes(oid, id, household, age, employment, gender, income, hasBike,
				hasAccessToCar, hasPersonalCar, hasCommuterTicket, hasLicense);
	}
}
