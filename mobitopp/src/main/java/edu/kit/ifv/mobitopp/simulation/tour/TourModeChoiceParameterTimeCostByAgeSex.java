package edu.kit.ifv.mobitopp.simulation.tour;



public class TourModeChoiceParameterTimeCostByAgeSex 
	extends TourModeChoiceParameterTimeCostByAgeSexBase 
	implements TourModeChoiceParameter
{

	


	/*

Call:
mlogit(formula = mode ~ notavailable + time + time:sex. + time:age. + 
    cost + cost:sex. + cost:age. | sex. + age. + purpose. + day. + 
    num_activities + containsStrolling. + containsVisit. + containsPrivateB. + 
    containsService. + containsLeisure. + containsShopping. + 
    containsBusiness., data = trainingdata, reflevel = "walking", 
    method = "nr", print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2858          0.3909          0.1277          0.0555          0.1401 

nr method
24 iterations, 0h:9m:15s 
g'(-H)^-1g = 9.26E-07 
gradient close to zero 

Coefficients :
                                            Estimate  Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                       0.131287    0.049469    2.65  0.00796 ** 
carpassenger:(intercept)                   -4.790934    0.070499  -67.96  < 2e-16 ***
cycling:(intercept)                        -1.636171    0.067340  -24.30  < 2e-16 ***
publictransport:(intercept)                -0.255784    0.055057   -4.65  3.4e-06 ***
notavailable                              -24.741062 1766.818770   -0.01  0.98883    
time                                       -0.044931    0.000911  -49.32  < 2e-16 ***
cost                                       -0.686020    0.015341  -44.72  < 2e-16 ***
time:sex.female                            -0.007177    0.000832   -8.63  < 2e-16 ***
time:age.06to09                            -0.011157    0.002718   -4.10  4.1e-05 ***
time:age.10to17                            -0.005025    0.001518   -3.31  0.00093 ***
time:age.18to25                             0.012936    0.001563    8.28  2.2e-16 ***
time:age.26to35                            -0.001742    0.001658   -1.05  0.29334    
time:age.51to60                             0.001614    0.001331    1.21  0.22523    
time:age.61to70                             0.001208    0.001292    0.94  0.34968    
time:age.71plus                            -0.003517    0.001578   -2.23  0.02581 *  
cost:sex.female                             0.028751    0.014818    1.94  0.05235 .  
cost:age.06to09                            -0.218801    0.129922   -1.68  0.09216 .  
cost:age.10to17                            -0.602140    0.043345  -13.89  < 2e-16 ***
cost:age.18to25                             0.116677    0.026856    4.34  1.4e-05 ***
cost:age.26to35                             0.084274    0.026206    3.22  0.00130 ** 
cost:age.51to60                             0.023248    0.022310    1.04  0.29738    
cost:age.61to70                             0.106287    0.024610    4.32  1.6e-05 ***
cost:age.71plus                             0.052502    0.030952    1.70  0.08984 .  
cardriver:sex.female                       -0.706641    0.025139  -28.11  < 2e-16 ***
carpassenger:sex.female                     0.578582    0.030571   18.93  < 2e-16 ***
cycling:sex.female                         -0.489845    0.030825  -15.89  < 2e-16 ***
publictransport:sex.female                 -0.051557    0.025899   -1.99  0.04651 *  
cardriver:age.06to09                       -1.350249 7247.845693    0.00  0.99985    
carpassenger:age.06to09                     1.588885    0.064482   24.64  < 2e-16 ***
cycling:age.06to09                         -0.978196    0.085681  -11.42  < 2e-16 ***
publictransport:age.06to09                 -1.126173    0.138712   -8.12  4.4e-16 ***
cardriver:age.10to17                       -1.157790    0.196752   -5.88  4.0e-09 ***
carpassenger:age.10to17                     1.618296    0.051650   31.33  < 2e-16 ***
cycling:age.10to17                          0.680280    0.052534   12.95  < 2e-16 ***
publictransport:age.10to17                  0.784797    0.049423   15.88  < 2e-16 ***
cardriver:age.18to25                        0.561846    0.061498    9.14  < 2e-16 ***
carpassenger:age.18to25                     1.403443    0.072007   19.49  < 2e-16 ***
cycling:age.18to25                          0.177843    0.077315    2.30  0.02144 *  
publictransport:age.18to25                  0.671492    0.056864   11.81  < 2e-16 ***
cardriver:age.26to35                       -0.412632    0.049812   -8.28  2.2e-16 ***
carpassenger:age.26to35                    -0.150914    0.070519   -2.14  0.03235 *  
cycling:age.26to35                         -0.502412    0.069818   -7.20  6.2e-13 ***
publictransport:age.26to35                  0.144652    0.053738    2.69  0.00711 ** 
cardriver:age.51to60                        0.128091    0.037923    3.38  0.00073 ***
carpassenger:age.51to60                     0.378267    0.053905    7.02  2.3e-12 ***
cycling:age.51to60                         -0.181114    0.051096   -3.54  0.00039 ***
publictransport:age.51to60                  0.127324    0.045777    2.78  0.00541 ** 
cardriver:age.61to70                       -0.234247    0.036971   -6.34  2.4e-10 ***
carpassenger:age.61to70                     0.506615    0.049766   10.18  < 2e-16 ***
cycling:age.61to70                         -0.472714    0.055394   -8.53  < 2e-16 ***
publictransport:age.61to70                  0.247731    0.047165    5.25  1.5e-07 ***
cardriver:age.71plus                       -0.223079    0.043556   -5.12  3.0e-07 ***
carpassenger:age.71plus                     0.450228    0.056822    7.92  2.2e-15 ***
cycling:age.71plus                         -0.374598    0.068910   -5.44  5.4e-08 ***
publictransport:age.71plus                  0.569588    0.054001   10.55  < 2e-16 ***
cardriver:purpose.business                  0.417833    0.101725    4.11  4.0e-05 ***
carpassenger:purpose.business               0.646452    0.156178    4.14  3.5e-05 ***
cycling:purpose.business                   -0.243643    0.161857   -1.51  0.13225    
publictransport:purpose.business           -0.325377    0.124120   -2.62  0.00876 ** 
cardriver:purpose.education                -0.974636    0.072565  -13.43  < 2e-16 ***
carpassenger:purpose.education              0.104709    0.067766    1.55  0.12231    
cycling:purpose.education                  -0.489402    0.067014   -7.30  2.8e-13 ***
publictransport:purpose.education          -0.133958    0.056254   -2.38  0.01725 *  
cardriver:purpose.service                   0.505681    0.042569   11.88  < 2e-16 ***
carpassenger:purpose.service                1.030669    0.070044   14.71  < 2e-16 ***
cycling:purpose.service                    -0.703891    0.074039   -9.51  < 2e-16 ***
publictransport:purpose.service            -1.930587    0.075160  -25.69  < 2e-16 ***
cardriver:purpose.private_business         -0.171615    0.042187   -4.07  4.7e-05 ***
carpassenger:purpose.private_business       1.358097    0.062578   21.70  < 2e-16 ***
cycling:purpose.private_business           -0.547222    0.067165   -8.15  4.4e-16 ***
publictransport:purpose.private_business   -1.112994    0.053277  -20.89  < 2e-16 ***
cardriver:purpose.visit                    -0.338494    0.048702   -6.95  3.6e-12 ***
carpassenger:purpose.visit                  1.135326    0.066077   17.18  < 2e-16 ***
cycling:purpose.visit                      -0.752970    0.077508   -9.71  < 2e-16 ***
publictransport:purpose.visit              -1.402741    0.063278  -22.17  < 2e-16 ***
cardriver:purpose.shopping_grocery         -0.120493    0.039908   -3.02  0.00253 ** 
carpassenger:purpose.shopping_grocery       1.407273    0.061716   22.80  < 2e-16 ***
cycling:purpose.shopping_grocery           -0.156283    0.060259   -2.59  0.00950 ** 
publictransport:purpose.shopping_grocery   -1.755406    0.057517  -30.52  < 2e-16 ***
cardriver:purpose.shopping_other            0.064883    0.056013    1.16  0.24672    
carpassenger:purpose.shopping_other         1.719306    0.073798   23.30  < 2e-16 ***
cycling:purpose.shopping_other             -0.478623    0.091227   -5.25  1.6e-07 ***
publictransport:purpose.shopping_other     -0.592304    0.067115   -8.83  < 2e-16 ***
cardriver:purpose.leisure_indoors          -0.909933    0.053572  -16.99  < 2e-16 ***
carpassenger:purpose.leisure_indoors        1.448243    0.068901   21.02  < 2e-16 ***
cycling:purpose.leisure_indoors            -1.472865    0.103934  -14.17  < 2e-16 ***
publictransport:purpose.leisure_indoors    -0.688631    0.059953  -11.49  < 2e-16 ***
cardriver:purpose.leisure_outdoors          0.315897    0.048704    6.49  8.8e-11 ***
carpassenger:purpose.leisure_outdoors       1.970861    0.064785   30.42  < 2e-16 ***
cycling:purpose.leisure_outdoors           -0.180757    0.070184   -2.58  0.01001 *  
publictransport:purpose.leisure_outdoors   -1.018406    0.063062  -16.15  < 2e-16 ***
cardriver:purpose.leisure_other            -0.481495    0.046843  -10.28  < 2e-16 ***
carpassenger:purpose.leisure_other          1.262948    0.064136   19.69  < 2e-16 ***
cycling:purpose.leisure_other              -0.673766    0.070712   -9.53  < 2e-16 ***
publictransport:purpose.leisure_other      -0.846695    0.054606  -15.51  < 2e-16 ***
cardriver:purpose.leisure_walk             -2.728217    0.052341  -52.12  < 2e-16 ***
carpassenger:purpose.leisure_walk          -0.688131    0.077118   -8.92  < 2e-16 ***
cycling:purpose.leisure_walk               -0.997650    0.064265  -15.52  < 2e-16 ***
publictransport:purpose.leisure_walk       -3.128785    0.076127  -41.10  < 2e-16 ***
cardriver:purpose.other                    -0.121343    0.290486   -0.42  0.67615    
carpassenger:purpose.other                  1.605865    0.274848    5.84  5.1e-09 ***
cycling:purpose.other                      -2.315496    1.024717   -2.26  0.02384 *  
publictransport:purpose.other              -0.579021    0.330944   -1.75  0.08019 .  
cardriver:day.Saturday                      0.026167    0.029364    0.89  0.37285    
carpassenger:day.Saturday                   0.489605    0.034819   14.06  < 2e-16 ***
cycling:day.Saturday                       -0.165605    0.049114   -3.37  0.00075 ***
publictransport:day.Saturday               -0.039105    0.040839   -0.96  0.33829    
cardriver:day.Sunday                       -0.395174    0.035897  -11.01  < 2e-16 ***
carpassenger:day.Sunday                     0.215610    0.040967    5.26  1.4e-07 ***
cycling:day.Sunday                         -0.469577    0.059339   -7.91  2.4e-15 ***
publictransport:day.Sunday                 -0.620598    0.051293  -12.10  < 2e-16 ***
cardriver:num_activities                    0.277075    0.030093    9.21  < 2e-16 ***
carpassenger:num_activities                 0.256695    0.035046    7.32  2.4e-13 ***
cycling:num_activities                      0.148044    0.047164    3.14  0.00170 ** 
publictransport:num_activities              0.253563    0.035905    7.06  1.6e-12 ***
cardriver:containsStrolling.TRUE           -1.443823    0.127469  -11.33  < 2e-16 ***
carpassenger:containsStrolling.TRUE        -1.213533    0.160022   -7.58  3.4e-14 ***
cycling:containsStrolling.TRUE             -0.798250    0.199948   -3.99  6.5e-05 ***
publictransport:containsStrolling.TRUE     -0.789581    0.147291   -5.36  8.3e-08 ***
cardriver:containsVisit.TRUE                0.346105    0.089754    3.86  0.00012 ***
carpassenger:containsVisit.TRUE             0.039703    0.099373    0.40  0.68950    
cycling:containsVisit.TRUE                 -0.140302    0.128851   -1.09  0.27621    
publictransport:containsVisit.TRUE         -0.213667    0.104054   -2.05  0.04003 *  
cardriver:containsPrivateB.TRUE             0.174898    0.071707    2.44  0.01473 *  
carpassenger:containsPrivateB.TRUE          0.091891    0.086253    1.07  0.28671    
cycling:containsPrivateB.TRUE               0.003708    0.111380    0.03  0.97344    
publictransport:containsPrivateB.TRUE       0.012181    0.086076    0.14  0.88747    
cardriver:containsService.TRUE              0.081095    0.126255    0.64  0.52067    
carpassenger:containsService.TRUE          -0.212488    0.171673   -1.24  0.21581    
cycling:containsService.TRUE               -0.712567    0.184864   -3.85  0.00012 ***
publictransport:containsService.TRUE       -1.590170    0.166060   -9.58  < 2e-16 ***
cardriver:containsLeisure.TRUE              0.598407    0.072968    8.20  2.2e-16 ***
carpassenger:containsLeisure.TRUE           1.055667    0.079369   13.30  < 2e-16 ***
cycling:containsLeisure.TRUE                0.066632    0.109502    0.61  0.54286    
publictransport:containsLeisure.TRUE        0.853477    0.082219   10.38  < 2e-16 ***
cardriver:containsShopping.TRUE             0.278953    0.060021    4.65  3.4e-06 ***
carpassenger:containsShopping.TRUE          0.100592    0.074236    1.36  0.17541    
cycling:containsShopping.TRUE               0.058845    0.095455    0.62  0.53758    
publictransport:containsShopping.TRUE       0.237118    0.071750    3.30  0.00095 ***
cardriver:containsBusiness.TRUE             0.276371    0.134102    2.06  0.03931 *  
carpassenger:containsBusiness.TRUE         -0.306657    0.213832   -1.43  0.15154    
cycling:containsBusiness.TRUE              -0.023716    0.187935   -0.13  0.89958    
publictransport:containsBusiness.TRUE      -0.013514    0.153085   -0.09  0.92965    
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -103000
McFadden R^2:  0.371 
Likelihood ratio test : chisq = 122000 (p.value = <2e-16)

		*/
	
	
	public TourModeChoiceParameterTimeCostByAgeSex() {
	
		
			cardriver                                   = 0.131287;     //se=0.049469
			carpassenger                               = -4.790934;     //se=0.070499
			cycling                                    = -1.636171;     //se=0.067340
			publictransport                            = -0.255784;     //se=0.055057
			
			notavailable                               = -24.741062;  //se=1766.818770
			
			time                                        = -0.044931;     //se=0.000911
			cost                                        = -0.686020;     //se=0.015341
			
			time_sex_female                             = -0.007177;     //se=0.000832
			cost_sex_female                              = 0.028751;     //se=0.014818
			
			time_age_06to09                             = -0.011157;     //se=0.002718
			time_age_10to17                             = -0.005025;     //se=0.001518
			time_age_18to25                              = 0.012936;     //se=0.001563
			time_age_26to35                             = -0.001742;     //se=0.001658
			time_age_51to60                              = 0.001614;     //se=0.001331
			time_age_61to70                              = 0.001208;     //se=0.001292
			time_age_71plus                             = -0.003517;     //se=0.001578
			
			cost_age_06to09                             = -0.218801;     //se=0.129922
			cost_age_10to17                             = -0.602140;     //se=0.043345
			cost_age_18to25                              = 0.116677;     //se=0.026856
			cost_age_26to35                              = 0.084274;     //se=0.026206
			cost_age_51to60                              = 0.023248;     //se=0.022310
			cost_age_61to70                              = 0.106287;     //se=0.024610
			cost_age_71plus                              = 0.052502;     //se=0.030952
			
			cardriver_sex_female                        = -0.706641;     //se=0.025139
			carpassenger_sex_female                      = 0.578582;     //se=0.030571
			cycling_sex_female                          = -0.489845;     //se=0.030825
			publictransport_sex_female                  = -0.051557;     //se=0.025899
			
			cardriver_age_06to09                        = -1.350249;  //se=7247.845693
			cardriver_age_10to17                        = -1.157790;     //se=0.196752
			cardriver_age_18to25                         = 0.561846;     //se=0.061498
			cardriver_age_26to35                        = -0.412632;     //se=0.049812
			cardriver_age_51to60                         = 0.128091;     //se=0.037923
			cardriver_age_61to70                        = -0.234247;     //se=0.036971
			cardriver_age_71plus                        = -0.223079;     //se=0.043556
			carpassenger_age_06to09                      = 1.588885;     //se=0.064482
			carpassenger_age_10to17                      = 1.618296;     //se=0.051650
			carpassenger_age_18to25                      = 1.403443;     //se=0.072007
			carpassenger_age_26to35                     = -0.150914;     //se=0.070519
			carpassenger_age_51to60                      = 0.378267;     //se=0.053905
			carpassenger_age_61to70                      = 0.506615;     //se=0.049766
			carpassenger_age_71plus                      = 0.450228;     //se=0.056822
			cycling_age_06to09                          = -0.978196;     //se=0.085681
			cycling_age_10to17                           = 0.680280;     //se=0.052534
			cycling_age_18to25                           = 0.177843;     //se=0.077315
			cycling_age_26to35                          = -0.502412;     //se=0.069818
			cycling_age_51to60                          = -0.181114;     //se=0.051096
			cycling_age_61to70                          = -0.472714;     //se=0.055394
			cycling_age_71plus                          = -0.374598;     //se=0.068910
			publictransport_age_06to09                  = -1.126173;     //se=0.138712
			publictransport_age_10to17                   = 0.784797;     //se=0.049423
			publictransport_age_18to25                   = 0.671492;     //se=0.056864
			publictransport_age_26to35                   = 0.144652;     //se=0.053738
			publictransport_age_51to60                   = 0.127324;     //se=0.045777
			publictransport_age_61to70                   = 0.247731;     //se=0.047165
			publictransport_age_71plus                   = 0.569588;     //se=0.054001
			
			cardriver_purpose_business                   = 0.417833;     //se=0.101725
			cardriver_purpose_education                 = -0.974636;     //se=0.072565
			cardriver_purpose_leisure_indoors           = -0.909933;     //se=0.053572
			cardriver_purpose_leisure_other             = -0.481495;     //se=0.046843
			cardriver_purpose_leisure_outdoors           = 0.315897;     //se=0.048704
			cardriver_purpose_leisure_walk              = -2.728217;     //se=0.052341
			cardriver_purpose_other                     = -0.121343;     //se=0.290486
			cardriver_purpose_private_business          = -0.171615;     //se=0.042187
			cardriver_purpose_service                    = 0.505681;     //se=0.042569
			cardriver_purpose_shopping_grocery          = -0.120493;     //se=0.039908
			cardriver_purpose_shopping_other             = 0.064883;     //se=0.056013
			cardriver_purpose_visit                     = -0.338494;     //se=0.048702
			carpassenger_purpose_business                = 0.646452;     //se=0.156178
			carpassenger_purpose_education               = 0.104709;     //se=0.067766
			carpassenger_purpose_leisure_indoors         = 1.448243;     //se=0.068901
			carpassenger_purpose_leisure_other           = 1.262948;     //se=0.064136
			carpassenger_purpose_leisure_outdoors        = 1.970861;     //se=0.064785
			carpassenger_purpose_leisure_walk           = -0.688131;     //se=0.077118
			carpassenger_purpose_other                   = 1.605865;     //se=0.274848
			carpassenger_purpose_private_business        = 1.358097;     //se=0.062578
			carpassenger_purpose_service                 = 1.030669;     //se=0.070044
			carpassenger_purpose_shopping_grocery        = 1.407273;     //se=0.061716
			carpassenger_purpose_shopping_other          = 1.719306;     //se=0.073798
			carpassenger_purpose_visit                   = 1.135326;     //se=0.066077
			cycling_purpose_business                    = -0.243643;     //se=0.161857
			cycling_purpose_education                   = -0.489402;     //se=0.067014
			cycling_purpose_leisure_indoors             = -1.472865;     //se=0.103934
			cycling_purpose_leisure_other               = -0.673766;     //se=0.070712
			cycling_purpose_leisure_outdoors            = -0.180757;     //se=0.070184
			cycling_purpose_leisure_walk                = -0.997650;     //se=0.064265
			cycling_purpose_other                       = -2.315496;     //se=1.024717
			cycling_purpose_private_business            = -0.547222;     //se=0.067165
			cycling_purpose_service                     = -0.703891;     //se=0.074039
			cycling_purpose_shopping_grocery            = -0.156283;     //se=0.060259
			cycling_purpose_shopping_other              = -0.478623;     //se=0.091227
			cycling_purpose_visit                       = -0.752970;     //se=0.077508
			publictransport_purpose_business            = -0.325377;     //se=0.124120
			publictransport_purpose_education           = -0.133958;     //se=0.056254
			publictransport_purpose_leisure_indoors     = -0.688631;     //se=0.059953
			publictransport_purpose_leisure_other       = -0.846695;     //se=0.054606
			publictransport_purpose_leisure_outdoors    = -1.018406;     //se=0.063062
			publictransport_purpose_leisure_walk        = -3.128785;     //se=0.076127
			publictransport_purpose_other               = -0.579021;     //se=0.330944
			publictransport_purpose_private_business    = -1.112994;     //se=0.053277
			publictransport_purpose_service             = -1.930587;     //se=0.075160
			publictransport_purpose_shopping_grocery    = -1.755406;     //se=0.057517
			publictransport_purpose_shopping_other      = -0.592304;     //se=0.067115
			publictransport_purpose_visit               = -1.402741;     //se=0.063278
			
			cardriver_day_Saturday                       = 0.026167;     //se=0.029364
			cardriver_day_Sunday                        = -0.395174;     //se=0.035897
			carpassenger_day_Saturday                    = 0.489605;     //se=0.034819
			carpassenger_day_Sunday                      = 0.215610;     //se=0.040967
			cycling_day_Saturday                        = -0.165605;     //se=0.049114
			cycling_day_Sunday                          = -0.469577;     //se=0.059339
			publictransport_day_Saturday                = -0.039105;     //se=0.040839
			publictransport_day_Sunday                  = -0.620598;     //se=0.051293
			
			cardriver_num_activities                     = 0.277075;     //se=0.030093
			carpassenger_num_activities                  = 0.256695;     //se=0.035046
			cycling_num_activities                       = 0.148044;     //se=0.047164
			publictransport_num_activities               = 0.253563;     //se=0.035905
			
			cardriver_containsStrolling                 = -1.443823;     //se=0.127469
			carpassenger_containsStrolling              = -1.213533;     //se=0.160022
			cycling_containsStrolling                   = -0.798250;     //se=0.199948
			publictransport_containsStrolling           = -0.789581;     //se=0.147291
			
			cardriver_containsBusiness                   = 0.276371;     //se=0.134102
			cardriver_containsLeisure                    = 0.598407;     //se=0.072968
			cardriver_containsPrivateB                   = 0.174898;     //se=0.071707
			cardriver_containsService                    = 0.081095;     //se=0.126255
			cardriver_containsShopping                   = 0.278953;     //se=0.060021
			cardriver_containsVisit                      = 0.346105;     //se=0.089754
			carpassenger_containsBusiness               = -0.306657;     //se=0.213832
			carpassenger_containsLeisure                 = 1.055667;     //se=0.079369
			carpassenger_containsPrivateB                = 0.091891;     //se=0.086253
			carpassenger_containsService                = -0.212488;     //se=0.171673
			carpassenger_containsShopping                = 0.100592;     //se=0.074236
			carpassenger_containsVisit                   = 0.039703;     //se=0.099373
			cycling_containsBusiness                    = -0.023716;     //se=0.187935
			cycling_containsLeisure                      = 0.066632;     //se=0.109502
			cycling_containsPrivateB                     = 0.003708;     //se=0.111380
			cycling_containsService                     = -0.712567;     //se=0.184864
			cycling_containsShopping                     = 0.058845;     //se=0.095455
			cycling_containsVisit                       = -0.140302;     //se=0.128851
			publictransport_containsBusiness            = -0.013514;     //se=0.153085
			publictransport_containsLeisure              = 0.853477;     //se=0.082219
			publictransport_containsPrivateB             = 0.012181;     //se=0.086076
			publictransport_containsService             = -1.590170;     //se=0.166060
			publictransport_containsShopping             = 0.237118;     //se=0.071750
			publictransport_containsVisit               = -0.213667;     //se=0.104054

	
		
	// Referenzklasse für Altersgruppe:
		time_age_36to50 = 0.0;
		cost_age_36to50 = 0.0;
	
		// Attribut wird  nicht verwendet
		cardriver_intrazonal 				= 0.0;
		carpassenger_intrazonal 		= 0.0;
		cycling_intrazonal 					= 0.0;
		publictransport_intrazonal	= 0.0;
		
	// System.out.println("test");	


		init();
	}



}
