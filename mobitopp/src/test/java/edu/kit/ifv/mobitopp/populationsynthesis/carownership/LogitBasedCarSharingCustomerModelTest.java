package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.DefaultPersonForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.DefaultHouseholdForSetup;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Graduation;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;

public class LogitBasedCarSharingCustomerModelTest {

	private final static double EPSILON = 0.01;

	private	Random random;

	private LogitBasedCarSharingCustomerModel stadtmobil;

	private ExampleZones zones;
	private Map<String, Float> carsharingcarDensities;
	private CarSharingDataForZone carSharing;

	private HouseholdForSetup household_dummy;
	private HouseholdForSetup household;

	private PersonBuilder person_dummy;
	private PersonBuilder male_working;
	private PersonBuilder female_working;

	@Before
	public void setUp() throws URISyntaxException {

		random = new Random(1234);

		stadtmobil = new LogitBasedCarSharingCustomerModel(random, "Stadtmobil", parameterFile());

		zones = ExampleZones.create();

		carsharingcarDensities = new HashMap<String, Float>();
		carsharingcarDensities.put("Stadtmobil",0.0f);
		carsharingcarDensities.put("Flinkster",0.0f);
		carsharingcarDensities.put("Car2Go",0.0f);

		carSharing = new CarSharingDataForZone(
			zone(),
			new ArrayList<StationBasedCarSharingOrganization>(),
			new HashMap<String,List<CarSharingStation>>(),
			new ArrayList<FreeFloatingCarSharingOrganization>(),
			new HashMap<String, Boolean>(),
			new HashMap<String, Integer>(),
			carsharingcarDensities
		);
	
		zone().setCarSharing(carSharing);

		household_dummy = new DefaultHouseholdForSetup(
																			null, // id
																			6, // nominalSize
																			0, // domcode
																			zone(),
																			null, // Location
																			5, // number of minors
																			5, // not simulated people,
																			4, // number of cars
																			3000, // income
                                      1, // income class
																			true
																		);

		household = new DefaultHouseholdForSetup(
																			null, // id
																			3, // nominalSize
																			0, // domcode
																			zone(),
																			null, // Location
																			1, // number of minors
																			1, // not simulated people,
																			0, // number of cars
																			3000, // income
                                      1, // income class
																			true
																		);

		person_dummy = new DefaultPersonForSetup(	
												null, // id
												household,
												90, // age,
												Employment.NONE,
												Gender.MALE,
												Graduation.undefined,
												0, // income
												null)
												.setHasBike(false)
												.setHasAccessToCar(true)
												.setHasPersonalCar(false)
												.setHasCommuterTicket(false)
												.setHasDrivingLicense(true);

		male_working = new DefaultPersonForSetup(	
												null, // id
												household,
												35, // age,
												Employment.FULLTIME,
												Gender.MALE,
                        Graduation.undefined,
												0, // income
												null)
												.setHasBike(false)
												.setHasAccessToCar(true)
												.setHasPersonalCar(false)
												.setHasCommuterTicket(false)
												.setHasDrivingLicense(true);

		female_working = new DefaultPersonForSetup(	
												null, // id
												household,
												30, // age,
												Employment.FULLTIME,
												Gender.FEMALE,
                        Graduation.undefined,
												0, // income
												null)
												.setHasBike(false)
												.setHasAccessToCar(true)
												.setHasPersonalCar(false)
												.setHasCommuterTicket(false)
												.setHasDrivingLicense(true);

		household.addPerson(male_working);
		household.addPerson(female_working);

	}

	private String parameterFile() throws URISyntaxException {
		return new File(
				getClass().getResource("LogitBasedCarSharingCustomerModelParameter_Stadtmobil.txt").toURI())
						.getAbsolutePath();
	}

	private Zone zone() {
		return zones.someZone();
	}

	protected static HouseholdForSetup makeHousehold(
		int nominalSize,
		int number_of_cars
	) {
		return new DefaultHouseholdForSetup(
																			null, // id
																			nominalSize, // nominalSize
																			0, // domcode
																			null, // zone,
																			null, // Location
																			nominalSize-1, // number of minors
																			nominalSize-1, // not simulated people,
																			number_of_cars, // number of cars
																			3000, // income
                                      1, // income class
																			true
																		);
	}

	protected static PersonBuilder makePerson(
		int age,
		Employment employment,
		Gender sex,
		boolean ticket
	) {
	
		return new DefaultPersonForSetup(	
												null, // id
												null, //household,
												age, // age,
												employment,
												sex,
												Graduation.undefined,
												0, // income
												null)
												.setHasBike(false)
												.setHasAccessToCar(true)
												.setHasPersonalCar(false)
												.setHasCommuterTicket(ticket)
												.setHasDrivingLicense(true);
	}

	@Test
	public void testConstructor() {

 		assertEquals(3, household.getSize());
 		assertEquals(0, household.getTotalNumberOfCars());

 		assertEquals(Employment.FULLTIME, male_working.employment());
 		assertEquals(35, male_working.age());
	}

