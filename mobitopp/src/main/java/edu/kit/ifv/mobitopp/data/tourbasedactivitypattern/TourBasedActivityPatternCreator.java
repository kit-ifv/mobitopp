package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class TourBasedActivityPatternCreator {


	public static TourBasedActivityPattern fromPatternActivityWeek(PatternActivityWeek activityWeek) {
		
		// besser: kein DayPattern -- stattdessem Liste mit TourPattern und SuperTourPattern
		
		List<TourBasedActivityPatternElement> elements = new ArrayList<TourBasedActivityPatternElement>();
	
		List<List<PatternActivity>>  tours = asTours(activityWeek.getPatternActivities());
	
		for (List<PatternActivity> tour : tours) {
		
			assert !tour.isEmpty();
			
			if(tour.size()==1 && tour.get(0).getActivityType()==ActivityType.HOME) {
				continue;
			}
		
			assert tour.get(0).getActivityType()!=ActivityType.HOME;
			
			if(!isSupertour(tour)) {
				
				TourPattern tourPattern = asTourPattern(tour);
				
				elements.add(tourPattern);
				
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
			
			return last.getStarttime() - first.getStarttime() > 24*60;
	}
	
	private static SuperTourPattern asSuperTourPattern(List<PatternActivity> tour) {
		// TODO Auto-generated method stub
		
		System.out.println("Supertourpattern not yet implemented");
		
		return null;
	}

	private static TourPattern asTourPattern(List<PatternActivity> fulltour) {
		
		assert fulltour.get(fulltour.size()-1).getActivityType() == ActivityType.HOME;
		
		List<PatternActivity> tour =  new ArrayList<PatternActivity>(fulltour.subList(0, fulltour.size()-1));
		
		// 1. Hauptaktivität bestimmen
		// 2. Aktivitäten vor Hauptaktivität bestiimen
		// 3. Aktivitäten nach Hauptaktivität bestiimen
		// 4. Aktivitäten auf Subtouren bestimmen
		
		
		PatternActivity homeActivity = homeActivity(fulltour);
		List<PatternActivity> mainPatternActivity = mainActivity(tour);
		
		ActivityType typeOfMainActivity = mainPatternActivity.get(0).getActivityType();
		DayOfWeek day = mainPatternActivity.get(0).getWeekDayType();
		
	
		PatternActivity firstPartOfMainPatternActivity = mainPatternActivity.get(0);
		PatternActivity  lastPartOfMainPatternActivity = mainPatternActivity.get(mainPatternActivity.size()-1);
		
		List<Activity> activitiesBefore = activitiesBeforeMainActivity(tour, firstPartOfMainPatternActivity);
		List<Activity> activitiesAfter = activitiesAfterMainActivity(tour, lastPartOfMainPatternActivity);
		List<Activity> activitiesOnSubtour = activitiesOnSubtour(tour, firstPartOfMainPatternActivity, lastPartOfMainPatternActivity);
	
		assert containsSubtour(tour,typeOfMainActivity) || activitiesBefore.size() + activitiesAfter.size() + 1 == tour.size();
		
		Time expectedEnd = SimpleTime.ofMinutes(homeActivity.getStarttime());
		
		Activity mainActivity = containsSubtour(tour,typeOfMainActivity) 
														? SplitActivity.fromPatternActivities(typeOfMainActivity, tour)
														: fromPatternActivity(mainPatternActivity.get(0));
		
		return new TourPattern(day, mainActivity, expectedEnd, activitiesBefore, activitiesAfter, activitiesOnSubtour);
	}
	
	private static List<Activity> activitiesOnSubtour(List<PatternActivity> tour,
			PatternActivity firstPartOfMainPatternActivity, PatternActivity lastPartOfMainPatternActivity) {
		
		assert firstPartOfMainPatternActivity.getActivityType() == lastPartOfMainPatternActivity.getActivityType();
		ActivityType typeOfMainActivity	= firstPartOfMainPatternActivity.getActivityType();
		
		int first = tour.indexOf(firstPartOfMainPatternActivity);
		int last = tour.lastIndexOf(lastPartOfMainPatternActivity);
		
		List<PatternActivity> reduced = tour.subList(first,last);
		
		
		if (reduced.isEmpty()) {
			return new ArrayList<Activity>();
		} else {
			return reduced.stream()
			.filter(x -> x.getActivityType() == typeOfMainActivity)
			.map(x -> fromPatternActivity(x))
			.collect(Collectors.toList());
		}
		
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
			
			activities.add(fromPatternActivity(activity));
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
			
			activities.add(fromPatternActivity(activity));
		}
		
		return activities;
	}

	private static PatternActivity homeActivity(List<PatternActivity> tour) {
		PatternActivity homeActivity = tour.get(tour.size()-1);
		assert homeActivity != null; 
		assert homeActivity.getActivityType() == ActivityType.HOME;
		
		return homeActivity;
	}

	private static List<PatternActivity> mainActivity(List<PatternActivity> tour) {
		
		Map<ActivityType,Integer> activityCount = new LinkedHashMap<ActivityType,Integer>();
		Map<ActivityType,Integer> totalDuration = new LinkedHashMap<ActivityType,Integer>();
		
		for(PatternActivity activity : tour) {
			
			ActivityType type = activity.getActivityType();
			int duration = activity.getDuration();
			
			assert type != ActivityType.HOME;
			assert !type.isHomeActivity();
		
			activityCount.putIfAbsent(type, 0);
			totalDuration.putIfAbsent(type, 0);
			
			activityCount.put(type, 1+activityCount.get(type));
			totalDuration.put(type, duration+totalDuration.get(type));
		}
		
		ActivityType typeOfMainActivity = activityCount.containsKey(ActivityType.WORK) ? ActivityType.WORK 
																			: activityCount.containsKey(ActivityType.EDUCATION) ? ActivityType.EDUCATION 
																			: activityCount.containsKey(ActivityType.SERVICE) ? ActivityType.SERVICE 
																		: typeOfMaxDuration(totalDuration);
			
		 List<PatternActivity> mainActivity = tour.stream()
											.filter(x -> x.getActivityType() == typeOfMainActivity).collect(Collectors.toList());
		
		// TODO: prüfen, ob Subtouren enthalten
		// zwei Aktivitäten desselben Typs UND Dauer dieser Aktivitäten ist größer als Zeitraum dazwischen
		
		 
		if(containsSubtour(tour, typeOfMainActivity)) {
			return mainActivity;
		} else {
			return new ArrayList<PatternActivity>(mainActivity.subList(0,1));
		}
	}


	private static boolean containsSubtour(List<PatternActivity> tour, ActivityType typeOfMainActivity) {
		
		return !subtours(tour, typeOfMainActivity).isEmpty();
	}
	
	private static List<List<PatternActivity>> subtours(List<PatternActivity> tour, ActivityType typeOfMainActivity) {

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
		
			return subtours;
	}

	private static ActivityType typeOfMaxDuration(Map<ActivityType, Integer> totalDuration) {

		return totalDuration.entrySet().stream().reduce(
					new AbstractMap.SimpleImmutableEntry<ActivityType,Integer>(null,-1),
						(a,b) -> a.getValue() > b.getValue() ? a : b
					).getKey();
	}

	static List<List<PatternActivity>> asTours(List<PatternActivity> activities) {
		List<List<PatternActivity>>  tours = new ArrayList<List<PatternActivity>>();
		
		List<PatternActivity>  tour = new ArrayList<PatternActivity>();
		
		for(PatternActivity activity : activities) {
		
			tour.add(activity);
			
			if (activity.getActivityType() == ActivityType.HOME ) {
				tours.add(tour);
				tour = new ArrayList<PatternActivity>();
			}
		}
		
		if (!tour.isEmpty()) {
				tours.add(tour);
		}
		
		return tours;
	}
	
	static Activity fromPatternActivity(PatternActivity activity) {
	
		return new SimpleActivity(
									activity.getActivityType(), 
									SimpleTime.ofMinutes(activity.getStarttime()),
									RelativeTime.ofMinutes(activity.getDuration())
						);
	}
	
	public static void main(String[] args) {
		System.out.println("test");
		
		 PaneldataReader reader = new  PaneldataReader(new File("data/stuttgart/data_stuttgart_prefs_2018_03_16.csv"));
		 
		 List<PersonOfPanelData> persons = reader.readPersons();
		 System.out.println(persons.get(0).getActivityPattern());
		 
		 for(int i=0; i<5; i++) {
		 
		
		 List<ActivityOfPanelData> activityOfPanelData = ActivityOfPanelData.parseActivities(persons.get(i).getActivityPattern());
		 PatternActivityWeek week = PatternActivityWeek.fromActivityOfPanelData(activityOfPanelData);
		
		 TourBasedActivityPatternCreator.asTours(week.getPatternActivities());
		 
		 TourBasedActivityPattern tourPattern = TourBasedActivityPatternCreator.fromPatternActivityWeek(week);
		 
		 System.out.println(activityOfPanelData);
		 System.out.println(week);
		 System.out.println(tourPattern);
		 }
	}
}
