package edu.kit.ifv.mobitopp.simulation.tour;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityPeriod;
import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.ActivitySequenceAsLinkedList;
import edu.kit.ifv.mobitopp.time.Time;

public class TourTest {
	

	Time hour0 = Data.someTime();
	Time hour7 = hour0.plusHours(7);
	Time hour8 = hour0.plusHours(8);
	Time hour13 = hour0.plusHours(13);
	Time hour17 = hour0.plusHours(17);
	Time hour19 = hour0.plusHours(19);
	Time hour20 = hour0.plusHours(20);
	Time hour22 = hour0.plusHours(22);
	
	TourAwareActivitySchedule schedule;
	TourAwareActivitySchedule scheduleWork;
	TourAwareActivitySchedule scheduleEducation;
	TourAwareActivitySchedule scheduleService;
	TourAwareActivitySchedule scheduleShopping;
	TourAwareActivitySchedule scheduleLeisure;
	
	ActivityIfc homeActivityBefore;
	ActivityIfc firstActivity;
	ActivityIfc secondActivity;
	ActivityIfc thirdActivity;
	ActivityIfc fourthActivity;
	
	@Before
	public void setUp() {
		
		schedule = new ActivityPeriod(new DefaultTourFactory());
		
		homeActivityBefore = ActivitySequenceAsLinkedList.newActivity(0,0,hour0,ActivityType.HOME,7*60,30);
		
		firstActivity = ActivitySequenceAsLinkedList.newActivity(1,1,hour8,ActivityType.WORK,8*60,30);
		secondActivity = ActivitySequenceAsLinkedList.newActivity(2,2,hour17,ActivityType.HOME,2*60,30);
		thirdActivity = ActivitySequenceAsLinkedList.newActivity(3,3,hour19,ActivityType.LEISURE,1*60,15);
		fourthActivity = ActivitySequenceAsLinkedList.newActivity(4,4,hour20,ActivityType.HOME,4*60,15);
		
		schedule.addAsLastActivity(homeActivityBefore);
		schedule.addAsLastActivity(firstActivity);
		schedule.addAsLastActivity(secondActivity);
		schedule.addAsLastActivity(thirdActivity);
		schedule.addAsLastActivity(fourthActivity);

		scheduleWork = new ActivityPeriod(new DefaultTourFactory());
		
		scheduleWork.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(5,0,hour0,ActivityType.HOME,6*60,1)
		);
		scheduleWork.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(6,1,hour7,ActivityType.SERVICE,20,15)
		);
		scheduleWork.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(7,1,hour8,ActivityType.WORK,8*60,30)
		);
		scheduleWork.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(8,2,hour17,ActivityType.LEISURE,2*60,15)
		);
		scheduleWork.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(9,1,hour19,ActivityType.EDUCATION,3*60,15)
		);
		scheduleWork.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(10,3,hour22,ActivityType.HOME,4*60,30)
		);
		
		scheduleEducation = new ActivityPeriod(new DefaultTourFactory());
		scheduleEducation.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(11,0,hour0,ActivityType.HOME,6*60,1)
		);
		scheduleEducation.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(12,1,hour8,ActivityType.EDUCATION,3*60,15)
		);
		scheduleEducation.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(13,1,hour13,ActivityType.SERVICE,1*60,15)
		);
		scheduleEducation.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(14,2,hour17,ActivityType.LEISURE,4*60,15)
		);
		scheduleEducation.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(15,3,hour20,ActivityType.HOME,7*60,15)
		);
		
		scheduleService = new ActivityPeriod(new DefaultTourFactory());
		scheduleService.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(21,0,hour0,ActivityType.HOME,6*60,1)
		);
		scheduleService.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(22,1,hour8,ActivityType.SERVICE,1*60,15)
		);
		scheduleService.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(23,2,hour17,ActivityType.LEISURE,5*60,15)
		);
		scheduleService.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(24,3,hour20,ActivityType.HOME,6*60,15)
		);
		
		scheduleShopping = new ActivityPeriod(new DefaultTourFactory());
		scheduleShopping.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(31,0,hour0,ActivityType.HOME,6*60,1)
		);
		scheduleShopping.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(32,1,hour8,ActivityType.SHOPPING,5*60,15)
		);
		scheduleShopping.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(33,2,hour17,ActivityType.LEISURE,4*60,15)
		);
		scheduleShopping.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(34,3,hour20,ActivityType.HOME,8*60,15)
		);
		
		scheduleLeisure = new ActivityPeriod(new DefaultTourFactory());
		scheduleLeisure.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(41,0,hour0,ActivityType.HOME,6*60,1)
		);
		scheduleLeisure.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(42,1,hour8,ActivityType.SHOPPING,4*60,15)
		);
		scheduleLeisure.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(43,2,hour17,ActivityType.LEISURE,5*60,15)
		);
		scheduleLeisure.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(44,3,hour20,ActivityType.HOME,8*60,15)
		);
		
		
		
	}
	

	
	@Test
	public void testNumberOfTours() {
		assertEquals(2,schedule.numberOfTours());
	}
	
	@Test
	public void testStartOfFirstTour() {
		assertEquals(firstActivity,schedule.startOfFirstTour());
	}
	
	@Test
	public void testStartOfTour() {
		assertTrue(schedule.isStartOfTour(firstActivity));
		assertFalse(schedule.isStartOfTour(secondActivity));
		assertTrue(schedule.isStartOfTour(thirdActivity));
		assertFalse(schedule.isStartOfTour(fourthActivity));
	}
	
	
	@Test
	public void testNumberOfTrips() {
		Tour tour1 = schedule.correspondingTour(firstActivity);
		Tour tour2 = schedule.correspondingTour(thirdActivity);
		
		assertEquals(2, tour1.numberOfTrips());
		assertEquals(2, tour2.numberOfTrips());
	}
	
	@Test
	public void testFirstTour() {
		Tour tour1 = schedule.correspondingTour(firstActivity);
		
		assertEquals(tour1, schedule.firstTour());
	}


	@Test
	public void testCorrespondingTour() {
		
		Tour tour1 = schedule.correspondingTour(firstActivity);
		Tour tour2 = schedule.correspondingTour(secondActivity);
		Tour tour3 = schedule.correspondingTour(thirdActivity);
		Tour tour4 = schedule.correspondingTour(fourthActivity);

		assertEquals(tour1, tour2);
		assertEquals(tour2, tour1);
		
		assertEquals(tour3, tour4);
		assertEquals(tour4, tour3);
	}

	
	@Test
	public void testTourFirstLast() {
		
		Tour tour1 = schedule.correspondingTour(firstActivity);
		Tour tour2 = schedule.correspondingTour(thirdActivity);

		assertSame(firstActivity, tour1.firstActivity());
		assertSame(secondActivity, tour1.lastActivity());
		assertSame(thirdActivity, tour2.firstActivity());
		assertSame(fourthActivity, tour2.lastActivity());
	}
	
	@Test
	public void testPurposeSimple() {
		
		Tour tour1 = schedule.correspondingTour(firstActivity);
		Tour tour2 = schedule.correspondingTour(thirdActivity);
		
		assertEquals(ActivityType.WORK,tour1.purpose());
		assertEquals(ActivityType.LEISURE,tour2.purpose());
	}
	
	@Test
	public void testPurposeWork() {
		
		Tour tour = scheduleWork.firstTour();
		
		assertEquals(ActivityType.WORK,tour.purpose());
	}
	
	@Test
	public void testPurposeEdu() {
		
		Tour tour = scheduleEducation.firstTour();
		
		assertEquals(ActivityType.EDUCATION,tour.purpose());
	}
	
	@Test
	public void testPurposeService() {
		
		Tour tour = scheduleService.firstTour();
		
		assertEquals(ActivityType.SERVICE,tour.purpose());
	}
	
	@Test
	public void testPurposeDuration() {
		
		Tour tourShopping = scheduleShopping.firstTour();
		Tour tourLeisure = scheduleLeisure.firstTour();
		
		assertEquals(ActivityType.SHOPPING,tourShopping.purpose());
		assertEquals(ActivityType.LEISURE,tourLeisure.purpose());
	}
	
	@Test
	public void testContainsActivity() {
		
		Tour tour1 = schedule.correspondingTour(firstActivity);
		Tour tour2 = schedule.correspondingTour(thirdActivity);
		
		assertTrue(tour1.containsActivityOf(ActivityType.WORK));
		assertFalse(tour1.containsActivityOf(ActivityType.EDUCATION));
		assertFalse(tour1.containsActivityOf(ActivityType.LEISURE));
		assertTrue(tour1.containsActivityOf(ActivityType.HOME));
		
		
		assertTrue(tour2.containsActivityOf(ActivityType.LEISURE));
		assertFalse(tour2.containsActivityOf(ActivityType.WORK));
		assertTrue(tour2.containsActivityOf(ActivityType.HOME));
	}
	
	@Test
	public void testMainActivitySimple() {
		
		Tour tour1 = schedule.correspondingTour(firstActivity);
		Tour tour2 = schedule.correspondingTour(thirdActivity);
		
		assertEquals(firstActivity,tour1.mainActivity());
		assertEquals(thirdActivity,tour2.mainActivity());
	}
	
	@Test
	public void testAsList() {
		
		List<ActivityIfc> tour1 = schedule.correspondingTour(firstActivity).asList();
		List<ActivityIfc> tour2 = schedule.correspondingTour(thirdActivity).asList();
		
		assertEquals(2,tour1.size());
		assertEquals(2,tour2.size());
		
		assertEquals(firstActivity,tour1.get(0));
		assertEquals(secondActivity,tour1.get(1));
		assertEquals(thirdActivity,tour2.get(0));
		assertEquals(fourthActivity,tour2.get(1));
	}
	
	@Test
	public void testPreviousTour() {
		
		Tour tour1 = schedule.correspondingTour(firstActivity);
		Tour tour2 = schedule.correspondingTour(thirdActivity);
		
		assertFalse(tour1.hasPreviousTour());
		assertTrue(tour2.hasPreviousTour());
		
		assertEquals(tour1,tour2.previousTour());
	}
	
	@Test
	public void testTourNumber() {
		
		Tour tour1 = schedule.correspondingTour(firstActivity);
		Tour tour2 = schedule.correspondingTour(thirdActivity);
		
		assertEquals(1,tour1.tourNumber());
		assertEquals(2,tour2.tourNumber());
	}
	
	@Test
	public void hasNextActivityOf() {
		
		Tour tour1 = schedule.correspondingTour(firstActivity);
		Tour tour2 = schedule.correspondingTour(thirdActivity);
		
		assertTrue(tour1.containsActivityOf(ActivityType.WORK));
		assertTrue(tour1.containsActivityOf(ActivityType.HOME));
		assertTrue(tour2.containsActivityOf(ActivityType.LEISURE));
		assertTrue(tour2.containsActivityOf(ActivityType.HOME));
		
		assertFalse(tour1.hasNextActivityOf(ActivityType.WORK, firstActivity));
		assertTrue(tour1.hasNextActivityOf(ActivityType.HOME, firstActivity));
		assertFalse(tour2.hasNextActivityOf(ActivityType.LEISURE, thirdActivity));
		assertTrue(tour2.hasNextActivityOf(ActivityType.HOME, thirdActivity));
	}
	
	@Test
	public void nextActivityOf() {
		
		Tour tour1 = schedule.correspondingTour(firstActivity);
		Tour tour2 = schedule.correspondingTour(thirdActivity);
		
		assertTrue(tour1.containsActivityOf(ActivityType.WORK));
		assertTrue(tour1.containsActivityOf(ActivityType.HOME));
		assertTrue(tour2.containsActivityOf(ActivityType.LEISURE));
		assertTrue(tour2.containsActivityOf(ActivityType.HOME));
		
		assertEquals(secondActivity, tour1.nextActivityOf(ActivityType.HOME, firstActivity).get());
		assertEquals(fourthActivity, tour2.nextActivityOf(ActivityType.HOME, thirdActivity).get());
	}
	
	@Test
	public void firstActivityOf() {
		
		Tour tour1 = schedule.correspondingTour(firstActivity);
		Tour tour2 = schedule.correspondingTour(thirdActivity);
		
		assertTrue(tour1.containsActivityOf(ActivityType.WORK));
		assertTrue(tour1.containsActivityOf(ActivityType.HOME));
		assertTrue(tour2.containsActivityOf(ActivityType.LEISURE));
		assertTrue(tour2.containsActivityOf(ActivityType.HOME));
		
		assertEquals(firstActivity, tour1.firstActivityOf(ActivityType.WORK).get());
		assertEquals(secondActivity, tour1.firstActivityOf(ActivityType.HOME).get());
		assertEquals(thirdActivity, tour2.firstActivityOf(ActivityType.LEISURE).get());
		assertEquals(fourthActivity, tour2.firstActivityOf(ActivityType.HOME).get());
	}
	
	
}
