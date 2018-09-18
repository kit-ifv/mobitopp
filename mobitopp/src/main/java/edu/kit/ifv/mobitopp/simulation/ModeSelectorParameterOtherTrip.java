package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.io.InputStream;

import edu.kit.ifv.mobitopp.util.parameter.ParameterFormularParser;

class ModeSelectorParameterOtherTrip {

	public final Double carsharing_sb_const 		= Double.NaN;
	public final Double carsharing_sb_shorttrip	= Double.NaN;
	public final Double carsharing_sb_distance	= Double.NaN;

	public final Double carsharing_ff_const 		= Double.NaN;
	public final Double carsharing_ff_shorttrip	= Double.NaN;
	public final Double carsharing_ff_distance	= Double.NaN;

	public final Double car_const 	= Double.NaN;
	public final Double foot_const 	= Double.NaN;
	public final Double pt_const 		= Double.NaN;
	public final Double pass_const 	= Double.NaN;
	public final Double bike_const 	= Double.NaN;

	public final Double time 				= Double.NaN;
	public final Double cost_km			= Double.NaN;



	public final Double car_last_car 		= Double.NaN;
	public final Double car_last_foot 	= Double.NaN;
	public final Double car_last_pt 		= Double.NaN;
	public final Double car_last_pass 	= Double.NaN;
	public final Double car_last_bike 	= Double.NaN;

	public final Double foot_last_car 	= Double.NaN;
	public final Double foot_last_foot 	= Double.NaN;
	public final Double foot_last_pt 		= Double.NaN;
	public final Double foot_last_pass 	= Double.NaN;
	public final Double foot_last_bike 	= Double.NaN;

	public final Double pt_last_car 	= Double.NaN;
	public final Double pt_last_foot 	= Double.NaN;
	public final Double pt_last_pt 		= Double.NaN;
	public final Double pt_last_pass 	= Double.NaN;
	public final Double pt_last_bike 	= Double.NaN;

	public final Double pass_last_car 	= Double.NaN;
	public final Double pass_last_foot	= Double.NaN;
	public final Double pass_last_pt 		= Double.NaN;
	public final Double pass_last_pass 	= Double.NaN;
	public final Double pass_last_bike 	= Double.NaN;

	public final Double bike_last_car 	= Double.NaN;
	public final Double bike_last_foot 	= Double.NaN;
	public final Double bike_last_pt 		= Double.NaN;
	public final Double bike_last_pass 	= Double.NaN;
	public final Double bike_last_bike 	= Double.NaN;


	public final Double foot_caravailable_true	= Double.NaN;
	public final Double foot_caravailable_no		= Double.NaN;
	public final Double foot_caravailable_part	= Double.NaN;

	public final Double pt_caravailable_true		= Double.NaN;
	public final Double pt_caravailable_no			= Double.NaN;
	public final Double pt_caravailable_part		= Double.NaN;

	public final Double pass_caravailable_true	= Double.NaN;
	public final Double pass_caravailable_no		= Double.NaN;
	public final Double pass_caravailable_part	= Double.NaN;

	public final Double bike_caravailable_true	= Double.NaN;
	public final Double bike_caravailable_no		= Double.NaN;
	public final Double bike_caravailable_part	= Double.NaN;


	public final Double foot_commticket_false		= Double.NaN;
	public final Double foot_commticket_true		= Double.NaN;

	public final Double pt_commticket_false			= Double.NaN;
	public final Double pt_commticket_true			= Double.NaN;

	public final Double pass_commticket_false		= Double.NaN;
	public final Double pass_commticket_true		= Double.NaN;

	public final Double bike_commticket_false		= Double.NaN;
	public final Double bike_commticket_true		= Double.NaN;




	public final Double foot_activity_private_business 	= Double.NaN;
	public final Double foot_activity_work							= Double.NaN;
	public final Double foot_activity_education					= Double.NaN;
	public final Double foot_activity_shopping					= Double.NaN;
	public final Double foot_activity_leisure						= Double.NaN;
	public final Double foot_activity_home							= Double.NaN;
	public final Double foot_activity_walk							= Double.NaN;
	public final Double foot_activity_service						= Double.NaN;
	public final Double foot_activity_business					= Double.NaN;

	public final Double pt_activity_private_business 		= Double.NaN;
	public final Double pt_activity_work								= Double.NaN;
	public final Double pt_activity_education						= Double.NaN;
	public final Double pt_activity_shopping						= Double.NaN;
	public final Double pt_activity_leisure							= Double.NaN;
	public final Double pt_activity_home								= Double.NaN;
	public final Double pt_activity_walk								= Double.NaN;
	public final Double pt_activity_service							= Double.NaN;
	public final Double pt_activity_business						= Double.NaN;

	public final Double pass_activity_private_business 	= Double.NaN;
	public final Double pass_activity_work							= Double.NaN;
	public final Double pass_activity_education					= Double.NaN;
	public final Double pass_activity_shopping					= Double.NaN;
	public final Double pass_activity_leisure						= Double.NaN;
	public final Double pass_activity_home							= Double.NaN;
	public final Double pass_activity_walk							= Double.NaN;
	public final Double pass_activity_service						= Double.NaN;
	public final Double pass_activity_business					= Double.NaN;

	public final Double bike_activity_private_business 	= Double.NaN;
	public final Double bike_activity_work							= Double.NaN;
	public final Double bike_activity_education					= Double.NaN;
	public final Double bike_activity_shopping					= Double.NaN;
	public final Double bike_activity_leisure						= Double.NaN;
	public final Double bike_activity_home							= Double.NaN;
	public final Double bike_activity_walk							= Double.NaN;
	public final Double bike_activity_service						= Double.NaN;
	public final Double bike_activity_business					= Double.NaN;


