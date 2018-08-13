package edu.kit.ifv.mobitopp.simulation.tour;

public class TourModeChoiceParameterTimeCostByEmpSexSameModeWithoutInitial
	extends TourModeChoiceParameterBase 
	implements TourModeChoiceParameter
{

	protected Double	walking_same_mode_as_before = 0.0;
	
	protected Double	cardriver_same_mode_as_before;
	protected Double	carpassenger_same_mode_as_before;
	protected Double	cycling_same_mode_as_before;
	protected Double	publictransport_same_mode_as_before;
	
	/*

Call:
mlogit(formula = mode ~ time:employment. + cost:employment. + 
    time:sex. + cost:sex. | intrazonal + sex. + employment. + 
    purpose. + day. + containsService. + containsStrolling. + 
    same_mode_as_before, data = trainingdata, reflevel = "walking", 
    method = "nr", print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2827          0.3916          0.1283          0.0559          0.1415 

nr method
21 iterations, 0h:5m:31s 
g'(-H)^-1g = 6.53E-07 
gradient close to zero 

Coefficients :
                                                Estimate  Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                           0.589902    0.057791   10.21  < 2e-16 ***
carpassenger:(intercept)                       -3.136830    0.083517  -37.56  < 2e-16 ***
cycling:(intercept)                            -1.623305    0.066130  -24.55  < 2e-16 ***
publictransport:(intercept)                     0.325493    0.057043    5.71  1.2e-08 ***
time:employment.fulltime                       -0.025548    0.000926  -27.59  < 2e-16 ***
time:employment.parttime                       -0.021769    0.001530  -14.23  < 2e-16 ***
time:employment.unemployed                     -0.011554    0.002581   -4.48  7.6e-06 ***
time:employment.apprentice                     -0.023156    0.003804   -6.09  1.1e-09 ***
time:employment.homekeeper                     -0.026640    0.002419  -11.01  < 2e-16 ***
time:employment.retired                        -0.023944    0.001087  -22.03  < 2e-16 ***
time:employment.student_primary                -0.030478    0.002801  -10.88  < 2e-16 ***
time:employment.student_secondary              -0.024921    0.001388  -17.96  < 2e-16 ***
time:employment.student_tertiary               -0.017038    0.002601   -6.55  5.7e-11 ***
time:employment.other                          -0.015620    0.002750   -5.68  1.3e-08 ***
cost:employment.fulltime                       -0.410013    0.015652  -26.20  < 2e-16 ***
cost:employment.parttime                       -0.295590    0.030870   -9.58  < 2e-16 ***
cost:employment.unemployed                     -0.264012    0.087293   -3.02  0.00249 ** 
cost:employment.apprentice                     -0.457321    0.056068   -8.16  4.4e-16 ***
cost:employment.homekeeper                     -0.189785    0.049989   -3.80  0.00015 ***
cost:employment.retired                        -0.281637    0.022513  -12.51  < 2e-16 ***
cost:employment.student_primary                -0.900658    0.142333   -6.33  2.5e-10 ***
cost:employment.student_secondary              -0.719018    0.046432  -15.49  < 2e-16 ***
cost:employment.student_tertiary               -0.269041    0.049084   -5.48  4.2e-08 ***
cost:employment.other                          -0.136123    0.061109   -2.23  0.02591 *  
time:sex.female                                -0.006794    0.001049   -6.48  9.5e-11 ***
cost:sex.female                                -0.050371    0.021278   -2.37  0.01792 *  
cardriver:intrazonal                           -1.282082    0.034783  -36.86  < 2e-16 ***
carpassenger:intrazonal                        -1.138874    0.044349  -25.68  < 2e-16 ***
cycling:intrazonal                              0.062772    0.046817    1.34  0.17999    
publictransport:intrazonal                     -3.372650    0.095007  -35.50  < 2e-16 ***
cardriver:sex.female                           -0.650307    0.042300  -15.37  < 2e-16 ***
carpassenger:sex.female                         0.296870    0.045859    6.47  9.6e-11 ***
cycling:sex.female                             -0.581408    0.051569  -11.27  < 2e-16 ***
publictransport:sex.female                      0.021820    0.042188    0.52  0.60502    
cardriver:employment.parttime                   0.155245    0.060873    2.55  0.01076 *  
carpassenger:employment.parttime                0.222638    0.077941    2.86  0.00428 ** 
cycling:employment.parttime                     0.384384    0.077627    4.95  7.4e-07 ***
publictransport:employment.parttime            -0.185202    0.070540   -2.63  0.00865 ** 
cardriver:employment.unemployed                -0.175032    0.152866   -1.15  0.25221    
carpassenger:employment.unemployed              0.299956    0.178541    1.68  0.09295 .  
cycling:employment.unemployed                  -0.569363    0.260278   -2.19  0.02870 *  
publictransport:employment.unemployed           0.097862    0.187693    0.52  0.60209    
cardriver:employment.apprentice                 0.328470    0.178751    1.84  0.06612 .  
carpassenger:employment.apprentice              0.858480    0.204554    4.20  2.7e-05 ***
cycling:employment.apprentice                  -0.043807    0.264499   -0.17  0.86845    
publictransport:employment.apprentice           0.571807    0.162571    3.52  0.00044 ***
cardriver:employment.homekeeper                -0.179613    0.076320   -2.35  0.01860 *  
carpassenger:employment.homekeeper              0.019146    0.097311    0.20  0.84403    
cycling:employment.homekeeper                   0.186601    0.107019    1.74  0.08122 .  
publictransport:employment.homekeeper          -0.462983    0.122939   -3.77  0.00017 ***
cardriver:employment.retired                   -0.199522    0.050617   -3.94  8.1e-05 ***
carpassenger:employment.retired                 0.187825    0.067798    2.77  0.00560 ** 
cycling:employment.retired                     -0.429605    0.078591   -5.47  4.6e-08 ***
publictransport:employment.retired              0.169097    0.066032    2.56  0.01044 *  
cardriver:employment.student_primary          -21.892116 2039.907029   -0.01  0.99144    
carpassenger:employment.student_primary         1.132609    0.095275   11.89  < 2e-16 ***
cycling:employment.student_primary             -0.529965    0.123462   -4.29  1.8e-05 ***
publictransport:employment.student_primary     -0.527461    0.168606   -3.13  0.00176 ** 
cardriver:employment.student_secondary         -1.570475    0.087557  -17.94  < 2e-16 ***
carpassenger:employment.student_secondary       1.251605    0.075360   16.61  < 2e-16 ***
cycling:employment.student_secondary            0.700149    0.080728    8.67  < 2e-16 ***
publictransport:employment.student_secondary    0.441448    0.076975    5.73  9.8e-09 ***
cardriver:employment.student_tertiary           0.307150    0.151767    2.02  0.04299 *  
carpassenger:employment.student_tertiary        0.572212    0.181310    3.16  0.00160 ** 
cycling:employment.student_tertiary             0.299903    0.206179    1.45  0.14579    
publictransport:employment.student_tertiary     0.352227    0.145519    2.42  0.01550 *  
cardriver:employment.other                     -0.360353    0.120603   -2.99  0.00281 ** 
carpassenger:employment.other                   0.689691    0.138036    5.00  5.8e-07 ***
cycling:employment.other                       -0.461195    0.211787   -2.18  0.02943 *  
publictransport:employment.other               -0.136759    0.149500   -0.91  0.36031    
cardriver:purpose.business                      0.384247    0.145112    2.65  0.00810 ** 
carpassenger:purpose.business                  -0.018473    0.247775   -0.07  0.94057    
cycling:purpose.business                       -0.662705    0.249172   -2.66  0.00782 ** 
publictransport:purpose.business               -0.413457    0.184256   -2.24  0.02484 *  
cardriver:purpose.education                    -0.997860    0.103923   -9.60  < 2e-16 ***
carpassenger:purpose.education                 -0.181766    0.098969   -1.84  0.06627 .  
cycling:purpose.education                      -0.709249    0.101255   -7.00  2.5e-12 ***
publictransport:purpose.education              -0.165246    0.090487   -1.83  0.06782 .  
cardriver:purpose.service                       0.634420    0.156374    4.06  5.0e-05 ***
carpassenger:purpose.service                    0.983894    0.225919    4.36  1.3e-05 ***
cycling:purpose.service                        -0.162676    0.246043   -0.66  0.50850    
publictransport:purpose.service                -0.040276    0.227916   -0.18  0.85973    
cardriver:purpose.private_business             -0.171446    0.063202   -2.71  0.00667 ** 
carpassenger:purpose.private_business           1.236251    0.091136   13.56  < 2e-16 ***
cycling:purpose.private_business               -0.766855    0.102208   -7.50  6.2e-14 ***
publictransport:purpose.private_business       -1.169437    0.085034  -13.75  < 2e-16 ***
cardriver:purpose.visit                        -0.182288    0.071382   -2.55  0.01066 *  
carpassenger:purpose.visit                      1.192650    0.095115   12.54  < 2e-16 ***
cycling:purpose.visit                          -0.869718    0.112647   -7.72  1.2e-14 ***
publictransport:purpose.visit                  -1.300241    0.096662  -13.45  < 2e-16 ***
cardriver:purpose.shopping_grocery             -0.199469    0.059557   -3.35  0.00081 ***
carpassenger:purpose.shopping_grocery           1.091169    0.089687   12.17  < 2e-16 ***
cycling:purpose.shopping_grocery               -0.373475    0.088477   -4.22  2.4e-05 ***
publictransport:purpose.shopping_grocery       -1.908154    0.089558  -21.31  < 2e-16 ***
cardriver:purpose.shopping_other               -0.006716    0.080998   -0.08  0.93392    
carpassenger:purpose.shopping_other             1.578888    0.105150   15.02  < 2e-16 ***
cycling:purpose.shopping_other                 -0.557264    0.131585   -4.24  2.3e-05 ***
publictransport:purpose.shopping_other         -0.631255    0.099991   -6.31  2.7e-10 ***
cardriver:purpose.leisure_indoors              -0.914042    0.078743  -11.61  < 2e-16 ***
carpassenger:purpose.leisure_indoors            1.362984    0.099475   13.70  < 2e-16 ***
cycling:purpose.leisure_indoors                -1.520778    0.147170  -10.33  < 2e-16 ***
publictransport:purpose.leisure_indoors        -0.734423    0.092451   -7.94  2.0e-15 ***
cardriver:purpose.leisure_outdoors              0.170357    0.071521    2.38  0.01722 *  
carpassenger:purpose.leisure_outdoors           1.755001    0.094249   18.62  < 2e-16 ***
cycling:purpose.leisure_outdoors               -0.271431    0.102889   -2.64  0.00834 ** 
publictransport:purpose.leisure_outdoors       -1.164238    0.096940  -12.01  < 2e-16 ***
cardriver:purpose.leisure_other                -0.434558    0.069346   -6.27  3.7e-10 ***
carpassenger:purpose.leisure_other              1.181821    0.093227   12.68  < 2e-16 ***
cycling:purpose.leisure_other                  -0.811801    0.104400   -7.78  7.5e-15 ***
publictransport:purpose.leisure_other          -0.893940    0.087032  -10.27  < 2e-16 ***
cardriver:purpose.leisure_walk                 -1.983749    0.204340   -9.71  < 2e-16 ***
carpassenger:purpose.leisure_walk              -0.181988    0.250286   -0.73  0.46715    
cycling:purpose.leisure_walk                   -1.140397    0.322545   -3.54  0.00041 ***
publictransport:purpose.leisure_walk           -2.180503    0.250755   -8.70  < 2e-16 ***
cardriver:purpose.other                        -0.211106    0.335860   -0.63  0.52964    
carpassenger:purpose.other                      0.776504    0.379363    2.05  0.04067 *  
cycling:purpose.other                          -1.377000    0.656533   -2.10  0.03596 *  
publictransport:purpose.other                  -0.324167    0.434893   -0.75  0.45603    
cardriver:day.Saturday                          0.078104    0.043148    1.81  0.07028 .  
carpassenger:day.Saturday                       0.420431    0.049440    8.50  < 2e-16 ***
cycling:day.Saturday                           -0.109417    0.070938   -1.54  0.12297    
publictransport:day.Saturday                    0.065346    0.061988    1.05  0.29181    
cardriver:day.Sunday                           -0.388516    0.052468   -7.40  1.3e-13 ***
carpassenger:day.Sunday                         0.062907    0.057023    1.10  0.26995    
cycling:day.Sunday                             -0.377196    0.085909   -4.39  1.1e-05 ***
publictransport:day.Sunday                     -0.561064    0.076447   -7.34  2.1e-13 ***
cardriver:containsService.TRUE                  0.038679    0.152327    0.25  0.79956    
carpassenger:containsService.TRUE              -0.079552    0.217155   -0.37  0.71411    
cycling:containsService.TRUE                   -0.711693    0.230102   -3.09  0.00198 ** 
publictransport:containsService.TRUE           -1.581106    0.206538   -7.66  1.9e-14 ***
cardriver:containsStrolling.TRUE               -0.368886    0.191212   -1.93  0.05371 .  
carpassenger:containsStrolling.TRUE            -0.070982    0.228934   -0.31  0.75652    
cycling:containsStrolling.TRUE                 -0.181022    0.310731   -0.58  0.56018    
publictransport:containsStrolling.TRUE          0.290612    0.223822    1.30  0.19415    
cardriver:same_mode_as_beforeTRUE               1.242337    0.024543   50.62  < 2e-16 ***
carpassenger:same_mode_as_beforeTRUE            1.110718    0.034105   32.57  < 2e-16 ***
cycling:same_mode_as_beforeTRUE                 2.389388    0.045399   52.63  < 2e-16 ***
publictransport:same_mode_as_beforeTRUE         1.248661    0.036314   34.39  < 2e-16 ***
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -49000
McFadden R^2:  0.41 
Likelihood ratio test : chisq = 68200 (p.value = <2e-16)

		*/


	
	
	public TourModeChoiceParameterTimeCostByEmpSexSameModeWithoutInitial() {
	
		walking																				 = 0.0				- 1.5;
		walking_intrazonal														 = 0.0				+ 1.5;
		walking_day_Saturday                           = 0.0				- 0.2;
		walking_day_Sunday                           	 = 0.0				- 0.2;
		
		
		cardriver                                      = 0.589902		- 0.3;
		carpassenger                                   = -3.136830;
		cycling                                        = -1.623305;
		publictransport                                = 0.325493;
		
		time_employment_fulltime                       = -0.025548;
		time_employment_parttime                       = -0.021769;
		time_employment_unemployed                     = -0.011554;
		time_employment_apprentice                     = -0.023156;
		time_employment_homekeeper                     = -0.026640;
		time_employment_retired                        = -0.023944;
		time_employment_pupil                = -0.030478;
//		time_employment_student_secondary              = -0.024921;
		time_employment_student               = -0.017038;
		time_employment_other                          = -0.015620;
		
		cost_employment_fulltime                       = -0.410013;
		cost_employment_parttime                       = -0.295590;
		cost_employment_unemployed                     = -0.264012;
		cost_employment_apprentice                     = -0.457321;
		cost_employment_homekeeper                     = -0.189785;
		cost_employment_retired                        = -0.281637;
		cost_employment_pupil                = -0.900658;
//		cost_employment_student_secondary              = -0.719018;
		cost_employment_student               = -0.269041;
		cost_employment_other                          = -0.136123;
		
		time_sex_female                                = -0.006794;
		cost_sex_female                                = -0.050371;
		
		cardriver_intrazonal                           = -1.282082	+ 1.0;
		carpassenger_intrazonal                        = -1.138874;
		cycling_intrazonal                              = 0.062772;
		publictransport_intrazonal                     = -3.372650	- 5.5;
		
		cardriver_sex_female                           = -0.650307	+ 0.4;
		carpassenger_sex_female                         = 0.296870	+ 0.3;
		cycling_sex_female                             = -0.581408;
		publictransport_sex_female                      = 0.021820;
		
		cardriver_employment_parttime                   = 0.155245;
		carpassenger_employment_parttime                = 0.222638;
		cycling_employment_parttime                     = 0.384384;
		publictransport_employment_parttime            = -0.185202;
		cardriver_employment_unemployed                = -0.175032;
		carpassenger_employment_unemployed              = 0.299956;
		cycling_employment_unemployed                  = -0.569363;
		publictransport_employment_unemployed           = 0.097862;
		cardriver_employment_apprentice                 = 0.328470	+ 0.3;
		carpassenger_employment_apprentice              = 0.858480	- 0.2;
		cycling_employment_apprentice                  = -0.043807;
		publictransport_employment_apprentice           = 0.571807;
		cardriver_employment_homekeeper                = -0.179613;
		carpassenger_employment_homekeeper              = 0.019146;
		cycling_employment_homekeeper                   = 0.186601;
		publictransport_employment_homekeeper          = -0.462983;
		cardriver_employment_retired                   = -0.199522;
		carpassenger_employment_retired                 = 0.187825;
		cycling_employment_retired                     = -0.429605;
		publictransport_employment_retired              = 0.169097;
		cardriver_employment_pupil          = -21.892116;
		carpassenger_employment_pupil         = 1.132609;
		cycling_employment_pupil             = -0.529965;
		publictransport_employment_pupil     = -0.527461;
//		cardriver_employment_student_secondary         = -1.570475	+ 0.4;
//		carpassenger_employment_student_secondary       = 1.251605;
//		cycling_employment_student_secondary            = 0.700149;
//		publictransport_employment_student_secondary    = 0.441448	+ 0.1;
		cardriver_employment_student           = 0.307150;
		carpassenger_employment_student        = 0.572212 	- 0.3;
		cycling_employment_student             = 0.299903;
		publictransport_employment_student     = 0.352227	+ 0.2;
		cardriver_employment_other                     = -0.360353;
		carpassenger_employment_other                   = 0.689691;
		cycling_employment_other                       = -0.461195;
		publictransport_employment_other               = -0.136759;
		
		cardriver_purpose_business                      = 0.384247;
		carpassenger_purpose_business                  = -0.018473;
		cycling_purpose_business                       = -0.662705;
		publictransport_purpose_business               = -0.413457;
		cardriver_purpose_education                    = -0.997860;
		carpassenger_purpose_education                 = -0.181766;
		cycling_purpose_education                      = -0.709249;
		publictransport_purpose_education              = -0.165246;
		cardriver_purpose_service                       = 0.634420;
		carpassenger_purpose_service                    = 0.983894;
		cycling_purpose_service                        = -0.162676;
		publictransport_purpose_service                = -0.040276;
		cardriver_purpose_private_business             = -0.171446;
		carpassenger_purpose_private_business           = 1.236251;
		cycling_purpose_private_business               = -0.766855;
		publictransport_purpose_private_business       = -1.169437;
		cardriver_purpose_visit                        = -0.182288;
		carpassenger_purpose_visit                      = 1.192650;
		cycling_purpose_visit                          = -0.869718;
		publictransport_purpose_visit                  = -1.300241;
		cardriver_purpose_shopping_grocery             = -0.199469;
		carpassenger_purpose_shopping_grocery           = 1.091169;
		cycling_purpose_shopping_grocery               = -0.373475;
		publictransport_purpose_shopping_grocery       = -1.908154;
		cardriver_purpose_shopping_other               = -0.006716;
		carpassenger_purpose_shopping_other             = 1.578888;
		cycling_purpose_shopping_other                 = -0.557264;
		publictransport_purpose_shopping_other         = -0.631255;
		cardriver_purpose_leisure_indoors              = -0.914042;
		carpassenger_purpose_leisure_indoors            = 1.362984;
		cycling_purpose_leisure_indoors                = -1.520778;
		publictransport_purpose_leisure_indoors        = -0.734423;
		cardriver_purpose_leisure_outdoors              = 0.170357;
		carpassenger_purpose_leisure_outdoors           = 1.755001;
		cycling_purpose_leisure_outdoors               = -0.271431;
		publictransport_purpose_leisure_outdoors       = -1.164238;
		cardriver_purpose_leisure_other                = -0.434558;
		carpassenger_purpose_leisure_other              = 1.181821;
		cycling_purpose_leisure_other                  = -0.811801;
		publictransport_purpose_leisure_other          = -0.893940;
		cardriver_purpose_leisure_walk                 = -1.983749;
		carpassenger_purpose_leisure_walk              = -0.181988;
		cycling_purpose_leisure_walk                   = -1.140397;
		publictransport_purpose_leisure_walk           = -2.180503;
		cardriver_purpose_other                        = -0.211106;
		carpassenger_purpose_other                      = 0.776504;
		cycling_purpose_other                          = -1.377000;
		publictransport_purpose_other                  = -0.324167;
		
		cardriver_day_Saturday                          = 0.078104	- 0.1;
		carpassenger_day_Saturday                       = 0.420431	+	0.3;
		cycling_day_Saturday                           = -0.109417;
		publictransport_day_Saturday                    = 0.065346;
		cardriver_day_Sunday                           = -0.388516	- 0.1;
		carpassenger_day_Sunday                         = 0.062907	+ 0.3;
		cycling_day_Sunday                             = -0.377196;
		publictransport_day_Sunday                     = -0.561064;
		
		cardriver_containsService                       = 0.038679;
		carpassenger_containsService                   = -0.079552;
		cycling_containsService                        = -0.711693;
		publictransport_containsService                = -1.581106;
		cardriver_containsStrolling                    = -0.368886;
		carpassenger_containsStrolling                 = -0.070982;
		cycling_containsStrolling                      = -0.181022;
		publictransport_containsStrolling               = 0.290612;
		
		cardriver_same_mode_as_before                   = 1.242337;
		carpassenger_same_mode_as_before                = 1.110718;
		cycling_same_mode_as_before                     = 2.389388;
		publictransport_same_mode_as_before             = 1.248661;
	
		init();
	}

	protected void init() {
		
		super.init();

		this.parameterWalk.put("SAME_MODE_AS_BEFORE", walking_same_mode_as_before);
		this.parameterBike.put("SAME_MODE_AS_BEFORE", cycling_same_mode_as_before);
		this.parameterCar.put("SAME_MODE_AS_BEFORE", cardriver_same_mode_as_before);
		this.parameterPassenger.put("SAME_MODE_AS_BEFORE", carpassenger_same_mode_as_before);
		this.parameterPt.put("SAME_MODE_AS_BEFORE", publictransport_same_mode_as_before);
		
	}



}
