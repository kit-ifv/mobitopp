package edu.kit.ifv.mobitopp.simulation;

class ModeSelectorParameterFirstTrip {

	public final float carsharing_sb_const 			=  -1.5f;
	public final float carsharing_sb_shorttrip	=  -1.0f;
	public final float carsharing_sb_distance		=  0.15f;		

	public final float carsharing_ff_const 			= -0.5f;
	public final float carsharing_ff_shorttrip	= -1.0f;
	public final float carsharing_ff_distance		= -0.05f;		

	public final float car_const 	=  0.0f;
	public final float foot_const 	=  0.7282f -2.5355f	+ 0.1f;
	public final float pt_const 		= -2.6699f -0.8402f	+ 0.9f - 0.6f;
	public final float pass_const 	= -1.7962f +0.0979f + 0.7f;
	public final float bike_const 	= -0.2889f -1.2546f + 1.0f + 0.1f;

	public final float time 				= -0.0102f;
	public final float cost_km			= -0.0282f;


	public final float foot_caravailable_true	= -0.9468f;
	public final float foot_caravailable_no		=  2.3661f;
	public final float foot_caravailable_part	=  0.0f;

	public final float pt_caravailable_true		= -1.6001f;
	public final float pt_caravailable_no			=  2.4664f;
	public final float pt_caravailable_part		=  0.0f;

	public final float pass_caravailable_true	= -1.1910f;
	public final float pass_caravailable_no		=  2.5440f;
	public final float pass_caravailable_part	=  0.0f;

	public final float bike_caravailable_true	= -1.5245f;
	public final float bike_caravailable_no		=  2.0363f;
	public final float bike_caravailable_part	=  0.0f;


	public final float foot_commticket_false		=  0.0f;
	public final float foot_commticket_true		=  0.8621f;

	public final float pt_commticket_false			=  0.0f;
	public final float pt_commticket_true			=  3.5163f;

	public final float pass_commticket_false		=  0.0f;
	public final float pass_commticket_true		=  0.7554f;

	public final float bike_commticket_false		=  0.0f;
	public final float bike_commticket_true		=  0.3378f;

	public final float foot_activity_private_business 	=   0.0f;
	public final float foot_activity_work								=  -0.2524f;
	public final float foot_activity_education					=   1.3114f;
	public final float foot_activity_shopping						=  -0.0383f;
	public final float foot_activity_leisure						=   0.1730f;
	public final float foot_activity_walk								=   8.2890f;
	public final float foot_activity_service						=  -0.7034f;
	public final float foot_activity_business						=  -0.7886f;

	public final float pt_activity_private_business 		=   0.0f;
	public final float pt_activity_work									=   0.5140f;
	public final float pt_activity_education						=   1.5488f;
	public final float pt_activity_shopping							=  -0.3953f;
	public final float pt_activity_leisure							=   0.2116f;
	public final float pt_activity_walk									=  -1.6447f;
	public final float pt_activity_service							=  -1.9713f;
	public final float pt_activity_business							=  -0.0501f;

	public final float pass_activity_private_business 	=   0.0f;
	public final float pass_activity_work								=  -1.2274f;
	public final float pass_activity_education					=  -0.6317f;
	public final float pass_activity_shopping						=  -0.1370f;
	public final float pass_activity_leisure						=   0.2757f;
	public final float pass_activity_walk								=   0.1835f;
	public final float pass_activity_service						=  -1.3161f;
	public final float pass_activity_business						=  -1.2343f;

	public final float bike_activity_private_business 	=   0.0f;
	public final float bike_activity_work								=   0.8274f;
	public final float bike_activity_education					=   1.0798f;
	public final float bike_activity_shopping						=   0.2980f;
	public final float bike_activity_leisure						=   0.5172f;
	public final float bike_activity_walk								=   5.0464f;
	public final float bike_activity_service						=  -1.1627f;
	public final float bike_activity_business						=   0.4171f;


	public final float foot_weekday_workday 					=   0.0f;
	public final float foot_weekday_saturday 					=   0.6280f;
	public final float foot_weekday_sunday 						=   3.6852f;

	public final float pt_weekday_workday 						=   0.0f;
	public final float pt_weekday_saturday 						=  -0.0473f;
	public final float pt_weekday_sunday 							=  -0.0746f;

	public final float pass_weekday_workday 					=   0.0f;
	public final float pass_weekday_saturday 					=   0.1617f;
	public final float pass_weekday_sunday 						=   2.1195f;

