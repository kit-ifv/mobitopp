package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.panel.ActivityOfPanelData;
import edu.kit.ifv.mobitopp.time.SimpleTime;

public class TourBasedActivityPatternCreatorTest {
	
	static final String pattern1 = "-1;7;890;-1;40;1;320;890;40;7;2480;1250;40;1;320;3770;40;7;1040;4130;40;1;320;5210;40;7;5360;5570";
	static final String pattern2 = "-1;7;896;-1;21;11;94;896;21;7;2744;1011;21;11;114;3776;21;7;1284;3911;21;11;114;5216;21;7;5604;5351";
	static final String pattern3 = "-1;7;6260;-1;15;1;315;6260;15;7;35;6590;20;1;200;6645;15;7;9465;6860";
	static final String pattern4 = "-1;7;4905;-1;15;42;120;4905;20;7;4135;5045;9;12;216;9189;5;7;190;9410;1;77;88;9601;1;7;5280;9690";
	static final String pattern5 = "-1;7;595;-1;10;11;65;595;30;7;1702;690;20;11;18;2412;40;7;3790;2470;15;11;15;6275;15;7;1425;6305;5;42;25;7735;5;7;2900;7765";
	
	
	
	PatternActivityWeek week;
	
	PatternActivityWeek week1;
	PatternActivityWeek week2;
	PatternActivityWeek week3;
	PatternActivityWeek week4;
	PatternActivityWeek week5;
	
	List<PatternActivity> tour_nosubtour;
	List<PatternActivity> tour_onesubtour;
	List<PatternActivity> tour_twosubtours;
	List<PatternActivity> servicetour_withsubtour;
	List<PatternActivity> shoppingtour_withoutsubtour;
	List<PatternActivity> worktour_withservice;
		 
		 
	@Before
	public void initialise() {
		 
		 week = createActivityWeek();
		 
		 week1 = fromString(pattern1);
		 week2 = fromString(pattern2);
		 week3 = fromString(pattern3);
		 week4 = fromString(pattern4);
		 week5 = fromString(pattern5);
		 
		 tour_nosubtour = tourWithNoSubtour();
		 tour_onesubtour = tourWithOneSubtour();
		 tour_twosubtours = tourWithTwoSubtours();
		 servicetour_withsubtour = servicetourWithSubtour();
		 shoppingtour_withoutsubtour = shoppingtourWithoutSubtour();
		 worktour_withservice = worktourWithService();
	}
	
	private List<PatternActivity> worktourWithService() {
		List<PatternActivity> tour = new ArrayList<PatternActivity>();
		
		tour.add(createActivity(ActivityType.SERVICE, DayOfWeek.MONDAY, SimpleTime.ofHours(6), RelativeTime.ofMinutes(10)));
		tour.add(createActivity(ActivityType.WORK, DayOfWeek.MONDAY, SimpleTime.ofHours(7), RelativeTime.ofHours(8)));
		tour.add(createActivity(ActivityType.SERVICE, DayOfWeek.MONDAY, SimpleTime.ofHours(17), RelativeTime.ofMinutes(10)));
		tour.add(createActivity(ActivityType.HOME, DayOfWeek.MONDAY, SimpleTime.ofHours(18), RelativeTime.ofHours(12)));
		
		return tour;
	}

	private List<PatternActivity> shoppingtourWithoutSubtour() {
		List<PatternActivity> tour = new ArrayList<PatternActivity>();
		
		tour.add(createActivity(ActivityType.SHOPPING, DayOfWeek.MONDAY, SimpleTime.ofHours(12), RelativeTime.ofMinutes(60)));
		tour.add(createActivity(ActivityType.LEISURE, DayOfWeek.MONDAY, SimpleTime.ofHours(13), RelativeTime.ofMinutes(15)));
		tour.add(createActivity(ActivityType.SHOPPING, DayOfWeek.MONDAY, SimpleTime.ofHours(14), RelativeTime.ofMinutes(60)));
		tour.add(createActivity(ActivityType.HOME, DayOfWeek.MONDAY, SimpleTime.ofHours(15), RelativeTime.ofHours(12)));
		
		return tour;
	}

