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
	
	static final String pattern_with_supertour = "-1;7;1125;-1;165;9;555;1125;194;9;3;1874;45;2;630;1920;45;9;720;2595;45;1;525;3360;225;7;810;4110;10;41;120;4930;10;7;1280;5060;20;42;30;6360;5;2;365;6395;20;7;1020;6780;20;41;110;7820;20;7;1230;7950;15;11;210;9195;15;7;180;9420;90;6;3;9690;89;7;1260;9780";
	static final String pattern_with_supertour2 = "-1;7;630;-1;0;42;120;630;5;6;85;755;5;12;265;845;5;7;25;1115;5;12;5;1145;25;53;5;1175;20;53;100;1200;20;12;660;1320;15;3;135;1995;15;7;220;2145;5;12;150;2370;5;7;80;2525;5;12;1410;2610;30;53;20;4050;5;7;790;4075;25;3;240;4890;60;51;179;5190;31;7;90;5400;10;12;800;5500;30;3;219;6330;21;7;390;6570;15;51;165;6975;15;7;15;7155;5;12;740;7175;5;1;360;7920;5;7;1070;8285;5;1;120;9360;5;7;115;9485;5;12;385;9605;5;7;715;9995";
	
	
	
	PatternActivityWeek week;
	
	PatternActivityWeek week1;
	PatternActivityWeek week2;
	PatternActivityWeek week3;
	PatternActivityWeek week4;
	PatternActivityWeek week5;
	
	PatternActivityWeek week_supertour;
	PatternActivityWeek week_supertour2;
	
	List<PatternActivity> tour_nosubtour;
	List<PatternActivity> tour_onesubtour;
	List<PatternActivity> tour_twosubtours;
	List<PatternActivity> tour_longsubtour;
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
		 
		 week_supertour = fromString(pattern_with_supertour);
		 week_supertour2 = fromString(pattern_with_supertour2);
		 
		 tour_nosubtour = tourWithNoSubtour();
		 tour_onesubtour = tourWithOneSubtour();
		 tour_twosubtours = tourWithTwoSubtours();
		 tour_longsubtour = tourWithLongSubtour();
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
	
	private List<PatternActivity> tourWithLongSubtour() {
		
		List<PatternActivity> tour = new ArrayList<PatternActivity>();
		
		tour.add(createActivity(ActivityType.EDUCATION, DayOfWeek.MONDAY, SimpleTime.ofHours(7), RelativeTime.ofHours(6)));
		tour.add(createActivity(ActivityType.PRIVATE_VISIT, DayOfWeek.MONDAY, SimpleTime.ofHours(12), RelativeTime.ofMinutes(45)));
		tour.add(createActivity(ActivityType.LEISURE_INDOOR, DayOfWeek.MONDAY, SimpleTime.ofHours(13), RelativeTime.ofMinutes(45)));
		tour.add(createActivity(ActivityType.PRIVATE_VISIT, DayOfWeek.MONDAY, SimpleTime.ofHours(14), RelativeTime.ofMinutes(45)));
		tour.add(createActivity(ActivityType.EDUCATION, DayOfWeek.MONDAY, SimpleTime.ofHours(15), RelativeTime.ofHours(2)));
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
		List<List<PatternActivity>> nosubtour = TourPattern.subtours(tour_nosubtour);
		List<List<PatternActivity>> onesubtour = TourPattern.subtours(tour_onesubtour);
		List<List<PatternActivity>> twosubtours = TourPattern.subtours(tour_twosubtours);
		List<List<PatternActivity>> longsubtour = TourPattern.subtours(tour_longsubtour);
		
		assertEquals(0, nosubtour.size());
		assertEquals(1, onesubtour.size());
		assertEquals(2, twosubtours.size());
		assertEquals(1, longsubtour.size());
		
		assertEquals(2, onesubtour.get(0).size());
		assertEquals(1, twosubtours.get(0).size());
		assertEquals(1, twosubtours.get(1).size());
		assertEquals(3, longsubtour.get(0).size());
	}
	
	@Test
	public void serviceTourWithSubtour() {
		
		assertEquals(1, TourPattern.subtours(servicetour_withsubtour).size());
		assertEquals(1, TourPattern.subtours(servicetour_withsubtour).get(0).size());
	}
	
	@Test
	public void shoppingTourWithoutSubtour() {
		
		assertEquals(0, TourPattern.subtours(shoppingtour_withoutsubtour).size());
	}
	
	@Test
	public void workTourWithoutServiceTwice() {
		
		assertEquals(0, TourPattern.subtours(worktour_withservice).size());
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
		 
//		 System.out.println(activityOfPanelData);
//		 System.out.println(week);
//		 System.out.println(patternActivities);
		
		assertEquals(activities.size(), TourBasedActivityPatternCreator.fromPatternActivityWeek(week).asActivities().size());
	}
	
	@Test
	public void asPatternActivities() {
		 List<ActivityOfPanelData> activityOfPanelData = ActivityOfPanelData.parseActivities(pattern3);
		 PatternActivityWeek week = PatternActivityWeek.fromActivityOfPanelData(activityOfPanelData);
		 TourBasedActivityPattern tourPattern = TourBasedActivityPatternCreator.fromPatternActivityWeek(week);
		 
		 List<PatternActivity> patternActivities = week.getPatternActivities();
		 List<ExtendedPatternActivity> tourActivities = tourPattern.asPatternActivities();

		 assertEquals(patternActivities.size(),tourActivities.size());
		 
		 for(int i=0; i<patternActivities.size(); i++) {
			 assertEquals(patternActivities.get(i).getActivityType(),tourActivities.get(i).getActivityType());
			 assertEquals(patternActivities.get(i).getDuration(),tourActivities.get(i).getDuration());
			 assertEquals(patternActivities.get(i).getStarttime(),tourActivities.get(i).getStarttime());
			 assertEquals(patternActivities.get(i).getObservedTripDuration(),tourActivities.get(i).getObservedTripDuration());
		 }
	}
	
	@Test
	public void asTourPattern() {
		
		TourPattern tp_nosubtour = TourPattern.fromPatternActivities(tour_nosubtour);
		TourPattern tp_onesubtour = TourPattern.fromPatternActivities(tour_onesubtour);
		TourPattern tp_twosubtours = TourPattern.fromPatternActivities(tour_twosubtours);
		TourPattern tp_service_withsubtour = TourPattern.fromPatternActivities(servicetour_withsubtour);
		TourPattern tp_shopping_withoutsubtour = TourPattern.fromPatternActivities(shoppingtour_withoutsubtour);
		TourPattern tp_work_withservice = TourPattern.fromPatternActivities(worktour_withservice);
		TourPattern tp_longsubtour = TourPattern.fromPatternActivities(tour_longsubtour);
		 
//		System.out.println(tp_onesubtour);
//		System.out.println(TourPattern.fromExtendedPatternActivities(tp_onesubtour.asPatternActivities(0)));
		
		
		assertEquals(tp_nosubtour,TourPattern.fromExtendedPatternActivities(tp_nosubtour.asPatternActivities(0)));
		assertEquals(tp_onesubtour,TourPattern.fromExtendedPatternActivities(tp_onesubtour.asPatternActivities(0)));
		assertEquals(tp_twosubtours,TourPattern.fromExtendedPatternActivities(tp_twosubtours.asPatternActivities(0)));
		assertEquals(tp_service_withsubtour,TourPattern.fromExtendedPatternActivities(tp_service_withsubtour.asPatternActivities(0)));
		assertEquals(tp_shopping_withoutsubtour,TourPattern.fromExtendedPatternActivities(tp_shopping_withoutsubtour.asPatternActivities(0)));
		assertEquals(tp_work_withservice,TourPattern.fromExtendedPatternActivities(tp_work_withservice.asPatternActivities(0)));
		assertEquals(tp_longsubtour,TourPattern.fromExtendedPatternActivities(tp_longsubtour.asPatternActivities(0)));
	}
	
	@Test
	public void asTourPatternXXX() {
		
		TourBasedActivityPattern tours1 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week1);
		TourBasedActivityPattern tours2 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week2);
		TourBasedActivityPattern tours3 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week3);
		TourBasedActivityPattern tours4 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week4);
		TourBasedActivityPattern tours5 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week5);
		
		assertEquals(tours1, tours1);
		assertEquals(tours1, TourBasedActivityPattern.fromExtendedPatternActivities(tours1.asPatternActivities()));
		assertEquals(tours2, TourBasedActivityPattern.fromExtendedPatternActivities(tours2.asPatternActivities()));
		assertEquals(tours3, TourBasedActivityPattern.fromExtendedPatternActivities(tours3.asPatternActivities()));
		assertEquals(tours4, TourBasedActivityPattern.fromExtendedPatternActivities(tours4.asPatternActivities()));
		assertEquals(tours5, TourBasedActivityPattern.fromExtendedPatternActivities(tours5.asPatternActivities()));
		
	}
	
	@Test
	public void withSuperTour() {
		
		TourBasedActivityPattern tours = TourBasedActivityPatternCreator.fromPatternActivityWeek(week_supertour);
		
		assertEquals(tours.asActivities().size(), TourBasedActivityPattern.fromExtendedPatternActivities(tours.asPatternActivities()).asActivities().size());
		assertEquals(tours.asActivities(), TourBasedActivityPattern.fromExtendedPatternActivities(tours.asPatternActivities()).asActivities());
		assertEquals(tours.asPatternActivities(), TourBasedActivityPattern.fromExtendedPatternActivities(tours.asPatternActivities()).asPatternActivities());
		
		assertEquals(tours.toString(), TourBasedActivityPattern.fromExtendedPatternActivities(tours.asPatternActivities()).toString());
		
		assertEquals(tours, TourBasedActivityPattern.fromExtendedPatternActivities(tours.asPatternActivities()));
	}
	
	@Test
	public void withSuperTour2() {
		
		TourBasedActivityPattern tours = TourBasedActivityPatternCreator.fromPatternActivityWeek(week_supertour2);
		
		assertEquals(tours.asActivities().size(), TourBasedActivityPattern.fromExtendedPatternActivities(tours.asPatternActivities()).asActivities().size());
		assertEquals(tours.asActivities(), TourBasedActivityPattern.fromExtendedPatternActivities(tours.asPatternActivities()).asActivities());
		assertEquals(tours.asPatternActivities(), TourBasedActivityPattern.fromExtendedPatternActivities(tours.asPatternActivities()).asPatternActivities());
		
		assertEquals(tours.toString(), TourBasedActivityPattern.fromExtendedPatternActivities(tours.asPatternActivities()).toString());
		
		assertEquals(tours, TourBasedActivityPattern.fromExtendedPatternActivities(tours.asPatternActivities()));
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
