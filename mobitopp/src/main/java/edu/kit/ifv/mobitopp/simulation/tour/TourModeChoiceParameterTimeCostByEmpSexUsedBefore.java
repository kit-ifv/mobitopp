package edu.kit.ifv.mobitopp.simulation.tour;


public class TourModeChoiceParameterTimeCostByEmpSexUsedBefore 
	extends TourModeChoiceParameterBaseTimeCostByEmpSex 
	implements TourModeChoiceParameter
{

	
	protected Double	walking_usedbefore = 0.0;
	protected Double	cardriver_usedbefore;
	protected Double	carpassenger_usedbefore;
	protected Double	cycling_usedbefore;
	protected Double	publictransport_usedbefore;
	
	protected Double	walking_firsttour					= 0.0;
	protected Double	cardriver_firsttour				;
	protected Double	carpassenger_firsttour		;
	protected Double	cycling_firsttour					;
	protected Double	publictransport_firsttour	;


	/*
	
Call:
mlogit(formula = mode ~ notavailable + time + time:sex. + time:employment. + 
    cost + cost:sex. + cost:employment. | sex. + employment. + 
    age. + purpose. + day. + intrazonal. + num_activities + containsStrolling. + 
    containsVisit. + containsPrivateB. + containsService. + containsLeisure. + 
    containsShopping. + containsBusiness. + firsttour. | usedbefore, 
    data = trainingdata, reflevel = "walking", method = "nr", 
    print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2876          0.3914          0.1279          0.0544          0.1388 

nr method
24 iterations, 0h:9m:24s 
g'(-H)^-1g = 4E-07 
gradient close to zero 

Coefficients :
                                            Estimate  Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                       0.089120    0.087729    1.02  0.30970    
carpassenger:(intercept)                   -3.411138    0.110145  -30.97  < 2e-16 ***
cycling:(intercept)                        -1.711047    0.117548  -14.56  < 2e-16 ***
publictransport:(intercept)                -0.243770    0.097744   -2.49  0.01263 *  
notavailable                              -24.235630 2726.569363   -0.01  0.99291    
time                                       -0.026228    0.000962  -27.26  < 2e-16 ***
cost                                       -0.478848    0.019661  -24.36  < 2e-16 ***
time:sex.female                            -0.002800    0.001176   -2.38  0.01731 *  
time:employment.parttime                   -0.012953    0.002083   -6.22  5.0e-10 ***
time:employment.marginal                    0.000345    0.003124    0.11  0.91215    
time:employment.homekeeper                 -0.007340    0.002630   -2.79  0.00526 ** 
time:employment.unemployed                 -0.005441    0.004813   -1.13  0.25828    
time:employment.retired                    -0.005385    0.001412   -3.81  0.00014 ***
time:employment.pupil                      -0.009185    0.001745   -5.26  1.4e-07 ***
time:employment.student                     0.010777    0.002460    4.38  1.2e-05 ***
time:employment.apprentice                 -0.011715    0.005841   -2.01  0.04488 *  
time:employment.other                      -0.041350    0.012165   -3.40  0.00068 ***
cost:sex.female                             0.007033    0.025342    0.28  0.78136    
cost:employment.parttime                    0.069990    0.039198    1.79  0.07417 .  
cost:employment.marginal                    0.196275    0.080318    2.44  0.01454 *  
cost:employment.homekeeper                  0.230373    0.063536    3.63  0.00029 ***
cost:employment.unemployed                  0.256513    0.101849    2.52  0.01178 *  
cost:employment.retired                     0.129791    0.032004    4.06  5.0e-05 ***
cost:employment.pupil                       0.094965    0.046408    2.05  0.04073 *  
cost:employment.student                     0.199484    0.059089    3.38  0.00074 ***
cost:employment.apprentice                  0.016062    0.078026    0.21  0.83690    
cost:employment.other                       0.129193    0.137372    0.94  0.34698    
cardriver:sex.female                       -0.509731    0.044872  -11.36  < 2e-16 ***
carpassenger:sex.female                     0.402315    0.048504    8.29  < 2e-16 ***
cycling:sex.female                         -0.425928    0.055892   -7.62  2.5e-14 ***
publictransport:sex.female                  0.025498    0.045767    0.56  0.57744    
cardriver:employment.parttime              -0.042003    0.066435   -0.63  0.52723    
carpassenger:employment.parttime            0.088237    0.086762    1.02  0.30915    
cycling:employment.parttime                 0.132534    0.089458    1.48  0.13847    
publictransport:employment.parttime         0.132441    0.080923    1.64  0.10171    
cardriver:employment.marginal               0.213717    0.100312    2.13  0.03313 *  
carpassenger:employment.marginal            0.523693    0.130265    4.02  5.8e-05 ***
cycling:employment.marginal                 0.464337    0.138370    3.36  0.00079 ***
publictransport:employment.marginal        -0.037717    0.164111   -0.23  0.81823    
cardriver:employment.homekeeper            -0.033279    0.079020   -0.42  0.67365    
carpassenger:employment.homekeeper          0.227939    0.102101    2.23  0.02558 *  
cycling:employment.homekeeper               0.112319    0.120620    0.93  0.35176    
publictransport:employment.homekeeper       0.163030    0.137567    1.19  0.23598    
cardriver:employment.unemployed            -0.393130    0.173683   -2.26  0.02361 *  
carpassenger:employment.unemployed          0.118198    0.199487    0.59  0.55351    
cycling:employment.unemployed               0.144744    0.246331    0.59  0.55680    
publictransport:employment.unemployed       0.427594    0.200941    2.13  0.03334 *  
cardriver:employment.retired               -0.310212    0.085980   -3.61  0.00031 ***
carpassenger:employment.retired            -0.089564    0.110725   -0.81  0.41858    
cycling:employment.retired                 -0.186958    0.152070   -1.23  0.21892    
publictransport:employment.retired          0.293968    0.120858    2.43  0.01500 *  
cardriver:employment.pupil                 -0.101777    0.179351   -0.57  0.57039    
carpassenger:employment.pupil               0.314272    0.193020    1.63  0.10349    
cycling:employment.pupil                    0.357922    0.271864    1.32  0.18799    
publictransport:employment.pupil            0.165161    0.189777    0.87  0.38414    
cardriver:employment.student                0.063366    0.172271    0.37  0.71300    
carpassenger:employment.student             0.252617    0.207485    1.22  0.22341    
cycling:employment.student                  0.642934    0.249806    2.57  0.01006 *  
publictransport:employment.student          0.134310    0.174602    0.77  0.44175    
cardriver:employment.apprentice             0.120948    0.249668    0.48  0.62808    
carpassenger:employment.apprentice          0.089036    0.272387    0.33  0.74376    
cycling:employment.apprentice              -0.233933    0.378578   -0.62  0.53662    
publictransport:employment.apprentice       0.226006    0.228546    0.99  0.32272    
cardriver:employment.other                 -0.649355    0.240984   -2.69  0.00705 ** 
carpassenger:employment.other              -0.290941    0.303200   -0.96  0.33727    
cycling:employment.other                   -0.141812    0.302760   -0.47  0.63950    
publictransport:employment.other            0.600329    0.284210    2.11  0.03466 *  
cardriver:age.06to09                       -0.025922 9300.607557    0.00  1.00000    
carpassenger:age.06to09                     1.089469    0.198102    5.50  3.8e-08 ***
cycling:age.06to09                         -0.677793    0.288897   -2.35  0.01897 *  
publictransport:age.06to09                 -0.610519    0.220712   -2.77  0.00567 ** 
cardriver:age.10to17                       -0.215459    0.307164   -0.70  0.48302    
carpassenger:age.10to17                     0.962738    0.192797    4.99  5.9e-07 ***
cycling:age.10to17                          0.243681    0.272395    0.89  0.37101    
publictransport:age.10to17                  0.432792    0.191629    2.26  0.02391 *  
cardriver:age.18to25                        0.397933    0.129168    3.08  0.00206 ** 
carpassenger:age.18to25                     0.753981    0.152043    4.96  7.1e-07 ***
cycling:age.18to25                         -0.051777    0.221571   -0.23  0.81523    
publictransport:age.18to25                  0.355981    0.151428    2.35  0.01873 *  
cardriver:age.26to35                       -0.232315    0.062022   -3.75  0.00018 ***
carpassenger:age.26to35                    -0.126126    0.087637   -1.44  0.15010    
cycling:age.26to35                         -0.350599    0.110399   -3.18  0.00149 ** 
publictransport:age.26to35                 -0.005650    0.087484   -0.06  0.94850    
cardriver:age.51to60                        0.128212    0.046482    2.76  0.00581 ** 
carpassenger:age.51to60                     0.303818    0.064297    4.73  2.3e-06 ***
cycling:age.51to60                         -0.070746    0.079678   -0.89  0.37460    
publictransport:age.51to60                  0.086078    0.071276    1.21  0.22717    
cardriver:age.61to70                        0.062373    0.075808    0.82  0.41063    
carpassenger:age.61to70                     0.352626    0.097044    3.63  0.00028 ***
cycling:age.61to70                          0.006072    0.135679    0.04  0.96430    
publictransport:age.61to70                  0.245061    0.109620    2.24  0.02538 *  
cardriver:age.71plus                        0.232349    0.088929    2.61  0.00898 ** 
carpassenger:age.71plus                     0.519522    0.111882    4.64  3.4e-06 ***
cycling:age.71plus                          0.324661    0.166813    1.95  0.05162 .  
publictransport:age.71plus                  0.473920    0.127297    3.72  0.00020 ***
cardriver:purpose.business                  0.391561    0.151816    2.58  0.00990 ** 
carpassenger:purpose.business               0.825257    0.216375    3.81  0.00014 ***
cycling:purpose.business                   -0.202467    0.239192   -0.85  0.39730    
publictransport:purpose.business           -0.310214    0.205099   -1.51  0.13040    
cardriver:purpose.education                -1.182445    0.121364   -9.74  < 2e-16 ***
carpassenger:purpose.education             -0.048966    0.102977   -0.48  0.63443    
cycling:purpose.education                  -0.682196    0.111105   -6.14  8.2e-10 ***
publictransport:purpose.education          -0.351660    0.098887   -3.56  0.00038 ***
cardriver:purpose.service                   0.588850    0.068730    8.57  < 2e-16 ***
carpassenger:purpose.service                0.907560    0.106097    8.55  < 2e-16 ***
cycling:purpose.service                    -0.950002    0.122099   -7.78  7.3e-15 ***
publictransport:purpose.service            -1.950997    0.126949  -15.37  < 2e-16 ***
cardriver:purpose.private_business         -0.180579    0.066821   -2.70  0.00688 ** 
carpassenger:purpose.private_business       1.092019    0.095472   11.44  < 2e-16 ***
cycling:purpose.private_business           -0.818111    0.110588   -7.40  1.4e-13 ***
publictransport:purpose.private_business   -1.200158    0.091203  -13.16  < 2e-16 ***
cardriver:purpose.visit                    -0.407348    0.075742   -5.38  7.5e-08 ***
carpassenger:purpose.visit                  0.895943    0.100112    8.95  < 2e-16 ***
cycling:purpose.visit                      -1.170071    0.127638   -9.17  < 2e-16 ***
publictransport:purpose.visit              -1.687710    0.109076  -15.47  < 2e-16 ***
cardriver:purpose.shopping_grocery         -0.176351    0.063439   -2.78  0.00544 ** 
carpassenger:purpose.shopping_grocery       1.006751    0.093749   10.74  < 2e-16 ***
cycling:purpose.shopping_grocery           -0.417571    0.100128   -4.17  3.0e-05 ***
publictransport:purpose.shopping_grocery   -2.154149    0.097296  -22.14  < 2e-16 ***
cardriver:purpose.shopping_other            0.066482    0.087627    0.76  0.44803    
carpassenger:purpose.shopping_other         1.564270    0.111591   14.02  < 2e-16 ***
cycling:purpose.shopping_other             -0.564547    0.147208   -3.84  0.00013 ***
publictransport:purpose.shopping_other     -0.716243    0.111730   -6.41  1.5e-10 ***
cardriver:purpose.leisure_indoors          -0.980725    0.082767  -11.85  < 2e-16 ***
carpassenger:purpose.leisure_indoors        1.267700    0.103601   12.24  < 2e-16 ***
cycling:purpose.leisure_indoors            -1.741854    0.158076  -11.02  < 2e-16 ***
publictransport:purpose.leisure_indoors    -0.912731    0.100649   -9.07  < 2e-16 ***
cardriver:purpose.leisure_outdoors          0.275104    0.076678    3.59  0.00033 ***
carpassenger:purpose.leisure_outdoors       1.728424    0.098837   17.49  < 2e-16 ***
cycling:purpose.leisure_outdoors           -0.338363    0.113530   -2.98  0.00288 ** 
publictransport:purpose.leisure_outdoors   -1.346927    0.105912  -12.72  < 2e-16 ***
cardriver:purpose.leisure_other            -0.441574    0.073945   -5.97  2.3e-09 ***
carpassenger:purpose.leisure_other          1.055517    0.097902   10.78  < 2e-16 ***
cycling:purpose.leisure_other              -0.887156    0.114953   -7.72  1.2e-14 ***
publictransport:purpose.leisure_other      -0.973400    0.095166  -10.23  < 2e-16 ***
cardriver:purpose.leisure_walk             -2.466454    0.082512  -29.89  < 2e-16 ***
carpassenger:purpose.leisure_walk          -0.644084    0.114627   -5.62  1.9e-08 ***
cycling:purpose.leisure_walk               -1.302843    0.108720  -11.98  < 2e-16 ***
publictransport:purpose.leisure_walk       -2.368821    0.133208  -17.78  < 2e-16 ***
cardriver:purpose.other                    -0.130323    0.453119   -0.29  0.77364    
carpassenger:purpose.other                  1.374930    0.370925    3.71  0.00021 ***
cycling:purpose.other                      -1.918031    1.078362   -1.78  0.07530 .  
publictransport:purpose.other              -0.734605    0.572306   -1.28  0.19929    
cardriver:day.Saturday                      0.012023    0.045163    0.27  0.79008    
carpassenger:day.Saturday                   0.456848    0.051855    8.81  < 2e-16 ***
cycling:day.Saturday                       -0.244866    0.077022   -3.18  0.00148 ** 
publictransport:day.Saturday               -0.058388    0.066347   -0.88  0.37883    
cardriver:day.Sunday                       -0.403989    0.054576   -7.40  1.3e-13 ***
carpassenger:day.Sunday                     0.047314    0.060852    0.78  0.43685    
cycling:day.Sunday                         -0.601314    0.091329   -6.58  4.6e-11 ***
publictransport:day.Sunday                 -0.685043    0.081550   -8.40  < 2e-16 ***
cardriver:intrazonal.TRUE                  -1.048605    0.036252  -28.93  < 2e-16 ***
carpassenger:intrazonal.TRUE               -1.047112    0.046122  -22.70  < 2e-16 ***
cycling:intrazonal.TRUE                     0.001453    0.051368    0.03  0.97743    
publictransport:intrazonal.TRUE            -2.991935    0.093932  -31.85  < 2e-16 ***
cardriver:num_activities                    0.346820    0.046058    7.53  5.1e-14 ***
carpassenger:num_activities                 0.395884    0.051352    7.71  1.3e-14 ***
cycling:num_activities                      0.159002    0.075804    2.10  0.03595 *  
publictransport:num_activities              0.310272    0.057067    5.44  5.4e-08 ***
cardriver:containsStrolling.TRUE           -1.718683    0.186976   -9.19  < 2e-16 ***
carpassenger:containsStrolling.TRUE        -1.246214    0.218826   -5.70  1.2e-08 ***
cycling:containsStrolling.TRUE             -0.788237    0.299252   -2.63  0.00844 ** 
publictransport:containsStrolling.TRUE     -1.363359    0.243680   -5.59  2.2e-08 ***
cardriver:containsVisit.TRUE                0.288389    0.138075    2.09  0.03674 *  
carpassenger:containsVisit.TRUE            -0.153105    0.148318   -1.03  0.30194    
cycling:containsVisit.TRUE                 -0.007097    0.202034   -0.04  0.97198    
publictransport:containsVisit.TRUE         -0.232536    0.170108   -1.37  0.17163    
cardriver:containsPrivateB.TRUE             0.108387    0.108870    1.00  0.31946    
carpassenger:containsPrivateB.TRUE         -0.109497    0.127210   -0.86  0.38937    
cycling:containsPrivateB.TRUE              -0.083470    0.180245   -0.46  0.64330    
publictransport:containsPrivateB.TRUE      -0.137020    0.136943   -1.00  0.31704    
cardriver:containsService.TRUE             -0.032324    0.188270   -0.17  0.86368    
carpassenger:containsService.TRUE          -0.236251    0.238717   -0.99  0.32234    
cycling:containsService.TRUE               -0.621794    0.276289   -2.25  0.02442 *  
publictransport:containsService.TRUE       -1.805136    0.257845   -7.00  2.5e-12 ***
cardriver:containsLeisure.TRUE              0.496568    0.108722    4.57  4.9e-06 ***
carpassenger:containsLeisure.TRUE           0.880719    0.113993    7.73  1.1e-14 ***
cycling:containsLeisure.TRUE                0.071934    0.174958    0.41  0.68096    
publictransport:containsLeisure.TRUE        0.483828    0.130014    3.72  0.00020 ***
cardriver:containsShopping.TRUE             0.198151    0.090259    2.20  0.02814 *  
carpassenger:containsShopping.TRUE         -0.075629    0.107083   -0.71  0.48002    
cycling:containsShopping.TRUE               0.121028    0.150308    0.81  0.42071    
publictransport:containsShopping.TRUE       0.003402    0.113413    0.03  0.97607    
cardriver:containsBusiness.TRUE             0.570994    0.218593    2.61  0.00900 ** 
carpassenger:containsBusiness.TRUE          0.186603    0.306586    0.61  0.54276    
cycling:containsBusiness.TRUE               0.534377    0.290230    1.84  0.06559 .  
publictransport:containsBusiness.TRUE       0.269922    0.255398    1.06  0.29057    
cardriver:firsttour.TRUE                    0.474707    0.061656    7.70  1.4e-14 ***
carpassenger:firsttour.TRUE                -0.258779    0.068063   -3.80  0.00014 ***
cycling:firsttour.TRUE                      0.248048    0.080634    3.08  0.00210 ** 
publictransport:firsttour.TRUE              0.414252    0.066280    6.25  4.1e-10 ***
walking:usedbeforeTRUE                      1.260243    0.030709   41.04  < 2e-16 ***
cardriver:usedbeforeTRUE                    1.540377    0.036691   41.98  < 2e-16 ***
carpassenger:usedbeforeTRUE                 1.408966    0.033308   42.30  < 2e-16 ***
cycling:usedbeforeTRUE                      2.937429    0.049291   59.59  < 2e-16 ***
publictransport:usedbeforeTRUE              1.991595    0.043442   45.85  < 2e-16 ***
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -42800
McFadden R^2:  0.479 
Likelihood ratio test : chisq = 78600 (p.value = <2e-16)


		*/


	
	
	public TourModeChoiceParameterTimeCostByEmpSexUsedBefore() {
	
		walking																				 = 0.0	- 0.3;
		walking_intrazonal														 = 0.0	+ 0.2;
		walking_day_Saturday                           = 0.0;
		walking_day_Sunday                           	 = 0.0;
		
		// curr: v8, next: v8

		cardriver                                    = 0.0891209 + 0.35;			//    0.087729    1.02  0.30970    
		carpassenger                                = -3.411138;			//    0.110145  -30.97  < 2e-16 ***
		cycling                                     = -1.711047;			//    0.117548  -14.56  < 2e-16 ***
		publictransport                             = -0.243770	+ 0.8;			//    0.097744   -2.49  0.01263 *  
		
		cardriver_intrazonal                        = -1.048605 - 1.0;			//    0.036252  -28.93  < 2e-16 ***
		carpassenger_intrazonal                     = -1.047112 - 0.2;			//    0.046122  -22.70  < 2e-16 ***
		cycling_intrazonal                           = 0.001453	+ 0.3;			//    0.051368    0.03  0.97743    
		publictransport_intrazonal                  = -2.991935	- 0.8;			//    0.093932  -31.85  < 2e-16 ***
		
		notavailable                               = -24.235630;			// 2726.569363   -0.01  0.99291    
		
		time                                        = -0.026228;			//    0.000962  -27.26  < 2e-16 ***
		cost                                        = -0.478848;			//    0.019661  -24.36  < 2e-16 ***
		
		time_sex_female                             = -0.002800;			//    0.001176   -2.38  0.01731 *  
		time_employment_parttime                    = -0.012953;			//    0.002083   -6.22  5.0e-10 ***
		time_employment_marginal                     = 0.000345;			//    0.003124    0.11  0.91215    
		time_employment_homekeeper                  = -0.007340;			//    0.002630   -2.79  0.00526 ** 
		time_employment_unemployed                  = -0.005441;			//    0.004813   -1.13  0.25828    
		time_employment_retired                     = -0.005385;			//    0.001412   -3.81  0.00014 ***
		time_employment_pupil                       = -0.009185;			//    0.001745   -5.26  1.4e-07 ***
		time_employment_student                      = 0.010777;			//    0.002460    4.38  1.2e-05 ***
		time_employment_apprentice                  = -0.011715;			//    0.005841   -2.01  0.04488 *  
		time_employment_other                       = -0.041350;			//    0.012165   -3.40  0.00068 ***
		cost_sex_female                              = 0.007033;			//    0.025342    0.28  0.78136    
		cost_employment_parttime                     = 0.069990;			//    0.039198    1.79  0.07417 .  
		cost_employment_marginal                     = 0.196275;			//    0.080318    2.44  0.01454 *  
		cost_employment_homekeeper                   = 0.230373;			//    0.063536    3.63  0.00029 ***
		cost_employment_unemployed                   = 0.256513;			//    0.101849    2.52  0.01178 *  
		cost_employment_retired                      = 0.129791;			//    0.032004    4.06  5.0e-05 ***
		cost_employment_pupil                        = 0.094965;			//    0.046408    2.05  0.04073 *  
		cost_employment_student                      = 0.199484;			//    0.059089    3.38  0.00074 ***
		cost_employment_apprentice                   = 0.016062;			//    0.078026    0.21  0.83690    
		cost_employment_other                        = 0.129193;			//    0.137372    0.94  0.34698    
		
		cardriver_sex_female                        = -0.509731 + 0.0;			//    0.044872  -11.36  < 2e-16 ***
		carpassenger_sex_female                      = 0.402315;			//    0.048504    8.29  < 2e-16 ***
		cycling_sex_female                          = -0.425928;			//    0.055892   -7.62  2.5e-14 ***
		publictransport_sex_female                   = 0.025498;			//    0.045767    0.56  0.57744    
		
		cardriver_employment_parttime               = -0.042003 + 0.5;			//    0.066435   -0.63  0.52723    
		carpassenger_employment_parttime             = 0.088237;			//    0.086762    1.02  0.30915    
		cycling_employment_parttime                  = 0.132534;			//    0.089458    1.48  0.13847    
		publictransport_employment_parttime          = 0.132441;			//    0.080923    1.64  0.10171    
		
		cardriver_employment_marginal                = 0.213717	+ 0.5;			//    0.100312    2.13  0.03313 *  
		carpassenger_employment_marginal             = 0.523693;			//    0.130265    4.02  5.8e-05 ***
		cycling_employment_marginal                  = 0.464337;			//    0.138370    3.36  0.00079 ***
		publictransport_employment_marginal         = -0.037717;			//    0.164111   -0.23  0.81823    
		
		cardriver_employment_homekeeper             = -0.033279 + 0.5;			//    0.079020   -0.42  0.67365    
		carpassenger_employment_homekeeper           = 0.227939;			//    0.102101    2.23  0.02558 *  
		cycling_employment_homekeeper                = 0.112319;			//    0.120620    0.93  0.35176    
		publictransport_employment_homekeeper        = 0.163030;			//    0.137567    1.19  0.23598    
		
		cardriver_employment_unemployed             = -0.393130;			//    0.173683   -2.26  0.02361 *  
		carpassenger_employment_unemployed           = 0.118198;			//    0.199487    0.59  0.55351    
		cycling_employment_unemployed                = 0.144744;			//    0.246331    0.59  0.55680    
		publictransport_employment_unemployed        = 0.427594;			//    0.200941    2.13  0.03334 *  
		cardriver_employment_retired                = -0.310212;			//    0.085980   -3.61  0.00031 ***
		carpassenger_employment_retired             = -0.089564;			//    0.110725   -0.81  0.41858    
		cycling_employment_retired                  = -0.186958;			//    0.152070   -1.23  0.21892    
		publictransport_employment_retired           = 0.293968;			//    0.120858    2.43  0.01500 *  
		cardriver_employment_pupil                  = -0.101777;			//    0.179351   -0.57  0.57039    
		carpassenger_employment_pupil                = 0.314272;			//    0.193020    1.63  0.10349    
		cycling_employment_pupil                     = 0.357922;			//    0.271864    1.32  0.18799    
		publictransport_employment_pupil             = 0.165161;			//    0.189777    0.87  0.38414    
		cardriver_employment_student                 = 0.063366;			//    0.172271    0.37  0.71300    
		carpassenger_employment_student              = 0.252617;			//    0.207485    1.22  0.22341    
		cycling_employment_student                   = 0.642934;			//    0.249806    2.57  0.01006 *  
		publictransport_employment_student           = 0.134310;			//    0.174602    0.77  0.44175    
		cardriver_employment_apprentice              = 0.120948;			//    0.249668    0.48  0.62808    
		carpassenger_employment_apprentice           = 0.089036;			//    0.272387    0.33  0.74376    
		cycling_employment_apprentice               = -0.233933;			//    0.378578   -0.62  0.53662    
		publictransport_employment_apprentice        = 0.226006;			//    0.228546    0.99  0.32272    
		cardriver_employment_other                  = -0.649355;			//    0.240984   -2.69  0.00705 ** 
		carpassenger_employment_other               = -0.290941;			//    0.303200   -0.96  0.33727    
		cycling_employment_other                    = -0.141812;			//    0.302760   -0.47  0.63950    
		publictransport_employment_other             = 0.600329;			//    0.284210    2.11  0.03466 *  
		cardriver_age_06to09                        = -0.025922;			// 9300.607557    0.00  1.00000    
		carpassenger_age_06to09                      = 1.089469;			//    0.198102    5.50  3.8e-08 ***
		cycling_age_06to09                          = -0.677793;			//    0.288897   -2.35  0.01897 *  
		publictransport_age_06to09                  = -0.610519;			//    0.220712   -2.77  0.00567 ** 
		cardriver_age_10to17                        = -0.215459;			//    0.307164   -0.70  0.48302    
		carpassenger_age_10to17                      = 0.962738;			//    0.192797    4.99  5.9e-07 ***
		cycling_age_10to17                           = 0.243681;			//    0.272395    0.89  0.37101    
		publictransport_age_10to17                   = 0.432792;			//    0.191629    2.26  0.02391 *  
		cardriver_age_18to25                         = 0.397933;			//    0.129168    3.08  0.00206 ** 
		carpassenger_age_18to25                      = 0.753981;			//    0.152043    4.96  7.1e-07 ***
		cycling_age_18to25                          = -0.051777;			//    0.221571   -0.23  0.81523    
		publictransport_age_18to25                   = 0.355981;			//    0.151428    2.35  0.01873 *  
		cardriver_age_26to35                        = -0.232315;			//    0.062022   -3.75  0.00018 ***
		carpassenger_age_26to35                     = -0.126126;			//    0.087637   -1.44  0.15010    
		cycling_age_26to35                          = -0.350599;			//    0.110399   -3.18  0.00149 ** 
		publictransport_age_26to35                  = -0.005650;			//    0.087484   -0.06  0.94850    
		
		cardriver_age_51to60                         = 0.128212 + 0.2;			//    0.046482    2.76  0.00581 ** 
		carpassenger_age_51to60                      = 0.303818;			//    0.064297    4.73  2.3e-06 ***
		cycling_age_51to60                          = -0.070746;			//    0.079678   -0.89  0.37460    
		publictransport_age_51to60                   = 0.086078;			//    0.071276    1.21  0.22717    
		
		cardriver_age_61to70                         = 0.062373 + 0.3;			//    0.075808    0.82  0.41063    
		carpassenger_age_61to70                      = 0.352626;			//    0.097044    3.63  0.00028 ***
		cycling_age_61to70                           = 0.006072;			//    0.135679    0.04  0.96430    
		publictransport_age_61to70                   = 0.245061;			//    0.109620    2.24  0.02538 *  
		
		cardriver_age_71plus                         = 0.232349 + 0.2;			//    0.088929    2.61  0.00898 ** 
		carpassenger_age_71plus                      = 0.519522;			//    0.111882    4.64  3.4e-06 ***
		cycling_age_71plus                           = 0.324661;			//    0.166813    1.95  0.05162 .  
		
		publictransport_age_71plus                   = 0.473920;			//    0.127297    3.72  0.00020 ***
		cardriver_purpose_business                   = 0.391561;			//    0.151816    2.58  0.00990 ** 
		carpassenger_purpose_business                = 0.825257;			//    0.216375    3.81  0.00014 ***
		cycling_purpose_business                    = -0.202467;			//    0.239192   -0.85  0.39730    
		publictransport_purpose_business            = -0.310214;			//    0.205099   -1.51  0.13040    
		cardriver_purpose_education                 = -1.182445;			//    0.121364   -9.74  < 2e-16 ***
		carpassenger_purpose_education              = -0.048966;			//    0.102977   -0.48  0.63443    
		cycling_purpose_education                   = -0.682196;			//    0.111105   -6.14  8.2e-10 ***
		publictransport_purpose_education           = -0.351660;			//    0.098887   -3.56  0.00038 ***
		cardriver_purpose_service                    = 0.588850;			//    0.068730    8.57  < 2e-16 ***
		carpassenger_purpose_service                 = 0.907560;			//    0.106097    8.55  < 2e-16 ***
		cycling_purpose_service                     = -0.950002;			//    0.122099   -7.78  7.3e-15 ***
		publictransport_purpose_service             = -1.950997;			//    0.126949  -15.37  < 2e-16 ***
		cardriver_purpose_private_business          = -0.180579;			//    0.066821   -2.70  0.00688 ** 
		carpassenger_purpose_private_business        = 1.092019;			//    0.095472   11.44  < 2e-16 ***
		cycling_purpose_private_business            = -0.818111;			//    0.110588   -7.40  1.4e-13 ***
		publictransport_purpose_private_business    = -1.200158;			//    0.091203  -13.16  < 2e-16 ***
		cardriver_purpose_visit                     = -0.407348;			//    0.075742   -5.38  7.5e-08 ***
		carpassenger_purpose_visit                   = 0.895943;			//    0.100112    8.95  < 2e-16 ***
		cycling_purpose_visit                       = -1.170071;			//    0.127638   -9.17  < 2e-16 ***
		publictransport_purpose_visit               = -1.687710;			//    0.109076  -15.47  < 2e-16 ***
		cardriver_purpose_shopping_grocery          = -0.176351;			//    0.063439   -2.78  0.00544 ** 
		carpassenger_purpose_shopping_grocery        = 1.006751;			//    0.093749   10.74  < 2e-16 ***
		cycling_purpose_shopping_grocery            = -0.417571;			//    0.100128   -4.17  3.0e-05 ***
		publictransport_purpose_shopping_grocery    = -2.154149;			//    0.097296  -22.14  < 2e-16 ***
		cardriver_purpose_shopping_other             = 0.066482;			//    0.087627    0.76  0.44803    
		carpassenger_purpose_shopping_other          = 1.564270;			//    0.111591   14.02  < 2e-16 ***
		cycling_purpose_shopping_other              = -0.564547;			//    0.147208   -3.84  0.00013 ***
		publictransport_purpose_shopping_other      = -0.716243;			//    0.111730   -6.41  1.5e-10 ***
		cardriver_purpose_leisure_indoors           = -0.980725;			//    0.082767  -11.85  < 2e-16 ***
		carpassenger_purpose_leisure_indoors         = 1.267700;			//    0.103601   12.24  < 2e-16 ***
		cycling_purpose_leisure_indoors             = -1.741854;			//    0.158076  -11.02  < 2e-16 ***
		publictransport_purpose_leisure_indoors     = -0.912731;			//    0.100649   -9.07  < 2e-16 ***
		cardriver_purpose_leisure_outdoors           = 0.275104;			//    0.076678    3.59  0.00033 ***
		carpassenger_purpose_leisure_outdoors        = 1.728424;			//    0.098837   17.49  < 2e-16 ***
		cycling_purpose_leisure_outdoors            = -0.338363;			//    0.113530   -2.98  0.00288 ** 
		publictransport_purpose_leisure_outdoors    = -1.346927;			//    0.105912  -12.72  < 2e-16 ***
		cardriver_purpose_leisure_other             = -0.441574;			//    0.073945   -5.97  2.3e-09 ***
		carpassenger_purpose_leisure_other           = 1.055517;			//    0.097902   10.78  < 2e-16 ***
		cycling_purpose_leisure_other               = -0.887156;			//    0.114953   -7.72  1.2e-14 ***
		publictransport_purpose_leisure_other       = -0.973400;			//    0.095166  -10.23  < 2e-16 ***
		
		cardriver_purpose_leisure_walk              = -2.466454 + 1.5;			//    0.082512  -29.89  < 2e-16 ***
		carpassenger_purpose_leisure_walk           = -0.644084 + 1.5;			//    0.114627   -5.62  1.9e-08 ***
		cycling_purpose_leisure_walk                = -1.302843 + 0.5;			//    0.108720  -11.98  < 2e-16 ***
		publictransport_purpose_leisure_walk        = -2.368821	+ 0.5;			//    0.133208  -17.78  < 2e-16 ***
		
		cardriver_purpose_other                     = -0.130323;			//    0.453119   -0.29  0.77364    
		carpassenger_purpose_other                   = 1.374930;			//    0.370925    3.71  0.00021 ***
		cycling_purpose_other                       = -1.918031;			//    1.078362   -1.78  0.07530 .  
		publictransport_purpose_other               = -0.734605;			//    0.572306   -1.28  0.19929    
		
		cardriver_day_Saturday                       = 0.012023;			//    0.045163    0.27  0.79008    
		carpassenger_day_Saturday                    = 0.456848 + 0.5;			//    0.051855    8.81  < 2e-16 ***
		cycling_day_Saturday                        = -0.244866;			//    0.077022   -3.18  0.00148 ** 
		publictransport_day_Saturday                = -0.058388 + 0.2;			//    0.066347   -0.88  0.37883    
		
		cardriver_day_Sunday                        = -0.403989;			//    0.054576   -7.40  1.3e-13 ***
		carpassenger_day_Sunday                      = 0.047314 + 0.5;			//    0.060852    0.78  0.43685    
		cycling_day_Sunday                          = -0.601314;			//    0.091329   -6.58  4.6e-11 ***
		publictransport_day_Sunday                  = -0.685043 + 0.2;			//    0.081550   -8.40  < 2e-16 ***
		
		cardriver_num_activities                     = 0.346820;			//    0.046058    7.53  5.1e-14 ***
		carpassenger_num_activities                  = 0.395884;			//    0.051352    7.71  1.3e-14 ***
		cycling_num_activities                       = 0.159002;			//    0.075804    2.10  0.03595 *  
		publictransport_num_activities               = 0.310272;			//    0.057067    5.44  5.4e-08 ***
		cardriver_containsStrolling                 = -1.718683;			//    0.186976   -9.19  < 2e-16 ***
		carpassenger_containsStrolling              = -1.246214;			//    0.218826   -5.70  1.2e-08 ***
		cycling_containsStrolling                   = -0.788237;			//    0.299252   -2.63  0.00844 ** 
		publictransport_containsStrolling           = -1.363359;			//    0.243680   -5.59  2.2e-08 ***
		cardriver_containsVisit                      = 0.288389;			//    0.138075    2.09  0.03674 *  
		carpassenger_containsVisit                  = -0.153105;			//    0.148318   -1.03  0.30194    
		cycling_containsVisit                       = -0.007097;			//    0.202034   -0.04  0.97198    
		publictransport_containsVisit               = -0.232536;			//    0.170108   -1.37  0.17163    
		cardriver_containsPrivateB                   = 0.108387;			//    0.108870    1.00  0.31946    
		carpassenger_containsPrivateB               = -0.109497;			//    0.127210   -0.86  0.38937    
		cycling_containsPrivateB                    = -0.083470;			//    0.180245   -0.46  0.64330    
		publictransport_containsPrivateB            = -0.137020;			//    0.136943   -1.00  0.31704    
		cardriver_containsService                   = -0.032324;			//    0.188270   -0.17  0.86368    
		carpassenger_containsService                = -0.236251;			//    0.238717   -0.99  0.32234    
		cycling_containsService                     = -0.621794;			//    0.276289   -2.25  0.02442 *  
		publictransport_containsService             = -1.805136;			//    0.257845   -7.00  2.5e-12 ***
		cardriver_containsLeisure                    = 0.496568;			//    0.108722    4.57  4.9e-06 ***
		carpassenger_containsLeisure                 = 0.880719;			//    0.113993    7.73  1.1e-14 ***
		cycling_containsLeisure                      = 0.071934;			//    0.174958    0.41  0.68096    
		publictransport_containsLeisure              = 0.483828;			//    0.130014    3.72  0.00020 ***
		cardriver_containsShopping                   = 0.198151;			//    0.090259    2.20  0.02814 *  
		carpassenger_containsShopping               = -0.075629;			//    0.107083   -0.71  0.48002    
		cycling_containsShopping                     = 0.121028;			//    0.150308    0.81  0.42071    
		publictransport_containsShopping             = 0.003402;			//    0.113413    0.03  0.97607    
		cardriver_containsBusiness                   = 0.570994;			//    0.218593    2.61  0.00900 ** 
		carpassenger_containsBusiness                = 0.186603;			//    0.306586    0.61  0.54276    
		cycling_containsBusiness                     = 0.534377;			//    0.290230    1.84  0.06559 .  
		publictransport_containsBusiness             = 0.269922;			//    0.255398    1.06  0.29057    
		cardriver_firsttour                          = 0.474707;			//    0.061656    7.70  1.4e-14 ***
		carpassenger_firsttour                      = -0.258779;			//    0.068063   -3.80  0.00014 ***
		cycling_firsttour                            = 0.248048;			//    0.080634    3.08  0.00210 ** 
		publictransport_firsttour                    = 0.414252;			//    0.066280    6.25  4.1e-10 ***
		walking_usedbefore                           = 1.260243;			//    0.030709   41.04  < 2e-16 ***
		cardriver_usedbefore                         = 1.540377;			//    0.036691   41.98  < 2e-16 ***
		carpassenger_usedbefore                      = 1.408966;			//    0.033308   42.30  < 2e-16 ***
		cycling_usedbefore                           = 2.937429;			//    0.049291   59.59  < 2e-16 ***
		publictransport_usedbefore                   = 1.991595;			//    0.043442   45.85  < 2e-16 ***

	
		

		init();
	}

	protected void init() {
		
		super.init();

		this.parameterWalk.put("USED_BEFORE", walking_usedbefore);
		this.parameterBike.put("USED_BEFORE", cycling_usedbefore);
		this.parameterCar.put("USED_BEFORE", cardriver_usedbefore);
		this.parameterPassenger.put("USED_BEFORE", carpassenger_usedbefore);
		this.parameterPt.put("USED_BEFORE", publictransport_usedbefore);
		
		this.parameterWalk.put("INITIAL_TOUR", walking_firsttour);
		this.parameterBike.put("INITIAL_TOUR", cycling_firsttour);
		this.parameterCar.put("INITIAL_TOUR", cardriver_firsttour);
		this.parameterPassenger.put("INITIAL_TOUR", carpassenger_firsttour);
		this.parameterPt.put("INITIAL_TOUR", publictransport_firsttour);
	}



}