	private List<PatternActivity> servicetourWithSubtour() {
		List<PatternActivity> tour = new ArrayList<PatternActivity>();
		
		tour.add(createActivity(ActivityType.SERVICE, DayOfWeek.MONDAY, SimpleTime.ofHours(12), RelativeTime.ofMinutes(60)));
		tour.add(createActivity(ActivityType.LEISURE, DayOfWeek.MONDAY, SimpleTime.ofHours(13), RelativeTime.ofMinutes(15)));
		tour.add(createActivity(ActivityType.SERVICE, DayOfWeek.MONDAY, SimpleTime.ofHours(14), RelativeTime.ofMinutes(60)));
		tour.add(createActivity(ActivityType.HOME, DayOfWeek.MONDAY, SimpleTime.ofHours(15), RelativeTime.ofHours(12)));
		
		return tour;
	}

	private List<PatternActivity> tourWithOneSubtour() {
		List<PatternActivity> tour = new ArrayList<PatternActivity>();
		
		tour.add(createActivity(ActivityType.WORK, DayOfWeek.MONDAY, SimpleTime.ofHours(7), RelativeTime.ofHours(8)));
		tour.add(createActivity(ActivityType.SHOPPING, DayOfWeek.MONDAY, SimpleTime.ofHours(12), RelativeTime.ofMinutes(45)));
		tour.add(createActivity(ActivityType.LEISURE, DayOfWeek.MONDAY, SimpleTime.ofHours(13), RelativeTime.ofMinutes(30)));
		tour.add(createActivity(ActivityType.WORK, DayOfWeek.MONDAY, SimpleTime.ofHours(14), RelativeTime.ofHours(4)));
		tour.add(createActivity(ActivityType.HOME, DayOfWeek.MONDAY, SimpleTime.ofHours(19), RelativeTime.ofHours(12)));
		
		return tour;
	}

	private List<PatternActivity> tourWithNoSubtour() {
		List<PatternActivity> tour = new ArrayList<PatternActivity>();
		
		tour.add(createActivity(ActivityType.WORK, DayOfWeek.MONDAY, SimpleTime.ofHours(7), RelativeTime.ofHours(8)));
		tour.add(createActivity(ActivityType.SHOPPING, DayOfWeek.MONDAY, SimpleTime.ofHours(17), RelativeTime.ofMinutes(45)));
		tour.add(createActivity(ActivityType.SHOPPING, DayOfWeek.MONDAY, SimpleTime.ofHours(18), RelativeTime.ofMinutes(45)));
		tour.add(createActivity(ActivityType.HOME, DayOfWeek.MONDAY, SimpleTime.ofHours(19), RelativeTime.ofHours(12)));
		
		return tour;
	}

	private List<PatternActivity> tourWithTwoSubtours() {
		
		List<PatternActivity> tour = new ArrayList<PatternActivity>();
		
		tour.add(createActivity(ActivityType.EDUCATION, DayOfWeek.MONDAY, SimpleTime.ofHours(7), RelativeTime.ofHours(8)));
		tour.add(createActivity(ActivityType.SHOPPING, DayOfWeek.MONDAY, SimpleTime.ofHours(12), RelativeTime.ofMinutes(45)));
		tour.add(createActivity(ActivityType.EDUCATION, DayOfWeek.MONDAY, SimpleTime.ofHours(13), RelativeTime.ofHours(2)));
		tour.add(createActivity(ActivityType.LEISURE, DayOfWeek.MONDAY, SimpleTime.ofHours(15), RelativeTime.ofMinutes(45)));
		tour.add(createActivity(ActivityType.EDUCATION, DayOfWeek.MONDAY, SimpleTime.ofHours(17), RelativeTime.ofHours(2)));
		tour.add(createActivity(ActivityType.HOME, DayOfWeek.MONDAY, SimpleTime.ofHours(19), RelativeTime.ofHours(12)));
		
		return tour;
	}


	@Test
	public void asTours() {
		
		List<List<PatternActivity>> tours1 = TourBasedActivityPatternCreator.asTours(week1);
		List<List<PatternActivity>> tours2 = TourBasedActivityPatternCreator.asTours(week2);
		List<List<PatternActivity>> tours3 = TourBasedActivityPatternCreator.asTours(week3);
		List<List<PatternActivity>> tours4 = TourBasedActivityPatternCreator.asTours(week4);
		List<List<PatternActivity>> tours5 = TourBasedActivityPatternCreator.asTours(week5);
		 
//		System.out.println(tours1);
//		System.out.println(tours2);
//		System.out.println(tours3);
//		System.out.println(tours4);
//		System.out.println(tours5);
		
		assertEquals(3,tours1.size());
		assertEquals(3,tours2.size());
		assertEquals(2,tours3.size());
		assertEquals(3,tours4.size());
		assertEquals(4,tours5.size());
	}
	
