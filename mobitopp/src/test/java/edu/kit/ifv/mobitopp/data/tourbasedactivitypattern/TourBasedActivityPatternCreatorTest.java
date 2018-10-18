package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.panel.ActivityOfPanelData;

public class TourBasedActivityPatternCreatorTest {
	
	static final String pattern1 = "-1;7;890;-1;40;1;320;890;40;7;2480;1250;40;1;320;3770;40;7;1040;4130;40;1;320;5210;40;7;5360;5570";
	static final String pattern2 = "-1;7;896;-1;21;11;94;896;21;7;2744;1011;21;11;114;3776;21;7;1284;3911;21;11;114;5216;21;7;5604;5351";
	static final String pattern3 = "-1;7;6260;-1;15;1;315;6260;15;7;35;6590;20;1;200;6645;15;7;9465;6860";
	static final String pattern4 = "-1;7;4905;-1;15;42;120;4905;20;7;4135;5045;9;12;216;9189;5;7;190;9410;1;77;88;9601;1;7;5280;9690";
	static final String pattern5 = "-1;7;595;-1;10;11;65;595;30;7;1702;690;20;11;18;2412;40;7;3790;2470;15;11;15;6275;15;7;1425;6305;5;42;25;7735;5;7;2900;7765";
	static final String pattern6 = "-1;7;1040;-1;10;6;160;1040;10;7;1130;1210;45;53;225;2385;30;7;3700;2640;10;41;111;6350;14;7;285;6475;12;6;103;6772;10;7;1090;6885;8;52;29;7983;43;52;170;8055;75;7;3;8300;9;7;2800;8310";
	static final String pattern7 = "-1;7;582;-1;8;1;236;582;1;11;5;819;5;2;105;829;15;42;24;949;19;42;5;992;9;2;118;1006;2;11;3;1126;11;11;3;1140;6;7;809;1149;7;2;47;1965;8;1;60;2020;11;2;56;2091;9;2;56;2156;7;12;40;2219;8;2;71;2267;20;2;27;2358;4;2;11;2389;6;2;8;2406;2;7;979;2416;10;2;68;3405;10;1;8;3483;1;77;3;3492;1;7;224;3494;1;77;18;3719;9;7;1079;3746;11;1;237;4836;1;77;23;5074;1;7;127;5098;9;2;83;5234;10;1;87;5327;9;7;834;5423;9;1;142;6266;4;2;61;6412;8;1;106;6481;4;2;62;6591;6;2;3;6659;3;2;70;6665;3;2;3;6738;6;1;3;6747;1;11;6;6749;1;11;4;6756;2;1;176;6762;9;7;771;6947;7;11;37;7725;11;11;6;7773;3;42;11;7782;15;7;1569;7808;12;6;6;9389;10;51;88;9405;8;6;29;9501;10;7;1114;9540";
	static final String pattern8 = "-1;7;820;-1;10;6;70;820;10;41;30;900;10;7;1250;940;10;6;50;2200;10;53;80;2260;10;7;995;2350;105;6;150;3450;90;7;1110;3690;90;6;150;4890;90;42;90;5130;15;6;3;5235;5;6;3;5240;5;41;75;5245;5;41;15;5325;5;6;3;5345;2;11;8;5347;15;7;1260;5370;10;6;60;6640;10;7;1240;6710;20;51;130;7970;30;7;3;8130;1;77;28;8131;1;7;1250;8160;10;51;60;9420;10;7;1400;9490";
	
	static final String pattern_with_supertour = "-1;7;1125;-1;165;9;555;1125;194;9;3;1874;45;2;630;1920;45;9;720;2595;45;1;525;3360;225;7;810;4110;10;41;120;4930;10;7;1280;5060;20;42;30;6360;5;2;365;6395;20;7;1020;6780;20;41;110;7820;20;7;1230;7950;15;11;210;9195;15;7;180;9420;90;6;3;9690;89;7;1260;9780";
	static final String pattern_with_supertour2 = "-1;7;630;-1;0;42;120;630;5;6;85;755;5;12;265;845;5;7;25;1115;5;12;5;1145;25;53;5;1175;20;53;100;1200;20;12;660;1320;15;3;135;1995;15;7;220;2145;5;12;150;2370;5;7;80;2525;5;12;1410;2610;30;53;20;4050;5;7;790;4075;25;3;240;4890;60;51;179;5190;31;7;90;5400;10;12;800;5500;30;3;219;6330;21;7;390;6570;15;51;165;6975;15;7;15;7155;5;12;740;7175;5;1;360;7920;5;7;1070;8285;5;1;120;9360;5;7;115;9485;5;12;385;9605;5;7;715;9995";
	
