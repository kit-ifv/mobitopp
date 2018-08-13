package edu.kit.ifv.mobitopp.simulation.tour;


public class TourModeChoiceParameterTimeCostByEmpSexModeSharesPref 
	extends TourModeChoiceParameterBaseTimeCostByEmpSex 
	implements TourModeChoiceParameter
{

	protected Double	walking_preference					;
	protected Double	cardriver_preference				;
	protected Double	carpassenger_preference			;
	protected Double	cycling_preference					;
	protected Double	publictransport_preference	;


	/*
Call:
mlogit(formula = mode ~ notavailable + time + time:sex. + time:employment. + 
    cost + cost:sex. + cost:employment. | sex. + employment. + 
    age. + purpose. + day. + intrazonal. + num_activities + containsStrolling. + 
    containsVisit. + containsPrivateB. + containsService. + containsLeisure. + 
    containsShopping. + containsBusiness. | preference, data = trainingdata, 
    reflevel = "walking", method = "nr", print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2876          0.3914          0.1279          0.0544          0.1388 

nr method
23 iterations, 0h:7m:9s 
g'(-H)^-1g = 8.11E-07 
gradient close to zero 

Coefficients :
                                            Estimate  Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                      -0.293352    0.093572   -3.14  0.00172 ** 
carpassenger:(intercept)                   -3.191544    0.113864  -28.03  < 2e-16 ***
cycling:(intercept)                        -1.559490    0.122480  -12.73  < 2e-16 ***
publictransport:(intercept)                -0.224418    0.101657   -2.21  0.02727 *  
notavailable                              -23.278048 1921.050915   -0.01  0.99033    
time                                       -0.022911    0.000939  -24.40  < 2e-16 ***
cost                                       -0.352451    0.021142  -16.67  < 2e-16 ***
time:sex.female                            -0.002507    0.001176   -2.13  0.03302 *  
time:employment.parttime                   -0.014396    0.002091   -6.89  5.7e-12 ***
time:employment.marginal                   -0.000873    0.003044   -0.29  0.77420    
time:employment.homekeeper                 -0.009961    0.002622   -3.80  0.00015 ***
time:employment.unemployed                 -0.007192    0.004690   -1.53  0.12519    
time:employment.retired                    -0.007681    0.001421   -5.40  6.5e-08 ***
time:employment.pupil                      -0.011054    0.001755   -6.30  3.0e-10 ***
time:employment.student                     0.009486    0.002352    4.03  5.5e-05 ***
time:employment.apprentice                 -0.012318    0.006000   -2.05  0.04009 *  
time:employment.other                      -0.049576    0.012783   -3.88  0.00011 ***
cost:sex.female                             0.015969    0.026588    0.60  0.54811    
cost:employment.parttime                    0.051832    0.040569    1.28  0.20138    
cost:employment.marginal                    0.127214    0.083879    1.52  0.12936    
cost:employment.homekeeper                  0.161912    0.065126    2.49  0.01291 *  
cost:employment.unemployed                  0.114360    0.107789    1.06  0.28871    
cost:employment.retired                     0.138181    0.033828    4.08  4.4e-05 ***
cost:employment.pupil                       0.208181    0.046313    4.50  7.0e-06 ***
cost:employment.student                     0.152180    0.061683    2.47  0.01362 *  
cost:employment.apprentice                  0.073765    0.084167    0.88  0.38081    
cost:employment.other                       0.015877    0.140279    0.11  0.90989    
cardriver:sex.female                       -0.276715    0.047560   -5.82  5.9e-09 ***
carpassenger:sex.female                     0.287235    0.051062    5.63  1.9e-08 ***
cycling:sex.female                         -0.297538    0.059615   -4.99  6.0e-07 ***
publictransport:sex.female                  0.027765    0.048528    0.57  0.56723    
cardriver:employment.parttime              -0.097344    0.069267   -1.41  0.15992    
carpassenger:employment.parttime            0.037770    0.088224    0.43  0.66856    
cycling:employment.parttime                 0.251890    0.092567    2.72  0.00651 ** 
publictransport:employment.parttime         0.305514    0.084696    3.61  0.00031 ***
cardriver:employment.marginal               0.299324    0.104185    2.87  0.00407 ** 
carpassenger:employment.marginal            0.493785    0.134097    3.68  0.00023 ***
cycling:employment.marginal                 0.429255    0.146836    2.92  0.00346 ** 
publictransport:employment.marginal         0.319483    0.171380    1.86  0.06230 .  
cardriver:employment.homekeeper             0.130630    0.083242    1.57  0.11658    
carpassenger:employment.homekeeper          0.196819    0.105861    1.86  0.06300 .  
cycling:employment.homekeeper               0.427040    0.124246    3.44  0.00059 ***
publictransport:employment.homekeeper       0.600493    0.143699    4.18  2.9e-05 ***
cardriver:employment.unemployed            -0.015800    0.185194   -0.09  0.93201    
carpassenger:employment.unemployed          0.042417    0.209806    0.20  0.83978    
cycling:employment.unemployed              -0.016999    0.269736   -0.06  0.94975    
publictransport:employment.unemployed       0.927599    0.211742    4.38  1.2e-05 ***
cardriver:employment.retired               -0.146127    0.090752   -1.61  0.10736    
carpassenger:employment.retired            -0.131208    0.114281   -1.15  0.25092    
cycling:employment.retired                  0.063581    0.160778    0.40  0.69250    
publictransport:employment.retired          0.587921    0.126533    4.65  3.4e-06 ***
cardriver:employment.pupil                  0.373876    0.184083    2.03  0.04225 *  
carpassenger:employment.pupil               0.447942    0.195548    2.29  0.02198 *  
cycling:employment.pupil                    0.242443    0.280273    0.87  0.38702    
publictransport:employment.pupil            0.269258    0.196869    1.37  0.17140    
cardriver:employment.student                0.262528    0.174921    1.50  0.13340    
carpassenger:employment.student             0.210276    0.209189    1.01  0.31480    
cycling:employment.student                  0.503826    0.259424    1.94  0.05213 .  
publictransport:employment.student          0.144475    0.183081    0.79  0.43004    
cardriver:employment.apprentice             0.144583    0.256853    0.56  0.57350    
carpassenger:employment.apprentice          0.144972    0.274602    0.53  0.59754    
cycling:employment.apprentice              -0.156119    0.387775   -0.40  0.68724    
publictransport:employment.apprentice       0.111171    0.240925    0.46  0.64449    
cardriver:employment.other                 -0.641500    0.256443   -2.50  0.01237 *  
carpassenger:employment.other              -0.560274    0.320186   -1.75  0.08015 .  
cycling:employment.other                    0.200613    0.298223    0.67  0.50114    
publictransport:employment.other            1.256600    0.290970    4.32  1.6e-05 ***
cardriver:age.06to09                        0.122900 6339.570595    0.00  0.99998    
carpassenger:age.06to09                     0.842096    0.202747    4.15  3.3e-05 ***
cycling:age.06to09                         -0.143884    0.298852   -0.48  0.63019    
publictransport:age.06to09                 -0.358173    0.228541   -1.57  0.11706    
cardriver:age.10to17                       -0.145098    0.315426   -0.46  0.64551    
carpassenger:age.10to17                     0.681633    0.196351    3.47  0.00052 ***
cycling:age.10to17                          0.195020    0.281967    0.69  0.48916    
publictransport:age.10to17                  0.304274    0.199498    1.53  0.12721    
cardriver:age.18to25                        0.213789    0.132155    1.62  0.10572    
carpassenger:age.18to25                     0.564918    0.154685    3.65  0.00026 ***
cycling:age.18to25                          0.069029    0.224552    0.31  0.75853    
publictransport:age.18to25                  0.168721    0.158160    1.07  0.28607    
cardriver:age.26to35                       -0.198738    0.065587   -3.03  0.00244 ** 
carpassenger:age.26to35                    -0.126698    0.089348   -1.42  0.15618    
cycling:age.26to35                         -0.238422    0.115646   -2.06  0.03924 *  
publictransport:age.26to35                 -0.035066    0.091657   -0.38  0.70204    
cardriver:age.51to60                        0.131853    0.049085    2.69  0.00723 ** 
carpassenger:age.51to60                     0.219922    0.066364    3.31  0.00092 ***
cycling:age.51to60                         -0.136419    0.083346   -1.64  0.10167    
publictransport:age.51to60                  0.111916    0.074298    1.51  0.13199    
cardriver:age.61to70                        0.130076    0.080283    1.62  0.10518    
carpassenger:age.61to70                     0.258182    0.100342    2.57  0.01008 *  
cycling:age.61to70                         -0.064123    0.143148   -0.45  0.65419    
publictransport:age.61to70                  0.238239    0.113967    2.09  0.03658 *  
cardriver:age.71plus                        0.223621    0.094476    2.37  0.01794 *  
carpassenger:age.71plus                     0.311364    0.116981    2.66  0.00778 ** 
cycling:age.71plus                          0.158934    0.175875    0.90  0.36617    
publictransport:age.71plus                  0.367595    0.133121    2.76  0.00576 ** 
cardriver:purpose.business                  0.291366    0.158180    1.84  0.06548 .  
carpassenger:purpose.business               0.654147    0.225092    2.91  0.00366 ** 
cycling:purpose.business                   -0.485294    0.253456   -1.91  0.05553 .  
publictransport:purpose.business           -0.426088    0.211617   -2.01  0.04406 *  
cardriver:purpose.education                -1.429168    0.129102  -11.07  < 2e-16 ***
carpassenger:purpose.education             -0.462883    0.107808   -4.29  1.8e-05 ***
cycling:purpose.education                  -0.889437    0.120152   -7.40  1.3e-13 ***
publictransport:purpose.education          -0.465980    0.104574   -4.46  8.4e-06 ***
cardriver:purpose.service                   0.524307    0.073148    7.17  7.6e-13 ***
carpassenger:purpose.service                0.813857    0.110163    7.39  1.5e-13 ***
cycling:purpose.service                    -1.116147    0.128890   -8.66  < 2e-16 ***
publictransport:purpose.service            -2.126263    0.132537  -16.04  < 2e-16 ***
cardriver:purpose.private_business         -0.253191    0.070824   -3.57  0.00035 ***
carpassenger:purpose.private_business       0.876897    0.099107    8.85  < 2e-16 ***
cycling:purpose.private_business           -0.872929    0.114987   -7.59  3.2e-14 ***
publictransport:purpose.private_business   -1.464461    0.095946  -15.26  < 2e-16 ***
cardriver:purpose.visit                    -0.509382    0.080229   -6.35  2.2e-10 ***
carpassenger:purpose.visit                  0.770071    0.103681    7.43  1.1e-13 ***
cycling:purpose.visit                      -1.256511    0.134705   -9.33  < 2e-16 ***
publictransport:purpose.visit              -1.918849    0.115814  -16.57  < 2e-16 ***
cardriver:purpose.shopping_grocery         -0.175323    0.067595   -2.59  0.00949 ** 
carpassenger:purpose.shopping_grocery       0.825168    0.097363    8.48  < 2e-16 ***
cycling:purpose.shopping_grocery           -0.496997    0.103851   -4.79  1.7e-06 ***
publictransport:purpose.shopping_grocery   -2.538753    0.104744  -24.24  < 2e-16 ***
cardriver:purpose.shopping_other            0.068975    0.092528    0.75  0.45600    
carpassenger:purpose.shopping_other         1.490224    0.115434   12.91  < 2e-16 ***
cycling:purpose.shopping_other             -0.684494    0.155808   -4.39  1.1e-05 ***
publictransport:purpose.shopping_other     -0.901659    0.116280   -7.75  8.9e-15 ***
cardriver:purpose.leisure_indoors          -1.103243    0.086935  -12.69  < 2e-16 ***
carpassenger:purpose.leisure_indoors        1.173020    0.106396   11.03  < 2e-16 ***
cycling:purpose.leisure_indoors            -2.073561    0.172954  -11.99  < 2e-16 ***
publictransport:purpose.leisure_indoors    -1.105808    0.104382  -10.59  < 2e-16 ***
cardriver:purpose.leisure_outdoors          0.215756    0.080482    2.68  0.00734 ** 
carpassenger:purpose.leisure_outdoors       1.633896    0.101729   16.06  < 2e-16 ***
cycling:purpose.leisure_outdoors           -0.435285    0.118905   -3.66  0.00025 ***
publictransport:purpose.leisure_outdoors   -1.570656    0.110682  -14.19  < 2e-16 ***
cardriver:purpose.leisure_other            -0.507325    0.078297   -6.48  9.2e-11 ***
carpassenger:purpose.leisure_other          0.967248    0.101037    9.57  < 2e-16 ***
cycling:purpose.leisure_other              -1.049754    0.122199   -8.59  < 2e-16 ***
publictransport:purpose.leisure_other      -1.156962    0.099453  -11.63  < 2e-16 ***
cardriver:purpose.leisure_walk             -2.533394    0.087442  -28.97  < 2e-16 ***
carpassenger:purpose.leisure_walk          -0.842457    0.119069   -7.08  1.5e-12 ***
cycling:purpose.leisure_walk               -1.297138    0.112578  -11.52  < 2e-16 ***
publictransport:purpose.leisure_walk       -2.529339    0.139651  -18.11  < 2e-16 ***
cardriver:purpose.other                    -0.339099    0.485939   -0.70  0.48529    
carpassenger:purpose.other                  0.930881    0.393539    2.37  0.01801 *  
cycling:purpose.other                      -2.612580    1.173522   -2.23  0.02600 *  
publictransport:purpose.other              -0.890288    0.589479   -1.51  0.13097    
cardriver:day.Saturday                     -0.036950    0.047085   -0.78  0.43259    
carpassenger:day.Saturday                   0.640869    0.053048   12.08  < 2e-16 ***
cycling:day.Saturday                       -0.223112    0.081306   -2.74  0.00607 ** 
publictransport:day.Saturday               -0.181994    0.069720   -2.61  0.00905 ** 
cardriver:day.Sunday                       -0.586064    0.056882  -10.30  < 2e-16 ***
carpassenger:day.Sunday                     0.228197    0.061823    3.69  0.00022 ***
cycling:day.Sunday                         -0.760191    0.099135   -7.67  1.8e-14 ***
publictransport:day.Sunday                 -0.918791    0.086046  -10.68  < 2e-16 ***
cardriver:intrazonal.TRUE                  -1.102420    0.038678  -28.50  < 2e-16 ***
carpassenger:intrazonal.TRUE               -1.162939    0.048553  -23.95  < 2e-16 ***
cycling:intrazonal.TRUE                     0.086721    0.054714    1.58  0.11297    
publictransport:intrazonal.TRUE            -2.976521    0.096687  -30.79  < 2e-16 ***
cardriver:num_activities                    0.327422    0.048596    6.74  1.6e-11 ***
carpassenger:num_activities                 0.386007    0.053736    7.18  6.8e-13 ***
cycling:num_activities                      0.159412    0.079775    2.00  0.04569 *  
publictransport:num_activities              0.328916    0.060196    5.46  4.7e-08 ***
cardriver:containsStrolling.TRUE           -1.632428    0.195428   -8.35  < 2e-16 ***
carpassenger:containsStrolling.TRUE        -1.288067    0.227438   -5.66  1.5e-08 ***
cycling:containsStrolling.TRUE             -0.760503    0.319362   -2.38  0.01725 *  
publictransport:containsStrolling.TRUE     -1.229110    0.253252   -4.85  1.2e-06 ***
cardriver:containsVisit.TRUE                0.209714    0.145144    1.44  0.14849    
carpassenger:containsVisit.TRUE            -0.215613    0.153968   -1.40  0.16140    
cycling:containsVisit.TRUE                  0.026482    0.213098    0.12  0.90110    
publictransport:containsVisit.TRUE         -0.284421    0.177870   -1.60  0.10981    
cardriver:containsPrivateB.TRUE             0.171163    0.114438    1.50  0.13473    
carpassenger:containsPrivateB.TRUE         -0.083759    0.132439   -0.63  0.52710    
cycling:containsPrivateB.TRUE               0.018207    0.186206    0.10  0.92211    
publictransport:containsPrivateB.TRUE      -0.143512    0.143518   -1.00  0.31733    
cardriver:containsService.TRUE             -0.060632    0.196409   -0.31  0.75755    
carpassenger:containsService.TRUE          -0.362936    0.249177   -1.46  0.14524    
cycling:containsService.TRUE               -0.691540    0.288097   -2.40  0.01638 *  
publictransport:containsService.TRUE       -1.812252    0.264755   -6.85  7.6e-12 ***
cardriver:containsLeisure.TRUE              0.546076    0.114244    4.78  1.8e-06 ***
carpassenger:containsLeisure.TRUE           0.918311    0.119534    7.68  1.6e-14 ***
cycling:containsLeisure.TRUE               -0.002067    0.187096   -0.01  0.99119    
publictransport:containsLeisure.TRUE        0.466051    0.136595    3.41  0.00065 ***
cardriver:containsShopping.TRUE             0.289595    0.096138    3.01  0.00259 ** 
carpassenger:containsShopping.TRUE         -0.069918    0.112745   -0.62  0.53516    
cycling:containsShopping.TRUE               0.162232    0.158299    1.02  0.30543    
publictransport:containsShopping.TRUE      -0.045600    0.120383   -0.38  0.70484    
cardriver:containsBusiness.TRUE             0.572238    0.229169    2.50  0.01252 *  
carpassenger:containsBusiness.TRUE          0.150227    0.315713    0.48  0.63419    
cycling:containsBusiness.TRUE               0.386735    0.309671    1.25  0.21172    
publictransport:containsBusiness.TRUE       0.299058    0.264982    1.13  0.25907    
walking:preference                          2.078661    0.060247   34.50  < 2e-16 ***
cardriver:preference                        2.691494    0.052590   51.18  < 2e-16 ***
carpassenger:preference                     3.347180    0.075528   44.32  < 2e-16 ***
cycling:preference                          5.860109    0.102106   57.39  < 2e-16 ***
publictransport:preference                  3.603485    0.080862   44.56  < 2e-16 ***
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -39200
McFadden R^2:  0.522 
Likelihood ratio test : chisq = 85700 (p.value = <2e-16)

		*/


	
	
	public TourModeChoiceParameterTimeCostByEmpSexModeSharesPref() {
	
		walking																				 = 0.0	- 0.3;
		walking_intrazonal														 = 0.0	+ 0.5;
		walking_day_Saturday                           = 0.0	- 0.2;
		walking_day_Sunday                           	 = 0.0	- 0.2;
		
		// curr: v7, next: v7
		
		cardriver                                   = -0.293352 + 0.1;				//    0.093572   -3.14  0.00172 ** 
		carpassenger                                = -3.191544;				//    0.113864  -28.03  < 2e-16 ***
		cycling                                     = -1.559490;				//    0.122480  -12.73  < 2e-16 ***
		publictransport                             = -0.224418 + 0.6;				//    0.101657   -2.21  0.02727 *  
		
		cardriver_intrazonal                        = -1.102420 - 0.6;				//    0.038678  -28.50  < 2e-16 ***
		carpassenger_intrazonal                     = -1.162939;				//    0.048553  -23.95  < 2e-16 ***
		cycling_intrazonal                           = 0.086721 + 0.4;				//    0.054714    1.58  0.11297    
		publictransport_intrazonal                  = -2.976521 - 0.6;				//    0.096687  -30.79  < 2e-16 ***
		
		notavailable                               = -23.278048;				// 1921.050915   -0.01  0.99033    
		time                                        = -0.022911;				//    0.000939  -24.40  < 2e-16 ***
		cost                                        = -0.352451;				//    0.021142  -16.67  < 2e-16 ***
		
		time_sex_female                             = -0.002507;				//    0.001176   -2.13  0.03302 *  
		time_employment_parttime                    = -0.014396;				//    0.002091   -6.89  5.7e-12 ***
		time_employment_marginal                    = -0.000873;				//    0.003044   -0.29  0.77420    
		time_employment_homekeeper                  = -0.009961;				//    0.002622   -3.80  0.00015 ***
		time_employment_unemployed                  = -0.007192;				//    0.004690   -1.53  0.12519    
		time_employment_retired                     = -0.007681;				//    0.001421   -5.40  6.5e-08 ***
		time_employment_pupil                       = -0.011054;				//    0.001755   -6.30  3.0e-10 ***
		time_employment_student                      = 0.009486;				//    0.002352    4.03  5.5e-05 ***
		time_employment_apprentice                  = -0.012318;				//    0.006000   -2.05  0.04009 *  
		time_employment_other                       = -0.049576;				//    0.012783   -3.88  0.00011 ***
		cost_sex_female                              = 0.015969;				//    0.026588    0.60  0.54811    
		cost_employment_parttime                     = 0.051832;				//    0.040569    1.28  0.20138    
		cost_employment_marginal                     = 0.127214;				//    0.083879    1.52  0.12936    
		cost_employment_homekeeper                   = 0.161912;				//    0.065126    2.49  0.01291 *  
		cost_employment_unemployed                   = 0.114360;				//    0.107789    1.06  0.28871    
		cost_employment_retired                      = 0.138181;				//    0.033828    4.08  4.4e-05 ***
		cost_employment_pupil                        = 0.208181;				//    0.046313    4.50  7.0e-06 ***
		cost_employment_student                      = 0.152180;				//    0.061683    2.47  0.01362 *  
		cost_employment_apprentice                   = 0.073765;				//    0.084167    0.88  0.38081    
		cost_employment_other                        = 0.015877;				//    0.140279    0.11  0.90989    
		
		cardriver_sex_female                        = -0.276715 + 0.2;				//    0.047560   -5.82  5.9e-09 ***
		carpassenger_sex_female                      = 0.287235;				//    0.051062    5.63  1.9e-08 ***
		cycling_sex_female                          = -0.297538;				//    0.059615   -4.99  6.0e-07 ***
		publictransport_sex_female                   = 0.027765;				//    0.048528    0.57  0.56723    
		
		cardriver_employment_parttime               = -0.097344 + 0.4;				//    0.069267   -1.41  0.15992    
		carpassenger_employment_parttime             = 0.037770;				//    0.088224    0.43  0.66856    
		cycling_employment_parttime                  = 0.251890;				//    0.092567    2.72  0.00651 ** 
		publictransport_employment_parttime          = 0.305514;				//    0.084696    3.61  0.00031 ***
		
		cardriver_employment_marginal                = 0.299324 + 0.2;				//    0.104185    2.87  0.00407 ** 
		carpassenger_employment_marginal             = 0.493785;				//    0.134097    3.68  0.00023 ***
		cycling_employment_marginal                  = 0.429255;				//    0.146836    2.92  0.00346 ** 
		publictransport_employment_marginal          = 0.319483;				//    0.171380    1.86  0.06230 .  
		
		cardriver_employment_homekeeper              = 0.130630 + 0.1;				//    0.083242    1.57  0.11658    
		carpassenger_employment_homekeeper           = 0.196819;				//    0.105861    1.86  0.06300 .  
		cycling_employment_homekeeper                = 0.427040;				//    0.124246    3.44  0.00059 ***
		publictransport_employment_homekeeper        = 0.600493;				//    0.143699    4.18  2.9e-05 ***
		
		cardriver_employment_unemployed             = -0.015800;				//    0.185194   -0.09  0.93201    
		carpassenger_employment_unemployed           = 0.042417;				//    0.209806    0.20  0.83978    
		cycling_employment_unemployed               = -0.016999;				//    0.269736   -0.06  0.94975    
		publictransport_employment_unemployed        = 0.927599;				//    0.211742    4.38  1.2e-05 ***
		cardriver_employment_retired                = -0.146127;				//    0.090752   -1.61  0.10736    
		carpassenger_employment_retired             = -0.131208;				//    0.114281   -1.15  0.25092    
		cycling_employment_retired                   = 0.063581;				//    0.160778    0.40  0.69250    
		publictransport_employment_retired           = 0.587921;				//    0.126533    4.65  3.4e-06 ***
		cardriver_employment_pupil                   = 0.373876;				//    0.184083    2.03  0.04225 *  
		carpassenger_employment_pupil                = 0.447942;				//    0.195548    2.29  0.02198 *  
		cycling_employment_pupil                     = 0.242443;				//    0.280273    0.87  0.38702    
		publictransport_employment_pupil             = 0.269258;				//    0.196869    1.37  0.17140    
		cardriver_employment_student                 = 0.262528;				//    0.174921    1.50  0.13340    
		carpassenger_employment_student              = 0.210276;				//    0.209189    1.01  0.31480    
		cycling_employment_student                   = 0.503826;				//    0.259424    1.94  0.05213 .  
		publictransport_employment_student           = 0.144475;				//    0.183081    0.79  0.43004    
		cardriver_employment_apprentice              = 0.144583;				//    0.256853    0.56  0.57350    
		carpassenger_employment_apprentice           = 0.144972;				//    0.274602    0.53  0.59754    
		cycling_employment_apprentice               = -0.156119;				//    0.387775   -0.40  0.68724    
		publictransport_employment_apprentice        = 0.111171;				//    0.240925    0.46  0.64449    
		cardriver_employment_other                  = -0.641500;				//    0.256443   -2.50  0.01237 *  
		carpassenger_employment_other               = -0.560274;				//    0.320186   -1.75  0.08015 .  
		cycling_employment_other                     = 0.200613;				//    0.298223    0.67  0.50114    
		publictransport_employment_other             = 1.256600;				//    0.290970    4.32  1.6e-05 ***
		
		cardriver_age_06to09                         = 0.122900;				// 6339.570595    0.00  0.99998    
		carpassenger_age_06to09                      = 0.842096;				//    0.202747    4.15  3.3e-05 ***
		cycling_age_06to09                          = -0.143884;				//    0.298852   -0.48  0.63019    
		publictransport_age_06to09                  = -0.358173;				//    0.228541   -1.57  0.11706    
		cardriver_age_10to17                        = -0.145098;				//    0.315426   -0.46  0.64551    
		carpassenger_age_10to17                      = 0.681633;				//    0.196351    3.47  0.00052 ***
		cycling_age_10to17                           = 0.195020;				//    0.281967    0.69  0.48916    
		publictransport_age_10to17                   = 0.304274;				//    0.199498    1.53  0.12721    
		cardriver_age_18to25                         = 0.213789;				//    0.132155    1.62  0.10572    
		carpassenger_age_18to25                      = 0.564918;				//    0.154685    3.65  0.00026 ***
		cycling_age_18to25                           = 0.069029;				//    0.224552    0.31  0.75853    
		publictransport_age_18to25                   = 0.168721;				//    0.158160    1.07  0.28607    
		cardriver_age_26to35                        = -0.198738;				//    0.065587   -3.03  0.00244 ** 
		carpassenger_age_26to35                     = -0.126698;				//    0.089348   -1.42  0.15618    
		cycling_age_26to35                          = -0.238422;				//    0.115646   -2.06  0.03924 *  
		publictransport_age_26to35                  = -0.035066;				//    0.091657   -0.38  0.70204    
		cardriver_age_51to60                         = 0.131853;				//    0.049085    2.69  0.00723 ** 
		carpassenger_age_51to60                      = 0.219922;				//    0.066364    3.31  0.00092 ***
		cycling_age_51to60                          = -0.136419;				//    0.083346   -1.64  0.10167    
		publictransport_age_51to60                   = 0.111916;				//    0.074298    1.51  0.13199    
		cardriver_age_61to70                         = 0.130076;				//    0.080283    1.62  0.10518    
		carpassenger_age_61to70                      = 0.258182;				//    0.100342    2.57  0.01008 *  
		cycling_age_61to70                          = -0.064123;				//    0.143148   -0.45  0.65419    
		publictransport_age_61to70                   = 0.238239;				//    0.113967    2.09  0.03658 *  
		cardriver_age_71plus                         = 0.223621;				//    0.094476    2.37  0.01794 *  
		carpassenger_age_71plus                      = 0.311364;				//    0.116981    2.66  0.00778 ** 
		cycling_age_71plus                           = 0.158934;				//    0.175875    0.90  0.36617    
		publictransport_age_71plus                   = 0.367595;				//    0.133121    2.76  0.00576 ** 
		
		cardriver_purpose_business                   = 0.291366;				//    0.158180    1.84  0.06548 .  
		carpassenger_purpose_business                = 0.654147;				//    0.225092    2.91  0.00366 ** 
		cycling_purpose_business                    = -0.485294;				//    0.253456   -1.91  0.05553 .  
		publictransport_purpose_business            = -0.426088;				//    0.211617   -2.01  0.04406 *  
		cardriver_purpose_education                 = -1.429168;				//    0.129102  -11.07  < 2e-16 ***
		carpassenger_purpose_education              = -0.462883;				//    0.107808   -4.29  1.8e-05 ***
		cycling_purpose_education                   = -0.889437;				//    0.120152   -7.40  1.3e-13 ***
		publictransport_purpose_education           = -0.465980;				//    0.104574   -4.46  8.4e-06 ***
		cardriver_purpose_service                    = 0.524307;				//    0.073148    7.17  7.6e-13 ***
		carpassenger_purpose_service                 = 0.813857;				//    0.110163    7.39  1.5e-13 ***
		cycling_purpose_service                     = -1.116147;				//    0.128890   -8.66  < 2e-16 ***
		publictransport_purpose_service             = -2.126263;				//    0.132537  -16.04  < 2e-16 ***
		cardriver_purpose_private_business          = -0.253191;				//    0.070824   -3.57  0.00035 ***
		carpassenger_purpose_private_business        = 0.876897;				//    0.099107    8.85  < 2e-16 ***
		cycling_purpose_private_business            = -0.872929;				//    0.114987   -7.59  3.2e-14 ***
		publictransport_purpose_private_business    = -1.464461;				//    0.095946  -15.26  < 2e-16 ***
		cardriver_purpose_visit                     = -0.509382;				//    0.080229   -6.35  2.2e-10 ***
		carpassenger_purpose_visit                   = 0.770071;				//    0.103681    7.43  1.1e-13 ***
		cycling_purpose_visit                       = -1.256511;				//    0.134705   -9.33  < 2e-16 ***
		publictransport_purpose_visit               = -1.918849;				//    0.115814  -16.57  < 2e-16 ***
		cardriver_purpose_shopping_grocery          = -0.175323;				//    0.067595   -2.59  0.00949 ** 
		carpassenger_purpose_shopping_grocery        = 0.825168;				//    0.097363    8.48  < 2e-16 ***
		cycling_purpose_shopping_grocery            = -0.496997;				//    0.103851   -4.79  1.7e-06 ***
		publictransport_purpose_shopping_grocery    = -2.538753;				//    0.104744  -24.24  < 2e-16 ***
		cardriver_purpose_shopping_other             = 0.068975;				//    0.092528    0.75  0.45600    
		carpassenger_purpose_shopping_other          = 1.490224;				//    0.115434   12.91  < 2e-16 ***
		cycling_purpose_shopping_other              = -0.684494;				//    0.155808   -4.39  1.1e-05 ***
		publictransport_purpose_shopping_other      = -0.901659;				//    0.116280   -7.75  8.9e-15 ***
		cardriver_purpose_leisure_indoors           = -1.103243;				//    0.086935  -12.69  < 2e-16 ***
		carpassenger_purpose_leisure_indoors         = 1.173020;				//    0.106396   11.03  < 2e-16 ***
		cycling_purpose_leisure_indoors             = -2.073561;				//    0.172954  -11.99  < 2e-16 ***
		publictransport_purpose_leisure_indoors     = -1.105808;				//    0.104382  -10.59  < 2e-16 ***
		cardriver_purpose_leisure_outdoors           = 0.215756;				//    0.080482    2.68  0.00734 ** 
		carpassenger_purpose_leisure_outdoors        = 1.633896;				//    0.101729   16.06  < 2e-16 ***
		cycling_purpose_leisure_outdoors            = -0.435285;				//    0.118905   -3.66  0.00025 ***
		publictransport_purpose_leisure_outdoors    = -1.570656;				//    0.110682  -14.19  < 2e-16 ***
		cardriver_purpose_leisure_other             = -0.507325;				//    0.078297   -6.48  9.2e-11 ***
		carpassenger_purpose_leisure_other           = 0.967248;				//    0.101037    9.57  < 2e-16 ***
		cycling_purpose_leisure_other               = -1.049754;				//    0.122199   -8.59  < 2e-16 ***
		publictransport_purpose_leisure_other       = -1.156962;				//    0.099453  -11.63  < 2e-16 ***
		
		cardriver_purpose_leisure_walk              = -2.533394	+ 1.5;				//    0.087442  -28.97  < 2e-16 ***
		carpassenger_purpose_leisure_walk           = -0.842457	+ 1.5;				//    0.119069   -7.08  1.5e-12 ***
		cycling_purpose_leisure_walk                = -1.297138	+ 1.0;				//    0.112578  -11.52  < 2e-16 ***
		publictransport_purpose_leisure_walk        = -2.529339	+ 1.0;				//    0.139651  -18.11  < 2e-16 ***
		
		cardriver_purpose_other                     = -0.339099;				//    0.485939   -0.70  0.48529    
		carpassenger_purpose_other                   = 0.930881;				//    0.393539    2.37  0.01801 *  
		cycling_purpose_other                       = -2.612580;				//    1.173522   -2.23  0.02600 *  
		publictransport_purpose_other               = -0.890288;				//    0.589479   -1.51  0.13097    
		
		cardriver_day_Saturday                      = -0.036950;				//    0.047085   -0.78  0.43259    
		carpassenger_day_Saturday                    = 0.640869;				//    0.053048   12.08  < 2e-16 ***
		cycling_day_Saturday                        = -0.223112;				//    0.081306   -2.74  0.00607 ** 
		publictransport_day_Saturday                = -0.181994	+ 0.1;				//    0.069720   -2.61  0.00905 ** 
		
		cardriver_day_Sunday                        = -0.586064;				//    0.056882  -10.30  < 2e-16 ***
		carpassenger_day_Sunday                      = 0.228197;				//    0.061823    3.69  0.00022 ***
		cycling_day_Sunday                          = -0.760191;				//    0.099135   -7.67  1.8e-14 ***
		publictransport_day_Sunday                  = -0.918791	+ 0.1;				//    0.086046  -10.68  < 2e-16 ***
		
		cardriver_num_activities                     = 0.327422;				//    0.048596    6.74  1.6e-11 ***
		carpassenger_num_activities                  = 0.386007;				//    0.053736    7.18  6.8e-13 ***
		cycling_num_activities                       = 0.159412;				//    0.079775    2.00  0.04569 *  
		publictransport_num_activities               = 0.328916;				//    0.060196    5.46  4.7e-08 ***
		cardriver_containsStrolling                 = -1.632428;				//    0.195428   -8.35  < 2e-16 ***
		carpassenger_containsStrolling              = -1.288067;				//    0.227438   -5.66  1.5e-08 ***
		cycling_containsStrolling                   = -0.760503;				//    0.319362   -2.38  0.01725 *  
		publictransport_containsStrolling           = -1.229110;				//    0.253252   -4.85  1.2e-06 ***
		cardriver_containsVisit                      = 0.209714;				//    0.145144    1.44  0.14849    
		carpassenger_containsVisit                  = -0.215613;				//    0.153968   -1.40  0.16140    
		cycling_containsVisit                        = 0.026482;				//    0.213098    0.12  0.90110    
		publictransport_containsVisit               = -0.284421;				//    0.177870   -1.60  0.10981    
		cardriver_containsPrivateB                   = 0.171163;				//    0.114438    1.50  0.13473    
		carpassenger_containsPrivateB               = -0.083759;				//    0.132439   -0.63  0.52710    
		cycling_containsPrivateB                     = 0.018207;				//    0.186206    0.10  0.92211    
		publictransport_containsPrivateB            = -0.143512;				//    0.143518   -1.00  0.31733    
		cardriver_containsService                   = -0.060632;				//    0.196409   -0.31  0.75755    
		carpassenger_containsService                = -0.362936;				//    0.249177   -1.46  0.14524    
		cycling_containsService                     = -0.691540;				//    0.288097   -2.40  0.01638 *  
		publictransport_containsService             = -1.812252;				//    0.264755   -6.85  7.6e-12 ***
		cardriver_containsLeisure                    = 0.546076;				//    0.114244    4.78  1.8e-06 ***
		carpassenger_containsLeisure                 = 0.918311;				//    0.119534    7.68  1.6e-14 ***
		cycling_containsLeisure                     = -0.002067;				//    0.187096   -0.01  0.99119    
		publictransport_containsLeisure              = 0.466051;				//    0.136595    3.41  0.00065 ***
		cardriver_containsShopping                   = 0.289595;				//    0.096138    3.01  0.00259 ** 
		carpassenger_containsShopping               = -0.069918;				//    0.112745   -0.62  0.53516    
		cycling_containsShopping                     = 0.162232;				//    0.158299    1.02  0.30543    
		publictransport_containsShopping            = -0.045600;				//    0.120383   -0.38  0.70484    
		cardriver_containsBusiness                   = 0.572238;				//    0.229169    2.50  0.01252 *  
		carpassenger_containsBusiness                = 0.150227;				//    0.315713    0.48  0.63419    
		cycling_containsBusiness                     = 0.386735;				//    0.309671    1.25  0.21172    
		publictransport_containsBusiness             = 0.299058;				//    0.264982    1.13  0.25907    
		
		walking_preference                           = 2.078661;				//    0.060247   34.50  < 2e-16 ***
		cardriver_preference                         = 2.691494;				//    0.052590   51.18  < 2e-16 ***
		carpassenger_preference                      = 3.347180;				//    0.075528   44.32  < 2e-16 ***
		cycling_preference                           = 5.860109;				//    0.102106   57.39  < 2e-16 ***
		publictransport_preference                   = 3.603485;				//    0.080862   44.56  < 2e-16 ***

	
		

		init();
	}

	protected void init() {
		
		super.init();

		this.parameterWalk.put("SHARES_AS_PREFERENCE", walking_preference);
		this.parameterBike.put("SHARES_AS_PREFERENCE", cycling_preference);
		this.parameterCar.put("SHARES_AS_PREFERENCE", cardriver_preference);
		this.parameterPassenger.put("SHARES_AS_PREFERENCE", carpassenger_preference);
		this.parameterPt.put("SHARES_AS_PREFERENCE", publictransport_preference);
	}



}