	@Test
	public void subtours() {
		List<List<PatternActivity>> nosubtour = TourBasedActivityPatternCreator.subtours(tour_nosubtour);
		List<List<PatternActivity>> onesubtour = TourBasedActivityPatternCreator.subtours(tour_onesubtour);
		List<List<PatternActivity>> twosubtours = TourBasedActivityPatternCreator.subtours(tour_twosubtours);
		
		assertEquals(0, nosubtour.size());
		assertEquals(1, onesubtour.size());
		assertEquals(2, twosubtours.size());
	}
	
	@Test
	public void serviceTourWithSubtour() {
		
		assertEquals(1, TourBasedActivityPatternCreator.subtours(servicetour_withsubtour).size());
	}
	
	@Test
	public void shoppingTourWithoutSubtour() {
		
		assertEquals(0, TourBasedActivityPatternCreator.subtours(shoppingtour_withoutsubtour).size());
	}
	
	@Test
	public void workTourWithoutServiceTwice() {
		
		assertEquals(0, TourBasedActivityPatternCreator.subtours(worktour_withservice).size());
	}
	
	@Test
	public void asPattern() {
		
		 List<ActivityOfPanelData> activityOfPanelData = ActivityOfPanelData.parseActivities(pattern3);
		 PatternActivityWeek week = PatternActivityWeek.fromActivityOfPanelData(activityOfPanelData);
		 TourBasedActivityPattern tourPattern = TourBasedActivityPatternCreator.fromPatternActivityWeek(week);
		 
		 // System.out.println(tourPattern);
		
		assertEquals(pattern3, TourBasedActivityPatternCreator.patternToString(tourPattern));
	}
	
	@Test
	public void asActivities() {
		
		 List<ActivityOfPanelData> activityOfPanelData = ActivityOfPanelData.parseActivities(pattern3);
		 PatternActivityWeek week = PatternActivityWeek.fromActivityOfPanelData(activityOfPanelData);
		 List<PatternActivity> patternActivities = week.getPatternActivities();
		 List<Activity> activities = TourBasedActivityPatternCreator.fromPatternActivity(patternActivities);
		 
		 System.out.println(activityOfPanelData);
		 System.out.println(week);
		 System.out.println(patternActivities);
		
		assertEquals(activities.size(), TourBasedActivityPatternCreator.fromPatternActivityWeek(week).asActivities().size());
	}



	
	private static PatternActivity createActivity(
			ActivityType activityType,
      DayOfWeek day,
      Time start,
      RelativeTime duration
			) {
		return new PatternActivity(activityType, day, 15, (int) start.toMinutes(), (int) duration.toMinutes());
	}
	
	
	private static PatternActivityWeek createActivityWeek() {
		
		List<PatternActivity> activities = new ArrayList<PatternActivity>();

		activities.add(createActivity(ActivityType.HOME, DayOfWeek.MONDAY, SimpleTime.ofHours(0), RelativeTime.ofHours(6)));
		activities.add(createActivity(ActivityType.WORK, DayOfWeek.MONDAY, SimpleTime.ofHours(7), RelativeTime.ofHours(8)));
		activities.add(createActivity(ActivityType.HOME, DayOfWeek.MONDAY, SimpleTime.ofHours(17), RelativeTime.ofHours(12)));
		
		activities.add(createActivity(ActivityType.WORK, DayOfWeek.TUESDAY, SimpleTime.ofHours(7), RelativeTime.ofHours(8)));
		activities.add(createActivity(ActivityType.SHOPPING, DayOfWeek.TUESDAY, SimpleTime.ofHours(16), RelativeTime.ofHours(1)));
		activities.add(createActivity(ActivityType.HOME, DayOfWeek.TUESDAY, SimpleTime.ofHours(18), RelativeTime.ofHours(12)));
		
		return new PatternActivityWeek(activities);
	}
	
	private static PatternActivityWeek fromString(String pattern) {
		 List<ActivityOfPanelData> activityOfPanelData = ActivityOfPanelData.parseActivities(pattern);
		 PatternActivityWeek week = PatternActivityWeek.fromActivityOfPanelData(activityOfPanelData);
		 
		 return week;
	}
}
