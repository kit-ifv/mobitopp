package edu.kit.ifv.mobitopp.simulation.tour;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityPeriod;
import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.ActivitySequenceAsLinkedList;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class SubtourTest {
	

	Time hour0 =  SimpleTime.of(6,0,0,0);
	Time hour7 =  SimpleTime.of(6,7,0,0);
	Time hour8 =  SimpleTime.of(6,8,0,0);
	Time hour12 = SimpleTime.of(6,12,0,0);
	Time hour13 = SimpleTime.of(6,13,0,0);
	Time hour14 = SimpleTime.of(6,14,0,0);
	Time hour15 = SimpleTime.of(6,15,0,0);
	Time hour16 = SimpleTime.of(6,16,0,0);
	Time hour17 = SimpleTime.of(6,17,0,0);
	Time hour18 = SimpleTime.of(6,18,0,0);
	Time hour19 = SimpleTime.of(6,19,0,0);
	Time hour20 = SimpleTime.of(6,20,0,0);
	Time hour22 = SimpleTime.of(6,22,0,0);
	
	TourAwareActivitySchedule scheduleWithoutSubtour;
	TourAwareActivitySchedule scheduleWithSubtourFromWork;
	TourAwareActivitySchedule scheduleWithSubtourFromEducation;
	TourAwareActivitySchedule scheduleWithSubtourFromService;
	TourAwareActivitySchedule scheduleWithSubtourFromVisit;
	TourAwareActivitySchedule scheduleWithTwoLeisureActivities;
	TourAwareActivitySchedule scheduleWithTwoServiceActivities;
	TourAwareActivitySchedule scheduleWithTwoSubtoursFromWork;
	TourAwareActivitySchedule scheduleWithSubtourFromWorkThreeActivities;
	
	ActivityIfc firstActivity;
	ActivityIfc secondActivity;
	
	ActivityIfc leisureFromWork;
	ActivityIfc shoppingFromEducation;
	ActivityIfc shoppingFromService;
	ActivityIfc leisureFromVisit;

	ActivityIfc privatebusinessFromWork;
	ActivityIfc shoppingFromWork;
	
	ActivityIfc firstLeisureActivity;
	ActivityIfc secondLeisureActivity;
	
	ActivityIfc subtourFirstActivity;
	ActivityIfc subtourSecondActivity;
	ActivityIfc subtourThirdActivity;
	
	ActivityIfc firstServiceActivity;
	ActivityIfc secondServiceActivity;
	
	@Before
	public void setUp() {
		
		
		scheduleWithoutSubtour 						=	new ActivityPeriod();
	
		scheduleWithoutSubtour.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(5,0,hour0,ActivityType.HOME,6*60,1)
		);
		scheduleWithoutSubtour.addAsLastActivity(
				firstActivity = ActivitySequenceAsLinkedList.newActivity(6,1,hour7,ActivityType.SERVICE,20,15)
		);
		scheduleWithoutSubtour.addAsLastActivity(
				secondActivity = ActivitySequenceAsLinkedList.newActivity(7,1,hour8,ActivityType.WORK,8*60,30)
		);
		scheduleWithoutSubtour.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(8,2,hour17,ActivityType.LEISURE,2*60,15)
		);
		scheduleWithoutSubtour.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(9,1,hour19,ActivityType.EDUCATION,3*60,15)
		);
		scheduleWithoutSubtour.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(10,3,hour22,ActivityType.HOME,4*60,30)
		);
		
		scheduleWithSubtourFromWork 			= new ActivityPeriod();
		
		scheduleWithSubtourFromWork.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(11,0,hour0,ActivityType.HOME,6*60,1)
		);
		scheduleWithSubtourFromWork.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(12,1,hour8,ActivityType.WORK,4*60,15)
		);
		scheduleWithSubtourFromWork.addAsLastActivity(
				leisureFromWork = ActivitySequenceAsLinkedList.newActivity(13,1,hour13,ActivityType.LEISURE_INDOOR,1*60,15)
		);
		scheduleWithSubtourFromWork.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(14,2,hour14,ActivityType.WORK,4*60,15)
		);
		scheduleWithSubtourFromWork.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(15,3,hour20,ActivityType.HOME,7*60,15)
		);
		
		scheduleWithSubtourFromEducation 	= new ActivityPeriod();
		
		scheduleWithSubtourFromEducation.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(21,0,hour0,ActivityType.HOME,6*60,1)
		);
		scheduleWithSubtourFromEducation.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(22,1,hour7,ActivityType.SERVICE,1*60,15)
		);
		scheduleWithSubtourFromEducation.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(23,1,hour8,ActivityType.EDUCATION,3*60,15)
		);
		scheduleWithSubtourFromEducation.addAsLastActivity(
				shoppingFromEducation = ActivitySequenceAsLinkedList.newActivity(24,2,hour13,ActivityType.SHOPPING,1*60,15)
		);
		scheduleWithSubtourFromEducation.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(25,1,hour8,ActivityType.EDUCATION,3*60,15)
		);
		scheduleWithSubtourFromEducation.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(26,3,hour20,ActivityType.HOME,6*60,15)
		);
		
		scheduleWithSubtourFromService 		= new ActivityPeriod();
		scheduleWithSubtourFromService.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(31,0,hour0,ActivityType.HOME,12*60,1)
		);
		scheduleWithSubtourFromService.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(32,1,hour13,ActivityType.SERVICE,5,15)
		);
		scheduleWithSubtourFromService.addAsLastActivity(
				shoppingFromService = ActivitySequenceAsLinkedList.newActivity(33,1,hour13,ActivityType.SHOPPING,40,15)
		);
		scheduleWithSubtourFromService.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(34,1,hour14,ActivityType.SERVICE,5,15)
		);
		scheduleWithSubtourFromService.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(35,3,hour20,ActivityType.HOME,8*60,15)
		);
		
		scheduleWithSubtourFromVisit 			= new ActivityPeriod();
		scheduleWithSubtourFromVisit.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(41,0,hour0,ActivityType.HOME,6*60,1)
		);
		scheduleWithSubtourFromVisit.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(42,1,hour8,ActivityType.PRIVATE_VISIT,4*60,15)
		);
		scheduleWithSubtourFromVisit.addAsLastActivity(
				leisureFromVisit = ActivitySequenceAsLinkedList.newActivity(43,2,hour13,ActivityType.LEISURE_WALK,1*60,15)
		);
		scheduleWithSubtourFromVisit.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(44,1,hour14,ActivityType.PRIVATE_VISIT,4*60,15)
		);
		scheduleWithSubtourFromVisit.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(45,3,hour17,ActivityType.HOME,8*60,15)
		);
		
		scheduleWithTwoLeisureActivities 	= new ActivityPeriod();
		
		scheduleWithTwoLeisureActivities.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(41,0,hour0,ActivityType.HOME,6*60,1)
		);
		scheduleWithTwoLeisureActivities.addAsLastActivity(
				firstLeisureActivity = ActivitySequenceAsLinkedList.newActivity(42,1,hour8,ActivityType.LEISURE_OUTDOOR,4*60,15)
		);
		scheduleWithTwoLeisureActivities.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(43,2,hour13,ActivityType.SHOPPING,1*60,15)
		);
		scheduleWithTwoLeisureActivities.addAsLastActivity(
				secondLeisureActivity = ActivitySequenceAsLinkedList.newActivity(44,1,hour14,ActivityType.LEISURE_OUTDOOR,4*60,15)
		);
		scheduleWithTwoLeisureActivities.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(45,1,hour14,ActivityType.PRIVATE_VISIT,4*60,15)
		);
		
		
		scheduleWithTwoSubtoursFromWork 		= new ActivityPeriod();
		
		scheduleWithTwoSubtoursFromWork.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(51,0,hour0,ActivityType.HOME,6*60,1)
		);
		scheduleWithTwoSubtoursFromWork.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(52,0,hour8,ActivityType.WORK,4*60,1)
		);
		scheduleWithTwoSubtoursFromWork.addAsLastActivity(
				privatebusinessFromWork = ActivitySequenceAsLinkedList.newActivity(53,0,hour12,ActivityType.PRIVATE_BUSINESS,1*60,1)
		);
		scheduleWithTwoSubtoursFromWork.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(54,0,hour13,ActivityType.WORK,2*60,1)
		);
		scheduleWithTwoSubtoursFromWork.addAsLastActivity(
				shoppingFromWork = ActivitySequenceAsLinkedList.newActivity(55,0,hour16,ActivityType.SHOPPING,1*60,1)
		);
		scheduleWithTwoSubtoursFromWork.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(56,0,hour18,ActivityType.WORK,2*60,1)
		);
		scheduleWithTwoSubtoursFromWork.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(57,0,hour20,ActivityType.HOME,6*60,1)
		);
		
		scheduleWithSubtourFromWorkThreeActivities 		= new ActivityPeriod();
		
		scheduleWithSubtourFromWorkThreeActivities.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(61,0,hour0,ActivityType.HOME,6*60,1)
		);
		scheduleWithSubtourFromWorkThreeActivities.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(62,0,hour8,ActivityType.WORK,4*60,1)
		);
		scheduleWithSubtourFromWorkThreeActivities.addAsLastActivity(
				subtourFirstActivity = ActivitySequenceAsLinkedList.newActivity(63,0,hour12,ActivityType.PRIVATE_BUSINESS,30,1)
		);
		scheduleWithSubtourFromWorkThreeActivities.addAsLastActivity(
				subtourSecondActivity = ActivitySequenceAsLinkedList.newActivity(64,0,hour13,ActivityType.LEISURE_INDOOR,1*60,1)
		);
		scheduleWithSubtourFromWorkThreeActivities.addAsLastActivity(
				subtourThirdActivity = ActivitySequenceAsLinkedList.newActivity(65,0,hour14,ActivityType.SHOPPING_DAILY,1*30,1)
		);
		scheduleWithSubtourFromWorkThreeActivities.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(66,0,hour15,ActivityType.WORK,4*60,1)
		);
		scheduleWithSubtourFromWorkThreeActivities.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(67,0,hour19,ActivityType.HOME,6*60,1)
		);
		
		scheduleWithTwoServiceActivities 	= new ActivityPeriod();
		
		scheduleWithTwoServiceActivities.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(71,0,hour0,ActivityType.HOME,13*60,1)
		);
		scheduleWithTwoServiceActivities.addAsLastActivity(
				firstServiceActivity = ActivitySequenceAsLinkedList.newActivity(72,1,hour14,ActivityType.SERVICE,30,15)
		);
		scheduleWithTwoServiceActivities.addAsLastActivity(
				secondServiceActivity = ActivitySequenceAsLinkedList.newActivity(73,1,hour15,ActivityType.SERVICE,30,15)
		);
		scheduleWithTwoServiceActivities.addAsLastActivity(
				ActivitySequenceAsLinkedList.newActivity(74,1,hour16,ActivityType.HOME,4*60,15)
		);
		
		
		
	}
	

	@Test
	public void privateVisit() {
		Tour tour = scheduleWithSubtourFromVisit.firstTour();
		assertEquals(ActivityType.PRIVATE_VISIT,tour.purpose());
		assertEquals(1,tour.numberOfSubtours());
	}
	
	@Test
	public void containsSubtour() {
		
		assertFalse("",scheduleWithoutSubtour.firstTour().containsSubtour());
		org.junit.Assert.assertTrue("st test",scheduleWithSubtourFromWork.firstTour().containsSubtour());
		org.junit.Assert.assertTrue(scheduleWithSubtourFromEducation.firstTour().containsSubtour());
		assertTrue(scheduleWithSubtourFromService.firstTour().containsSubtour());
		assertTrue(scheduleWithSubtourFromVisit.firstTour().containsSubtour());
		assertFalse(scheduleWithTwoLeisureActivities.firstTour().containsSubtour());
		assertTrue(scheduleWithTwoSubtoursFromWork.firstTour().containsSubtour());
		assertTrue(scheduleWithSubtourFromWorkThreeActivities.firstTour().containsSubtour());
		assertFalse(scheduleWithTwoServiceActivities.firstTour().containsSubtour());
	}
	
	@Test
	public void numberOfSubtours() {
		assertEquals(0,scheduleWithoutSubtour.firstTour().numberOfSubtours());
		assertEquals(1,scheduleWithSubtourFromWork.firstTour().numberOfSubtours());
		assertEquals(1,scheduleWithSubtourFromEducation.firstTour().numberOfSubtours());
		assertEquals(1,scheduleWithSubtourFromService.firstTour().numberOfSubtours());
		assertEquals(1,scheduleWithSubtourFromVisit.firstTour().numberOfSubtours());
		assertEquals(0,scheduleWithTwoLeisureActivities.firstTour().numberOfSubtours());
		assertEquals(2,scheduleWithTwoSubtoursFromWork.firstTour().numberOfSubtours());
		assertEquals(1,scheduleWithSubtourFromWorkThreeActivities.firstTour().numberOfSubtours());
		assertEquals(0,scheduleWithTwoServiceActivities.firstTour().numberOfSubtours());
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void scheduleWithoutSubtourNotFound() {
		
		Tour tour = scheduleWithoutSubtour.firstTour();
		
		tour.nthSubtour(0);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void scheduleWithoutTwoLeisureActivtiesSubtourNotFound() {
		
		Tour tour = scheduleWithTwoLeisureActivities.firstTour();
		
		tour.nthSubtour(0);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void scheduleWithoutTwoServiceActivtiesSubtourNotFound() {
		Tour tour = scheduleWithTwoServiceActivities.firstTour();
		
		tour.nthSubtour(1);
	}
	
	@Test
	public void nthSubtour() {
		Tour withSubtourFromWork = scheduleWithSubtourFromWork.firstTour();
		Tour withSubtourFromEducation = scheduleWithSubtourFromEducation.firstTour();
		Tour withSubtourFromService = scheduleWithSubtourFromService.firstTour();
		Tour withSubtourFromVisit = scheduleWithSubtourFromVisit.firstTour();
		
		assertTrue(withSubtourFromWork.containsSubtour());
		assertEquals(ActivityType.WORK,withSubtourFromWork.purpose());
		
		assertSame(withSubtourFromWork.toString(),
				leisureFromWork, withSubtourFromWork.nthSubtour(0).firstActivity());
		assertSame(shoppingFromEducation, withSubtourFromEducation.nthSubtour(0).firstActivity());
		assertSame(shoppingFromService, withSubtourFromService.nthSubtour(0).firstActivity());
		assertSame(leisureFromVisit, withSubtourFromVisit.nthSubtour(0).firstActivity());
	}
	
	
	@Test
	public void severalSubtours() {
		Tour tour = scheduleWithTwoSubtoursFromWork.firstTour();
		
		assertSame(privatebusinessFromWork, tour.nthSubtour(0).firstActivity());
		assertSame(shoppingFromWork, tour.nthSubtour(1).firstActivity());
	}
	
	@Test
	public void subtourWithSeveralActivities() {
		Tour tour = scheduleWithSubtourFromWorkThreeActivities.firstTour();
	
		assertTrue(tour.contains(subtourFirstActivity));
		assertTrue(tour.contains(subtourSecondActivity));
		assertTrue(tour.contains(subtourThirdActivity));
		assertEquals(subtourFirstActivity,tour.nthSubtour(0).firstActivity());
	}
	
	@Test
	public void numberOfTrips() {
		Tour withSubtourFromWork = scheduleWithSubtourFromWork.firstTour();
		Tour withSubtourFromEducation = scheduleWithSubtourFromEducation.firstTour();
		Tour withSubtourFromService = scheduleWithSubtourFromService.firstTour();
		Tour withSubtourFromVisit = scheduleWithSubtourFromVisit.firstTour();
		
		Tour withTwoSubtours = scheduleWithTwoSubtoursFromWork.firstTour();
		Tour withSubtourWithThreeActivities = scheduleWithSubtourFromWorkThreeActivities.firstTour();
		
		assertEquals(2, withSubtourFromWork.nthSubtour(0).numberOfTrips());
		assertEquals(2, withSubtourFromEducation.nthSubtour(0).numberOfTrips());
		assertEquals(2, withSubtourFromService.nthSubtour(0).numberOfTrips());
		assertEquals(2, withSubtourFromVisit.nthSubtour(0).numberOfTrips());
		
		assertEquals(2, withTwoSubtours.nthSubtour(0).numberOfTrips());
		assertEquals(2, withTwoSubtours.nthSubtour(1).numberOfTrips());
		
		assertEquals(4, withSubtourWithThreeActivities.nthSubtour(0).numberOfTrips());
	}
	
	@Test
	public void startOfSubtour() {
		Tour withSubtourFromWork = scheduleWithSubtourFromWork.firstTour();
		Tour withSubtourFromEducation = scheduleWithSubtourFromEducation.firstTour();
		Tour withSubtourFromService = scheduleWithSubtourFromService.firstTour();
		Tour withSubtourFromVisit = scheduleWithSubtourFromVisit.firstTour();
		
		Tour withTwoSubtours = scheduleWithTwoSubtoursFromWork.firstTour();
		Tour withSubtourWithThreeActivities = scheduleWithSubtourFromWorkThreeActivities.firstTour();
		
		assertTrue(withSubtourFromWork.isStartOfSubtour(leisureFromWork));
		assertTrue(withSubtourFromEducation.isStartOfSubtour(shoppingFromEducation));
		assertTrue(withSubtourFromService.isStartOfSubtour(shoppingFromService));
		assertTrue(withSubtourFromVisit.isStartOfSubtour(leisureFromVisit));
		
		assertTrue(withTwoSubtours.isStartOfSubtour(privatebusinessFromWork));
		assertTrue(withTwoSubtours.isStartOfSubtour(shoppingFromWork));
		
		assertTrue(withSubtourWithThreeActivities.isStartOfSubtour(subtourFirstActivity));
		assertFalse(withSubtourWithThreeActivities.isStartOfSubtour(subtourSecondActivity));
		assertFalse(withSubtourWithThreeActivities.isStartOfSubtour(subtourThirdActivity));
	}
	
	@Test
	public void endOfSubtour() {
		Tour withSubtourFromWork = scheduleWithSubtourFromWork.firstTour();
		Tour withSubtourFromEducation = scheduleWithSubtourFromEducation.firstTour();
		Tour withSubtourFromService = scheduleWithSubtourFromService.firstTour();
		Tour withSubtourFromVisit = scheduleWithSubtourFromVisit.firstTour();
		
		Tour withTwoSubtours = scheduleWithTwoSubtoursFromWork.firstTour();
		Tour withSubtourWithThreeActivities = scheduleWithSubtourFromWorkThreeActivities.firstTour();
		
		assertFalse(withSubtourFromWork.isEndOfSubtour(leisureFromWork));
		assertTrue(withSubtourFromWork.isEndOfSubtour(scheduleWithSubtourFromWork.nextActivity(leisureFromWork)));
		
		assertFalse(withSubtourFromEducation.isEndOfSubtour(shoppingFromEducation));
		assertTrue(withSubtourFromEducation.isEndOfSubtour(scheduleWithSubtourFromEducation.nextActivity(shoppingFromEducation)));
		
		assertFalse(withSubtourFromService.isEndOfSubtour(shoppingFromService));
		assertTrue(withSubtourFromService.isEndOfSubtour(scheduleWithSubtourFromService.nextActivity(shoppingFromService)));
		
		assertFalse(withSubtourFromVisit.isEndOfSubtour(leisureFromVisit));
		assertTrue(withSubtourFromVisit.isEndOfSubtour(scheduleWithSubtourFromVisit.nextActivity(leisureFromVisit)));
		
		assertFalse(withTwoSubtours.isEndOfSubtour(privatebusinessFromWork));
		assertTrue(withTwoSubtours.isEndOfSubtour(scheduleWithTwoSubtoursFromWork.nextActivity(privatebusinessFromWork)));
		
		assertFalse(withTwoSubtours.isEndOfSubtour(shoppingFromWork));
		assertTrue(withTwoSubtours.isEndOfSubtour(scheduleWithTwoSubtoursFromWork.nextActivity(shoppingFromWork)));
		
		assertFalse(withSubtourWithThreeActivities.isEndOfSubtour(subtourFirstActivity));
		assertFalse(withSubtourWithThreeActivities.isEndOfSubtour(subtourSecondActivity));
		assertFalse(withSubtourWithThreeActivities.isEndOfSubtour(subtourThirdActivity));
		assertTrue(withSubtourWithThreeActivities.isEndOfSubtour(scheduleWithSubtourFromWorkThreeActivities.nextActivity(subtourThirdActivity)));
	}
	
	@Test
	public void isInSubtour() {
		Tour withSubtourFromWork = scheduleWithSubtourFromWork.firstTour();
		Tour withSubtourFromEducation = scheduleWithSubtourFromEducation.firstTour();
		Tour withSubtourFromService = scheduleWithSubtourFromService.firstTour();
		Tour withSubtourFromVisit = scheduleWithSubtourFromVisit.firstTour();
		Tour withoutSubtour = scheduleWithoutSubtour.firstTour();
		Tour withTwoLeisureActivities = scheduleWithTwoLeisureActivities.firstTour();
		Tour withTwoServiceActivities = scheduleWithTwoServiceActivities.firstTour();
		
		Tour withTwoSubtours = scheduleWithTwoSubtoursFromWork.firstTour();
		Tour withSubtourWithThreeActivities = scheduleWithSubtourFromWorkThreeActivities.firstTour();
		
		assertTrue(withSubtourFromWork.isInSubtour(leisureFromWork));
		assertTrue(withSubtourFromEducation.isInSubtour(shoppingFromEducation));
		assertTrue(withSubtourFromService.isInSubtour(shoppingFromService));
		assertTrue(withSubtourFromVisit.isInSubtour(leisureFromVisit));
		
		assertTrue(withTwoSubtours.isInSubtour(privatebusinessFromWork));
		assertTrue(withTwoSubtours.isInSubtour(shoppingFromWork));
		
		assertTrue(withSubtourWithThreeActivities.isInSubtour(subtourFirstActivity));
		assertTrue(withSubtourWithThreeActivities.isInSubtour(subtourSecondActivity));
		assertTrue(withSubtourWithThreeActivities.isInSubtour(subtourThirdActivity));
		
		assertFalse(withoutSubtour.isInSubtour(secondActivity));
		assertFalse(withTwoLeisureActivities.isInSubtour(firstLeisureActivity));
		assertFalse(withTwoLeisureActivities.isInSubtour(secondLeisureActivity));
		
		assertFalse(withTwoServiceActivities.isInSubtour(firstServiceActivity));
		assertFalse(withTwoServiceActivities.isInSubtour(secondServiceActivity));
		
	}
	
	@Test(expected=NoSuchElementException.class)
	public void isInSubtourInvalid() {
		Tour withSubtourFromWork = scheduleWithSubtourFromWork.firstTour();
		
		withSubtourFromWork.isInSubtour(shoppingFromService);
	}
	
	@Test(expected=NoSuchElementException.class)
	public void correspondingSubtourInvalid() {
		Tour withSubtourFromWork = scheduleWithSubtourFromWork.firstTour();
		
		withSubtourFromWork.correspondingSubtour(shoppingFromService);
	}
	
	@Test
	public void correspondingSubtour() {
		Tour withSubtourFromWork = scheduleWithSubtourFromWork.firstTour();
		Tour withSubtourFromEducation = scheduleWithSubtourFromEducation.firstTour();
		Tour withSubtourFromService = scheduleWithSubtourFromService.firstTour();
		Tour withSubtourFromVisit = scheduleWithSubtourFromVisit.firstTour();
		
		Tour withTwoSubtours = scheduleWithTwoSubtoursFromWork.firstTour();
		Tour withSubtourWithThreeActivities = scheduleWithSubtourFromWorkThreeActivities.firstTour();
		
		assertTrue(withSubtourFromWork.correspondingSubtour(leisureFromWork).get().contains(leisureFromWork));
		assertTrue(withSubtourFromEducation.correspondingSubtour(shoppingFromEducation).get().contains(shoppingFromEducation));
		assertTrue(withSubtourFromService.correspondingSubtour(shoppingFromService).get().contains(shoppingFromService));
		assertTrue(withSubtourFromVisit.correspondingSubtour(leisureFromVisit).get().contains(leisureFromVisit));
		
		assertTrue(withTwoSubtours.correspondingSubtour(privatebusinessFromWork).get().contains(privatebusinessFromWork));
		assertTrue(withTwoSubtours.correspondingSubtour(shoppingFromWork).get().contains(shoppingFromWork));
		
		assertFalse(withTwoSubtours.correspondingSubtour(privatebusinessFromWork).get().contains(shoppingFromWork));
		assertFalse(withTwoSubtours.correspondingSubtour(shoppingFromWork).get().contains(privatebusinessFromWork));
		
		assertTrue(withSubtourWithThreeActivities.correspondingSubtour(subtourFirstActivity).get().contains(subtourFirstActivity));
		assertTrue(withSubtourWithThreeActivities.correspondingSubtour(subtourSecondActivity).get().contains(subtourSecondActivity));
		assertTrue(withSubtourWithThreeActivities.correspondingSubtour(subtourThirdActivity).get().contains(subtourThirdActivity));
	}
	
	@Test
	public void purpose() {
		Subtour subtourFromWork = scheduleWithSubtourFromWork.firstTour().nthSubtour(0);
		Subtour subtourFromEducation = scheduleWithSubtourFromEducation.firstTour().nthSubtour(0);
		Subtour subtourFromService = scheduleWithSubtourFromService.firstTour().nthSubtour(0);
		Subtour subtourFromVisit = scheduleWithSubtourFromVisit.firstTour().nthSubtour(0);
		
		Subtour subtour1 = scheduleWithTwoSubtoursFromWork.firstTour().nthSubtour(0);
		Subtour subtour2 = scheduleWithTwoSubtoursFromWork.firstTour().nthSubtour(1);
		Subtour subtourWithThreeActivities = scheduleWithSubtourFromWorkThreeActivities.firstTour().nthSubtour(0);
		
		assertEquals(ActivityType.LEISURE_INDOOR,subtourFromWork.purpose());
		assertEquals(ActivityType.SHOPPING,subtourFromEducation.purpose());
		assertEquals(ActivityType.SHOPPING,subtourFromService.purpose());
		assertEquals(ActivityType.LEISURE_WALK,subtourFromVisit.purpose());
		
		assertEquals(ActivityType.PRIVATE_BUSINESS,subtour1.purpose());
		assertEquals(ActivityType.SHOPPING,subtour2.purpose());
		
		assertEquals(ActivityType.LEISURE_INDOOR,subtourWithThreeActivities.purpose());
	}
	
	@Test
	public void mainActivity() {
		Subtour subtourFromWork = scheduleWithSubtourFromWork.firstTour().nthSubtour(0);
		Subtour subtourFromEducation = scheduleWithSubtourFromEducation.firstTour().nthSubtour(0);
		Subtour subtourFromService = scheduleWithSubtourFromService.firstTour().nthSubtour(0);
		Subtour subtourFromVisit = scheduleWithSubtourFromVisit.firstTour().nthSubtour(0);
		
		Subtour subtour1 = scheduleWithTwoSubtoursFromWork.firstTour().nthSubtour(0);
		Subtour subtour2 = scheduleWithTwoSubtoursFromWork.firstTour().nthSubtour(1);
		Subtour subtourWithThreeActivities = scheduleWithSubtourFromWorkThreeActivities.firstTour().nthSubtour(0);
		
		assertEquals(leisureFromWork,subtourFromWork.mainActivity());
		assertEquals(shoppingFromEducation,subtourFromEducation.mainActivity());
		assertEquals(shoppingFromService,subtourFromService.mainActivity());
		assertEquals(leisureFromVisit,subtourFromVisit.mainActivity());
		
		assertEquals(privatebusinessFromWork,subtour1.mainActivity());
		assertEquals(shoppingFromWork,subtour2.mainActivity());
		
		assertEquals(subtourSecondActivity,subtourWithThreeActivities.mainActivity());
	}
	
	@Test
	public void firstActivity() {
		Subtour subtourFromWork = scheduleWithSubtourFromWork.firstTour().nthSubtour(0);
		Subtour subtourFromEducation = scheduleWithSubtourFromEducation.firstTour().nthSubtour(0);
		Subtour subtourFromService = scheduleWithSubtourFromService.firstTour().nthSubtour(0);
		Subtour subtourFromVisit = scheduleWithSubtourFromVisit.firstTour().nthSubtour(0);
		
		Subtour subtour1 = scheduleWithTwoSubtoursFromWork.firstTour().nthSubtour(0);
		Subtour subtour2 = scheduleWithTwoSubtoursFromWork.firstTour().nthSubtour(1);
		Subtour subtourWithThreeActivities = scheduleWithSubtourFromWorkThreeActivities.firstTour().nthSubtour(0);
		
		assertEquals(leisureFromWork,subtourFromWork.firstActivity());
		assertEquals(shoppingFromEducation,subtourFromEducation.firstActivity());
		assertEquals(shoppingFromService,subtourFromService.firstActivity());
		assertEquals(leisureFromVisit,subtourFromVisit.firstActivity());
		
		assertEquals(privatebusinessFromWork,subtour1.firstActivity());
		assertEquals(shoppingFromWork,subtour2.firstActivity());
		
		assertEquals(subtourFirstActivity,subtourWithThreeActivities.firstActivity());
	}
	
	@Test
	public void lastActivity() {
		Subtour subtourFromWork = scheduleWithSubtourFromWork.firstTour().nthSubtour(0);
		Subtour subtourFromEducation = scheduleWithSubtourFromEducation.firstTour().nthSubtour(0);
		Subtour subtourFromService = scheduleWithSubtourFromService.firstTour().nthSubtour(0);
		Subtour subtourFromVisit = scheduleWithSubtourFromVisit.firstTour().nthSubtour(0);
		
		Subtour subtour1 = scheduleWithTwoSubtoursFromWork.firstTour().nthSubtour(0);
		Subtour subtour2 = scheduleWithTwoSubtoursFromWork.firstTour().nthSubtour(1);
		Subtour subtourWithThreeActivities = scheduleWithSubtourFromWorkThreeActivities.firstTour().nthSubtour(0);
		
		assertEquals(ActivityType.WORK,subtourFromWork.lastActivity().activityType());
		assertEquals(ActivityType.EDUCATION,subtourFromEducation.lastActivity().activityType());
		assertEquals(ActivityType.SERVICE,subtourFromService.lastActivity().activityType());
		assertEquals(ActivityType.PRIVATE_VISIT,subtourFromVisit.lastActivity().activityType());
		
		assertEquals(ActivityType.WORK,subtour1.lastActivity().activityType());
		assertEquals(ActivityType.WORK,subtour2.lastActivity().activityType());
		
		assertEquals(ActivityType.WORK,subtourWithThreeActivities.lastActivity().activityType());
	}
	
}
