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
import edu.kit.ifv.mobitopp.populationsynthesis.EconomicalStatus;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.LogitBasedCarSharingCustomerModel;
import edu.kit.ifv.mobitopp.simulation.DefaultHouseholdForSetup;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Graduation;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;

public class LogitBasedCarSharingCustomerModelFlinksterTest {

	private final static double EPSILON = 0.01;

	private	Random random;

	private LogitBasedCarSharingCustomerModel flinkster;

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

		flinkster = new LogitBasedCarSharingCustomerModel(random, "Flinkster", parameterFile());

		zones = ExampleZones.create();

		carsharingcarDensities = new HashMap<String, Float>();
		carsharingcarDensities.put("Flinkster",0.0f);

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
																			6,
																			0, // domcode
																			zone(),
																			null, // Location
																			5, // number of minors
																			5, // not simulated people,
																			4, // number of cars,
																			3000, // income
                                      1, // income class
																			EconomicalStatus.veryLow,
																			true
																		);

		household = new DefaultHouseholdForSetup(
																			null, // id
																			4,
																			0, // domcode
																			zone(),
																			null, // Location
																			2, // number of minors
																			2, // not simulated people,
																			0, // number of cars
																			3000, // income
                                      1, // income class
																			EconomicalStatus.veryLow,
																			true
																		);

		person_dummy = new DefaultPersonForSetup(	
												null, // id
												household,
												50, // age,
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
				getClass().getResource("LogitBasedCarSharingCustomerModelParameter_Flinkster.txt").toURI())
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
																			EconomicalStatus.veryLow,
																			true
																		);
	}

	protected static PersonBuilder makePerson(
		int age,
		Employment employment,
		Gender sex,
		boolean ticket
	) {
	
		return   new DefaultPersonForSetup(	
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

 		assertEquals(4, household.getSize());
 		assertEquals(0, household.getTotalNumberOfCars());

 		assertEquals(Employment.FULLTIME, male_working.employment());
 		assertEquals(35, male_working.age());
	}

	@Test
	public void testFlinksterDummy() {

		double expected = -6.0862;

 		assertEquals(expected, flinkster.calculateUtility(person_dummy,household_dummy,zone()), EPSILON);

		expected = -6.0862 + 0.55032 + 0.9263;

 		assertEquals(expected, flinkster.calculateUtility(male_working,household_dummy,zone()), EPSILON);

		expected = -6.0862 + -1.8967 + 1.1547 + 0.9263;

 		assertEquals(expected, flinkster.calculateUtility(female_working,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMale() {

		double expected = -6.0862;

		PersonBuilder person = makePerson(50, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);

 		assertEquals(expected, flinkster.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMaleTicket() {

		double expected = -6.0862 + 0.1095;

		PersonBuilder person = makePerson(50, 
																				Employment.NONE, 
																				Gender.MALE,
																				true
																				);

 		assertEquals(expected, flinkster.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMaleFulltime() {

		double expected = -6.0862 + 0.9263 ;

		PersonBuilder person = makePerson(50, 
																				Employment.FULLTIME, 
																				Gender.MALE,
																				false
																				);

 		assertEquals(expected, flinkster.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testFlinksterFemaleFulltime() {

		double expected = -6.0862 + -1.8967 + 0.9263 ;

		PersonBuilder person = makePerson(50, 
																				Employment.FULLTIME, 
																				Gender.FEMALE,
																				false
																				);

 		assertEquals(expected, flinkster.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMaleAge24() {

		double expected = -6.0862 + -0.57665;

		PersonBuilder person = makePerson(24, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);

 		assertEquals(expected, flinkster.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMaleFulltimeAge30() {

		double expected = -6.0862 + 0.9263 + 1.1547;

		PersonBuilder person = makePerson(30, 
																				Employment.FULLTIME, 
																				Gender.MALE,
																				false
																				);

 		assertEquals(expected, flinkster.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testFlinksterFemaleFulltimeAge30() {

		double expected = -6.0862 + -1.8967 + 0.9263 + 1.1547;

		PersonBuilder person = makePerson(30, 
																				Employment.FULLTIME, 
																				Gender.FEMALE,
																				false
																				);

 		assertEquals(expected, flinkster.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMaleHouseholdCars() {

		double expected = -6.0862;

		PersonBuilder person = makePerson(50, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);

		HouseholdForSetup household = makeHousehold(5,5);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + 2.1474;

		household = makeHousehold(5,0);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -0.27;

		household = makeHousehold(5,1);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMaleHouseholdPersons() {

		double expected = -6.0862;

		PersonBuilder person = makePerson(50, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);

		HouseholdForSetup household = makeHousehold(5,5);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.5638;

		household = makeHousehold(1,5);

 		assertEquals(1, household.nominalSize());
 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -0.40315;

		household = makeHousehold(2,5);

 		assertEquals(2, household.nominalSize());
 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + 0.1005;

		household = makeHousehold(3,5);

 		assertEquals(3, household.nominalSize());
 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + 0.4201;

		household = makeHousehold(4,5);

 		assertEquals(4, household.nominalSize());
	}

	@Test
	public void testFlinksterMaleHouseholdSize1() {

		PersonBuilder person = makePerson(50, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);


		double expected = -6.0862 + -1.5638;

		household = makeHousehold(1,5);

		household.addPerson(person);

 		assertEquals(1, household.nominalSize());
 		assertEquals(1, household.getSize());
 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMaleHouseholdSize2() {

		PersonBuilder person = makePerson(50, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);

		double expected = -6.0862 + -0.40315;

		household = makeHousehold(2,5);

		household.addPerson(person);

 		assertEquals(2, household.getSize());
 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testFlinksterFemaleAge() {

		double expected = -6.0862 + -1.8967 + 0.6756;

		PersonBuilder person = makePerson(90, 
																				Employment.NONE, 
																				Gender.FEMALE,
																				false
																				);

		HouseholdForSetup household = makeHousehold(5,5);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665;

		person = makePerson(18, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);

		person = makePerson(24, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + 1.1547;

		person = makePerson(25, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);

		person = makePerson(34, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + 0.55032;

		person = makePerson(35, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);

		person = makePerson(49, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + 0.0;

		person = makePerson(50, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);

		person = makePerson(64, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + 0.6756;

		person = makePerson(65, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);

		person = makePerson(80, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMaleHouseholdSize2Ticket() {

		PersonBuilder person = makePerson(50, 
																				Employment.NONE, 
																				Gender.MALE,
																				true
																				);

		double expected = -6.0862 + -0.40315 + 0.1095;

		household = makeHousehold(2,5);

		household.addPerson(person);

 		assertEquals(2, household.nominalSize());
 		assertEquals(2, household.getSize());
 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testFlinksterFemaleEmployment() {

		double expected = -6.0862 + -1.8967 + -0.57665;

		PersonBuilder person = makePerson(20, 
																				Employment.NONE, 
																				Gender.FEMALE,
																				false
																				);

		HouseholdForSetup household = makeHousehold(5,5);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665 + 0.9263;

		person = makePerson(20, Employment.FULLTIME, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665 + 1.1843;

		person = makePerson(20, Employment.PARTTIME, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665 + -2.1083;

		person = makePerson(20, Employment.UNEMPLOYED, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665 + 0.0458;

		person = makePerson(20, Employment.STUDENT_SECONDARY, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665 + 0.6085;

		person = makePerson(20, Employment.STUDENT_TERTIARY, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665 + -0.8818;

		person = makePerson(20, Employment.EDUCATION, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665 + -0.9886;

		person = makePerson(20, Employment.HOMEKEEPER, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665 + -1.0008;

		person = makePerson(20, Employment.RETIRED, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testReal1() {

		PersonBuilder person = makePerson(46, 
																				Employment.FULLTIME,
																				Gender.MALE,
																				false
																				);

		double expected = -6.0862 + 0.9263 + 0.55032 + 0.0 + -0.27;

		household = makeHousehold(5,1);

 		assertEquals(5, household.nominalSize());
 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testFlinkster() {
		double expected_male = -6.0862 + 0.55032 + 2.1474 + 0.4201 + 0.9263;

		assertEquals(expected_male, flinkster.calculateUtility(male_working, household, zone()), EPSILON);
	}

}

