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
import edu.kit.ifv.mobitopp.util.panel.ActivityOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PaneldataReader;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class TourBasedActivityPatternCreator {


  public static TourBasedActivityPattern fromPatternActivityWeek(PatternActivityWeek activityWeek) {
    List<TourBasedActivityPatternElement> elements = new ArrayList<>();

    PatternActivity startActivity = activityWeek.getPatternActivities().get(0);
    if (ActivityType.HOME.equals(startActivity.getActivityType())) {
      assert startActivity.getActivityType() == ActivityType.HOME : startActivity;
      elements
          .add(new HomeActivity(startActivity.getWeekDayType(),
              SimpleActivity.fromPatternActivity(startActivity)));
    }
    
		List<List<PatternActivity>>  tours = asTours(activityWeek);
	
    for (List<PatternActivity> tour : tours) {

      assert !tour.isEmpty();
      if (tour.size() == 1 && tour.get(0).getActivityType() == ActivityType.HOME) {
        PatternActivity home = tour.get(0);
        elements
            .add(new HomeActivity(home.getWeekDayType(), SimpleActivity.fromPatternActivity(home)));
      } else {

        assert tour.get(0).getActivityType() != ActivityType.HOME;

        if (!isSupertour(tour)) {

          TourPattern tourPattern = TourPattern.fromPatternActivities(tour);

          elements.add(tourPattern);

        } else {
          SuperTourPattern tourPattern = asSuperTourPattern(tour);
          elements.add(tourPattern);
        }

        PatternActivity home = tour.get(tour.size() - 1);
        assert home.getActivityType() == ActivityType.HOME;
        elements
            .add(new HomeActivity(home.getWeekDayType(), SimpleActivity.fromPatternActivity(home)));
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
			boolean containsSurrogateHome = ! tour.stream()
																		.filter(x -> (x.getActivityType() == ActivityType.OTHERHOME || x.getActivityType() == ActivityType.PRIVATE_VISIT))
																		.collect(Collectors.toList()).isEmpty();
		*/
			
			long cntOtherHome = tour.stream()
																		.filter(x -> x.getActivityType() == ActivityType.OTHERHOME)
																		.count();
			
			long cntVisit = tour.stream()
																		.filter(x -> x.getActivityType() == ActivityType.OTHERHOME)
																		.count();
			
			return last.startTime().differenceTo(first.startTime()).toMinutes() > 24*60
//						&& containsSurrogateHome;
						&& (cntOtherHome >=2 || cntVisit >= 2);
	}
	
	private static SuperTourPattern asSuperTourPattern(List<PatternActivity> tour) {
		
		return SuperTourPattern.fromPatternActivities(tour);
	}

	public static TourPattern asTourPattern(List<PatternActivity> fulltour) {
		return TourPattern.fromPatternActivities(fulltour);
	}
	
	static ActivityType typeOfMaxDuration(Map<ActivityType, Integer> totalDuration) {

		return totalDuration.entrySet().stream().reduce(
					new AbstractMap.SimpleImmutableEntry<ActivityType,Integer>(null,-1),
						(a,b) -> a.getValue() > b.getValue() ? a : b
					).getKey();
	}

  static List<List<PatternActivity>> asTours(PatternActivityWeek week) {
    List<List<PatternActivity>> tours = new ArrayList<List<PatternActivity>>();

    List<PatternActivity> tour = new ArrayList<PatternActivity>();

    boolean firstTrip = true;

    for (PatternActivity activity : week.getPatternActivities()) {

      tour.add(activity);

      if (activity.getActivityType() == ActivityType.HOME) {

        // if (tour.size() >= 2) {
        if (!firstTrip) {
          tours.add(tour);
        }
        tour = new ArrayList<PatternActivity>();
      }
      firstTrip = false;
    }

    if (!tour.isEmpty()) {
      tours.add(tour);
    }

    return tours;
  }
	
	static List<Activity> fromPatternActivity(List<PatternActivity> activities) {
		
		return  activities.stream().map(x -> SimpleActivity.fromPatternActivity(x)).collect(Collectors.toList());
	}
	
	public static void main(String[] args) {
		System.out.println("test");
			
			 PaneldataReader reader = new  PaneldataReader(new File("data/stuttgart/data_stuttgart_prefs_2018_03_16.csv"));
			 
			 List<PersonOfPanelData> persons = reader.readPersons();
			 
			 for(int i=0; i<persons.size(); i++) {
				 
				String pattern = persons.get(i).getActivityPattern();
			 
			 // System.out.println(persons.get(i).getActivityPattern());
			 List<ActivityOfPanelData> activityOfPanelData = ActivityOfPanelData.parseActivities(persons.get(i).getActivityPattern());
			 PatternActivityWeek week = PatternActivityWeek.fromActivityOfPanelData(activityOfPanelData);
			 
			 // assert pattern.equals(week.asCSV()) || pattern.isEmpty()  : ("\nA " + pattern + "\nB " + week.asCSV());
			
			 TourBasedActivityPatternCreator.asTours(week);
			 
			 TourBasedActivityPattern tourPattern = TourBasedActivityPatternCreator.fromPatternActivityWeek(week);
			 
			 tourPattern.asActivities();
			 List<ExtendedPatternActivity> epas = tourPattern.asPatternActivities();
			
			 
			 assert activityOfPanelData.isEmpty() || activityOfPanelData.size() == week.getPatternActivities().size() : (activityOfPanelData.size() + " : " + tourPattern.asActivities().size() ) ;
			 
			 assert pattern.equals(patternToString(tourPattern)) || pattern.isEmpty()  : ("\nA " + pattern 
					 + "\nB " + patternToString(tourPattern) + "\n" + tourPattern + "\n" + pretty(week)
					 + "\n" + tourPattern.asPatternActivities()
					 );
			 
			 assert week.getPatternActivities().size() == tourPattern.asActivities().size() : (activityOfPanelData.size() + " : " + tourPattern.asActivities().size() ) ;
			 
			 TourBasedActivityPattern tp = TourBasedActivityPattern.fromExtendedPatternActivities(epas);
			 
			 assert tourPattern.equals(tp) : ("\n" + tourPattern + "\n" + tp);
		 
		 }
	}

	public static String patternToString(TourBasedActivityPattern tourPattern) {
		
		List<Activity> activities = tourPattern.asActivities();
		
		String s = activities.stream().map(x -> asCSV(x)).collect(Collectors.joining(";"));
		
		return s;
	}

	private static String asCSV(Activity x) {
		
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
	
	private static String pretty(PatternActivityWeek week) {
		String result = "";
		for(PatternActivity activity : week.getPatternActivities()) {
			result += activity + "\n";
		}
		return result;
	}
}