	public final Double foot_weekday_workday 					= Double.NaN;
	public final Double foot_weekday_saturday 				= Double.NaN;
	public final Double foot_weekday_sunday 					= Double.NaN;

	public final Double pt_weekday_workday 						= Double.NaN;
	public final Double pt_weekday_saturday 					= Double.NaN;
	public final Double pt_weekday_sunday 						= Double.NaN;

	public final Double pass_weekday_workday 					= Double.NaN;
	public final Double pass_weekday_saturday 				= Double.NaN;
	public final Double pass_weekday_sunday 					= Double.NaN;

	public final Double bike_weekday_workday 					= Double.NaN;
	public final Double bike_weekday_saturday 				= Double.NaN;
	public final Double bike_weekday_sunday 					= Double.NaN;


	public final Double foot_hhtype_other							= Double.NaN;
	public final Double foot_hhtype_2adults						= Double.NaN;
	public final Double foot_hhtype_1adult						= Double.NaN;
	public final Double foot_hhtype_kids							= Double.NaN;

	public final Double pt_hhtype_other								= Double.NaN;
	public final Double pt_hhtype_2adults							= Double.NaN;
	public final Double pt_hhtype_1adult							= Double.NaN;
	public final Double pt_hhtype_kids								= Double.NaN;

	public final Double pass_hhtype_other							= Double.NaN;
	public final Double pass_hhtype_2adults						= Double.NaN;
	public final Double pass_hhtype_1adult						= Double.NaN;
	public final Double pass_hhtype_kids							= Double.NaN;

	public final Double bike_hhtype_other							= Double.NaN;
	public final Double bike_hhtype_2adults						= Double.NaN;
	public final Double bike_hhtype_1adult						= Double.NaN;
	public final Double bike_hhtype_kids							= Double.NaN;


	public final Double foot_employment_student_tertiary 	= Double.NaN;
	public final Double foot_employment_education					= Double.NaN;
	public final Double foot_employment_work_from41				= Double.NaN;
	public final Double foot_employment_work_to40					= Double.NaN;
	public final Double foot_employment_unemployed				= Double.NaN;
	public final Double foot_employment_jobless						= Double.NaN;
	public final Double foot_employment_retired						= Double.NaN;
	public final Double foot_employment_student_primary	 	= Double.NaN;
	public final Double foot_employment_student_secondary	= Double.NaN;

	public final Double pt_employment_student_tertiary 		= Double.NaN;
	public final Double pt_employment_education						= Double.NaN;
	public final Double pt_employment_work_from41					= Double.NaN;
	public final Double pt_employment_work_to40						= Double.NaN;
	public final Double pt_employment_unemployed					= Double.NaN;
	public final Double pt_employment_jobless							= Double.NaN;
	public final Double pt_employment_retired							= Double.NaN;
	public final Double pt_employment_student_primary 		= Double.NaN;
	public final Double pt_employment_student_secondary 	= Double.NaN;

	public final Double pass_employment_student_tertiary 	= Double.NaN;
	public final Double pass_employment_education					= Double.NaN;
	public final Double pass_employment_work_from41				= Double.NaN;
	public final Double pass_employment_work_to40					= Double.NaN;
	public final Double pass_employment_unemployed				= Double.NaN;
	public final Double pass_employment_jobless						= Double.NaN;
	public final Double pass_employment_retired						= Double.NaN;
	public final Double pass_employment_student_primary 	= Double.NaN;
	public final Double pass_employment_student_secondary	= Double.NaN;

	public final Double bike_employment_student_tertiary 	= Double.NaN;
	public final Double bike_employment_education					= Double.NaN;
	public final Double bike_employment_work_from41				= Double.NaN;
	public final Double bike_employment_work_to40					= Double.NaN;
	public final Double bike_employment_unemployed				= Double.NaN;
	public final Double bike_employment_jobless						= Double.NaN;
	public final Double bike_employment_retired						= Double.NaN;
	public final Double bike_employment_student_primary 	= Double.NaN;
	public final Double bike_employment_student_secondary	= Double.NaN;


	public final Double car_parkingpressure								= Double.NaN;
	public final Double foot_parkingpressure							= Double.NaN;
	public final Double pt_parkingpressure								= Double.NaN;
	public final Double pass_parkingpressure							= Double.NaN;
	public final Double bike_parkingpressure							= Double.NaN;

	public final Double car_distance										= Double.NaN;
	public final Double foot_distance										= Double.NaN;
	public final Double pt_distance											= Double.NaN;
	public final Double pass_distance										= Double.NaN;
	public final Double bike_distance										= Double.NaN;

	public final Double car_commutingdistance							= Double.NaN;
	public final Double foot_commutingdistance						= Double.NaN;
	public final Double pt_commutingdistance							= Double.NaN;
	public final Double pass_commutingdistance						= Double.NaN;
	public final Double bike_commutingdistance						= Double.NaN;

	public final Double car_shorttrip							= Double.NaN;
	public final Double foot_shorttrip						= Double.NaN;
	public final Double pt_shorttrip							= Double.NaN;
	public final Double pass_shorttrip						= Double.NaN;
	public final Double bike_shorttrip						= Double.NaN;

	public ModeSelectorParameterOtherTrip(File file) {
		super();
		new ParameterFormularParser().parseConfig(file, this);
	}

	public ModeSelectorParameterOtherTrip() {
		super();
		new ParameterFormularParser().parseConfig(defaultParameters(), this);
	}

	private InputStream defaultParameters() {
		return ModeSelectorParameterOtherTrip.class
				.getResourceAsStream("default-mode-selector-parameter-other-trip.txt");
	}
}
