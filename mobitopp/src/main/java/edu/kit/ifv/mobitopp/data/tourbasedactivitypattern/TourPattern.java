package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

public class TourPattern implements TourBasedActivityPatternElement {
	
	public final DayOfWeek day;
	public final Activity mainActivity;
	public final List<Activity> outboundTripActivities;
	public final List<Activity> inboundTripActivities;
	public final List<List<Activity>> subtourActivities;

	public static final TourPattern NOTOUR = new TourPattern();
	
	private TourPattern() {
	  day = null;	
	  mainActivity = null;	
	  outboundTripActivities = new ArrayList<>();	
	  inboundTripActivities = new ArrayList<>();	
	  subtourActivities = new ArrayList<>();	
	}
	
	public TourPattern(
		DayOfWeek day,
		Activity mainActivity, 
		List<Activity> outboundTripActivities,
		List<Activity> inboundTripActivities,
		List<List<Activity>> subtourActivities
	) {
		
		assert mainActivity instanceof SimpleActivity && subtourActivities.isEmpty()
				|| mainActivity instanceof SplitActivity && !subtourActivities.isEmpty();
		
		assert day == mainActivity.plannedStart().weekDay() : (day + " + " + mainActivity.plannedStart().weekDay() ) ;
		assert day.equals(mainActivity.plannedStart().weekDay());
		
		assert mainActivity instanceof SimpleActivity ||  !subtourActivities.isEmpty();
		assert mainActivity instanceof SimpleActivity ||  !subtourActivities.get(0).isEmpty() :
				( day + "\n" + mainActivity + "\n" + subtourActivities);
		assert mainActivity instanceof SimpleActivity 
				||  day == subtourActivities.get(0).get(0).plannedStart().weekDay()
				||  day.next() == subtourActivities.get(0).get(0).plannedStart().weekDay();
		
		this.day = day;
		this.mainActivity = mainActivity;
		this.outboundTripActivities = Collections.unmodifiableList(outboundTripActivities);
		this.inboundTripActivities = Collections.unmodifiableList(inboundTripActivities);
		this.subtourActivities = Collections.unmodifiableList(subtourActivities);
	}

	@Override
	public DayOfWeek startDay() {
		return day;
	}
	
	@Override
	public String toString() {
		if(this==NOTOUR) return "TourPattern.NOTOUR";
		
		return "TourPattern( " + day + ", " + mainActivity 
	//			+ ", " + expectedEnd 
				+ ", ["  
					+  outboundTripActivities.size() + "," 
					+  inboundTripActivities.size() + "," 
					+  subtourActivities.size() 
//					+ "\n" +  outboundTripActivities
//					+ "\n" +  inboundTripActivities
//					+ "\n" +  subtourActivities
				+ "])";
	}

	@Override
	public Time start() {
		
		if (!outboundTripActivities.isEmpty()) {
			Activity next = outboundTripActivities.get(0);
			return next.plannedStart().minus(next.expectedTripDuration());
		}
		
		return mainActivity.plannedStart().minus(mainActivity.expectedTripDuration());
	}
	
	@Override
	public List<Activity> asActivities() {
		List<Activity> activities = new ArrayList<Activity>();
		
		if(this==NOTOUR) { return Collections.emptyList(); }
		
		activities.addAll(outboundTripActivities);
		
		if (numberOfSubtours() == 0) {
			activities.add(mainActivity);
		} else {
			
			assert mainActivity instanceof SplitActivity;
			
			assert numberOfSubtours()+1 == ((SplitActivity)mainActivity).parts().size();

			List<SimpleActivity> parts =  ((SplitActivity)mainActivity).parts();
			
			for(int i=0; i<numberOfSubtours(); i++) {
				activities.add(parts.get(i));
				
				for(Activity subtourActivity :  subtourActivities.get(i)) {
					activities.add(subtourActivity);
				}
				
			}
			
			assert numberOfSubtours() ==  parts.size()-1 
					: (numberOfSubtours() + " : " + parts.size());
			// activities.add(parts.get(numberOfSubtours()+1));
			activities.add(parts.get(numberOfSubtours()));
			
		}
		
		activities.addAll(inboundTripActivities);
	
		return activities;
	}
	
