package edu.kit.ifv.mobitopp.simulation;

class ModeSelectorParameterOtherTrip {

	public final float carsharing_sb_const 	  	=  -1.5f;
	public final float carsharing_sb_shorttrip	=  -1.0f;
	public final float carsharing_sb_distance		=  0.15f;		

	public final float carsharing_ff_const 	  	= -0.5f;
	public final float carsharing_ff_shorttrip	= -1.0f;
	public final float carsharing_ff_distance		= -0.05f;		

	public final float car_const 	  =  0.0f;
	public final float foot_const 	= -0.9766f -2.5558f - 0.8f 					+ 0.3f;
	public final float pt_const 		= -4.0281f -1.2280f - 0.0f - 0.5f		+ 0.8f;
	public final float pass_const 	= -3.4463f +0.1618f + 0.7f - 0.5f		+ 0.4f;
	public final float bike_const 	= -3.0257f -1.7043f - 0.5f + 0.2f		+ 0.2f;

	public final float time 				= -0.0134f;
	public final float cost_km			= -0.0298f;



	public final float car_last_car 	= 0.0f + 1.0f;
	public final float car_last_foot 	= 0.0f + 0.8f;
	public final float car_last_pt 		= 0.0f + 0.6f;
	public final float car_last_pass 	= 0.0f + 0.5f;
	public final float car_last_bike 	= 0.0f + 0.3f;

	public final float foot_last_car 	= 0.0f	+ 0.8f;
	public final float foot_last_foot = 3.3883f + 0.4f;
	public final float foot_last_pt 	= 2.9914f;
	public final float foot_last_pass = 2.6433f + 0.4f;
	public final float foot_last_bike = 2.0787f;

	public final float pt_last_car 	= 0.0f		- 0.1f;
	public final float pt_last_foot = 2.8677f - 0.8f;
	public final float pt_last_pt 	= 4.7725f + 0.2f;
	public final float pt_last_pass = 2.8936f - 0.8f;
	public final float pt_last_bike = 2.6457f;

	public final float pass_last_car 	= 0.0f	+ 0.3f;
	public final float pass_last_foot = 2.0989f + 0.3f;
	public final float pass_last_pt 	= 2.7006f;
	public final float pass_last_pass = 4.6064f + 0.3f;
	public final float pass_last_bike = 2.1010f;

	public final float bike_last_car 	= 0.0f		+ 1.8f;
	public final float bike_last_foot = 1.9099f;
	public final float bike_last_pt 	= 2.8007f + 0.0f;
	public final float bike_last_pass = 2.4327f;
	public final float bike_last_bike = 5.8491f;


	public final float foot_caravailable_true	= -0.2249f;
	public final float foot_caravailable_no		=  1.7847f;
	public final float foot_caravailable_part	=  0.0f;

	public final float pt_caravailable_true		= -0.6947f;
	public final float pt_caravailable_no			=  1.8398f;
	public final float pt_caravailable_part		=  0.0f;

	public final float pass_caravailable_true	= -0.4906f;
	public final float pass_caravailable_no		=  1.9301f;
	public final float pass_caravailable_part	=  0.0f;

	public final float bike_caravailable_true	= -0.6383f;
	public final float bike_caravailable_no		=  1.5430f;
	public final float bike_caravailable_part	=  0.0f;


	public final float foot_commticket_false		=  0.0f;
	public final float foot_commticket_true			=  0.1298f;

	public final float pt_commticket_false			=  0.0f;
	public final float pt_commticket_true				=  1.7425f;

	public final float pass_commticket_false		=  0.0f;
	public final float pass_commticket_true			=  0.1344f;

	public final float bike_commticket_false		=  0.0f;
	public final float bike_commticket_true			=  -0.0321f;




