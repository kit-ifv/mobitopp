package edu.kit.ifv.mobitopp.simulation.modeChoice.stuttgart;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoiceParameter;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Employment;

import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Collections;

public class ModeSelectorParameterOtherTrip
	implements ModeChoiceParameter
{

	public final double carsharing_sb_const 	  	=  -1.5f;
	public final double carsharing_sb_shorttrip	=  -1.0f;
	public final double carsharing_sb_distance		=  0.15f;

	public final double carsharing_ff_const 	  	= -0.5f;
	public final double carsharing_ff_shorttrip	= -1.0f;
	public final double carsharing_ff_distance		= -0.05f;

	public final double car_const 	  =  0.0f;
	public final double foot_const 	= -0.9766f -4.0558f;
	public final double pt_const 		= -4.0281f -0.9280f;
	public final double pass_const 	= -3.4463f +0.7618f;
	public final double bike_const 	= -3.0257f -1.8043f;

	public final double time 				= -0.0134f;
	public final double cost_km			= -0.0298f;



	public final double car_last_car 	= 0.0f + 1.0f;
	public final double car_last_foot 	= 0.0f + 0.8f;
	public final double car_last_pt 		= 0.0f + 0.6f;
	public final double car_last_pass 	= 0.0f + 0.5f;
	public final double car_last_bike 	= 0.0f + 0.3f;

	public final double foot_last_car 	= 0.0f	+ 0.8f;
	public final double foot_last_foot = 3.3883f + 0.4f;
	public final double foot_last_pt 	= 2.9914f;
	public final double foot_last_pass = 2.6433f + 0.4f;
	public final double foot_last_bike = 2.0787f;

	public final double pt_last_car 	= 0.0f		- 0.1f;
	public final double pt_last_foot = 2.8677f - 0.8f;
	public final double pt_last_pt 	= 4.7725f + 0.2f;
	public final double pt_last_pass = 2.8936f - 0.8f;
	public final double pt_last_bike = 2.6457f;

	public final double pass_last_car 	= 0.0f	+ 0.3f;
	public final double pass_last_foot = 2.0989f + 0.3f;
	public final double pass_last_pt 	= 2.7006f;
	public final double pass_last_pass = 4.6064f + 0.3f;
	public final double pass_last_bike = 2.1010f;

	public final double bike_last_car 	= 0.0f		+ 1.8f;
	public final double bike_last_foot = 1.9099f;
	public final double bike_last_pt 	= 2.8007f + 0.0f;
	public final double bike_last_pass = 2.4327f;
	public final double bike_last_bike = 5.8491f;


	public final double foot_caravailable_true	= -0.2249f;
	public final double foot_caravailable_no		=  1.7847f;
	public final double foot_caravailable_part	=  0.0f;

	public final double pt_caravailable_true		= -0.6947f;
	public final double pt_caravailable_no			=  1.8398f;
	public final double pt_caravailable_part		=  0.0f;

	public final double pass_caravailable_true	= -0.4906f;
	public final double pass_caravailable_no		=  1.9301f;
	public final double pass_caravailable_part	=  0.0f;

	public final double bike_caravailable_true	= -0.6383f;
	public final double bike_caravailable_no		=  1.5430f;
	public final double bike_caravailable_part	=  0.0f;


	public final double foot_commticket_false		=  0.0f;
	public final double foot_commticket_true			=  0.1298f;

	public final double pt_commticket_false			=  0.0f;
	public final double pt_commticket_true				=  1.7425f;

	public final double pass_commticket_false		=  0.0f;
	public final double pass_commticket_true			=  0.1344f;

	public final double bike_commticket_false		=  0.0f;
	public final double bike_commticket_true			=  -0.0321f;




	public final double foot_activity_private_business 	=   0.0f + 1.7;
	public final double foot_activity_work								=   0.0352f + 0.8;
	public final double foot_activity_education					=   1.3783f + 1.25;
	public final double foot_activity_shopping						=  -0.0714f + 1.25;
	public final double foot_activity_leisure						=   0.5168f + 0.2f + 2.25;
	public final double foot_activity_home								=  -0.0107f + 3.8;
	public final double foot_activity_walk								=   8.6241f;
	public final double foot_activity_service						=  -0.7756f - 0.3f + 1.0;
	public final double foot_activity_business						=  -0.1838f - 1.0f + 1.0;

	public final double pt_activity_private_business 		=   0.0f;
	public final double pt_activity_work									=   0.7986f - 0.6;
	public final double pt_activity_education						=   2.1520f - 0.8;
	public final double pt_activity_shopping							=  -0.3780f - 0.7;
	public final double pt_activity_leisure							=   0.3233f - 0.25;
	public final double pt_activity_home									=   0.7535f - 0.2;
	public final double pt_activity_walk									=  -0.7115f;
	public final double pt_activity_service							=  -1.3699f - 0.3f - 0.75;
	public final double pt_activity_business							=  -0.2594f - 0.2f - 0.8;

	public final double pass_activity_private_business 	=   0.0f + 0.5;
	public final double pass_activity_work								=  -1.1023f - 0.25;
	public final double pass_activity_education					=  -0.6120f - 0.6;
	public final double pass_activity_shopping						=  -0.0713f + 0.25;
	public final double pass_activity_leisure						=   0.5793f + 0.5f;
	public final double pass_activity_home								=  -0.1960f - 0.9;
	public final double pass_activity_walk								=   0.3543f;
	public final double pass_activity_service						=  -0.9250f - 0.5f - 0.5;
	public final double pass_activity_business						=  -0.2975f - 0.6f;

	public final double bike_activity_private_business 	=   0.0f;
	public final double bike_activity_work								=   1.0286f - 0.5;
	public final double bike_activity_education					=   1.5253f;
	public final double bike_activity_shopping						=   0.2197f - 0.5;
	public final double bike_activity_leisure						=   0.2315f;
	public final double bike_activity_home								=   0.4395f + 0.2;
	public final double bike_activity_walk								=   6.0873f;
	public final double bike_activity_service						=  -0.9958f - 0.1f - 0.5;
	public final double bike_activity_business						=  -0.1757f - 0.5;


	public final double foot_weekday_workday 					=   0.0f;
	public final double foot_weekday_saturday 					=   0.0691f - 0.0f;
	public final double foot_weekday_sunday 						=   0.4860f - 0.2f;

	public final double pt_weekday_workday 						=   0.0f;
	public final double pt_weekday_saturday 						=  -0.1646f + 0.5f;
	public final double pt_weekday_sunday 							=  -0.4646f + 0.6f;

	public final double pass_weekday_workday 					=   0.0f;
	public final double pass_weekday_saturday 					=   0.4558f + 0.4f;
	public final double pass_weekday_sunday 						=   0.4862f + 0.0f;

	public final double bike_weekday_workday 					=   0.0f;
	public final double bike_weekday_saturday 					=   -0.0958f;
	public final double bike_weekday_sunday 						=   0.1273f;


	public final double foot_hhtype_other							=   0.0f;
	public final double foot_hhtype_2adults						=   0.2901f;
	public final double foot_hhtype_1adult							=   0.3682f;
	public final double foot_hhtype_kids								=   0.1567f;

	public final double pt_hhtype_other								=   0.0f;
	public final double pt_hhtype_2adults							=   0.2066f;
	public final double pt_hhtype_1adult								=   0.3091f;
	public final double pt_hhtype_kids									=  -0.0417f;

	public final double pass_hhtype_other							=   0.0f;
	public final double pass_hhtype_2adults						=   0.3170f;
	public final double pass_hhtype_1adult							=  -0.6129f;
	public final double pass_hhtype_kids								=  -0.0924f;

	public final double bike_hhtype_other							=   0.0f;
	public final double bike_hhtype_2adults						=   0.2090f;
	public final double bike_hhtype_1adult							=   0.2453f;
	public final double bike_hhtype_kids								=   0.3132f;


	public final double foot_employment_student_tertiary 	=   0.0f	- 1.0f;
	public final double foot_employment_education					=  -0.1756f	- 0.1f;
	public final double foot_employment_work_from41				=   0.2054f + 0.5f;
	public final double foot_employment_work_to40					=   0.3351f + 0.5f;
	public final double foot_employment_unemployed					=   0.4154f - 0.2f; // Househusband
	public final double foot_employment_jobless						=   0.4154f - 0.9f; // Jobless
	public final double foot_employment_retired						=   0.4349f	- 0.9f;
	public final double foot_employment_student_primary	 	=   5.0032f - 0.05f;
	public final double foot_employment_student_secondary	=   0.5341f - 0.5f;

	public final double pt_employment_student_tertiary 	=   0.0f	- 1.7f;
	public final double pt_employment_education					=  -0.2767f - 0.7f;
	public final double pt_employment_work_from41				=   0.2042f	- 0.3f;
	public final double pt_employment_work_to40					=   0.2192f	- 0.3f;
	public final double pt_employment_unemployed					=   0.3336f	- 0.1f;
	public final double pt_employment_jobless						=   0.3336f	+ 0.5f;
	public final double pt_employment_retired						=   0.3892f + 0.1f;
	public final double pt_employment_student_primary 		=   4.3631f	- 0.2f;
	public final double pt_employment_student_secondary 	=   0.5280f - 0.0f;

	public final double pass_employment_student_tertiary 	=   0.0f - 1.5f;
	public final double pass_employment_education					=  -0.0863f	- 1.1f;
	public final double pass_employment_work_from41				=  -0.2412f - 0.7f;
	public final double pass_employment_work_to40					=  -0.2125f - 0.7f;
	public final double pass_employment_unemployed					=   0.1484f - 0.45f;
	public final double pass_employment_jobless						=   0.1484f - 0.55f;
	public final double pass_employment_retired						=  -0.2066f - 0.45f;
	public final double pass_employment_student_primary 		=   5.3108f + 0.3f;
	public final double pass_employment_student_secondary	=   0.7441f - 0.6f;

	public final double bike_employment_student_tertiary 	=   0.0f - 0.5f;
	public final double bike_employment_education					=  -0.2863f;
	public final double bike_employment_work_from41				=   0.3280f - 0.2f;
	public final double bike_employment_work_to40					=   0.3523f - 0.2f;
	public final double bike_employment_unemployed					=   0.1905f - 0.4f;
	public final double bike_employment_jobless						=   0.1905f - 0.4f;
	public final double bike_employment_retired						=   0.1000f - 0.15f;
	public final double bike_employment_student_primary 		=   4.3664f + 0.0f;
	public final double bike_employment_student_secondary	=   0.7397f - 0.1f;


	public final double car_parkingpressure							=   0.0f;
	public final double foot_parkingpressure							=   0.1227f;
	public final double pt_parkingpressure								=   0.2117f;
	public final double pass_parkingpressure							=   0.0271f;
	public final double bike_parkingpressure							=   0.0205f;

	public final double car_distance											=   0.0f;
	public final double foot_distance										=  -0.2837f;
	public final double pt_distance											=   0.0165f 	+ 0.065f;
	public final double pass_distance										=   0.00565f 	+ 0.02f;
	public final double bike_distance										=  -0.1192f;

	public final double car_commutingdistance						=   0.0f;
	public final double foot_commutingdistance						=  -0.00305f;
	public final double pt_commutingdistance							=  -0.00978f;
	public final double pass_commutingdistance						=  -0.00574f;
	public final double bike_commutingdistance						=  -0.00474f;

	public final double car_shorttrip						=    0.0f;
	public final double foot_shorttrip						=    2.5558f - 0.5f;
	public final double pt_shorttrip							=    1.2280f - 3.2f;
	public final double pass_shorttrip						=   -0.1618f - 0.7f;
	public final double bike_shorttrip						=    1.7043f + 0.4f;

	private final Map<Mode,Map<String,Double>> parameters;

	public ModeSelectorParameterOtherTrip() {

		parameters = Collections.unmodifiableMap(createParameterMaps());
	}

	private Map<Mode,Map<String,Double>> createParameterMaps() {

		Map<String,Double> parameterWalk 			= new LinkedHashMap<>();
		Map<String,Double> parameterBike 			= new LinkedHashMap<>();
		Map<String,Double> parameterCar 			= new LinkedHashMap<>();
		Map<String,Double> parameterPassenger = new LinkedHashMap<>();
		Map<String,Double> parameterPt 				= new LinkedHashMap<>();
		Map<String,Double> parameterCarsharingStation 	= new LinkedHashMap<>();
		Map<String,Double> parameterCarsharingFree 			= new LinkedHashMap<>();


		parameterWalk.put("CONST", 		foot_const);
		parameterWalk.put("TIME", 		time);
		parameterWalk.put("COST_KM", 	cost_km);

		parameterWalk.put("PARKINGSTRESS",			foot_parkingpressure);
		parameterWalk.put("DISTANCE",						foot_distance);
		parameterWalk.put("COMMUTINGDISTANCE",	foot_commutingdistance);
		parameterWalk.put("SHORTTRIP",					foot_shorttrip);

		parameterWalk.put("CARAVAILABLE_YES", 					foot_caravailable_true);
		parameterWalk.put("CARAVAILABLE_NO", 						foot_caravailable_no);
		parameterWalk.put("CARAVAILABLE_PART", 					foot_caravailable_part);

		parameterWalk.put("COMMUTERTICKET_TRUE", 				foot_commticket_true);

		parameterWalk.put("ACTIVITY_PRIVATE_BUSINESS", 	foot_activity_private_business);
		parameterWalk.put("ACTIVITY_WORK", 							foot_activity_work);
		parameterWalk.put("ACTIVITY_EDUCATION", 				foot_activity_education);
		parameterWalk.put("ACTIVITY_SHOPPING", 					foot_activity_shopping);
		parameterWalk.put("ACTIVITY_LEISURE", 					foot_activity_leisure);
		parameterWalk.put("ACTIVITY_WALK", 							foot_activity_walk);
		parameterWalk.put("ACTIVITY_SERVICE", 					foot_activity_service);
		parameterWalk.put("ACTIVITY_BUSINESS", 					foot_activity_business);

		parameterWalk.put("WEEKDAY_WORKDAY", 						foot_weekday_workday);
		parameterWalk.put("WEEKDAY_SATURDAY", 					foot_weekday_saturday);
		parameterWalk.put("WEEKDAY_SUNDAY", 						foot_weekday_sunday);

		parameterWalk.put("HHTYPE_OTHER", 							foot_hhtype_other);
		parameterWalk.put("HHTYPE_2ADULTS", 						foot_hhtype_2adults);
		parameterWalk.put("HHTYPE_1ADULT", 							foot_hhtype_1adult);
		parameterWalk.put("HHTYPE_KIDS", 								foot_hhtype_kids);

		parameterWalk.put("EMPLOYMENT_STUDENT_TERIARY",		foot_employment_student_tertiary);
		parameterWalk.put("EMPLOYMENT_EDUCATION",					foot_employment_education);
		parameterWalk.put("EMPLOYMENT_WORK_FROM41",				foot_employment_work_from41);
		parameterWalk.put("EMPLOYMENT_WORK_TO40",					foot_employment_work_to40);
		parameterWalk.put("EMPLOYMENT_UNEMPLOYED",				foot_employment_unemployed);
		parameterWalk.put("EMPLOYMENT_JOBLESS",						foot_employment_jobless);
		parameterWalk.put("EMPLOYMENT_RETIRED",						foot_employment_retired);
		parameterWalk.put("EMPLOYMENT_STUDENT_PRIMARY",		foot_employment_student_primary);
		parameterWalk.put("EMPLOYMENT_STUDENT_SECONDARY",	foot_employment_student_secondary);

		parameterWalk.put("LAST_MODE_WALK",				foot_last_foot);
		parameterWalk.put("LAST_MODE_BIKE",				foot_last_bike);
		parameterWalk.put("LAST_MODE_CAR",				foot_last_car);
		parameterWalk.put("LAST_MODE_PASSENGER",	foot_last_pass);
		parameterWalk.put("LAST_MODE_PT",					foot_last_pt);

		// Bike
		parameterBike.put("CONST", 		bike_const);
		parameterBike.put("TIME", 		time);
		parameterBike.put("COST_KM", 	cost_km);

		parameterBike.put("PARKINGSTRESS",			bike_parkingpressure);
		parameterBike.put("DISTANCE",						bike_distance);
		parameterBike.put("COMMUTINGDISTANCE",	bike_commutingdistance);
		parameterBike.put("SHORTTRIP",					bike_shorttrip);

		parameterBike.put("CARAVAILABLE_YES", 	bike_caravailable_true);
		parameterBike.put("CARAVAILABLE_NO", 		bike_caravailable_no);
		parameterBike.put("CARAVAILABLE_PART", 	bike_caravailable_part);

		parameterBike.put("COMMUTERTICKET_TRUE", 	bike_commticket_true);

		parameterBike.put("ACTIVITY_PRIVATE_BUSINESS", 	bike_activity_private_business);
		parameterBike.put("ACTIVITY_WORK", 							bike_activity_work);
		parameterBike.put("ACTIVITY_EDUCATION", 				bike_activity_education);
		parameterBike.put("ACTIVITY_SHOPPING", 					bike_activity_shopping);
		parameterBike.put("ACTIVITY_LEISURE", 					bike_activity_leisure);
		parameterBike.put("ACTIVITY_WALK", 							bike_activity_walk);
		parameterBike.put("ACTIVITY_SERVICE", 					bike_activity_service);
		parameterBike.put("ACTIVITY_BUSINESS", 					bike_activity_business);

		parameterBike.put("WEEKDAY_WORKDAY", 					bike_weekday_workday);
		parameterBike.put("WEEKDAY_SATURDAY", 				bike_weekday_saturday);
		parameterBike.put("WEEKDAY_SUNDAY", 					bike_weekday_sunday);

		parameterBike.put("HHTYPE_OTHER", 					bike_hhtype_other);
		parameterBike.put("HHTYPE_2ADULTS", 				bike_hhtype_2adults);
		parameterBike.put("HHTYPE_1ADULT", 					bike_hhtype_1adult);
		parameterBike.put("HHTYPE_KIDS", 						bike_hhtype_kids);

		parameterBike.put("EMPLOYMENT_STUDENT_TERIARY",		bike_employment_student_tertiary);
		parameterBike.put("EMPLOYMENT_EDUCATION",					bike_employment_education);
		parameterBike.put("EMPLOYMENT_WORK_FROM41",				bike_employment_work_from41);
		parameterBike.put("EMPLOYMENT_WORK_TO40",					bike_employment_work_to40);
		parameterBike.put("EMPLOYMENT_UNEMPLOYED",				bike_employment_unemployed);
		parameterBike.put("EMPLOYMENT_JOBLESS",						bike_employment_jobless);
		parameterBike.put("EMPLOYMENT_RETIRED",						bike_employment_retired);
		parameterBike.put("EMPLOYMENT_STUDENT_PRIMARY",		bike_employment_student_primary);
		parameterBike.put("EMPLOYMENT_STUDENT_SECONDARY",	bike_employment_student_secondary);

		parameterBike.put("LAST_MODE_WALK",				bike_last_foot);
		parameterBike.put("LAST_MODE_BIKE",				bike_last_bike);
		parameterBike.put("LAST_MODE_CAR",				bike_last_car);
		parameterBike.put("LAST_MODE_PASSENGER",	bike_last_pass);
		parameterBike.put("LAST_MODE_PT",					bike_last_pt);

		// Passenger
		parameterPassenger.put("CONST", 		pass_const);
		parameterPassenger.put("TIME", 		time);
		parameterPassenger.put("COST_KM", 	cost_km);

		parameterPassenger.put("PARKINGSTRESS",			pass_parkingpressure);
		parameterPassenger.put("DISTANCE",						pass_distance);
		parameterPassenger.put("COMMUTINGDISTANCE",	pass_commutingdistance);
		parameterPassenger.put("SHORTTRIP",					pass_shorttrip);

		parameterPassenger.put("CARAVAILABLE_YES", 	pass_caravailable_true);
		parameterPassenger.put("CARAVAILABLE_NO", 		pass_caravailable_no);
		parameterPassenger.put("CARAVAILABLE_PART", 	pass_caravailable_part);

		parameterPassenger.put("COMMUTERTICKET_TRUE", 	pass_commticket_true);

		parameterPassenger.put("ACTIVITY_PRIVATE_BUSINESS", 	pass_activity_private_business);
		parameterPassenger.put("ACTIVITY_WORK", 							pass_activity_work);
		parameterPassenger.put("ACTIVITY_EDUCATION", 				pass_activity_education);
		parameterPassenger.put("ACTIVITY_SHOPPING", 					pass_activity_shopping);
		parameterPassenger.put("ACTIVITY_LEISURE", 					pass_activity_leisure);
		parameterPassenger.put("ACTIVITY_WALK", 							pass_activity_walk);
		parameterPassenger.put("ACTIVITY_SERVICE", 					pass_activity_service);
		parameterPassenger.put("ACTIVITY_BUSINESS", 					pass_activity_business);

		parameterPassenger.put("WEEKDAY_WORKDAY", 					pass_weekday_workday);
		parameterPassenger.put("WEEKDAY_SATURDAY", 				pass_weekday_saturday);
		parameterPassenger.put("WEEKDAY_SUNDAY", 					pass_weekday_sunday);

		parameterPassenger.put("HHTYPE_OTHER", 					pass_hhtype_other);
		parameterPassenger.put("HHTYPE_2ADULTS", 				pass_hhtype_2adults);
		parameterPassenger.put("HHTYPE_1ADULT", 					pass_hhtype_1adult);
		parameterPassenger.put("HHTYPE_KIDS", 						pass_hhtype_kids);

		parameterPassenger.put("EMPLOYMENT_STUDENT_TERIARY",		pass_employment_student_tertiary);
		parameterPassenger.put("EMPLOYMENT_EDUCATION",					pass_employment_education);
		parameterPassenger.put("EMPLOYMENT_WORK_FROM41",				pass_employment_work_from41);
		parameterPassenger.put("EMPLOYMENT_WORK_TO40",					pass_employment_work_to40);
		parameterPassenger.put("EMPLOYMENT_UNEMPLOYED",					pass_employment_unemployed);
		parameterPassenger.put("EMPLOYMENT_JOBLESS",						pass_employment_jobless);
		parameterPassenger.put("EMPLOYMENT_RETIRED",						pass_employment_retired);
		parameterPassenger.put("EMPLOYMENT_STUDENT_PRIMARY",		pass_employment_student_primary);
		parameterPassenger.put("EMPLOYMENT_STUDENT_SECONDARY",	pass_employment_student_secondary);

		parameterPassenger.put("LAST_MODE_WALK",				pass_last_foot);
		parameterPassenger.put("LAST_MODE_BIKE",				pass_last_bike);
		parameterPassenger.put("LAST_MODE_CAR",					pass_last_car);
		parameterPassenger.put("LAST_MODE_PASSENGER",		pass_last_pass);
		parameterPassenger.put("LAST_MODE_PT",					pass_last_pt);

		// Public Transport
		parameterPt.put("CONST", 		pt_const);
		parameterPt.put("TIME", 		time);
		parameterPt.put("COST_KM", 	cost_km);

		parameterPt.put("PARKINGSTRESS",			pt_parkingpressure);
		parameterPt.put("DISTANCE",						pt_distance);
		parameterPt.put("COMMUTINGDISTANCE",	pt_commutingdistance);
		parameterPt.put("SHORTTRIP",					pt_shorttrip);

		parameterPt.put("CARAVAILABLE_YES", 	pt_caravailable_true);
		parameterPt.put("CARAVAILABLE_NO", 		pt_caravailable_no);
		parameterPt.put("CARAVAILABLE_PART", 	pt_caravailable_part);

		parameterPt.put("COMMUTERTICKET_TRUE", 	pt_commticket_true);

		parameterPt.put("ACTIVITY_PRIVATE_BUSINESS", 	pt_activity_private_business);
		parameterPt.put("ACTIVITY_WORK", 							pt_activity_work);
		parameterPt.put("ACTIVITY_EDUCATION", 				pt_activity_education);
		parameterPt.put("ACTIVITY_SHOPPING", 					pt_activity_shopping);
		parameterPt.put("ACTIVITY_LEISURE", 					pt_activity_leisure);
		parameterPt.put("ACTIVITY_WALK", 							pt_activity_walk);
		parameterPt.put("ACTIVITY_SERVICE", 					pt_activity_service);
		parameterPt.put("ACTIVITY_BUSINESS", 					pt_activity_business);

		parameterPt.put("WEEKDAY_WORKDAY", 					pt_weekday_workday);
		parameterPt.put("WEEKDAY_SATURDAY", 				pt_weekday_saturday);
		parameterPt.put("WEEKDAY_SUNDAY", 					pt_weekday_sunday);

		parameterPt.put("HHTYPE_OTHER", 					pt_hhtype_other);
		parameterPt.put("HHTYPE_2ADULTS", 				pt_hhtype_2adults);
		parameterPt.put("HHTYPE_1ADULT", 					pt_hhtype_1adult);
		parameterPt.put("HHTYPE_KIDS", 						pt_hhtype_kids);

		parameterPt.put("EMPLOYMENT_STUDENT_TERIARY",		pt_employment_student_tertiary);
		parameterPt.put("EMPLOYMENT_EDUCATION",					pt_employment_education);
		parameterPt.put("EMPLOYMENT_WORK_FROM41",				pt_employment_work_from41);
		parameterPt.put("EMPLOYMENT_WORK_TO40",					pt_employment_work_to40);
		parameterPt.put("EMPLOYMENT_UNEMPLOYED",				pt_employment_unemployed);
		parameterPt.put("EMPLOYMENT_JOBLESS",						pt_employment_jobless);
		parameterPt.put("EMPLOYMENT_RETIRED",						pt_employment_retired);
		parameterPt.put("EMPLOYMENT_STUDENT_PRIMARY",		pt_employment_student_primary);
		parameterPt.put("EMPLOYMENT_STUDENT_SECONDARY",	pt_employment_student_secondary);

		parameterPt.put("LAST_MODE_WALK",				pt_last_foot);
		parameterPt.put("LAST_MODE_BIKE",				pt_last_bike);
		parameterPt.put("LAST_MODE_CAR",				pt_last_car);
		parameterPt.put("LAST_MODE_PASSENGER",	pt_last_pass);
		parameterPt.put("LAST_MODE_PT",					pt_last_pt);

		// Car
		parameterCar.put("TIME", 		time);
		parameterCar.put("COST_KM", 	cost_km);

		parameterCar.put("CONST", 						car_const);
		parameterCar.put("PARKINGSTRESS",			car_parkingpressure);
		parameterCar.put("DISTANCE",					car_distance);
		parameterCar.put("COMMUTINGDISTANCE",	car_commutingdistance);
		parameterCar.put("SHORTTRIP",					car_shorttrip);

		parameterCar.put("LAST_MODE_WALK",				car_last_foot);
		parameterCar.put("LAST_MODE_BIKE",				car_last_bike);
		parameterCar.put("LAST_MODE_CAR",					car_last_car);
		parameterCar.put("LAST_MODE_PASSENGER",		car_last_pass);
		parameterCar.put("LAST_MODE_PT",					car_last_pt);

		// Carsharing station-based
		parameterCarsharingStation.put("TIME", 		time);
		parameterCarsharingStation.put("COST_KM", cost_km);

		parameterCarsharingStation.put("CONST", 						carsharing_sb_const);
		parameterCarsharingStation.put("DISTANCE",					carsharing_sb_distance);
		parameterCarsharingStation.put("SHORTTRIP",					carsharing_sb_shorttrip);

		parameterCarsharingStation.put("PARKINGSTRESS",			car_parkingpressure);
		parameterCarsharingStation.put("COMMUTINGDISTANCE",	car_commutingdistance);

		parameterCarsharingStation.put("LAST_MODE_WALK",				car_last_foot);
		parameterCarsharingStation.put("LAST_MODE_BIKE",				car_last_bike);
		parameterCarsharingStation.put("LAST_MODE_CAR",					car_last_car);
		parameterCarsharingStation.put("LAST_MODE_PASSENGER",		car_last_pass);
		parameterCarsharingStation.put("LAST_MODE_PT",					car_last_pt);

		// Carsharing free-floating
		parameterCarsharingFree.put("TIME", 		time);
		parameterCarsharingFree.put("COST_KM", 	cost_km);

		parameterCarsharingFree.put("CONST", 							carsharing_ff_const);
		parameterCarsharingFree.put("SHORTTRIP",					carsharing_ff_shorttrip);
		parameterCarsharingFree.put("DISTANCE",						carsharing_ff_distance);

		parameterCarsharingFree.put("PARKINGSTRESS",			car_parkingpressure);
		parameterCarsharingFree.put("COMMUTINGDISTANCE",	car_commutingdistance);

		parameterCarsharingFree.put("LAST_MODE_WALK",				car_last_foot);
		parameterCarsharingFree.put("LAST_MODE_BIKE",				car_last_bike);
		parameterCarsharingFree.put("LAST_MODE_CAR",				car_last_car);
		parameterCarsharingFree.put("LAST_MODE_PASSENGER",	car_last_pass);
		parameterCarsharingFree.put("LAST_MODE_PT",					car_last_pt);


		Map<Mode,Map<String,Double>> parameterForMode = new LinkedHashMap<>();

		parameterForMode.put(Mode.PEDESTRIAN, 				Collections.unmodifiableMap(parameterWalk));
		parameterForMode.put(Mode.BIKE, 							Collections.unmodifiableMap(parameterBike));
		parameterForMode.put(Mode.CAR, 								Collections.unmodifiableMap(parameterCar));
		parameterForMode.put(Mode.PASSENGER,		 			Collections.unmodifiableMap(parameterPassenger));
		parameterForMode.put(Mode.PUBLICTRANSPORT, 		Collections.unmodifiableMap(parameterPt));
		parameterForMode.put(Mode.CARSHARING_STATION, Collections.unmodifiableMap(parameterCarsharingStation));
		parameterForMode.put(Mode.CARSHARING_FREE, 		Collections.unmodifiableMap(parameterCarsharingFree));

		return parameterForMode;
	}



	@Override
	public Map<Mode,Map<String,Double>> gatherAttributes(
		Person person,
		Set<Mode> modes,
		Zone sourceZone,
		Zone targetZone,
		ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		ImpedanceIfc impedance
	) {

		Time date = nextActivity.startDate();
		Household hh = person.household();

		int homeZone = hh.homeZone().getOid();

		int poleZone = person.fixedActivityZone().getOid();

		int source = sourceZone.getOid();
		int target = targetZone.getOid();

		double caravailable_part = !person.hasPersonalCar() && person.hasAccessToCar() ? 1 : 0;
		double caravailable_true = person.hasPersonalCar() ? 1 : 0;  // personal car
		double caravailable_no 	= !person.hasAccessToCar() ? 1 : 0;   // car available

		double commticket_false			= person.hasCommuterTicket() ? 0 : 1;
		double commticket_true			= person.hasCommuterTicket() ? 1 : 0;

		double activity_private_business 	= nextActivity.activityType() == ActivityType.PRIVATE_BUSINESS ? 1 : 0;
		double activity_work 			= nextActivity.activityType().isWorkActivity() ? 1 : 0;
		double activity_education	= nextActivity.activityType() == ActivityType.EDUCATION ? 1 : 0;
		double activity_shopping 	= nextActivity.activityType().isShoppingActivity()
															&& nextActivity.activityType() != ActivityType.PRIVATE_BUSINESS ? 1 : 0;
		double activity_leisure 	= nextActivity.activityType().isLeisureActivity()
															&& nextActivity.activityType() != ActivityType.LEISURE_WALK ? 1 : 0;
		double activity_walk 			= nextActivity.activityType() == ActivityType.LEISURE_WALK ? 1 : 0;
		double activity_service 	= nextActivity.activityType() == ActivityType.SERVICE ? 1 : 0;
		double activity_business 	= nextActivity.activityType().isBusinessActivity() ? 1 : 0;
		double activity_home 			= nextActivity.activityType().isHomeActivity() ? 1 : 0;

		double weekday_workday 		= date.weekDay() != DayOfWeek.SATURDAY
																	&& date.weekDay() != DayOfWeek.SUNDAY ? 1 : 0;
		double weekday_saturday 		= date.weekDay() == DayOfWeek.SATURDAY ? 1 : 0;
		double weekday_sunday 			= date.weekDay() == DayOfWeek.SUNDAY ? 1 : 0;;

		double hhtype_other 			= hh.getSize() >= 3 && hh.numberOfNotSimulatedChildren() == 0 ? 1 : 0;
		double hhtype_1adult 			= hh.getSize() == 1 ? 1 : 0;
		double hhtype_2adults 			= hh.getSize() == 2 && hh.numberOfNotSimulatedChildren() == 0 ? 1 : 0;
		double hhtype_kids 				= hh.numberOfNotSimulatedChildren() > 0 ? 1 : 0;

		double employment_student_tertiary = person.employment() == Employment.STUDENT_TERTIARY ? 1 : 0;
		double employment_education 					= person.employment() == Employment.EDUCATION  ? 1 : 0;
		double employment_work_from41 				= (person.employment() == Employment.FULLTIME
																					|| person.employment() == Employment.PARTTIME)
																				&& person.age() > 40 ? 1 : 0;
		double employment_work_to40 					= (person.employment() == Employment.FULLTIME
																					|| person.employment() == Employment.PARTTIME)
																				&& person.age() <= 40 ? 1 : 0;
		double employment_unemployed 				= person.employment() == Employment.HOMEKEEPER ? 1 : 0;
		double employment_jobless		 				= person.employment() == Employment.UNEMPLOYED ? 1 : 0;
		double employment_retired 						= person.employment() == Employment.RETIRED ? 1 : 0;
		double employment_student_primary 		= person.employment() == Employment.STUDENT_PRIMARY ? 1 : 0;
		double employment_student_secondary 	= person.employment() == Employment.STUDENT_SECONDARY ? 1 : 0;



		double parkingpressure 	= impedance.getParkingStress(target, date);
		double distance 					= (float) Math.max(0.1,
																						impedance.getDistance(source, target)/1000.0);
		double commutingdistance = impedance.getDistance(homeZone, poleZone)/1000.0f;

		double shorttrip = distance <= 1.5 ? 1.0f : 0.0f;

		double last_mode_car 	= previousActivity.mode() == Mode.CAR
														|| previousActivity.mode() == Mode.CARSHARING_STATION
														|| previousActivity.mode() == Mode.CARSHARING_FREE
																																					? 1.0f : 0.0f;
		double last_mode_foot 	= previousActivity.mode() == Mode.PEDESTRIAN ? 1.0f : 0.0f;
		double last_mode_pt 		= previousActivity.mode() == Mode.PUBLICTRANSPORT ? 1.0f : 0.0f;
		double last_mode_pass 	= previousActivity.mode() == Mode.PASSENGER ? 1.0f : 0.0f;
		double last_mode_bike 	= previousActivity.mode() == Mode.BIKE ? 1.0f : 0.0f;


		Map<String,Double> attributes = new LinkedHashMap<>();

		attributes.put("CONST", 	1.0);

		attributes.put("PARKINGSTRESS",			parkingpressure);
		attributes.put("DISTANCE",					distance);
		attributes.put("COMMUTINGDISTANCE",	commutingdistance);
		attributes.put("SHORTTRIP",					shorttrip);

		attributes.put("CARAVAILABLE_PART", 				caravailable_part);
		attributes.put("CARAVAILABLE_YES", 					caravailable_true);
		attributes.put("CARAVAILABLE_NO", 					caravailable_no);

		attributes.put("COMMUTERTICKET_FALSE", 			commticket_false);
		attributes.put("COMMUTERTICKET_TRUE", 			commticket_true);

		attributes.put("ACTIVITY_PRIVATE_BUSINESS", 	activity_private_business);
		attributes.put("ACTIVITY_WORK", 							activity_work);
		attributes.put("ACTIVITY_EDUCATION", 					activity_education);
		attributes.put("ACTIVITY_SHOPPING", 					activity_shopping);
		attributes.put("ACTIVITY_LEISURE", 						activity_leisure);
		attributes.put("ACTIVITY_WALK", 							activity_walk);
		attributes.put("ACTIVITY_SERVICE", 						activity_service);
		attributes.put("ACTIVITY_BUSINESS", 					activity_business);
		attributes.put("ACTIVITY_HOME", 							activity_home);

		attributes.put("WEEKDAY_WORKDAY", 						weekday_workday);
		attributes.put("WEEKDAY_SATURDAY", 						weekday_saturday);
		attributes.put("WEEKDAY_SUNDAY", 							weekday_sunday);

		attributes.put("HHTYPE_OTHER", 								hhtype_other);
		attributes.put("HHTYPE_2ADULTS", 							hhtype_2adults);
		attributes.put("HHTYPE_1ADULT", 							hhtype_1adult);
		attributes.put("HHTYPE_KIDS", 								hhtype_kids);

		attributes.put("EMPLOYMENT_STUDENT_TERIARY",		employment_student_tertiary);
		attributes.put("EMPLOYMENT_EDUCATION",					employment_education);
		attributes.put("EMPLOYMENT_WORK_FROM41",				employment_work_from41);
		attributes.put("EMPLOYMENT_WORK_TO40",					employment_work_to40);
		attributes.put("EMPLOYMENT_UNEMPLOYED",					employment_unemployed);
		attributes.put("EMPLOYMENT_JOBLESS",						employment_jobless);
		attributes.put("EMPLOYMENT_RETIRED",						employment_retired);
		attributes.put("EMPLOYMENT_STUDENT_PRIMARY",		employment_student_primary);
		attributes.put("EMPLOYMENT_STUDENT_SECONDARY",	employment_student_secondary);

		attributes.put("LAST_MODE_WALK",	last_mode_foot);
		attributes.put("LAST_MODE_BIKE",	last_mode_bike);
		attributes.put("LAST_MODE_CAR",		last_mode_car);
		attributes.put("LAST_MODE_PASSENGER",	last_mode_pass);
		attributes.put("LAST_MODE_PT",	last_mode_pt);

		Map<Mode,Map<String,Double>> modeAttributes = new LinkedHashMap<>();

		for (Mode mode : modes) {

			Map<String,Double> attrib = new LinkedHashMap<>(attributes);

			double time 	= impedance.getTravelTime(source, target, mode, date);

			double cost_km 	= (mode==Mode.PUBLICTRANSPORT && person.hasCommuterTicket()) ? 0.0
												: impedance.getTravelCost(source, target, mode, date)/distance;

		 	attrib.put("TIME", 		time);
			attrib.put("COST_KM", cost_km);

			modeAttributes.put(mode, attrib);
		}

		return  modeAttributes;
	}

	@Override
	public Map<String,Double> parameterForMode(Mode mode) {

		assert parameters.containsKey(mode);

		return parameters.get(mode);
	}
}
