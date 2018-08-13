package edu.kit.ifv.mobitopp.simulation.tour;



public class TourModeChoiceParameterTimeCostByEmpSex 
	extends TourModeChoiceParameterTimeCostByEmpSexBase 
	implements TourModeChoiceParameter
{

	


	/*

Call:
mlogit(formula = mode ~ notavailable + time + time:sex. + time:employment. + 
    cost + cost:sex. + cost:employment. | sex. + employment. + 
    purpose. + day. + intrazonal. + num_activities + containsStrolling. + 
    containsVisit. + containsPrivateB. + containsService. + containsLeisure. + 
    containsShopping. + containsBusiness., data = trainingdata, 
    reflevel = "walking", method = "nr", print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2821          0.3958          0.1301          0.0536          0.1385 

nr method
24 iterations, 0h:5m:59s 
g'(-H)^-1g = 4.81E-07 
gradient close to zero 

Coefficients :
                                                Estimate  Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                           1.100896    0.068386   16.10  < 2e-16 ***
carpassenger:(intercept)                       -3.462956    0.095612  -36.22  < 2e-16 ***
cycling:(intercept)                            -1.401308    0.089917  -15.58  < 2e-16 ***
publictransport:(intercept)                     0.457483    0.073556    6.22  5.0e-10 ***
notavailable                                  -25.099613 2470.847628   -0.01  0.99189    
time                                           -0.024115    0.000832  -28.97  < 2e-16 ***
cost                                           -0.485238    0.014700  -33.01  < 2e-16 ***
time:sex.female                                -0.007246    0.000992   -7.30  2.8e-13 ***
time:employment.parttime                        0.003764    0.001604    2.35  0.01896 *  
time:employment.marginal                        0.010244    0.002461    4.16  3.2e-05 ***
time:employment.homekeeper                      0.008009    0.001967    4.07  4.7e-05 ***
time:employment.unemployed                     -0.008787    0.005165   -1.70  0.08891 .  
time:employment.retired                         0.005195    0.001154    4.50  6.7e-06 ***
time:employment.student_primary                -0.015317    0.003207   -4.78  1.8e-06 ***
time:employment.student_secondary              -0.004402    0.001620   -2.72  0.00657 ** 
time:employment.student_tertiary                0.007390    0.002334    3.17  0.00154 ** 
time:employment.apprentice                      0.004757    0.003391    1.40  0.16060    
time:employment.other                          -0.008915    0.004485   -1.99  0.04684 *  
cost:sex.female                                -0.150297    0.020873   -7.20  6.0e-13 ***
cost:employment.parttime                        0.186230    0.032599    5.71  1.1e-08 ***
cost:employment.marginal                        0.372754    0.060091    6.20  5.5e-10 ***
cost:employment.homekeeper                      0.298956    0.051319    5.83  5.7e-09 ***
cost:employment.unemployed                      0.306659    0.094380    3.25  0.00116 ** 
cost:employment.retired                         0.171107    0.024312    7.04  2.0e-12 ***
cost:employment.student_primary                -0.421582    0.145079   -2.91  0.00366 ** 
cost:employment.student_secondary              -0.352884    0.045672   -7.73  1.1e-14 ***
cost:employment.student_tertiary                0.222413    0.043721    5.09  3.6e-07 ***
cost:employment.apprentice                     -0.000654    0.055130   -0.01  0.99053    
cost:employment.other                           0.140005    0.082535    1.70  0.08983 .  
cardriver:sex.female                           -0.742352    0.041191  -18.02  < 2e-16 ***
carpassenger:sex.female                         0.406870    0.045306    8.98  < 2e-16 ***
cycling:sex.female                             -0.632539    0.049565  -12.76  < 2e-16 ***
publictransport:sex.female                      0.014626    0.041098    0.36  0.72192    
cardriver:employment.parttime                   0.166481    0.061997    2.69  0.00725 ** 
carpassenger:employment.parttime                0.411396    0.081235    5.06  4.1e-07 ***
cycling:employment.parttime                     0.573302    0.078279    7.32  2.4e-13 ***
publictransport:employment.parttime            -0.191042    0.072701   -2.63  0.00859 ** 
cardriver:employment.marginal                   0.283347    0.087675    3.23  0.00123 ** 
carpassenger:employment.marginal                0.865199    0.114474    7.56  4.1e-14 ***
cycling:employment.marginal                     0.668666    0.115535    5.79  7.1e-09 ***
publictransport:employment.marginal            -0.770981    0.151474   -5.09  3.6e-07 ***
cardriver:employment.homekeeper                 0.033815    0.071984    0.47  0.63853    
carpassenger:employment.homekeeper              0.516428    0.092143    5.60  2.1e-08 ***
cycling:employment.homekeeper                   0.202152    0.108456    1.86  0.06234 .  
publictransport:employment.homekeeper          -0.595552    0.122051   -4.88  1.1e-06 ***
cardriver:employment.unemployed                -0.728491    0.175620   -4.15  3.4e-05 ***
carpassenger:employment.unemployed             -0.128440    0.204790   -0.63  0.53054    
cycling:employment.unemployed                  -0.594626    0.267369   -2.22  0.02615 *  
publictransport:employment.unemployed          -0.114465    0.199319   -0.57  0.56578    
cardriver:employment.retired                   -0.212223    0.049193   -4.31  1.6e-05 ***
carpassenger:employment.retired                 0.459641    0.067253    6.83  8.2e-12 ***
cycling:employment.retired                     -0.243651    0.077121   -3.16  0.00158 ** 
publictransport:employment.retired              0.120796    0.064758    1.87  0.06213 .  
cardriver:employment.student_primary           -0.932147 9065.624835    0.00  0.99992    
carpassenger:employment.student_primary         1.141004    0.096050   11.88  < 2e-16 ***
cycling:employment.student_primary             -1.025620    0.127899   -8.02  1.1e-15 ***
publictransport:employment.student_primary     -0.842828    0.165272   -5.10  3.4e-07 ***
cardriver:employment.student_secondary          0.089636    0.102687    0.87  0.38271    
carpassenger:employment.student_secondary       1.421086    0.075757   18.76  < 2e-16 ***
cycling:employment.student_secondary            0.826044    0.077577   10.65  < 2e-16 ***
publictransport:employment.student_secondary    0.714158    0.075168    9.50  < 2e-16 ***
cardriver:employment.student_tertiary          -0.063201    0.135393   -0.47  0.64065    
carpassenger:employment.student_tertiary        0.786969    0.156896    5.02  5.3e-07 ***
cycling:employment.student_tertiary             0.415492    0.168332    2.47  0.01358 *  
publictransport:employment.student_tertiary     0.377079    0.127146    2.97  0.00302 ** 
cardriver:employment.apprentice                 0.809172    0.171756    4.71  2.5e-06 ***
carpassenger:employment.apprentice              1.424013    0.185254    7.69  1.5e-14 ***
cycling:employment.apprentice                   0.151344    0.230842    0.66  0.51207    
publictransport:employment.apprentice           0.814952    0.155512    5.24  1.6e-07 ***
cardriver:employment.other                     -0.512487    0.141739   -3.62  0.00030 ***
carpassenger:employment.other                   0.433072    0.164405    2.63  0.00843 ** 
cycling:employment.other                       -0.555424    0.202831   -2.74  0.00617 ** 
publictransport:employment.other               -0.002349    0.162190   -0.01  0.98845    
cardriver:purpose.business                      0.635176    0.148239    4.28  1.8e-05 ***
carpassenger:purpose.business                   0.330506    0.234821    1.41  0.15929    
cycling:purpose.business                       -0.047174    0.215557   -0.22  0.82677    
publictransport:purpose.business               -0.440094    0.196490   -2.24  0.02511 *  
cardriver:purpose.education                    -0.919438    0.107933   -8.52  < 2e-16 ***
carpassenger:purpose.education                 -0.014044    0.098087   -0.14  0.88615    
cycling:purpose.education                      -0.605826    0.097554   -6.21  5.3e-10 ***
publictransport:purpose.education              -0.319144    0.088037   -3.63  0.00029 ***
cardriver:purpose.service                       0.588325    0.063500    9.26  < 2e-16 ***
carpassenger:purpose.service                    0.659768    0.101353    6.51  7.5e-11 ***
cycling:purpose.service                        -1.042522    0.113971   -9.15  < 2e-16 ***
publictransport:purpose.service                -1.802048    0.112508  -16.02  < 2e-16 ***
cardriver:purpose.private_business             -0.045976    0.061767   -0.74  0.45667    
carpassenger:purpose.private_business           1.262673    0.090136   14.01  < 2e-16 ***
cycling:purpose.private_business               -0.549231    0.097112   -5.66  1.6e-08 ***
publictransport:purpose.private_business       -1.015461    0.081905  -12.40  < 2e-16 ***
cardriver:purpose.visit                        -0.193588    0.069722   -2.78  0.00549 ** 
carpassenger:purpose.visit                      1.155041    0.093726   12.32  < 2e-16 ***
cycling:purpose.visit                          -0.769293    0.108838   -7.07  1.6e-12 ***
publictransport:purpose.visit                  -1.202147    0.093314  -12.88  < 2e-16 ***
cardriver:purpose.shopping_grocery             -0.068973    0.059084   -1.17  0.24306    
carpassenger:purpose.shopping_grocery           1.118755    0.089256   12.53  < 2e-16 ***
cycling:purpose.shopping_grocery               -0.237538    0.087693   -2.71  0.00675 ** 
publictransport:purpose.shopping_grocery       -1.716834    0.088166  -19.47  < 2e-16 ***
cardriver:purpose.shopping_other                0.041398    0.080192    0.52  0.60569    
carpassenger:purpose.shopping_other             1.559959    0.104480   14.93  < 2e-16 ***
cycling:purpose.shopping_other                 -0.404549    0.129478   -3.12  0.00178 ** 
publictransport:purpose.shopping_other         -0.635770    0.098890   -6.43  1.3e-10 ***
cardriver:purpose.leisure_indoors              -0.798580    0.076025  -10.50  < 2e-16 ***
carpassenger:purpose.leisure_indoors            1.370128    0.097732   14.02  < 2e-16 ***
cycling:purpose.leisure_indoors                -1.399686    0.147808   -9.47  < 2e-16 ***
publictransport:purpose.leisure_indoors        -0.631017    0.088977   -7.09  1.3e-12 ***
cardriver:purpose.leisure_outdoors              0.310703    0.070919    4.38  1.2e-05 ***
carpassenger:purpose.leisure_outdoors           1.816284    0.093534   19.42  < 2e-16 ***
cycling:purpose.leisure_outdoors               -0.141884    0.099644   -1.42  0.15447    
publictransport:purpose.leisure_outdoors       -1.005100    0.095418  -10.53  < 2e-16 ***
cardriver:purpose.leisure_other                -0.371665    0.068355   -5.44  5.4e-08 ***
carpassenger:purpose.leisure_other              1.191688    0.092438   12.89  < 2e-16 ***
cycling:purpose.leisure_other                  -0.669401    0.101504   -6.59  4.3e-11 ***
publictransport:purpose.leisure_other          -0.738828    0.084308   -8.76  < 2e-16 ***
cardriver:purpose.leisure_walk                 -2.300405    0.076139  -30.21  < 2e-16 ***
carpassenger:purpose.leisure_walk              -0.443249    0.105978   -4.18  2.9e-05 ***
cycling:purpose.leisure_walk                   -1.083910    0.093058  -11.65  < 2e-16 ***
publictransport:purpose.leisure_walk           -2.169160    0.119969  -18.08  < 2e-16 ***
cardriver:purpose.other                         0.745207    0.425633    1.75  0.07998 .  
carpassenger:purpose.other                      2.176062    0.398918    5.45  4.9e-08 ***
cycling:purpose.other                          -1.545014    1.044746   -1.48  0.13918    
publictransport:purpose.other                   0.176414    0.479589    0.37  0.71299    
cardriver:day.Saturday                          0.012536    0.042308    0.30  0.76701    
carpassenger:day.Saturday                       0.499342    0.048151   10.37  < 2e-16 ***
cycling:day.Saturday                           -0.199639    0.069096   -2.89  0.00386 ** 
publictransport:day.Saturday                   -0.030478    0.059498   -0.51  0.60848    
cardriver:day.Sunday                           -0.380172    0.051198   -7.43  1.1e-13 ***
carpassenger:day.Sunday                         0.200993    0.056304    3.57  0.00036 ***
cycling:day.Sunday                             -0.409509    0.081137   -5.05  4.5e-07 ***
publictransport:day.Sunday                     -0.644701    0.074632   -8.64  < 2e-16 ***
cardriver:intrazonal.TRUE                      -1.297500    0.033790  -38.40  < 2e-16 ***
carpassenger:intrazonal.TRUE                   -0.962294    0.042809  -22.48  < 2e-16 ***
cycling:intrazonal.TRUE                        -0.004732    0.045170   -0.10  0.91657    
publictransport:intrazonal.TRUE                -3.386892    0.092646  -36.56  < 2e-16 ***
cardriver:num_activities                        0.219140    0.039457    5.55  2.8e-08 ***
carpassenger:num_activities                     0.157446    0.046267    3.40  0.00067 ***
cycling:num_activities                          0.161827    0.060685    2.67  0.00766 ** 
publictransport:num_activities                  0.213325    0.047616    4.48  7.5e-06 ***
cardriver:containsStrolling.TRUE               -0.758267    0.191115   -3.97  7.3e-05 ***
carpassenger:containsStrolling.TRUE            -0.648781    0.231312   -2.80  0.00503 ** 
cycling:containsStrolling.TRUE                 -0.385286    0.301452   -1.28  0.20121    
publictransport:containsStrolling.TRUE         -0.438041    0.224284   -1.95  0.05081 .  
cardriver:containsVisit.TRUE                    0.625751    0.132941    4.71  2.5e-06 ***
carpassenger:containsVisit.TRUE                 0.339917    0.139007    2.45  0.01447 *  
cycling:containsVisit.TRUE                     -0.144721    0.195515   -0.74  0.45918    
publictransport:containsVisit.TRUE             -0.194776    0.151799   -1.28  0.19945    
cardriver:containsPrivateB.TRUE                 0.176625    0.097877    1.80  0.07114 .  
carpassenger:containsPrivateB.TRUE              0.139582    0.116501    1.20  0.23087    
cycling:containsPrivateB.TRUE                  -0.022576    0.153249   -0.15  0.88288    
publictransport:containsPrivateB.TRUE           0.020790    0.118205    0.18  0.86039    
cardriver:containsService.TRUE                  0.134084    0.171867    0.78  0.43530    
carpassenger:containsService.TRUE              -0.192679    0.232256   -0.83  0.40677    
cycling:containsService.TRUE                   -0.883462    0.265592   -3.33  0.00088 ***
publictransport:containsService.TRUE           -1.649115    0.232211   -7.10  1.2e-12 ***
cardriver:containsLeisure.TRUE                  0.411020    0.099233    4.14  3.4e-05 ***
carpassenger:containsLeisure.TRUE               1.164112    0.106003   10.98  < 2e-16 ***
cycling:containsLeisure.TRUE                    0.123975    0.150401    0.82  0.40977    
publictransport:containsLeisure.TRUE            0.617452    0.113627    5.43  5.5e-08 ***
cardriver:containsShopping.TRUE                 0.192045    0.081261    2.36  0.01811 *  
carpassenger:containsShopping.TRUE             -0.080856    0.100446   -0.80  0.42084    
cycling:containsShopping.TRUE                   0.065885    0.128249    0.51  0.60744    
publictransport:containsShopping.TRUE          -0.001717    0.098667   -0.02  0.98612    
cardriver:containsBusiness.TRUE                 0.295654    0.177653    1.66  0.09607 .  
carpassenger:containsBusiness.TRUE             -0.431633    0.305662   -1.41  0.15791    
cycling:containsBusiness.TRUE                  -0.095519    0.251981   -0.38  0.70463    
publictransport:containsBusiness.TRUE           0.109364    0.204716    0.53  0.59319    
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -51900
McFadden R^2:  0.376 
Likelihood ratio test : chisq = 62500 (p.value = <2e-16)


		*/
	
	
	public TourModeChoiceParameterTimeCostByEmpSex() {

		
		
		
		cardriver                                       = 1.100896;     // se=0.068386
		carpassenger                                   = -3.462956;     // se=0.095612
		cycling                                        = -1.401308;     // se=0.089917
		publictransport                                 = 0.457483;     // se=0.073556
		
		notavailable                                  = -25.099613;  // se=2470.847628
		time                                           = -0.024115;     // se=0.000832
		cost                                           = -0.485238;     // se=0.014700
		
		time_sex_female                                = -0.007246;     // se=0.000992
		
		time_employment_parttime                        = 0.003764;     // se=0.001604
		time_employment_marginal                        = 0.010244;     // se=0.002461
		time_employment_homekeeper                      = 0.008009;     // se=0.001967
		time_employment_unemployed                     = -0.008787;     // se=0.005165
		time_employment_retired                         = 0.005195;     // se=0.001154
		time_employment_pupil                = -0.015317;     // se=0.003207
//		time_employment_student_secondary              = -0.004402;     // se=0.001620
		time_employment_student                = 0.007390;     // se=0.002334
		time_employment_apprentice                      = 0.004757;     // se=0.003391
		time_employment_other                          = -0.008915;     // se=0.004485
		
		cost_sex_female                                = -0.150297;     // se=0.020873
		
		cost_employment_parttime                        = 0.186230;     // se=0.032599
		cost_employment_marginal                        = 0.372754;     // se=0.060091
		cost_employment_homekeeper                      = 0.298956;     // se=0.051319
		cost_employment_unemployed                      = 0.306659;     // se=0.094380
		cost_employment_retired                         = 0.171107;     // se=0.024312
		cost_employment_pupil                = -0.421582;     // se=0.145079
//		cost_employment_student_secondary              = -0.352884;     // se=0.045672
		cost_employment_student                = 0.222413;     // se=0.043721
		cost_employment_apprentice                     = -0.000654;     // se=0.055130
		cost_employment_other                           = 0.140005;     // se=0.082535
		
		cardriver_sex_female                           = -0.742352;     // se=0.041191
		carpassenger_sex_female                         = 0.406870;     // se=0.045306
		cycling_sex_female                             = -0.632539;     // se=0.049565
		publictransport_sex_female                      = 0.014626;     // se=0.041098
		
		cardriver_employment_apprentice                 = 0.809172;     // se=0.171756
		cardriver_employment_homekeeper                 = 0.033815;     // se=0.071984
		cardriver_employment_marginal                   = 0.283347;     // se=0.087675
		cardriver_employment_other                     = -0.512487;     // se=0.141739
		cardriver_employment_parttime                   = 0.166481;     // se=0.061997
		cardriver_employment_retired                   = -0.212223;     // se=0.049193
		cardriver_employment_pupil           = -0.932147;  // se=9065.624835
//		cardriver_employment_student_secondary          = 0.089636;     // se=0.102687
		cardriver_employment_student          = -0.063201;     // se=0.135393
		cardriver_employment_unemployed                = -0.728491;     // se=0.175620
		carpassenger_employment_apprentice              = 1.424013;     // se=0.185254
		carpassenger_employment_homekeeper              = 0.516428;     // se=0.092143
		carpassenger_employment_marginal                = 0.865199;     // se=0.114474
		carpassenger_employment_other                   = 0.433072;     // se=0.164405
		carpassenger_employment_parttime                = 0.411396;     // se=0.081235
		carpassenger_employment_retired                 = 0.459641;     // se=0.067253
		carpassenger_employment_pupil         = 1.141004;     // se=0.096050
//		carpassenger_employment_student_secondary       = 1.421086;     // se=0.075757
		carpassenger_employment_student        = 0.786969;     // se=0.156896
		carpassenger_employment_unemployed             = -0.128440;     // se=0.204790
		cycling_employment_apprentice                   = 0.151344;     // se=0.230842
		cycling_employment_homekeeper                   = 0.202152;     // se=0.108456
		cycling_employment_marginal                     = 0.668666;     // se=0.115535
		cycling_employment_other                       = -0.555424;     // se=0.202831
		cycling_employment_parttime                     = 0.573302;     // se=0.078279
		cycling_employment_retired                     = -0.243651;     // se=0.077121
		cycling_employment_pupil             = -1.025620;     // se=0.127899
//		cycling_employment_student_secondary            = 0.826044;     // se=0.077577
		cycling_employment_student             = 0.415492;     // se=0.168332
		cycling_employment_unemployed                  = -0.594626;     // se=0.267369
		publictransport_employment_apprentice           = 0.814952;     // se=0.155512
		publictransport_employment_homekeeper          = -0.595552;     // se=0.122051
		publictransport_employment_marginal            = -0.770981;     // se=0.151474
		publictransport_employment_other               = -0.002349;     // se=0.162190
		publictransport_employment_parttime            = -0.191042;     // se=0.072701
		publictransport_employment_retired              = 0.120796;     // se=0.064758
		publictransport_employment_pupil     = -0.842828;     // se=0.165272
//		publictransport_employment_student_secondary    = 0.714158;     // se=0.075168
		publictransport_employment_student     = 0.377079;     // se=0.127146
		publictransport_employment_unemployed          = -0.114465;     // se=0.199319
		
		cardriver_purpose_business                      = 0.635176;     // se=0.148239
		carpassenger_purpose_business                   = 0.330506;     // se=0.234821
		cycling_purpose_business                       = -0.047174;     // se=0.215557
		publictransport_purpose_business               = -0.440094;     // se=0.196490
		cardriver_purpose_education                    = -0.919438;     // se=0.107933
		carpassenger_purpose_education                 = -0.014044;     // se=0.098087
		cycling_purpose_education                      = -0.605826;     // se=0.097554
		publictransport_purpose_education              = -0.319144;     // se=0.088037
		cardriver_purpose_service                       = 0.588325;     // se=0.063500
		carpassenger_purpose_service                    = 0.659768;     // se=0.101353
		cycling_purpose_service                        = -1.042522;     // se=0.113971
		publictransport_purpose_service                = -1.802048;     // se=0.112508
		cardriver_purpose_private_business             = -0.045976;     // se=0.061767
		carpassenger_purpose_private_business           = 1.262673;     // se=0.090136
		cycling_purpose_private_business               = -0.549231;     // se=0.097112
		publictransport_purpose_private_business       = -1.015461;     // se=0.081905
		cardriver_purpose_visit                        = -0.193588;     // se=0.069722
		carpassenger_purpose_visit                      = 1.155041;     // se=0.093726
		cycling_purpose_visit                          = -0.769293;     // se=0.108838
		publictransport_purpose_visit                  = -1.202147;     // se=0.093314
		cardriver_purpose_shopping_grocery             = -0.068973;     // se=0.059084
		carpassenger_purpose_shopping_grocery           = 1.118755;     // se=0.089256
		cycling_purpose_shopping_grocery               = -0.237538;     // se=0.087693
		publictransport_purpose_shopping_grocery       = -1.716834;     // se=0.088166
		cardriver_purpose_shopping_other                = 0.041398;     // se=0.080192
		carpassenger_purpose_shopping_other             = 1.559959;     // se=0.104480
		cycling_purpose_shopping_other                 = -0.404549;     // se=0.129478
		publictransport_purpose_shopping_other         = -0.635770;     // se=0.098890
		cardriver_purpose_leisure_indoors              = -0.798580;     // se=0.076025
		carpassenger_purpose_leisure_indoors            = 1.370128;     // se=0.097732
		cycling_purpose_leisure_indoors                = -1.399686;     // se=0.147808
		publictransport_purpose_leisure_indoors        = -0.631017;     // se=0.088977
		cardriver_purpose_leisure_outdoors              = 0.310703;     // se=0.070919
		carpassenger_purpose_leisure_outdoors           = 1.816284;     // se=0.093534
		cycling_purpose_leisure_outdoors               = -0.141884;     // se=0.099644
		publictransport_purpose_leisure_outdoors       = -1.005100;     // se=0.095418
		cardriver_purpose_leisure_other                = -0.371665;     // se=0.068355
		carpassenger_purpose_leisure_other              = 1.191688;     // se=0.092438
		cycling_purpose_leisure_other                  = -0.669401;     // se=0.101504
		publictransport_purpose_leisure_other          = -0.738828;     // se=0.084308
		cardriver_purpose_leisure_walk                 = -2.300405;     // se=0.076139
		carpassenger_purpose_leisure_walk              = -0.443249;     // se=0.105978
		cycling_purpose_leisure_walk                   = -1.083910;     // se=0.093058
		publictransport_purpose_leisure_walk           = -2.169160;     // se=0.119969
		cardriver_purpose_other                         = 0.745207;     // se=0.425633
		carpassenger_purpose_other                      = 2.176062;     // se=0.398918
		cycling_purpose_other                          = -1.545014;     // se=1.044746
		publictransport_purpose_other                   = 0.176414;     // se=0.479589
		
		cardriver_day_Saturday                          = 0.012536;     // se=0.042308
		carpassenger_day_Saturday                       = 0.499342;     // se=0.048151
		cycling_day_Saturday                           = -0.199639;     // se=0.069096
		publictransport_day_Saturday                   = -0.030478;     // se=0.059498
		cardriver_day_Sunday                           = -0.380172;     // se=0.051198
		carpassenger_day_Sunday                         = 0.200993;     // se=0.056304
		cycling_day_Sunday                             = -0.409509;     // se=0.081137
		publictransport_day_Sunday                     = -0.644701;     // se=0.074632
		
		cardriver_intrazonal                           = -1.297500;     // se=0.033790
		carpassenger_intrazonal                        = -0.962294;     // se=0.042809
		cycling_intrazonal                             = -0.004732;     // se=0.045170
		publictransport_intrazonal                     = -3.386892;     // se=0.092646
		
		cardriver_num_activities                        = 0.219140;     // se=0.039457
		carpassenger_num_activities                     = 0.157446;     // se=0.046267
		cycling_num_activities                          = 0.161827;     // se=0.060685
		publictransport_num_activities                  = 0.213325;     // se=0.047616
		
		cardriver_containsStrolling                    = -0.758267;     // se=0.191115
		carpassenger_containsStrolling                 = -0.648781;     // se=0.231312
		cycling_containsStrolling                      = -0.385286;     // se=0.301452
		publictransport_containsStrolling              = -0.438041;     // se=0.224284
		cardriver_containsVisit                         = 0.625751;     // se=0.132941
		carpassenger_containsVisit                      = 0.339917;     // se=0.139007
		cycling_containsVisit                          = -0.144721;     // se=0.195515
		publictransport_containsVisit                  = -0.194776;     // se=0.151799
		cardriver_containsPrivateB                      = 0.176625;     // se=0.097877
		carpassenger_containsPrivateB                   = 0.139582;     // se=0.116501
		cycling_containsPrivateB                       = -0.022576;     // se=0.153249
		publictransport_containsPrivateB                = 0.020790;     // se=0.118205
		cardriver_containsService                       = 0.134084;     // se=0.171867
		carpassenger_containsService                   = -0.192679;     // se=0.232256
		cycling_containsService                        = -0.883462;     // se=0.265592
		publictransport_containsService                = -1.649115;     // se=0.232211
		cardriver_containsLeisure                       = 0.411020;     // se=0.099233
		carpassenger_containsLeisure                    = 1.164112;     // se=0.106003
		cycling_containsLeisure                         = 0.123975;     // se=0.150401
		publictransport_containsLeisure                 = 0.617452;     // se=0.113627
		cardriver_containsShopping                      = 0.192045;     // se=0.081261
		carpassenger_containsShopping                  = -0.080856;     // se=0.100446
		cycling_containsShopping                        = 0.065885;     // se=0.128249
		publictransport_containsShopping               = -0.001717;     // se=0.098667
		cardriver_containsBusiness                      = 0.295654;     // se=0.177653
		carpassenger_containsBusiness                  = -0.431633;     // se=0.305662
		cycling_containsBusiness                       = -0.095519;     // se=0.251981
		publictransport_containsBusiness                = 0.109364;     // se=0.204716
		
		
		
		//
		
		time_employment_fulltime                         = 0.0;
		cost_employment_fulltime                         = 0.0;

		
		walking_employment_apprentice          = 0.0;
		walking_employment_homekeeper          = 0.0;
		walking_employment_marginal            = 0.0;
		walking_employment_other               = 0.0;
		walking_employment_parttime            = 0.0;
		walking_employment_retired             = 0.0;
		walking_employment_pupil     = 0.0;
//		walking_employment_student_secondary   = 0.0;
		walking_employment_student    = 0.0;
		walking_employment_unemployed          = 0.0;

		init();
	}



}
