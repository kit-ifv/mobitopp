package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

public class SuperTourPattern implements TourBasedActivityPatternElement {

	
	public final DayOfWeek day;
	public final Activity surrogateHomeActivity;
	public final List<Activity> outboundTripActivities;
	public final List<TourPattern> nonlocalActivityPattern;
	public final List<Activity> inboundTripActivities;
	
	private static Set<ActivityType> possibleSurrogateHomeActivities
		= new LinkedHashSet<ActivityType>(Arrays.asList(ActivityType.HOME, ActivityType.OTHERHOME, ActivityType.PRIVATE_VISIT));
	
	public SuperTourPattern(
		final DayOfWeek day,
		final Activity surrogateHomeActivity, 
		final List<Activity> outboundTripActivities,
		final List<TourPattern> nonlocalActivityPattern,
		final List<Activity> inboundTripActivities
	) {
		this.day = day;
		this.surrogateHomeActivity = surrogateHomeActivity;
		this.outboundTripActivities = Collections.unmodifiableList(outboundTripActivities);
		this.inboundTripActivities = Collections.unmodifiableList(inboundTripActivities);
		this.nonlocalActivityPattern = Collections.unmodifiableList(nonlocalActivityPattern);
	}

	public DayOfWeek startDay() {
		return  day;
	}

	@Override
	public Time start() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Activity> asActivities() {
		// TODO Auto-generated method stub
		return new ArrayList<Activity>();
	}

	@Override
	public List<ExtendedPatternActivity> asPatternActivities(int i) {
		// TODO Auto-generated method stub
		return new ArrayList<ExtendedPatternActivity>();
	}

	// TODO: ausprogrammieren
	public static SuperTourPattern fromPatternActivities(List<PatternActivity> activities) {
		
		ActivityType surrogateHomeActivityType = surrogateHomeActivityType(activities);
		
		// System.out.println("Surrogate home: " + surrogateHomeActivityType + "\n");
		
		Activity surrogateHomeActivity = findSurrogateHome(surrogateHomeActivityType, activities);
		
		// Was passiert, wenn zweimal OTHERHOME hintereinander vorkommt?
		
		// 1. activities zerlegen in "touren", die durch HOME/SurrogateHome beendet werden
		// 2. erste "Tour": outbound
		//    letzte "Tour": inbound
		//    übrige Touren: non-local
		// 3. ACHTUNG: non-local können Subtouren enthalten
		
		List<List<PatternActivity>> tours =  splitIntoTours(surrogateHomeActivityType, activities);
		
		
		List<Activity> outboundTripActivities = tourAsActivities(surrogateHomeActivityType, tours.get(0));
		List<Activity> inboundTripActivities = tourAsActivities(surrogateHomeActivityType, tours.get(tours.size()-1));
		
		// System.out.println(activities + "\n");
		// System.out.println(tours + "\n");
		// System.out.println(tours.subList(1, tours.size()-1) + "\n");

		List<TourPattern> nonLocalTours = toursAsTourPattern(surrogateHomeActivityType,tours.subList(1, tours.size()-1));
	
		
		return new SuperTourPattern(activities.get(0).getWeekDayType(),
																	surrogateHomeActivity,
																	outboundTripActivities,
																	nonLocalTours,
																	inboundTripActivities);
	}


	private static List<TourPattern> toursAsTourPattern(ActivityType surrogateHomeActivityType,
			List<List<PatternActivity>> tours) {
		
		List<TourPattern> nonLocalTours = new ArrayList<TourPattern>();
				
		for(List<PatternActivity> tour : tours) {
			
			assert !tour.isEmpty();
			
			TourPattern tp;
			
			if (tour.size() > 1) {
		
				tp =  TourPattern.fromPatternActivities(surrogateHomeActivityType, tour);
			} else {
				tp = TourPattern.degenerateTour(tour.get(0));
			}
			
			nonLocalTours.add(tp);
		}
	
		/*
		List<TourPattern> nonLocalTours = tours.stream()
								.map(x -> TourPattern.fromPatternActivities(surrogateHomeActivityType, x))
								.collect(Collectors.toList());
		*/
		
		return nonLocalTours;
	}

	private static List<Activity> tourAsActivities(ActivityType surrogateHomeActivityType, List<PatternActivity> activities) {
		
		assert !activities.isEmpty();
		assert activities.get(activities.size()-1).getActivityType() == surrogateHomeActivityType
					|| activities.get(activities.size()-1).getActivityType() == ActivityType.HOME;
		
		List<Activity> result = activities.subList(0,activities.size()-1).stream()
													.map(x -> SimpleActivity.fromPatternActivity(x))
													.collect(Collectors.toList());
		
		return result;
	}

	private static ActivityType surrogateHomeActivityType(List<PatternActivity> supertour) {
		
		assert supertour.get(supertour.size()-1).getActivityType() == ActivityType.HOME;
		assert supertour.get(0).getActivityType() != ActivityType.HOME;
		
		Map<ActivityType,Integer> activityCount = new LinkedHashMap<ActivityType,Integer>();
		
		for(PatternActivity activity : supertour) {
		
			if(possibleSurrogateHomeActivities.contains(activity.getActivityType())) {
				
				ActivityType type = activity.getActivityType();
				
				activityCount.putIfAbsent(type, 0);
			
				activityCount.put(type, 1+activityCount.get(type));
			}
		}
		
		assert activityCount.getOrDefault(ActivityType.HOME,0) == 1;
		
		assert activityCount.getOrDefault(ActivityType.OTHERHOME,0) >= 1
				|| activityCount.getOrDefault(ActivityType.PRIVATE_VISIT,0) >= 1 
				 : ( activityCount + "\n" + supertour);
	
		
		ActivityType typeOfSurrogateHomeActivity = activityCount.containsKey(ActivityType.OTHERHOME) ? ActivityType.OTHERHOME 
																			: ActivityType.PRIVATE_VISIT ;
		
		return typeOfSurrogateHomeActivity;
	}
	
	private static Activity findSurrogateHome(ActivityType surrogateHomeActivityType,
			List<PatternActivity> activities) {
		
		List<PatternActivity> surrogate = activities.stream()
												.filter(x -> x.getActivityType() == surrogateHomeActivityType)
												.collect(Collectors.toList());
	
		assert !surrogate.isEmpty();
		
		return surrogate.size() == 1 ? SimpleActivity.fromPatternActivity(surrogate.get(0))
				: SplitActivity.fromPatternActivities(surrogateHomeActivityType, surrogate);
	}
	
	private static List<List<PatternActivity>> splitIntoTours(ActivityType surrogateHomeActivityType, List<PatternActivity> activities) {
		
		assert activities.get(activities.size()-1).getActivityType() == ActivityType.HOME;
		
		List<List<PatternActivity>> tours = new ArrayList<List<PatternActivity>>();
		
		List<PatternActivity> currentTour = new ArrayList<PatternActivity>();
		tours.add(currentTour);
		
		
		for(PatternActivity activity : activities) {
		
			currentTour.add(activity);
			
			if (activity.getActivityType() == surrogateHomeActivityType) {
		
				currentTour = new  ArrayList<PatternActivity>();;
				tours.add(currentTour);
			}
		}
		// System.out.println(tours);
		
		return tours;
	}

}
