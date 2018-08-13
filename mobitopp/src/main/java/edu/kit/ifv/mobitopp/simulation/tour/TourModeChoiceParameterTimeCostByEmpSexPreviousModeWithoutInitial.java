package edu.kit.ifv.mobitopp.simulation.tour;


public class TourModeChoiceParameterTimeCostByEmpSexPreviousModeWithoutInitial 
	extends TourModeChoiceParameterBase 
	implements TourModeChoiceParameter
{

	protected Double walking_mode_before_cardriver = 0.0;
	protected Double walking_mode_before_carpassenger = 0.0;
	protected Double walking_mode_before_cycling = 0.0;
	protected Double walking_mode_before_publictransport = 0.0;
	protected Double walking_mode_before_walking = 0.0;
	
	protected Double cardriver_mode_before_cardriver;
	protected Double cardriver_mode_before_carpassenger;
	protected Double cardriver_mode_before_cycling;
	protected Double cardriver_mode_before_publictransport;
	protected Double cardriver_mode_before_walking;
	protected Double carpassenger_mode_before_cardriver;
	protected Double carpassenger_mode_before_carpassenger;
	protected Double carpassenger_mode_before_cycling;
	protected Double carpassenger_mode_before_publictransport;
	protected Double carpassenger_mode_before_walking;
	protected Double cycling_mode_before_cardriver;
	protected Double cycling_mode_before_carpassenger;
	protected Double cycling_mode_before_cycling;
	protected Double cycling_mode_before_publictransport;
	protected Double cycling_mode_before_walking;
	protected Double publictransport_mode_before_cardriver;
	protected Double publictransport_mode_before_carpassenger;
	protected Double publictransport_mode_before_cycling;
	protected Double publictransport_mode_before_publictransport;
	protected Double publictransport_mode_before_walking;


	/*
	 * 
Call:
mlogit(formula = mode ~ time:employment. + cost:employment. + 
    time:sex. + cost:sex. | intrazonal + sex. + employment. + 
    purpose. + day. + containsService. + containsStrolling. + 
    mode_before., data = trainingdata, reflevel = "walking", 
    method = "nr", print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2827          0.3916          0.1283          0.0559          0.1415 

nr method
21 iterations, 0h:8m:31s 
g'(-H)^-1g = 6.5E-07 
gradient close to zero 

Coefficients :
                                                Estimate  Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                           0.448675    0.060829    7.38  1.6e-13 ***
carpassenger:(intercept)                       -3.423535    0.088225  -38.80  < 2e-16 ***
cycling:(intercept)                            -1.978812    0.076636  -25.82  < 2e-16 ***
publictransport:(intercept)                     0.181737    0.063392    2.87  0.00415 ** 
time:employment.fulltime                       -0.025301    0.000926  -27.33  < 2e-16 ***
time:employment.parttime                       -0.021652    0.001537  -14.09  < 2e-16 ***
time:employment.unemployed                     -0.011522    0.002605   -4.42  9.7e-06 ***
time:employment.apprentice                     -0.022718    0.003814   -5.96  2.6e-09 ***
time:employment.homekeeper                     -0.026392    0.002426  -10.88  < 2e-16 ***
time:employment.retired                        -0.023918    0.001091  -21.92  < 2e-16 ***
time:employment.student_primary                -0.030824    0.002837  -10.86  < 2e-16 ***
time:employment.student_secondary              -0.024631    0.001389  -17.73  < 2e-16 ***
time:employment.student_tertiary               -0.016808    0.002597   -6.47  9.7e-11 ***
time:employment.other                          -0.015567    0.002763   -5.63  1.8e-08 ***
cost:employment.fulltime                       -0.399061    0.015676  -25.46  < 2e-16 ***
cost:employment.parttime                       -0.285636    0.030787   -9.28  < 2e-16 ***
cost:employment.unemployed                     -0.257929    0.086242   -2.99  0.00278 ** 
cost:employment.apprentice                     -0.444739    0.056071   -7.93  2.2e-15 ***
cost:employment.homekeeper                     -0.184190    0.049458   -3.72  0.00020 ***
cost:employment.retired                        -0.274337    0.022426  -12.23  < 2e-16 ***
cost:employment.student_primary                -0.918705    0.141759   -6.48  9.1e-11 ***
cost:employment.student_secondary              -0.724934    0.046332  -15.65  < 2e-16 ***
cost:employment.student_tertiary               -0.260184    0.049182   -5.29  1.2e-07 ***
cost:employment.other                          -0.127494    0.060574   -2.10  0.03531 *  
time:sex.female                                -0.007022    0.001054   -6.66  2.7e-11 ***
cost:sex.female                                -0.052519    0.021201   -2.48  0.01324 *  
cardriver:intrazonal                           -1.307693    0.035415  -36.93  < 2e-16 ***
carpassenger:intrazonal                        -1.176718    0.044938  -26.19  < 2e-16 ***
cycling:intrazonal                              0.014674    0.047776    0.31  0.75874    
publictransport:intrazonal                     -3.368146    0.095045  -35.44  < 2e-16 ***
cardriver:sex.female                           -0.650895    0.042855  -15.19  < 2e-16 ***
carpassenger:sex.female                         0.304659    0.046400    6.57  5.2e-11 ***
cycling:sex.female                             -0.591442    0.052306  -11.31  < 2e-16 ***
publictransport:sex.female                      0.012701    0.042542    0.30  0.76528    
cardriver:employment.parttime                   0.153818    0.061423    2.50  0.01227 *  
carpassenger:employment.parttime                0.233533    0.078411    2.98  0.00290 ** 
cycling:employment.parttime                     0.401459    0.078282    5.13  2.9e-07 ***
publictransport:employment.parttime            -0.179893    0.070843   -2.54  0.01111 *  
cardriver:employment.unemployed                -0.145977    0.154612   -0.94  0.34509    
carpassenger:employment.unemployed              0.385160    0.180819    2.13  0.03316 *  
cycling:employment.unemployed                  -0.482111    0.263180   -1.83  0.06697 .  
publictransport:employment.unemployed           0.127067    0.188014    0.68  0.49915    
cardriver:employment.apprentice                 0.355751    0.180258    1.97  0.04843 *  
carpassenger:employment.apprentice              0.871813    0.205854    4.24  2.3e-05 ***
cycling:employment.apprentice                  -0.069060    0.264546   -0.26  0.79405    
publictransport:employment.apprentice           0.579479    0.163307    3.55  0.00039 ***
cardriver:employment.homekeeper                -0.165244    0.077133   -2.14  0.03217 *  
carpassenger:employment.homekeeper              0.068925    0.098084    0.70  0.48224    
cycling:employment.homekeeper                   0.243801    0.108361    2.25  0.02446 *  
publictransport:employment.homekeeper          -0.443203    0.122953   -3.60  0.00031 ***
cardriver:employment.retired                   -0.185328    0.051238   -3.62  0.00030 ***
carpassenger:employment.retired                 0.247928    0.068456    3.62  0.00029 ***
cycling:employment.retired                     -0.380935    0.079448   -4.79  1.6e-06 ***
publictransport:employment.retired              0.187217    0.066502    2.82  0.00487 ** 
cardriver:employment.student_primary          -21.850678 2045.237182   -0.01  0.99148    
carpassenger:employment.student_primary         1.261953    0.098222   12.85  < 2e-16 ***
cycling:employment.student_primary             -0.424715    0.127537   -3.33  0.00087 ***
publictransport:employment.student_primary     -0.462221    0.168824   -2.74  0.00618 ** 
cardriver:employment.student_secondary         -1.536725    0.088282  -17.41  < 2e-16 ***
carpassenger:employment.student_secondary       1.321489    0.077424   17.07  < 2e-16 ***
cycling:employment.student_secondary            0.750966    0.083739    8.97  < 2e-16 ***
publictransport:employment.student_secondary    0.456290    0.078126    5.84  5.2e-09 ***
cardriver:employment.student_tertiary           0.326263    0.153227    2.13  0.03323 *  
carpassenger:employment.student_tertiary        0.592119    0.182427    3.25  0.00117 ** 
cycling:employment.student_tertiary             0.286721    0.206988    1.39  0.16599    
publictransport:employment.student_tertiary     0.369454    0.146381    2.52  0.01161 *  
cardriver:employment.other                     -0.338614    0.121646   -2.78  0.00538 ** 
carpassenger:employment.other                   0.749264    0.139082    5.39  7.2e-08 ***
cycling:employment.other                       -0.404281    0.213101   -1.90  0.05781 .  
publictransport:employment.other               -0.118414    0.149903   -0.79  0.42956    
cardriver:purpose.business                      0.379260    0.146554    2.59  0.00966 ** 
carpassenger:purpose.business                  -0.016297    0.247417   -0.07  0.94748    
cycling:purpose.business                       -0.665148    0.250845   -2.65  0.00801 ** 
publictransport:purpose.business               -0.417702    0.184997   -2.26  0.02395 *  
cardriver:purpose.education                    -1.040796    0.104535   -9.96  < 2e-16 ***
carpassenger:purpose.education                 -0.226472    0.099655   -2.27  0.02305 *  
cycling:purpose.education                      -0.767865    0.102749   -7.47  7.8e-14 ***
publictransport:purpose.education              -0.219095    0.090982   -2.41  0.01604 *  
cardriver:purpose.service                       0.651127    0.157831    4.13  3.7e-05 ***
carpassenger:purpose.service                    0.985946    0.227029    4.34  1.4e-05 ***
cycling:purpose.service                        -0.161189    0.247671   -0.65  0.51516    
publictransport:purpose.service                -0.029250    0.228518   -0.13  0.89815    
cardriver:purpose.private_business             -0.174444    0.063937   -2.73  0.00637 ** 
carpassenger:purpose.private_business           1.209615    0.091446   13.23  < 2e-16 ***
cycling:purpose.private_business               -0.789991    0.103040   -7.67  1.8e-14 ***
publictransport:purpose.private_business       -1.178430    0.085463  -13.79  < 2e-16 ***
cardriver:purpose.visit                        -0.191292    0.072104   -2.65  0.00798 ** 
carpassenger:purpose.visit                      1.160665    0.095496   12.15  < 2e-16 ***
cycling:purpose.visit                          -0.891220    0.113564   -7.85  4.2e-15 ***
publictransport:purpose.visit                  -1.315502    0.097056  -13.55  < 2e-16 ***
cardriver:purpose.shopping_grocery             -0.190893    0.060305   -3.17  0.00155 ** 
carpassenger:purpose.shopping_grocery           1.080638    0.090041   12.00  < 2e-16 ***
cycling:purpose.shopping_grocery               -0.387316    0.089548   -4.33  1.5e-05 ***
publictransport:purpose.shopping_grocery       -1.904226    0.090009  -21.16  < 2e-16 ***
cardriver:purpose.shopping_other                0.003050    0.081826    0.04  0.97026    
carpassenger:purpose.shopping_other             1.574924    0.105555   14.92  < 2e-16 ***
cycling:purpose.shopping_other                 -0.544436    0.132328   -4.11  3.9e-05 ***
publictransport:purpose.shopping_other         -0.630401    0.100461   -6.28  3.5e-10 ***
cardriver:purpose.leisure_indoors              -0.923480    0.079464  -11.62  < 2e-16 ***
carpassenger:purpose.leisure_indoors            1.320436    0.099875   13.22  < 2e-16 ***
cycling:purpose.leisure_indoors                -1.544788    0.147939  -10.44  < 2e-16 ***
publictransport:purpose.leisure_indoors        -0.744276    0.092949   -8.01  1.1e-15 ***
cardriver:purpose.leisure_outdoors              0.180305    0.072403    2.49  0.01276 *  
carpassenger:purpose.leisure_outdoors           1.733137    0.094712   18.30  < 2e-16 ***
cycling:purpose.leisure_outdoors               -0.268979    0.103855   -2.59  0.00960 ** 
publictransport:purpose.leisure_outdoors       -1.162941    0.097363  -11.94  < 2e-16 ***
cardriver:purpose.leisure_other                -0.445787    0.070086   -6.36  2.0e-10 ***
carpassenger:purpose.leisure_other              1.147350    0.093617   12.26  < 2e-16 ***
cycling:purpose.leisure_other                  -0.835584    0.105292   -7.94  2.0e-15 ***
publictransport:purpose.leisure_other          -0.906496    0.087511  -10.36  < 2e-16 ***
cardriver:purpose.leisure_walk                 -1.993808    0.205804   -9.69  < 2e-16 ***
carpassenger:purpose.leisure_walk              -0.222341    0.251107   -0.89  0.37592    
cycling:purpose.leisure_walk                   -1.199915    0.324928   -3.69  0.00022 ***
publictransport:purpose.leisure_walk           -2.193662    0.252129   -8.70  < 2e-16 ***
cardriver:purpose.other                        -0.197341    0.341362   -0.58  0.56320    
carpassenger:purpose.other                      0.781723    0.385555    2.03  0.04261 *  
cycling:purpose.other                          -1.326588    0.660641   -2.01  0.04464 *  
publictransport:purpose.other                  -0.339889    0.439013   -0.77  0.43880    
cardriver:day.Saturday                          0.066877    0.043501    1.54  0.12421    
carpassenger:day.Saturday                       0.404798    0.049760    8.13  4.4e-16 ***
cycling:day.Saturday                           -0.125620    0.071444   -1.76  0.07870 .  
publictransport:day.Saturday                    0.051258    0.062150    0.82  0.40951    
cardriver:day.Sunday                           -0.402450    0.052904   -7.61  2.8e-14 ***
carpassenger:day.Sunday                         0.054503    0.057512    0.95  0.34329    
cycling:day.Sunday                             -0.398830    0.086719   -4.60  4.2e-06 ***
publictransport:day.Sunday                     -0.581350    0.076609   -7.59  3.2e-14 ***
cardriver:containsService.TRUE                  0.022312    0.153762    0.15  0.88462    
carpassenger:containsService.TRUE              -0.105345    0.218250   -0.48  0.62932    
cycling:containsService.TRUE                   -0.721290    0.231575   -3.11  0.00184 ** 
publictransport:containsService.TRUE           -1.597372    0.207183   -7.71  1.3e-14 ***
cardriver:containsStrolling.TRUE               -0.376586    0.192546   -1.96  0.05049 .  
carpassenger:containsStrolling.TRUE            -0.077215    0.229681   -0.34  0.73673    
cycling:containsStrolling.TRUE                 -0.179107    0.312842   -0.57  0.56697    
publictransport:containsStrolling.TRUE          0.288337    0.225181    1.28  0.20038    
cardriver:mode_before.cycling                   0.631176    0.072890    8.66  < 2e-16 ***
carpassenger:mode_before.cycling                0.785452    0.081796    9.60  < 2e-16 ***
cycling:mode_before.cycling                     3.025817    0.067660   44.72  < 2e-16 ***
publictransport:mode_before.cycling             0.490545    0.088709    5.53  3.2e-08 ***
cardriver:mode_before.publictransport           0.007665    0.054682    0.14  0.88852    
carpassenger:mode_before.publictransport        0.272830    0.057226    4.77  1.9e-06 ***
cycling:mode_before.publictransport             0.441537    0.075926    5.82  6.0e-09 ***
publictransport:mode_before.publictransport     1.370165    0.052013   26.34  < 2e-16 ***
cardriver:mode_before.cardriver                 1.461118    0.035808   40.80  < 2e-16 ***
carpassenger:mode_before.cardriver              0.509019    0.048623   10.47  < 2e-16 ***
cycling:mode_before.cardriver                   0.597649    0.063810    9.37  < 2e-16 ***
publictransport:mode_before.cardriver           0.138503    0.053527    2.59  0.00967 ** 
cardriver:mode_before.carpassenger              0.339675    0.050225    6.76  1.4e-11 ***
carpassenger:mode_before.carpassenger           1.539453    0.047578   32.36  < 2e-16 ***
cycling:mode_before.carpassenger                0.736056    0.072024   10.22  < 2e-16 ***
publictransport:mode_before.carpassenger        0.476189    0.058593    8.13  4.4e-16 ***
cardriver:mode_before.other                     0.029953    0.690053    0.04  0.96538    
carpassenger:mode_before.other                  0.409006    0.798058    0.51  0.60830    
cycling:mode_before.other                       0.114630    1.077453    0.11  0.91527    
publictransport:mode_before.other              -0.643228    1.001944   -0.64  0.52089    
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -48800
McFadden R^2:  0.412 
Likelihood ratio test : chisq = 68500 (p.value = <2e-16)

		*/


	


	

	
	public TourModeChoiceParameterTimeCostByEmpSexPreviousModeWithoutInitial() {
		
		cardriver                                      = 0.448675;
		carpassenger                                  = -3.423535;
		cycling                                       = -1.978812;
		publictransport                                = 0.181737;
		time_employment_fulltime                       = -0.025301;
		time_employment_parttime                       = -0.021652;
		time_employment_unemployed                     = -0.011522;
		time_employment_apprentice                     = -0.022718;
		time_employment_homekeeper                     = -0.026392;
		time_employment_retired                        = -0.023918;
		time_employment_pupil                = -0.030824;
//		time_employment_student_secondary              = -0.024631;
		time_employment_student               = -0.016808;
		time_employment_other                          = -0.015567;
		cost_employment_fulltime                       = -0.399061;
		cost_employment_parttime                       = -0.285636;
		cost_employment_unemployed                     = -0.257929;
		cost_employment_apprentice                     = -0.444739;
		cost_employment_homekeeper                     = -0.184190;
		cost_employment_retired                        = -0.274337;
		cost_employment_pupil                = -0.918705;
//		cost_employment_student_secondary              = -0.724934;
		cost_employment_student               = -0.260184;
		cost_employment_other                          = -0.127494;
		time_sex_female                                = -0.007022;
		cost_sex_female                                = -0.052519;
		cardriver_intrazonal                           = -1.307693;
		carpassenger_intrazonal                        = -1.176718;
		cycling_intrazonal                              = 0.014674;
		publictransport_intrazonal                     = -3.368146;
		cardriver_sex_female                           = -0.650895;
		carpassenger_sex_female                         = 0.304659;
		cycling_sex_female                             = -0.591442;
		publictransport_sex_female                      = 0.012701;
		cardriver_employment_parttime                   = 0.153818;
		carpassenger_employment_parttime                = 0.233533;
		cycling_employment_parttime                     = 0.401459;
		publictransport_employment_parttime            = -0.179893;
		cardriver_employment_unemployed                = -0.145977;
		carpassenger_employment_unemployed              = 0.385160;
		cycling_employment_unemployed                  = -0.482111;
		publictransport_employment_unemployed           = 0.127067;
		cardriver_employment_apprentice                 = 0.355751;
		carpassenger_employment_apprentice              = 0.871813;
		cycling_employment_apprentice                  = -0.069060;
		publictransport_employment_apprentice           = 0.579479;
		cardriver_employment_homekeeper                = -0.165244;
		carpassenger_employment_homekeeper              = 0.068925;
		cycling_employment_homekeeper                   = 0.243801;
		publictransport_employment_homekeeper          = -0.443203;
		cardriver_employment_retired                   = -0.185328;
		carpassenger_employment_retired                 = 0.247928;
		cycling_employment_retired                     = -0.380935;
		publictransport_employment_retired              = 0.187217;
		cardriver_employment_pupil          = -21.850678;
		carpassenger_employment_pupil         = 1.261953;
		cycling_employment_pupil             = -0.424715;
		publictransport_employment_pupil     = -0.462221;
//		cardriver_employment_student_secondary         = -1.536725;
//		carpassenger_employment_student_secondary       = 1.321489;
//		cycling_employment_student_secondary            = 0.750966;
//		publictransport_employment_student_secondary    = 0.456290;
		cardriver_employment_student           = 0.326263;
		carpassenger_employment_student        = 0.592119;
		cycling_employment_student             = 0.286721;
		publictransport_employment_student     = 0.369454;
		cardriver_employment_other                     = -0.338614;
		carpassenger_employment_other                   = 0.749264;
		cycling_employment_other                       = -0.404281;
		publictransport_employment_other               = -0.118414;
		cardriver_purpose_business                      = 0.379260;
		carpassenger_purpose_business                  = -0.016297;
		cycling_purpose_business                       = -0.665148;
		publictransport_purpose_business               = -0.417702;
		cardriver_purpose_education                    = -1.040796;
		carpassenger_purpose_education                 = -0.226472;
		cycling_purpose_education                      = -0.767865;
		publictransport_purpose_education              = -0.219095;
		cardriver_purpose_service                       = 0.651127;
		carpassenger_purpose_service                    = 0.985946;
		cycling_purpose_service                        = -0.161189;
		publictransport_purpose_service                = -0.029250;
		cardriver_purpose_private_business             = -0.174444;
		carpassenger_purpose_private_business           = 1.209615;
		cycling_purpose_private_business               = -0.789991;
		publictransport_purpose_private_business       = -1.178430;
		cardriver_purpose_visit                        = -0.191292;
		carpassenger_purpose_visit                      = 1.160665;
		cycling_purpose_visit                          = -0.891220;
		publictransport_purpose_visit                  = -1.315502;
		cardriver_purpose_shopping_grocery             = -0.190893;
		carpassenger_purpose_shopping_grocery           = 1.080638;
		cycling_purpose_shopping_grocery               = -0.387316;
		publictransport_purpose_shopping_grocery       = -1.904226;
		cardriver_purpose_shopping_other                = 0.003050;
		carpassenger_purpose_shopping_other             = 1.574924;
		cycling_purpose_shopping_other                 = -0.544436;
		publictransport_purpose_shopping_other         = -0.630401;
		cardriver_purpose_leisure_indoors              = -0.923480;
		carpassenger_purpose_leisure_indoors            = 1.320436;
		cycling_purpose_leisure_indoors                = -1.544788;
		publictransport_purpose_leisure_indoors        = -0.744276;
		cardriver_purpose_leisure_outdoors              = 0.180305;
		carpassenger_purpose_leisure_outdoors           = 1.733137;
		cycling_purpose_leisure_outdoors               = -0.268979;
		publictransport_purpose_leisure_outdoors       = -1.162941;
		cardriver_purpose_leisure_other                = -0.445787;
		carpassenger_purpose_leisure_other              = 1.147350;
		cycling_purpose_leisure_other                  = -0.835584;
		publictransport_purpose_leisure_other          = -0.906496;
		cardriver_purpose_leisure_walk                 = -1.993808;
		carpassenger_purpose_leisure_walk              = -0.222341;
		cycling_purpose_leisure_walk                   = -1.199915;
		publictransport_purpose_leisure_walk           = -2.193662;
		cardriver_purpose_other                        = -0.197341;
		carpassenger_purpose_other                      = 0.781723;
		cycling_purpose_other                          = -1.326588;
		publictransport_purpose_other                  = -0.339889;
		cardriver_day_Saturday                          = 0.066877;
		carpassenger_day_Saturday                       = 0.404798;
		cycling_day_Saturday                           = -0.125620;
		publictransport_day_Saturday                    = 0.051258;
		cardriver_day_Sunday                           = -0.402450;
		carpassenger_day_Sunday                         = 0.054503;
		cycling_day_Sunday                             = -0.398830;
		publictransport_day_Sunday                     = -0.581350;
		cardriver_containsService                       = 0.022312;
		carpassenger_containsService                   = -0.105345;
		cycling_containsService                        = -0.721290;
		publictransport_containsService                = -1.597372;
		cardriver_containsStrolling                    = -0.376586;
		carpassenger_containsStrolling                 = -0.077215;
		cycling_containsStrolling                      = -0.179107;
		publictransport_containsStrolling               = 0.288337;
		cardriver_mode_before_cycling                   = 0.631176;
		carpassenger_mode_before_cycling                = 0.785452;
		cycling_mode_before_cycling                     = 3.025817;
		publictransport_mode_before_cycling             = 0.490545;
		cardriver_mode_before_publictransport           = 0.007665;
		carpassenger_mode_before_publictransport        = 0.272830;
		cycling_mode_before_publictransport             = 0.441537;
		publictransport_mode_before_publictransport     = 1.370165;
		cardriver_mode_before_cardriver                 = 1.461118;
		carpassenger_mode_before_cardriver              = 0.509019;
		cycling_mode_before_cardriver                   = 0.597649;
		publictransport_mode_before_cardriver           = 0.138503;
		cardriver_mode_before_carpassenger              = 0.339675;
		carpassenger_mode_before_carpassenger           = 1.539453;
		cycling_mode_before_carpassenger                = 0.736056;
		publictransport_mode_before_carpassenger        = 0.476189;

		init();
	}

	
	protected void init() {
		
		super.init();

		
		this.parameterWalk.put("PREVIOUS_MODE_WALK", 			walking_mode_before_walking);
		this.parameterWalk.put("PREVIOUS_MODE_BIKE", 			walking_mode_before_cycling);
		this.parameterWalk.put("PREVIOUS_MODE_CAR", 			walking_mode_before_cardriver);
		this.parameterWalk.put("PREVIOUS_MODE_PASSENGER", walking_mode_before_carpassenger);
		this.parameterWalk.put("PREVIOUS_MODE_PT", 				walking_mode_before_publictransport);
		
		this.parameterBike.put("PREVIOUS_MODE_WALK", 				cycling_mode_before_walking);
		this.parameterBike.put("PREVIOUS_MODE_BIKE", 				cycling_mode_before_cycling);
		this.parameterBike.put("PREVIOUS_MODE_CAR", 				cycling_mode_before_cardriver);
		this.parameterBike.put("PREVIOUS_MODE_PASSENGER", 	cycling_mode_before_carpassenger);
		this.parameterBike.put("PREVIOUS_MODE_PT", 					cycling_mode_before_publictransport);
		
		this.parameterCar.put("PREVIOUS_MODE_WALK", 				cardriver_mode_before_walking);
		this.parameterCar.put("PREVIOUS_MODE_BIKE", 				cardriver_mode_before_cycling);
		this.parameterCar.put("PREVIOUS_MODE_CAR", 					cardriver_mode_before_cardriver);
		this.parameterCar.put("PREVIOUS_MODE_PASSENGER", 		cardriver_mode_before_carpassenger);
		this.parameterCar.put("PREVIOUS_MODE_PT", 					cardriver_mode_before_publictransport);
		
		this.parameterPassenger.put("PREVIOUS_MODE_WALK", 			carpassenger_mode_before_walking);
		this.parameterPassenger.put("PREVIOUS_MODE_BIKE", 			carpassenger_mode_before_cycling);
		this.parameterPassenger.put("PREVIOUS_MODE_CAR", 				carpassenger_mode_before_cardriver);
		this.parameterPassenger.put("PREVIOUS_MODE_PASSENGER", 	carpassenger_mode_before_carpassenger);
		this.parameterPassenger.put("PREVIOUS_MODE_PT", 				carpassenger_mode_before_publictransport);
		
		this.parameterPt.put("PREVIOUS_MODE_WALK", 			publictransport_mode_before_walking);
		this.parameterPt.put("PREVIOUS_MODE_BIKE", 			publictransport_mode_before_cycling);
		this.parameterPt.put("PREVIOUS_MODE_CAR", 			publictransport_mode_before_cardriver);
		this.parameterPt.put("PREVIOUS_MODE_PASSENGER", publictransport_mode_before_carpassenger);
		this.parameterPt.put("PREVIOUS_MODE_PT", 				publictransport_mode_before_publictransport);
	}





}
