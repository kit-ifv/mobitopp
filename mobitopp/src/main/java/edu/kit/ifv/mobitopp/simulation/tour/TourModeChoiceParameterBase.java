package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.Mode;

public abstract class TourModeChoiceParameterBase extends ModeChoiceAttributes 
	implements TourModeChoiceParameter {

	protected final Map<String,Double> parameterGeneric = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterWalk = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterBike = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterCar = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterPassenger = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterPt = new LinkedHashMap<String,Double>();
	
	protected Double walking 															= 0.0;
	
	protected Double walking_employment_fulltime 					= 0.0;
	protected Double walking_employment_apprentice				= 0.0;
	protected Double walking_employment_homekeeper				= 0.0;
	protected Double walking_employment_marginal					= 0.0;
	protected Double walking_employment_other							= 0.0;
	protected Double walking_employment_parttime					= 0.0;
	protected Double walking_employment_retired						= 0.0;
	protected Double walking_employment_pupil							= 0.0;
	protected Double walking_employment_student						= 0.0;
	protected Double walking_employment_unemployed				= 0.0;
	
	protected Double cycling_employment_fulltime 					= 0.0;
	protected Double cardriver_employment_fulltime 				= 0.0;
	protected Double carpassenger_employment_fulltime 		= 0.0;
	protected Double publictransport_employment_fulltime 	= 0.0;
	
	protected Double walking_hhtype_couple      					= 0.0;
	protected Double cycling_hhtype_couple      					= 0.0;
	protected Double cardriver_hhtype_couple      				= 0.0;
	protected Double carpassenger_hhtype_couple      	  	= 0.0;
	protected Double publictransport_hhtype_couple      	= 0.0;
	
	
	protected Double cardriver;
	protected Double carpassenger;
	protected Double cycling;
	protected Double publictransport;
	
	protected Double time;
	protected Double cost;
	
	protected Double notavailable;
	
	protected Double time_sex_female;
	
	protected Double time_employment_fulltime = 0.0; // Referenzklasse
	protected Double time_employment_parttime;
	protected Double time_employment_marginal;
	protected Double time_employment_unemployed;
	protected Double time_employment_apprentice;
	protected Double time_employment_homekeeper;
	protected Double time_employment_retired;
	protected Double time_employment_pupil;
	protected Double time_employment_student;
	protected Double time_employment_other;
	
	protected Double time_hhtype_couple	= 0.0; // Referenzklasse
	protected Double time_hhtype_single;
	protected Double time_hhtype_kids_0to7;
	protected Double time_hhtype_kids_8to12;
	protected Double time_hhtype_kids_13to18;
	protected Double time_hhtype_multiadult;

	protected Double time_age_06to09;
	protected Double time_age_10to17;
	protected Double time_age_18to25;
	protected Double time_age_26to35;
	protected Double time_age_36to50 = 0.0; // Referenzklasse
	protected Double time_age_51to60;
	protected Double time_age_61to70;
	protected Double time_age_71plus;
	
	
	protected Double cost_sex_female;
	
	protected Double cost_employment_fulltime = 0.0; // Referenzklasse
	protected Double cost_employment_parttime;
	protected Double cost_employment_marginal;
	protected Double cost_employment_unemployed;
	protected Double cost_employment_apprentice;
	protected Double cost_employment_homekeeper;
	protected Double cost_employment_retired;
	protected Double cost_employment_pupil;
	protected Double cost_employment_student;
	protected Double cost_employment_other;
	
	protected Double cost_hhtype_couple		= 0.0; // Referenzklassse
	protected Double cost_hhtype_single;
	protected Double cost_hhtype_kids_0to7;
	protected Double cost_hhtype_kids_8to12;
	protected Double cost_hhtype_kids_13to18;
	protected Double cost_hhtype_multiadult;
	
	protected Double cost_age_06to09 ;
	protected Double cost_age_10to17 ;
	protected Double cost_age_18to25 ;
	protected Double cost_age_26to35 ;
	protected Double cost_age_36to50 = 0.0; //Referenzklasse
	protected Double cost_age_51to60 ;
	protected Double cost_age_61to70 ;
	protected Double cost_age_71plus ;
	// protected Double cost_age_unknown;
	
	
	protected Double cardriver_intrazonal;
	protected Double carpassenger_intrazonal;
	protected Double cycling_intrazonal;
	protected Double publictransport_intrazonal;
		
	public Double cardriver_num_activities; 				
	public Double carpassenger_num_activities;
	public Double cycling_num_activities;
	public Double publictransport_num_activities;
	
	protected Double cardriver_sex_female;
	protected Double carpassenger_sex_female;
	protected Double cycling_sex_female;
	protected Double publictransport_sex_female;
	
	protected Double cardriver_employment_apprentice;
	protected Double cardriver_employment_homekeeper;
	protected Double cardriver_employment_marginal;
	protected Double cardriver_employment_other;
	protected Double cardriver_employment_parttime;
	protected Double cardriver_employment_retired;
	protected Double cardriver_employment_pupil;
	protected Double cardriver_employment_student;
	protected Double cardriver_employment_unemployed;
	protected Double carpassenger_employment_apprentice;
	protected Double carpassenger_employment_homekeeper;
	protected Double carpassenger_employment_marginal;
	protected Double carpassenger_employment_other;
	protected Double carpassenger_employment_parttime;
	protected Double carpassenger_employment_retired;
	protected Double carpassenger_employment_pupil;
	protected Double carpassenger_employment_student;
	protected Double carpassenger_employment_unemployed;
	protected Double cycling_employment_apprentice;
	protected Double cycling_employment_homekeeper;
	protected Double cycling_employment_marginal;
	protected Double cycling_employment_other;
	protected Double cycling_employment_parttime;
	protected Double cycling_employment_retired;
	protected Double cycling_employment_pupil;
	protected Double cycling_employment_student;
	protected Double cycling_employment_unemployed;
	protected Double publictransport_employment_apprentice;
	protected Double publictransport_employment_homekeeper;
	protected Double publictransport_employment_marginal;
	protected Double publictransport_employment_other;
	protected Double publictransport_employment_parttime;
	protected Double publictransport_employment_retired;
	protected Double publictransport_employment_pupil;
	protected Double publictransport_employment_student;
	protected Double publictransport_employment_unemployed;
	
	protected Double cardriver_purpose_business;
	protected Double cardriver_purpose_education;
	protected Double cardriver_purpose_leisure_indoors;
	protected Double cardriver_purpose_leisure_other;
	protected Double cardriver_purpose_leisure_outdoors;
	protected Double cardriver_purpose_leisure_walk;
	protected Double cardriver_purpose_other;
	protected Double cardriver_purpose_private_business;
	protected Double cardriver_purpose_service;
	protected Double cardriver_purpose_shopping_grocery;
	protected Double cardriver_purpose_shopping_other;
	protected Double cardriver_purpose_visit;
	protected Double carpassenger_purpose_business;
	protected Double carpassenger_purpose_education;
	protected Double carpassenger_purpose_leisure_indoors;
	protected Double carpassenger_purpose_leisure_other;
	protected Double carpassenger_purpose_leisure_outdoors;
	protected Double carpassenger_purpose_leisure_walk;
	protected Double carpassenger_purpose_other;
	protected Double carpassenger_purpose_private_business;
	protected Double carpassenger_purpose_service;
	protected Double carpassenger_purpose_shopping_grocery;
	protected Double carpassenger_purpose_shopping_other;
	protected Double carpassenger_purpose_visit;
	protected Double cycling_purpose_business;
	protected Double cycling_purpose_education;
	protected Double cycling_purpose_leisure_indoors;
	protected Double cycling_purpose_leisure_other;
	protected Double cycling_purpose_leisure_outdoors;
	protected Double cycling_purpose_leisure_walk;
	protected Double cycling_purpose_other;
	protected Double cycling_purpose_private_business;
	protected Double cycling_purpose_service;
	protected Double cycling_purpose_shopping_grocery;
	protected Double cycling_purpose_shopping_other;
	protected Double cycling_purpose_visit;
	protected Double publictransport_purpose_business;
	protected Double publictransport_purpose_education;
	protected Double publictransport_purpose_leisure_indoors;
	protected Double publictransport_purpose_leisure_other;
	protected Double publictransport_purpose_leisure_outdoors;
	protected Double publictransport_purpose_leisure_walk;
	protected Double publictransport_purpose_other;
	protected Double publictransport_purpose_private_business;
	protected Double publictransport_purpose_service;
	protected Double publictransport_purpose_shopping_grocery;
	protected Double publictransport_purpose_shopping_other;
	protected Double publictransport_purpose_visit;
	
	protected Double walking_purpose_business = 0.0;
	protected Double walking_purpose_education = 0.0;
	protected Double walking_purpose_leisure_indoors = 0.0;
	protected Double walking_purpose_leisure_other = 0.0;
	protected Double walking_purpose_leisure_outdoors = 0.0;
	protected Double walking_purpose_leisure_walk = 0.0;
	protected Double walking_purpose_other = 0.0;
	protected Double walking_purpose_private_business = 0.0;
	protected Double walking_purpose_service = 0.0;
	protected Double walking_purpose_shopping_grocery = 0.0;
	protected Double walking_purpose_shopping_other = 0.0;
	protected Double walking_purpose_visit = 0.0;
	
	protected Double cardriver_hhtype_kids_0to7;
	protected Double cardriver_hhtype_kids_13to18;
	protected Double cardriver_hhtype_kids_8to12;
	protected Double cardriver_hhtype_multiadult;
	protected Double cardriver_hhtype_single;
	protected Double carpassenger_hhtype_kids_0to7;
	protected Double carpassenger_hhtype_kids_13to18;
	protected Double carpassenger_hhtype_kids_8to12;
	protected Double carpassenger_hhtype_multiadult;
	protected Double carpassenger_hhtype_single;
	protected Double cycling_hhtype_kids_0to7;
	protected Double cycling_hhtype_kids_13to18;
	protected Double cycling_hhtype_kids_8to12;
	protected Double cycling_hhtype_multiadult;
	protected Double cycling_hhtype_single;
	protected Double publictransport_hhtype_kids_0to7;
	protected Double publictransport_hhtype_kids_13to18;
	protected Double publictransport_hhtype_kids_8to12;
	protected Double publictransport_hhtype_multiadult;
	protected Double publictransport_hhtype_single;
	protected Double walking_hhtype_kids_0to7 = 0.0;
	protected Double walking_hhtype_kids_13to18 = 0.0;
	protected Double walking_hhtype_kids_8to12 = 0.0;
	protected Double walking_hhtype_multiadult = 0.0;
	protected Double walking_hhtype_single = 0.0;
	
	protected Double cardriver_age_06to09 ;
	protected Double cardriver_age_10to17 ;
	protected Double cardriver_age_18to25 ;
	protected Double cardriver_age_26to35 ;
	protected Double cardriver_age_36to50 ;
	protected Double cardriver_age_51to60 ;
	protected Double cardriver_age_61to70 ;
	protected Double cardriver_age_71plus ;
	protected Double carpassenger_age_06to09 ;
	protected Double carpassenger_age_10to17 ;
	protected Double carpassenger_age_18to25 ;
	protected Double carpassenger_age_26to35 ;
	protected Double carpassenger_age_36to50 ;
	protected Double carpassenger_age_51to60 ;
	protected Double carpassenger_age_61to70 ;
	protected Double carpassenger_age_71plus ;
	protected Double publictransport_age_06to09 ;
	protected Double publictransport_age_10to17 ;
	protected Double publictransport_age_18to25 ;
	protected Double publictransport_age_26to35 ;
	protected Double publictransport_age_36to50 ;
	protected Double publictransport_age_51to60 ;
	protected Double publictransport_age_61to70 ;
	protected Double publictransport_age_71plus ;
	protected Double cycling_age_06to09 ;
	protected Double cycling_age_10to17 ;
	protected Double cycling_age_18to25 ;
	protected Double cycling_age_26to35 ;
	protected Double cycling_age_36to50 ;
	protected Double cycling_age_51to60 ;
	protected Double cycling_age_61to70 ;
	protected Double cycling_age_71plus ;
	
	protected Double walking_sex_female  = 0.0;
	
	protected Double walking_age_06to09  = 0.0;
	protected Double walking_age_10to17  = 0.0;
	protected Double walking_age_18to25  = 0.0;
	protected Double walking_age_26to35  = 0.0;
	protected Double walking_age_36to50  = 0.0;
	protected Double walking_age_51to60  = 0.0;
	protected Double walking_age_61to70  = 0.0;
	protected Double walking_age_71plus  = 0.0;
	
	protected Double cardriver_day_Saturday;
	protected Double carpassenger_day_Saturday;
	protected Double cycling_day_Saturday;
	protected Double publictransport_day_Saturday;
	protected Double cardriver_day_Sunday;
	protected Double carpassenger_day_Sunday;
	protected Double cycling_day_Sunday;
	protected Double publictransport_day_Sunday;

	protected Double cardriver_containsBusiness;
	protected Double cardriver_containsLeisure;
	protected Double cardriver_containsPrivateB;
	protected Double cardriver_containsService;
	protected Double cardriver_containsShopping;
	protected Double cardriver_containsStrolling;
	protected Double cardriver_containsVisit;
	protected Double carpassenger_containsBusiness;
	protected Double carpassenger_containsLeisure;
	protected Double carpassenger_containsPrivateB;
	protected Double carpassenger_containsService;
	protected Double carpassenger_containsShopping;
	protected Double carpassenger_containsStrolling;
	protected Double carpassenger_containsVisit;
	protected Double cycling_containsBusiness;
	protected Double cycling_containsLeisure;
	protected Double cycling_containsPrivateB;
	protected Double cycling_containsService;
	protected Double cycling_containsShopping;
	protected Double cycling_containsStrolling;
	protected Double cycling_containsVisit;
	protected Double publictransport_containsBusiness;
	protected Double publictransport_containsLeisure;
	protected Double publictransport_containsPrivateB;
	protected Double publictransport_containsService;
	protected Double publictransport_containsShopping;
	protected Double publictransport_containsStrolling;
	protected Double publictransport_containsVisit;
	
	protected Double walking_day_Saturday 								= 0.0;
	protected Double walking_day_Sunday 									= 0.0;
	
	protected Double walking_containsStrolling      			= 0.0;	
	protected Double walking_containsService      				= 0.0;	
	protected Double walking_containsBusiness      				= 0.0;	
	protected Double walking_containsLeisure      				= 0.0;	
	protected Double walking_containsPrivateB      				= 0.0;	
	protected Double walking_containsShopping      				= 0.0;	
	protected Double walking_containsVisit        				= 0.0;	
	protected Double walking_intrazonal 									= 0.0;
	protected Double walking_num_activities 							= 0.0;

	public TourModeChoiceParameterBase() {
	}

	protected void init() {

		//TODO: Enums statt Strings!
		this.parameterGeneric.put("TIME", 					time);
		this.parameterGeneric.put("COST", 					cost);
		
		this.parameterGeneric.put("TIME:FEMALE", 					time_sex_female);
		this.parameterGeneric.put("COST:FEMALE", 					cost_sex_female);
		
		this.parameterGeneric.put("TIME:EMPLOYMENT_EDUCATION", 					time_employment_apprentice);
assert(time_employment_fulltime != null);
		this.parameterGeneric.put("TIME:EMPLOYMENT_FULLTIME", 					time_employment_fulltime);
		this.parameterGeneric.put("TIME:EMPLOYMENT_INFANT", 						time_employment_pupil);
		this.parameterGeneric.put("TIME:EMPLOYMENT_UNEMPLOYED", 						time_employment_unemployed);
		this.parameterGeneric.put("TIME:EMPLOYMENT_NONE", 							time_employment_other);
		this.parameterGeneric.put("TIME:EMPLOYMENT_HOMEKEEPER", 				time_employment_homekeeper);
		this.parameterGeneric.put("TIME:EMPLOYMENT_PARTTIME", 					time_employment_parttime);
		this.parameterGeneric.put("TIME:EMPLOYMENT_RETIRED", 						time_employment_retired);
		this.parameterGeneric.put("TIME:EMPLOYMENT_STUDENT_PRIMARY", 		time_employment_pupil);
		this.parameterGeneric.put("TIME:EMPLOYMENT_STUDENT_SECONDARY", 	time_employment_pupil);
		this.parameterGeneric.put("TIME:EMPLOYMENT_STUDENT_TERTIARY", 	time_employment_student);
		
		this.parameterGeneric.put("TIME:AGE_0TO9",  time_age_06to09);
assert(time_age_06to09 != null);
		this.parameterGeneric.put("TIME:AGE_10TO17",  time_age_10to17);
		this.parameterGeneric.put("TIME:AGE_18TO25",  time_age_18to25);
		this.parameterGeneric.put("TIME:AGE_26TO35",  time_age_26to35);
		this.parameterGeneric.put("TIME:AGE_36TO50",  time_age_36to50);
assert(time_age_36to50 != null);
		this.parameterGeneric.put("TIME:AGE_51TO60",  time_age_51to60);
		this.parameterGeneric.put("TIME:AGE_61TO70",  time_age_61to70);
		this.parameterGeneric.put("TIME:AGE_71PLUS",  time_age_71plus);
		// this.parameterGeneric.put("TIME:AGE_UNKNOWN", time_age_unknown);
		
		this.parameterGeneric.put("TIME:HHTYPE_SINGLE",  time_hhtype_single);
		this.parameterGeneric.put("TIME:HHTYPE_COUPLE",  time_hhtype_couple);
assert(time_hhtype_couple != null);
		this.parameterGeneric.put("TIME:HHTYPE_KIDS0TO7",  time_hhtype_kids_0to7);
		this.parameterGeneric.put("TIME:HHTYPE_KIDS8TO12",  time_hhtype_kids_8to12);
		this.parameterGeneric.put("TIME:HHTYPE_KIDS13PLUS",  time_hhtype_kids_13to18);
		this.parameterGeneric.put("TIME:HHTYPE_MULTIADULT",  time_hhtype_multiadult);
		
		
		
		this.parameterGeneric.put("COST:EMPLOYMENT_EDUCATION", 					cost_employment_apprentice);
		this.parameterGeneric.put("COST:EMPLOYMENT_FULLTIME", 					cost_employment_fulltime);
		this.parameterGeneric.put("COST:EMPLOYMENT_INFANT", 						cost_employment_pupil);
		this.parameterGeneric.put("COST:EMPLOYMENT_UNEMPLOYED", 				cost_employment_unemployed);
		this.parameterGeneric.put("COST:EMPLOYMENT_NONE", 							cost_employment_other);
		this.parameterGeneric.put("COST:EMPLOYMENT_HOMEKEEPER", 				cost_employment_homekeeper);
		this.parameterGeneric.put("COST:EMPLOYMENT_PARTTIME", 					cost_employment_parttime);
		this.parameterGeneric.put("COST:EMPLOYMENT_RETIRED", 						cost_employment_retired);
		this.parameterGeneric.put("COST:EMPLOYMENT_STUDENT_PRIMARY", 		cost_employment_pupil);
		this.parameterGeneric.put("COST:EMPLOYMENT_STUDENT_SECONDARY", 	cost_employment_pupil);
		this.parameterGeneric.put("COST:EMPLOYMENT_STUDENT_TERTIARY", 	cost_employment_student);
		
		this.parameterGeneric.put("COST:AGE_0TO9",  cost_age_06to09);
		this.parameterGeneric.put("COST:AGE_10TO17",  cost_age_10to17);
		this.parameterGeneric.put("COST:AGE_18TO25",  cost_age_18to25);
		this.parameterGeneric.put("COST:AGE_26TO35",  cost_age_26to35);
		this.parameterGeneric.put("COST:AGE_36TO50",  cost_age_36to50);
		this.parameterGeneric.put("COST:AGE_51TO60",  cost_age_51to60);
		this.parameterGeneric.put("COST:AGE_61TO70",  cost_age_61to70);
		this.parameterGeneric.put("COST:AGE_71PLUS",  cost_age_71plus);
		// this.parameterGeneric.put("COST:AGE_UNKNOWN", cost_age_unknown);
		
		this.parameterGeneric.put("COST:HHTYPE_SINGLE",  		cost_hhtype_single);
		this.parameterGeneric.put("COST:HHTYPE_COUPLE",  		cost_hhtype_couple);
		this.parameterGeneric.put("COST:HHTYPE_KIDS0TO7",  	cost_hhtype_kids_0to7);
		this.parameterGeneric.put("COST:HHTYPE_KIDS8TO12",  cost_hhtype_kids_8to12);
		this.parameterGeneric.put("COST:HHTYPE_KIDS13PLUS", cost_hhtype_kids_13to18);
		this.parameterGeneric.put("COST:HHTYPE_MULTIADULT", cost_hhtype_multiadult);
	
		/*
		this.parameterGeneric.put("SQRT_COST:EMPLOYMENT_EDUCATION", 				sqrt_cost_employment_apprentice);
		this.parameterGeneric.put("SQRT_COST:EMPLOYMENT_FULLTIME", 					sqrt_cost_employment_fulltime);
		this.parameterGeneric.put("SQRT_COST:EMPLOYMENT_INFANT", 0.0);
		this.parameterGeneric.put("SQRT_COST:EMPLOYMENT_UNEMPLOYED", 					sqrt_cost_employment_unemployed);
		this.parameterGeneric.put("SQRT_COST:EMPLOYMENT_NONE", 							sqrt_cost_employment_other);
		this.parameterGeneric.put("SQRT_COST:EMPLOYMENT_HOMEKEEPER", 			sqrt_cost_employment_homekeeper);
		this.parameterGeneric.put("SQRT_COST:EMPLOYMENT_PARTTIME", 					sqrt_cost_employment_parttime);
		this.parameterGeneric.put("SQRT_COST:EMPLOYMENT_RETIRED", 					sqrt_cost_employment_retired);
		this.parameterGeneric.put("SQRT_COST:EMPLOYMENT_STUDENT_PRIMARY", 	sqrt_cost_employment_student_primary);
		this.parameterGeneric.put("SQRT_COST:EMPLOYMENT_STUDENT_SECONDARY", sqrt_cost_employment_student_secondary);
		this.parameterGeneric.put("SQRT_COST:EMPLOYMENT_STUDENT_TERTIARY", 	sqrt_cost_employment_student_tertiary);
		*/
		
		this.parameterWalk.putAll(this.parameterGeneric);
		this.parameterBike.putAll(this.parameterGeneric);
		this.parameterCar.putAll(this.parameterGeneric);
		this.parameterPassenger.putAll(this.parameterGeneric);
		this.parameterPt.putAll(this.parameterGeneric);
		
		// walk
		this.parameterWalk.put("CONST", 	walking);
		this.parameterWalk.put("DAY_SA",  walking_day_Saturday);
		this.parameterWalk.put("DAY_SU",  walking_day_Sunday);
		this.parameterWalk.put("TOUR_CONTAINS_SERVICE",  walking_containsService);
		this.parameterWalk.put("TOUR_CONTAINS_STROLLING",  walking_containsStrolling);
		this.parameterWalk.put("TOUR_CONTAINS_BUSINESS",  walking_containsBusiness);
		this.parameterWalk.put("TOUR_CONTAINS_LEISURE",  walking_containsLeisure);
		this.parameterWalk.put("TOUR_CONTAINS_PRIVATE_BUSINESS",  walking_containsPrivateB);
		this.parameterWalk.put("TOUR_CONTAINS_SHOPPING",  walking_containsShopping);
		this.parameterWalk.put("TOUR_CONTAINS_VISIT",  walking_containsVisit);
		this.parameterWalk.put("INTRAZONAL", 	walking_intrazonal);
		this.parameterWalk.put("NUM_ACTIVITIES", 	walking_num_activities);
		
		this.parameterWalk.put("FEMALE", walking_sex_female);
		
		this.parameterWalk.put("EMPLOYMENT_EDUCATION",  				walking_employment_apprentice);
		this.parameterWalk.put("EMPLOYMENT_FULLTIME",  					walking_employment_fulltime);
		this.parameterWalk.put("EMPLOYMENT_INFANT",  						walking_employment_pupil);
		this.parameterWalk.put("EMPLOYMENT_UNEMPLOYED",  				walking_employment_unemployed);
		this.parameterWalk.put("EMPLOYMENT_NONE",  							walking_employment_other);
		this.parameterWalk.put("EMPLOYMENT_HOMEKEEPER",  				walking_employment_homekeeper);
		this.parameterWalk.put("EMPLOYMENT_PARTTIME",  					walking_employment_parttime);
		this.parameterWalk.put("EMPLOYMENT_RETIRED",  					walking_employment_retired);
		this.parameterWalk.put("EMPLOYMENT_STUDENT_PRIMARY",  	walking_employment_pupil);
		this.parameterWalk.put("EMPLOYMENT_STUDENT_SECONDARY",	walking_employment_pupil);
		this.parameterWalk.put("EMPLOYMENT_STUDENT_TERTIARY",  	walking_employment_student);
	

		this.parameterWalk.put("AGE_0TO9",  	walking_age_06to09);
		this.parameterWalk.put("AGE_10TO17",  walking_age_10to17);
		this.parameterWalk.put("AGE_18TO25",  walking_age_18to25);
		this.parameterWalk.put("AGE_26TO35",  walking_age_26to35);
		this.parameterWalk.put("AGE_51TO60",  walking_age_51to60);
		this.parameterWalk.put("AGE_61TO70",  walking_age_61to70);
		this.parameterWalk.put("AGE_71PLUS",  walking_age_71plus);

		
		this.parameterWalk.put("HHTYPE_SINGLE",  			walking_hhtype_single);
		this.parameterWalk.put("HHTYPE_COUPLE",  			walking_hhtype_couple);
		this.parameterWalk.put("HHTYPE_KIDS0TO7",  		walking_hhtype_kids_0to7);
		this.parameterWalk.put("HHTYPE_KIDS8TO12",  	walking_hhtype_kids_8to12);
		this.parameterWalk.put("HHTYPE_KIDS13PLUS",  	walking_hhtype_kids_13to18);
		this.parameterWalk.put("HHTYPE_MULTIADULT",  	walking_hhtype_multiadult);
	
		this.parameterWalk.put("PURPOSE_BUSINESS",  				walking_purpose_business);
		this.parameterWalk.put("PURPOSE_EDUCATION",  				walking_purpose_education);
		this.parameterWalk.put("PURPOSE_HOME",  						0.0);
		this.parameterWalk.put("PURPOSE_LEISURE_INDOOR",  	walking_purpose_leisure_indoors);
		this.parameterWalk.put("PURPOSE_LEISURE_OUTDOOR",  	walking_purpose_leisure_outdoors);
		this.parameterWalk.put("PURPOSE_LEISURE_OTHER",  		walking_purpose_leisure_other);
		this.parameterWalk.put("PURPOSE_LEISURE_WALK",  		walking_purpose_leisure_walk);
		this.parameterWalk.put("PURPOSE_OTHER",  						walking_purpose_other);
		this.parameterWalk.put("PURPOSE_PRIVATE_BUSINESS",  walking_purpose_private_business);
		this.parameterWalk.put("PURPOSE_PRIVATE_VISIT",  		walking_purpose_visit);
		this.parameterWalk.put("PURPOSE_SERVICE",  					walking_purpose_service);
		this.parameterWalk.put("PURPOSE_SHOPPING_DAILY",  	walking_purpose_shopping_grocery);
		this.parameterWalk.put("PURPOSE_SHOPPING_OTHER",  	walking_purpose_shopping_other);
		this.parameterWalk.put("PURPOSE_BUSINESS",  				walking_purpose_business);
		
		
	
		// bike
		this.parameterBike.put("CONST", 	cycling);
		this.parameterBike.put("INTRAZONAL", 	cycling_intrazonal);
		this.parameterBike.put("NUM_ACTIVITIES", 	cycling_num_activities);
		/*
		this.parameterBike.put("COMMUTING_TICKET", cycling_transitpass_TRUE);
		this.parameterBike.put("NO_DRIVING_LICENCE", cycling_licence_FALSE);
		this.parameterBike.put("HAS_BICYCLE", cycling_bicycle_TRUE);
		*/
		this.parameterBike.put("TOUR_CONTAINS_SERVICE",  cycling_containsService);
		this.parameterBike.put("TOUR_CONTAINS_STROLLING",  cycling_containsStrolling);
		this.parameterBike.put("TOUR_CONTAINS_BUSINESS",  cycling_containsBusiness);
		this.parameterBike.put("TOUR_CONTAINS_LEISURE",  cycling_containsLeisure);
		this.parameterBike.put("TOUR_CONTAINS_PRIVATE_BUSINESS",  cycling_containsPrivateB);
		this.parameterBike.put("TOUR_CONTAINS_SHOPPING",  cycling_containsShopping);
		this.parameterBike.put("TOUR_CONTAINS_VISIT",  cycling_containsVisit);
		this.parameterBike.put("FEMALE", cycling_sex_female);
		this.parameterBike.put("DAY_SA",  cycling_day_Saturday);
		this.parameterBike.put("DAY_SU",  cycling_day_Sunday);
	
	
		this.parameterBike.put("EMPLOYMENT_EDUCATION",  				cycling_employment_apprentice);
		this.parameterBike.put("EMPLOYMENT_FULLTIME",  					cycling_employment_fulltime);
		this.parameterBike.put("EMPLOYMENT_INFANT",  						cycling_employment_pupil);
		this.parameterBike.put("EMPLOYMENT_UNEMPLOYED",  				cycling_employment_unemployed);
		this.parameterBike.put("EMPLOYMENT_NONE",  							cycling_employment_other);
		this.parameterBike.put("EMPLOYMENT_HOMEKEEPER",  				cycling_employment_homekeeper);
		this.parameterBike.put("EMPLOYMENT_PARTTIME",  					cycling_employment_parttime);
		this.parameterBike.put("EMPLOYMENT_RETIRED",  					cycling_employment_retired);
		this.parameterBike.put("EMPLOYMENT_STUDENT_PRIMARY",  	cycling_employment_pupil);
		this.parameterBike.put("EMPLOYMENT_STUDENT_SECONDARY",	cycling_employment_pupil);
		this.parameterBike.put("EMPLOYMENT_STUDENT_TERTIARY",  	cycling_employment_student);
	

		this.parameterBike.put("AGE_0TO9",  	cycling_age_06to09);
		this.parameterBike.put("AGE_10TO17",  cycling_age_10to17);
		this.parameterBike.put("AGE_18TO25",  cycling_age_18to25);
		this.parameterBike.put("AGE_26TO35",  cycling_age_26to35);
		this.parameterBike.put("AGE_51TO60",  cycling_age_51to60);
		this.parameterBike.put("AGE_61TO70",  cycling_age_61to70);
		this.parameterBike.put("AGE_71PLUS",  cycling_age_71plus);

		
		this.parameterBike.put("HHTYPE_SINGLE",  			cycling_hhtype_single);
		this.parameterBike.put("HHTYPE_COUPLE",  			cycling_hhtype_couple);
		this.parameterBike.put("HHTYPE_KIDS0TO7",  		cycling_hhtype_kids_0to7);
		this.parameterBike.put("HHTYPE_KIDS8TO12",  	cycling_hhtype_kids_8to12);
		this.parameterBike.put("HHTYPE_KIDS13PLUS",  	cycling_hhtype_kids_13to18);
		this.parameterBike.put("HHTYPE_MULTIADULT",  	cycling_hhtype_multiadult);
	
		this.parameterBike.put("PURPOSE_BUSINESS",  				cycling_purpose_business);
		this.parameterBike.put("PURPOSE_EDUCATION",  				cycling_purpose_education);
		this.parameterBike.put("PURPOSE_HOME",  						0.0);
		this.parameterBike.put("PURPOSE_LEISURE_INDOOR",  	cycling_purpose_leisure_indoors);
		this.parameterBike.put("PURPOSE_LEISURE_OUTDOOR",  	cycling_purpose_leisure_outdoors);
		this.parameterBike.put("PURPOSE_LEISURE_OTHER",  		cycling_purpose_leisure_other);
		this.parameterBike.put("PURPOSE_LEISURE_WALK",  		cycling_purpose_leisure_walk);
		this.parameterBike.put("PURPOSE_OTHER",  						cycling_purpose_other);
		this.parameterBike.put("PURPOSE_PRIVATE_BUSINESS",  cycling_purpose_private_business);
		this.parameterBike.put("PURPOSE_PRIVATE_VISIT",  		cycling_purpose_visit);
		this.parameterBike.put("PURPOSE_SERVICE",  					cycling_purpose_service);
		this.parameterBike.put("PURPOSE_SHOPPING_DAILY",  	cycling_purpose_shopping_grocery);
		this.parameterBike.put("PURPOSE_SHOPPING_OTHER",  	cycling_purpose_shopping_other);
		this.parameterBike.put("PURPOSE_BUSINESS",  				cycling_purpose_business);
	
		// car
		this.parameterCar.put("CONST", 	cardriver);
		this.parameterCar.put("INTRAZONAL", 	cardriver_intrazonal);
		this.parameterCar.put("NUM_ACTIVITIES", 	cardriver_num_activities);
		/*
		this.parameterCar.put("COMMUTING_TICKET", cardriver_transitpass_TRUE);
		this.parameterCar.put("NO_DRIVING_LICENCE", cardriver_licence_FALSE);
		this.parameterCar.put("HAS_BICYCLE", cardriver_bicycle_TRUE);
		*/
		this.parameterCar.put("TOUR_CONTAINS_SERVICE",  cardriver_containsService);
		this.parameterCar.put("TOUR_CONTAINS_STROLLING",  cardriver_containsStrolling);
		this.parameterCar.put("TOUR_CONTAINS_BUSINESS",  cardriver_containsBusiness);
		this.parameterCar.put("TOUR_CONTAINS_LEISURE",  cardriver_containsLeisure);
		this.parameterCar.put("TOUR_CONTAINS_PRIVATE_BUSINESS",  cardriver_containsPrivateB);
		this.parameterCar.put("TOUR_CONTAINS_SHOPPING",  cardriver_containsShopping);
		this.parameterCar.put("TOUR_CONTAINS_VISIT",  cardriver_containsVisit);
		this.parameterCar.put("FEMALE", cardriver_sex_female);
		this.parameterCar.put("DAY_SA",  cardriver_day_Saturday);
		this.parameterCar.put("DAY_SU",  cardriver_day_Sunday);
	
	
		this.parameterCar.put("EMPLOYMENT_EDUCATION",  				cardriver_employment_apprentice);
		this.parameterCar.put("EMPLOYMENT_FULLTIME",  				cardriver_employment_fulltime);
		this.parameterCar.put("EMPLOYMENT_INFANT",  					cardriver_employment_pupil);
		this.parameterCar.put("EMPLOYMENT_UNEMPLOYED",  			cardriver_employment_unemployed);
		this.parameterCar.put("EMPLOYMENT_NONE",  						cardriver_employment_other);
		this.parameterCar.put("EMPLOYMENT_HOMEKEEPER",  			cardriver_employment_homekeeper);
		this.parameterCar.put("EMPLOYMENT_PARTTIME",  				cardriver_employment_parttime);
		this.parameterCar.put("EMPLOYMENT_RETIRED",  					cardriver_employment_retired);
		this.parameterCar.put("EMPLOYMENT_STUDENT_PRIMARY",  	cardriver_employment_pupil);
		this.parameterCar.put("EMPLOYMENT_STUDENT_SECONDARY", cardriver_employment_pupil);
		this.parameterCar.put("EMPLOYMENT_STUDENT_TERTIARY",  cardriver_employment_student);


		this.parameterCar.put("AGE_0TO9",  	cardriver_age_06to09);
		this.parameterCar.put("AGE_10TO17",  cardriver_age_10to17);
		this.parameterCar.put("AGE_18TO25",  cardriver_age_18to25);
		this.parameterCar.put("AGE_26TO35",  cardriver_age_26to35);
		this.parameterCar.put("AGE_51TO60",  cardriver_age_51to60);
		this.parameterCar.put("AGE_61TO70",  cardriver_age_61to70);
		this.parameterCar.put("AGE_71PLUS",  cardriver_age_71plus);

		
		this.parameterCar.put("HHTYPE_SINGLE",  			cardriver_hhtype_single);
		this.parameterCar.put("HHTYPE_COUPLE",  			cardriver_hhtype_couple);
		this.parameterCar.put("HHTYPE_KIDS0TO7",  		cardriver_hhtype_kids_0to7);
		this.parameterCar.put("HHTYPE_KIDS8TO12",  		cardriver_hhtype_kids_8to12);
		this.parameterCar.put("HHTYPE_KIDS13PLUS",  	cardriver_hhtype_kids_13to18);
		this.parameterCar.put("HHTYPE_MULTIADULT",  	cardriver_hhtype_multiadult);
	
		this.parameterCar.put("PURPOSE_BUSINESS",  				cardriver_purpose_business);
		this.parameterCar.put("PURPOSE_EDUCATION",  			cardriver_purpose_education);
		this.parameterCar.put("PURPOSE_HOME",  						0.0);
		this.parameterCar.put("PURPOSE_LEISURE_INDOOR",  	cardriver_purpose_leisure_indoors);
		this.parameterCar.put("PURPOSE_LEISURE_OUTDOOR",  cardriver_purpose_leisure_outdoors);
		this.parameterCar.put("PURPOSE_LEISURE_OTHER",  	cardriver_purpose_leisure_other);
		this.parameterCar.put("PURPOSE_LEISURE_WALK",  		cardriver_purpose_leisure_walk);
		this.parameterCar.put("PURPOSE_OTHER",  					cardriver_purpose_other);
		this.parameterCar.put("PURPOSE_PRIVATE_BUSINESS", cardriver_purpose_private_business);
		this.parameterCar.put("PURPOSE_PRIVATE_VISIT",  	cardriver_purpose_visit);
		this.parameterCar.put("PURPOSE_SERVICE",  				cardriver_purpose_service);
		this.parameterCar.put("PURPOSE_SHOPPING_DAILY",  	cardriver_purpose_shopping_grocery);
		this.parameterCar.put("PURPOSE_SHOPPING_OTHER",  	cardriver_purpose_shopping_other);
		this.parameterCar.put("PURPOSE_BUSINESS",  				cardriver_purpose_business);
	
		// passenger
		this.parameterPassenger.put("CONST", 	carpassenger);
		this.parameterPassenger.put("INTRAZONAL", 	carpassenger_intrazonal);
		this.parameterPassenger.put("NUM_ACTIVITIES", 	carpassenger_num_activities);
		/*
		this.parameterPassenger.put("COMMUTING_TICKET", carpassenger_transitpass_TRUE);
		this.parameterPassenger.put("NO_DRIVING_LICENCE", carpassenger_licence_FALSE);
		this.parameterPassenger.put("HAS_BICYCLE", carpassenger_bicycle_TRUE);
		*/
		this.parameterPassenger.put("TOUR_CONTAINS_SERVICE",  carpassenger_containsService);
		this.parameterPassenger.put("TOUR_CONTAINS_STROLLING",  carpassenger_containsStrolling);
		this.parameterPassenger.put("TOUR_CONTAINS_BUSINESS",  carpassenger_containsBusiness);
		this.parameterPassenger.put("TOUR_CONTAINS_LEISURE",  carpassenger_containsLeisure);
		this.parameterPassenger.put("TOUR_CONTAINS_PRIVATE_BUSINESS",  carpassenger_containsPrivateB);
		this.parameterPassenger.put("TOUR_CONTAINS_SHOPPING",  carpassenger_containsShopping);
		this.parameterPassenger.put("TOUR_CONTAINS_VISIT",  carpassenger_containsVisit);
		this.parameterPassenger.put("FEMALE", carpassenger_sex_female);
		this.parameterPassenger.put("DAY_SA",  carpassenger_day_Saturday);
		this.parameterPassenger.put("DAY_SU",  carpassenger_day_Sunday);
	
		this.parameterPassenger.put("EMPLOYMENT_EDUCATION",  				carpassenger_employment_apprentice);
		this.parameterPassenger.put("EMPLOYMENT_FULLTIME",  				carpassenger_employment_fulltime);
		this.parameterPassenger.put("EMPLOYMENT_INFANT",  					carpassenger_employment_pupil);
		this.parameterPassenger.put("EMPLOYMENT_UNEMPLOYED",  			carpassenger_employment_unemployed);
		this.parameterPassenger.put("EMPLOYMENT_NONE",  						carpassenger_employment_other);
		this.parameterPassenger.put("EMPLOYMENT_HOMEKEEPER", 				carpassenger_employment_homekeeper);
		this.parameterPassenger.put("EMPLOYMENT_PARTTIME",  				carpassenger_employment_parttime);
		this.parameterPassenger.put("EMPLOYMENT_RETIRED",  					carpassenger_employment_retired);
		this.parameterPassenger.put("EMPLOYMENT_STUDENT_PRIMARY", 	carpassenger_employment_pupil);
		this.parameterPassenger.put("EMPLOYMENT_STUDENT_SECONDARY",	carpassenger_employment_pupil);
		this.parameterPassenger.put("EMPLOYMENT_STUDENT_TERTIARY", 	carpassenger_employment_student);
	
	

		this.parameterPassenger.put("AGE_0TO9",  	 carpassenger_age_06to09);
		this.parameterPassenger.put("AGE_10TO17",  carpassenger_age_10to17);
		this.parameterPassenger.put("AGE_18TO25",  carpassenger_age_18to25);
		this.parameterPassenger.put("AGE_26TO35",  carpassenger_age_26to35);
		this.parameterPassenger.put("AGE_51TO60",  carpassenger_age_51to60);
		this.parameterPassenger.put("AGE_61TO70",  carpassenger_age_61to70);
		this.parameterPassenger.put("AGE_71PLUS",  carpassenger_age_71plus);
		
		
		this.parameterPassenger.put("HHTYPE_SINGLE",  			carpassenger_hhtype_single);
		this.parameterPassenger.put("HHTYPE_COUPLE",  			carpassenger_hhtype_couple);
		this.parameterPassenger.put("HHTYPE_KIDS0TO7",  		carpassenger_hhtype_kids_0to7);
		this.parameterPassenger.put("HHTYPE_KIDS8TO12",  		carpassenger_hhtype_kids_8to12);
		this.parameterPassenger.put("HHTYPE_KIDS13PLUS",  	carpassenger_hhtype_kids_13to18);
		this.parameterPassenger.put("HHTYPE_MULTIADULT",  	carpassenger_hhtype_multiadult);
		
	
		this.parameterPassenger.put("PURPOSE_BUSINESS",  				carpassenger_purpose_business);
		this.parameterPassenger.put("PURPOSE_EDUCATION",  			carpassenger_purpose_education);
		this.parameterPassenger.put("PURPOSE_HOME",  						0.0);
		this.parameterPassenger.put("PURPOSE_LEISURE_INDOOR",  	carpassenger_purpose_leisure_indoors);
		this.parameterPassenger.put("PURPOSE_LEISURE_OUTDOOR",  carpassenger_purpose_leisure_outdoors);
		this.parameterPassenger.put("PURPOSE_LEISURE_OTHER",  	carpassenger_purpose_leisure_other);
		this.parameterPassenger.put("PURPOSE_LEISURE_WALK",  		carpassenger_purpose_leisure_walk);
		this.parameterPassenger.put("PURPOSE_OTHER",  					carpassenger_purpose_other);
		this.parameterPassenger.put("PURPOSE_PRIVATE_BUSINESS", carpassenger_purpose_private_business);
		this.parameterPassenger.put("PURPOSE_PRIVATE_VISIT",  	carpassenger_purpose_visit);
		this.parameterPassenger.put("PURPOSE_SERVICE",  				carpassenger_purpose_service);
		this.parameterPassenger.put("PURPOSE_SHOPPING_DAILY",  	carpassenger_purpose_shopping_grocery);
		this.parameterPassenger.put("PURPOSE_SHOPPING_OTHER",  	carpassenger_purpose_shopping_other);
		this.parameterPassenger.put("PURPOSE_BUSINESS",  				carpassenger_purpose_business);
	
	// pt
		this.parameterPt.put("CONST", 	publictransport);
		this.parameterPt.put("INTRAZONAL", 	publictransport_intrazonal);
		this.parameterPt.put("NUM_ACTIVITIES", 	publictransport_num_activities);
		/*
		this.parameterPt.put("COMMUTING_TICKET", publictransport_transitpass_TRUE);
		this.parameterPt.put("NO_DRIVING_LICENCE", publictransport_licence_FALSE);
		this.parameterPt.put("HAS_BICYCLE", publictransport_bicycle_TRUE);
		*/
		this.parameterPt.put("TOUR_CONTAINS_SERVICE",  publictransport_containsService);
		this.parameterPt.put("TOUR_CONTAINS_STROLLING",  publictransport_containsStrolling);
		this.parameterPt.put("TOUR_CONTAINS_BUSINESS",  publictransport_containsBusiness);
		this.parameterPt.put("TOUR_CONTAINS_LEISURE",  publictransport_containsLeisure);
		this.parameterPt.put("TOUR_CONTAINS_PRIVATE_BUSINESS",  publictransport_containsPrivateB);
		this.parameterPt.put("TOUR_CONTAINS_SHOPPING",  publictransport_containsShopping);
		this.parameterPt.put("TOUR_CONTAINS_VISIT",  publictransport_containsVisit);
		this.parameterPt.put("FEMALE", publictransport_sex_female);
		this.parameterPt.put("DAY_SA",  publictransport_day_Saturday);
		this.parameterPt.put("DAY_SU",  publictransport_day_Sunday);
	
	
		this.parameterPt.put("EMPLOYMENT_EDUCATION",  				publictransport_employment_apprentice);
		this.parameterPt.put("EMPLOYMENT_FULLTIME",  					publictransport_employment_fulltime);
		this.parameterPt.put("EMPLOYMENT_INFANT",  						publictransport_employment_pupil);
		this.parameterPt.put("EMPLOYMENT_UNEMPLOYED",  				publictransport_employment_unemployed);
		this.parameterPt.put("EMPLOYMENT_NONE",  							publictransport_employment_other);
		this.parameterPt.put("EMPLOYMENT_HOMEKEEPER",  				publictransport_employment_homekeeper);
		this.parameterPt.put("EMPLOYMENT_PARTTIME",  					publictransport_employment_parttime);
		this.parameterPt.put("EMPLOYMENT_RETIRED",  					publictransport_employment_retired);
		this.parameterPt.put("EMPLOYMENT_STUDENT_PRIMARY",		publictransport_employment_pupil);
		this.parameterPt.put("EMPLOYMENT_STUDENT_SECONDARY",	publictransport_employment_pupil);
		this.parameterPt.put("EMPLOYMENT_STUDENT_TERTIARY",		publictransport_employment_student);
	
	
		this.parameterPt.put("AGE_0TO9",  	publictransport_age_06to09);
		this.parameterPt.put("AGE_10TO17",  publictransport_age_10to17);
		this.parameterPt.put("AGE_18TO25",  publictransport_age_18to25);
		this.parameterPt.put("AGE_26TO35",  publictransport_age_26to35);
		this.parameterPt.put("AGE_51TO60",  publictransport_age_51to60);
		this.parameterPt.put("AGE_61TO70",  publictransport_age_61to70);
		this.parameterPt.put("AGE_71PLUS",  publictransport_age_71plus);
		
		this.parameterPt.put("HHTYPE_SINGLE",  			publictransport_hhtype_single);
		this.parameterPt.put("HHTYPE_COUPLE",  			publictransport_hhtype_couple);
		this.parameterPt.put("HHTYPE_KIDS0TO7",  		publictransport_hhtype_kids_0to7);
		this.parameterPt.put("HHTYPE_KIDS8TO12",  	publictransport_hhtype_kids_8to12);
		this.parameterPt.put("HHTYPE_KIDS13PLUS",  	publictransport_hhtype_kids_13to18);
		this.parameterPt.put("HHTYPE_MULTIADULT",  	publictransport_hhtype_multiadult);
	
		this.parameterPt.put("PURPOSE_BUSINESS",  				publictransport_purpose_business);
		this.parameterPt.put("PURPOSE_EDUCATION",  				publictransport_purpose_education);
		this.parameterPt.put("PURPOSE_HOME",  						0.0); 	// wird benötigt für Ausnahmefälle
		this.parameterPt.put("PURPOSE_LEISURE_INDOOR",  	publictransport_purpose_leisure_indoors);
		this.parameterPt.put("PURPOSE_LEISURE_OUTDOOR",  	publictransport_purpose_leisure_outdoors);
		this.parameterPt.put("PURPOSE_LEISURE_OTHER",  		publictransport_purpose_leisure_other);
		this.parameterPt.put("PURPOSE_LEISURE_WALK",  		publictransport_purpose_leisure_walk);
		this.parameterPt.put("PURPOSE_OTHER",  						publictransport_purpose_other);
		this.parameterPt.put("PURPOSE_PRIVATE_BUSINESS",  publictransport_purpose_private_business);
		this.parameterPt.put("PURPOSE_PRIVATE_VISIT",  		publictransport_purpose_visit);
		this.parameterPt.put("PURPOSE_SERVICE",  					publictransport_purpose_service);
		this.parameterPt.put("PURPOSE_SHOPPING_DAILY",  	publictransport_purpose_shopping_grocery);
		this.parameterPt.put("PURPOSE_SHOPPING_OTHER",  	publictransport_purpose_shopping_other);
		this.parameterPt.put("PURPOSE_BUSINESS",  				publictransport_purpose_business);
	}

	public Map<String,Double> parameterForMode(Mode mode) {
		if (Mode.PEDESTRIAN.equals(mode)) {
			return parameterWalk;
		}
		if (Mode.BIKE.equals(mode)) {
			return parameterBike;
		}
		if (Mode.CAR.equals(mode)) {
			return parameterCar;
		}
		if (Mode.PASSENGER.equals(mode)) {
			return parameterPassenger;
		}
		if (Mode.PUBLICTRANSPORT.equals(mode)) {
			return parameterPt;
		}
		throw new AssertionError("invalid mode: " + mode);
	}

}