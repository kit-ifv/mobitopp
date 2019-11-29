package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public class WithinTourModeChoiceAttributes 
{
	
	protected final List<String> compound = Arrays.asList(new String[] {
																							"TIME", "COST"
			});
	
	//TODO: prüfen, ob sich das mit ModeChoiceAttributes zusammenführen lässt

	public WithinTourModeChoiceAttributes() {
		super();
	}

	// TODO auf <String,Integer> umstellen
	public Map<Mode, Map<String, Double>> gatherAttributes(Set<Mode> modes, Tour tour, Mode tourMode, Person person, Zone source, Zone destination,
			ActivityIfc prevActivity, ActivityIfc nextActivity, ImpedanceIfc impedance) {
			
			assert tour.contains(nextActivity);
				
				ZoneId sourceId = source.getId();
				ZoneId destinationId = destination.getId();
			
					Time date = prevActivity.calculatePlannedEndDate();
			
					double distance 		= Math.max(0.1, impedance.getDistance(sourceId, destinationId)/1000.0);
			
					assert !Double.isNaN(distance);
					assert distance > 0.0f;
			
					double intrazonal = source == destination || distance <= 1.7f ? 1 : 0;
			
					double commuting_ticket = person.hasCommuterTicket() ? 1 : 0;
			
					double no_driving_licence = !person.hasPersonalCar() && !person.hasAccessToCar()  ? 1 : 0;  
					
					double has_bicycle = person.hasBike() ? 1 : 0;
					
					double female = person.gender() == Gender.FEMALE ? 1 : 0;
			
					double day_SA 		= date.weekDay() == DayOfWeek.SATURDAY ? 1 : 0;
					double day_SU 		= date.weekDay() == DayOfWeek.SUNDAY ? 1 : 0;
			
					double employment_EDUCATION 				= person.employment() == Employment.EDUCATION ? 1 : 0;
					double employment_INFANT 		  			= person.employment() == Employment.INFANT ? 1 : 0;
					double employment_JOBLESS 	  			= person.employment() == Employment.UNEMPLOYED ? 1 : 0;
					double employment_NONE 							= person.employment() == Employment.NONE ? 1 : 0;
					double employment_NOT_WORKING 			= person.employment() == Employment.HOMEKEEPER ? 1 : 0;
					double employment_PARTTIME 					= person.employment() == Employment.PARTTIME ? 1 : 0;
					double employment_FULLTIME 					= person.employment() == Employment.FULLTIME ? 1 : 0;
					double employment_RETIRED 					= person.employment() == Employment.RETIRED ? 1 : 0;
					double employment_STUDENT_PRIMARY 	= person.employment() == Employment.STUDENT_PRIMARY ? 1 : 0;
					double employment_STUDENT_SECONDARY	= person.employment() == Employment.STUDENT_SECONDARY ? 1 : 0;
					double employment_STUDENT_TERTIARY 	= person.employment() == Employment.STUDENT_TERTIARY ? 1 : 0;
			
			
					double purpose_WORK 		 		 = nextActivity.activityType().isWorkActivity() ? 1 : 0; // reference case
					double purpose_BUSINESS 		 = nextActivity.activityType().isBusinessActivity() ? 1 : 0;
					double purpose_EDUCATION 	 = nextActivity.activityType() == ActivityType.EDUCATION_PRIMARY
																	|| nextActivity.activityType() == ActivityType.EDUCATION_SECONDARY
																	|| nextActivity.activityType() == ActivityType.EDUCATION_TERTIARY
																	|| nextActivity.activityType() == ActivityType.EDUCATION_OCCUP
																	|| nextActivity.activityType() == ActivityType.EDUCATION ? 1 : 0;
			
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
			
					double age_0to9 						=                           person.age() <=  9 ? 1 : 0;
					double age_10to17 					= person.age() >= 10  && person.age() <= 17 ? 1 : 0;
					double age_18to25 					= person.age() >= 18  && person.age() <= 25 ? 1 : 0;
					double age_26to35 					= person.age() >= 26  && person.age() <= 35 ? 1 : 0;
					double age_36to50 					= person.age() >= 36  && person.age() <= 50 ? 1 : 0;
					double age_51to60 					= person.age() >= 51  && person.age() <= 60 ? 1 : 0;
					double age_61to70 					= person.age() >= 61  && person.age() <= 70 ? 1 : 0;
					double age_71plus 					= person.age() >= 71                           ? 1 : 0;
					
					Mode previousMode = tour.hasPreviousTour() ? tour.previousTour().mode() : StandardMode.UNDEFINED;
					double initialTour = tour.hasPreviousTour() ? 0.0 : 1.0;
					
					boolean withinTour = tour.lastActivity() != nextActivity;
					
				
					Map<String,Double> employment = new LinkedHashMap<String,Double>();	
					Map<String,Double> attributes = new LinkedHashMap<String,Double>();	
					
			
					attributes.put("CONST", 	1.0);
					attributes.put("INTRAZONAL", 	intrazonal);
					
					/*
					attributes.put("COMMUTING_TICKET", commuting_ticket);
					attributes.put("NO_DRIVING_LICENCE", no_driving_licence);
					attributes.put("HAS_BICYCLE", has_bicycle);
					attributes.put("FEMALE", female);
					attributes.put("DAY_SA",  day_SA);
					attributes.put("DAY_SU",  day_SU);
					attributes.put("INITIAL_TOUR",  initialTour);
					*/
			
					/*
					employment.put("EMPLOYMENT_EDUCATION",  				employment_EDUCATION);
					employment.put("EMPLOYMENT_FULLTIME",  					employment_FULLTIME);
					employment.put("EMPLOYMENT_INFANT",  						employment_INFANT);
					employment.put("EMPLOYMENT_JOBLESS",  					employment_JOBLESS);
					employment.put("EMPLOYMENT_NONE",  							employment_NONE);
					employment.put("EMPLOYMENT_NOT_WORKING",  			employment_NOT_WORKING);
					employment.put("EMPLOYMENT_PARTTIME",  					employment_PARTTIME);
					employment.put("EMPLOYMENT_RETIRED",  					employment_RETIRED);
					employment.put("EMPLOYMENT_STUDENT_PRIMARY",  	employment_STUDENT_PRIMARY);
					employment.put("EMPLOYMENT_STUDENT_SECONDARY", 	employment_STUDENT_SECONDARY);
					employment.put("EMPLOYMENT_STUDENT_TERTIARY", 	employment_STUDENT_TERTIARY);
					*/
				
					/*
					for(String emp : employment.keySet()) {
						Double emp_value = employment.get(emp);
						
						attributes.put(emp, emp_value);
						
						for (String pre : this.compound) {
							
							attributes.put(pre + ":" + emp, emp_value);
						}
					}
					*/
			
					attributes.put("FEMALE", female);
					
					attributes.put("TIME:FEMALE", female);
					attributes.put("COST:FEMALE", female);
					
					Map<String,Double> agegroups = new LinkedHashMap<String,Double>();
			
					
					agegroups.put("AGE_0TO9",  	age_0to9);
					agegroups.put("AGE_10TO17",  age_10to17);
					agegroups.put("AGE_18TO25",  age_18to25);
					agegroups.put("AGE_26TO35",  age_26to35);
					agegroups.put("AGE_36TO50",  age_36to50);
					agegroups.put("AGE_51TO60",  age_51to60);
					agegroups.put("AGE_61TO70",  age_61to70);
					agegroups.put("AGE_71PLUS",  age_71plus);
					
				
					for(String age : agegroups.keySet()) {
						Double age_value = agegroups.get(age);
						
						attributes.put(age, age_value);
						
						for (String pre : this.compound) {
							
							attributes.put(pre + ":" + age, age_value);
						}
					}
				
					
			
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
			
						double time 	= impedance.getTravelTime(sourceId, destinationId, mode, date);
			
						double cost 	= (mode==StandardMode.PUBLICTRANSPORT && person.hasCommuterTicket()) ? 0.0
															: impedance.getTravelCost(sourceId, destinationId, mode, date);
						
			
			
						attrib.put("DISTANCE_KM", distance);
					 	attrib.put("TIME", time);
					 	// attrib.put("SQRT_TIME", Math.sqrt(time));
						attrib.put("COST", cost);
						// attrib.put("SQRT_COST", Math.sqrt(cost));
						
						attrib.put("ISTOURMODE",  					mode == tourMode ? 1.0 : 0.0);
						attrib.put("ISTOURMODE_WITHINTOUR",  	mode == tourMode && withinTour ? 1.0 : 0.0);
						
					
						/*
						attrib.put("SAME_MODE_AS_BEFORE", mode == previousMode ? 1.0 : 0.0);
						
						attrib.put("PREVIOUS_MODE_WALK", previousMode == Mode.PEDESTRIAN ? 1.0 : 0.0);
						attrib.put("PREVIOUS_MODE_BIKE", previousMode == Mode.BIKE ? 1.0 : 0.0);
						attrib.put("PREVIOUS_MODE_CAR", previousMode == Mode.CAR ? 1.0 : 0.0);
						attrib.put("PREVIOUS_MODE_PASSENGER", previousMode == Mode.PASSENGER ? 1.0 : 0.0);
						attrib.put("PREVIOUS_MODE_PT", previousMode == Mode.PUBLICTRANSPORT ? 1.0 : 0.0);
						*/
			
						modeAttributes.put(mode, attrib);
					}
			
					return  modeAttributes;
			}

}