package edu.kit.ifv.mobitopp.simulation.tour;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityPeriod;
import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.ActivitySequenceAsLinkedList;
import edu.kit.ifv.mobitopp.time.Time;

public class SimpleTourBasedModeChoiceModelTest {
	
	TourAwareActivitySchedule schedule;
	
	Time hour8 = Data.someTime().plusHours(8);
	Time hour17 = hour8.plusHours(9);
	
	ActivityIfc firstActivity;
	ActivityIfc secondActivity;
	
	TourOnlyModeChoiceModel model;
	Set<Mode> binaryChoiceSet;
	
	Tour tour;
	Person person; 
	Map<Mode, Double> preferences;
	Collection<Mode> choiceSet;
	double randomNumber;

	
	@Before
	public void setUp() {
		
		model = new DefaultTourModeChoiceModel(new PreferenceOnlyUtilityFunction());
		
		binaryChoiceSet = new LinkedHashSet<Mode>();
		binaryChoiceSet.add(Mode.CAR);
		binaryChoiceSet.add(Mode.PUBLICTRANSPORT);
		
		schedule = new ActivityPeriod(new DefaultTourFactory());
		
		firstActivity = ActivitySequenceAsLinkedList.newActivity(1,1,hour8,ActivityType.WORK,8*60,30);
		secondActivity = ActivitySequenceAsLinkedList.newActivity(2,2,hour17,ActivityType.HOME,2*60,30);		
		
		schedule.addAsLastActivity(firstActivity);
		schedule.addAsLastActivity(secondActivity);
		
		tour = schedule.firstTour();
		
		
	}
	
	@Test
	public void testTour() {
		assertEquals(firstActivity, tour.firstActivity());
		assertEquals(secondActivity, tour.lastActivity());
	}
	
	@Test
	public void testSingleModeChoiceSet() {
		
		Mode chosen0 = model.selectMode(null, null, Mode.MODESET_CAR_ONLY, 0.0);
		Mode chosen1 = model.selectMode(null, null, Mode.MODESET_CAR_ONLY, 1.0);
		
		assertEquals(Mode.CAR, chosen0);
		assertEquals(Mode.CAR, chosen1);
	}

	@Test(expected = AssertionError.class)
	public void testInvalidRandomNumberTooHigh() {
		
		model.selectMode(null, null, Mode.MODESET_CAR_ONLY, 2.0);
	}
	
	@Test(expected = AssertionError.class)
	public void testInvalidRandomNumberTooLow() {
		
		model.selectMode(null, null, Mode.MODESET_CAR_ONLY, -0.1);
	}
	
	@Test
	public void testBinaryModeChoiceEqualProb() {

		Map<Mode,Double> utilities = new LinkedHashMap<Mode,Double>();
		utilities.put(Mode.CAR, 0.1);
		utilities.put(Mode.PUBLICTRANSPORT, 0.1);
		utilities.put(Mode.BIKE, 1.0);
		
		Mode chosen0 = model.selectMode(null, null, binaryChoiceSet, 0.49);
		Mode chosen1 = model.selectMode(null, null, binaryChoiceSet, 0.51);
		
		assertEquals(Mode.CAR, chosen0);
		assertEquals(Mode.PUBLICTRANSPORT, chosen1);
	}
}