	public final float bike_weekday_workday 					=   0.0f;
	public final float bike_weekday_saturday 					=   -2.1148f;
	public final float bike_weekday_sunday 						=   -0.4720f;


	public final float foot_hhtype_other							=   0.0f;
	public final float foot_hhtype_2adults						=   0.7236f;
	public final float foot_hhtype_1adult							=   0.9611f;
	public final float foot_hhtype_kids								=   0.3984f;	

	public final float pt_hhtype_other								=   0.0f;
	public final float pt_hhtype_2adults							=   0.6425f;
	public final float pt_hhtype_1adult								=   0.7769f;
	public final float pt_hhtype_kids									=   0.2456f;	

	public final float pass_hhtype_other							=   0.0f;
	public final float pass_hhtype_2adults						=   0.3058f;
	public final float pass_hhtype_1adult							=  -1.3062f;
	public final float pass_hhtype_kids								=  -0.2996f;	

	public final float bike_hhtype_other							=   0.0f;
	public final float bike_hhtype_2adults						=   0.1100f;
	public final float bike_hhtype_1adult							=   0.2721f;
	public final float bike_hhtype_kids								=   0.7919f;	


	public final float foot_employment_student_tertiary 	=   0.0f;
	public final float foot_employment_education					=   0.1316f;
	public final float foot_employment_work_from41				=   0.7693f;
	public final float foot_employment_work_to40					=   0.6087f;
	public final float foot_employment_notemployed				=   1.0620f;
	public final float foot_employment_retired						=   0.7579f;
	public final float foot_employment_student_primary	 	=   3.7866f;
	public final float foot_employment_student_secondary	=   0.5328f;

	public final float pt_employment_student_tertiary 	=   0.0f;
	public final float pt_employment_education					=   -0.3362f;
	public final float pt_employment_work_from41				=   0.2859f;
	public final float pt_employment_work_to40					=   0.3982f;
	public final float pt_employment_notemployed				=   0.9388f;
	public final float pt_employment_retired						=   0.8726f;
	public final float pt_employment_student_primary 		=   2.5690f;
	public final float pt_employment_student_secondary 	=   0.3837f;

	public final float pass_employment_student_tertiary 	=   0.0f;
	public final float pass_employment_education					=   0.6165f;
	public final float pass_employment_work_from41				=   0.000860f;
	public final float pass_employment_work_to40					=   0.2398f;
	public final float pass_employment_notemployed				=   0.3636f;
	public final float pass_employment_retired						=   0.5508f;
	public final float pass_employment_student_primary 		=   4.5114f;
	public final float pass_employment_student_secondary	=   1.5469f;

	public final float bike_employment_student_tertiary 	=   0.0f;
	public final float bike_employment_education					=  -0.7324f;
	public final float bike_employment_work_from41				=  -0.1068f;
	public final float bike_employment_work_to40					=  -0.1765f;
	public final float bike_employment_notemployed				=  -0.2762f;
	public final float bike_employment_retired						=  -0.4995f;
	public final float bike_employment_student_primary 		=   0.9420f;
	public final float bike_employment_student_secondary	=   0.3377f;


	public final float car_parkingpressure							=   0.0f;
	public final float foot_parkingpressure							=   0.1123f;
	public final float pt_parkingpressure								=   0.2198f;
	public final float pass_parkingpressure							=   0.0487f;
	public final float bike_parkingpressure							=   0.0462f;

	public final float car_distance											=   0.0f;		
	public final float foot_distance										=  -0.3729f;		
	public final float pt_distance											=   0.0189f + 0.03f;		
	public final float pass_distance										=   0.0149f;		
	public final float bike_distance										=  -0.1719f - 0.1f;		

	public final float car_commutingdistance						=   0.0f;		
	public final float foot_commutingdistance						=  -0.0114f;
	public final float pt_commutingdistance							=  -0.0139f;
	public final float pass_commutingdistance						=  -0.0129f;
	public final float bike_commutingdistance						=  -0.00867f;

	public final float car_shorttrip						=  -1.0f;		
	public final float foot_shorttrip						=   2.5355f	-1.9f;
	public final float pt_shorttrip							=   0.8402f -4.15f + 0.1f;
	public final float pass_shorttrip						=  -0.0979f	-0.85f;
	public final float bike_shorttrip						=   1.2546f	-1.35f;
}
