package edu.kit.ifv.mobitopp.simulation.tour;



public class TourModeChoiceParameterTimeCostByAgeHhtSex extends TourModeChoiceParameterTimeCostByAgeHhtSexBase 
	implements TourModeChoiceParameter
{

	


	/*


Call:
mlogit(formula = mode ~ notavailable + time + time:sex. + time:age. + 
    time:hhtype. + cost + cost:sex. + cost:age. + cost:hhtype. | 
    sex. + age. + hhtype. + purpose. + day. + intrazonal. + num_activities + 
        containsStrolling. + containsVisit. + containsPrivateB. + 
        containsService. + containsLeisure. + containsShopping. + 
        containsBusiness., data = trainingdata, reflevel = "walking", 
    method = "nr", print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2835          0.3918          0.1295          0.0552          0.1401 

nr method
24 iterations, 0h:13m:14s 
g'(-H)^-1g = 9.64E-07 
gradient close to zero 

Coefficients :
                                            Estimate  Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                       0.726180    0.060895   11.93  < 2e-16 ***
carpassenger:(intercept)                   -3.420534    0.081906  -41.76  < 2e-16 ***
cycling:(intercept)                        -1.432940    0.080238  -17.86  < 2e-16 ***
publictransport:(intercept)                 0.196178    0.066018    2.97  0.00296 ** 
notavailable                              -24.848755 1747.984076   -0.01  0.98866    
time                                       -0.024900    0.001085  -22.95  < 2e-16 ***
cost                                       -0.400046    0.020190  -19.81  < 2e-16 ***
time:sex.female                            -0.005752    0.000633   -9.09  < 2e-16 ***
time:age.06to09                            -0.014984    0.002293   -6.54  6.3e-11 ***
time:age.10to17                            -0.001376    0.001266   -1.09  0.27710    
time:age.18to25                             0.008105    0.001224    6.62  3.5e-11 ***
time:age.26to35                            -0.007020    0.001306   -5.37  7.7e-08 ***
time:age.51to60                            -0.001013    0.001129   -0.90  0.36956    
time:age.61to70                             0.001061    0.001194    0.89  0.37430    
time:age.71plus                            -0.005596    0.001467   -3.82  0.00014 ***
time:hhtype.single                          0.002070    0.001161    1.78  0.07458 .  
time:hhtype.kids_0to7                       0.006054    0.001190    5.09  3.6e-07 ***
time:hhtype.kids_8to12                     -0.005361    0.001435   -3.74  0.00019 ***
time:hhtype.kids_13to18                    -0.001101    0.001316   -0.84  0.40278    
time:hhtype.multiadult                      0.002458    0.001112    2.21  0.02703 *  
cost:sex.female                            -0.026796    0.012725   -2.11  0.03522 *  
cost:age.06to09                            -0.359665    0.111154   -3.24  0.00121 ** 
cost:age.10to17                            -0.587666    0.041480  -14.17  < 2e-16 ***
cost:age.18to25                             0.043421    0.024468    1.77  0.07596 .  
cost:age.26to35                            -0.016790    0.023020   -0.73  0.46577    
cost:age.51to60                            -0.000679    0.020466   -0.03  0.97354    
cost:age.61to70                             0.037222    0.024229    1.54  0.12447    
cost:age.71plus                             0.007738    0.029837    0.26  0.79538    
cost:hhtype.single                         -0.128336    0.026511   -4.84  1.3e-06 ***
cost:hhtype.kids_0to7                      -0.053359    0.023996   -2.22  0.02617 *  
cost:hhtype.kids_8to12                     -0.118041    0.025834   -4.57  4.9e-06 ***
cost:hhtype.kids_13to18                    -0.101569    0.024047   -4.22  2.4e-05 ***
cost:hhtype.multiadult                     -0.023627    0.020416   -1.16  0.24716    
cardriver:sex.female                       -0.685043    0.024912  -27.50  < 2e-16 ***
carpassenger:sex.female                     0.611446    0.029588   20.67  < 2e-16 ***
cycling:sex.female                         -0.490220    0.030340  -16.16  < 2e-16 ***
publictransport:sex.female                 -0.038032    0.026785   -1.42  0.15564    
cardriver:age.06to09                       -0.970698 6292.205592    0.00  0.99988    
carpassenger:age.06to09                     1.417581    0.067431   21.02  < 2e-16 ***
cycling:age.06to09                         -1.236324    0.086389  -14.31  < 2e-16 ***
publictransport:age.06to09                 -0.784571    0.129982   -6.04  1.6e-09 ***
cardriver:age.10to17                       -1.288802    0.200254   -6.44  1.2e-10 ***
carpassenger:age.10to17                     1.476943    0.053946   27.38  < 2e-16 ***
cycling:age.10to17                          0.521026    0.053616    9.72  < 2e-16 ***
publictransport:age.10to17                  0.817723    0.055179   14.82  < 2e-16 ***
cardriver:age.18to25                        0.499541    0.063239    7.90  2.9e-15 ***
carpassenger:age.18to25                     1.197854    0.073023   16.40  < 2e-16 ***
cycling:age.18to25                          0.214205    0.079329    2.70  0.00693 ** 
publictransport:age.18to25                  0.844957    0.063055   13.40  < 2e-16 ***
cardriver:age.26to35                       -0.279297    0.050862   -5.49  4.0e-08 ***
carpassenger:age.26to35                    -0.234929    0.070063   -3.35  0.00080 ***
cycling:age.26to35                         -0.392762    0.070727   -5.55  2.8e-08 ***
publictransport:age.26to35                  0.199346    0.056124    3.55  0.00038 ***
cardriver:age.51to60                        0.199247    0.042812    4.65  3.3e-06 ***
carpassenger:age.51to60                     0.265775    0.058596    4.54  5.7e-06 ***
cycling:age.51to60                         -0.030014    0.056421   -0.53  0.59475    
publictransport:age.51to60                  0.178379    0.050565    3.53  0.00042 ***
cardriver:age.61to70                        0.033969    0.048105    0.71  0.48010    
carpassenger:age.61to70                     0.459327    0.063094    7.28  3.3e-13 ***
cycling:age.61to70                         -0.148517    0.068463   -2.17  0.03006 *  
publictransport:age.61to70                  0.216520    0.057119    3.79  0.00015 ***
cardriver:age.71plus                       -0.001405    0.054266   -0.03  0.97934    
carpassenger:age.71plus                     0.445275    0.069552    6.40  1.5e-10 ***
cycling:age.71plus                         -0.043650    0.081062   -0.54  0.59025    
publictransport:age.71plus                  0.458664    0.063753    7.19  6.3e-13 ***
cardriver:hhtype.single                     0.263845    0.050642    5.21  1.9e-07 ***
carpassenger:hhtype.single                 -1.905217    0.079311  -24.02  < 2e-16 ***
cycling:hhtype.single                       0.008032    0.071613    0.11  0.91070    
publictransport:hhtype.single               0.150163    0.050106    3.00  0.00273 ** 
cardriver:hhtype.kids_0to7                  0.217833    0.049965    4.36  1.3e-05 ***
carpassenger:hhtype.kids_0to7              -0.221718    0.064338   -3.45  0.00057 ***
cycling:hhtype.kids_0to7                    0.378160    0.064597    5.85  4.8e-09 ***
publictransport:hhtype.kids_0to7            0.031446    0.056038    0.56  0.57469    
cardriver:hhtype.kids_8to12                 0.565048    0.053131   10.63  < 2e-16 ***
carpassenger:hhtype.kids_8to12             -0.160708    0.065276   -2.46  0.01382 *  
cycling:hhtype.kids_8to12                   0.484891    0.064737    7.49  6.9e-14 ***
publictransport:hhtype.kids_8to12           0.217373    0.056747    3.83  0.00013 ***
cardriver:hhtype.kids_13to18                0.660754    0.050566   13.07  < 2e-16 ***
carpassenger:hhtype.kids_13to18            -0.020940    0.063798   -0.33  0.74274    
cycling:hhtype.kids_13to18                  0.398772    0.064056    6.23  4.8e-10 ***
publictransport:hhtype.kids_13to18          0.333591    0.054794    6.09  1.1e-09 ***
cardriver:hhtype.multiadult                 0.434904    0.043071   10.10  < 2e-16 ***
carpassenger:hhtype.multiadult              0.112046    0.054855    2.04  0.04109 *  
cycling:hhtype.multiadult                   0.277454    0.060291    4.60  4.2e-06 ***
publictransport:hhtype.multiadult           0.013634    0.050122    0.27  0.78562    
cardriver:purpose.business                  0.518998    0.103439    5.02  5.2e-07 ***
carpassenger:purpose.business               0.441761    0.161316    2.74  0.00617 ** 
cycling:purpose.business                   -0.346508    0.160401   -2.16  0.03075 *  
publictransport:purpose.business           -0.264141    0.131990   -2.00  0.04537 *  
cardriver:purpose.education                -1.124490    0.072805  -15.45  < 2e-16 ***
carpassenger:purpose.education             -0.104280    0.067585   -1.54  0.12284    
cycling:purpose.education                  -0.595152    0.066104   -9.00  < 2e-16 ***
publictransport:purpose.education          -0.277647    0.058669   -4.73  2.2e-06 ***
cardriver:purpose.service                   0.529691    0.044989   11.77  < 2e-16 ***
carpassenger:purpose.service                0.822640    0.070986   11.59  < 2e-16 ***
cycling:purpose.service                    -0.947352    0.075100  -12.61  < 2e-16 ***
publictransport:purpose.service            -1.717796    0.078310  -21.94  < 2e-16 ***
cardriver:purpose.private_business         -0.210535    0.043013   -4.89  9.8e-07 ***
carpassenger:purpose.private_business       1.159106    0.062316   18.60  < 2e-16 ***
cycling:purpose.private_business           -0.733954    0.066747  -11.00  < 2e-16 ***
publictransport:purpose.private_business   -1.070100    0.055406  -19.31  < 2e-16 ***
cardriver:purpose.visit                    -0.248243    0.049065   -5.06  4.2e-07 ***
carpassenger:purpose.visit                  1.099045    0.065301   16.83  < 2e-16 ***
cycling:purpose.visit                      -0.865380    0.075673  -11.44  < 2e-16 ***
publictransport:purpose.visit              -1.261096    0.064975  -19.41  < 2e-16 ***
cardriver:purpose.shopping_grocery         -0.227076    0.040820   -5.56  2.7e-08 ***
carpassenger:purpose.shopping_grocery       1.049949    0.061612   17.04  < 2e-16 ***
cycling:purpose.shopping_grocery           -0.420383    0.059916   -7.02  2.3e-12 ***
publictransport:purpose.shopping_grocery   -1.753033    0.059453  -29.49  < 2e-16 ***
cardriver:purpose.shopping_other           -0.042706    0.056903   -0.75  0.45294    
carpassenger:purpose.shopping_other         1.504994    0.073389   20.51  < 2e-16 ***
cycling:purpose.shopping_other             -0.588160    0.090485   -6.50  8.0e-11 ***
publictransport:purpose.shopping_other     -0.678732    0.069125   -9.82  < 2e-16 ***
cardriver:purpose.leisure_indoors          -0.950495    0.054213  -17.53  < 2e-16 ***
carpassenger:purpose.leisure_indoors        1.328222    0.068483   19.39  < 2e-16 ***
cycling:purpose.leisure_indoors            -1.518148    0.103667  -14.64  < 2e-16 ***
publictransport:purpose.leisure_indoors    -0.712062    0.061916  -11.50  < 2e-16 ***
cardriver:purpose.leisure_outdoors          0.259703    0.049731    5.22  1.8e-07 ***
carpassenger:purpose.leisure_outdoors       1.754968    0.064728   27.11  < 2e-16 ***
cycling:purpose.leisure_outdoors           -0.335781    0.069651   -4.82  1.4e-06 ***
publictransport:purpose.leisure_outdoors   -1.018904    0.065688  -15.51  < 2e-16 ***
cardriver:purpose.leisure_other            -0.493282    0.047740  -10.33  < 2e-16 ***
carpassenger:purpose.leisure_other          1.113936    0.063960   17.42  < 2e-16 ***
cycling:purpose.leisure_other              -0.829392    0.069946  -11.86  < 2e-16 ***
publictransport:purpose.leisure_other      -0.745542    0.057165  -13.04  < 2e-16 ***
cardriver:purpose.leisure_walk             -2.414821    0.053386  -45.23  < 2e-16 ***
carpassenger:purpose.leisure_walk          -0.637532    0.076118   -8.38  < 2e-16 ***
cycling:purpose.leisure_walk               -1.349561    0.065275  -20.68  < 2e-16 ***
publictransport:purpose.leisure_walk       -2.218542    0.084409  -26.28  < 2e-16 ***
cardriver:purpose.other                     0.278201    0.269638    1.03  0.30219    
carpassenger:purpose.other                  1.592918    0.253394    6.29  3.3e-10 ***
cycling:purpose.other                      -1.012424    0.485322   -2.09  0.03697 *  
publictransport:purpose.other              -0.207605    0.307882   -0.67  0.50012    
cardriver:day.Saturday                      0.021173    0.030003    0.71  0.48037    
carpassenger:day.Saturday                   0.502228    0.034650   14.49  < 2e-16 ***
cycling:day.Saturday                       -0.142461    0.048329   -2.95  0.00320 ** 
publictransport:day.Saturday               -0.062642    0.042428   -1.48  0.13983    
cardriver:day.Sunday                       -0.408746    0.036467  -11.21  < 2e-16 ***
carpassenger:day.Sunday                     0.245497    0.040387    6.08  1.2e-09 ***
cycling:day.Sunday                         -0.437913    0.058017   -7.55  4.4e-14 ***
publictransport:day.Sunday                 -0.666555    0.053164  -12.54  < 2e-16 ***
cardriver:intrazonal.TRUE                  -1.239802    0.024091  -51.46  < 2e-16 ***
carpassenger:intrazonal.TRUE               -1.001518    0.031043  -32.26  < 2e-16 ***
cycling:intrazonal.TRUE                     0.028054    0.031679    0.89  0.37585    
publictransport:intrazonal.TRUE            -3.419855    0.067849  -50.40  < 2e-16 ***
cardriver:num_activities                    0.230945    0.028914    7.99  1.3e-15 ***
carpassenger:num_activities                 0.189093    0.033194    5.70  1.2e-08 ***
cycling:num_activities                      0.157579    0.043429    3.63  0.00029 ***
publictransport:num_activities              0.212191    0.034294    6.19  6.1e-10 ***
cardriver:containsStrolling.TRUE           -1.147441    0.131254   -8.74  < 2e-16 ***
carpassenger:containsStrolling.TRUE        -0.952424    0.162554   -5.86  4.7e-09 ***
cycling:containsStrolling.TRUE             -0.683619    0.208119   -3.28  0.00102 ** 
publictransport:containsStrolling.TRUE     -0.557702    0.150216   -3.71  0.00021 ***
cardriver:containsVisit.TRUE                0.478174    0.093580    5.11  3.2e-07 ***
carpassenger:containsVisit.TRUE             0.226769    0.099626    2.28  0.02283 *  
cycling:containsVisit.TRUE                 -0.203378    0.136502   -1.49  0.13624    
publictransport:containsVisit.TRUE         -0.180794    0.106760   -1.69  0.09037 .  
cardriver:containsPrivateB.TRUE             0.208212    0.071741    2.90  0.00370 ** 
carpassenger:containsPrivateB.TRUE          0.240784    0.084901    2.84  0.00457 ** 
cycling:containsPrivateB.TRUE               0.055536    0.108208    0.51  0.60778    
publictransport:containsPrivateB.TRUE      -0.002219    0.086273   -0.03  0.97948    
cardriver:containsService.TRUE              0.159686    0.124365    1.28  0.19914    
carpassenger:containsService.TRUE          -0.111801    0.166665   -0.67  0.50234    
cycling:containsService.TRUE               -0.800554    0.180829   -4.43  9.5e-06 ***
publictransport:containsService.TRUE       -1.562599    0.163271   -9.57  < 2e-16 ***
cardriver:containsLeisure.TRUE              0.475102    0.071436    6.65  2.9e-11 ***
carpassenger:containsLeisure.TRUE           1.039779    0.077249   13.46  < 2e-16 ***
cycling:containsLeisure.TRUE                0.108766    0.106966    1.02  0.30924    
publictransport:containsLeisure.TRUE        0.628493    0.081575    7.70  1.3e-14 ***
cardriver:containsShopping.TRUE             0.257104    0.059359    4.33  1.5e-05 ***
carpassenger:containsShopping.TRUE          0.140077    0.072524    1.93  0.05343 .  
cycling:containsShopping.TRUE               0.103070    0.091981    1.12  0.26248    
publictransport:containsShopping.TRUE       0.126133    0.071296    1.77  0.07687 .  
cardriver:containsBusiness.TRUE             0.362874    0.134693    2.69  0.00706 ** 
carpassenger:containsBusiness.TRUE         -0.200564    0.215933   -0.93  0.35298    
cycling:containsBusiness.TRUE               0.005363    0.183213    0.03  0.97665    
publictransport:containsBusiness.TRUE       0.126247    0.153305    0.82  0.41022    
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -102000
McFadden R^2:  0.385 
Likelihood ratio test : chisq = 128000 (p.value = <2e-16)

		*/
	
	
	public TourModeChoiceParameterTimeCostByAgeHhtSex() {
	
		
	// Referenzklasse für Altersgruppe:
		time_age_36to50 = 0.0;
		cost_age_36to50 = 0.0;
		
		
		/* *********************************************************************************************** */

		walking																			=  0.0				+ 0.7;
		cardriver                                   =  0.726180;    // se=0.060895
		carpassenger                                = -3.420534		- 1.8;    // se=0.081906
		cycling                                     = -1.432940		+ 0.1;    // se=0.080238
		publictransport                             =  0.196178		+ 1.2;    // se=0.066018
		
		notavailable                               = -24.848755; // se=1747.984076
		
		time                                        = -0.024900;    // se=0.001085
		cost                                        = -0.400046;    // se=0.020190
		
		time_sex_female                             = -0.005752;    // se=0.000633
		
		time_age_06to09                             = -0.014984;    // se=0.002293
		time_age_10to17                             = -0.001376;    // se=0.001266
		time_age_18to25                              = 0.008105;    // se=0.001224
		time_age_26to35                             = -0.007020;    // se=0.001306
		time_age_51to60                             = -0.001013;    // se=0.001129
		time_age_61to70                              = 0.001061;    // se=0.001194
		time_age_71plus                             = -0.005596;    // se=0.001467
		
		time_hhtype_single                           = 0.002070;    // se=0.001161
		time_hhtype_kids_0to7                        = 0.006054;    // se=0.001190
		time_hhtype_kids_8to12                      = -0.005361;    // se=0.001435
		time_hhtype_kids_13to18                     = -0.001101;    // se=0.001316
		time_hhtype_multiadult                       = 0.002458;    // se=0.001112
		
		cost_sex_female                             = -0.026796;    // se=0.012725
		
		cost_age_06to09                             = -0.359665;    // se=0.111154
		cost_age_10to17                             = -0.587666;    // se=0.041480
		cost_age_18to25                              = 0.043421;    // se=0.024468
		cost_age_26to35                             = -0.016790;    // se=0.023020
		cost_age_51to60                             = -0.000679;    // se=0.020466
		cost_age_61to70                              = 0.037222;    // se=0.024229
		cost_age_71plus                              = 0.007738;    // se=0.029837
		
		cost_hhtype_single                          = -0.128336;    // se=0.026511
		cost_hhtype_kids_0to7                       = -0.053359;    // se=0.023996
		cost_hhtype_kids_8to12                      = -0.118041;    // se=0.025834
		cost_hhtype_kids_13to18                     = -0.101569;    // se=0.024047
		cost_hhtype_multiadult                      = -0.023627;    // se=0.020416
		
		cardriver_sex_female                        = -0.685043;    // se=0.024912
		carpassenger_sex_female                      = 0.611446;    // se=0.029588
		cycling_sex_female                          = -0.490220;    // se=0.030340
		publictransport_sex_female                  = -0.038032;    // se=0.026785
		
		cardriver_age_06to09                        = -0.970698; // se=6292.205592
		carpassenger_age_06to09                      = 1.417581;    // se=0.067431
		cycling_age_06to09                          = -1.236324;    // se=0.086389
		publictransport_age_06to09                  = -0.784571;    // se=0.129982
		cardriver_age_10to17                        = -1.288802;    // se=0.200254
		carpassenger_age_10to17                      = 1.476943;    // se=0.053946
		cycling_age_10to17                           = 0.521026;    // se=0.053616
		publictransport_age_10to17                   = 0.817723;    // se=0.055179
		cardriver_age_18to25                         = 0.499541;    // se=0.063239
		carpassenger_age_18to25                      = 1.197854;    // se=0.073023
		cycling_age_18to25                           = 0.214205;    // se=0.079329
		publictransport_age_18to25                   = 0.844957;    // se=0.063055
		cardriver_age_26to35                        = -0.279297;    // se=0.050862
		carpassenger_age_26to35                     = -0.234929;    // se=0.070063
		cycling_age_26to35                          = -0.392762;    // se=0.070727
		publictransport_age_26to35                   = 0.199346;    // se=0.056124
		cardriver_age_51to60                         = 0.199247;    // se=0.042812
		carpassenger_age_51to60                      = 0.265775;    // se=0.058596
		cycling_age_51to60                          = -0.030014;    // se=0.056421
		publictransport_age_51to60                   = 0.178379;    // se=0.050565
		cardriver_age_61to70                         = 0.033969;    // se=0.048105
		carpassenger_age_61to70                      = 0.459327;    // se=0.063094
		cycling_age_61to70                          = -0.148517;    // se=0.068463
		publictransport_age_61to70                   = 0.216520;    // se=0.057119
		cardriver_age_71plus                        = -0.001405;    // se=0.054266
		carpassenger_age_71plus                      = 0.445275;    // se=0.069552
		cycling_age_71plus                          = -0.043650;    // se=0.081062
		publictransport_age_71plus                   = 0.458664;    // se=0.063753
		
		cardriver_hhtype_single                      = 0.263845;    // se=0.050642
		carpassenger_hhtype_single                  = -1.905217;    // se=0.079311
		cycling_hhtype_single                        = 0.008032;    // se=0.071613
		publictransport_hhtype_single                = 0.150163;    // se=0.050106
		cardriver_hhtype_kids_0to7                   = 0.217833;    // se=0.049965
		carpassenger_hhtype_kids_0to7               = -0.221718;    // se=0.064338
		cycling_hhtype_kids_0to7                     = 0.378160;    // se=0.064597
		publictransport_hhtype_kids_0to7             = 0.031446;    // se=0.056038
		cardriver_hhtype_kids_8to12                  = 0.565048;    // se=0.053131
		carpassenger_hhtype_kids_8to12              = -0.160708;    // se=0.065276
		cycling_hhtype_kids_8to12                    = 0.484891;    // se=0.064737
		publictransport_hhtype_kids_8to12            = 0.217373;    // se=0.056747
		cardriver_hhtype_kids_13to18                 = 0.660754;    // se=0.050566
		carpassenger_hhtype_kids_13to18             = -0.020940;    // se=0.063798
		cycling_hhtype_kids_13to18                   = 0.398772;    // se=0.064056
		publictransport_hhtype_kids_13to18           = 0.333591;    // se=0.054794
		cardriver_hhtype_multiadult                  = 0.434904;    // se=0.043071
		carpassenger_hhtype_multiadult               = 0.112046;    // se=0.054855
		cycling_hhtype_multiadult                    = 0.277454;    // se=0.060291
		publictransport_hhtype_multiadult            = 0.013634;    // se=0.050122
		
		cardriver_purpose_business                   = 0.518998;    // se=0.103439
		carpassenger_purpose_business                = 0.441761;    // se=0.161316
		cycling_purpose_business                    = -0.346508;    // se=0.160401
		publictransport_purpose_business            = -0.264141;    // se=0.131990
		cardriver_purpose_education                 = -1.124490;    // se=0.072805
		carpassenger_purpose_education              = -0.104280;    // se=0.067585
		cycling_purpose_education                   = -0.595152;    // se=0.066104
		publictransport_purpose_education           = -0.277647;    // se=0.058669
		cardriver_purpose_service                    = 0.529691;    // se=0.044989
		carpassenger_purpose_service                 = 0.822640;    // se=0.070986
		cycling_purpose_service                     = -0.947352;    // se=0.075100
		publictransport_purpose_service             = -1.717796;    // se=0.078310
		cardriver_purpose_private_business          = -0.210535;    // se=0.043013
		carpassenger_purpose_private_business        = 1.159106;    // se=0.062316
		cycling_purpose_private_business            = -0.733954;    // se=0.066747
		publictransport_purpose_private_business    = -1.070100;    // se=0.055406
		cardriver_purpose_visit                     = -0.248243;    // se=0.049065
		carpassenger_purpose_visit                   = 1.099045;    // se=0.065301
		cycling_purpose_visit                       = -0.865380;    // se=0.075673
		publictransport_purpose_visit               = -1.261096;    // se=0.064975
		cardriver_purpose_shopping_grocery          = -0.227076;    // se=0.040820
		carpassenger_purpose_shopping_grocery        = 1.049949;    // se=0.061612
		cycling_purpose_shopping_grocery            = -0.420383;    // se=0.059916
		publictransport_purpose_shopping_grocery    = -1.753033;    // se=0.059453
		cardriver_purpose_shopping_other            = -0.042706;    // se=0.056903
		carpassenger_purpose_shopping_other          = 1.504994;    // se=0.073389
		cycling_purpose_shopping_other              = -0.588160;    // se=0.090485
		publictransport_purpose_shopping_other      = -0.678732;    // se=0.069125
		cardriver_purpose_leisure_indoors           = -0.950495;    // se=0.054213
		carpassenger_purpose_leisure_indoors         = 1.328222;    // se=0.068483
		cycling_purpose_leisure_indoors             = -1.518148;    // se=0.103667
		publictransport_purpose_leisure_indoors     = -0.712062;    // se=0.061916
		cardriver_purpose_leisure_outdoors           = 0.259703;    // se=0.049731
		carpassenger_purpose_leisure_outdoors        = 1.754968;    // se=0.064728
		cycling_purpose_leisure_outdoors            = -0.335781;    // se=0.069651
		publictransport_purpose_leisure_outdoors    = -1.018904;    // se=0.065688
		cardriver_purpose_leisure_other             = -0.493282;    // se=0.047740
		carpassenger_purpose_leisure_other           = 1.113936;    // se=0.063960
		cycling_purpose_leisure_other               = -0.829392;    // se=0.069946
		publictransport_purpose_leisure_other       = -0.745542;    // se=0.057165
		cardriver_purpose_leisure_walk              = -2.414821;    // se=0.053386
		carpassenger_purpose_leisure_walk           = -0.637532;    // se=0.076118
		cycling_purpose_leisure_walk                = -1.349561;    // se=0.065275
		publictransport_purpose_leisure_walk        = -2.218542;    // se=0.084409
		cardriver_purpose_other                      = 0.278201;    // se=0.269638
		carpassenger_purpose_other                   = 1.592918;    // se=0.253394
		cycling_purpose_other                       = -1.012424;    // se=0.485322
		publictransport_purpose_other               = -0.207605;    // se=0.307882
		
		cardriver_day_Saturday                       = 0.021173;    // se=0.030003
		carpassenger_day_Saturday                    = 0.502228;    // se=0.034650
		cycling_day_Saturday                        = -0.142461;    // se=0.048329
		publictransport_day_Saturday                = -0.062642;    // se=0.042428
		cardriver_day_Sunday                        = -0.408746;    // se=0.036467
		carpassenger_day_Sunday                      = 0.245497;    // se=0.040387
		cycling_day_Sunday                          = -0.437913;    // se=0.058017
		publictransport_day_Sunday                  = -0.666555;    // se=0.053164
		
		cardriver_intrazonal                        = -1.239802;    // se=0.024091
		carpassenger_intrazonal                     = -1.001518;    // se=0.031043
		cycling_intrazonal                           = 0.028054;    // se=0.031679
		publictransport_intrazonal                  = -3.419855;    // se=0.067849
		
		cardriver_num_activities                     = 0.230945;    // se=0.028914
		carpassenger_num_activities                  = 0.189093;    // se=0.033194
		cycling_num_activities                       = 0.157579;    // se=0.043429
		publictransport_num_activities               = 0.212191;    // se=0.034294
		
		cardriver_containsStrolling                 = -1.147441;    // se=0.131254
		carpassenger_containsStrolling              = -0.952424;    // se=0.162554
		cycling_containsStrolling                   = -0.683619;    // se=0.208119
		publictransport_containsStrolling           = -0.557702;    // se=0.150216
		cardriver_containsVisit                      = 0.478174;    // se=0.093580
		carpassenger_containsVisit                   = 0.226769;    // se=0.099626
		cycling_containsVisit                       = -0.203378;    // se=0.136502
		publictransport_containsVisit               = -0.180794;    // se=0.106760
		cardriver_containsPrivateB                   = 0.208212;    // se=0.071741
		carpassenger_containsPrivateB                = 0.240784;    // se=0.084901
		cycling_containsPrivateB                     = 0.055536;    // se=0.108208
		publictransport_containsPrivateB            = -0.002219;    // se=0.086273
		cardriver_containsService                    = 0.159686;    // se=0.124365
		carpassenger_containsService                = -0.111801;    // se=0.166665
		cycling_containsService                     = -0.800554;    // se=0.180829
		publictransport_containsService             = -1.562599;    // se=0.163271
		cardriver_containsLeisure                    = 0.475102;    // se=0.071436
		carpassenger_containsLeisure                 = 1.039779;    // se=0.077249
		cycling_containsLeisure                      = 0.108766;    // se=0.106966
		publictransport_containsLeisure              = 0.628493;    // se=0.081575
		cardriver_containsShopping                   = 0.257104;    // se=0.059359
		carpassenger_containsShopping                = 0.140077;    // se=0.072524
		cycling_containsShopping                     = 0.103070;    // se=0.091981
		publictransport_containsShopping             = 0.126133;    // se=0.071296
		cardriver_containsBusiness                   = 0.362874;    // se=0.134693
		carpassenger_containsBusiness               = -0.200564;    // se=0.215933
		cycling_containsBusiness                     = 0.005363;    // se=0.183213
		publictransport_containsBusiness             = 0.126247;    // se=0.153305
		
	System.out.println("test");	


		init();
	}



}