	@Test
	public void testStadtmobilDummy() {

		double expected = -6.5162;

 		assertEquals(expected, stadtmobil.calculateUtility(person_dummy,household_dummy,zone()), EPSILON);

		expected = -6.5162 + 0.0763 + 0.9332;

 		assertEquals(expected, stadtmobil.calculateUtility(male_working,household_dummy,zone()), EPSILON);

		expected = -6.5162 + 0.0763 + -0.5607 + 1.3747;

 		assertEquals(expected, stadtmobil.calculateUtility(female_working,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testStadtmobilMale() {

		double expected = -6.5162;

		PersonBuilder person = makePerson(90, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testStadtmobilMaleTicket() {

		double expected = -6.5162 + 0.1095;

		PersonBuilder person = makePerson(90, 
																				Employment.NONE, 
																				Gender.MALE,
																				true
																				);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testStadtmobilMaleFulltime() {

		double expected = -6.5162 + 0.0763 ;

		PersonBuilder person = makePerson(90, 
																				Employment.FULLTIME, 
																				Gender.MALE,
																				false
																				);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testStadtmobilFemaleFulltime() {

		double expected = -6.5162 + -0.5607 + 0.0763;

		PersonBuilder person = makePerson(90, 
																				Employment.FULLTIME, 
																				Gender.FEMALE,
																				false
																				);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testStadtmobilMaleAge24() {

		double expected = -6.5162 -0.69665;

		PersonBuilder person = makePerson(24, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testStadtmobilMaleFulltimeAge30() {

		double expected = -6.5162  + 0.0763 + 1.3747;

		PersonBuilder person = makePerson(30, 
																				Employment.FULLTIME, 
																				Gender.MALE,
																				false
																				);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testStadtmobilFemaleFulltimeAge30() {

		double expected = -6.5162 + -0.5607 + 0.0763 + 1.3747;

		PersonBuilder person = makePerson(30, 
																				Employment.FULLTIME, 
																				Gender.FEMALE,
																				false
																				);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testStadtmobilMaleHouseholdCars() {

		double expected = -6.5162;

		PersonBuilder person = makePerson(90, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);

		HouseholdForSetup household = makeHousehold(5,5);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + 2.1474;

		household = makeHousehold(5,0);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + -0.27;

		household = makeHousehold(5,1);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testStadtmobilMaleHouseholdPersons() {

		double expected = -6.5162;

		PersonBuilder person = makePerson(90, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);

		HouseholdForSetup household = makeHousehold(5,5);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + -1.5638;

		household = makeHousehold(1,5);

 		assertEquals(1, household.nominalSize());
 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + -0.40315;

		household = makeHousehold(2,5);

 		assertEquals(2, household.nominalSize());
 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + 0.1005;

		household = makeHousehold(3,5);

 		assertEquals(3, household.nominalSize());
 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + 0.4201;

		household = makeHousehold(4,5);

 		assertEquals(4, household.nominalSize());
	}

	@Test
	public void testStadtmobilMaleHouseholdSize1() {

		PersonBuilder person = makePerson(90, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);


		double expected = -6.5162 + -1.5638;

		household = makeHousehold(1,5);

		household.addPerson(person);

 		assertEquals(1, household.getSize());
 		assertEquals(1, household.nominalSize());
 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testStadtmobilMaleHouseholdSize2() {

		PersonBuilder person = makePerson(90, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);

		double expected = -6.5162 + -0.40315;

		household = makeHousehold(2,5);

		household.addPerson(person);

 		assertEquals(2, household.nominalSize());
 		assertEquals(2, household.getSize());
 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testStadtmobilFemaleAge() {

		double expected = -6.5162 + -0.5607;

		PersonBuilder person = makePerson(90, 
																				Employment.NONE, 
																				Gender.FEMALE,
																				false
																				);

		HouseholdForSetup household = makeHousehold(5,5);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + -0.5607 + -0.69665;

		person = makePerson(18, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);

		person = makePerson(24, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + -0.5607 + 1.3747;

		person = makePerson(25, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);

		person = makePerson(34, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + -0.5607 + 0.9332;

		person = makePerson(35, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);

		person = makePerson(49, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + -0.5607 + 0.9065;

		person = makePerson(50, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);

		person = makePerson(64, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + -0.5607;

		person = makePerson(65, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);

		person = makePerson(80, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testStadtmobilMaleHouseholdSize2Ticket() {

		PersonBuilder person = makePerson(90, 
																				Employment.NONE, 
																				Gender.MALE,
																				true
																				);

		double expected = -6.5162 + -0.40315 + 0.1095;

		household = makeHousehold(2,5);

 		assertEquals(2, household.nominalSize());
 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testStadtmobilFemaleEmployment() {

		double expected = -6.5162 + -0.5607 + -0.69665;

		PersonBuilder person = makePerson(20, 
																				Employment.NONE, 
																				Gender.FEMALE,
																				false
																				);

		HouseholdForSetup household = makeHousehold(5,5);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + -0.5607 + -0.69665 + 0.0763;

		person = makePerson(20, Employment.FULLTIME, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + -0.5607 + -0.69665 + 0.8843;

		person = makePerson(20, Employment.PARTTIME, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + -0.5607 + -0.69665 + -1.8083;

		person = makePerson(20, Employment.UNEMPLOYED, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + -0.5607 + -0.69665 + 1.4458;

		person = makePerson(20, Employment.STUDENT_SECONDARY, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + -0.5607 + -0.69665 + 0.5085;

		person = makePerson(20, Employment.STUDENT_TERTIARY, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + -0.5607 + -0.69665 + -0.5818;

		person = makePerson(20, Employment.EDUCATION, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + -0.5607 + -0.69665 + -0.2886;

		person = makePerson(20, Employment.HOMEKEEPER, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.5162 + -0.5607 + -0.69665 + -0.1008;

		person = makePerson(20, Employment.RETIRED, 
														Gender.FEMALE, false);

 		assertEquals(expected, stadtmobil.calculateUtility(person,household,zone()), EPSILON);
	}


	@Test
	public void testStadtmobil() {

		double expected_male = -6.5162 + 0.0763 + 2.1474 + 0.1005 + 0.9332;

 		assertEquals(expected_male, stadtmobil.calculateUtility(male_working,household,zone()), EPSILON);
	}

}