	private int numberOfSubtours() {
		return subtourActivities.size();
	}
	
	

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((inboundTripActivities == null) ? 0 : inboundTripActivities.hashCode());
		result = prime * result + ((mainActivity == null) ? 0 : mainActivity.hashCode());
		result = prime * result + ((outboundTripActivities == null) ? 0 : outboundTripActivities.hashCode());
		result = prime * result + ((subtourActivities == null) ? 0 : subtourActivities.hashCode());
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
		TourPattern other = (TourPattern) obj;
		if (day != other.day)
			return false;
		if (inboundTripActivities == null) {
			if (other.inboundTripActivities != null)
				return false;
		} else if (!inboundTripActivities.equals(other.inboundTripActivities))
			return false;
		if (mainActivity == null) {
			if (other.mainActivity != null)
				return false;
		} else if (!mainActivity.equals(other.mainActivity))
			return false;
		if (outboundTripActivities == null) {
			if (other.outboundTripActivities != null)
				return false;
		} else if (!outboundTripActivities.equals(other.outboundTripActivities))
			return false;
		if (subtourActivities == null) {
			if (other.subtourActivities != null)
				return false;
		} else if (!subtourActivities.equals(other.subtourActivities))
			return false;
		return true;
	}

	@Override
	public List<ExtendedPatternActivity> asPatternActivities(int tournr) {
		List<ExtendedPatternActivity> activities = new ArrayList<ExtendedPatternActivity>();
		
		for(Activity act : outboundTripActivities) {
			activities.add(ExtendedPatternActivity.fromActivity(tournr, false, act));
		}
		
		if (numberOfSubtours() == 0) {
			activities.add(ExtendedPatternActivity.fromActivity(tournr, true, mainActivity));
		} else {
			
			assert mainActivity instanceof SplitActivity;
			
			assert numberOfSubtours()+1 == ((SplitActivity)mainActivity).parts().size() : 
				(numberOfSubtours() + " : " + ((SplitActivity)mainActivity).parts().size() 
						+ "\n" + this );

			List<SimpleActivity> parts =  ((SplitActivity)mainActivity).parts();
			
			for(int i=0; i<numberOfSubtours(); i++) {
				activities.add(ExtendedPatternActivity.fromActivity(tournr, true, parts.get(i)));
				
				for(Activity subtourActivity :  subtourActivities.get(i)) {
					activities.add(ExtendedPatternActivity.fromActivity(tournr, false, subtourActivity));
				}
				
			}
			activities.add(ExtendedPatternActivity.fromActivity(tournr, true, parts.get(parts.size()-1)));
			
		}
		
		for(Activity act : inboundTripActivities) {
			activities.add(ExtendedPatternActivity.fromActivity(tournr, false, act));
		}
	
		return activities;
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
				
				subtours.add(new ArrayList<PatternActivity>(potentialSubtour));
					
			}
		
			return subtours;
	}

	static boolean containsSubtour(ActivityType homeActivityType, List<PatternActivity> tour) {
		
		return !subtoursOfMainActivity(homeActivityType, tour).isEmpty();
	}

	static List<List<PatternActivity>> subtoursWithLeadingAndTrailingMainActivity(ActivityType homeActivityType, List<PatternActivity> tour) {
		
		 ActivityType typeOfMainActivity = mainActivityType(homeActivityType, tour);
		 
		 Set<ActivityType> subtoursPossible = new LinkedHashSet<>(Arrays.asList(ActivityType.WORK, ActivityType.EDUCATION, ActivityType.SERVICE));
		 
		 if (subtoursPossible.contains(typeOfMainActivity)) {
			 List<List<PatternActivity>> potentialSubtours = potentialSubtours(tour, typeOfMainActivity);
			 return potentialSubtours;
		 }
		
		 return new ArrayList<List<PatternActivity>>();
	}

	static List<List<Activity>> asListOfActivities(List<List<PatternActivity>> subtourActivities) {
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


	static List<Activity> activitiesBeforeMainActivity(List<PatternActivity> tour,
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

	static List<Activity> activitiesAfterMainActivity(List<PatternActivity> tour,
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

	static ActivityType mainActivityType(ActivityType homeActivityType, List<PatternActivity> tour) {
		
		assert tour.get(0).getActivityType() != ActivityType.HOME;
		
		Map<ActivityType,Integer> activityCount = new LinkedHashMap<ActivityType,Integer>();
		Map<ActivityType,Integer> totalDuration = new LinkedHashMap<ActivityType,Integer>();
		
		for(PatternActivity activity : tour) {
			
			if(activity.getActivityType() == homeActivityType) {
				continue;
			}
			
			ActivityType type = activity.getActivityType();
			int duration = activity.getDuration();
			
			assert type != homeActivityType;
		
			activityCount.putIfAbsent(type, 0);
			totalDuration.putIfAbsent(type, 0);
			
			activityCount.put(type, 1+activityCount.get(type));
			totalDuration.put(type, duration+totalDuration.get(type));
		}
		
		ActivityType typeOfMainActivity = activityCount.containsKey(ActivityType.WORK) ? ActivityType.WORK 
																			: activityCount.containsKey(ActivityType.EDUCATION) ? ActivityType.EDUCATION 
																			: activityCount.containsKey(ActivityType.SERVICE) ? ActivityType.SERVICE 
																		: TourBasedActivityPatternCreator.typeOfMaxDuration(totalDuration);
		
		return typeOfMainActivity;
	}
	
	static List<List<PatternActivity>> subtoursOfMainActivity(List<PatternActivity> tour) {
		return subtoursOfMainActivity(ActivityType.HOME, tour);
	}
	
	static List<List<PatternActivity>> subtoursOfMainActivity(ActivityType homeActivityType, List<PatternActivity> tour) {

		return partsAndsubtoursOfMainActivity(homeActivityType, tour).subtours;
	}
	
	static PartsAndSubtoursOfMainActivity partsAndsubtoursOfMainActivity(ActivityType homeActivityType, List<PatternActivity> tour) {
		
		
		ActivityType typeOfMainActivity = mainActivityType(homeActivityType, tour);
		
	 List<PatternActivity> mainActivity = tour.stream()
											.filter(x -> x.getActivityType() == typeOfMainActivity).collect(Collectors.toList());
		
		List<PatternActivity> partsOfMainActivity = new ArrayList<PatternActivity>();
		List<List<PatternActivity>> subtoursOfMain = new ArrayList<List<PatternActivity>>();
		
		List<List<PatternActivity>> subtours = subtoursWithLeadingAndTrailingMainActivity(homeActivityType, tour);
		
		if(subtours.isEmpty()) {
			assert mainActivity.size() >= 1 : ("\n" + mainActivity + "\n" + subtours + "\n" + tour);
			
			partsOfMainActivity = new ArrayList<PatternActivity>(mainActivity.subList(0,1));
			
			return new PartsAndSubtoursOfMainActivity(partsOfMainActivity, subtoursOfMain);
		} 
		 
		for(List<PatternActivity> subtour: subtours) {
			
			assert subtour.size() >= 3;
		
			assert subtour.get(0).getActivityType() == typeOfMainActivity;
			assert subtour.get(subtour.size()-1).getActivityType() == typeOfMainActivity;
			
			if (partsOfMainActivity.isEmpty()) {
				
				List<PatternActivity> firstSubtour = subtour;
				
				partsOfMainActivity.add(firstSubtour.get(0));
				partsOfMainActivity.add(firstSubtour.get(firstSubtour.size()-1));
				
				subtoursOfMain.add(new ArrayList<PatternActivity>(subtour.subList(1,subtour.size()-1)));
			} else {
				List<PatternActivity> otherSubtour = subtour;
				
				if(partsOfMainActivity.get(partsOfMainActivity.size()-1).equals(otherSubtour.get(0))) {
					
					partsOfMainActivity.add(otherSubtour.get(otherSubtour.size()-1));
					
					subtoursOfMain.add(new ArrayList<PatternActivity>(subtour.subList(1,subtour.size()-1)));
				} else {
					break;
				}
			}
			
		}
		
		return new PartsAndSubtoursOfMainActivity(partsOfMainActivity, subtoursOfMain);
	}

	static List<PatternActivity> mainActivity(ActivityType homeActivityType, List<PatternActivity> tour) {
		return partsAndsubtoursOfMainActivity(homeActivityType, tour).parts;
	}
	

	public static TourPattern fromPatternActivities(List<PatternActivity> fulltour) {
			return fromPatternActivities(ActivityType.HOME, fulltour);
	}
	
	public static TourPattern fromPatternActivities(ActivityType homeActivityType, List<PatternActivity> fulltour) {
		
		assert fulltour.get(0).getActivityType() != homeActivityType;
		assert fulltour.get(fulltour.size()-1).getActivityType() == homeActivityType;
		
		List<PatternActivity> tour =  new ArrayList<PatternActivity>(fulltour.subList(0, fulltour.size()-1));
		
		// 1. Hauptaktivität bestimmen
		// 2. Aktivitäten vor Hauptaktivität bestimmen
		// 3. Aktivitäten nach Hauptaktivität bestiimen
		// 4. Aktivitäten auf Subtouren bestimmen
		
		ActivityType typeOfMainActivity = mainActivityType(homeActivityType, fulltour);
		
		List<PatternActivity> mainPatternActivity = mainActivity(homeActivityType, fulltour);
		List<List<PatternActivity>> subtourActivities = subtoursOfMainActivity(homeActivityType, fulltour);
		
		assert mainPatternActivity.size() >=1;
		
		assert mainPatternActivity.size() <= subtourActivities.size()+1 
				: (mainPatternActivity.size() + ": " + subtourActivities.size()
						+ "\n" + fulltour + "\n" + mainPatternActivity + "\n" + subtourActivities);
		
		
		DayOfWeek day = mainPatternActivity.get(0).getWeekDayType();
		
		 assert subtourActivities.isEmpty() 
		 		|| ( homeActivityType==ActivityType.HOME && TourBasedActivityPatternCreator.isSupertour(fulltour) )
		 		|| subtourActivities.get(0).get(0).getWeekDayType() == day 
		 		|| subtourActivities.get(0).get(0).getWeekDayType() == day.next() : prettyPrint(fulltour);
		
		PatternActivity firstPartOfMainPatternActivity = mainPatternActivity.get(0);
		PatternActivity  lastPartOfMainPatternActivity = mainPatternActivity.get(mainPatternActivity.size()-1);
		
		List<Activity> activitiesBefore = activitiesBeforeMainActivity(tour, firstPartOfMainPatternActivity);
		List<Activity> activitiesAfter = activitiesAfterMainActivity(tour, lastPartOfMainPatternActivity);
		List<List<Activity>> activitiesOnSubtour = asListOfActivities(subtourActivities);
	
		assert containsSubtour(homeActivityType, tour) || activitiesBefore.size() + activitiesAfter.size() + 1 == tour.size();
		
		Activity mainActivity = mainPatternActivity.size() > 1
														? SplitActivity.fromPatternActivities(typeOfMainActivity, mainPatternActivity)
														: SimpleActivity.fromPatternActivity(mainPatternActivity.get(0));
														
		assert mainActivity instanceof SimpleActivity  
			|| ((SplitActivity)mainActivity).parts().size() == mainPatternActivity.size();
														
		assert day == mainPatternActivity.get(0).getWeekDayType();
		assert day == mainActivity.plannedStart().weekDay() : (day + " :" + mainActivity) ;
		
		return new TourPattern(day, mainActivity, activitiesBefore, activitiesAfter, activitiesOnSubtour);
	}

	
	private static String prettyPrint(List<PatternActivity> fulltour) {
		String result = "\n";
		
		for(PatternActivity act : fulltour) {
		 result += act.toString() + "\n";
		}
		
		return result;
	}

	public static TourPattern fromExtendedPatternActivities(List<ExtendedPatternActivity> activities) {
	
		List<ExtendedPatternActivity> partsOfMainActivity = findPartsOfMainActivity(activities);
		assert !partsOfMainActivity.isEmpty() : activities;
		
		assert partsOfMainActivity.size() == 1 || !partsOfMainActivity.get(0).equals(partsOfMainActivity.get(1)) : activities;
		
		Activity mainActivity =  partsOfMainActivity.size()==1 
					? SimpleActivity.fromPatternActivity(partsOfMainActivity.get(0))
					: SplitActivity.fromPatternActivities(partsOfMainActivity.get(0).getActivityType(),partsOfMainActivity) ;
		DayOfWeek day 	= mainActivity.plannedStart().weekDay();
		
		List<Activity> outboundActivities = activitiesBefore(partsOfMainActivity.get(0), activities);
		List<Activity> inboundActivities = activitiesAfter(partsOfMainActivity.get(partsOfMainActivity.size()-1), activities);
		List<List<Activity>> subtourActivities = activitiesOnSubtours(partsOfMainActivity, activities);
		
		return new TourPattern(day, mainActivity, outboundActivities, inboundActivities, subtourActivities);
		
	}

	private static List<List<Activity>> activitiesOnSubtours(List<ExtendedPatternActivity> partsOfMainActivity,
			List<ExtendedPatternActivity> activities) {
		assert !partsOfMainActivity.isEmpty();
		
		assert partsOfMainActivity.size() == 1 || !partsOfMainActivity.get(0).equals(partsOfMainActivity.get(1)) : partsOfMainActivity;
		
		if (partsOfMainActivity.size()==1) {
			return new ArrayList<List<Activity>>();
		}
		
		assert partsOfMainActivity.size() >= 2;
		
		List<List<Activity>> results = new ArrayList<List<Activity>>();
		
		for(int i=0; i< partsOfMainActivity.size()-1; i++) {
			
			ExtendedPatternActivity first = partsOfMainActivity.get(i);
			ExtendedPatternActivity next = partsOfMainActivity.get(i+1);
			
			assert !first.equals(next) : partsOfMainActivity;
	
			/*
			assert activities.indexOf(first) < activities.indexOf(next) 
				: (activities.indexOf(first) + " : " + activities.indexOf(next)
						+ "\n same?" + (first==next)
						+ "\n equals?" + (first.equals(next))
						+ "\n first=" + first
						+ "\n next=" + next
						+ "\n first_idx=" + partsOfMainActivity.indexOf(first)
						+ "\n next_idx=" + partsOfMainActivity.indexOf(next)
						);
						*/
			
			assert activities.indexOf(first) < activities.indexOf(next) 
				: (activities.indexOf(first) + " : " + activities.indexOf(next)
						+ "\n" + first + "\n" + next + "\n" + partsOfMainActivity + "\n" + activities);
			
			List<ExtendedPatternActivity> subtour = activities.subList(1+activities.indexOf(first), activities.indexOf(next));
			
			List<Activity> subtourActivities =
					subtour.stream().map(x -> SimpleActivity.fromPatternActivity(x)).collect(Collectors.toList());
			
			results.add(subtourActivities);
		}
		
		return results;
	}

	private static List<Activity> activitiesAfter(ExtendedPatternActivity mainActivity,
			Collection<ExtendedPatternActivity> activities) {
		
		List<ExtendedPatternActivity> reversed = new LinkedList<>(activities);
		Collections.reverse(reversed);
		
		List<Activity> result = new ArrayList<Activity>();
		
		for (ExtendedPatternActivity act : reversed) {
			if(act==mainActivity) { break; }
			
			result.add(SimpleActivity.fromPatternActivity(act));
		}
		
		Collections.reverse(result);
		
		return result;
	}

	private static List<Activity> activitiesBefore(ExtendedPatternActivity mainActivity,
		Collection<ExtendedPatternActivity> activities) {
		
		List<Activity> result = new ArrayList<Activity>();
		
		for (ExtendedPatternActivity act : activities) {
			if(act==mainActivity) { break; }
			
			result.add(SimpleActivity.fromPatternActivity(act));
		}
		
		return result;
	}

	private static List<ExtendedPatternActivity> findPartsOfMainActivity(Collection<ExtendedPatternActivity> activities) {

		return activities.stream().filter(x -> x.isMainActivity()).collect(Collectors.toList());
	}
	
	public static TourPattern degenerateTour(PatternActivity activity) {
	 return new TourPattern(activity.getWeekDayType(), SimpleActivity.fromPatternActivity(activity), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());	
	}

}
