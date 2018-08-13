package edu.kit.ifv.mobitopp.simulation.tour;


public class TourModeChoiceParameterTimeCostByEmpSexModeShares 
	extends TourModeChoiceParameterBaseTimeCostByEmpSex 
	implements TourModeChoiceParameter
{

	protected Double	walking_share_walking					= 0.0;
	protected Double	cardriver_share_walking				;
	protected Double	carpassenger_share_walking		;
	protected Double	cycling_share_walking					;
	protected Double	publictransport_share_walking	;
	
	protected Double	walking_share_cycling					= 0.0;
	protected Double	cardriver_share_cycling				;
	protected Double	carpassenger_share_cycling			;
	protected Double	cycling_share_cycling					;
	protected Double	publictransport_share_cycling	;

	protected Double	walking_share_cardriver					= 0.0;
	protected Double	cardriver_share_cardriver				;
	protected Double	carpassenger_share_cardriver			;
	protected Double	cycling_share_cardriver					;
	protected Double	publictransport_share_cardriver	;

	protected Double	walking_share_carpassenger					= 0.0;
	protected Double	cardriver_share_carpassenger				;
	protected Double	carpassenger_share_carpassenger			;
	protected Double	cycling_share_carpassenger					;
	protected Double	publictransport_share_carpassenger	;

	protected Double	walking_share_publictransport					= 0.0;
	protected Double	cardriver_share_publictransport				;
	protected Double	carpassenger_share_publictransport			;
	protected Double	cycling_share_publictransport					;
	protected Double	publictransport_share_publictransport	;



	/*

Call:
mlogit(formula = mode ~ notavailable + time + time:sex. + time:employment. + 
    cost + cost:sex. + cost:employment. | sex. + employment. + 
    age. + purpose. + day. + intrazonal. + num_activities + containsStrolling. + 
    containsVisit. + containsPrivateB. + containsService. + containsLeisure. + 
    containsShopping. + containsBusiness. + share_cycling + share_walking + 
    share_carpassenger + share_cardriver + share_publictransport, 
    data = trainingdata, reflevel = "walking", method = "nr", 
    print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2876          0.3914          0.1279          0.0544          0.1388 

nr method
23 iterations, 0h:8m:15s 
g'(-H)^-1g = 8.12E-07 
gradient close to zero 

Coefficients :
                                           Estimate Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                       0.85794    0.37394    2.29  0.02177 *  
carpassenger:(intercept)                   -2.02017    0.37951   -5.32  1.0e-07 ***
cycling:(intercept)                        -0.45742    0.52900   -0.86  0.38721    
publictransport:(intercept)                 0.41880    0.46683    0.90  0.36966    
notavailable                              -23.26878 1921.32087   -0.01  0.99034    
time                                       -0.02278    0.00094  -24.22  < 2e-16 ***
cost                                       -0.32793    0.02134  -15.36  < 2e-16 ***
time:sex.female                            -0.00263    0.00118   -2.23  0.02594 *  
time:employment.parttime                   -0.01460    0.00210   -6.96  3.4e-12 ***
time:employment.marginal                   -0.00108    0.00306   -0.35  0.72363    
time:employment.homekeeper                 -0.01002    0.00263   -3.82  0.00014 ***
time:employment.unemployed                 -0.00765    0.00473   -1.62  0.10579    
time:employment.retired                    -0.00797    0.00143   -5.59  2.3e-08 ***
time:employment.pupil                      -0.01149    0.00176   -6.55  5.8e-11 ***
time:employment.student                     0.00928    0.00235    3.95  7.9e-05 ***
time:employment.apprentice                 -0.01272    0.00608   -2.09  0.03640 *  
time:employment.other                      -0.04997    0.01289   -3.88  0.00011 ***
cost:sex.female                             0.01445    0.02665    0.54  0.58761    
cost:employment.parttime                    0.05201    0.04065    1.28  0.20079    
cost:employment.marginal                    0.11040    0.08373    1.32  0.18734    
cost:employment.homekeeper                  0.14433    0.06503    2.22  0.02644 *  
cost:employment.unemployed                  0.10113    0.10873    0.93  0.35234    
cost:employment.retired                     0.12567    0.03392    3.71  0.00021 ***
cost:employment.pupil                       0.16695    0.04711    3.54  0.00039 ***
cost:employment.student                     0.14126    0.06211    2.27  0.02294 *  
cost:employment.apprentice                  0.07636    0.08408    0.91  0.36379    
cost:employment.other                       0.01430    0.13918    0.10  0.91816    
cardriver:sex.female                       -0.25252    0.04897   -5.16  2.5e-07 ***
carpassenger:sex.female                     0.33497    0.05190    6.45  1.1e-10 ***
cycling:sex.female                         -0.26586    0.06141   -4.33  1.5e-05 ***
publictransport:sex.female                  0.01662    0.04954    0.34  0.73729    
cardriver:employment.parttime              -0.13491    0.06983   -1.93  0.05338 .  
carpassenger:employment.parttime           -0.01100    0.08869   -0.12  0.90127    
cycling:employment.parttime                 0.24011    0.09332    2.57  0.01008 *  
publictransport:employment.parttime         0.27247    0.08480    3.21  0.00131 ** 
cardriver:employment.marginal               0.24462    0.10476    2.34  0.01954 *  
carpassenger:employment.marginal            0.42912    0.13480    3.18  0.00145 ** 
cycling:employment.marginal                 0.42936    0.14844    2.89  0.00382 ** 
publictransport:employment.marginal         0.26377    0.17127    1.54  0.12353    
cardriver:employment.homekeeper             0.06887    0.08379    0.82  0.41111    
carpassenger:employment.homekeeper          0.12887    0.10656    1.21  0.22653    
cycling:employment.homekeeper               0.44531    0.12627    3.53  0.00042 ***
publictransport:employment.homekeeper       0.53655    0.14355    3.74  0.00019 ***
cardriver:employment.unemployed            -0.05220    0.18643   -0.28  0.77946    
carpassenger:employment.unemployed          0.02295    0.21229    0.11  0.91391    
cycling:employment.unemployed              -0.01538    0.27603   -0.06  0.95557    
publictransport:employment.unemployed       0.87420    0.21165    4.13  3.6e-05 ***
cardriver:employment.retired               -0.19008    0.09144   -2.08  0.03765 *  
carpassenger:employment.retired            -0.15328    0.11491   -1.33  0.18225    
cycling:employment.retired                  0.09400    0.16258    0.58  0.56314    
publictransport:employment.retired          0.54898    0.12669    4.33  1.5e-05 ***
cardriver:employment.pupil                  0.41022    0.18512    2.22  0.02669 *  
carpassenger:employment.pupil               0.48820    0.19650    2.48  0.01297 *  
cycling:employment.pupil                    0.27427    0.28135    0.97  0.32964    
publictransport:employment.pupil            0.24934    0.19587    1.27  0.20302    
cardriver:employment.student                0.30013    0.17646    1.70  0.08897 .  
carpassenger:employment.student             0.26460    0.21040    1.26  0.20853    
cycling:employment.student                  0.51374    0.26131    1.97  0.04930 *  
publictransport:employment.student          0.18172    0.18227    1.00  0.31876    
cardriver:employment.apprentice             0.19614    0.26089    0.75  0.45217    
carpassenger:employment.apprentice          0.22972    0.27785    0.83  0.40838    
cycling:employment.apprentice              -0.13904    0.38894   -0.36  0.72072    
publictransport:employment.apprentice       0.13881    0.24011    0.58  0.56318    
cardriver:employment.other                 -0.67582    0.25866   -2.61  0.00898 ** 
carpassenger:employment.other              -0.55976    0.32088   -1.74  0.08107 .  
cycling:employment.other                    0.23918    0.30104    0.79  0.42690    
publictransport:employment.other            1.18787    0.29086    4.08  4.4e-05 ***
cardriver:age.06to09                        0.08051 6301.32160    0.00  0.99999    
carpassenger:age.06to09                     0.90682    0.20491    4.43  9.6e-06 ***
cycling:age.06to09                         -0.03863    0.30251   -0.13  0.89838    
publictransport:age.06to09                 -0.43873    0.22878   -1.92  0.05515 .  
cardriver:age.10to17                       -0.03431    0.31630   -0.11  0.91362    
carpassenger:age.10to17                     0.83883    0.19827    4.23  2.3e-05 ***
cycling:age.10to17                          0.22798    0.28391    0.80  0.42198    
publictransport:age.10to17                  0.29917    0.19969    1.50  0.13409    
cardriver:age.18to25                        0.29366    0.13361    2.20  0.02795 *  
carpassenger:age.18to25                     0.66460    0.15545    4.28  1.9e-05 ***
cycling:age.18to25                          0.06659    0.22561    0.30  0.76787    
publictransport:age.18to25                  0.19930    0.15768    1.26  0.20625    
cardriver:age.26to35                       -0.16613    0.06601   -2.52  0.01185 *  
carpassenger:age.26to35                    -0.06749    0.08956   -0.75  0.45115    
cycling:age.26to35                         -0.22706    0.11666   -1.95  0.05161 .  
publictransport:age.26to35                 -0.01382    0.09160   -0.15  0.88004    
cardriver:age.51to60                        0.14631    0.04939    2.96  0.00306 ** 
carpassenger:age.51to60                     0.24767    0.06641    3.73  0.00019 ***
cycling:age.51to60                         -0.12348    0.08389   -1.47  0.14103    
publictransport:age.51to60                  0.10959    0.07442    1.47  0.14087    
cardriver:age.61to70                        0.15838    0.08101    1.96  0.05058 .  
carpassenger:age.61to70                     0.30436    0.10072    3.02  0.00251 ** 
cycling:age.61to70                         -0.05600    0.14461   -0.39  0.69857    
publictransport:age.61to70                  0.24287    0.11413    2.13  0.03334 *  
cardriver:age.71plus                        0.25421    0.09514    2.67  0.00754 ** 
carpassenger:age.71plus                     0.37830    0.11742    3.22  0.00127 ** 
cycling:age.71plus                          0.16380    0.17754    0.92  0.35620    
publictransport:age.71plus                  0.36619    0.13320    2.75  0.00597 ** 
cardriver:purpose.business                  0.29048    0.15861    1.83  0.06703 .  
carpassenger:purpose.business               0.63228    0.22502    2.81  0.00496 ** 
cycling:purpose.business                   -0.49794    0.25470   -1.95  0.05059 .  
publictransport:purpose.business           -0.44504    0.21174   -2.10  0.03557 *  
cardriver:purpose.education                -1.47579    0.12957  -11.39  < 2e-16 ***
carpassenger:purpose.education             -0.50733    0.10807   -4.69  2.7e-06 ***
cycling:purpose.education                  -0.94023    0.12162   -7.73  1.1e-14 ***
publictransport:purpose.education          -0.47358    0.10427   -4.54  5.6e-06 ***
cardriver:purpose.service                   0.55154    0.07366    7.49  7.0e-14 ***
carpassenger:purpose.service                0.81317    0.11024    7.38  1.6e-13 ***
cycling:purpose.service                    -1.12088    0.13014   -8.61  < 2e-16 ***
publictransport:purpose.service            -2.10744    0.13233  -15.93  < 2e-16 ***
cardriver:purpose.private_business         -0.21644    0.07125   -3.04  0.00238 ** 
carpassenger:purpose.private_business       0.89448    0.09911    9.02  < 2e-16 ***
cycling:purpose.private_business           -0.87959    0.11592   -7.59  3.3e-14 ***
publictransport:purpose.private_business   -1.44994    0.09578  -15.14  < 2e-16 ***
cardriver:purpose.visit                    -0.48048    0.08068   -5.96  2.6e-09 ***
carpassenger:purpose.visit                  0.77531    0.10367    7.48  7.5e-14 ***
cycling:purpose.visit                      -1.26636    0.13554   -9.34  < 2e-16 ***
publictransport:purpose.visit              -1.92363    0.11577  -16.62  < 2e-16 ***
cardriver:purpose.shopping_grocery         -0.11919    0.06817   -1.75  0.08041 .  
carpassenger:purpose.shopping_grocery       0.87726    0.09756    8.99  < 2e-16 ***
cycling:purpose.shopping_grocery           -0.49874    0.10509   -4.75  2.1e-06 ***
publictransport:purpose.shopping_grocery   -2.49179    0.10450  -23.85  < 2e-16 ***
cardriver:purpose.shopping_other            0.10798    0.09308    1.16  0.24606    
carpassenger:purpose.shopping_other         1.50754    0.11563   13.04  < 2e-16 ***
cycling:purpose.shopping_other             -0.66862    0.15640   -4.27  1.9e-05 ***
publictransport:purpose.shopping_other     -0.90767    0.11625   -7.81  5.8e-15 ***
cardriver:purpose.leisure_indoors          -1.07116    0.08738  -12.26  < 2e-16 ***
carpassenger:purpose.leisure_indoors        1.17217    0.10638   11.02  < 2e-16 ***
cycling:purpose.leisure_indoors            -2.07411    0.17347  -11.96  < 2e-16 ***
publictransport:purpose.leisure_indoors    -1.10658    0.10425  -10.62  < 2e-16 ***
cardriver:purpose.leisure_outdoors          0.27551    0.08107    3.40  0.00068 ***
carpassenger:purpose.leisure_outdoors       1.66023    0.10182   16.31  < 2e-16 ***
cycling:purpose.leisure_outdoors           -0.42166    0.11971   -3.52  0.00043 ***
publictransport:purpose.leisure_outdoors   -1.56435    0.11057  -14.15  < 2e-16 ***
cardriver:purpose.leisure_other            -0.47156    0.07866   -5.99  2.0e-09 ***
carpassenger:purpose.leisure_other          0.98193    0.10105    9.72  < 2e-16 ***
cycling:purpose.leisure_other              -1.05211    0.12302   -8.55  < 2e-16 ***
publictransport:purpose.leisure_other      -1.14552    0.09933  -11.53  < 2e-16 ***
cardriver:purpose.leisure_walk             -2.51813    0.08784  -28.67  < 2e-16 ***
carpassenger:purpose.leisure_walk          -0.84294    0.11914   -7.08  1.5e-12 ***
cycling:purpose.leisure_walk               -1.32831    0.11416  -11.64  < 2e-16 ***
publictransport:purpose.leisure_walk       -2.48813    0.13956  -17.83  < 2e-16 ***
cardriver:purpose.other                    -0.33697    0.48896   -0.69  0.49072    
carpassenger:purpose.other                  0.91970    0.39679    2.32  0.02046 *  
cycling:purpose.other                      -2.59210    1.16593   -2.22  0.02620 *  
publictransport:purpose.other              -0.95596    0.59674   -1.60  0.10917    
cardriver:day.Saturday                     -0.01732    0.04737   -0.37  0.71471    
carpassenger:day.Saturday                   0.65173    0.05316   12.26  < 2e-16 ***
cycling:day.Saturday                       -0.21392    0.08171   -2.62  0.00884 ** 
publictransport:day.Saturday               -0.19200    0.06973   -2.75  0.00590 ** 
cardriver:day.Sunday                       -0.56818    0.05714   -9.94  < 2e-16 ***
carpassenger:day.Sunday                     0.23777    0.06193    3.84  0.00012 ***
cycling:day.Sunday                         -0.76301    0.09964   -7.66  1.9e-14 ***
publictransport:day.Sunday                 -0.92331    0.08610  -10.72  < 2e-16 ***
cardriver:intrazonal.TRUE                  -1.13195    0.03939  -28.74  < 2e-16 ***
carpassenger:intrazonal.TRUE               -1.20055    0.04906  -24.47  < 2e-16 ***
cycling:intrazonal.TRUE                     0.04822    0.05620    0.86  0.39094    
publictransport:intrazonal.TRUE            -2.90131    0.09585  -30.27  < 2e-16 ***
cardriver:num_activities                    0.32440    0.04872    6.66  2.8e-11 ***
carpassenger:num_activities                 0.38040    0.05379    7.07  1.5e-12 ***
cycling:num_activities                      0.16131    0.08002    2.02  0.04381 *  
publictransport:num_activities              0.31786    0.06023    5.28  1.3e-07 ***
cardriver:containsStrolling.TRUE           -1.65064    0.19591   -8.43  < 2e-16 ***
carpassenger:containsStrolling.TRUE        -1.30411    0.22794   -5.72  1.1e-08 ***
cycling:containsStrolling.TRUE             -0.77742    0.32116   -2.42  0.01549 *  
publictransport:containsStrolling.TRUE     -1.24959    0.25296   -4.94  7.8e-07 ***
cardriver:containsVisit.TRUE                0.20815    0.14559    1.43  0.15282    
carpassenger:containsVisit.TRUE            -0.21854    0.15452   -1.41  0.15728    
cycling:containsVisit.TRUE                  0.03003    0.21435    0.14  0.88858    
publictransport:containsVisit.TRUE         -0.28528    0.17775   -1.60  0.10850    
cardriver:containsPrivateB.TRUE             0.17442    0.11487    1.52  0.12890    
carpassenger:containsPrivateB.TRUE         -0.08040    0.13284   -0.61  0.54501    
cycling:containsPrivateB.TRUE               0.01244    0.18711    0.07  0.94699    
publictransport:containsPrivateB.TRUE      -0.14475    0.14343   -1.01  0.31288    
cardriver:containsService.TRUE             -0.04557    0.19723   -0.23  0.81726    
carpassenger:containsService.TRUE          -0.36173    0.24958   -1.45  0.14724    
cycling:containsService.TRUE               -0.69986    0.28997   -2.41  0.01580 *  
publictransport:containsService.TRUE       -1.81609    0.26502   -6.85  7.2e-12 ***
cardriver:containsLeisure.TRUE              0.55502    0.11448    4.85  1.2e-06 ***
carpassenger:containsLeisure.TRUE           0.92331    0.11989    7.70  1.4e-14 ***
cycling:containsLeisure.TRUE                0.01144    0.18756    0.06  0.95138    
publictransport:containsLeisure.TRUE        0.44321    0.13653    3.25  0.00117 ** 
cardriver:containsShopping.TRUE             0.30300    0.09644    3.14  0.00168 ** 
carpassenger:containsShopping.TRUE         -0.04321    0.11300   -0.38  0.70221    
cycling:containsShopping.TRUE               0.17644    0.15881    1.11  0.26658    
publictransport:containsShopping.TRUE      -0.04324    0.12023   -0.36  0.71913    
cardriver:containsBusiness.TRUE             0.57011    0.22948    2.48  0.01298 *  
carpassenger:containsBusiness.TRUE          0.14580    0.31558    0.46  0.64407    
cycling:containsBusiness.TRUE               0.37823    0.31077    1.22  0.22357    
publictransport:containsBusiness.TRUE       0.29499    0.26472    1.11  0.26513    
cardriver:share_cycling                    -0.67436    0.39254   -1.72  0.08580 .  
carpassenger:share_cycling                 -0.60434    0.39214   -1.54  0.12328    
cycling:share_cycling                       5.11799    0.52863    9.68  < 2e-16 ***
publictransport:share_cycling              -0.10235    0.48811   -0.21  0.83390    
cardriver:share_walking                    -3.19716    0.37004   -8.64  < 2e-16 ***
carpassenger:share_walking                 -3.38391    0.36879   -9.18  < 2e-16 ***
cycling:share_walking                      -3.46081    0.52624   -6.58  4.8e-11 ***
publictransport:share_walking              -2.51434    0.46277   -5.43  5.5e-08 ***
cardriver:share_carpassenger               -1.15415    0.37926   -3.04  0.00234 ** 
carpassenger:share_carpassenger             2.13127    0.37201    5.73  1.0e-08 ***
cycling:share_carpassenger                 -1.07123    0.53617   -2.00  0.04573 *  
publictransport:share_carpassenger         -0.49064    0.47131   -1.04  0.29787    
cardriver:share_cardriver                   1.55198    0.36778    4.22  2.4e-05 ***
carpassenger:share_cardriver               -0.95313    0.36918   -2.58  0.00983 ** 
cycling:share_cardriver                    -0.99823    0.52417   -1.90  0.05686 .  
publictransport:share_cardriver            -0.79759    0.46443   -1.72  0.08591 .  
cardriver:share_publictransport            -1.88539    0.37576   -5.02  5.2e-07 ***
carpassenger:share_publictransport         -2.04057    0.37318   -5.47  4.6e-08 ***
cycling:share_publictransport              -1.17027    0.52810   -2.22  0.02669 *  
publictransport:share_publictransport       2.54391    0.46305    5.49  3.9e-08 ***
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -39100
McFadden R^2:  0.524 
Likelihood ratio test : chisq = 85900 (p.value = <2e-16)

		*/


	
	
	public TourModeChoiceParameterTimeCostByEmpSexModeShares() {
	
		walking																				 = 0.0	- 0.35;
		walking_intrazonal														 = 0.0	+ 0.45;
		walking_day_Saturday                           = 0.0;
		walking_day_Sunday                           	 = 0.0;
	
		// curr: v5, next: v5

		cardriver                                    = 0.85794;				//    0.37394    2.29  0.02177 *  
		carpassenger                                = -2.02017	- 0.1;				//    0.37951   -5.32  1.0e-07 ***
		cycling                                     = -0.45742;				//    0.52900   -0.86  0.38721    
		publictransport                              = 0.41880	+ 0.5;				//    0.46683    0.90  0.36966    
		
		cardriver_intrazonal                        = -1.13195	- 0.4;				//    0.03939  -28.74  < 2e-16 ***
		carpassenger_intrazonal                     = -1.20055	+ 0.1;				//    0.04906  -24.47  < 2e-16 ***
		cycling_intrazonal                           = 0.04822	+ 0.3;				//    0.05620    0.86  0.39094    
		publictransport_intrazonal                  = -2.90131	- 0.5;				//    0.09585  -30.27  < 2e-16 ***
		
		notavailable                               = -23.26878;				// 1921.32087   -0.01  0.99034    
		time                                        = -0.02278;				//    0.00094  -24.22  < 2e-16 ***
		cost                                        = -0.32793;				//    0.02134  -15.36  < 2e-16 ***
		
		time_sex_female                             = -0.00263;				//    0.00118   -2.23  0.02594 *  
		time_employment_parttime                    = -0.01460;				//    0.00210   -6.96  3.4e-12 ***
		time_employment_marginal                    = -0.00108;				//    0.00306   -0.35  0.72363    
		time_employment_homekeeper                  = -0.01002;				//    0.00263   -3.82  0.00014 ***
		time_employment_unemployed                  = -0.00765;				//    0.00473   -1.62  0.10579    
		time_employment_retired                     = -0.00797;				//    0.00143   -5.59  2.3e-08 ***
		time_employment_pupil                       = -0.01149;				//    0.00176   -6.55  5.8e-11 ***
		time_employment_student                      = 0.00928;				//    0.00235    3.95  7.9e-05 ***
		time_employment_apprentice                  = -0.01272;				//    0.00608   -2.09  0.03640 *  
		time_employment_other                       = -0.04997;				//    0.01289   -3.88  0.00011 ***
		cost_sex_female                              = 0.01445;				//    0.02665    0.54  0.58761    
		cost_employment_parttime                     = 0.05201;				//    0.04065    1.28  0.20079    
		cost_employment_marginal                     = 0.11040;				//    0.08373    1.32  0.18734    
		cost_employment_homekeeper                   = 0.14433;				//    0.06503    2.22  0.02644 *  
		cost_employment_unemployed                   = 0.10113;				//    0.10873    0.93  0.35234    
		cost_employment_retired                      = 0.12567;				//    0.03392    3.71  0.00021 ***
		cost_employment_pupil                        = 0.16695;				//    0.04711    3.54  0.00039 ***
		cost_employment_student                      = 0.14126;				//    0.06211    2.27  0.02294 *  
		cost_employment_apprentice                   = 0.07636;				//    0.08408    0.91  0.36379    
		cost_employment_other                        = 0.01430;				//    0.13918    0.10  0.91816    
		cardriver_sex_female                        = -0.25252;				//    0.04897   -5.16  2.5e-07 ***
		carpassenger_sex_female                      = 0.33497;				//    0.05190    6.45  1.1e-10 ***
		cycling_sex_female                          = -0.26586;				//    0.06141   -4.33  1.5e-05 ***
		publictransport_sex_female                   = 0.01662;				//    0.04954    0.34  0.73729    
		
		cardriver_employment_parttime               = -0.13491	+ 0.5;				//    0.06983   -1.93  0.05338 .  
		carpassenger_employment_parttime            = -0.01100	+ 0.2;				//    0.08869   -0.12  0.90127    
		cycling_employment_parttime                  = 0.24011;				//    0.09332    2.57  0.01008 *  
		publictransport_employment_parttime          = 0.27247;				//    0.08480    3.21  0.00131 ** 
		
		cardriver_employment_marginal                = 0.24462	+ 0.2;				//    0.10476    2.34  0.01954 *  
		carpassenger_employment_marginal             = 0.42912;				//    0.13480    3.18  0.00145 ** 
		cycling_employment_marginal                  = 0.42936;				//    0.14844    2.89  0.00382 ** 
		publictransport_employment_marginal          = 0.26377;				//    0.17127    1.54  0.12353    
		
		cardriver_employment_homekeeper              = 0.06887	+ 0.4;				//    0.08379    0.82  0.41111    
		carpassenger_employment_homekeeper           = 0.12887;				//    0.10656    1.21  0.22653    
		cycling_employment_homekeeper                = 0.44531;				//    0.12627    3.53  0.00042 ***
		publictransport_employment_homekeeper        = 0.53655	+ 0.2;				//    0.14355    3.74  0.00019 ***
		
		cardriver_employment_unemployed             = -0.05220;				//    0.18643   -0.28  0.77946    
		carpassenger_employment_unemployed           = 0.02295;				//    0.21229    0.11  0.91391    
		cycling_employment_unemployed               = -0.01538;				//    0.27603   -0.06  0.95557    
		publictransport_employment_unemployed        = 0.87420;				//    0.21165    4.13  3.6e-05 ***
		cardriver_employment_retired                = -0.19008;				//    0.09144   -2.08  0.03765 *  
		carpassenger_employment_retired             = -0.15328;				//    0.11491   -1.33  0.18225    
		cycling_employment_retired                   = 0.09400;				//    0.16258    0.58  0.56314    
		publictransport_employment_retired           = 0.54898;				//    0.12669    4.33  1.5e-05 ***
		cardriver_employment_pupil                   = 0.41022;				//    0.18512    2.22  0.02669 *  
		carpassenger_employment_pupil                = 0.48820;				//    0.19650    2.48  0.01297 *  
		cycling_employment_pupil                     = 0.27427;				//    0.28135    0.97  0.32964    
		publictransport_employment_pupil             = 0.24934;				//    0.19587    1.27  0.20302    
		cardriver_employment_student                 = 0.30013;				//    0.17646    1.70  0.08897 .  
		carpassenger_employment_student              = 0.26460;				//    0.21040    1.26  0.20853    
		cycling_employment_student                   = 0.51374;				//    0.26131    1.97  0.04930 *  
		publictransport_employment_student           = 0.18172;				//    0.18227    1.00  0.31876    
		cardriver_employment_apprentice              = 0.19614;				//    0.26089    0.75  0.45217    
		carpassenger_employment_apprentice           = 0.22972;				//    0.27785    0.83  0.40838    
		cycling_employment_apprentice               = -0.13904;				//    0.38894   -0.36  0.72072    
		publictransport_employment_apprentice        = 0.13881;				//    0.24011    0.58  0.56318    
		cardriver_employment_other                  = -0.67582;				//    0.25866   -2.61  0.00898 ** 
		carpassenger_employment_other               = -0.55976;				//    0.32088   -1.74  0.08107 .  
		cycling_employment_other                     = 0.23918;				//    0.30104    0.79  0.42690    
		publictransport_employment_other             = 1.18787;				//    0.29086    4.08  4.4e-05 ***
		
		cardriver_age_06to09                         = 0.08051;				// 6301.32160    0.00  0.99999    
		carpassenger_age_06to09                      = 0.90682;				//    0.20491    4.43  9.6e-06 ***
		cycling_age_06to09                          = -0.03863;				//    0.30251   -0.13  0.89838    
		publictransport_age_06to09                  = -0.43873;				//    0.22878   -1.92  0.05515 .  
		
		cardriver_age_10to17                        = -0.03431;				//    0.31630   -0.11  0.91362    
		carpassenger_age_10to17                      = 0.83883;				//    0.19827    4.23  2.3e-05 ***
		cycling_age_10to17                           = 0.22798;				//    0.28391    0.80  0.42198    
		publictransport_age_10to17                   = 0.29917;				//    0.19969    1.50  0.13409    
		
		cardriver_age_18to25                         = 0.29366	- 0.1;				//    0.13361    2.20  0.02795 *  
		carpassenger_age_18to25                      = 0.66460;				//    0.15545    4.28  1.9e-05 ***
		cycling_age_18to25                           = 0.06659;				//    0.22561    0.30  0.76787    
		publictransport_age_18to25                   = 0.19930	+ 0.1;				//    0.15768    1.26  0.20625    
		
		cardriver_age_26to35                        = -0.16613	- 0.1;				//    0.06601   -2.52  0.01185 *  
		carpassenger_age_26to35                     = -0.06749;				//    0.08956   -0.75  0.45115    
		cycling_age_26to35                          = -0.22706;				//    0.11666   -1.95  0.05161 .  
		publictransport_age_26to35                  = -0.01382	+ 0.1;				//    0.09160   -0.15  0.88004    
		
		cardriver_age_51to60                         = 0.14631;				//    0.04939    2.96  0.00306 ** 
		carpassenger_age_51to60                      = 0.24767;				//    0.06641    3.73  0.00019 ***
		cycling_age_51to60                          = -0.12348;				//    0.08389   -1.47  0.14103    
		publictransport_age_51to60                   = 0.10959;				//    0.07442    1.47  0.14087    
		cardriver_age_61to70                         = 0.15838;				//    0.08101    1.96  0.05058 .  
		carpassenger_age_61to70                      = 0.30436;				//    0.10072    3.02  0.00251 ** 
		cycling_age_61to70                          = -0.05600;				//    0.14461   -0.39  0.69857    
		publictransport_age_61to70                   = 0.24287;				//    0.11413    2.13  0.03334 *  
		cardriver_age_71plus                         = 0.25421;				//    0.09514    2.67  0.00754 ** 
		carpassenger_age_71plus                      = 0.37830;				//    0.11742    3.22  0.00127 ** 
		cycling_age_71plus                           = 0.16380;				//    0.17754    0.92  0.35620    
		publictransport_age_71plus                   = 0.36619;				//    0.13320    2.75  0.00597 ** 
		cardriver_purpose_business                   = 0.29048;				//    0.15861    1.83  0.06703 .  
		carpassenger_purpose_business                = 0.63228;				//    0.22502    2.81  0.00496 ** 
		cycling_purpose_business                    = -0.49794;				//    0.25470   -1.95  0.05059 .  
		publictransport_purpose_business            = -0.44504;				//    0.21174   -2.10  0.03557 *  
		cardriver_purpose_education                 = -1.47579;				//    0.12957  -11.39  < 2e-16 ***
		carpassenger_purpose_education              = -0.50733;				//    0.10807   -4.69  2.7e-06 ***
		cycling_purpose_education                   = -0.94023;				//    0.12162   -7.73  1.1e-14 ***
		publictransport_purpose_education           = -0.47358;				//    0.10427   -4.54  5.6e-06 ***
		cardriver_purpose_service                    = 0.55154;				//    0.07366    7.49  7.0e-14 ***
		carpassenger_purpose_service                 = 0.81317;				//    0.11024    7.38  1.6e-13 ***
		cycling_purpose_service                     = -1.12088;				//    0.13014   -8.61  < 2e-16 ***
		publictransport_purpose_service             = -2.10744;				//    0.13233  -15.93  < 2e-16 ***
		cardriver_purpose_private_business          = -0.21644;				//    0.07125   -3.04  0.00238 ** 
		carpassenger_purpose_private_business        = 0.89448;				//    0.09911    9.02  < 2e-16 ***
		cycling_purpose_private_business            = -0.87959;				//    0.11592   -7.59  3.3e-14 ***
		publictransport_purpose_private_business    = -1.44994;				//    0.09578  -15.14  < 2e-16 ***
		cardriver_purpose_visit                     = -0.48048;				//    0.08068   -5.96  2.6e-09 ***
		carpassenger_purpose_visit                   = 0.77531;				//    0.10367    7.48  7.5e-14 ***
		cycling_purpose_visit                       = -1.26636;				//    0.13554   -9.34  < 2e-16 ***
		publictransport_purpose_visit               = -1.92363;				//    0.11577  -16.62  < 2e-16 ***
		cardriver_purpose_shopping_grocery          = -0.11919;				//    0.06817   -1.75  0.08041 .  
		carpassenger_purpose_shopping_grocery        = 0.87726;				//    0.09756    8.99  < 2e-16 ***
		cycling_purpose_shopping_grocery            = -0.49874;				//    0.10509   -4.75  2.1e-06 ***
		publictransport_purpose_shopping_grocery    = -2.49179;				//    0.10450  -23.85  < 2e-16 ***
		cardriver_purpose_shopping_other             = 0.10798;				//    0.09308    1.16  0.24606    
		carpassenger_purpose_shopping_other          = 1.50754;				//    0.11563   13.04  < 2e-16 ***
		cycling_purpose_shopping_other              = -0.66862;				//    0.15640   -4.27  1.9e-05 ***
		publictransport_purpose_shopping_other      = -0.90767;				//    0.11625   -7.81  5.8e-15 ***
		cardriver_purpose_leisure_indoors           = -1.07116;				//    0.08738  -12.26  < 2e-16 ***
		carpassenger_purpose_leisure_indoors         = 1.17217;				//    0.10638   11.02  < 2e-16 ***
		cycling_purpose_leisure_indoors             = -2.07411;				//    0.17347  -11.96  < 2e-16 ***
		publictransport_purpose_leisure_indoors     = -1.10658;				//    0.10425  -10.62  < 2e-16 ***
		cardriver_purpose_leisure_outdoors           = 0.27551;				//    0.08107    3.40  0.00068 ***
		carpassenger_purpose_leisure_outdoors        = 1.66023;				//    0.10182   16.31  < 2e-16 ***
		cycling_purpose_leisure_outdoors            = -0.42166;				//    0.11971   -3.52  0.00043 ***
		publictransport_purpose_leisure_outdoors    = -1.56435;				//    0.11057  -14.15  < 2e-16 ***
		cardriver_purpose_leisure_other             = -0.47156;				//    0.07866   -5.99  2.0e-09 ***
		carpassenger_purpose_leisure_other           = 0.98193;				//    0.10105    9.72  < 2e-16 ***
		cycling_purpose_leisure_other               = -1.05211;				//    0.12302   -8.55  < 2e-16 ***
		publictransport_purpose_leisure_other       = -1.14552;				//    0.09933  -11.53  < 2e-16 ***
		
		cardriver_purpose_leisure_walk              = -2.51813	+ 2.2;				//    0.08784  -28.67  < 2e-16 ***
		carpassenger_purpose_leisure_walk           = -0.84294	+ 1.5;				//    0.11914   -7.08  1.5e-12 ***
		cycling_purpose_leisure_walk                = -1.32831	+ 0.9;				//    0.11416  -11.64  < 2e-16 ***
		publictransport_purpose_leisure_walk        = -2.48813	+ 1.0;				//    0.13956  -17.83  < 2e-16 ***
		
		cardriver_purpose_other                     = -0.33697;				//    0.48896   -0.69  0.49072    
		carpassenger_purpose_other                   = 0.91970;				//    0.39679    2.32  0.02046 *  
		cycling_purpose_other                       = -2.59210;				//    1.16593   -2.22  0.02620 *  
		publictransport_purpose_other               = -0.95596;				//    0.59674   -1.60  0.10917    
		
		cardriver_day_Saturday                      = -0.01732;				//    0.04737   -0.37  0.71471    
		carpassenger_day_Saturday                    = 0.65173;				//    0.05316   12.26  < 2e-16 ***
		cycling_day_Saturday                        = -0.21392;				//    0.08171   -2.62  0.00884 ** 
		publictransport_day_Saturday                = -0.19200;				//    0.06973   -2.75  0.00590 ** 
		cardriver_day_Sunday                        = -0.56818;				//    0.05714   -9.94  < 2e-16 ***
		carpassenger_day_Sunday                      = 0.23777;				//    0.06193    3.84  0.00012 ***
		cycling_day_Sunday                          = -0.76301;				//    0.09964   -7.66  1.9e-14 ***
		publictransport_day_Sunday                  = -0.92331;				//    0.08610  -10.72  < 2e-16 ***
		
		cardriver_num_activities                     = 0.32440;				//    0.04872    6.66  2.8e-11 ***
		carpassenger_num_activities                  = 0.38040;				//    0.05379    7.07  1.5e-12 ***
		cycling_num_activities                       = 0.16131;				//    0.08002    2.02  0.04381 *  
		publictransport_num_activities               = 0.31786;				//    0.06023    5.28  1.3e-07 ***
		cardriver_containsStrolling                 = -1.65064;				//    0.19591   -8.43  < 2e-16 ***
		carpassenger_containsStrolling              = -1.30411;				//    0.22794   -5.72  1.1e-08 ***
		cycling_containsStrolling                   = -0.77742;				//    0.32116   -2.42  0.01549 *  
		publictransport_containsStrolling           = -1.24959;				//    0.25296   -4.94  7.8e-07 ***
		cardriver_containsVisit                      = 0.20815;				//    0.14559    1.43  0.15282    
		carpassenger_containsVisit                  = -0.21854;				//    0.15452   -1.41  0.15728    
		cycling_containsVisit                        = 0.03003;				//    0.21435    0.14  0.88858    
		publictransport_containsVisit               = -0.28528;				//    0.17775   -1.60  0.10850    
		cardriver_containsPrivateB                   = 0.17442;				//    0.11487    1.52  0.12890    
		carpassenger_containsPrivateB               = -0.08040;				//    0.13284   -0.61  0.54501    
		cycling_containsPrivateB                     = 0.01244;				//    0.18711    0.07  0.94699    
		publictransport_containsPrivateB            = -0.14475;				//    0.14343   -1.01  0.31288    
		cardriver_containsService                   = -0.04557;				//    0.19723   -0.23  0.81726    
		carpassenger_containsService                = -0.36173;				//    0.24958   -1.45  0.14724    
		cycling_containsService                     = -0.69986;				//    0.28997   -2.41  0.01580 *  
		publictransport_containsService             = -1.81609;				//    0.26502   -6.85  7.2e-12 ***
		cardriver_containsLeisure                    = 0.55502;				//    0.11448    4.85  1.2e-06 ***
		carpassenger_containsLeisure                 = 0.92331;				//    0.11989    7.70  1.4e-14 ***
		cycling_containsLeisure                      = 0.01144;				//    0.18756    0.06  0.95138    
		publictransport_containsLeisure              = 0.44321;				//    0.13653    3.25  0.00117 ** 
		cardriver_containsShopping                   = 0.30300;				//    0.09644    3.14  0.00168 ** 
		carpassenger_containsShopping               = -0.04321;				//    0.11300   -0.38  0.70221    
		cycling_containsShopping                     = 0.17644;				//    0.15881    1.11  0.26658    
		publictransport_containsShopping            = -0.04324;				//    0.12023   -0.36  0.71913    
		cardriver_containsBusiness                   = 0.57011;				//    0.22948    2.48  0.01298 *  
		carpassenger_containsBusiness                = 0.14580;				//    0.31558    0.46  0.64407    
		cycling_containsBusiness                     = 0.37823;				//    0.31077    1.22  0.22357    
		publictransport_containsBusiness             = 0.29499;				//    0.26472    1.11  0.26513    
		
		cardriver_share_cycling                     = -0.67436;				//    0.39254   -1.72  0.08580 .  
		carpassenger_share_cycling                  = -0.60434;				//    0.39214   -1.54  0.12328    
		cycling_share_cycling                        = 5.11799;				//    0.52863    9.68  < 2e-16 ***
		publictransport_share_cycling               = -0.10235;				//    0.48811   -0.21  0.83390    
		cardriver_share_walking                     = -3.19716;				//    0.37004   -8.64  < 2e-16 ***
		carpassenger_share_walking                  = -3.38391;				//    0.36879   -9.18  < 2e-16 ***
		cycling_share_walking                       = -3.46081;				//    0.52624   -6.58  4.8e-11 ***
		publictransport_share_walking               = -2.51434;				//    0.46277   -5.43  5.5e-08 ***
		cardriver_share_carpassenger                = -1.15415;				//    0.37926   -3.04  0.00234 ** 
		carpassenger_share_carpassenger              = 2.13127;				//    0.37201    5.73  1.0e-08 ***
		cycling_share_carpassenger                  = -1.07123;				//    0.53617   -2.00  0.04573 *  
		publictransport_share_carpassenger          = -0.49064;				//    0.47131   -1.04  0.29787    
		cardriver_share_cardriver                    = 1.55198;				//    0.36778    4.22  2.4e-05 ***
		carpassenger_share_cardriver                = -0.95313;				//    0.36918   -2.58  0.00983 ** 
		cycling_share_cardriver                     = -0.99823;				//    0.52417   -1.90  0.05686 .  
		publictransport_share_cardriver             = -0.79759;				//    0.46443   -1.72  0.08591 .  
		cardriver_share_publictransport             = -1.88539;				//    0.37576   -5.02  5.2e-07 ***
		carpassenger_share_publictransport          = -2.04057;				//    0.37318   -5.47  4.6e-08 ***
		cycling_share_publictransport               = -1.17027;				//    0.52810   -2.22  0.02669 *  
		publictransport_share_publictransport        = 2.54391;				//    0.46305    5.49  3.9e-08 ***

	
		

		init();
	}

	protected void init() {
		
		super.init();

		this.parameterWalk.put("SHARE_MODE_WALK", 			walking_share_walking);
		this.parameterWalk.put("SHARE_MODE_BIKE", 			walking_share_cycling);
		this.parameterWalk.put("SHARE_MODE_CAR", 			walking_share_cardriver);
		this.parameterWalk.put("SHARE_MODE_PASSENGER", walking_share_carpassenger);
		this.parameterWalk.put("SHARE_MODE_PT", 				walking_share_publictransport);
		
		this.parameterBike.put("SHARE_MODE_WALK", 				cycling_share_walking);
		this.parameterBike.put("SHARE_MODE_BIKE", 				cycling_share_cycling);
		this.parameterBike.put("SHARE_MODE_CAR", 				cycling_share_cardriver);
		this.parameterBike.put("SHARE_MODE_PASSENGER", 	cycling_share_carpassenger);
		this.parameterBike.put("SHARE_MODE_PT", 					cycling_share_publictransport);
		
		this.parameterCar.put("SHARE_MODE_WALK", 				cardriver_share_walking);
		this.parameterCar.put("SHARE_MODE_BIKE", 				cardriver_share_cycling);
		this.parameterCar.put("SHARE_MODE_CAR", 					cardriver_share_cardriver);
		this.parameterCar.put("SHARE_MODE_PASSENGER", 		cardriver_share_carpassenger);
		this.parameterCar.put("SHARE_MODE_PT", 					cardriver_share_publictransport);
		
		this.parameterPassenger.put("SHARE_MODE_WALK", 			carpassenger_share_walking);
		this.parameterPassenger.put("SHARE_MODE_BIKE", 			carpassenger_share_cycling);
		this.parameterPassenger.put("SHARE_MODE_CAR", 				carpassenger_share_cardriver);
		this.parameterPassenger.put("SHARE_MODE_PASSENGER", 	carpassenger_share_carpassenger);
		this.parameterPassenger.put("SHARE_MODE_PT", 				carpassenger_share_publictransport);
		
		this.parameterPt.put("SHARE_MODE_WALK", 			publictransport_share_walking);
		this.parameterPt.put("SHARE_MODE_BIKE", 			publictransport_share_cycling);
		this.parameterPt.put("SHARE_MODE_CAR", 			publictransport_share_cardriver);
		this.parameterPt.put("SHARE_MODE_PASSENGER", publictransport_share_carpassenger);
		this.parameterPt.put("SHARE_MODE_PT", 				publictransport_share_publictransport);
	}



}
