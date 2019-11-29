package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;

public class WithinTourModeChoiceParameterOnlyFlexibleModesEmpAgeSexPurposeTourmodeWithintour 
	extends WithinTourModeChoiceParameterOnlyFlexibleModesBase 
	implements WithinTourModeChoiceParameter 
{
	
	protected Double walking                                	= 0.0 ;
	protected Double walking_istourmode                     	= 0.0 ;
	protected Double walking_withintour        						   	= 0.0 ;
	protected Double walking_istourmode_withintour           	= 0.0 ;
	
	protected Double walking_sex_female 											= 0.0 ;
	
	protected Double walking_age_06to09 						       	= 0.0 ;
	protected Double walking_age_10to17 						       	= 0.0 ;
	protected Double walking_age_18to25 						       	= 0.0 ;
	protected Double walking_age_26to35 						       	= 0.0 ;
	protected Double walking_age_36to50 						       	= 0.0 ;
	protected Double walking_age_51to60 						       	= 0.0 ;
	protected Double walking_age_61to70 						       	= 0.0 ;
	protected Double walking_age_71plus 						       	= 0.0 ;
	
	protected Double publictransport_age_36to50 						= 0.0 ;
	protected Double carpassenger_age_36to50 						   	= 0.0 ;
	
	protected Double publictransport_employment_fulltime		= 0.0 ;
	protected Double carpassenger_employment_fulltime			 	= 0.0 ;
	


	/*
	
Call:
mlogit(formula = mode ~ time + cost | istourmode * withintour. + 
    purpose. + tourpurpose. + day. + intrazonal. + sex. + age. + 
    employment., data = trainingdata, reflevel = "walking", method = "nr", 
    print.level = 0)

Frequencies of alternatives:
        walking    carpassenger publictransport 
          0.514           0.241           0.245 

nr method
7 iterations, 0h:0m:19s 
g'(-H)^-1g = 2.43E-07 
gradient close to zero 

Coefficients :
                                                Estimate Std. Error t-value Pr(>|t|)    
carpassenger:(intercept)                       -2.905742   1.252102   -2.32  0.02030 *  
publictransport:(intercept)                    -0.807478   2.146332   -0.38  0.70676    
time                                           -0.032999   0.000858  -38.47  < 2e-16 ***
cost                                           -0.257359   0.024367  -10.56  < 2e-16 ***
carpassenger:istourmodeTRUE                     4.363345   0.059056   73.89  < 2e-16 ***
publictransport:istourmodeTRUE                  3.344919   0.062077   53.88  < 2e-16 ***
carpassenger:withintour.TRUE                   -0.779424   1.221412   -0.64  0.52339    
publictransport:withintour.TRUE                 0.221744   2.137891    0.10  0.91739    
carpassenger:purpose.business                   1.085972   0.439921    2.47  0.01357 *  
publictransport:purpose.business               -0.536600   0.351973   -1.52  0.12737    
carpassenger:purpose.education                 -0.339786   0.388981   -0.87  0.38238    
publictransport:purpose.education              -0.798580   0.310501   -2.57  0.01011 *  
carpassenger:purpose.service                    0.954546   0.358023    2.67  0.00767 ** 
publictransport:purpose.service                -0.260869   0.290661   -0.90  0.36945    
carpassenger:purpose.private_business           0.618572   0.323202    1.91  0.05563 .  
publictransport:purpose.private_business       -0.489424   0.247172   -1.98  0.04769 *  
carpassenger:purpose.visit                      0.445531   0.325246    1.37  0.17074    
publictransport:purpose.visit                  -0.225926   0.256638   -0.88  0.37868    
carpassenger:purpose.shopping_grocery           0.616965   0.307421    2.01  0.04476 *  
publictransport:purpose.shopping_grocery       -0.629363   0.229672   -2.74  0.00614 ** 
carpassenger:purpose.shopping_other            -0.005785   0.321623   -0.02  0.98565    
publictransport:purpose.shopping_other         -0.989130   0.242476   -4.08  4.5e-05 ***
carpassenger:purpose.leisure_indoors           -0.076299   0.323337   -0.24  0.81345    
publictransport:purpose.leisure_indoors        -1.066317   0.247895   -4.30  1.7e-05 ***
carpassenger:purpose.leisure_outdoors           0.534972   0.348695    1.53  0.12498    
publictransport:purpose.leisure_outdoors       -0.635516   0.278858   -2.28  0.02267 *  
carpassenger:purpose.leisure_other             -0.121054   0.323673   -0.37  0.70840    
publictransport:purpose.leisure_other          -0.894895   0.248593   -3.60  0.00032 ***
carpassenger:purpose.leisure_walk              -2.566861   0.427853   -6.00  2.0e-09 ***
publictransport:purpose.leisure_walk           -3.867406   0.617327   -6.26  3.7e-10 ***
carpassenger:purpose.other                     -1.019327   1.252429   -0.81  0.41571    
publictransport:purpose.other                  -1.187434   2.146363   -0.55  0.58011    
carpassenger:tourpurpose.business               0.322773   0.321010    1.01  0.31466    
publictransport:tourpurpose.business           -0.002727   0.279771   -0.01  0.99222    
carpassenger:tourpurpose.education             -0.553409   0.132169   -4.19  2.8e-05 ***
publictransport:tourpurpose.education          -0.027310   0.125254   -0.22  0.82740    
carpassenger:tourpurpose.service                0.147225   0.139993    1.05  0.29296    
publictransport:tourpurpose.service            -0.572481   0.140420   -4.08  4.6e-05 ***
carpassenger:tourpurpose.private_business       0.061953   0.129858    0.48  0.63330    
publictransport:tourpurpose.private_business   -0.302757   0.121893   -2.48  0.01300 *  
carpassenger:tourpurpose.visit                  1.000082   0.133802    7.47  7.7e-14 ***
publictransport:tourpurpose.visit              -0.661021   0.148706   -4.45  8.8e-06 ***
carpassenger:tourpurpose.shopping_grocery       0.356314   0.124385    2.86  0.00418 ** 
publictransport:tourpurpose.shopping_grocery   -0.636449   0.124255   -5.12  3.0e-07 ***
carpassenger:tourpurpose.shopping_other         0.357929   0.154070    2.32  0.02017 *  
publictransport:tourpurpose.shopping_other      0.070159   0.143954    0.49  0.62600    
carpassenger:tourpurpose.leisure_indoors        0.588573   0.136922    4.30  1.7e-05 ***
publictransport:tourpurpose.leisure_indoors    -0.373839   0.132562   -2.82  0.00480 ** 
carpassenger:tourpurpose.leisure_outdoors       0.878494   0.133314    6.59  4.4e-11 ***
publictransport:tourpurpose.leisure_outdoors   -0.515972   0.143862   -3.59  0.00034 ***
carpassenger:tourpurpose.leisure_other          0.272633   0.129198    2.11  0.03484 *  
publictransport:tourpurpose.leisure_other      -0.300088   0.128510   -2.34  0.01954 *  
carpassenger:tourpurpose.leisure_walk          -0.435655   0.147847   -2.95  0.00321 ** 
publictransport:tourpurpose.leisure_walk       -0.852200   0.167536   -5.09  3.6e-07 ***
carpassenger:tourpurpose.other                  0.469799   0.505473    0.93  0.35267    
publictransport:tourpurpose.other               1.290031   0.712379    1.81  0.07016 .  
carpassenger:day.Saturday                       0.005424   0.073937    0.07  0.94152    
publictransport:day.Saturday                   -0.040417   0.087543   -0.46  0.64431    
carpassenger:day.Sunday                         0.036056   0.086049    0.42  0.67520    
publictransport:day.Sunday                     -0.198869   0.109722   -1.81  0.06991 .  
carpassenger:intrazonal.TRUE                   -0.665743   0.057710  -11.54  < 2e-16 ***
publictransport:intrazonal.TRUE                -2.365574   0.100253  -23.60  < 2e-16 ***
carpassenger:sex.female                         0.371080   0.055416    6.70  2.1e-11 ***
publictransport:sex.female                      0.071759   0.056003    1.28  0.20008    
carpassenger:age.06to09                         1.181254   0.257258    4.59  4.4e-06 ***
publictransport:age.06to09                     -0.639704   0.267365   -2.39  0.01673 *  
carpassenger:age.10to17                         1.227233   0.250774    4.89  9.9e-07 ***
publictransport:age.10to17                      0.369691   0.243461    1.52  0.12889    
carpassenger:age.18to25                         0.801170   0.204810    3.91  9.2e-05 ***
publictransport:age.18to25                      0.497920   0.202178    2.46  0.01379 *  
carpassenger:age.26to35                         0.082292   0.117918    0.70  0.48526    
publictransport:age.26to35                      0.179293   0.106480    1.68  0.09222 .  
carpassenger:age.51to60                         0.324748   0.097901    3.32  0.00091 ***
publictransport:age.51to60                      0.031409   0.102112    0.31  0.75839    
carpassenger:age.61to70                         0.256725   0.140966    1.82  0.06858 .  
publictransport:age.61to70                      0.114536   0.154834    0.74  0.45946    
carpassenger:age.71plus                         0.340421   0.162639    2.09  0.03634 *  
publictransport:age.71plus                      0.378080   0.175391    2.16  0.03111 *  
carpassenger:employment.parttime               -0.330888   0.105648   -3.13  0.00174 ** 
publictransport:employment.parttime             0.032415   0.102986    0.31  0.75295    
carpassenger:employment.marginal                0.105428   0.159134    0.66  0.50764    
publictransport:employment.marginal            -0.010671   0.184818   -0.06  0.95396    
carpassenger:employment.homekeeper             -0.276281   0.123884   -2.23  0.02574 *  
publictransport:employment.homekeeper           0.019620   0.144332    0.14  0.89187    
carpassenger:employment.unemployed              0.016078   0.250424    0.06  0.94881    
publictransport:employment.unemployed          -0.008353   0.245430   -0.03  0.97285    
carpassenger:employment.retired                -0.228291   0.150335   -1.52  0.12888    
publictransport:employment.retired              0.027476   0.165355    0.17  0.86803    
carpassenger:employment.pupil                  -0.050483   0.242863   -0.21  0.83533    
publictransport:employment.pupil                0.302640   0.240828    1.26  0.20887    
carpassenger:employment.student                 0.222064   0.245963    0.90  0.36661    
publictransport:employment.student              0.000420   0.225829    0.00  0.99851    
carpassenger:employment.apprentice              0.257205   0.283852    0.91  0.36487    
publictransport:employment.apprentice           0.185417   0.270407    0.69  0.49290    
carpassenger:employment.other                  -0.498883   0.367337   -1.36  0.17443    
publictransport:employment.other                0.140798   0.346521    0.41  0.68451    
carpassenger:istourmodeTRUE:withintour.TRUE    -1.176658   0.110976  -10.60  < 2e-16 ***
publictransport:istourmodeTRUE:withintour.TRUE -2.306945   0.111098  -20.77  < 2e-16 ***
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -11400
McFadden R^2:  0.699 
Likelihood ratio test : chisq = 53200 (p.value = <2e-16)


		*/
	

	protected Double carpassenger                                    = -2.905742;    //se=1.252102
	protected Double publictransport                                 = -0.807478;    //se=2.146332
	
	protected Double time                                            = -0.032999;    //se=0.000858
	protected Double cost                                            = -0.257359;    //se=0.024367
	
	protected Double carpassenger_istourmode                          = 4.363345;    //se=0.059056
	protected Double publictransport_istourmode                       = 3.344919;    //se=0.062077
	
	protected Double carpassenger_withintour                          = -0.779424;    //se=1.221412
	protected Double publictransport_withintour                       = 0.221744;    //se=2.137891
	
	protected Double carpassenger_istourmode_withintour               = -1.176658;    //se=0.110976
	protected Double publictransport_istourmode_withintour            = -2.306945;    //se=0.111098
	
	protected Double carpassenger_purpose_business                    = 1.085972;    //se=0.439921
	protected Double carpassenger_purpose_education                   = -0.339786;    //se=0.388981
	protected Double carpassenger_purpose_leisure_indoors             = -0.076299;    //se=0.323337
	protected Double carpassenger_purpose_leisure_other               = -0.121054;    //se=0.323673
	protected Double carpassenger_purpose_leisure_outdoors            = 0.534972;    //se=0.348695
	protected Double carpassenger_purpose_leisure_walk                = -2.566861;    //se=0.427853
	protected Double carpassenger_purpose_other                       = -1.019327;    //se=1.252429
	protected Double carpassenger_purpose_private_business            = 0.618572;    //se=0.323202
	protected Double carpassenger_purpose_service                     = 0.954546;    //se=0.358023
	protected Double carpassenger_purpose_shopping_grocery            = 0.616965;    //se=0.307421
	protected Double carpassenger_purpose_shopping_other              = -0.005785;    //se=0.321623
	protected Double carpassenger_purpose_visit                       = 0.445531;    //se=0.325246
	protected Double publictransport_purpose_business                 = -0.536600;    //se=0.351973
	protected Double publictransport_purpose_education                = -0.798580;    //se=0.310501
	protected Double publictransport_purpose_leisure_indoors          = -1.066317;    //se=0.247895
	protected Double publictransport_purpose_leisure_other            = -0.894895;    //se=0.248593
	protected Double publictransport_purpose_leisure_outdoors         = -0.635516;    //se=0.278858
	protected Double publictransport_purpose_leisure_walk             = -3.867406;    //se=0.617327
	protected Double publictransport_purpose_other                    = -1.187434;    //se=2.146363
	protected Double publictransport_purpose_private_business         = -0.489424;    //se=0.247172
	protected Double publictransport_purpose_service                  = -0.260869;    //se=0.290661
	protected Double publictransport_purpose_shopping_grocery         = -0.629363;    //se=0.229672
	protected Double publictransport_purpose_shopping_other           = -0.989130;    //se=0.242476
	protected Double publictransport_purpose_visit                    = -0.225926;    //se=0.256638
	
	protected Double carpassenger_tourpurpose_business                = 0.322773;    //se=0.321010
	protected Double carpassenger_tourpurpose_education               = -0.553409;    //se=0.132169
	protected Double carpassenger_tourpurpose_leisure_indoors         = 0.588573;    //se=0.136922
	protected Double carpassenger_tourpurpose_leisure_other           = 0.272633;    //se=0.129198
	protected Double carpassenger_tourpurpose_leisure_outdoors        = 0.878494;    //se=0.133314
	protected Double carpassenger_tourpurpose_leisure_walk            = -0.435655;    //se=0.147847
	protected Double carpassenger_tourpurpose_other                   = 0.469799;    //se=0.505473
	protected Double carpassenger_tourpurpose_private_business        = 0.061953;    //se=0.129858
	protected Double carpassenger_tourpurpose_service                 = 0.147225;    //se=0.139993
	protected Double carpassenger_tourpurpose_shopping_grocery        = 0.356314;    //se=0.124385
	protected Double carpassenger_tourpurpose_shopping_other          = 0.357929;    //se=0.154070
	protected Double carpassenger_tourpurpose_visit                   = 1.000082;    //se=0.133802
	protected Double publictransport_tourpurpose_business             = -0.002727;    //se=0.279771
	protected Double publictransport_tourpurpose_education            = -0.027310;    //se=0.125254
	protected Double publictransport_tourpurpose_leisure_indoors      = -0.373839;    //se=0.132562
	protected Double publictransport_tourpurpose_leisure_other        = -0.300088;    //se=0.128510
	protected Double publictransport_tourpurpose_leisure_outdoors     = -0.515972;    //se=0.143862
	protected Double publictransport_tourpurpose_leisure_walk         = -0.852200;    //se=0.167536
	protected Double publictransport_tourpurpose_other                = 1.290031;    //se=0.712379
	protected Double publictransport_tourpurpose_private_business     = -0.302757;    //se=0.121893
	protected Double publictransport_tourpurpose_service              = -0.572481;    //se=0.140420
	protected Double publictransport_tourpurpose_shopping_grocery     = -0.636449;    //se=0.124255
	protected Double publictransport_tourpurpose_shopping_other       = 0.070159;    //se=0.143954
	protected Double publictransport_tourpurpose_visit                = -0.661021;    //se=0.148706
	
	protected Double carpassenger_day_Saturday                        = 0.005424;    //se=0.073937
	protected Double publictransport_day_Saturday                     = -0.040417;    //se=0.087543
	protected Double carpassenger_day_Sunday                          = 0.036056;    //se=0.086049
	protected Double publictransport_day_Sunday                       = -0.198869;    //se=0.109722
	
	protected Double carpassenger_intrazonal                          = -0.665743;    //se=0.057710
	protected Double publictransport_intrazonal                       = -2.365574;    //se=0.100253
	
	protected Double carpassenger_sex_female                          = 0.371080;    //se=0.055416
	protected Double publictransport_sex_female                       = 0.071759;    //se=0.056003
	
	protected Double carpassenger_age_06to09                          = 1.181254;    //se=0.257258
	protected Double publictransport_age_06to09                      = -0.639704;    //se=0.267365
	protected Double carpassenger_age_10to17                          = 1.227233;    //se=0.250774
	protected Double publictransport_age_10to17                       = 0.369691;    //se=0.243461
	protected Double carpassenger_age_18to25                          = 0.801170;    //se=0.204810
	protected Double publictransport_age_18to25                       = 0.497920;    //se=0.202178
	protected Double carpassenger_age_26to35                          = 0.082292;    //se=0.117918
	protected Double publictransport_age_26to35                       = 0.179293;    //se=0.106480
	protected Double carpassenger_age_51to60                          = 0.324748;    //se=0.097901
	protected Double publictransport_age_51to60                       = 0.031409;    //se=0.102112
	protected Double carpassenger_age_61to70                          = 0.256725;    //se=0.140966
	protected Double publictransport_age_61to70                       = 0.114536;    //se=0.154834
	protected Double carpassenger_age_71plus                          = 0.340421;    //se=0.162639
	protected Double publictransport_age_71plus                       = 0.378080;    //se=0.175391
	
	protected Double carpassenger_employment_parttime                = -0.330888;    //se=0.105648
	protected Double publictransport_employment_parttime              = 0.032415;    //se=0.102986
	protected Double carpassenger_employment_marginal                 = 0.105428;    //se=0.159134
	protected Double publictransport_employment_marginal             = -0.010671;    //se=0.184818
	protected Double carpassenger_employment_homekeeper              = -0.276281;    //se=0.123884
	protected Double publictransport_employment_homekeeper            = 0.019620;    //se=0.144332
	protected Double carpassenger_employment_unemployed               = 0.016078;    //se=0.250424
	protected Double publictransport_employment_unemployed           = -0.008353;    //se=0.245430
	protected Double carpassenger_employment_retired                 = -0.228291;    //se=0.150335
	protected Double publictransport_employment_retired               = 0.027476;    //se=0.165355
	protected Double carpassenger_employment_pupil                   = -0.050483;    //se=0.242863
	protected Double publictransport_employment_pupil                 = 0.302640;    //se=0.240828
	protected Double carpassenger_employment_student                  = 0.222064;    //se=0.245963
	protected Double publictransport_employment_student               = 0.000420;    //se=0.225829
	protected Double carpassenger_employment_apprentice               = 0.257205;    //se=0.283852
	protected Double publictransport_employment_apprentice            = 0.185417;    //se=0.270407
	protected Double carpassenger_employment_other                   = -0.498883;    //se=0.367337
	protected Double publictransport_employment_other                 = 0.140798;    //se=0.346521


	

	
	public WithinTourModeChoiceParameterOnlyFlexibleModesEmpAgeSexPurposeTourmodeWithintour() {
		init();
	}


	protected void init() {

		
		this.parameterGeneric.put("TIME", 					time);
		this.parameterGeneric.put("COST", 					cost);
		
		
		this.parameterWalk.putAll(this.parameterGeneric);
		this.parameterPassenger.putAll(this.parameterGeneric);
		this.parameterPt.putAll(this.parameterGeneric);
		
		// walk
		this.parameterWalk.put("CONST", 	walking);
		this.parameterWalk.put("ISTOURMODE", 	walking_istourmode);
		this.parameterWalk.put("WITHINTOUR", 	walking_withintour);
		this.parameterWalk.put("ISTOURMODE_WITHINTOUR", 	walking_istourmode_withintour);
	
		/*
		this.parameterWalk.put("FEMALE", walking_sex_female);
		this.parameterWalk.put("DAY_SA",  walking_day_Saturday);
		this.parameterWalk.put("DAY_SU",  walking_day_Sunday);
		
		this.parameterWalk.put("AGE_0TO9",  	walking_age_06to09);
		this.parameterWalk.put("AGE_10TO17",  walking_age_10to17);
		this.parameterWalk.put("AGE_18TO25",  walking_age_18to25);
		this.parameterWalk.put("AGE_26TO35",  walking_age_26to35);
		this.parameterWalk.put("AGE_36TO50",  walking_age_36to50);
		this.parameterWalk.put("AGE_51TO60",  walking_age_51to60);
		this.parameterWalk.put("AGE_61TO70",  walking_age_61to70);
		this.parameterWalk.put("AGE_71PLUS",  walking_age_71plus);
		*/
	
		// bike
	
		// passenger
		this.parameterPassenger.put("CONST", 	carpassenger);
		this.parameterPassenger.put("ISTOURMODE", 	carpassenger_istourmode);
		this.parameterPassenger.put("WITHINTOUR", 	carpassenger_withintour);
		this.parameterPassenger.put("ISTOURMODE_WITHINTOUR", 	carpassenger_istourmode_withintour);
	
		this.parameterPassenger.put("PURPOSE_BUSINESS",  				carpassenger_purpose_business);
		this.parameterPassenger.put("PURPOSE_EDUCATION",  				carpassenger_purpose_education);
		this.parameterPassenger.put("PURPOSE_HOME",  						0.0);
		this.parameterPassenger.put("PURPOSE_LEISURE_INDOOR",  	carpassenger_purpose_leisure_indoors);
		this.parameterPassenger.put("PURPOSE_LEISURE_OUTDOOR",  	carpassenger_purpose_leisure_outdoors);
		this.parameterPassenger.put("PURPOSE_LEISURE_OTHER",  		carpassenger_purpose_leisure_other);
		this.parameterPassenger.put("PURPOSE_LEISURE_WALK",  		carpassenger_purpose_leisure_walk);
		this.parameterPassenger.put("PURPOSE_OTHER",  						carpassenger_purpose_other);
		this.parameterPassenger.put("PURPOSE_PRIVATE_BUSINESS",  carpassenger_purpose_private_business);
		this.parameterPassenger.put("PURPOSE_PRIVATE_VISIT",  		carpassenger_purpose_visit);
		this.parameterPassenger.put("PURPOSE_SERVICE",  					carpassenger_purpose_service);
		this.parameterPassenger.put("PURPOSE_SHOPPING_DAILY",  	carpassenger_purpose_shopping_grocery);
		this.parameterPassenger.put("PURPOSE_SHOPPING_OTHER",  	carpassenger_purpose_shopping_other);
		this.parameterPassenger.put("PURPOSE_BUSINESS",  				carpassenger_purpose_business);
		
		this.parameterPassenger.put("TOURPURPOSE_BUSINESS",  				carpassenger_tourpurpose_business);
		this.parameterPassenger.put("TOURPURPOSE_EDUCATION",  				carpassenger_tourpurpose_education);
		this.parameterPassenger.put("TOURPURPOSE_HOME",  						0.0);
		this.parameterPassenger.put("TOURPURPOSE_LEISURE_INDOOR",  	carpassenger_tourpurpose_leisure_indoors);
		this.parameterPassenger.put("TOURPURPOSE_LEISURE_OUTDOOR",  	carpassenger_tourpurpose_leisure_outdoors);
		this.parameterPassenger.put("TOURPURPOSE_LEISURE_OTHER",  		carpassenger_tourpurpose_leisure_other);
		this.parameterPassenger.put("TOURPURPOSE_LEISURE_WALK",  		carpassenger_tourpurpose_leisure_walk);
		this.parameterPassenger.put("TOURPURPOSE_OTHER",  						carpassenger_tourpurpose_other);
		this.parameterPassenger.put("TOURPURPOSE_PRIVATE_BUSINESS",  carpassenger_tourpurpose_private_business);
		this.parameterPassenger.put("TOURPURPOSE_PRIVATE_VISIT",  		carpassenger_tourpurpose_visit);
		this.parameterPassenger.put("TOURPURPOSE_SERVICE",  					carpassenger_tourpurpose_service);
		this.parameterPassenger.put("TOURPURPOSE_SHOPPING_DAILY",  	carpassenger_tourpurpose_shopping_grocery);
		this.parameterPassenger.put("TOURPURPOSE_SHOPPING_OTHER",  	carpassenger_tourpurpose_shopping_other);
		this.parameterPassenger.put("TOURPURPOSE_BUSINESS",  				carpassenger_tourpurpose_business);
		
		this.parameterPassenger.put("INTRAZONAL", 	carpassenger_intrazonal);
		
		this.parameterPassenger.put("FEMALE", 			carpassenger_sex_female);
		this.parameterPassenger.put("DAY_SA",  			carpassenger_day_Saturday);
		this.parameterPassenger.put("DAY_SU",  			carpassenger_day_Sunday);
		
		this.parameterPassenger.put("AGE_0TO9",  	carpassenger_age_06to09);
		this.parameterPassenger.put("AGE_10TO17",  carpassenger_age_10to17);
		this.parameterPassenger.put("AGE_18TO25",  carpassenger_age_18to25);
		this.parameterPassenger.put("AGE_26TO35",  carpassenger_age_26to35);
		this.parameterPassenger.put("AGE_36TO50",  carpassenger_age_36to50);
		this.parameterPassenger.put("AGE_51TO60",  carpassenger_age_51to60);
		this.parameterPassenger.put("AGE_61TO70",  carpassenger_age_61to70);
		this.parameterPassenger.put("AGE_71PLUS",  carpassenger_age_71plus);
		
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
	
	// pt
		this.parameterPt.put("CONST", 	publictransport);
		this.parameterPt.put("ISTOURMODE", 	publictransport_istourmode);
		this.parameterPt.put("WITHINTOUR", 	publictransport_withintour);
		this.parameterPt.put("ISTOURMODE_WITHINTOUR", 	publictransport_istourmode_withintour);
	
		this.parameterPt.put("PURPOSE_BUSINESS",  				publictransport_purpose_business);
		this.parameterPt.put("PURPOSE_EDUCATION",  				publictransport_purpose_education);
		this.parameterPt.put("PURPOSE_HOME",  						0.0);
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
		
		this.parameterPt.put("TOURPURPOSE_BUSINESS",  				publictransport_tourpurpose_business);
		this.parameterPt.put("TOURPURPOSE_EDUCATION",  				publictransport_tourpurpose_education);
		this.parameterPt.put("TOURPURPOSE_HOME",  						0.0);
		this.parameterPt.put("TOURPURPOSE_LEISURE_INDOOR",  	publictransport_tourpurpose_leisure_indoors);
		this.parameterPt.put("TOURPURPOSE_LEISURE_OUTDOOR",  	publictransport_tourpurpose_leisure_outdoors);
		this.parameterPt.put("TOURPURPOSE_LEISURE_OTHER",  		publictransport_tourpurpose_leisure_other);
		this.parameterPt.put("TOURPURPOSE_LEISURE_WALK",  		publictransport_tourpurpose_leisure_walk);
		this.parameterPt.put("TOURPURPOSE_OTHER",  						publictransport_tourpurpose_other);
		this.parameterPt.put("TOURPURPOSE_PRIVATE_BUSINESS",  publictransport_tourpurpose_private_business);
		this.parameterPt.put("TOURPURPOSE_PRIVATE_VISIT",  		publictransport_tourpurpose_visit);
		this.parameterPt.put("TOURPURPOSE_SERVICE",  					publictransport_tourpurpose_service);
		this.parameterPt.put("TOURPURPOSE_SHOPPING_DAILY",  	publictransport_tourpurpose_shopping_grocery);
		this.parameterPt.put("TOURPURPOSE_SHOPPING_OTHER",  	publictransport_tourpurpose_shopping_other);
		this.parameterPt.put("TOURPURPOSE_BUSINESS",  				publictransport_tourpurpose_business);
		
		this.parameterPt.put("INTRAZONAL", 	publictransport_intrazonal);
		
		this.parameterPt.put("DAY_SA",  publictransport_day_Saturday);
		this.parameterPt.put("DAY_SU",  publictransport_day_Sunday);
		
		this.parameterPt.put("FEMALE", publictransport_sex_female);
		
		this.parameterPt.put("AGE_0TO9",  	publictransport_age_06to09);
		this.parameterPt.put("AGE_10TO17",  publictransport_age_10to17);
		this.parameterPt.put("AGE_18TO25",  publictransport_age_18to25);
		this.parameterPt.put("AGE_26TO35",  publictransport_age_26to35);
		this.parameterPt.put("AGE_36TO50",  publictransport_age_36to50);
		this.parameterPt.put("AGE_51TO60",  publictransport_age_51to60);
		this.parameterPt.put("AGE_61TO70",  publictransport_age_61to70);
		this.parameterPt.put("AGE_71PLUS",  publictransport_age_71plus);
		
		this.parameterPt.put("EMPLOYMENT_EDUCATION",  				publictransport_employment_apprentice);
		this.parameterPt.put("EMPLOYMENT_FULLTIME",  					publictransport_employment_fulltime);
		this.parameterPt.put("EMPLOYMENT_INFANT",  						publictransport_employment_pupil);
		this.parameterPt.put("EMPLOYMENT_UNEMPLOYED",  				publictransport_employment_unemployed);
		this.parameterPt.put("EMPLOYMENT_NONE",  							publictransport_employment_other);
		this.parameterPt.put("EMPLOYMENT_HOMEKEEPER", 				publictransport_employment_homekeeper);
		this.parameterPt.put("EMPLOYMENT_PARTTIME",  					publictransport_employment_parttime);
		this.parameterPt.put("EMPLOYMENT_RETIRED",  					publictransport_employment_retired);
		this.parameterPt.put("EMPLOYMENT_STUDENT_PRIMARY", 		publictransport_employment_pupil);
		this.parameterPt.put("EMPLOYMENT_STUDENT_SECONDARY",	publictransport_employment_pupil);
		this.parameterPt.put("EMPLOYMENT_STUDENT_TERTIARY", 	publictransport_employment_student);
	
	}

	


	// Copied from TourModeChoiceParameterBase -- refactor?
	@Override
	public Map<String,Double> parameterForMode(Mode mode) {
		assert mode != StandardMode.BIKE : mode;
		assert mode != StandardMode.CAR : mode;
		if (StandardMode.PEDESTRIAN.equals(mode)) {
			return parameterWalk;
		}
		if (StandardMode.PASSENGER.equals(mode)) {
			return parameterPassenger;
		}
		if (StandardMode.PUBLICTRANSPORT.equals(mode)) {
			return parameterPt;
		}
		throw new AssertionError("invalid mode: " + mode);
	}

}