	public final float foot_activity_private_business 	=   0.0f;
	public final float foot_activity_work								=   0.0352f;
	public final float foot_activity_education					=   1.3783f;
	public final float foot_activity_shopping						=  -0.0714f;
	public final float foot_activity_leisure						=   0.5168f + 0.2f;
	public final float foot_activity_home								=  -0.0107f;
	public final float foot_activity_walk								=   8.6241f;
	public final float foot_activity_service						=  -0.7756f - 0.3f;
	public final float foot_activity_business						=  -0.1838f - 1.0f;

	public final float pt_activity_private_business 		=   0.0f;
	public final float pt_activity_work									=   0.7986f;
	public final float pt_activity_education						=   2.1520f;
	public final float pt_activity_shopping							=  -0.3780f;
	public final float pt_activity_leisure							=   0.3233f;
	public final float pt_activity_home									=   0.7535f;
	public final float pt_activity_walk									=  -0.7115f;
	public final float pt_activity_service							=  -1.3699f - 0.3f;
	public final float pt_activity_business							=  -0.2594f - 0.2f;

	public final float pass_activity_private_business 	=   0.0f;
	public final float pass_activity_work								=  -1.1023f;
	public final float pass_activity_education					=  -0.6120f;
	public final float pass_activity_shopping						=  -0.0713f;
	public final float pass_activity_leisure						=   0.5793f + 0.5f;
	public final float pass_activity_home								=  -0.1960f;
	public final float pass_activity_walk								=   0.3543f;
	public final float pass_activity_service						=  -0.9250f - 0.5f;
	public final float pass_activity_business						=  -0.2975f - 0.6f;

	public final float bike_activity_private_business 	=   0.0f;
	public final float bike_activity_work								=   1.0286f;
	public final float bike_activity_education					=   1.5253f;
	public final float bike_activity_shopping						=   0.2197f;
	public final float bike_activity_leisure						=   0.2315f;
	public final float bike_activity_home								=   0.4395f;
	public final float bike_activity_walk								=   6.0873f;
	public final float bike_activity_service						=  -0.9958f - 0.1f;
	public final float bike_activity_business						=  -0.1757f;


	public final float foot_weekday_workday 					=   0.0f;
	public final float foot_weekday_saturday 					=   0.0691f - 0.0f;
	public final float foot_weekday_sunday 						=   0.4860f - 0.2f;

	public final float pt_weekday_workday 						=   0.0f;
	public final float pt_weekday_saturday 						=  -0.1646f + 0.5f;
	public final float pt_weekday_sunday 							=  -0.4646f + 0.6f;

	public final float pass_weekday_workday 					=   0.0f;
	public final float pass_weekday_saturday 					=   0.4558f + 0.4f;
	public final float pass_weekday_sunday 						=   0.4862f + 0.0f;

	public final float bike_weekday_workday 					=   0.0f;
	public final float bike_weekday_saturday 					=   -0.0958f;
	public final float bike_weekday_sunday 						=   0.1273f;


	public final float foot_hhtype_other							=   0.0f;
	public final float foot_hhtype_2adults						=   0.2901f;
	public final float foot_hhtype_1adult							=   0.3682f;
	public final float foot_hhtype_kids								=   0.1567f;	

	public final float pt_hhtype_other								=   0.0f;
	public final float pt_hhtype_2adults							=   0.2066f;
	public final float pt_hhtype_1adult								=   0.3091f;
	public final float pt_hhtype_kids									=  -0.0417f;	

	public final float pass_hhtype_other							=   0.0f;
	public final float pass_hhtype_2adults						=   0.3170f;
	public final float pass_hhtype_1adult							=  -0.6129f;
	public final float pass_hhtype_kids								=  -0.0924f;	

	public final float bike_hhtype_other							=   0.0f;
	public final float bike_hhtype_2adults						=   0.2090f;
	public final float bike_hhtype_1adult							=   0.2453f;
	public final float bike_hhtype_kids								=   0.3132f;	


