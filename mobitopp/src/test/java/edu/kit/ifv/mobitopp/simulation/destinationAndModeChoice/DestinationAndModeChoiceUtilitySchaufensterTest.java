package edu.kit.ifv.mobitopp.simulation.destinationAndModeChoice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.MockedZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Household_Stub;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.Person_Stub;
import edu.kit.ifv.mobitopp.simulation.activityschedule.Activity;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class DestinationAndModeChoiceUtilitySchaufensterTest {

	static final double EPSILON = 0.00001;

	DestinationAndModeChoiceUtilitySchaufenster utility;
	ImpedanceIfc impedance;

	Set<Mode> modes;

	Time date1 = Data.someTime().plusHours(6);
	Time date2 = date1.plusHours(2);

	Activity activity1 = new Activity(1, (byte)1, ActivityType.WORK, date1, (short)120, (short)5);
	Activity activity2 = new Activity(2, (byte)2, ActivityType.HOME, date2, (short)120, (short)5);
	Activity homeActivity = activity2;

	Household household = new Household_Stub(1, 1000);
	Person person = new EmobilityPerson(new Person_Stub(1,household, null, homeActivity, true), 
																			1.0f,
																			EmobilityPerson.PublicChargingInfluencesDestinationChoice.NEVER,
																			new HashMap<String, Boolean>());

	Person person_noTicket = new EmobilityPerson(new Person_Stub(2,household, null, homeActivity, false), 
																			1.0f, 
																			EmobilityPerson.PublicChargingInfluencesDestinationChoice.NEVER,
																			new HashMap<String, Boolean>());

	private MockedZones zones;

	@Before
	public void setUp() throws URISyntaxException {
		impedance = new Impedance_Stub(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f);
		utility = new DestinationAndModeChoiceUtilitySchaufenster(impedance, parameterFile());

		modes = new TreeSet<Mode>();
		modes.add(Mode.PEDESTRIAN);

		activity1.setMode(Mode.PEDESTRIAN);
		zones = MockedZones.create();
	}
	
	private String parameterFile() throws URISyntaxException {
		return new File(getClass().getResource("DestinationAndModeChoiceParameterSchaufensterDummy.txt").toURI()).getAbsolutePath();
	}

	@After
	public void verifyZones() {
		zones.verify();
	}
	
	private Zone someZone() {
		return zones.someZone();
	}

	private Zone otherZone() {
		return zones.otherZone();
	}

	@Test
	public void testConstructor() {

		assertNotNull(utility);

		assertEquals(1.00, utility.VM1, EPSILON);
	}

	@Test
	public void testImpedance() {

		assertNotNull(impedance);

		assertEquals(1.0, impedance.getTravelTime(-1,-1,null,null), EPSILON);
		assertEquals(2.0, impedance.getTravelCost(-1,-1,null,null), EPSILON);
		assertEquals(3.0, impedance.getDistance(-1,-1), EPSILON);
		assertEquals(4.0, impedance.getParkingCost(-1,null), EPSILON);
		assertEquals(5.0, impedance.getParkingStress(-1,null), EPSILON);
	}

	@Test
	public void testUtilityMode() {

		assertNotNull(utility);

		Map<Mode,Double> result = utility.calculateUtilitiesForLowerModel(modes, person, activity1, activity2, 
																																				someZone(), otherZone());

		assertEquals(1, result.size());
		assertTrue(result.containsKey(Mode.PEDESTRIAN));

		assertEquals(1.00, result.get(Mode.PEDESTRIAN), EPSILON);
	}

	@Test
	public void testUtilityPublicTransport() {

		assertNotNull(utility);
		assertNotNull(impedance);

		assertTrue(person.hasCommuterTicket());
		assertFalse(person_noTicket.hasCommuterTicket());

		assertEquals(2.0, impedance.getTravelCost(0,0, Mode.PUBLICTRANSPORT, (Time)null), EPSILON);

		modes.add(Mode.PUBLICTRANSPORT);

		assertEquals(2, modes.size());

		Map<Mode,Double> result = utility.calculateUtilitiesForLowerModel(modes, person, activity1, activity2, 
																																				someZone(), otherZone());

		Map<Mode,Double> result_noTicket = utility.calculateUtilitiesForLowerModel(modes, person_noTicket, 
																																								activity1, activity2, 
																																							someZone(), otherZone());

		assertEquals(2, result.size());
		assertEquals(2, result_noTicket.size());

		assertTrue(result.containsKey(Mode.PEDESTRIAN));
		assertTrue(result.containsKey(Mode.PUBLICTRANSPORT));

		assertTrue(result_noTicket.containsKey(Mode.PEDESTRIAN));
		assertTrue(result_noTicket.containsKey(Mode.PUBLICTRANSPORT));

		assertEquals(1.00, result.get(Mode.PEDESTRIAN), EPSILON);
		assertEquals(1.00, result_noTicket.get(Mode.PEDESTRIAN), EPSILON);

		assertEquals(2.50, result.get(Mode.PUBLICTRANSPORT), EPSILON);
		assertEquals(-2.50, result_noTicket.get(Mode.PUBLICTRANSPORT), EPSILON);
	}

}



