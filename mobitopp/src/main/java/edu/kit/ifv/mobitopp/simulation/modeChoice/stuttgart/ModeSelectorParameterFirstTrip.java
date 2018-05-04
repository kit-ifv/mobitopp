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

public class ModeSelectorParameterFirstTrip 
	implements ModeChoiceParameter
{

	public final double carsharing_sb_const 			=  -1.5f;
	public final double carsharing_sb_shorttrip	=  -1.0f;
	public final double carsharing_sb_distance		=  0.15f;		

	public final double carsharing_ff_const 			= -0.5f;
	public final double carsharing_ff_shorttrip	= -1.0f;
	public final double carsharing_ff_distance		= -0.05f;		

	public final double car_const 	=  0.0f;
	public final double foot_const 	=  0.7282f -2.5355f	+ 0.1f;
	public final double pt_const 		= -2.6699f -0.8402f	+ 0.9f - 0.6f;
	public final double pass_const 	= -1.7962f +0.0979f + 0.7f;
	public final double bike_const 	= -0.2889f -1.2546f + 1.0f + 0.1f;

	public final double time 				= -0.0102f;
	public final double cost_km			= -0.0282f;


	public final double foot_caravailable_true	= -0.9468f;
	public final double foot_caravailable_no		=  2.3661f;
	public final double foot_caravailable_part	=  0.0f;

	public final double pt_caravailable_true		= -1.6001f;
	public final double pt_caravailable_no			=  2.4664f;
	public final double pt_caravailable_part		=  0.0f;

	public final double pass_caravailable_true	= -1.1910f;
	public final double pass_caravailable_no		=  2.5440f;
	public final double pass_caravailable_part	=  0.0f;

	public final double bike_caravailable_true	= -1.5245f;
	public final double bike_caravailable_no		=  2.0363f;
	public final double bike_caravailable_part	=  0.0f;


	public final double foot_commticket_false		=  0.0f;
	public final double foot_commticket_true		=  0.8621f;

	public final double pt_commticket_false			=  0.0f;
	public final double pt_commticket_true			=  3.5163f;

	public final double pass_commticket_false		=  0.0f;
	public final double pass_commticket_true		=  0.7554f;

	public final double bike_commticket_false		=  0.0f;
	public final double bike_commticket_true		=  0.3378f;

	// First trip is (per definition) never heads to home loaction
	public final double foot_activity_private_business 	=   0.0f;
	public final double foot_activity_work								=  -0.2524f;
	public final double foot_activity_education					=   1.3114f;
	public final double foot_activity_shopping						=  -0.0383f;
	public final double foot_activity_leisure						=   0.1730f;
	public final double foot_activity_walk								=   8.2890f;
	public final double foot_activity_service						=  -0.7034f;
	public final double foot_activity_business						=  -0.7886f;

	public final double pt_activity_private_business 		=   0.0f;
	public final double pt_activity_work									=   0.5140f;
	public final double pt_activity_education						=   1.5488f;
	public final double pt_activity_shopping							=  -0.3953f;
	public final double pt_activity_leisure							=   0.2116f;
	public final double pt_activity_walk									=  -1.6447f;
	public final double pt_activity_service							=  -1.9713f;
	public final double pt_activity_business							=  -0.0501f;

	public final double pass_activity_private_business 	=   0.0f;
	public final double pass_activity_work								=  -1.2274f;
	public final double pass_activity_education					=  -0.6317f;
	public final double pass_activity_shopping						=  -0.1370f;
	public final double pass_activity_leisure						=   0.2757f;
	public final double pass_activity_walk								=   0.1835f;
	public final double pass_activity_service						=  -1.3161f;
	public final double pass_activity_business						=  -1.2343f;

	public final double bike_activity_private_business 	=   0.0f;
	public final double bike_activity_work								=   0.8274f;
	public final double bike_activity_education					=   1.0798f;
	public final double bike_activity_shopping						=   0.2980f;
	public final double bike_activity_leisure						=   0.5172f;
	public final double bike_activity_walk								=   5.0464f;
	public final double bike_activity_service						=  -1.1627f;
	public final double bike_activity_business						=   0.4171f;


	public final double foot_weekday_workday 					=   0.0f;
	public final double foot_weekday_saturday 					=   0.6280f;
	public final double foot_weekday_sunday 						=   3.6852f;

	public final double pt_weekday_workday 						=   0.0f;
	public final double pt_weekday_saturday 						=  -0.0473f;
	public final double pt_weekday_sunday 							=  -0.0746f;

	public final double pass_weekday_workday 					=   0.0f;
	public final double pass_weekday_saturday 					=   0.1617f;
	public final double pass_weekday_sunday 						=   2.1195f;

	public final double bike_weekday_workday 					=   0.0f;
	public final double bike_weekday_saturday 					=   -2.1148f;
	public final double bike_weekday_sunday 						=   -0.4720f;


	public final double foot_hhtype_other							=   0.0f;
	public final double foot_hhtype_2adults						=   0.7236f;
	public final double foot_hhtype_1adult							=   0.9611f;
	public final double foot_hhtype_kids								=   0.3984f;	

	public final double pt_hhtype_other								=   0.0f;
	public final double pt_hhtype_2adults							=   0.6425f;
	public final double pt_hhtype_1adult								=   0.7769f;
	public final double pt_hhtype_kids									=   0.2456f;	

	public final double pass_hhtype_other							=   0.0f;
	public final double pass_hhtype_2adults						=   0.3058f;
	public final double pass_hhtype_1adult							=  -1.3062f;
	public final double pass_hhtype_kids								=  -0.2996f;	

	public final double bike_hhtype_other							=   0.0f;
	public final double bike_hhtype_2adults						=   0.1100f;
	public final double bike_hhtype_1adult							=   0.2721f;
	public final double bike_hhtype_kids								=   0.7919f;	


	public final double foot_employment_student_tertiary 	=   0.0f;
	public final double foot_employment_education					=   0.1316f;
	public final double foot_employment_work_from41				=   0.7693f;
	public final double foot_employment_work_to40					=   0.6087f;
	public final double foot_employment_notemployed				=   1.0620f;
	public final double foot_employment_retired						=   0.7579f;
	public final double foot_employment_student_primary	 	=   3.7866f;
	public final double foot_employment_student_secondary	=   0.5328f;

	public final double pt_employment_student_tertiary 	=   0.0f;
	public final double pt_employment_education					=   -0.3362f;
	public final double pt_employment_work_from41				=   0.2859f;
	public final double pt_employment_work_to40					=   0.3982f;
	public final double pt_employment_notemployed				=   0.9388f;
	public final double pt_employment_retired						=   0.8726f;
	public final double pt_employment_student_primary 		=   2.5690f;
	public final double pt_employment_student_secondary 	=   0.3837f;

	public final double pass_employment_student_tertiary 	=   0.0f;
	public final double pass_employment_education					=   0.6165f;
	public final double pass_employment_work_from41				=   0.000860f;
	public final double pass_employment_work_to40					=   0.2398f;
	public final double pass_employment_notemployed				=   0.3636f;
	public final double pass_employment_retired						=   0.5508f;
	public final double pass_employment_student_primary 		=   4.5114f;
	public final double pass_employment_student_secondary	=   1.5469f;

	public final double bike_employment_student_tertiary 	=   0.0f;
	public final double bike_employment_education					=  -0.7324f;
	public final double bike_employment_work_from41				=  -0.1068f;
	public final double bike_employment_work_to40					=  -0.1765f;
	public final double bike_employment_notemployed				=  -0.2762f;
	public final double bike_employment_retired						=  -0.4995f;
	public final double bike_employment_student_primary 		=   0.9420f;
	public final double bike_employment_student_secondary	=   0.3377f;

	public final double car_parkingpressure							=   0.0f;
	public final double foot_parkingpressure							=   0.1123f;
	public final double pt_parkingpressure								=   0.2198f;
	public final double pass_parkingpressure							=   0.0487f;
	public final double bike_parkingpressure							=   0.0462f;

	public final double car_distance											=   0.0f;		
	public final double foot_distance										=  -0.3729f;		
	public final double pt_distance											=   0.0189f + 0.03f;		
	public final double pass_distance										=   0.0149f;		
	public final double bike_distance										=  -0.1719f - 0.1f;		

	public final double car_commutingdistance						=   0.0f;		
	public final double foot_commutingdistance						=  -0.0114f;
	public final double pt_commutingdistance							=  -0.0139f;
	public final double pass_commutingdistance						=  -0.0129f;
	public final double bike_commutingdistance						=  -0.00867f;

	public final double car_shorttrip							=  	0.0f;		
	public final double foot_shorttrip						=   2.5355f	-1.9f;
	public final double pt_shorttrip							=   0.8402f -4.15f + 0.1f;
	public final double pass_shorttrip						=  -0.0979f	-0.85f;
	public final double bike_shorttrip						=   1.2546f	-1.35f;


	private final Map<Mode,Map<String,Double>> parameters;

	public ModeSelectorParameterFirstTrip() {

		this.parameters = Collections.unmodifiableMap(createParameterMaps());
	}

	private Map<Mode,Map<String,Double>> createParameterMaps() {

		Map<String,Double> parameterWalk 			= new LinkedHashMap<String,Double>();
		Map<String,Double> parameterBike 			= new LinkedHashMap<String,Double>();
		Map<String,Double> parameterCar 			= new LinkedHashMap<String,Double>();
		Map<String,Double> parameterPassenger = new LinkedHashMap<String,Double>();
		Map<String,Double> parameterPt 				= new LinkedHashMap<String,Double>();
		Map<String,Double> parameterCarsharingStation 	= new LinkedHashMap<String,Double>();
		Map<String,Double> parameterCarsharingFree 			= new LinkedHashMap<String,Double>();


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

		parameterWalk.put("COMMUTERTICKET_FALSE", 			foot_commticket_false);
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
		parameterWalk.put("EMPLOYMENT_NOTEMPLOYED",				foot_employment_notemployed);
		parameterWalk.put("EMPLOYMENT_RETIRED",						foot_employment_retired);
		parameterWalk.put("EMPLOYMENT_STUDENT_PRIMARY",		foot_employment_student_primary);
		parameterWalk.put("EMPLOYMENT_STUDENT_SECONDARY",	foot_employment_student_secondary);

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

		parameterBike.put("COMMUTERTICKET_FALSE", bike_commticket_false);
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
		parameterBike.put("EMPLOYMENT_NOTEMPLOYED",				bike_employment_notemployed);
		parameterBike.put("EMPLOYMENT_RETIRED",						bike_employment_retired);
		parameterBike.put("EMPLOYMENT_STUDENT_PRIMARY",		bike_employment_student_primary);
		parameterBike.put("EMPLOYMENT_STUDENT_SECONDARY",	bike_employment_student_secondary);

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

		parameterPassenger.put("COMMUTERTICKET_FALSE", pass_commticket_false);
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
		parameterPassenger.put("EMPLOYMENT_NOTEMPLOYED",				pass_employment_notemployed);
		parameterPassenger.put("EMPLOYMENT_RETIRED",						pass_employment_retired);
		parameterPassenger.put("EMPLOYMENT_STUDENT_PRIMARY",		pass_employment_student_primary);
		parameterPassenger.put("EMPLOYMENT_STUDENT_SECONDARY",	pass_employment_student_secondary);

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

		parameterPt.put("COMMUTERTICKET_FALSE", pt_commticket_false);
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
		parameterPt.put("EMPLOYMENT_NOTEMPLOYED",				pt_employment_notemployed);
		parameterPt.put("EMPLOYMENT_RETIRED",						pt_employment_retired);
		parameterPt.put("EMPLOYMENT_STUDENT_PRIMARY",		pt_employment_student_primary);
		parameterPt.put("EMPLOYMENT_STUDENT_SECONDARY",	pt_employment_student_secondary);

		// Car
		parameterCar.put("TIME", 		time);
		parameterCar.put("COST_KM", 	cost_km);

		parameterCar.put("CONST", 						car_const);
		parameterCar.put("PARKINGSTRESS",			car_parkingpressure);
		parameterCar.put("DISTANCE",					car_distance);
		parameterCar.put("COMMUTINGDISTANCE",	car_commutingdistance);
		parameterCar.put("SHORTTRIP",					car_shorttrip);

		// Carsharing station-based
		parameterCarsharingStation.put("TIME", 		time);
		parameterCarsharingStation.put("COST_KM", cost_km);

		parameterCarsharingStation.put("CONST", 						carsharing_sb_const);
		parameterCarsharingStation.put("DISTANCE",					carsharing_sb_distance);
		parameterCarsharingStation.put("SHORTTRIP",					carsharing_sb_shorttrip);

		parameterCarsharingStation.put("PARKINGSTRESS",			car_parkingpressure);
		parameterCarsharingStation.put("COMMUTINGDISTANCE",	car_commutingdistance);

		// Carsharing free-floating
		parameterCarsharingFree.put("TIME", 		time);
		parameterCarsharingFree.put("COST_KM", 	cost_km);

		parameterCarsharingFree.put("CONST", 							carsharing_ff_const);
		parameterCarsharingFree.put("SHORTTRIP",					carsharing_ff_shorttrip);
		parameterCarsharingFree.put("DISTANCE",						carsharing_ff_distance);

		parameterCarsharingFree.put("PARKINGSTRESS",			car_parkingpressure);
		parameterCarsharingFree.put("COMMUTINGDISTANCE",	car_commutingdistance);


		Map<Mode,Map<String,Double>> parameterForMode = new LinkedHashMap<Mode,Map<String,Double>>();

		parameterForMode.put(Mode.PEDESTRIAN, 				Collections.unmodifiableMap(parameterWalk));
		parameterForMode.put(Mode.BIKE, 							Collections.unmodifiableMap(parameterBike));
		parameterForMode.put(Mode.CAR, 								Collections.unmodifiableMap(parameterCar));
		parameterForMode.put(Mode.PASSENGER,		 			Collections.unmodifiableMap(parameterPassenger));
		parameterForMode.put(Mode.PUBLICTRANSPORT, 		Collections.unmodifiableMap(parameterPt));
		parameterForMode.put(Mode.CARSHARING_STATION, Collections.unmodifiableMap(parameterCarsharingStation));
		parameterForMode.put(Mode.CARSHARING_FREE, 		Collections.unmodifiableMap(parameterCarsharingFree));

		return parameterForMode;
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
		double activity_education 	= nextActivity.activityType() == ActivityType.EDUCATION ? 1 : 0;
		double activity_shopping 	= nextActivity.activityType().isShoppingActivity() 
															&& nextActivity.activityType() != ActivityType.PRIVATE_BUSINESS ? 1 : 0;
		double activity_leisure 		= nextActivity.activityType().isLeisureActivity() 
															&& nextActivity.activityType() != ActivityType.LEISURE_WALK ? 1 : 0;
		double activity_walk 			= nextActivity.activityType() == ActivityType.LEISURE_WALK ? 1 : 0;
		double activity_service 		= nextActivity.activityType() == ActivityType.SERVICE ? 1 : 0;
		double activity_business 	= nextActivity.activityType().isBusinessActivity() ? 1 : 0;

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
		double employment_notemployed 				= person.employment() == Employment.HOMEKEEPER
																				|| person.employment() == Employment.UNEMPLOYED ? 1 : 0;
		double employment_retired 						= person.employment() == Employment.RETIRED ? 1 : 0;
		double employment_student_primary 		= person.employment() == Employment.STUDENT_PRIMARY ? 1 : 0;
		double employment_student_secondary 	= person.employment() == Employment.STUDENT_SECONDARY ? 1 : 0;



		double parkingpressure 	= impedance.getParkingStress(target, date);
		double distance 					= (float) Math.max(0.1,
																						impedance.getDistance(source, target)/1000.0);
		double commutingdistance = impedance.getDistance(homeZone, poleZone)/1000.0f;

		double shorttrip = distance <= 1.3 ? 1.0f : 0.0f;



		Map<String,Double> attributes = new LinkedHashMap<String,Double>();

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
		attributes.put("EMPLOYMENT_NOTEMPLOYED",				employment_notemployed);
		attributes.put("EMPLOYMENT_RETIRED",						employment_retired);
		attributes.put("EMPLOYMENT_STUDENT_PRIMARY",		employment_student_primary);
		attributes.put("EMPLOYMENT_STUDENT_SECONDARY",	employment_student_secondary);

		Map<Mode,Map<String,Double>> modeAttributes = new LinkedHashMap<Mode,Map<String,Double>>();

		for (Mode mode : modes) {

			Map<String,Double> attrib = new LinkedHashMap<String,Double>(attributes);

			double time 	= impedance.getTravelTime(source, target, mode, date);

			double cost_km 	= (mode==Mode.PUBLICTRANSPORT && person.hasCommuterTicket()) ? 0.0
												: impedance.getTravelCost(source, target, mode, date)/distance;

		 	attrib.put("TIME", 		time);
			attrib.put("COST_KM", cost_km);

			modeAttributes.put(mode, attrib);
		}

		return  modeAttributes;
	}

	public Map<String,Double> parameterForMode(Mode mode) {
	
		assert this.parameters.containsKey(mode);

		return this.parameters.get(mode);
	}
}
