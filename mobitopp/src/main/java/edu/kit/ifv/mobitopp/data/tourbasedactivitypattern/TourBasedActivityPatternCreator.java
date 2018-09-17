package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
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
				
				TourPattern tourPattern = asTourPattern(tour);
				
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

	private static boolean isSupertour(List<PatternActivity> tour) {
	
			assert tour != null;
			
			if(tour.size() < 2) {
				return false;
			}
			PatternActivity first = tour.get(0);
			PatternActivity last = tour.get(tour.size()-1);
			
			assert last.getActivityType() == ActivityType.HOME;
		
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
		
		System.out.println("Supertourpattern not yet implemented");
		
		return SuperTourPattern.fromPatternActivities(tour);
	}

	static TourPattern asTourPattern(List<PatternActivity> fulltour) {
		
		assert fulltour.get(fulltour.size()-1).getActivityType() == ActivityType.HOME;
		
		List<PatternActivity> tour =  new ArrayList<PatternActivity>(fulltour.subList(0, fulltour.size()-1));
		
		// 1. Hauptaktivität bestimmen
		// 2. Aktivitäten vor Hauptaktivität bestimmen
		// 3. Aktivitäten nach Hauptaktivität bestiimen
		// 4. Aktivitäten auf Subtouren bestimmen
		
		
		// PatternActivity homeActivity = homeActivity(fulltour);
		
		ActivityType typeOfMainActivity = mainActivityType(fulltour);
		
		List<PatternActivity> mainPatternActivity = mainActivity(fulltour);
		List<List<PatternActivity>> subtourActivities = subtours(fulltour);
		
		assert mainPatternActivity.size() == subtourActivities.size()+1 
				: (mainPatternActivity.size() + ": " + subtourActivities.size()
						+ "\n" + fulltour + "\n" + mainPatternActivity + "\n" + subtourActivities);
		
		
		DayOfWeek day = mainPatternActivity.get(0).getWeekDayType();
		
		
		 assert subtourActivities.isEmpty() || isSupertour(fulltour) || subtourActivities.get(0).get(0).getWeekDayType() == day : fulltour;
		
	
		PatternActivity firstPartOfMainPatternActivity = mainPatternActivity.get(0);
		PatternActivity  lastPartOfMainPatternActivity = mainPatternActivity.get(mainPatternActivity.size()-1);

		
		List<Activity> activitiesBefore = activitiesBeforeMainActivity(tour, firstPartOfMainPatternActivity);
		List<Activity> activitiesAfter = activitiesAfterMainActivity(tour, lastPartOfMainPatternActivity);
		List<List<Activity>> activitiesOnSubtour = asListOfActivities(subtourActivities);
	
		assert containsSubtour(tour) || activitiesBefore.size() + activitiesAfter.size() + 1 == tour.size();
		
		Activity mainActivity = mainPatternActivity.size() > 1
														? SplitActivity.fromPatternActivities(typeOfMainActivity, mainPatternActivity)
														: SimpleActivity.fromPatternActivity(mainPatternActivity.get(0));
	
		
		return new TourPattern(day, mainActivity, activitiesBefore, activitiesAfter, activitiesOnSubtour);
	}
	
	private static List<List<Activity>> asListOfActivities(List<List<PatternActivity>> subtourActivities) {
		List<List<Activity>> result = new ArrayList<List<Activity>>();
	
		for(List<PatternActivity> subtourPattern : subtourActivities)  {
			List<Activity> subtour = new ArrayList<Activity>();
			
			for(PatternActivity act: subtourPattern) {
				subtour.add(SimpleActivity.fromPatternActivity(act));
			}
			
			result.add(subtour);
		}
		
		return result;
	}


	private static List<Activity> activitiesAfterMainActivity(List<PatternActivity> tour,
			PatternActivity mainActivity) {
		
		tour = new LinkedList<PatternActivity>(tour);
		Collections.reverse(tour);
		
		List<Activity> activities = new ArrayList<Activity>();
		
		for (PatternActivity activity : tour) {
			if (activity == mainActivity) {
				break;
			}
			
			activities.add(SimpleActivity.fromPatternActivity(activity));
		}
		
		Collections.reverse(activities);
		
		return activities;
	}

	private static List<Activity> activitiesBeforeMainActivity(List<PatternActivity> tour,
			PatternActivity mainActivity) {
		
		List<Activity> activities = new ArrayList<Activity>();
		
		for (PatternActivity activity : tour) {
			if (activity == mainActivity) {
				break;
			}
			
			activities.add(SimpleActivity.fromPatternActivity(activity));
		}
		
		return activities;
	}

	private static PatternActivity homeActivity(List<PatternActivity> tour) {
		PatternActivity homeActivity = tour.get(tour.size()-1);
		assert homeActivity != null; 
		assert homeActivity.getActivityType() == ActivityType.HOME;
		
		return homeActivity;
	}
	
	private static ActivityType mainActivityType(List<PatternActivity> tour) {
		
		assert tour.get(0).getActivityType() != ActivityType.HOME;
		
		Map<ActivityType,Integer> activityCount = new LinkedHashMap<ActivityType,Integer>();
		Map<ActivityType,Integer> totalDuration = new LinkedHashMap<ActivityType,Integer>();
		
		for(PatternActivity activity : tour) {
			
			if(activity.getActivityType() == ActivityType.HOME) {
			// if(activity.getActivityType().isHomeActivity()) {
				continue;
			}
			
			ActivityType type = activity.getActivityType();
			int duration = activity.getDuration();
			
			assert type != ActivityType.HOME;
			// assert !type.isHomeActivity();
		
			activityCount.putIfAbsent(type, 0);
			totalDuration.putIfAbsent(type, 0);
			
			activityCount.put(type, 1+activityCount.get(type));
			totalDuration.put(type, duration+totalDuration.get(type));
		}
		
		ActivityType typeOfMainActivity = activityCount.containsKey(ActivityType.WORK) ? ActivityType.WORK 
																			: activityCount.containsKey(ActivityType.EDUCATION) ? ActivityType.EDUCATION 
																			: activityCount.containsKey(ActivityType.SERVICE) ? ActivityType.SERVICE 
																		: typeOfMaxDuration(totalDuration);
		
		return typeOfMainActivity;
	}
			
		
		

	private static List<PatternActivity> mainActivity(List<PatternActivity> tour) {
	
		assert !tour.isEmpty();
		
		ActivityType typeOfMainActivity = mainActivityType(tour);
			
		 List<PatternActivity> mainActivity = tour.stream()
											.filter(x -> x.getActivityType() == typeOfMainActivity).collect(Collectors.toList());
		
	
		List<List<PatternActivity>> subtours = subtoursWithLeadingAndTrailingMainActivity(tour);
		 
		if(subtours.isEmpty()) {
			assert mainActivity.size() >= 1 : ("\n" + mainActivity + "\n" + subtours + "\n" + tour);
			return new ArrayList<PatternActivity>(mainActivity.subList(0,1));
		} else if(subtours.size() == 1) {
			List<PatternActivity> subtour = subtours.get(0);
			return Arrays.asList(subtour.get(0),subtour.get(subtour.size()-1));
		} else {
			List<PatternActivity> partsOfMainActivity = new ArrayList<PatternActivity>();
			List<PatternActivity> firstSubtour = subtours.get(0);
			
			partsOfMainActivity.add(firstSubtour.get(0));
			partsOfMainActivity.add(firstSubtour.get(firstSubtour.size()-1));
			
			for(int i=1; i<subtours.size(); i++) {
				
				List<PatternActivity> subtour = subtours.get(i);
				partsOfMainActivity.add(subtour.get(subtour.size()-1));
			}
			
			return partsOfMainActivity;
		}
	}


	private static boolean containsSubtour(List<PatternActivity> tour) {
		
		return !subtours(tour).isEmpty();
	}
	
	static List<List<PatternActivity>> subtours(List<PatternActivity> tour) {
		
		List<List<PatternActivity>> result = new ArrayList<List<PatternActivity>>();
		
		ActivityType typeOfMainActivity = mainActivityType(tour);
		 
		for(List<PatternActivity> subtour: subtoursWithLeadingAndTrailingMainActivity(tour)) {
			
			assert subtour.size() >= 3;
		
			assert subtour.get(0).getActivityType() == typeOfMainActivity;
			assert subtour.get(subtour.size()-1).getActivityType() == typeOfMainActivity;
			
			result.add(new ArrayList<PatternActivity>(subtour.subList(1,subtour.size()-1)));
		}
		
		return result;
	}
	
	static List<List<PatternActivity>> subtoursWithLeadingAndTrailingMainActivity(List<PatternActivity> tour) {
		
		 ActivityType typeOfMainActivity = mainActivityType(tour);
		 
		 Set<ActivityType> subtoursPossible = new LinkedHashSet<>(Arrays.asList(ActivityType.WORK, ActivityType.EDUCATION, ActivityType.SERVICE));
		 
		 if (subtoursPossible.contains(typeOfMainActivity)) {
			 return potentialSubtours(tour, typeOfMainActivity);
		 }
		
		 return new ArrayList<List<PatternActivity>>();
	}
	
	private static List<List<PatternActivity>> potentialSubtours(List<PatternActivity> tour, ActivityType typeOfMainActivity) {

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

	private static ActivityType typeOfMaxDuration(Map<ActivityType, Integer> totalDuration) {

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
		 
		 for(int i=0; i<5; i++) {
		 
		
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