	public final float foot_employment_student_tertiary 	=   0.0f	- 1.0f;
	public final float foot_employment_education					=  -0.1756f	- 0.1f;
	public final float foot_employment_work_from41				=   0.2054f + 0.5f;
	public final float foot_employment_work_to40					=   0.3351f + 0.5f;
	public final float foot_employment_unemployed					=   0.4154f - 0.2f; // Househusband
	public final float foot_employment_jobless						=   0.4154f - 0.9f; // jobless
	public final float foot_employment_retired						=   0.4349f	- 0.9f;
	public final float foot_employment_student_primary	 	=   5.0032f - 0.05f;
	public final float foot_employment_student_secondary	=   0.5341f - 0.5f;

	public final float pt_employment_student_tertiary 	=   0.0f	- 1.7f;
	public final float pt_employment_education					=  -0.2767f - 0.7f;
	public final float pt_employment_work_from41				=   0.2042f	- 0.3f;
	public final float pt_employment_work_to40					=   0.2192f	- 0.3f;
	public final float pt_employment_unemployed					=   0.3336f	- 0.1f;
	public final float pt_employment_jobless						=   0.3336f	+ 0.5f;
	public final float pt_employment_retired						=   0.3892f + 0.1f;
	public final float pt_employment_student_primary 		=   4.3631f	- 0.2f;
	public final float pt_employment_student_secondary 	=   0.5280f - 0.0f;

	public final float pass_employment_student_tertiary 	=   0.0f - 1.5f;
	public final float pass_employment_education					=  -0.0863f	- 1.1f;
	public final float pass_employment_work_from41				=  -0.2412f - 0.7f;
	public final float pass_employment_work_to40					=  -0.2125f - 0.7f;
	public final float pass_employment_unemployed					=   0.1484f - 0.45f;
	public final float pass_employment_jobless						=   0.1484f - 0.55f;
	public final float pass_employment_retired						=  -0.2066f - 0.45f;
	public final float pass_employment_student_primary 		=   5.3108f + 0.3f;
	public final float pass_employment_student_secondary	=   0.7441f - 0.6f;

	public final float bike_employment_student_tertiary 	=   0.0f - 0.5f;
	public final float bike_employment_education					=  -0.2863f;
	public final float bike_employment_work_from41				=   0.3280f - 0.2f;
	public final float bike_employment_work_to40					=   0.3523f - 0.2f;
	public final float bike_employment_unemployed					=   0.1905f - 0.4f;
	public final float bike_employment_jobless						=   0.1905f - 0.4f;
	public final float bike_employment_retired						=   0.1000f - 0.15f;
	public final float bike_employment_student_primary 		=   4.3664f + 0.0f;
	public final float bike_employment_student_secondary	=   0.7397f - 0.1f;


	public final float car_parkingpressure							=   0.0f;
	public final float foot_parkingpressure							=   0.1227f;
	public final float pt_parkingpressure								=   0.2117f;
	public final float pass_parkingpressure							=   0.0271f;
	public final float bike_parkingpressure							=   0.0205f;

	public final float car_distance											=   0.0f;		
	public final float foot_distance										=  -0.2837f;		
	public final float pt_distance											=   0.0165f 	+ 0.03f		+ 0.035f;		
	public final float pass_distance										=   0.00565f 	+ 0.03f 	- 0.01f;		
	public final float bike_distance										=  -0.1192f;		

	public final float car_commutingdistance						=   0.0f;		
	public final float foot_commutingdistance						=  -0.00305f;
	public final float pt_commutingdistance							=  -0.00978f;
	public final float pass_commutingdistance						=  -0.00574f;
	public final float bike_commutingdistance						=  -0.00474f;

	public final float car_shorttrip						=    0.0f;		
	public final float foot_shorttrip						=    2.5558f - 0.2f					- 0.3f;
	public final float pt_shorttrip							=    1.2280f - 2.9f -1.5f 	+ 1.2f;
	public final float pass_shorttrip						=   -0.1618f + 0.3f					- 1.0f;
	public final float bike_shorttrip						=    1.7043f + 0.4f;
}
