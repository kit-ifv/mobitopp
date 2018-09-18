package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.panel.ActivityOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PaneldataReader;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class TourBasedActivityPatternCreator {


	public static TourBasedActivityPattern fromPatternActivityWeek(PatternActivityWeek activityWeek) {
		
		
		List<TourBasedActivityPatternElement> elements = new ArrayList<TourBasedActivityPatternElement>();
		
		PatternActivity starthome = activityWeek.getPatternActivities().get(0);
		assert starthome.getActivityType()==ActivityType.HOME : starthome;
		elements.add(new HomeActivity(starthome.getWeekDayType(),SimpleActivity.fromPatternActivity(starthome)));
	
		List<List<PatternActivity>>  tours = asTours(activityWeek);
	
		for (List<PatternActivity> tour : tours) {
		
			assert !tour.isEmpty();
			assert !(tour.size()==1 && tour.get(0).getActivityType()==ActivityType.HOME);
		
			assert tour.get(0).getActivityType()!=ActivityType.HOME;
			
			if(!isSupertour(tour)) {
				
				TourPattern tourPattern = TourPattern.fromPatternActivities(tour);
				
				elements.add(tourPattern);
				
				PatternActivity home = tour.get(tour.size()-1);
				assert home.getActivityType()==ActivityType.HOME;
				elements.add(new HomeActivity(home.getWeekDayType(),SimpleActivity.fromPatternActivity(home)));
				
			} else {
				SuperTourPattern tourPattern = asSuperTourPattern(tour);
				
				elements.add(tourPattern);
			}
		}
		
		return new TourBasedActivityPattern(elements);
	}

	static boolean isSupertour(List<PatternActivity> tour) {
	
			assert tour != null;
			
			if(tour.size() < 2) {
				return false;
			}
			PatternActivity first = tour.get(0);
			PatternActivity last = tour.get(tour.size()-1);
			
			assert last.getActivityType() == ActivityType.HOME :  tour;
		
		/*	
			System.out.println((last.getStarttime() - first.getStarttime())  + " "
					+ (last.getStarttime() - first.getStarttime() > 24*60)); 
					*/
			
			boolean containsSurrogateHome = ! tour.stream()
																		.filter(x -> (x.getActivityType() == ActivityType.OTHERHOME || x.getActivityType() == ActivityType.PRIVATE_VISIT))
																		.collect(Collectors.toList()).isEmpty();
			
			return minutesSinceStartOfWeek(last).differenceTo(minutesSinceStartOfWeek(first)).toMinutes() > 24*60
						&& containsSurrogateHome;
	}
	
	private static SuperTourPattern asSuperTourPattern(List<PatternActivity> tour) {
		
		return SuperTourPattern.fromPatternActivities(tour);
	}

	public static TourPattern asTourPattern(List<PatternActivity> fulltour) {
		return TourPattern.fromPatternActivities(fulltour);
	}
	
	static List<List<PatternActivity>> potentialSubtours(List<PatternActivity> tour, ActivityType typeOfMainActivity) {

			List<PatternActivity> activitiesOfMainActivityType = tour.stream().
					filter(x -> x.getActivityType() == typeOfMainActivity).
					collect(Collectors.toList());

			
			List<List<PatternActivity>> subtours = new ArrayList<List<PatternActivity>>();
			
			if (activitiesOfMainActivityType.size() < 2) { return subtours; }
			
			for(int i=0; i<activitiesOfMainActivityType.size()-1; i++) {
				
				PatternActivity first = activitiesOfMainActivityType.get(i);
				PatternActivity next = activitiesOfMainActivityType.get(i+1);
			
				assert tour.contains(first);
				assert tour.contains(next);
				
				List<PatternActivity> potentialSubtour = tour.subList(tour.indexOf(first), 1+tour.indexOf(next));
				
				if (potentialSubtour.size() <= 2) { continue; }
				
				assert potentialSubtour.size() > 2;
				assert potentialSubtour.get(1).getActivityType() != typeOfMainActivity;
				
				int timeBetween = next.getStarttime() - (first.getStarttime()+first.getDuration());
			
				if (timeBetween < first.getDuration() + next.getDuration()) {
					subtours.add(new ArrayList<PatternActivity>(potentialSubtour));
				}
			}
			// TODO: prüfen, ob die beiden Teile der Hauptaktivität in der Subtour enthalten sind
		
			return subtours;
	}

	static ActivityType typeOfMaxDuration(Map<ActivityType, Integer> totalDuration) {

		return totalDuration.entrySet().stream().reduce(
					new AbstractMap.SimpleImmutableEntry<ActivityType,Integer>(null,-1),
						(a,b) -> a.getValue() > b.getValue() ? a : b
					).getKey();
	}

	static List<List<PatternActivity>> asTours(PatternActivityWeek week) {
		List<List<PatternActivity>>  tours = new ArrayList<List<PatternActivity>>();
		
		List<PatternActivity>  tour = new ArrayList<PatternActivity>();
		
		for(PatternActivity activity : week.getPatternActivities()) {
		
			tour.add(activity);
			
			if (activity.getActivityType() == ActivityType.HOME ) {
				
				if (tour.size() >= 2) {
					tours.add(tour);
				}
				tour = new ArrayList<PatternActivity>();
			}
		}
		
		if (!tour.isEmpty()) {
				tours.add(tour);
		}
		
		return tours;
	}
	
	static List<Activity> fromPatternActivity(List<PatternActivity> activities) {
		
		return  activities.stream().map(x -> SimpleActivity.fromPatternActivity(x)).collect(Collectors.toList());
	}
	
	static Time minutesSinceStartOfWeek(PatternActivity activity) {
		
		return SimpleTime.ofMinutes(activity.getWeekDayTypeAsInt()*24*60+activity.getStarttime());
	}

	public static void main(String[] args) {
		System.out.println("test");
		
		 PaneldataReader reader = new  PaneldataReader(new File("data/stuttgart/data_stuttgart_prefs_2018_03_16.csv"));
		 
		 List<PersonOfPanelData> persons = reader.readPersons();
		 
		 // for(int i=0; i<5; i++) {
		 for(int i=0; i<persons.size(); i++) {
		 
		
		 System.out.println(persons.get(i).getActivityPattern());
		 List<ActivityOfPanelData> activityOfPanelData = ActivityOfPanelData.parseActivities(persons.get(i).getActivityPattern());
		 PatternActivityWeek week = PatternActivityWeek.fromActivityOfPanelData(activityOfPanelData);
		
		 TourBasedActivityPatternCreator.asTours(week);
		 
		 TourBasedActivityPattern tourPattern = TourBasedActivityPatternCreator.fromPatternActivityWeek(week);
		 
		 System.out.println(activityOfPanelData);
		 System.out.println(week);
		 System.out.println(tourPattern);
		 }
	}

	public static String patternToString(TourBasedActivityPattern tourPattern) {
		
		List<Activity> activities = tourPattern.asActivities();
		
		String s = activities.stream().map(x -> asCSV(x)).collect(Collectors.joining(";"));
		
		return s;
	}

	private static String asCSV(Activity x) {
		
		// ActivityOfPanelData
		if (x.plannedStart().equals(SimpleTime.ofMinutes(0)) && x.expectedTripDuration().toMinutes() == -1) {
			
		return  x.expectedTripDuration().toMinutes()
					+ ";" + x.activityType().getTypeAsInt()
					+ ";" + x.plannedDuration().toMinutes()
					+ ";" + "-1"
					;
		}
		
		return  x.expectedTripDuration().toMinutes()
					+ ";" + x.activityType().getTypeAsInt()
					+ ";" + x.plannedDuration().toMinutes()
					+ ";" + x.plannedStart().toMinutes()
					;
	}
}
