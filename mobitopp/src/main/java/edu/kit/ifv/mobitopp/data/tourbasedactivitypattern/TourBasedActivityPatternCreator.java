package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.util.panel.ActivityOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PaneldataReader;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class TourBasedActivityPatternCreator {


	public static TourBasedActivityPattern stayAtHome(DayOfWeek startDay, DayOfWeek endDay) {
		
		
		List<TourBasedActivityPatternElement> elements = new ArrayList<TourBasedActivityPatternElement>();
		
		for(DayOfWeek day=startDay; day!=endDay.next(); day.next()) {
			elements.add(new DayStayAtHomePattern(day));
		}
		
		return new TourBasedActivityPattern(elements);
	}

	/*
	public static TourBasedActivityPattern fromPatternActivityWeek(PatternActivityWeek activityWeek) {
	
		List<TourBasedActivityPatternElement> elements = new ArrayList<TourBasedActivityPatternElement>();
		
		
		for (DayOfWeek day : DayOfWeek.values()) {
			
			List<PatternActivity> activities = activityWeek.getPatternActivities(day);
			
			if(activities.size()==0) {
				elements.add(new StayAtHomePattern(1));
			}
			
			if(activities.size()==1) {
				assert activities.get(0).getActivityType().isHomeActivity();
				// assert activities.get(0).getActivityType() == ActivityType.HOME;
				// !! evt. Home-Activity erst am nächsten Tag
			}
			
			if(activities.size()==2) {
				assert activities.get(1).getActivityType().isHomeActivity();
				assert !activities.get(0).getActivityType().isHomeActivity();
				
				Activity activity = fromPatternActivity(activities.get(0));
				
				elements.add(DayPattern.SimpleDay(activity));
			}
			
			if(activities.size()>2) {
				// Home-Activities suchen -- in Touren zerlegen
				// Subtouren identifizieren!!
				
				List<List<PatternActivity>> tours = asTours(activities);
			}
		}
		
		
		elements.add(new StayAtHomePattern(7));
		
		return new TourBasedActivityPattern(elements);
	}
*/
	public static TourBasedActivityPattern fromPatternActivityWeek(PatternActivityWeek activityWeek) {
		
		Map<DayOfWeek,List<TourPattern>> toursByDay = new LinkedHashMap<DayOfWeek,List<TourPattern>>();
		Map<DayOfWeek,MultiDayPattern> multiDayTours = new LinkedHashMap<DayOfWeek,MultiDayPattern>();
		
		// TODO: CHECKTHIS: Kann ein Tag erst eine Tour enthalten, und anschließend eine MultiDayTour???
		// z.B: morgens noch Brötchen kaufen gehen, zurück nach Hause, anschließend in den Urlaub
		// Lösung: MultiDayPattern kann noch Touren davor und danach enthalten
		
		// besser: kein DayPattern -- stattdessem Liste mit TourPattern und SuperTourPattern
		
		for(DayOfWeek day : DayOfWeek.values() ) {
			toursByDay.put(day,  new ArrayList<TourPattern>());
		}
	
	
		List<List<PatternActivity>>  tours = asTours(activityWeek.getPatternActivities());
	
		for (List<PatternActivity> tour : tours) {
			
			if(!isSupertour(tour)) {
				
				DayOfWeek day = tour.get(0).getWeekDayType();
				
				TourPattern tourPattern = asTourPattern(tour);
				
				toursByDay.get(day).add(tourPattern);
				
			} else {
				// TODO: Supertour
				// MultiDayPattern
			}
		}
		// 1). in "Touren" (Listen von aktivitäten Zerlegen)
		// 2). Testen ob Suprtour
		// 3). Tag bestimmen, Tour(en) erzeugen, zu Tagesprogramm zusammenfassen
		
		List<TourBasedActivityPatternElement> elements = new ArrayList<TourBasedActivityPatternElement>();
		
		for(DayOfWeek day : DayOfWeek.values() ) {
			List<TourPattern> toursOfDay = toursByDay.get(day);
		
				if (!toursOfDay.isEmpty()) {
					DayTourPattern dayTour = new DayTourPattern(day, toursOfDay);
					elements.add(dayTour);
				} else {
					// TODO: Supertouren beachtne
					
					DayStayAtHomePattern stayAtHome = new DayStayAtHomePattern(day);
					elements.add(stayAtHome);
				}
		}
		
		return new TourBasedActivityPattern(elements);
	}
	
	private static TourPattern asTourPattern(List<PatternActivity> tour) {
		// TODO Auto-generated method stub
		
		// 1. Hauptaktivität bestimmen
		// 2. Aktivitäten vor Hauptaktivität bestiimen
		// 3. Aktivitäten nach Hauptaktivität bestiimen
		// 4. Aktivitäten auf Subtouren bestimmen
		return null;
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
	
		return new Activity(
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
		
		 List<ActivityOfPanelData> activityOfPanelData = ActivityOfPanelData.parseActivities(persons.get(0).getActivityPattern());
		 PatternActivityWeek week = PatternActivityWeek.fromActivityOfPanelData(activityOfPanelData);
		
		 TourBasedActivityPatternCreator.asTours(week.getPatternActivities());
		 
		 System.out.println(activityOfPanelData);
		 System.out.println(week);
	}
}
