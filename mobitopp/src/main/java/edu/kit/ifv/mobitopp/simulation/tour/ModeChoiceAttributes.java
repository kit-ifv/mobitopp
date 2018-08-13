package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.HouseholdType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public class ModeChoiceAttributes {

	protected final List<String> compound =  Arrays.asList(new String[] {
		//																					"TIME", "SQRT_TIME", "COST", "SQRT_COST"
																							"TIME", "COST"
			});

	public ModeChoiceAttributes() {
		super();
	}

	public Map<Mode, Map<String, Double>> gatherAttributes(
		Set<Mode> modes, 
		Tour tour, 
		Person person, 
		Map<Mode, Double> preferences, 
		ImpedanceIfc impedance
	) {
		
//		System.out.println(preferences);
	
			ActivityIfc mainActivity = tour.mainActivity();
	
			int source = person.homeZone().getOid();
			
			assert tour.mainActivity().isLocationSet() || tour instanceof Subtour;
			assert tour.mainActivity().isLocationSet() || tour.firstActivity().isLocationSet();
			
			int target = tour.mainActivity().zone().getOid();
	
			Time date = tour.mainActivity().startDate();
			Household hh = person.household();
	
			double distance 		= Math.max(0.1, impedance.getDistance(source, target)/1000.0);
	
			assert !Double.isNaN(distance);
			assert distance > 0.0f;
			
			Map<Mode, Double> sharesAsPreferences = person.modeChoicePrefsSurvey().asMap();
	
	/*
			double time_km 	= impedance.getTravelTime(source, target, mode, date)/distance;
	
			double cost_km 	= (mode==Mode.PUBLICTRANSPORT && person.hasCommuterTicket()) ? 0.0
													: impedance.getTravelCost(source, target, mode, date)/distance;
	*/
	
			// double intrazonal = source == target || distance <= 1.7f ? 1 : 0;
			double intrazonal = source == target || distance <= 1.0f ? 1 : 0;
			// double intrazonal = source == target ? 1 : 0;
	
			double commuting_ticket = person.hasCommuterTicket() ? 1 : 0;
	
			double no_driving_licence = !person.hasPersonalCar() && !person.hasAccessToCar()  ? 1 : 0;  
			
			double has_bicycle = person.hasBike() ? 1 : 0;
			
			double num_activities = tour.numberOfTrips() - 1;
			assert num_activities >= 0 : tour;
			
			Mode tourMode = tour.firstActivity().mode();
			
			double contains_service = tour.containsActivityOf(ActivityType.SERVICE) ? 1 : 0;
			double contains_strolling = tour.containsActivityOf(ActivityType.LEISURE_WALK) ? 1 : 0;
			double contains_business = tour.containsActivityOf(ActivityType.BUSINESS) ? 1 : 0;
			double contains_leisure = tour.containsActivityOf(ActivityType.LEISURE)  ||
																tour.containsActivityOf(ActivityType.LEISURE_INDOOR) ||
																tour.containsActivityOf(ActivityType.LEISURE_OUTDOOR) ||
																tour.containsActivityOf(ActivityType.LEISURE_OTHER) 		? 1 : 0;
			double contains_private_business = tour.containsActivityOf(ActivityType.PRIVATE_BUSINESS) ? 1 : 0;
			double contains_shopping = tour.containsActivityOf(ActivityType.SHOPPING)  ||
																tour.containsActivityOf(ActivityType.SHOPPING_DAILY) ||
																tour.containsActivityOf(ActivityType.SHOPPING_OTHER) 		? 1 : 0;
			double contains_visit = tour.containsActivityOf(ActivityType.PRIVATE_VISIT) ? 1 : 0;
			
	
			double female = person.gender() == Gender.FEMALE ? 1 : 0;
	
			double day_SA 		= date.weekDay() == DayOfWeek.SATURDAY ? 1 : 0;
			double day_SU 		= date.weekDay() == DayOfWeek.SUNDAY ? 1 : 0;
	
			double employment_EDUCATION 				= person.employment() == Employment.EDUCATION ? 1 : 0;
			double employment_INFANT 		  			= person.employment() == Employment.INFANT ? 1 : 0;
			double employment_UNEMPLOYED 	  		= person.employment() == Employment.UNEMPLOYED ? 1 : 0;
			double employment_NONE 							= person.employment() == Employment.NONE ? 1 : 0;
			double employment_HOMEKEEPER  			= person.employment() == Employment.HOMEKEEPER ? 1 : 0;
			double employment_PARTTIME 					= person.employment() == Employment.PARTTIME ? 1 : 0;
			double employment_MARGINAL 					= person.employment() == Employment.MARGINAL ? 1 : 0;
			double employment_FULLTIME 					= person.employment() == Employment.FULLTIME ? 1 : 0;
			double employment_RETIRED 					= person.employment() == Employment.RETIRED ? 1 : 0;
			double employment_STUDENT_PRIMARY 	= person.employment() == Employment.STUDENT_PRIMARY ? 1 : 0;
			double employment_STUDENT_SECONDARY	= person.employment() == Employment.STUDENT_SECONDARY ? 1 : 0;
			double employment_STUDENT_TERTIARY 	= person.employment() == Employment.STUDENT_TERTIARY ? 1 : 0;
	
	
			double purpose_WORK 		 		 = mainActivity.activityType().isWorkActivity() ? 1 : 0; // reference case
			double purpose_BUSINESS 		 = mainActivity.activityType().isBusinessActivity() ? 1 : 0;
			double purpose_EDUCATION 	 = mainActivity.activityType() == ActivityType.EDUCATION_PRIMARY
															|| mainActivity.activityType() == ActivityType.EDUCATION_SECONDARY
															|| mainActivity.activityType() == ActivityType.EDUCATION_TERTIARY
															|| mainActivity.activityType() == ActivityType.EDUCATION_OCCUP
															|| mainActivity.activityType() == ActivityType.EDUCATION ? 1 : 0;
	
			double purpose_HOME 				 			= mainActivity.activityType().isHomeActivity() ? 1 : 0;
			double purpose_LEISURE_INDOOR 		= mainActivity.activityType() == ActivityType.LEISURE_INDOOR ? 1 : 0;
			double purpose_LEISURE_OUTDOOR 	= mainActivity.activityType() == ActivityType.LEISURE_OUTDOOR ? 1 : 0;
			double purpose_LEISURE_OTHER 		= mainActivity.activityType() == ActivityType.LEISURE_OTHER ? 1 : 0;
			double purpose_LEISURE_WALK 			= mainActivity.activityType() == ActivityType.LEISURE_WALK ? 1 : 0;
			double purpose_OTHER 			 			= mainActivity.activityType() == ActivityType.UNDEFINED ? 1 : 0;
			double purpose_SERVICE 		 			= mainActivity.activityType() == ActivityType.SERVICE ? 1 : 0;
			double purpose_SHOPPING_DAILY 		= mainActivity.activityType() == ActivityType.SHOPPING_DAILY ? 1 : 0;
			double purpose_SHOPPING_OTHER 		= mainActivity.activityType() == ActivityType.SHOPPING_OTHER ? 1 : 0;
			double purpose_PRIVATE_BUSINESS 	= mainActivity.activityType() == ActivityType.PRIVATE_BUSINESS ? 1 : 0;
			double purpose_PRIVATE_VISIT 		= mainActivity.activityType() == ActivityType.PRIVATE_VISIT ? 1 : 0;
	
			double age_0to9 						=                           person.age() <=  9 ? 1 : 0;
			double age_10to17 					= person.age() >= 10  && person.age() <= 17 ? 1 : 0;
			double age_18to25 					= person.age() >= 18  && person.age() <= 25 ? 1 : 0;
			double age_26to35 					= person.age() >= 26  && person.age() <= 35 ? 1 : 0;
			double age_36to50 					= person.age() >= 36  && person.age() <= 50 ? 1 : 0;
			double age_51to60 					= person.age() >= 51  && person.age() <= 60 ? 1 : 0;
			double age_61to70 					= person.age() >= 61  && person.age() <= 70 ? 1 : 0;
			double age_71plus 					= person.age() >= 71                           ? 1 : 0;
		
			// TODO: methode type nach Household verschieben!
			double hhtype_single					=	HouseholdType.type(hh) == HouseholdType.SINGLE 			? 1 : 0;
			double hhtype_couple					= HouseholdType.type(hh) == HouseholdType.COUPLE 			? 1 : 0;
			double hhtype_kids_0to7				= HouseholdType.type(hh) == HouseholdType.KIDS0TO7 		? 1 : 0;
			double hhtype_kids_8to12			= HouseholdType.type(hh) == HouseholdType.KIDS8TO12 	? 1 : 0;
			double hhtype_kids_13to18			= HouseholdType.type(hh) == HouseholdType.KIDS13PLUS 	? 1 : 0;
			double hhtype_multiadult			= HouseholdType.type(hh) == HouseholdType.MULTIADULT 	? 1 : 0;
			
			Mode previousMode = tour.hasPreviousTour() ? tour.previousTour().mode() : Mode.UNDEFINED;
			double initialTour = tour.hasPreviousTour() ? 0.0 : 1.0;
			
			double shares_walking = sharesAsPreferences.get(Mode.PEDESTRIAN);
			double shares_cycling = sharesAsPreferences.get(Mode.BIKE);
			double shares_cardriver = sharesAsPreferences.get(Mode.CAR);
			double shares_carpassenger = sharesAsPreferences.get(Mode.PASSENGER);
			double shares_publictransport = sharesAsPreferences.get(Mode.PUBLICTRANSPORT);
			
			TourAwareActivitySchedule schedule = (TourAwareActivitySchedule) person.activitySchedule();
			
			Map<Mode,Integer> usedModes = schedule.alreadyUsedTourmodes(mainActivity);
	
		
			Map<String,Double> employment = new LinkedHashMap<String,Double>();	
			Map<String,Double> age = new LinkedHashMap<String,Double>();	
			Map<String,Double> hhtype = new LinkedHashMap<String,Double>();	
			Map<String,Double> attributes = new LinkedHashMap<String,Double>();	
			
	
			attributes.put("CONST", 	1.0);
			attributes.put("INTRAZONAL", 	intrazonal);
			// attributes.put("DIST_KM", new Double(distance));
			attributes.put("COMMUTING_TICKET", commuting_ticket);
			attributes.put("NO_DRIVING_LICENCE", no_driving_licence);
			attributes.put("HAS_BICYCLE", has_bicycle);
			
			attributes.put("NUM_ACTIVITIES", num_activities);
			attributes.put("TOUR_CONTAINS_SERVICE", contains_service);
			attributes.put("TOUR_CONTAINS_STROLLING", contains_strolling);
			attributes.put("TOUR_CONTAINS_BUSINESS", contains_business);
			attributes.put("TOUR_CONTAINS_LEISURE", contains_leisure);
			attributes.put("TOUR_CONTAINS_PRIVATE_BUSINESS", contains_private_business);
			attributes.put("TOUR_CONTAINS_SHOPPING", contains_shopping);
			attributes.put("TOUR_CONTAINS_VISIT", contains_visit);
			
			
			attributes.put("FEMALE", female);
			attributes.put("DAY_SA",  day_SA);
			attributes.put("DAY_SU",  day_SU);
			attributes.put("INITIAL_TOUR",  initialTour);
			
			attributes.put("SHARE_MODE_WALK",  shares_walking);
			attributes.put("SHARE_MODE_BIKE",  shares_cycling);
			attributes.put("SHARE_MODE_CAR",  shares_cardriver);
			attributes.put("SHARE_MODE_PASSENGER",  shares_carpassenger);
			attributes.put("SHARE_MODE_PT",  shares_publictransport);
	
			employment.put("EMPLOYMENT_EDUCATION",  				employment_EDUCATION);
			employment.put("EMPLOYMENT_FULLTIME",  					employment_FULLTIME);
			employment.put("EMPLOYMENT_INFANT",  						employment_INFANT);
			employment.put("EMPLOYMENT_UNEMPLOYED",  				employment_UNEMPLOYED);
			employment.put("EMPLOYMENT_NONE",  							employment_NONE);
			employment.put("EMPLOYMENT_HOMEKEEPER",  			  employment_HOMEKEEPER);
			employment.put("EMPLOYMENT_PARTTIME",  					employment_PARTTIME);
			employment.put("EMPLOYMENT_MARGINAL",  					employment_MARGINAL);
			employment.put("EMPLOYMENT_RETIRED",  					employment_RETIRED);
			employment.put("EMPLOYMENT_STUDENT_PRIMARY",  	employment_STUDENT_PRIMARY);
			employment.put("EMPLOYMENT_STUDENT_SECONDARY", 	employment_STUDENT_SECONDARY);
			employment.put("EMPLOYMENT_STUDENT_TERTIARY", 	employment_STUDENT_TERTIARY);
			
			for(String emp : employment.keySet()) {
				Double emp_value = employment.get(emp);
				
				attributes.put(emp, emp_value);
				
				for (String pre : this.compound) {
					
					attributes.put(pre + ":" + emp, emp_value);
				}
			}
			
			age.put("AGE_0TO9",  	age_0to9);
			age.put("AGE_10TO17",  age_10to17);
			age.put("AGE_18TO25",  age_18to25);
			age.put("AGE_26TO35",  age_26to35);
			age.put("AGE_36TO50",  age_36to50);
			age.put("AGE_51TO60",  age_51to60);
			age.put("AGE_61TO70",  age_61to70);
			age.put("AGE_71PLUS",  age_71plus);
			
			for(String ag : age.keySet()) {
				Double ag_value = age.get(ag);
				
				attributes.put(ag, ag_value);
				
				for (String pre : this.compound) {
					
					attributes.put(pre + ":" + ag, ag_value);
				}
			}
		
			hhtype.put("HHTYPE_SINGLE",  			hhtype_single);
			hhtype.put("HHTYPE_COUPLE",  			hhtype_couple);
			hhtype.put("HHTYPE_KIDS0TO7",  		hhtype_kids_0to7);
			hhtype.put("HHTYPE_KIDS8TO12",  	hhtype_kids_8to12);
			hhtype.put("HHTYPE_KIDS13PLUS",  	hhtype_kids_13to18);
			hhtype.put("HHTYPE_MULTIADULT",  	hhtype_multiadult);
			
			for(String hht : hhtype.keySet()) {
				Double hht_value = hhtype.get(hht);
				
				attributes.put(hht, hht_value);
				
				for (String pre : this.compound) {
					
					attributes.put(pre + ":" + hht, hht_value);
				}
			}
			
			
			
			attributes.put("TIME:FEMALE", female);
			attributes.put("COST:FEMALE", female);
	
	
			attributes.put("PURPOSE_WORK",  						purpose_WORK);
			attributes.put("PURPOSE_BUSINESS",  				purpose_BUSINESS);
			attributes.put("PURPOSE_EDUCATION",  				purpose_EDUCATION);
			attributes.put("PURPOSE_HOME",  						purpose_HOME);
			attributes.put("PURPOSE_LEISURE_INDOOR",  	purpose_LEISURE_INDOOR);
			attributes.put("PURPOSE_LEISURE_OUTDOOR",  	purpose_LEISURE_OUTDOOR);
			attributes.put("PURPOSE_LEISURE_OTHER",  		purpose_LEISURE_OTHER);
			attributes.put("PURPOSE_LEISURE_WALK",  		purpose_LEISURE_WALK);
			attributes.put("PURPOSE_OTHER",  						purpose_OTHER);
			attributes.put("PURPOSE_PRIVATE_BUSINESS",  purpose_PRIVATE_BUSINESS);
			attributes.put("PURPOSE_PRIVATE_VISIT",  		purpose_PRIVATE_VISIT);
			attributes.put("PURPOSE_SERVICE",  					purpose_SERVICE);
			attributes.put("PURPOSE_SHOPPING_DAILY",  	purpose_SHOPPING_DAILY);
			attributes.put("PURPOSE_SHOPPING_OTHER",  	purpose_SHOPPING_OTHER);
			attributes.put("PURPOSE_BUSINESS",  				purpose_BUSINESS);
			
			Map<Mode,Map<String,Double>> modeAttributes = new LinkedHashMap<Mode,Map<String,Double>>();
	
			for (Mode mode : modes) {
	
				Map<String,Double> attrib = new LinkedHashMap<String,Double>(attributes);
	
				double time 	= impedance.getTravelTime(source, target, mode, date);
	
				double cost 	= (mode==Mode.PUBLICTRANSPORT && person.hasCommuterTicket()) ? 0.0
													: impedance.getTravelCost(source, target, mode, date);
	
	
				attrib.put("DISTANCE_KM", distance);
			 	attrib.put("TIME", time);
			 	// attrib.put("SQRT_TIME", Math.sqrt(time));
				attrib.put("COST", cost);
				// attrib.put("SQRT_COST", Math.sqrt(cost));
				
			//	System.out.println(mode + " " + attrib);
				
				attrib.put("ISTOURMODE", mode == tourMode ? 1.0 : 0.0);
				
				attrib.put("SAME_MODE_AS_BEFORE", mode == previousMode ? 1.0 : 0.0);
				
				attrib.put("PREVIOUS_MODE_WALK", previousMode == Mode.PEDESTRIAN ? 1.0 : 0.0);
				attrib.put("PREVIOUS_MODE_BIKE", previousMode == Mode.BIKE ? 1.0 : 0.0);
				attrib.put("PREVIOUS_MODE_CAR", previousMode == Mode.CAR ? 1.0 : 0.0);
				attrib.put("PREVIOUS_MODE_PASSENGER", previousMode == Mode.PASSENGER ? 1.0 : 0.0);
				attrib.put("PREVIOUS_MODE_PT", previousMode == Mode.PUBLICTRANSPORT ? 1.0 : 0.0);
				
				assert preferences.containsKey(mode);
				assert sharesAsPreferences.containsKey(mode);
				
				attrib.put("SHARES_AS_PREFERENCE", sharesAsPreferences.get(mode));
				attrib.put("NUM_USED_BEFORE", (double) usedModes.getOrDefault(mode, 0));
				attrib.put("USED_BEFORE", usedModes.getOrDefault(mode, 0) > 0 ? 1.0 : 0.0);
				
				attrib.put("PREFERENCE", preferences.get(mode));
	
				modeAttributes.put(mode, attrib);
			}
	
			return  modeAttributes;
		}

}