	// static final String pattern_with_split_activity = "-1;7;582;-1;8;1;236;582;1;11;5;819;5;2;105;829;15;42;24;949;19;42;5;992;9;2;118;1006;2;11;3;1126;11;11;3;1140;6;7;809;1149;7;2;47;1965;8;1;60;2020;11;2;56;2091;9;2;56;2156;7;12;40;2219;8;2;71;2267;20;2;27;2358;4;2;11;2389;6;2;8;2406;2;7;979;2416;10;2;68;3405;10;1;8;3483;1;77;3;3492;1;7;224;3494;1;77;18;3719;9;7;1079;3746;11;1;237;4836;1;77;23;5074;1;7;127;5098;9;2;83;5234;10;1;87;5327;9;7;834;5423;9;1;142;6266;4;2;61;6412;8;1;106;6481;4;2;62;6591;6;2;3;6659;3;2;70;6665;3;2;3;6738;6;1;3;6747;1;11;6;6749;1;11;4;6756;2;1;176;6762;9;7;771;6947;7;11;37;7725;11;11;6;7773;3;42;11;7782;15;7;1569;7808;12;6;6;9389;10;51;88;9405;8;6;29;9501;10;7;1114;9540";
	
	
	PatternActivityWeek week;
	
	PatternActivityWeek week1;
	PatternActivityWeek week2;
	PatternActivityWeek week3;
	PatternActivityWeek week4;
	PatternActivityWeek week5;
	PatternActivityWeek week6;
	PatternActivityWeek week7;
	PatternActivityWeek week8;
	
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
		 week6 = fromString(pattern6);
		 week7 = fromString(pattern7);
		 week8 = fromString(pattern8);
		 
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
		List<List<PatternActivity>> tours6 = TourBasedActivityPatternCreator.asTours(week6);
		List<List<PatternActivity>> tours7 = TourBasedActivityPatternCreator.asTours(week7);
		List<List<PatternActivity>> tours8 = TourBasedActivityPatternCreator.asTours(week8);
		 
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
		assertEquals(6,tours6.size());
		assertEquals(9,tours7.size());
		assertEquals(8,tours8.size());
	}
	
	@Test
	public void subtours() {
		List<List<PatternActivity>> nosubtour = TourPattern.subtoursOfMainActivity(tour_nosubtour);
		List<List<PatternActivity>> onesubtour = TourPattern.subtoursOfMainActivity(tour_onesubtour);
		List<List<PatternActivity>> twosubtours = TourPattern.subtoursOfMainActivity(tour_twosubtours);
		List<List<PatternActivity>> longsubtour = TourPattern.subtoursOfMainActivity(tour_longsubtour);
		
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
		
		assertEquals(1, TourPattern.subtoursOfMainActivity(servicetour_withsubtour).size());
		assertEquals(1, TourPattern.subtoursOfMainActivity(servicetour_withsubtour).get(0).size());
	}
	
	@Test
	public void shoppingTourWithoutSubtour() {
		
		assertEquals(0, TourPattern.subtoursOfMainActivity(shoppingtour_withoutsubtour).size());
	}
	
	@Test
	public void workTourWithoutServiceTwice() {
		
		assertEquals(0, TourPattern.subtoursOfMainActivity(worktour_withservice).size());
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
	public void asTourPatternFromString() {
		
		TourBasedActivityPattern tours1 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week1);
		TourBasedActivityPattern tours2 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week2);
		TourBasedActivityPattern tours3 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week3);
		TourBasedActivityPattern tours4 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week4);
		TourBasedActivityPattern tours5 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week5);
		TourBasedActivityPattern tours6 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week6);
		TourBasedActivityPattern tours7 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week7);
		
		assertEquals(tours1, tours1);
		assertEquals(tours1, TourBasedActivityPattern.fromExtendedPatternActivities(tours1.asPatternActivities()));
		assertEquals(tours2, TourBasedActivityPattern.fromExtendedPatternActivities(tours2.asPatternActivities()));
		assertEquals(tours3, TourBasedActivityPattern.fromExtendedPatternActivities(tours3.asPatternActivities()));
		assertEquals(tours4, TourBasedActivityPattern.fromExtendedPatternActivities(tours4.asPatternActivities()));
		assertEquals(tours5, TourBasedActivityPattern.fromExtendedPatternActivities(tours5.asPatternActivities()));
		assertEquals(tours6, TourBasedActivityPattern.fromExtendedPatternActivities(tours6.asPatternActivities()));
		assertEquals(tours7, TourBasedActivityPattern.fromExtendedPatternActivities(tours7.asPatternActivities()));
	}
	
	@Test
	public void sizeFromString() {
		
		TourBasedActivityPattern tours1 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week1);
		TourBasedActivityPattern tours2 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week2);
		TourBasedActivityPattern tours3 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week3);
		TourBasedActivityPattern tours4 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week4);
		TourBasedActivityPattern tours5 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week5);
		TourBasedActivityPattern tours6 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week6);
		TourBasedActivityPattern tours7 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week7);
		
		assertEquals(week1.getPatternActivities().size(), tours1.asPatternActivities().size());
		assertEquals(week2.getPatternActivities().size(), tours2.asPatternActivities().size());
		assertEquals(week3.getPatternActivities().size(), tours3.asPatternActivities().size());
		assertEquals(week4.getPatternActivities().size(), tours4.asPatternActivities().size());
		assertEquals(week5.getPatternActivities().size(), tours5.asPatternActivities().size());
		assertEquals(week6.getPatternActivities().size(), tours6.asPatternActivities().size());
		assertEquals(week7.getPatternActivities().size(), tours7.asPatternActivities().size());
	}
	
	@Test
	public void testPattern7() {
		
		TourBasedActivityPattern tours7 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week7);

		assertEquals(1,tours7.toursOfDay(DayOfWeek.MONDAY).size());
		assertEquals(1,tours7.toursOfDay(DayOfWeek.TUESDAY).size());
		assertEquals(2,tours7.toursOfDay(DayOfWeek.WEDNESDAY).size());
		assertEquals(2,tours7.toursOfDay(DayOfWeek.THURSDAY).size());
		assertEquals(1,tours7.toursOfDay(DayOfWeek.FRIDAY).size());
		assertEquals(1,tours7.toursOfDay(DayOfWeek.SATURDAY).size());
		assertEquals(1,tours7.toursOfDay(DayOfWeek.SUNDAY).size());
		
		TourPattern fridayTour = tours7.toursOfDay(DayOfWeek.FRIDAY).get(0);
		
		
		assertEquals(0, fridayTour.outboundTripActivities.size());
		assertEquals(0, fridayTour.inboundTripActivities.size());
		assertEquals(3, fridayTour.subtourActivities.size());
		assertEquals(1, fridayTour.subtourActivities.get(0).size());
		assertEquals(4, fridayTour.subtourActivities.get(1).size());
		assertEquals(2, fridayTour.subtourActivities.get(2).size());
		
		assertEquals(11, fridayTour.asActivities().size());
	}
	
	@Test
	public void testPattern8() {
		
		TourBasedActivityPattern tours8 = TourBasedActivityPatternCreator.fromPatternActivityWeek(week8);

		assertEquals(1,tours8.toursOfDay(DayOfWeek.MONDAY).size());
		assertEquals(1,tours8.toursOfDay(DayOfWeek.TUESDAY).size());
		assertEquals(1,tours8.toursOfDay(DayOfWeek.WEDNESDAY).size());
		assertEquals(1,tours8.toursOfDay(DayOfWeek.THURSDAY).size());
		assertEquals(1,tours8.toursOfDay(DayOfWeek.FRIDAY).size());
		assertEquals(2,tours8.toursOfDay(DayOfWeek.SATURDAY).size());
		assertEquals(1,tours8.toursOfDay(DayOfWeek.SUNDAY).size());
		
		TourPattern thursdayTour = tours8.toursOfDay(DayOfWeek.THURSDAY).get(0);
	
		// Achtung: Pattern8 ist eigentlich eine Tour, die zwei Aktivitäten mit Subtour enthält
		
		assertEquals(0, thursdayTour.outboundTripActivities.size());
		assertEquals(5, thursdayTour.inboundTripActivities.size());
		assertEquals(1, thursdayTour.subtourActivities.size());
		assertEquals(1, thursdayTour.subtourActivities.get(0).size());
		
		assertEquals(8, thursdayTour.asActivities().size());
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
