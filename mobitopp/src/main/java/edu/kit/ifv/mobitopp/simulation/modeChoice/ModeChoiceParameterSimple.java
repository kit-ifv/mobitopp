package edu.kit.ifv.mobitopp.simulation.modeChoice;

import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;

class ModeChoiceParameterSimple 
	implements ModeChoiceParameter
{

	public final double walk_const                              =  -0.0f						-0.15f;
	public final double bike_const                              =  -1.135554216f		-0.10f;
	public final double car_const                               =  -0.156715219f		-0.35f	+0.05f;
	public final double passenger_const                         =  -4.294146034f		-0.35f	-0.1f;
	public final double pt_const                                =  -2.837248541f		+0.18f	+0.05f;

	public final double bike_intrazonal              =  -0.095082432f	+0.3f		+0.2f;
	public final double car_intrazonal               =  -0.920633136f	+1.2f		-0.2f;
	public final double passenger_intrazonal         =  -1.183225738f	+0.04f	+0.0f;
	public final double pt_intrazonal                =  -2.030177214f	-0.0f;

	public final double time_km                  =  -0.028840345f;
	public final double cost_km                  =  -0.357300688f;

	public final double bike_dist_km                  =   0.563432837f;
	public final double car_dist_km                   =   0.782612544f;
	public final double passenger_dist_km             =   0.794702610f;
	public final double pt_dist_km                    =   0.794385070f;

	public final double bike_commuting_ticket                   =  -0.427519173f;
	public final double car_commuting_ticket                    =  -0.822681319f;
	public final double passenger_commuting_ticket              =  -0.195836830f;
	public final double pt_commuting_ticket                     =   2.369536426f;

	public final double bike_no_driving_licence                 =  -0.592530236f;
	public final double car_no_driving_licence                  =  -4.130871709f;
	public final double passenger_no_driving_licence            =   0.003197803f;
	public final double pt_no_driving_licence                   =  -0.055511164f;

	public final double bike_female                   =  -0.567033272f;
	public final double car_female                    =  -0.639109750f;
	public final double passenger_female              =   0.674199210f;
	public final double pt_female                     =   0.084118145f;


	public final double walk_day_SA                        =  	0.0f					-0.4f;
	public final double bike_day_SA                        =  -0.196256913f;
	public final double car_day_SA                         =  -0.004288772f	+0.0f;
	public final double passenger_day_SA                   =   0.529847096f	+0.4f;
	public final double pt_day_SA                          =  -0.138846928f;

	public final double walk_day_SU                        =  	0.0f					-0.7f;
	public final double bike_day_SU                        =  -0.454292877f;
	public final double car_day_SU                         =  -0.550669592f	+0.1f;
	public final double passenger_day_SU                   =   0.167301163f	+0.3f;
	public final double pt_day_SU                          =  -1.063381319f	+0.2f;


	public final double bike_employment_EDUCATION          =   0.114985596f;
	public final double car_employment_EDUCATION           =   0.509643064f 	+0.5f;
	public final double passenger_employment_EDUCATION     =   0.339039803f	-0.5f;
	public final double pt_employment_EDUCATION            =   0.047232353f;

	public final double bike_employment_INFANT             =  -0.363205044f;
	public final double car_employment_INFANT              =  -0.037373775f;
	public final double passenger_employment_INFANT        =   0.913189939f;
	public final double pt_employment_INFANT               =   0.690772082f;

	public final double bike_employment_JOBLESS            =  -0.315296701f;
	public final double car_employment_JOBLESS             =  -0.584236222f;
	public final double passenger_employment_JOBLESS       =  -0.218397789f;
	public final double pt_employment_JOBLESS              =   0.058309263f;

	public final double bike_employment_NONE               =  -0.683183159f;
	public final double car_employment_NONE                =  -0.471357952f	+0.5f;
	public final double passenger_employment_NONE          =  -0.189507836f	-0.5f;
	public final double pt_employment_NONE                 =  -0.034615119f;

	public final double bike_employment_NOT_WORKING        =  -0.198162389f;
	public final double car_employment_NOT_WORKING         =  -0.215497288f	+0.3f;
	public final double passenger_employment_NOT_WORKING   =  -0.028002575f;
	public final double pt_employment_NOT_WORKING          =  -0.157271937f	-0.1f;

	public final double bike_employment_PARTTIME           =   0.324478964f	-0.03f;
	public final double car_employment_PARTTIME            =   0.135891002f	+0.25f;
	public final double passenger_employment_PARTTIME      =   0.183096535f	-0.2f;
	public final double pt_employment_PARTTIME             =   0.010515388f	+0.0f;

	public final double bike_employment_FULLTIME           =   0.0f	-0.05f;
	public final double car_employment_FULLTIME            =   0.0f	+0.15f;
	public final double passenger_employment_FULLTIME      =   0.0f	-0.15f;
	public final double pt_employment_FULLTIME             =   0.0f	+0.05f;

	public final double bike_employment_RETIRED            =  -0.640145297f	+0.15f;
	public final double car_employment_RETIRED             =  -0.626501531f	+0.5f;
	public final double passenger_employment_RETIRED       =  -0.419037233f	+0.1f;
	public final double pt_employment_RETIRED              =  -0.077916076f	+0.15f;

	public final double bike_employment_STUDENT            =   0.396924921f;
	public final double car_employment_STUDENT             =  -0.075693423f	+0.7f;
	public final double passenger_employment_STUDENT       =   0.046438566f	-0.4f;
	public final double pt_employment_STUDENT              =  -0.200476993f	-0.4f;


	public final double bike_purpose_BUSINESS              =  -0.315712362f	-0.75f;
	public final double car_purpose_BUSINESS               =   0.568011794f	+0.9f;
	public final double passenger_purpose_BUSINESS         =   0.743220379f	+1.3f;
	public final double pt_purpose_BUSINESS                =  -0.031422537f	+0.7f;

	public final double bike_purpose_EDUCATION             =  -0.720921709f	+0.25f;
	public final double car_purpose_EDUCATION              =  -1.021528043f	-0.8f;
	public final double passenger_purpose_EDUCATION        =  -0.138616258f	+0.65f;
	public final double pt_purpose_EDUCATION               =  -0.111216798f	+0.65f;

	public final double bike_purpose_HOME                  =  -0.823556102f;
	public final double car_purpose_HOME                   =  -0.062112463f	-0.05f;
	public final double passenger_purpose_HOME             =   1.530957797f;
	public final double pt_purpose_HOME                    =  -0.155199243f;

	public final double bike_purpose_LEISURE_INDOOR        =  -1.618148446f	- 1.5f;
	public final double car_purpose_LEISURE_INDOOR         =  -0.845954981f	- 1.2f;
	public final double passenger_purpose_LEISURE_INDOOR   =   1.163868360f	+ 0.15f;
	public final double pt_purpose_LEISURE_INDOOR          =  -0.457385550f	- 0.1f;

	public final double bike_purpose_LEISURE_OTHER         =  -0.846082922f	-0.55f;
	public final double car_purpose_LEISURE_OTHER          =  -0.179380713f	-0.6f;
	public final double passenger_purpose_LEISURE_OTHER    =   1.253801946f	+0.2f;
	public final double pt_purpose_LEISURE_OTHER           =  -0.429696731f	-0.0f;

	public final double bike_purpose_LEISURE_OUTDOOR       =  -0.312092202f	+0.0f;
	public final double car_purpose_LEISURE_OUTDOOR        =   0.609016017f	-0.3f;
	public final double passenger_purpose_LEISURE_OUTDOOR  =   1.813688344f	+0.5f;
	public final double pt_purpose_LEISURE_OUTDOOR         =  -0.824449765f	+0.2f;

	public final double bike_purpose_LEISURE_WALK          =  -2.374425547f	+0.5f;
	public final double car_purpose_LEISURE_WALK           =  -2.962287630f	-2.0f;
	public final double passenger_purpose_LEISURE_WALK     =  -1.209223937f	-0.5f;
	public final double pt_purpose_LEISURE_WALK            =  -3.606917986f;

	public final double bike_purpose_OTHER                 =  -0.356103564f;
	public final double car_purpose_OTHER                  =   0.660259921f	-1.0f;
	public final double passenger_purpose_OTHER            =   2.317950216f	-0.5f;
	public final double pt_purpose_OTHER                   =   0.675906529f	-2.0f;

	public final double bike_purpose_PRIVATE_BUSINESS      =  -0.705941299f 	+0.3f;
	public final double car_purpose_PRIVATE_BUSINESS       =   0.251098346f 	+0.1f;
	public final double passenger_purpose_PRIVATE_BUSINESS =   1.347996542f	+0.6f;
	public final double pt_purpose_PRIVATE_BUSINESS        =  -0.486515902f	+0.6f;

	public final double bike_purpose_PRIVATE_VISIT         =  -0.871281194f	+0.05f;
	public final double car_purpose_PRIVATE_VISIT          =   0.046198790f	-0.15f;
	public final double passenger_purpose_PRIVATE_VISIT    =   1.235097268f	+0.5f;
	public final double pt_purpose_PRIVATE_VISIT           =  -1.157203997f	+0.35f;

	public final double bike_purpose_SERVICE               =  -0.948053231f	-0.37f;
	public final double car_purpose_SERVICE                =   1.044898934f	+0.7f;
	public final double passenger_purpose_SERVICE          =   0.984002644f	+0.35f;
	public final double pt_purpose_SERVICE                 =  -1.379886290f	-0.0f;

	public final double bike_purpose_SHOPPING_DAILY        =  -0.562891949f	+0.2f;
	public final double car_purpose_SHOPPING_DAILY         =   0.253130727f	+0.2f;
	public final double passenger_purpose_SHOPPING_DAILY   =   1.222357053f	+0.6f;
	public final double pt_purpose_SHOPPING_DAILY          =  -1.100201907f	+0.8f;

	public final double bike_purpose_SHOPPING_OTHER        =  -0.596589234f	-0.05f;
	public final double car_purpose_SHOPPING_OTHER         =   0.302772456f 	+0.1f;
	public final double passenger_purpose_SHOPPING_OTHER   =   1.596579178f	+0.5f;
	public final double pt_purpose_SHOPPING_OTHER          =  -0.294744124f 	+0.4f;


	public final double bike_age_0to9                       =  -1.219998625f		+0.2f; // Correction for "Student"
	public final double car_age_0to9                       =  -19.409428774f		-0.6f;
	public final double passenger_age_0to9                  =   1.444763141f		+0.4f	+0.0f;
	public final double pt_age_0to9                         =  -0.369952894f		+0.3f	-1.0f -0.4f;

	public final double bike_age_10to17                     =   0.905400552f; // Correction for "Student"
	public final double car_age_10to17                      =  -0.028866673f		-0.6f - 0.5f;
	public final double passenger_age_10to17                =   1.641004767f		+0.4f	+0.1f;
	public final double pt_age_10to17                       =   0.650167559f		+0.3f	+0.3f;

	public final double bike_age_18to25                     =   0.066218139f;
	public final double car_age_18to25                      =   0.611543802f;
	public final double passenger_age_18to25                =   1.100420667f;
	public final double pt_age_18to25                       =   0.514064868f;

	public final double bike_age_26to35                     =  -0.487771970f;
	public final double car_age_26to35                      =  -0.465007769f;
	public final double passenger_age_26to35                =  -0.197607298f;
	public final double pt_age_26to35                       =  -0.084504160f;
	public final double bike_age_51to60                     =  -0.279277908f;
	public final double car_age_51to60                      =   0.135240486f;
	public final double passenger_age_51to60                =   0.360189845f;
	public final double pt_age_51to60                       =   0.188256908f;
	public final double bike_age_61to70                     =  -0.128295437f;
	public final double car_age_61to70                      =   0.063149242f;
	public final double passenger_age_61to70                =   0.683020271f;
	public final double pt_age_61to70                       =   0.334229251f;
	public final double bike_age_70plus                       =  -0.226297261f;
	public final double car_age_70plus                        =   0.026608404f;
	public final double passenger_age_70plus                  =   0.619391086f;
	public final double pt_age_70plus                         =   0.481590162f;


	private final Map<String,Double> parameterWalk 			= new LinkedHashMap<String,Double>();
	private final Map<String,Double> parameterBike 			= new LinkedHashMap<String,Double>();
	private final Map<String,Double> parameterCar 				= new LinkedHashMap<String,Double>();
	private final Map<String,Double> parameterPassenger 	= new LinkedHashMap<String,Double>();
	private final Map<String,Double> parameterPt 				= new LinkedHashMap<String,Double>();

	public ModeChoiceParameterSimple() {

		init();
	}

	private void init() {

		// walk
		this.parameterWalk.put("CONST", 	walk_const);
		this.parameterWalk.put("TIME_KM", time_km);
		this.parameterWalk.put("COST_KM", cost_km);
		this.parameterWalk.put("DAY_SA",  walk_day_SA);
		this.parameterWalk.put("DAY_SU",  walk_day_SU);

		// bike
		this.parameterBike.put("CONST", 	bike_const);
		this.parameterBike.put("INTRAZONAL", 	bike_intrazonal);
		this.parameterBike.put("TIME_KM", time_km);
		this.parameterBike.put("COST_KM", cost_km);
		this.parameterBike.put("DIST_KM", bike_dist_km);
		this.parameterBike.put("COMMUTING_TICKET", bike_commuting_ticket);
		this.parameterBike.put("NO_DRIVING_LICENCE", bike_no_driving_licence);
		this.parameterBike.put("FEMALE", bike_female);
		this.parameterBike.put("DAY_SA",  bike_day_SA);
		this.parameterBike.put("DAY_SU",  bike_day_SU);

		this.parameterBike.put("EMPLOYMENT_EDUCATION",  	bike_employment_EDUCATION);
		this.parameterBike.put("EMPLOYMENT_FULLTIME",  		bike_employment_FULLTIME);
		this.parameterBike.put("EMPLOYMENT_INFANT",  			bike_employment_INFANT);
		this.parameterBike.put("EMPLOYMENT_JOBLESS",  		bike_employment_JOBLESS);
		this.parameterBike.put("EMPLOYMENT_NONE",  				bike_employment_NONE);
		this.parameterBike.put("EMPLOYMENT_NOT_WORKING",  bike_employment_NOT_WORKING);
		this.parameterBike.put("EMPLOYMENT_PARTTIME",  		bike_employment_PARTTIME);
		this.parameterBike.put("EMPLOYMENT_RETIRED",  		bike_employment_RETIRED);
		this.parameterBike.put("EMPLOYMENT_STUDENT",  		bike_employment_STUDENT);

		this.parameterBike.put("AGE_0TO9",  	bike_age_0to9);
		this.parameterBike.put("AGE_10TO17",  bike_age_10to17);
		this.parameterBike.put("AGE_18TO25",  bike_age_18to25);
		this.parameterBike.put("AGE_26TO35",  bike_age_26to35);
		this.parameterBike.put("AGE_51TO60",  bike_age_51to60);
		this.parameterBike.put("AGE_61TO70",  bike_age_61to70);
		this.parameterBike.put("AGE_70PLUS",  bike_age_70plus);

		this.parameterBike.put("PURPOSE_BUSINESS",  				bike_purpose_BUSINESS);
		this.parameterBike.put("PURPOSE_EDUCATION",  				bike_purpose_EDUCATION);
		this.parameterBike.put("PURPOSE_HOME",  						bike_purpose_HOME);
		this.parameterBike.put("PURPOSE_LEISURE_INDOOR",  	bike_purpose_LEISURE_INDOOR);
		this.parameterBike.put("PURPOSE_LEISURE_OUTDOOR",  	bike_purpose_LEISURE_OUTDOOR);
		this.parameterBike.put("PURPOSE_LEISURE_OTHER",  		bike_purpose_LEISURE_OTHER);
		this.parameterBike.put("PURPOSE_LEISURE_WALK",  		bike_purpose_LEISURE_WALK);
		this.parameterBike.put("PURPOSE_OTHER",  						bike_purpose_OTHER);
		this.parameterBike.put("PURPOSE_PRIVATE_BUSINESS",  bike_purpose_PRIVATE_BUSINESS);
		this.parameterBike.put("PURPOSE_PRIVATE_VISIT",  		bike_purpose_PRIVATE_VISIT);
		this.parameterBike.put("PURPOSE_SERVICE",  					bike_purpose_SERVICE);
		this.parameterBike.put("PURPOSE_SHOPPING_DAILY",  	bike_purpose_SHOPPING_DAILY);
		this.parameterBike.put("PURPOSE_SHOPPING_OTHER",  	bike_purpose_SHOPPING_OTHER);
		this.parameterBike.put("PURPOSE_BUSINESS",  				bike_purpose_BUSINESS);

		// car
		this.parameterCar.put("CONST", 	car_const);
		this.parameterCar.put("INTRAZONAL", 	car_intrazonal);
		this.parameterCar.put("TIME_KM", time_km);
		this.parameterCar.put("COST_KM", cost_km);
		this.parameterCar.put("DIST_KM", car_dist_km);
		this.parameterCar.put("COMMUTING_TICKET", car_commuting_ticket);
		this.parameterCar.put("NO_DRIVING_LICENCE", car_no_driving_licence);
		this.parameterCar.put("FEMALE", car_female);
		this.parameterCar.put("DAY_SA",  car_day_SA);
		this.parameterCar.put("DAY_SU",  car_day_SU);

		this.parameterCar.put("EMPLOYMENT_EDUCATION",  	car_employment_EDUCATION);
		this.parameterCar.put("EMPLOYMENT_FULLTIME",  		car_employment_FULLTIME);
		this.parameterCar.put("EMPLOYMENT_INFANT",  			car_employment_INFANT);
		this.parameterCar.put("EMPLOYMENT_JOBLESS",  		car_employment_JOBLESS);
		this.parameterCar.put("EMPLOYMENT_NONE",  				car_employment_NONE);
		this.parameterCar.put("EMPLOYMENT_NOT_WORKING",  car_employment_NOT_WORKING);
		this.parameterCar.put("EMPLOYMENT_PARTTIME",  		car_employment_PARTTIME);
		this.parameterCar.put("EMPLOYMENT_RETIRED",  		car_employment_RETIRED);
		this.parameterCar.put("EMPLOYMENT_STUDENT",  		car_employment_STUDENT);

		this.parameterCar.put("AGE_0TO9",  	car_age_0to9);
		this.parameterCar.put("AGE_10TO17",  car_age_10to17);
		this.parameterCar.put("AGE_18TO25",  car_age_18to25);
		this.parameterCar.put("AGE_26TO35",  car_age_26to35);
		this.parameterCar.put("AGE_51TO60",  car_age_51to60);
		this.parameterCar.put("AGE_61TO70",  car_age_61to70);
		this.parameterCar.put("AGE_70PLUS",  car_age_70plus);

		this.parameterCar.put("PURPOSE_BUSINESS",  				car_purpose_BUSINESS);
		this.parameterCar.put("PURPOSE_EDUCATION",  				car_purpose_EDUCATION);
		this.parameterCar.put("PURPOSE_HOME",  						car_purpose_HOME);
		this.parameterCar.put("PURPOSE_LEISURE_INDOOR",  	car_purpose_LEISURE_INDOOR);
		this.parameterCar.put("PURPOSE_LEISURE_OUTDOOR",  	car_purpose_LEISURE_OUTDOOR);
		this.parameterCar.put("PURPOSE_LEISURE_OTHER",  		car_purpose_LEISURE_OTHER);
		this.parameterCar.put("PURPOSE_LEISURE_WALK",  		car_purpose_LEISURE_WALK);
		this.parameterCar.put("PURPOSE_OTHER",  						car_purpose_OTHER);
		this.parameterCar.put("PURPOSE_PRIVATE_BUSINESS",  car_purpose_PRIVATE_BUSINESS);
		this.parameterCar.put("PURPOSE_PRIVATE_VISIT",  		car_purpose_PRIVATE_VISIT);
		this.parameterCar.put("PURPOSE_SERVICE",  					car_purpose_SERVICE);
		this.parameterCar.put("PURPOSE_SHOPPING_DAILY",  	car_purpose_SHOPPING_DAILY);
		this.parameterCar.put("PURPOSE_SHOPPING_OTHER",  	car_purpose_SHOPPING_OTHER);
		this.parameterCar.put("PURPOSE_BUSINESS",  				car_purpose_BUSINESS);

		// passenger
		this.parameterPassenger.put("CONST", 	passenger_const);
		this.parameterPassenger.put("INTRAZONAL", 	passenger_intrazonal);
		this.parameterPassenger.put("TIME_KM", time_km);
		this.parameterPassenger.put("COST_KM", cost_km);
		this.parameterPassenger.put("DIST_KM", passenger_dist_km);
		this.parameterPassenger.put("COMMUTING_TICKET", passenger_commuting_ticket);
		this.parameterPassenger.put("NO_DRIVING_LICENCE", passenger_no_driving_licence);
		this.parameterPassenger.put("FEMALE", passenger_female);
		this.parameterPassenger.put("DAY_SA",  passenger_day_SA);
		this.parameterPassenger.put("DAY_SU",  passenger_day_SU);

		this.parameterPassenger.put("EMPLOYMENT_EDUCATION",  	passenger_employment_EDUCATION);
		this.parameterPassenger.put("EMPLOYMENT_FULLTIME",  		passenger_employment_FULLTIME);
		this.parameterPassenger.put("EMPLOYMENT_INFANT",  			passenger_employment_INFANT);
		this.parameterPassenger.put("EMPLOYMENT_JOBLESS",  		passenger_employment_JOBLESS);
		this.parameterPassenger.put("EMPLOYMENT_NONE",  				passenger_employment_NONE);
		this.parameterPassenger.put("EMPLOYMENT_NOT_WORKING",  passenger_employment_NOT_WORKING);
		this.parameterPassenger.put("EMPLOYMENT_PARTTIME",  		passenger_employment_PARTTIME);
		this.parameterPassenger.put("EMPLOYMENT_RETIRED",  		passenger_employment_RETIRED);
		this.parameterPassenger.put("EMPLOYMENT_STUDENT",  		passenger_employment_STUDENT);

		this.parameterPassenger.put("AGE_0TO9",  	passenger_age_0to9);
		this.parameterPassenger.put("AGE_10TO17",  passenger_age_10to17);
		this.parameterPassenger.put("AGE_18TO25",  passenger_age_18to25);
		this.parameterPassenger.put("AGE_26TO35",  passenger_age_26to35);
		this.parameterPassenger.put("AGE_51TO60",  passenger_age_51to60);
		this.parameterPassenger.put("AGE_61TO70",  passenger_age_61to70);
		this.parameterPassenger.put("AGE_70PLUS",  passenger_age_70plus);

		this.parameterPassenger.put("PURPOSE_BUSINESS",  				passenger_purpose_BUSINESS);
		this.parameterPassenger.put("PURPOSE_EDUCATION",  				passenger_purpose_EDUCATION);
		this.parameterPassenger.put("PURPOSE_HOME",  						passenger_purpose_HOME);
		this.parameterPassenger.put("PURPOSE_LEISURE_INDOOR",  	passenger_purpose_LEISURE_INDOOR);
		this.parameterPassenger.put("PURPOSE_LEISURE_OUTDOOR",  	passenger_purpose_LEISURE_OUTDOOR);
		this.parameterPassenger.put("PURPOSE_LEISURE_OTHER",  		passenger_purpose_LEISURE_OTHER);
		this.parameterPassenger.put("PURPOSE_LEISURE_WALK",  		passenger_purpose_LEISURE_WALK);
		this.parameterPassenger.put("PURPOSE_OTHER",  						passenger_purpose_OTHER);
		this.parameterPassenger.put("PURPOSE_PRIVATE_BUSINESS",  passenger_purpose_PRIVATE_BUSINESS);
		this.parameterPassenger.put("PURPOSE_PRIVATE_VISIT",  		passenger_purpose_PRIVATE_VISIT);
		this.parameterPassenger.put("PURPOSE_SERVICE",  					passenger_purpose_SERVICE);
		this.parameterPassenger.put("PURPOSE_SHOPPING_DAILY",  	passenger_purpose_SHOPPING_DAILY);
		this.parameterPassenger.put("PURPOSE_SHOPPING_OTHER",  	passenger_purpose_SHOPPING_OTHER);
		this.parameterPassenger.put("PURPOSE_BUSINESS",  				passenger_purpose_BUSINESS);

	// pt
		this.parameterPt.put("CONST", 	pt_const);
		this.parameterPt.put("INTRAZONAL", 	pt_intrazonal);
		this.parameterPt.put("TIME_KM", time_km);
		this.parameterPt.put("COST_KM", cost_km);
		this.parameterPt.put("DIST_KM", pt_dist_km);
		this.parameterPt.put("COMMUTING_TICKET", pt_commuting_ticket);
		this.parameterPt.put("NO_DRIVING_LICENCE", pt_no_driving_licence);
		this.parameterPt.put("FEMALE", pt_female);
		this.parameterPt.put("DAY_SA",  pt_day_SA);
		this.parameterPt.put("DAY_SU",  pt_day_SU);

		this.parameterPt.put("EMPLOYMENT_EDUCATION",  	pt_employment_EDUCATION);
		this.parameterPt.put("EMPLOYMENT_FULLTIME",  		pt_employment_FULLTIME);
		this.parameterPt.put("EMPLOYMENT_INFANT",  			pt_employment_INFANT);
		this.parameterPt.put("EMPLOYMENT_JOBLESS",  		pt_employment_JOBLESS);
		this.parameterPt.put("EMPLOYMENT_NONE",  				pt_employment_NONE);
		this.parameterPt.put("EMPLOYMENT_NOT_WORKING",  pt_employment_NOT_WORKING);
		this.parameterPt.put("EMPLOYMENT_PARTTIME",  		pt_employment_PARTTIME);
		this.parameterPt.put("EMPLOYMENT_RETIRED",  		pt_employment_RETIRED);
		this.parameterPt.put("EMPLOYMENT_STUDENT",  		pt_employment_STUDENT);

		this.parameterPt.put("AGE_0TO9",  	pt_age_0to9);
		this.parameterPt.put("AGE_10TO17",  pt_age_10to17);
		this.parameterPt.put("AGE_18TO25",  pt_age_18to25);
		this.parameterPt.put("AGE_26TO35",  pt_age_26to35);
		this.parameterPt.put("AGE_51TO60",  pt_age_51to60);
		this.parameterPt.put("AGE_61TO70",  pt_age_61to70);
		this.parameterPt.put("AGE_70PLUS",  pt_age_70plus);

		this.parameterPt.put("PURPOSE_BUSINESS",  				pt_purpose_BUSINESS);
		this.parameterPt.put("PURPOSE_EDUCATION",  				pt_purpose_EDUCATION);
		this.parameterPt.put("PURPOSE_HOME",  						pt_purpose_HOME);
		this.parameterPt.put("PURPOSE_LEISURE_INDOOR",  	pt_purpose_LEISURE_INDOOR);
		this.parameterPt.put("PURPOSE_LEISURE_OUTDOOR",  	pt_purpose_LEISURE_OUTDOOR);
		this.parameterPt.put("PURPOSE_LEISURE_OTHER",  		pt_purpose_LEISURE_OTHER);
		this.parameterPt.put("PURPOSE_LEISURE_WALK",  		pt_purpose_LEISURE_WALK);
		this.parameterPt.put("PURPOSE_OTHER",  						pt_purpose_OTHER);
		this.parameterPt.put("PURPOSE_PRIVATE_BUSINESS",  pt_purpose_PRIVATE_BUSINESS);
		this.parameterPt.put("PURPOSE_PRIVATE_VISIT",  		pt_purpose_PRIVATE_VISIT);
		this.parameterPt.put("PURPOSE_SERVICE",  					pt_purpose_SERVICE);
		this.parameterPt.put("PURPOSE_SHOPPING_DAILY",  	pt_purpose_SHOPPING_DAILY);
		this.parameterPt.put("PURPOSE_SHOPPING_OTHER",  	pt_purpose_SHOPPING_OTHER);
		this.parameterPt.put("PURPOSE_BUSINESS",  				pt_purpose_BUSINESS);
	}



	public Map<Mode,Map<String,Double>> gatherAttributes(
		Person person,
		Set<Mode> modes,
		Zone sourceZone,
		Zone targetZone,
		ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		ImpedanceIfc impedance
	) {

		int source = sourceZone.getOid();
		int target = targetZone.getOid();

		Time date = nextActivity.startDate();

		double distance 		= Math.max(0.1, impedance.getDistance(source, target)/1000.0);

		assert !Double.isNaN(distance);
		assert distance > 0.0f;

		double intrazonal = source == target || distance <= 1.0f ? 1 : 0;

		double commuting_ticket = person.hasCommuterTicket() ? 1 : 0;

		double no_driving_licence = !person.hasPersonalCar() && !person.hasAccessToCar()  ? 1 : 0;  

		double female = person.gender() == Gender.FEMALE ? 1 : 0;

		double day_SA 		= date.weekDay() == DayOfWeek.SATURDAY ? 1 : 0;
		double day_SU 		= date.weekDay() == DayOfWeek.SUNDAY ? 1 : 0;

		double employment_EDUCATION 		= person.employment() == Employment.EDUCATION ? 1 : 0;
		double employment_INFANT 		  = person.employment() == Employment.INFANT ? 1 : 0;
		double employment_JOBLESS 	  	= person.employment() == Employment.UNEMPLOYED ? 1 : 0;
		double employment_NONE 				= person.employment() == Employment.NONE ? 1 : 0;
		double employment_NOT_WORKING 	= person.employment() == Employment.HOMEKEEPER ? 1 : 0;
		double employment_PARTTIME 		= person.employment() == Employment.PARTTIME ? 1 : 0;
		double employment_FULLTIME 		= person.employment() == Employment.FULLTIME ? 1 : 0;
		double employment_RETIRED 			= person.employment() == Employment.RETIRED ? 1 : 0;
		double employment_STUDENT 			= person.employment() == Employment.STUDENT_PRIMARY
															 || person.employment() == Employment.STUDENT_SECONDARY
															 || person.employment() == Employment.STUDENT_TERTIARY
															 || person.employment() == Employment.STUDENT ? 1 : 0;


		double purpose_WORK 		 		 = nextActivity.activityType().isWorkActivity() ? 1 : 0; // reference case
		double purpose_BUSINESS 		 = nextActivity.activityType().isBusinessActivity() ? 1 : 0;
		double purpose_EDUCATION 	 = nextActivity.activityType() == ActivityType.EDUCATION_PRIMARY
														|| nextActivity.activityType() == ActivityType.EDUCATION_SECONDARY
														|| nextActivity.activityType() == ActivityType.EDUCATION_TERTIARY
														|| nextActivity.activityType() == ActivityType.EDUCATION_OCCUP
														|| nextActivity.activityType() == ActivityType.EDUCATION ? 1 : 0;

		double purpose_HOME 				 		= nextActivity.activityType().isHomeActivity() ? 1 : 0;
		double purpose_LEISURE_INDOOR 	= nextActivity.activityType() == ActivityType.LEISURE_INDOOR ? 1 : 0;
		double purpose_LEISURE_OUTDOOR 	= nextActivity.activityType() == ActivityType.LEISURE_OUTDOOR ? 1 : 0;
		double purpose_LEISURE_OTHER 		= nextActivity.activityType() == ActivityType.LEISURE_OTHER ? 1 : 0;
		double purpose_LEISURE_WALK 		= nextActivity.activityType() == ActivityType.LEISURE_WALK ? 1 : 0;
		double purpose_OTHER 			 			= nextActivity.activityType() == ActivityType.UNDEFINED ? 1 : 0;
		double purpose_SERVICE 		 			= nextActivity.activityType() == ActivityType.SERVICE ? 1 : 0;
		double purpose_SHOPPING_DAILY 	= nextActivity.activityType() == ActivityType.SHOPPING_DAILY ? 1 : 0;
		double purpose_SHOPPING_OTHER 	= nextActivity.activityType() == ActivityType.SHOPPING_OTHER ? 1 : 0;
		double purpose_PRIVATE_BUSINESS = nextActivity.activityType() == ActivityType.PRIVATE_BUSINESS ? 1 : 0;
		double purpose_PRIVATE_VISIT 		= nextActivity.activityType() == ActivityType.PRIVATE_VISIT ? 1 : 0;

		double age_0to9 						=                           person.age() <=  9 ? 1 : 0;
		double age_10to17 					= person.age() >= 10  && person.age() <= 17 ? 1 : 0;
		double age_18to25 					= person.age() >= 18  && person.age() <= 25 ? 1 : 0;
		double age_26to35 					= person.age() >= 26  && person.age() <= 35 ? 1 : 0;
		double age_51to60 					= person.age() >= 51  && person.age() <= 60 ? 1 : 0;
		double age_61to70 					= person.age() >= 61  && person.age() <= 70 ? 1 : 0;
		double age_70plus 					= person.age() >= 70                        ? 1 : 0;

	
		Map<String,Double> attributes = new LinkedHashMap<String,Double>();	

		attributes.put("CONST", 	1.0);
		attributes.put("INTRAZONAL", 	intrazonal);
		attributes.put("DIST_KM", new Double(distance));
		attributes.put("COMMUTING_TICKET", commuting_ticket);
		attributes.put("NO_DRIVING_LICENCE", no_driving_licence);
		attributes.put("FEMALE", female);
		attributes.put("DAY_SA",  day_SA);
		attributes.put("DAY_SU",  day_SU);

		attributes.put("EMPLOYMENT_EDUCATION",  	employment_EDUCATION);
		attributes.put("EMPLOYMENT_FULLTIME",  		employment_FULLTIME);
		attributes.put("EMPLOYMENT_INFANT",  			employment_INFANT);
		attributes.put("EMPLOYMENT_JOBLESS",  		employment_JOBLESS);
		attributes.put("EMPLOYMENT_NONE",  				employment_NONE);
		attributes.put("EMPLOYMENT_NOT_WORKING",  employment_NOT_WORKING);
		attributes.put("EMPLOYMENT_PARTTIME",  		employment_PARTTIME);
		attributes.put("EMPLOYMENT_RETIRED",  		employment_RETIRED);
		attributes.put("EMPLOYMENT_STUDENT",  		employment_STUDENT);

		attributes.put("AGE_0TO9",  	age_0to9);
		attributes.put("AGE_10TO17",  age_10to17);
		attributes.put("AGE_18TO25",  age_18to25);
		attributes.put("AGE_26TO35",  age_26to35);
		attributes.put("AGE_51TO60",  age_51to60);
		attributes.put("AGE_61TO70",  age_61to70);
		attributes.put("AGE_70PLUS",  age_70plus);

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

			double time_km 	= impedance.getTravelTime(source, target, mode, date)/distance;

			double cost_km 	= (mode==Mode.PUBLICTRANSPORT && person.hasCommuterTicket()) ? 0.0
												: impedance.getTravelCost(source, target, mode, date)/distance;


		 	attrib.put("TIME_KM", 		time_km);
			attrib.put("COST_KM", cost_km);

			modeAttributes.put(mode, attrib);
		}

		return  modeAttributes;
	}

	public Map<String,Double> parameterForMode(Mode mode) {

		switch(mode) {
			case PEDESTRIAN:
				return parameterWalk;
			case BIKE:
				return parameterBike;
			case CAR:
				return parameterCar;
			case PASSENGER:
				return parameterPassenger;
			case PUBLICTRANSPORT:
				return parameterPt;
			default:
				throw new AssertionError("invalid mode: " + mode);
		}
	}

}
