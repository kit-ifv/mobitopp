package edu.kit.ifv.mobitopp.simulation.activityschedule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.Activity;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityPeriod;
import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.ActivityAsLinkedListElement;
import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.LinkedListElement;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityDurationNoRandomizer;
import edu.kit.ifv.mobitopp.time.Time;


public class ActivityPeriodTest {

	protected ActivityPeriod week;

	protected	Activity activity1;
	protected	Activity activity2;
	protected Activity activity3;
	protected Activity activity4;

	protected Activity activity3_conflict;

	protected	Activity nextDay_activity1;
	protected	Activity nextDay_activity2;

	private Time firstDay;

	private Time secondDay;

	private Activity makeActivity(
		int oid,
		Time startDate,
		ActivityType activityType,
		int activityNr,
		int duration,
		int tripDuration
	) {

		Activity act = 	new ActivityAsLinkedListElement(
											oid,
											(byte)activityNr,
											activityType,
											startDate,
											(short)duration,
											(short)tripDuration
										);

		return act;
	}



	@Before
	public void setUp() {

		week = new ActivityPeriod(new ActivityDurationNoRandomizer(), new PatternActivityWeek(),new ArrayList<Time>());

		firstDay = Data.someTime();
		activity1 = makeActivity(1, firstDay, ActivityType.HOME, 1, 6*60, 1);
		activity2 = makeActivity(2, firstDay.plusHours(7), ActivityType.WORK, 2, 8*60, 30);
		activity3 = makeActivity(3, firstDay.plusHours(18), ActivityType.SHOPPING, 3, 1*60, 15);
		activity4 = makeActivity(4, firstDay.plusHours(20), ActivityType.HOME, 4, 11*60, 15);

		activity3_conflict = makeActivity(5, firstDay.plusHours(18), ActivityType.SHOPPING, 3, 3*60, 15);

		secondDay = firstDay.nextDay();
		nextDay_activity1 = makeActivity(6, secondDay, ActivityType.HOME, 1, 6*60, 1);
		nextDay_activity2 = makeActivity(7, secondDay.plusHours(7), ActivityType.WORK, 2, 8*60, 30);
	}


	@Test
	public void testIsEmpty() {
		
		assertTrue(week.isEmpty());
		
		week.addToFront((LinkedListElement)activity1);
		
		assertFalse(week.isEmpty());
	}
	
	@Test
	public void testContains() {
		
		assertFalse(week.contains(activity1));
		assertFalse(week.contains(activity2));
		
		week.addToFront((LinkedListElement)activity1);
		
		assertTrue(week.contains(activity1));
		assertFalse(week.contains(activity2));
		
		week.addToBack((LinkedListElement)activity2);
		
		assertTrue(week.contains(activity1));
		assertTrue(week.contains(activity2));
	}
	
	@Test
	public void testInBetween() {
		
		week.addToBack((LinkedListElement)activity1);
		week.addToBack((LinkedListElement)activity2);
		week.addToBack((LinkedListElement)activity3);
		
		List<ActivityIfc> between = week.activitiesBetween(activity1,activity3);
		
		assertEquals(1, between.size());
		assertEquals(activity2, between.get(0));
	}
	
	@Test
	public void testAddToFront() {

		week.addToFront((LinkedListElement)activity1);

		assertEquals("failure - first activity wrong", activity1, week.first());
		assertEquals("failure - last activity wrong", activity1, week.last());

		week.addToFront((LinkedListElement)activity2);

		assertEquals("failure - first activity wrong", activity2, week.first());
		assertEquals("failure - last activity wrong", activity1, week.last());

		week.addToFront((LinkedListElement)activity3);

		assertEquals("failure - first activity wrong", activity3, week.first());
		assertEquals("failure - last activity wrong", activity1, week.last());

	}

