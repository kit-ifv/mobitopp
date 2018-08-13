package edu.kit.ifv.mobitopp.simulation.tour;



public class TourModeChoiceParameterTimeCostByAgeSexIz 
	extends TourModeChoiceParameterTimeCostByAgeSexBase 
	implements TourModeChoiceParameter
{

	


	/*

Call:
mlogit(formula = mode ~ notavailable + time + time:sex. + time:age. + 
    cost + cost:sex. + cost:age. | sex. + age. + purpose. + day. + 
    intrazonal. + num_activities + containsStrolling. + containsVisit. + 
    containsPrivateB. + containsService. + containsLeisure. + 
    containsShopping. + containsBusiness., data = trainingdata, 
    reflevel = "walking", method = "nr", print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2858          0.3909          0.1277          0.0555          0.1401 

nr method
24 iterations, 0h:9m:41s 
g'(-H)^-1g = 9.42E-07 
gradient close to zero 

Coefficients :
                                            Estimate  Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                       0.842304    0.052295   16.11  < 2e-16 ***
carpassenger:(intercept)                   -4.103833    0.071847  -57.12  < 2e-16 ***
cycling:(intercept)                        -1.344718    0.067861  -19.82  < 2e-16 ***
publictransport:(intercept)                 0.332748    0.058195    5.72  1.1e-08 ***
notavailable                              -24.780925 1767.454938   -0.01  0.98881    
time                                       -0.034769    0.000855  -40.67  < 2e-16 ***
cost                                       -0.689965    0.015148  -45.55  < 2e-16 ***
time:sex.female                            -0.005153    0.000769   -6.70  2.1e-11 ***
time:age.06to09                            -0.008666    0.002499   -3.47  0.00052 ***
time:age.10to17                            -0.003628    0.001434   -2.53  0.01142 *  
time:age.18to25                             0.009894    0.001451    6.82  9.1e-12 ***
time:age.26to35                            -0.003055    0.001540   -1.98  0.04733 *  
time:age.51to60                             0.001433    0.001219    1.18  0.23981    
time:age.61to70                             0.000971    0.001184    0.82  0.41226    
time:age.71plus                            -0.002193    0.001443   -1.52  0.12862    
cost:sex.female                             0.013670    0.014626    0.93  0.34995    
cost:age.06to09                            -0.113678    0.110913   -1.02  0.30540    
cost:age.10to17                            -0.418008    0.041122  -10.17  < 2e-16 ***
cost:age.18to25                             0.136958    0.026500    5.17  2.4e-07 ***
cost:age.26to35                             0.092666    0.025826    3.59  0.00033 ***
cost:age.51to60                             0.021970    0.021933    1.00  0.31651    
cost:age.61to70                             0.090351    0.024165    3.74  0.00018 ***
cost:age.71plus                             0.044818    0.030349    1.48  0.13975    
cardriver:sex.female                       -0.674418    0.025357  -26.60  < 2e-16 ***
carpassenger:sex.female                     0.600301    0.030380   19.76  < 2e-16 ***
cycling:sex.female                         -0.485319    0.030441  -15.94  < 2e-16 ***
publictransport:sex.female                 -0.034353    0.027316   -1.26  0.20853    
cardriver:age.06to09                       -1.097672 6413.889044    0.00  0.99986    
carpassenger:age.06to09                     1.529011    0.065190   23.45  < 2e-16 ***
cycling:age.06to09                         -1.116310    0.085117  -13.11  < 2e-16 ***
publictransport:age.06to09                 -0.988486    0.131511   -7.52  5.6e-14 ***
cardriver:age.10to17                       -1.223083    0.197294   -6.20  5.7e-10 ***
carpassenger:age.10to17                     1.608163    0.052304   30.75  < 2e-16 ***
cycling:age.10to17                          0.642268    0.051848   12.39  < 2e-16 ***
publictransport:age.10to17                  0.775829    0.053392   14.53  < 2e-16 ***
cardriver:age.18to25                        0.514697    0.061844    8.32  < 2e-16 ***
carpassenger:age.18to25                     1.385675    0.071291   19.44  < 2e-16 ***
cycling:age.18to25                          0.186971    0.076496    2.44  0.01452 *  
publictransport:age.18to25                  0.655475    0.060252   10.88  < 2e-16 ***
cardriver:age.26to35                       -0.463442    0.050169   -9.24  < 2e-16 ***
carpassenger:age.26to35                    -0.188398    0.069835   -2.70  0.00698 ** 
cycling:age.26to35                         -0.471240    0.068996   -6.83  8.5e-12 ***
publictransport:age.26to35                  0.053015    0.055729    0.95  0.34145    
cardriver:age.51to60                        0.129077    0.038092    3.39  0.00070 ***
carpassenger:age.51to60                     0.385370    0.053181    7.25  4.3e-13 ***
cycling:age.51to60                         -0.175560    0.050421   -3.48  0.00050 ***
publictransport:age.51to60                  0.142965    0.047749    2.99  0.00275 ** 
cardriver:age.61to70                       -0.291003    0.037279   -7.81  6.0e-15 ***
carpassenger:age.61to70                     0.454215    0.049174    9.24  < 2e-16 ***
cycling:age.61to70                         -0.460459    0.054654   -8.42  < 2e-16 ***
publictransport:age.61to70                  0.165172    0.049021    3.37  0.00075 ***
cardriver:age.71plus                       -0.277279    0.043827   -6.33  2.5e-10 ***
carpassenger:age.71plus                     0.418874    0.056091    7.47  8.1e-14 ***
cycling:age.71plus                         -0.345454    0.067949   -5.08  3.7e-07 ***
publictransport:age.71plus                  0.445775    0.056172    7.94  2.0e-15 ***
cardriver:purpose.business                  0.515757    0.104118    4.95  7.3e-07 ***
carpassenger:purpose.business               0.717387    0.156866    4.57  4.8e-06 ***
cycling:purpose.business                   -0.292859    0.159974   -1.83  0.06715 .  
publictransport:purpose.business           -0.094963    0.132746   -0.72  0.47438    
cardriver:purpose.education                -1.068552    0.073664  -14.51  < 2e-16 ***
carpassenger:purpose.education              0.017150    0.068265    0.25  0.80164    
cycling:purpose.education                  -0.553171    0.066402   -8.33  < 2e-16 ***
publictransport:purpose.education          -0.241253    0.059908   -4.03  5.6e-05 ***
cardriver:purpose.service                   0.551173    0.043898   12.56  < 2e-16 ***
carpassenger:purpose.service                0.984752    0.070469   13.97  < 2e-16 ***
cycling:purpose.service                    -0.872532    0.073935  -11.80  < 2e-16 ***
publictransport:purpose.service            -1.706718    0.078182  -21.83  < 2e-16 ***
cardriver:purpose.private_business         -0.197657    0.043229   -4.57  4.8e-06 ***
carpassenger:purpose.private_business       1.278303    0.062977   20.30  < 2e-16 ***
cycling:purpose.private_business           -0.688058    0.066750  -10.31  < 2e-16 ***
publictransport:purpose.private_business   -1.018404    0.056301  -18.09  < 2e-16 ***
cardriver:purpose.visit                    -0.320451    0.049943   -6.42  1.4e-10 ***
carpassenger:purpose.visit                  1.112013    0.066592   16.70  < 2e-16 ***
cycling:purpose.visit                      -0.882239    0.077060  -11.45  < 2e-16 ***
publictransport:purpose.visit              -1.266630    0.067032  -18.90  < 2e-16 ***
cardriver:purpose.shopping_grocery         -0.203514    0.040928   -4.97  6.6e-07 ***
carpassenger:purpose.shopping_grocery       1.239450    0.062161   19.94  < 2e-16 ***
cycling:purpose.shopping_grocery           -0.370226    0.060067   -6.16  7.1e-10 ***
publictransport:purpose.shopping_grocery   -1.701175    0.060130  -28.29  < 2e-16 ***
cardriver:purpose.shopping_other           -0.036598    0.057118   -0.64  0.52169    
carpassenger:purpose.shopping_other         1.597416    0.074223   21.52  < 2e-16 ***
cycling:purpose.shopping_other             -0.555777    0.090496   -6.14  8.2e-10 ***
publictransport:purpose.shopping_other     -0.643948    0.070298   -9.16  < 2e-16 ***
cardriver:purpose.leisure_indoors          -0.962799    0.054762  -17.58  < 2e-16 ***
carpassenger:purpose.leisure_indoors        1.376719    0.069430   19.83  < 2e-16 ***
cycling:purpose.leisure_indoors            -1.512268    0.103364  -14.63  < 2e-16 ***
publictransport:purpose.leisure_indoors    -0.689011    0.063342  -10.88  < 2e-16 ***
cardriver:purpose.leisure_outdoors          0.267916    0.049826    5.38  7.6e-08 ***
carpassenger:purpose.leisure_outdoors       1.873813    0.065336   28.68  < 2e-16 ***
cycling:purpose.leisure_outdoors           -0.295889    0.069731   -4.24  2.2e-05 ***
publictransport:purpose.leisure_outdoors   -0.968115    0.066416  -14.58  < 2e-16 ***
cardriver:purpose.leisure_other            -0.469247    0.048019   -9.77  < 2e-16 ***
carpassenger:purpose.leisure_other          1.230945    0.064636   19.04  < 2e-16 ***
cycling:purpose.leisure_other              -0.797559    0.070294  -11.35  < 2e-16 ***
publictransport:purpose.leisure_other      -0.706146    0.058335  -12.10  < 2e-16 ***
cardriver:purpose.leisure_walk             -2.425378    0.053963  -44.95  < 2e-16 ***
carpassenger:purpose.leisure_walk          -0.520068    0.077483   -6.71  1.9e-11 ***
cycling:purpose.leisure_walk               -1.326126    0.065659  -20.20  < 2e-16 ***
publictransport:purpose.leisure_walk       -2.165435    0.085939  -25.20  < 2e-16 ***
cardriver:purpose.other                     0.131372    0.301157    0.44  0.66267    
carpassenger:purpose.other                  1.848446    0.278116    6.65  3.0e-11 ***
cycling:purpose.other                      -2.302529    1.020220   -2.26  0.02401 *  
publictransport:purpose.other              -0.040508    0.371814   -0.11  0.91324    
cardriver:day.Saturday                      0.017073    0.030052    0.57  0.56996    
carpassenger:day.Saturday                   0.489639    0.034978   14.00  < 2e-16 ***
cycling:day.Saturday                       -0.148034    0.048639   -3.04  0.00234 ** 
publictransport:day.Saturday               -0.061341    0.043114   -1.42  0.15481    
cardriver:day.Sunday                       -0.418969    0.036747  -11.40  < 2e-16 ***
carpassenger:day.Sunday                     0.203583    0.041094    4.95  7.3e-07 ***
cycling:day.Sunday                         -0.440659    0.058498   -7.53  5.0e-14 ***
publictransport:day.Sunday                 -0.687115    0.054161  -12.69  < 2e-16 ***
cardriver:intrazonal.TRUE                  -1.058577    0.024023  -44.06  < 2e-16 ***
carpassenger:intrazonal.TRUE               -0.739842    0.031351  -23.60  < 2e-16 ***
cycling:intrazonal.TRUE                     0.195679    0.031695    6.17  6.7e-10 ***
publictransport:intrazonal.TRUE            -3.246929    0.067897  -47.82  < 2e-16 ***
cardriver:num_activities                    0.270391    0.030512    8.86  < 2e-16 ***
carpassenger:num_activities                 0.257202    0.035203    7.31  2.7e-13 ***
cycling:num_activities                      0.157524    0.046726    3.37  0.00075 ***
publictransport:num_activities              0.243145    0.037183    6.54  6.2e-11 ***
cardriver:containsStrolling.TRUE           -1.447244    0.128442  -11.27  < 2e-16 ***
carpassenger:containsStrolling.TRUE        -1.217362    0.159322   -7.64  2.2e-14 ***
cycling:containsStrolling.TRUE             -0.772697    0.198211   -3.90  9.7e-05 ***
publictransport:containsStrolling.TRUE     -0.863026    0.151803   -5.69  1.3e-08 ***
cardriver:containsVisit.TRUE                0.336435    0.091102    3.69  0.00022 ***
carpassenger:containsVisit.TRUE             0.033099    0.099763    0.33  0.74006    
cycling:containsVisit.TRUE                 -0.121845    0.127865   -0.95  0.34063    
publictransport:containsVisit.TRUE         -0.257201    0.108381   -2.37  0.01764 *  
cardriver:containsPrivateB.TRUE             0.141330    0.072818    1.94  0.05228 .  
carpassenger:containsPrivateB.TRUE          0.062649    0.086620    0.72  0.46952    
cycling:containsPrivateB.TRUE               0.024562    0.110479    0.22  0.82407    
publictransport:containsPrivateB.TRUE      -0.046419    0.089351   -0.52  0.60340    
cardriver:containsService.TRUE             -0.012399    0.127239   -0.10  0.92237    
carpassenger:containsService.TRUE          -0.328759    0.171368   -1.92  0.05506 .  
cycling:containsService.TRUE               -0.778625    0.183003   -4.25  2.1e-05 ***
publictransport:containsService.TRUE       -1.706409    0.167932  -10.16  < 2e-16 ***
cardriver:containsLeisure.TRUE              0.445470    0.073240    6.08  1.2e-09 ***
carpassenger:containsLeisure.TRUE           0.943762    0.079125   11.93  < 2e-16 ***
cycling:containsLeisure.TRUE                0.116251    0.109146    1.07  0.28683    
publictransport:containsLeisure.TRUE        0.602243    0.084680    7.11  1.1e-12 ***
cardriver:containsShopping.TRUE             0.220839    0.060747    3.64  0.00028 ***
carpassenger:containsShopping.TRUE          0.045155    0.074378    0.61  0.54378    
cycling:containsShopping.TRUE               0.082577    0.094840    0.87  0.38392    
publictransport:containsShopping.TRUE       0.130030    0.074252    1.75  0.07991 .  
cardriver:containsBusiness.TRUE             0.280038    0.136773    2.05  0.04061 *  
carpassenger:containsBusiness.TRUE         -0.313192    0.215007   -1.46  0.14521    
cycling:containsBusiness.TRUE              -0.025020    0.186472   -0.13  0.89326    
publictransport:containsBusiness.TRUE       0.001813    0.158809    0.01  0.99089    
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -100000
McFadden R^2:  0.391 
Likelihood ratio test : chisq = 129000 (p.value = <2e-16)


		*/
	
	
	public TourModeChoiceParameterTimeCostByAgeSexIz() {
	
		cardriver                                   = 0.842304;     //se=0.052295
		carpassenger                               = -4.103833;     //se=0.071847
		cycling                                    = -1.344718;     //se=0.067861
		publictransport                             = 0.332748;     //se=0.058195
			
		notavailable                               = -24.780925;  //se=1767.454938
		time                                        = -0.034769;     //se=0.000855
		cost                                        = -0.689965;     //se=0.015148
		
		time_sex_female                             = -0.005153;     //se=0.000769
		cost_sex_female                              = 0.013670;     //se=0.014626
		
		time_age_06to09                             = -0.008666;     //se=0.002499
		time_age_10to17                             = -0.003628;     //se=0.001434
		time_age_18to25                              = 0.009894;     //se=0.001451
		time_age_26to35                             = -0.003055;     //se=0.001540
		time_age_51to60                              = 0.001433;     //se=0.001219
		time_age_61to70                              = 0.000971;     //se=0.001184
		time_age_71plus                             = -0.002193;     //se=0.001443
		
		cost_age_06to09                             = -0.113678;     //se=0.110913
		cost_age_10to17                             = -0.418008;     //se=0.041122
		cost_age_18to25                              = 0.136958;     //se=0.026500
		cost_age_26to35                              = 0.092666;     //se=0.025826
		cost_age_51to60                              = 0.021970;     //se=0.021933
		cost_age_61to70                              = 0.090351;     //se=0.024165
		cost_age_71plus                              = 0.044818;     //se=0.030349
		
		cardriver_sex_female                        = -0.674418;     //se=0.025357
		carpassenger_sex_female                      = 0.600301;     //se=0.030380
		cycling_sex_female                          = -0.485319;     //se=0.030441
		publictransport_sex_female                  = -0.034353;     //se=0.027316
		
		cardriver_age_06to09                        = -1.097672;  //se=6413.889044
		cardriver_age_10to17                        = -1.223083;     //se=0.197294
		cardriver_age_18to25                         = 0.514697;     //se=0.061844
		cardriver_age_26to35                        = -0.463442;     //se=0.050169
		cardriver_age_51to60                         = 0.129077;     //se=0.038092
		cardriver_age_61to70                        = -0.291003;     //se=0.037279
		cardriver_age_71plus                        = -0.277279;     //se=0.043827
		carpassenger_age_06to09                      = 1.529011;     //se=0.065190
		carpassenger_age_10to17                      = 1.608163;     //se=0.052304
		carpassenger_age_18to25                      = 1.385675;     //se=0.071291
		carpassenger_age_26to35                     = -0.188398;     //se=0.069835
		carpassenger_age_51to60                      = 0.385370;     //se=0.053181
		carpassenger_age_61to70                      = 0.454215;     //se=0.049174
		carpassenger_age_71plus                      = 0.418874;     //se=0.056091
		cycling_age_06to09                          = -1.116310;     //se=0.085117
		cycling_age_10to17                           = 0.642268;     //se=0.051848
		cycling_age_18to25                           = 0.186971;     //se=0.076496
		cycling_age_26to35                          = -0.471240;     //se=0.068996
		cycling_age_51to60                          = -0.175560;     //se=0.050421
		cycling_age_61to70                          = -0.460459;     //se=0.054654
		cycling_age_71plus                          = -0.345454;     //se=0.067949
		publictransport_age_06to09                  = -0.988486;     //se=0.131511
		publictransport_age_10to17                   = 0.775829;     //se=0.053392
		publictransport_age_18to25                   = 0.655475;     //se=0.060252
		publictransport_age_26to35                   = 0.053015;     //se=0.055729
		publictransport_age_51to60                   = 0.142965;     //se=0.047749
		publictransport_age_61to70                   = 0.165172;     //se=0.049021
		publictransport_age_71plus                   = 0.445775;     //se=0.056172
		
		cardriver_purpose_business                   = 0.515757;     //se=0.104118
		cardriver_purpose_education                 = -1.068552;     //se=0.073664
		cardriver_purpose_leisure_indoors           = -0.962799;     //se=0.054762
		cardriver_purpose_leisure_other             = -0.469247;     //se=0.048019
		cardriver_purpose_leisure_outdoors           = 0.267916;     //se=0.049826
		cardriver_purpose_leisure_walk              = -2.425378;     //se=0.053963
		cardriver_purpose_other                      = 0.131372;     //se=0.301157
		cardriver_purpose_private_business          = -0.197657;     //se=0.043229
		cardriver_purpose_service                    = 0.551173;     //se=0.043898
		cardriver_purpose_shopping_grocery          = -0.203514;     //se=0.040928
		cardriver_purpose_shopping_other            = -0.036598;     //se=0.057118
		cardriver_purpose_visit                     = -0.320451;     //se=0.049943
		carpassenger_purpose_business                = 0.717387;     //se=0.156866
		carpassenger_purpose_education               = 0.017150;     //se=0.068265
		carpassenger_purpose_leisure_indoors         = 1.376719;     //se=0.069430
		carpassenger_purpose_leisure_other           = 1.230945;     //se=0.064636
		carpassenger_purpose_leisure_outdoors        = 1.873813;     //se=0.065336
		carpassenger_purpose_leisure_walk           = -0.520068;     //se=0.077483
		carpassenger_purpose_other                   = 1.848446;     //se=0.278116
		carpassenger_purpose_private_business        = 1.278303;     //se=0.062977
		carpassenger_purpose_service                 = 0.984752;     //se=0.070469
		carpassenger_purpose_shopping_grocery        = 1.239450;     //se=0.062161
		carpassenger_purpose_shopping_other          = 1.597416;     //se=0.074223
		carpassenger_purpose_visit                   = 1.112013;     //se=0.066592
		cycling_purpose_business                    = -0.292859;     //se=0.159974
		cycling_purpose_education                   = -0.553171;     //se=0.066402
		cycling_purpose_leisure_indoors             = -1.512268;     //se=0.103364
		cycling_purpose_leisure_other               = -0.797559;     //se=0.070294
		cycling_purpose_leisure_outdoors            = -0.295889;     //se=0.069731
		cycling_purpose_leisure_walk                = -1.326126;     //se=0.065659
		cycling_purpose_other                       = -2.302529;     //se=1.020220
		cycling_purpose_private_business            = -0.688058;     //se=0.066750
		cycling_purpose_service                     = -0.872532;     //se=0.073935
		cycling_purpose_shopping_grocery            = -0.370226;     //se=0.060067
		cycling_purpose_shopping_other              = -0.555777;     //se=0.090496
		cycling_purpose_visit                       = -0.882239;     //se=0.077060
		publictransport_purpose_business            = -0.094963;     //se=0.132746
		publictransport_purpose_education           = -0.241253;     //se=0.059908
		publictransport_purpose_leisure_indoors     = -0.689011;     //se=0.063342
		publictransport_purpose_leisure_other       = -0.706146;     //se=0.058335
		publictransport_purpose_leisure_outdoors    = -0.968115;     //se=0.066416
		publictransport_purpose_leisure_walk        = -2.165435;     //se=0.085939
		publictransport_purpose_other               = -0.040508;     //se=0.371814
		publictransport_purpose_private_business    = -1.018404;     //se=0.056301
		publictransport_purpose_service             = -1.706718;     //se=0.078182
		publictransport_purpose_shopping_grocery    = -1.701175;     //se=0.060130
		publictransport_purpose_shopping_other      = -0.643948;     //se=0.070298
		publictransport_purpose_visit               = -1.266630;     //se=0.067032
		
		cardriver_day_Saturday                       = 0.017073;     //se=0.030052
		cardriver_day_Sunday                        = -0.418969;     //se=0.036747
		carpassenger_day_Saturday                    = 0.489639;     //se=0.034978
		carpassenger_day_Sunday                      = 0.203583;     //se=0.041094
		cycling_day_Saturday                        = -0.148034;     //se=0.048639
		cycling_day_Sunday                          = -0.440659;     //se=0.058498
		publictransport_day_Saturday                = -0.061341;     //se=0.043114
		publictransport_day_Sunday                  = -0.687115;     //se=0.054161
		
		cardriver_intrazonal                        = -1.058577;     //se=0.024023
		carpassenger_intrazonal                     = -0.739842;     //se=0.031351
		cycling_intrazonal                           = 0.195679;     //se=0.031695
		publictransport_intrazonal                  = -3.246929;     //se=0.067897
		
		cardriver_num_activities                     = 0.270391;     //se=0.030512
		carpassenger_num_activities                  = 0.257202;     //se=0.035203
		cycling_num_activities                       = 0.157524;     //se=0.046726
		publictransport_num_activities               = 0.243145;     //se=0.037183
		
		cardriver_containsBusiness                   = 0.280038;     //se=0.136773
		cardriver_containsLeisure                    = 0.445470;     //se=0.073240
		cardriver_containsPrivateB                   = 0.141330;     //se=0.072818
		cardriver_containsService                   = -0.012399;     //se=0.127239
		cardriver_containsShopping                   = 0.220839;     //se=0.060747
		cardriver_containsStrolling                 = -1.447244;     //se=0.128442
		cardriver_containsVisit                      = 0.336435;     //se=0.091102
		carpassenger_containsBusiness               = -0.313192;     //se=0.215007
		carpassenger_containsLeisure                 = 0.943762;     //se=0.079125
		carpassenger_containsPrivateB                = 0.062649;     //se=0.086620
		carpassenger_containsService                = -0.328759;     //se=0.171368
		carpassenger_containsShopping                = 0.045155;     //se=0.074378
		carpassenger_containsStrolling              = -1.217362;     //se=0.159322
		carpassenger_containsVisit                   = 0.033099;     //se=0.099763
		cycling_containsBusiness                    = -0.025020;     //se=0.186472
		cycling_containsLeisure                      = 0.116251;     //se=0.109146
		cycling_containsPrivateB                     = 0.024562;     //se=0.110479
		cycling_containsService                     = -0.778625;     //se=0.183003
		cycling_containsShopping                     = 0.082577;     //se=0.094840
		cycling_containsStrolling                   = -0.772697;     //se=0.198211
		cycling_containsVisit                       = -0.121845;     //se=0.127865
		publictransport_containsBusiness             = 0.001813;     //se=0.158809
		publictransport_containsLeisure              = 0.602243;     //se=0.084680
		publictransport_containsPrivateB            = -0.046419;     //se=0.089351
		publictransport_containsService             = -1.706409;     //se=0.167932
		publictransport_containsShopping             = 0.130030;     //se=0.074252
		publictransport_containsStrolling           = -0.863026;     //se=0.151803
		publictransport_containsVisit               = -0.257201;     //se=0.108381
		
			
				
			// Referenzklasse für Altersgruppe:
		time_age_36to50 = 0.0;
		cost_age_36to50 = 0.0;
		
	// System.out.println("test");	


		init();
	}



}
