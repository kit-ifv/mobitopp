package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

public abstract class WithinTourModeChoiceParameterOnlyFlexibleModesBase 
	implements WithinTourModeChoiceParameter {

	protected final Map<String,Double> parameterGeneric = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterWalk = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterPassenger = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterPt = new LinkedHashMap<String,Double>();

	public WithinTourModeChoiceParameterOnlyFlexibleModesBase() {
	}

	@Override
	public Map<Mode, Map<String, Double>> gatherAttributes(
		Set<Mode> modes, 
		Tour tour, 
		Mode tourMode, 
		Person person, 
		Zone source, 
		Zone destination,
		ActivityIfc prevActivity, 
		ActivityIfc nextActivity, 
		ImpedanceIfc impedance
	) {
		
			assert tour.contains(nextActivity);
				
				ZoneId sourceOid = source.getId();
				ZoneId destOid = destination.getId();
			
					Time date = prevActivity.calculatePlannedEndDate();
			
					double distance 		= Math.max(0.1, impedance.getDistance(sourceOid, destOid)/1000.0);
			
					assert !Double.isNaN(distance);
					assert distance > 0.0f;
			
					double intrazonal = source == destination || distance <= 1.0f ? 1 : 0;
			
					double female = person.gender() == Gender.FEMALE ? 1 : 0;
			
					double day_SA 		= date.weekDay() == DayOfWeek.SATURDAY ? 1 : 0;
					double day_SU 		= date.weekDay() == DayOfWeek.SUNDAY ? 1 : 0;
			
					double employment_EDUCATION 				= person.employment() == Employment.EDUCATION ? 1 : 0;
					double employment_INFANT 		  			= person.employment() == Employment.INFANT ? 1 : 0;
					double employment_UNEMPLOYED 	  			= person.employment() == Employment.UNEMPLOYED ? 1 : 0;
					double employment_NONE 							= person.employment() == Employment.NONE ? 1 : 0;
					double employment_HOMEKEEPER 				= person.employment() == Employment.HOMEKEEPER ? 1 : 0;
					double employment_PARTTIME 					= person.employment() == Employment.PARTTIME ? 1 : 0;
					double employment_FULLTIME 					= person.employment() == Employment.FULLTIME ? 1 : 0;
					double employment_RETIRED 					= person.employment() == Employment.RETIRED ? 1 : 0;
					double employment_STUDENT_PRIMARY 	= person.employment() == Employment.STUDENT_PRIMARY ? 1 : 0;
					double employment_STUDENT_SECONDARY	= person.employment() == Employment.STUDENT_SECONDARY ? 1 : 0;
					double employment_STUDENT_TERTIARY 	= person.employment() == Employment.STUDENT_TERTIARY ? 1 : 0;
			
			
					double purpose_WORK 		 		 = nextActivity.activityType().isWorkActivity() ? 1 : 0; // reference case
					double purpose_BUSINESS 		 = nextActivity.activityType().isBusinessActivity() ? 1 : 0;
					double purpose_EDUCATION 	 = nextActivity.activityType().isEducationActivity() ? 1 : 0;
					double purpose_HOME 				 			= nextActivity.activityType().isHomeActivity() ? 1 : 0;
					double purpose_LEISURE_INDOOR 		= nextActivity.activityType() == ActivityType.LEISURE_INDOOR ? 1 : 0;
					double purpose_LEISURE_OUTDOOR 	= nextActivity.activityType() == ActivityType.LEISURE_OUTDOOR ? 1 : 0;
					double purpose_LEISURE_OTHER 		= nextActivity.activityType() == ActivityType.LEISURE_OTHER ? 1 : 0;
					double purpose_LEISURE_WALK 			= nextActivity.activityType() == ActivityType.LEISURE_WALK ? 1 : 0;
					double purpose_OTHER 			 			= nextActivity.activityType() == ActivityType.UNDEFINED ? 1 : 0;
					double purpose_SERVICE 		 			= nextActivity.activityType() == ActivityType.SERVICE ? 1 : 0;
					double purpose_SHOPPING_DAILY 		= nextActivity.activityType() == ActivityType.SHOPPING_DAILY ? 1 : 0;
					double purpose_SHOPPING_OTHER 		= nextActivity.activityType() == ActivityType.SHOPPING_OTHER ? 1 : 0;
					double purpose_PRIVATE_BUSINESS 	= nextActivity.activityType() == ActivityType.PRIVATE_BUSINESS ? 1 : 0;
					double purpose_PRIVATE_VISIT 		= nextActivity.activityType() == ActivityType.PRIVATE_VISIT ? 1 : 0;
					
					double tourpurpose_WORK 		 		 = tour.mainActivity().activityType().isWorkActivity() ? 1 : 0; // reference case
					double tourpurpose_BUSINESS 		 = tour.mainActivity().activityType().isBusinessActivity() ? 1 : 0;
					double tourpurpose_EDUCATION 	 = tour.mainActivity().activityType().isEducationActivity() ? 1 : 0;
					double tourpurpose_HOME 				 			= tour.mainActivity().activityType().isHomeActivity() ? 1 : 0;
					double tourpurpose_LEISURE_INDOOR 		= tour.mainActivity().activityType() == ActivityType.LEISURE_INDOOR ? 1 : 0;
					double tourpurpose_LEISURE_OUTDOOR 	= tour.mainActivity().activityType() == ActivityType.LEISURE_OUTDOOR ? 1 : 0;
					double tourpurpose_LEISURE_OTHER 		= tour.mainActivity().activityType() == ActivityType.LEISURE_OTHER ? 1 : 0;
					double tourpurpose_LEISURE_WALK 			= tour.mainActivity().activityType() == ActivityType.LEISURE_WALK ? 1 : 0;
					double tourpurpose_OTHER 			 			= tour.mainActivity().activityType() == ActivityType.UNDEFINED ? 1 : 0;
					double tourpurpose_SERVICE 		 			= tour.mainActivity().activityType() == ActivityType.SERVICE ? 1 : 0;
					double tourpurpose_SHOPPING_DAILY 		= tour.mainActivity().activityType() == ActivityType.SHOPPING_DAILY ? 1 : 0;
					double tourpurpose_SHOPPING_OTHER 		= tour.mainActivity().activityType() == ActivityType.SHOPPING_OTHER ? 1 : 0;
					double tourpurpose_PRIVATE_BUSINESS 	= tour.mainActivity().activityType() == ActivityType.PRIVATE_BUSINESS ? 1 : 0;
					double tourpurpose_PRIVATE_VISIT 		= tour.mainActivity().activityType() == ActivityType.PRIVATE_VISIT ? 1 : 0;
			
					double age_0to9 						=                           person.age() <=  9 ? 1 : 0;
					double age_10to17 					= person.age() >= 10  && person.age() <= 17 ? 1 : 0;
					double age_18to25 					= person.age() >= 18  && person.age() <= 25 ? 1 : 0;
					double age_26to35 					= person.age() >= 26  && person.age() <= 35 ? 1 : 0;
					double age_36to50 					= person.age() >= 36  && person.age() <= 50 ? 1 : 0;
					double age_51to60 					= person.age() >= 51  && person.age() <= 60 ? 1 : 0;
					double age_61to70 					= person.age() >= 61  && person.age() <= 70 ? 1 : 0;
					double age_71plus 					= person.age() >= 71                           ? 1 : 0;
					
					
					boolean withinTour = tour.lastActivity() != nextActivity;
					
				
					Map<String,Double> attributes = new LinkedHashMap<String,Double>();	
					
			
					attributes.put("CONST", 	1.0);
					attributes.put("INTRAZONAL", 	intrazonal);
					attributes.put("FEMALE", female);
					attributes.put("DAY_SA",  day_SA);
					attributes.put("DAY_SU",  day_SU);

					attributes.put("EMPLOYMENT_EDUCATION",  				employment_EDUCATION);
					attributes.put("EMPLOYMENT_FULLTIME",  					employment_FULLTIME);
					attributes.put("EMPLOYMENT_INFANT",  						employment_INFANT);
					attributes.put("EMPLOYMENT_UNEMPLOYED",  					employment_UNEMPLOYED);
					attributes.put("EMPLOYMENT_NONE",  							employment_NONE);
					attributes.put("EMPLOYMENT_HOMEKEEPER",  			employment_HOMEKEEPER);
					attributes.put("EMPLOYMENT_PARTTIME",  					employment_PARTTIME);
					attributes.put("EMPLOYMENT_RETIRED",  					employment_RETIRED);
					attributes.put("EMPLOYMENT_STUDENT_PRIMARY",  	employment_STUDENT_PRIMARY);
					attributes.put("EMPLOYMENT_STUDENT_SECONDARY", 	employment_STUDENT_SECONDARY);
					attributes.put("EMPLOYMENT_STUDENT_TERTIARY", 	employment_STUDENT_TERTIARY);

				
				
					attributes.put("FEMALE",  	female);
					
					attributes.put("AGE_0TO9",  	age_0to9);
					attributes.put("AGE_10TO17",  age_10to17);
					attributes.put("AGE_18TO25",  age_18to25);
					attributes.put("AGE_26TO35",  age_26to35);
					attributes.put("AGE_36TO50",  age_36to50);
					attributes.put("AGE_51TO60",  age_51to60);
					attributes.put("AGE_61TO70",  age_61to70);
					attributes.put("AGE_71PLUS",  age_71plus);
					
					attributes.put("TIME:FEMALE", female);
					attributes.put("COST:FEMALE", female);
					 	
					attributes.put("TIME:AGE_0TO9",  	age_0to9);
					attributes.put("TIME:AGE_10TO17",  age_10to17);
					attributes.put("TIME:AGE_18TO25",  age_18to25);
					attributes.put("TIME:AGE_26TO35",  age_26to35);
					attributes.put("TIME:AGE_36TO50",  age_36to50);
					attributes.put("TIME:AGE_51TO60",  age_51to60);
					attributes.put("TIME:AGE_61TO70",  age_61to70);
					attributes.put("TIME:AGE_71PLUS",  age_71plus);
						
					attributes.put("COST:AGE_0TO9",  	age_0to9);
					attributes.put("COST:AGE_10TO17",  age_10to17);
					attributes.put("COST:AGE_18TO25",  age_18to25);
					attributes.put("COST:AGE_26TO35",  age_26to35);
					attributes.put("COST:AGE_36TO50",  age_36to50);
					attributes.put("COST:AGE_51TO60",  age_51to60);
					attributes.put("COST:AGE_61TO70",  age_61to70);
					attributes.put("COST:AGE_71PLUS",  age_71plus);
				
			
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
					
					attributes.put("TOURPURPOSE_WORK",  						tourpurpose_WORK);
					attributes.put("TOURPURPOSE_BUSINESS",  				tourpurpose_BUSINESS);
					attributes.put("TOURPURPOSE_EDUCATION",  				tourpurpose_EDUCATION);
					attributes.put("TOURPURPOSE_HOME",  						tourpurpose_HOME);
					attributes.put("TOURPURPOSE_LEISURE_INDOOR",  	tourpurpose_LEISURE_INDOOR);
					attributes.put("TOURPURPOSE_LEISURE_OUTDOOR",  	tourpurpose_LEISURE_OUTDOOR);
					attributes.put("TOURPURPOSE_LEISURE_OTHER",  		tourpurpose_LEISURE_OTHER);
					attributes.put("TOURPURPOSE_LEISURE_WALK",  		tourpurpose_LEISURE_WALK);
					attributes.put("TOURPURPOSE_OTHER",  						tourpurpose_OTHER);
					attributes.put("TOURPURPOSE_PRIVATE_BUSINESS",  tourpurpose_PRIVATE_BUSINESS);
					attributes.put("TOURPURPOSE_PRIVATE_VISIT",  		tourpurpose_PRIVATE_VISIT);
					attributes.put("TOURPURPOSE_SERVICE",  					tourpurpose_SERVICE);
					attributes.put("TOURPURPOSE_SHOPPING_DAILY",  	tourpurpose_SHOPPING_DAILY);
					attributes.put("TOURPURPOSE_SHOPPING_OTHER",  	tourpurpose_SHOPPING_OTHER);
					
					attributes.put("WITHINTOUR",  				withinTour ? 1.0 : 0.0);
					
					
					attributes.put("TOURMODE_WALK",  				tourMode == StandardMode.PEDESTRIAN ? 1.0 : 0.0);
					attributes.put("TOURMODE_BIKE",  				tourMode == StandardMode.BIKE ? 1.0 : 0.0);
					attributes.put("TOURMODE_CAR",  				tourMode == StandardMode.CAR ? 1.0 : 0.0);
					attributes.put("TOURMODE_PASSENGER", 		tourMode == StandardMode.PASSENGER ? 1.0 : 0.0);
					attributes.put("TOURMODE_PT",  					tourMode == StandardMode.PUBLICTRANSPORT ? 1.0 : 0.0);
					
					attributes.put("TOURMODE_WALK_WITHINTOUR",  		withinTour && tourMode == StandardMode.PEDESTRIAN ? 1.0 : 0.0);
					attributes.put("TOURMODE_BIKE_WITHINTOUR",  		withinTour && tourMode == StandardMode.BIKE ? 1.0 : 0.0);
					attributes.put("TOURMODE_CAR_WITHINTOUR",  			withinTour && tourMode == StandardMode.CAR ? 1.0 : 0.0);
					attributes.put("TOURMODE_PASSENGER_WITHINTOUR", withinTour && tourMode == StandardMode.PASSENGER ? 1.0 : 0.0);
					attributes.put("TOURMODE_PT_WITHINTOUR",  			withinTour && tourMode == StandardMode.PUBLICTRANSPORT ? 1.0 : 0.0);
					
					Map<Mode,Map<String,Double>> modeAttributes = new LinkedHashMap<Mode,Map<String,Double>>();
			
					for (Mode mode : modes) {
			
						Map<String,Double> attrib = new LinkedHashMap<String,Double>(attributes);
			
						double time 	= impedance.getTravelTime(sourceOid, destOid, mode, date);
			
						double cost 	= (mode==StandardMode.PUBLICTRANSPORT && person.hasCommuterTicket()) ? 0.0
															: impedance.getTravelCost(sourceOid, destOid, mode, date);
						
			
			
						// attrib.put("DISTANCE_KM", distance);
					 	attrib.put("TIME", time);
						attrib.put("COST", cost);
						
						
						attrib.put("ISTOURMODE",  					mode == tourMode ? 1.0 : 0.0);
						attrib.put("ISTOURMODE_WITHINTOUR",  	mode == tourMode && withinTour ? 1.0 : 0.0);
						
					
						modeAttributes.put(mode, attrib);
					}
			
					return  modeAttributes;
			}

}