	@Test
	public void testAddToBack() {

		week.addToBack((LinkedListElement)activity1);

		assertEquals("failure - first activity wrong", activity1, week.first());
		assertEquals("failure - last activity wrong", activity1, week.last());

		week.addToBack((LinkedListElement)activity2);

		assertEquals("failure - first activity wrong", activity1, week.first());
		assertEquals("failure - last activity wrong", activity2, week.last());

		week.addToBack((LinkedListElement)activity3);

		assertEquals("failure - first activity wrong", activity1, week.first());
		assertEquals("failure - last activity wrong", activity3, week.last());

		assertEquals("failure - first activity wrong", activity1, week.firstActivity());
		assertEquals("failure - last activity wrong", activity3, week.lastActivity());

	}

	@Test
	public void testAddActivity() {

		week.addAsLastActivity(activity1);

		assertEquals("failure - first activity wrong", activity1, week.firstActivity());
		assertEquals("failure - last activity wrong", activity1, week.lastActivity());

		week.addAsLastActivity(activity2);

		assertEquals("failure - first activity wrong", activity1, week.firstActivity());
		assertEquals("failure - last activity wrong", activity2, week.lastActivity());

		week.addAsLastActivity(activity3);

		assertEquals("failure - first activity wrong", activity1, week.firstActivity());
		assertEquals("failure - last activity wrong", activity3, week.lastActivity());
	}

	@Test
	public void testInsertOccupationAfter() {

		week.addAsLastActivity(activity1);
		week.insertAfter(activity1, activity2);

		assertEquals("failure - first activity wrong", activity1, week.firstActivity());
		assertEquals("failure - last activity wrong", activity2, week.lastActivity());

		week.insertAfter(activity2, activity3);

		assertEquals("failure - first activity wrong", activity1, week.firstActivity());
		assertEquals("failure - last activity wrong", activity3, week.lastActivity());
	}

	@Test
	public void testInsertOccupationAfter2() {

		week.addAsLastActivity(activity1);
		week.insertAfter(activity1, activity4);

		assertEquals("failure - first activity wrong", activity1, week.firstActivity());
		assertEquals("failure - last activity wrong", activity4, week.lastActivity());

		week.insertAfter(activity1, activity3);

		assertEquals("failure - first activity wrong", activity1, week.firstActivity());
		assertEquals("failure - last activity wrong", activity4, week.lastActivity());

		week.insertAfter(activity1, activity2);

		assertEquals("failure - first activity wrong", activity1, week.firstActivity());
		assertEquals("failure - last activity wrong", activity4, week.lastActivity());
	}

	@Test
	public void testNextPrev() {

		week.addAsLastActivity(activity1);
		week.insertAfter(activity1, activity4);

		assertEquals("failure - prevActivity() wrong", activity1, week.prevActivity(activity4));
		assertEquals("failure - nextActivity() wrong", activity4, week.nextActivity(activity1));

		week.insertAfter(activity1, activity3);

		assertEquals("failure - prevActivity() wrong", activity1, week.prevActivity(activity3));
		assertEquals("failure - prevActivity() wrong", activity3, week.prevActivity(activity4));
		assertEquals("failure - nextActivity() wrong", activity4, week.nextActivity(activity3));
		assertEquals("failure - nextActivity() wrong", activity3, week.nextActivity(activity1));

		week.insertAfter(activity1, activity2);

		assertEquals("failure - prevActivity() wrong", activity1, week.prevActivity(activity2));
		assertEquals("failure - prevActivity() wrong", activity2, week.prevActivity(activity3));
		assertEquals("failure - prevActivity() wrong", activity3, week.prevActivity(activity4));
		assertEquals("failure - nextActivity() wrong", activity4, week.nextActivity(activity3));
		assertEquals("failure - nextActivity() wrong", activity3, week.nextActivity(activity2));
		assertEquals("failure - nextActivity() wrong", activity2, week.nextActivity(activity1));
	}



