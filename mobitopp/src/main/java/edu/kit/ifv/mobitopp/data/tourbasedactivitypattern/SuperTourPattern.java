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
		return !outboundTripActivities.isEmpty() ? outboundTripActivities.get(0).plannedStart()
				: surrogateHomeActivity.plannedStart() ;
	}

	@Override
	public List<Activity> asActivities() {
		List<Activity> activities =  new ArrayList<Activity>();
		
		activities.addAll(outboundTripActivities);
		
		if(surrogateHomeActivity instanceof SplitActivity) {
			
			List<SimpleActivity> homelikeActivities = ((SplitActivity) surrogateHomeActivity).parts() ;
			
			assert homelikeActivities.size() == nonlocalActivityPattern.size()+1;
			
			for (int i=0; i<homelikeActivities.size()-1;i++) {
				
				activities.add(homelikeActivities.get(i));
				activities.addAll(nonlocalActivityPattern.get(i).asActivities());
			}
			activities.add(homelikeActivities.get(homelikeActivities.size()-1));
		
		}
		
		activities.addAll(inboundTripActivities);
		
		return activities;
	}

	@Override
	public List<ExtendedPatternActivity> asPatternActivities(int tournr) {
		
		List<ExtendedPatternActivity> activities = asActivities().stream()
								.map(x -> ExtendedPatternActivity.fromActivity(tournr, x.activityType() == surrogateHomeActivity.activityType(), true, x))
								.collect(Collectors.toList());
		
		return activities;
	}
	
	@Override
	public String toString() {
		return "SuperTourPattern(\n"
					+ surrogateHomeActivity + "\n"
					+ outboundTripActivities + "\n"
					+ nonlocalActivityPattern + "\n"
					+ inboundTripActivities + "\n"
				+ ")\n";
	}

	public static SuperTourPattern fromPatternActivities(List<? extends PatternActivity> activities) {
		
		ActivityType surrogateHomeActivityType = surrogateHomeActivityType(activities);
		
		Activity surrogateHomeActivity = findSurrogateHome(surrogateHomeActivityType, activities);
		
		// 1. activities zerlegen in "touren", die durch HOME/SurrogateHome beendet werden
		// 2. erste "Tour": outbound
		//    letzte "Tour": inbound
		//    übrige Touren: non-local
		// 3. ACHTUNG: non-local können Subtouren enthalten
		
		List<List<PatternActivity>> tours =  splitIntoTours(surrogateHomeActivityType, activities);
		
		
		List<Activity> outboundTripActivities = tourAsActivities(surrogateHomeActivityType, tours.get(0));
		List<Activity> inboundTripActivities = tourAsActivities(surrogateHomeActivityType, tours.get(tours.size()-1));

		List<TourPattern> nonLocalTours = toursAsTourPattern(surrogateHomeActivityType,tours.subList(1, tours.size()-1));
		
		SuperTourPattern stp = new SuperTourPattern(activities.get(0).getWeekDayType(),
																	surrogateHomeActivity,
																	outboundTripActivities,
																	nonLocalTours,
																	inboundTripActivities);
		
		
// System.out.println(stp);	
// System.out.println(stp.asActivities());	
		
		return stp;
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
				tp = TourPattern.NOTOUR;
			}
			
			nonLocalTours.add(tp);
		}
	
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

	private static ActivityType surrogateHomeActivityType(List<? extends PatternActivity> supertour) {
		
		assert supertour.get(supertour.size()-1).getActivityType() == ActivityType.HOME : supertour;
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
		List<? extends PatternActivity> activities) {
		
		List<PatternActivity> surrogate = activities.stream()
												.filter(x -> x.getActivityType() == surrogateHomeActivityType)
												.collect(Collectors.toList());
	
		assert !surrogate.isEmpty();
		
		return surrogate.size() == 1 ? SimpleActivity.fromPatternActivity(surrogate.get(0))
				: SplitActivity.fromPatternActivities(surrogateHomeActivityType, surrogate);
	}
	
	private static List<List<PatternActivity>> splitIntoTours(ActivityType surrogateHomeActivityType, List<? extends PatternActivity> activities) {
		
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
		
		return tours;
	}

	public static TourBasedActivityPatternElement fromExtendedPatternActivities(List<ExtendedPatternActivity> tour) {
		
		// Achtung: Tour enthält am Ende keine HOME-Aktivität
		
		List<ExtendedPatternActivity> padded_tour = new ArrayList<>(tour);
		padded_tour.add(ExtendedPatternActivity.STAYATHOME_ACTIVITY);
		
		 return fromPatternActivities(padded_tour);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((inboundTripActivities == null) ? 0 : inboundTripActivities.hashCode());
		result = prime * result + ((nonlocalActivityPattern == null) ? 0 : nonlocalActivityPattern.hashCode());
		result = prime * result + ((outboundTripActivities == null) ? 0 : outboundTripActivities.hashCode());
		result = prime * result + ((surrogateHomeActivity == null) ? 0 : surrogateHomeActivity.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SuperTourPattern other = (SuperTourPattern) obj;
		if (day != other.day)
			return false;
		if (inboundTripActivities == null) {
			if (other.inboundTripActivities != null)
				return false;
		} else if (!inboundTripActivities.equals(other.inboundTripActivities))
			return false;
		if (nonlocalActivityPattern == null) {
			if (other.nonlocalActivityPattern != null)
				return false;
		} else if (!nonlocalActivityPattern.equals(other.nonlocalActivityPattern))
			return false;
		if (outboundTripActivities == null) {
			if (other.outboundTripActivities != null)
				return false;
		} else if (!outboundTripActivities.equals(other.outboundTripActivities))
			return false;
		if (surrogateHomeActivity == null) {
			if (other.surrogateHomeActivity != null)
				return false;
		} else if (!surrogateHomeActivity.equals(other.surrogateHomeActivity))
			return false;
		return true;
	}
	
	

}