	@Test
	public void testRemove() {

		week.addAsLastActivity(activity1);
		week.insertAfter(activity1, activity2);
		week.insertAfter(activity2, activity3);
		week.insertAfter(activity3, activity4);

		assertEquals("failure - first activity wrong", activity1, week.firstActivity());

		week.removeActivity(activity1);	
		assertEquals("failure - first activity wrong", activity2, week.firstActivity());
		assertEquals("failure - last activity wrong", activity4, week.lastActivity());

		week.removeActivity(activity4);	
		assertEquals("failure - last activity wrong", activity3, week.lastActivity());
	}


	@Test
	public void testOrderInvariant() {

		week.addAsLastActivity(activity1);
		week.insertAfter(activity1, activity2);
		week.insertAfter(activity2, activity3);
		week.insertAfter(activity3, activity4);

		assertTrue("failure - checkOrder()", week.checkOrderInvariant());
	}

	@Test
	public void testOrderInvariantFalse() {

		week.addAsLastActivity(activity1);
		week.insertAfter(activity1, activity2);
		week.insertAfter(activity2, activity3);
		week.insertAfter(activity2, activity4);

		assertFalse("failure - checkOrder()", week.checkOrderInvariant());

		week.fixStartTimeOfActivities();

		assertTrue("failure - checkOrder()", week.checkOrderInvariant());
	}

	@Test
	public void testNonOverlappingInvariant() {

		week.addAsLastActivity(activity1);
		week.insertAfter(activity1, activity2);
		week.insertAfter(activity2, activity3);
		week.insertAfter(activity3, activity4);

		assertTrue("failure - checkOrder()", week.checkNonOverlappingInvariant());
	}

	@Test
	public void testNonOverlappingInvariantFalse() {

		week.addAsLastActivity(activity1);
		week.insertAfter(activity1, activity2);
		week.insertAfter(activity2, activity3_conflict);
		week.insertAfter(activity3_conflict, activity4);

		assertFalse("failure - checkOrder()", week.checkNonOverlappingInvariant());

		week.fixStartTimeOfActivities();

		assertTrue("failure - checkOrder()", week.checkNonOverlappingInvariant());
	}

	@Test
	public void testFixStartTimeOfActivities() {

		Activity act1 = makeActivity(10, firstDay, ActivityType.HOME, 1, 6*60, 1);
		Activity act2 = makeActivity(11, firstDay.plusHours(12), ActivityType.SHOPPING, 3, 2*60, 15);
		Activity act3 = makeActivity(12, firstDay.plusHours(11), ActivityType.WORK, 2, 3*60, 30);

		week.addAsLastActivity(act1);
		week.insertAfter(act1, act2);
		week.insertAfter(act2, act3);

		assertEquals("failure - fixStartTimeOfActivities()", 11, act3.startDate().getHour());
		assertEquals("failure - fixStartTimeOfActivities()", 14, act3.calculatePlannedEndDate().getHour());

		week.fixStartTimeOfActivities();

		assertEquals("failure - fixStartTimeOfActivities()", 0, act1.startDate().getHour());
		assertEquals("failure - fixStartTimeOfActivities()", 6, act1.calculatePlannedEndDate().getHour());

		assertEquals("failure - fixStartTimeOfActivities()", 12, act2.startDate().getHour());
		assertEquals("failure - fixStartTimeOfActivities()", 14, act2.calculatePlannedEndDate().getHour());

		assertEquals("failure - fixStartTimeOfActivities()", 14, act3.startDate().getHour());
		assertEquals("failure - fixStartTimeOfActivities()", 30, act3.startDate().getMinute());
		assertEquals("failure - fixStartTimeOfActivities()", 3*60, act3.duration());
		assertEquals("failure - fixStartTimeOfActivities()", 17, act3.calculatePlannedEndDate().getHour());
		assertEquals("failure - fixStartTimeOfActivities()", 30, act3.calculatePlannedEndDate().getMinute());
	}







}
