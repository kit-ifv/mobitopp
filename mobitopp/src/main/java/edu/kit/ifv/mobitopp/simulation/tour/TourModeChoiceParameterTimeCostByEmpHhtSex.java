package edu.kit.ifv.mobitopp.simulation.tour;



public class TourModeChoiceParameterTimeCostByEmpHhtSex extends TourModeChoiceParameterBase 
	implements TourModeChoiceParameter
{

	


	/*
	
	Call:
mlogit(formula = mode ~ notavailable + time + time:sex. + time:employment. + 
    time:hhtype. + cost + cost:sex. + cost:employment. + cost:hhtype. | 
    employment. + hhtype. + sex. + purpose. + day. + intrazonal. + 
        num_activities + containsStrolling. + containsVisit. + 
        containsPrivateB. + containsService. + containsLeisure. + 
        containsShopping. + containsBusiness., data = trainingdata, 
    reflevel = "walking", method = "nr", print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2835          0.3918          0.1295          0.0552          0.1401 

nr method
24 iterations, 0h:15m:47s 
g'(-H)^-1g = 9.57E-07 
gradient close to zero 

Coefficients :
                                                Estimate  Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                           0.746566    0.054427   13.72  < 2e-16 ***
carpassenger:(intercept)                       -3.432784    0.073537  -46.68  < 2e-16 ***
cycling:(intercept)                            -1.550086    0.072089  -21.50  < 2e-16 ***
publictransport:(intercept)                     0.412119    0.058588    7.03  2.0e-12 ***
notavailable                                  -25.175315 1752.524123   -0.01  0.98854    
time                                           -0.029887    0.000910  -32.86  < 2e-16 ***
cost                                           -0.461250    0.015481  -29.80  < 2e-16 ***
time:sex.female                                -0.005807    0.000727   -7.99  1.3e-15 ***
time:employment.parttime                       -0.000651    0.001233   -0.53  0.59783    
time:employment.marginal                        0.004604    0.002110    2.18  0.02914 *  
time:employment.homekeeper                      0.003531    0.001578    2.24  0.02527 *  
time:employment.unemployed                      0.001626    0.002992    0.54  0.58670    
time:employment.retired                         0.006091    0.000999    6.10  1.1e-09 ***
time:employment.student_primary                -0.011704    0.002319   -5.05  4.5e-07 ***
time:employment.student_secondary               0.000515    0.001230    0.42  0.67539    
time:employment.student_tertiary                0.014083    0.001404   10.03  < 2e-16 ***
time:employment.apprentice                      0.007793    0.002225    3.50  0.00046 ***
time:employment.other                          -0.008860    0.003259   -2.72  0.00656 ** 
time:hhtype.single                              0.002691    0.001163    2.31  0.02068 *  
time:hhtype.kids_0to7                           0.008908    0.001097    8.12  4.4e-16 ***
time:hhtype.kids_8to12                         -0.001961    0.001362   -1.44  0.15000    
time:hhtype.kids_13to18                         0.002943    0.001281    2.30  0.02162 *  
time:hhtype.multiadult                          0.005382    0.001080    4.98  6.2e-07 ***
cost:sex.female                                -0.103257    0.014950   -6.91  5.0e-12 ***
cost:employment.parttime                        0.178898    0.022852    7.83  4.9e-15 ***
cost:employment.marginal                        0.398844    0.041662    9.57  < 2e-16 ***
cost:employment.homekeeper                      0.298185    0.034709    8.59  < 2e-16 ***
cost:employment.unemployed                      0.202830    0.063387    3.20  0.00137 ** 
cost:employment.retired                         0.160482    0.019652    8.17  2.2e-16 ***
cost:employment.student_primary                -0.271329    0.112086   -2.42  0.01549 *  
cost:employment.student_secondary              -0.296621    0.033677   -8.81  < 2e-16 ***
cost:employment.student_tertiary                0.210147    0.032289    6.51  7.6e-11 ***
cost:employment.apprentice                      0.108953    0.039867    2.73  0.00628 ** 
cost:employment.other                           0.232159    0.062084    3.74  0.00018 ***
cost:hhtype.single                             -0.108527    0.026517   -4.09  4.3e-05 ***
cost:hhtype.kids_0to7                          -0.046760    0.022385   -2.09  0.03672 *  
cost:hhtype.kids_8to12                         -0.117957    0.023799   -4.96  7.2e-07 ***
cost:hhtype.kids_13to18                        -0.095623    0.022763   -4.20  2.7e-05 ***
cost:hhtype.multiadult                         -0.013615    0.019809   -0.69  0.49187    
cardriver:employment.parttime                   0.031589    0.044301    0.71  0.47582    
carpassenger:employment.parttime                0.203763    0.058681    3.47  0.00052 ***
cycling:employment.parttime                     0.344358    0.056103    6.14  8.4e-10 ***
publictransport:employment.parttime            -0.231932    0.051522   -4.50  6.7e-06 ***
cardriver:employment.marginal                   0.080135    0.066113    1.21  0.22548    
carpassenger:employment.marginal                0.554558    0.086084    6.44  1.2e-10 ***
cycling:employment.marginal                     0.599193    0.083822    7.15  8.8e-13 ***
publictransport:employment.marginal            -0.621374    0.101796   -6.10  1.0e-09 ***
cardriver:employment.homekeeper                -0.025189    0.052498   -0.48  0.63137    
carpassenger:employment.homekeeper              0.295327    0.067739    4.36  1.3e-05 ***
cycling:employment.homekeeper                   0.113290    0.077644    1.46  0.14454    
publictransport:employment.homekeeper          -0.564523    0.085412   -6.61  3.9e-11 ***
cardriver:employment.unemployed                -0.319311    0.118009   -2.71  0.00681 ** 
carpassenger:employment.unemployed              0.404508    0.137403    2.94  0.00324 ** 
cycling:employment.unemployed                   0.072445    0.161698    0.45  0.65413    
publictransport:employment.unemployed           0.163296    0.133242    1.23  0.22036    
cardriver:employment.retired                   -0.064827    0.039846   -1.63  0.10375    
carpassenger:employment.retired                 0.459107    0.052896    8.68  < 2e-16 ***
cycling:employment.retired                     -0.033783    0.060178   -0.56  0.57453    
publictransport:employment.retired              0.058473    0.049725    1.18  0.23962    
cardriver:employment.student_primary           -0.647024 6408.585244    0.00  0.99992    
carpassenger:employment.student_primary         1.451257    0.074446   19.49  < 2e-16 ***
cycling:employment.student_primary             -1.023226    0.090785  -11.27  < 2e-16 ***
publictransport:employment.student_primary     -0.904654    0.133284   -6.79  1.1e-11 ***
cardriver:employment.student_secondary          0.120250    0.074801    1.61  0.10792    
carpassenger:employment.student_secondary       1.452696    0.059685   24.34  < 2e-16 ***
cycling:employment.student_secondary            0.693402    0.058809   11.79  < 2e-16 ***
publictransport:employment.student_secondary    0.634339    0.057903   10.96  < 2e-16 ***
cardriver:employment.student_tertiary           0.194193    0.099295    1.96  0.05050 .  
carpassenger:employment.student_tertiary        0.710162    0.118591    5.99  2.1e-09 ***
cycling:employment.student_tertiary             0.302040    0.133968    2.25  0.02416 *  
publictransport:employment.student_tertiary     0.498285    0.095476    5.22  1.8e-07 ***
cardriver:employment.apprentice                 0.689115    0.123748    5.57  2.6e-08 ***
carpassenger:employment.apprentice              1.281056    0.129567    9.89  < 2e-16 ***
cycling:employment.apprentice                   0.035474    0.152126    0.23  0.81561    
publictransport:employment.apprentice           0.719367    0.107450    6.69  2.2e-11 ***
cardriver:employment.other                     -0.433732    0.103358   -4.20  2.7e-05 ***
carpassenger:employment.other                   0.524561    0.120009    4.37  1.2e-05 ***
cycling:employment.other                       -0.857291    0.164109   -5.22  1.8e-07 ***
publictransport:employment.other               -0.284896    0.126128   -2.26  0.02390 *  
cardriver:hhtype.single                         0.265684    0.050761    5.23  1.7e-07 ***
carpassenger:hhtype.single                     -1.885233    0.079237  -23.79  < 2e-16 ***
cycling:hhtype.single                           0.034710    0.071649    0.48  0.62807    
publictransport:hhtype.single                   0.120077    0.050031    2.40  0.01639 *  
cardriver:hhtype.kids_0to7                      0.112488    0.044359    2.54  0.01122 *  
carpassenger:hhtype.kids_0to7                  -0.350627    0.056018   -6.26  3.9e-10 ***
cycling:hhtype.kids_0to7                        0.314669    0.057658    5.46  4.8e-08 ***
publictransport:hhtype.kids_0to7               -0.075405    0.051665   -1.46  0.14443    
cardriver:hhtype.kids_8to12                     0.507019    0.047365   10.70  < 2e-16 ***
carpassenger:hhtype.kids_8to12                 -0.246969    0.056969   -4.34  1.5e-05 ***
cycling:hhtype.kids_8to12                       0.431619    0.057567    7.50  6.5e-14 ***
publictransport:hhtype.kids_8to12               0.082508    0.051886    1.59  0.11180    
cardriver:hhtype.kids_13to18                    0.631661    0.047305   13.35  < 2e-16 ***
carpassenger:hhtype.kids_13to18                -0.074193    0.057882   -1.28  0.19991    
cycling:hhtype.kids_13to18                      0.353088    0.058778    6.01  1.9e-09 ***
publictransport:hhtype.kids_13to18              0.239273    0.051533    4.64  3.4e-06 ***
cardriver:hhtype.multiadult                     0.453400    0.041544   10.91  < 2e-16 ***
carpassenger:hhtype.multiadult                  0.093475    0.052309    1.79  0.07394 .  
cycling:hhtype.multiadult                       0.206083    0.058028    3.55  0.00038 ***
publictransport:hhtype.multiadult               0.012141    0.048218    0.25  0.80120    
cardriver:sex.female                           -0.703300    0.029413  -23.91  < 2e-16 ***
carpassenger:sex.female                         0.533726    0.032219   16.57  < 2e-16 ***
cycling:sex.female                             -0.598913    0.034918  -17.15  < 2e-16 ***
publictransport:sex.female                      0.043465    0.029437    1.48  0.13979    
cardriver:purpose.business                      0.573061    0.104185    5.50  3.8e-08 ***
carpassenger:purpose.business                   0.431261    0.163987    2.63  0.00854 ** 
cycling:purpose.business                       -0.322728    0.160634   -2.01  0.04453 *  
publictransport:purpose.business               -0.241188    0.133466   -1.81  0.07074 .  
cardriver:purpose.education                    -1.031462    0.076885  -13.42  < 2e-16 ***
carpassenger:purpose.education                  0.028056    0.069981    0.40  0.68848    
cycling:purpose.education                      -0.579789    0.068476   -8.47  < 2e-16 ***
publictransport:purpose.education              -0.285908    0.062162   -4.60  4.2e-06 ***
cardriver:purpose.service                       0.568155    0.046561   12.20  < 2e-16 ***
carpassenger:purpose.service                    0.823861    0.072976   11.29  < 2e-16 ***
cycling:purpose.service                        -0.943936    0.077567  -12.17  < 2e-16 ***
publictransport:purpose.service                -1.678536    0.080610  -20.82  < 2e-16 ***
cardriver:purpose.private_business             -0.122658    0.044345   -2.77  0.00568 ** 
carpassenger:purpose.private_business           1.256845    0.064431   19.51  < 2e-16 ***
cycling:purpose.private_business               -0.669710    0.068643   -9.76  < 2e-16 ***
publictransport:purpose.private_business       -1.049605    0.058186  -18.04  < 2e-16 ***
cardriver:purpose.visit                        -0.160583    0.050325   -3.19  0.00142 ** 
carpassenger:purpose.visit                      1.207709    0.067386   17.92  < 2e-16 ***
cycling:purpose.visit                          -0.820965    0.077500  -10.59  < 2e-16 ***
publictransport:purpose.visit                  -1.229343    0.067342  -18.26  < 2e-16 ***
cardriver:purpose.shopping_grocery             -0.142683    0.042256   -3.38  0.00073 ***
carpassenger:purpose.shopping_grocery           1.114884    0.063875   17.45  < 2e-16 ***
cycling:purpose.shopping_grocery               -0.363446    0.062179   -5.85  5.1e-09 ***
publictransport:purpose.shopping_grocery       -1.730322    0.062265  -27.79  < 2e-16 ***
cardriver:purpose.shopping_other                0.041736    0.057945    0.72  0.47136    
carpassenger:purpose.shopping_other             1.590658    0.075126   21.17  < 2e-16 ***
cycling:purpose.shopping_other                 -0.528039    0.091950   -5.74  9.3e-09 ***
publictransport:purpose.shopping_other         -0.664884    0.071284   -9.33  < 2e-16 ***
cardriver:purpose.leisure_indoors              -0.872912    0.055220  -15.81  < 2e-16 ***
carpassenger:purpose.leisure_indoors            1.423397    0.070275   20.25  < 2e-16 ***
cycling:purpose.leisure_indoors                -1.467781    0.104730  -14.01  < 2e-16 ***
publictransport:purpose.leisure_indoors        -0.689546    0.063952  -10.78  < 2e-16 ***
cardriver:purpose.leisure_outdoors              0.343707    0.050833    6.76  1.4e-11 ***
carpassenger:purpose.leisure_outdoors           1.861680    0.066726   27.90  < 2e-16 ***
cycling:purpose.leisure_outdoors               -0.299683    0.071575   -4.19  2.8e-05 ***
publictransport:purpose.leisure_outdoors       -1.003361    0.067908  -14.78  < 2e-16 ***
cardriver:purpose.leisure_other                -0.406262    0.048953   -8.30  < 2e-16 ***
carpassenger:purpose.leisure_other              1.224429    0.066024   18.55  < 2e-16 ***
cycling:purpose.leisure_other                  -0.774708    0.071807  -10.79  < 2e-16 ***
publictransport:purpose.leisure_other          -0.722637    0.059852  -12.07  < 2e-16 ***
cardriver:purpose.leisure_walk                 -2.330109    0.054229  -42.97  < 2e-16 ***
carpassenger:purpose.leisure_walk              -0.537469    0.077564   -6.93  4.2e-12 ***
cycling:purpose.leisure_walk                   -1.295886    0.066975  -19.35  < 2e-16 ***
publictransport:purpose.leisure_walk           -2.187571    0.085933  -25.46  < 2e-16 ***
cardriver:purpose.other                         0.548270    0.280262    1.96  0.05043 .  
carpassenger:purpose.other                      2.019820    0.266604    7.58  3.6e-14 ***
cycling:purpose.other                          -0.954924    0.487393   -1.96  0.05008 .  
publictransport:purpose.other                   0.014128    0.311795    0.05  0.96386    
cardriver:day.Saturday                          0.006739    0.030198    0.22  0.82340    
carpassenger:day.Saturday                       0.496944    0.034673   14.33  < 2e-16 ***
cycling:day.Saturday                           -0.149083    0.048573   -3.07  0.00215 ** 
publictransport:day.Saturday                   -0.064803    0.042560   -1.52  0.12786    
cardriver:day.Sunday                           -0.422792    0.036611  -11.55  < 2e-16 ***
carpassenger:day.Sunday                         0.238015    0.040331    5.90  3.6e-09 ***
cycling:day.Sunday                             -0.434115    0.058186   -7.46  8.6e-14 ***
publictransport:day.Sunday                     -0.666540    0.053167  -12.54  < 2e-16 ***
cardriver:intrazonal.TRUE                      -1.237140    0.024157  -51.21  < 2e-16 ***
carpassenger:intrazonal.TRUE                   -1.008293    0.030866  -32.67  < 2e-16 ***
cycling:intrazonal.TRUE                         0.022051    0.031735    0.69  0.48715    
publictransport:intrazonal.TRUE                -3.424834    0.067848  -50.48  < 2e-16 ***
cardriver:num_activities                        0.228263    0.028786    7.93  2.2e-15 ***
carpassenger:num_activities                     0.190771    0.032975    5.79  7.2e-09 ***
cycling:num_activities                          0.158815    0.043455    3.65  0.00026 ***
publictransport:num_activities                  0.201101    0.034238    5.87  4.3e-09 ***
cardriver:containsStrolling.TRUE               -1.136732    0.131544   -8.64  < 2e-16 ***
carpassenger:containsStrolling.TRUE            -0.929561    0.161107   -5.77  7.9e-09 ***
cycling:containsStrolling.TRUE                 -0.651879    0.208345   -3.13  0.00175 ** 
publictransport:containsStrolling.TRUE         -0.596733    0.151427   -3.94  8.1e-05 ***
cardriver:containsVisit.TRUE                    0.487893    0.093734    5.21  1.9e-07 ***
carpassenger:containsVisit.TRUE                 0.245788    0.099430    2.47  0.01344 *  
cycling:containsVisit.TRUE                     -0.214029    0.136815   -1.56  0.11773    
publictransport:containsVisit.TRUE             -0.172028    0.106452   -1.62  0.10609    
cardriver:containsPrivateB.TRUE                 0.225700    0.071820    3.14  0.00167 ** 
carpassenger:containsPrivateB.TRUE              0.241689    0.084662    2.85  0.00431 ** 
cycling:containsPrivateB.TRUE                   0.067760    0.108251    0.63  0.53135    
publictransport:containsPrivateB.TRUE           0.010105    0.086415    0.12  0.90691    
cardriver:containsService.TRUE                  0.153808    0.124831    1.23  0.21790    
carpassenger:containsService.TRUE              -0.114542    0.166726   -0.69  0.49208    
cycling:containsService.TRUE                   -0.813304    0.181186   -4.49  7.2e-06 ***
publictransport:containsService.TRUE           -1.510703    0.163199   -9.26  < 2e-16 ***
cardriver:containsLeisure.TRUE                  0.490477    0.071467    6.86  6.7e-12 ***
carpassenger:containsLeisure.TRUE               1.062807    0.076918   13.82  < 2e-16 ***
cycling:containsLeisure.TRUE                    0.130598    0.107010    1.22  0.22230    
publictransport:containsLeisure.TRUE            0.643439    0.081589    7.89  3.1e-15 ***
cardriver:containsShopping.TRUE                 0.259692    0.059301    4.38  1.2e-05 ***
carpassenger:containsShopping.TRUE              0.116689    0.072100    1.62  0.10557    
cycling:containsShopping.TRUE                   0.095046    0.092068    1.03  0.30191    
publictransport:containsShopping.TRUE           0.130346    0.071300    1.83  0.06753 .  
cardriver:containsBusiness.TRUE                 0.364819    0.135134    2.70  0.00694 ** 
carpassenger:containsBusiness.TRUE             -0.240103    0.217399   -1.10  0.26940    
cycling:containsBusiness.TRUE                   0.004306    0.183571    0.02  0.98129    
publictransport:containsBusiness.TRUE           0.143649    0.154292    0.93  0.35184    
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -102000
McFadden R^2:  0.384 
Likelihood ratio test : chisq = 128000 (p.value = <2e-16)
> 


		*/
	
	
	public TourModeChoiceParameterTimeCostByEmpHhtSex() {
	
	
		
		////
		
		cardriver                                       = 0.746566;
		carpassenger                                   = -3.432784;
		cycling                                        = -1.550086;
		publictransport                                 = 0.412119;
		
		notavailable                                   = -25.175315;
		
		time                                            = -0.029887;
		cost                                            = -0.461250;
		
		time_sex_female                                 = -0.005807;
		time_employment_parttime                        = -0.000651;
		time_employment_marginal                         = 0.004604;
		time_employment_homekeeper                       = 0.003531;
		time_employment_unemployed                       = 0.001626;
		time_employment_retired                          = 0.006091;
		time_employment_pupil                 = -0.011704;
//		time_employment_student_secondary                = 0.000515;
		time_employment_student                 = 0.014083;
		time_employment_apprentice                       = 0.007793;
		time_employment_other                           = -0.008860;
		time_hhtype_single                               = 0.002691;
		time_hhtype_kids_0to7                            = 0.008908;
		time_hhtype_kids_8to12                          = -0.001961;
		time_hhtype_kids_13to18                          = 0.002943;
		time_hhtype_multiadult                           = 0.005382;
		
		cost_sex_female                                 = -0.103257;
		cost_employment_parttime                         = 0.178898;
		cost_employment_marginal                         = 0.398844;
		cost_employment_homekeeper                       = 0.298185;
		cost_employment_unemployed                       = 0.202830;
		cost_employment_retired                          = 0.160482;
		cost_employment_pupil                 = -0.271329;
//		cost_employment_student_secondary               = -0.296621;
		cost_employment_student                 = 0.210147;
		cost_employment_apprentice                       = 0.108953;
		cost_employment_other                            = 0.232159;
		cost_hhtype_single                              = -0.108527;
		cost_hhtype_kids_0to7                           = -0.046760;
		cost_hhtype_kids_8to12                          = -0.117957;
		cost_hhtype_kids_13to18                         = -0.095623;
		cost_hhtype_multiadult                          = -0.013615;
		
		cardriver_employment_parttime                    = 0.031589;
		carpassenger_employment_parttime                 = 0.203763;
		cycling_employment_parttime                      = 0.344358;
		publictransport_employment_parttime             = -0.231932;
		cardriver_employment_marginal                    = 0.080135;
		carpassenger_employment_marginal                 = 0.554558;
		cycling_employment_marginal                      = 0.599193;
		publictransport_employment_marginal             = -0.621374;
		cardriver_employment_homekeeper                 = -0.025189;
		carpassenger_employment_homekeeper               = 0.295327;
		cycling_employment_homekeeper                    = 0.113290;
		publictransport_employment_homekeeper           = -0.564523;
		cardriver_employment_unemployed                 = -0.319311;
		carpassenger_employment_unemployed               = 0.404508;
		cycling_employment_unemployed                    = 0.072445;
		publictransport_employment_unemployed            = 0.163296;
		cardriver_employment_retired                    = -0.064827;
		carpassenger_employment_retired                  = 0.459107;
		cycling_employment_retired                      = -0.033783;
		publictransport_employment_retired               = 0.058473;
		cardriver_employment_pupil            = -0.647024;
		carpassenger_employment_pupil          = 1.451257;
		cycling_employment_pupil              = -1.023226;
		publictransport_employment_pupil      = -0.904654;
//		cardriver_employment_student_secondary           = 0.120250;
//		carpassenger_employment_student_secondary        = 1.452696;
//		cycling_employment_student_secondary             = 0.693402;
//		publictransport_employment_student_secondary     = 0.634339;
		cardriver_employment_student            = 0.194193;
		carpassenger_employment_student         = 0.710162;
		cycling_employment_student              = 0.302040;
		publictransport_employment_student      = 0.498285;
		cardriver_employment_apprentice                  = 0.689115;
		carpassenger_employment_apprentice               = 1.281056;
		cycling_employment_apprentice                    = 0.035474;
		publictransport_employment_apprentice            = 0.719367;
		cardriver_employment_other                      = -0.433732;
		carpassenger_employment_other                    = 0.524561;
		cycling_employment_other                        = -0.857291;
		publictransport_employment_other                = -0.284896;
		
		cardriver_hhtype_single                          = 0.265684;
		carpassenger_hhtype_single                      = -1.885233;
		cycling_hhtype_single                            = 0.034710;
		publictransport_hhtype_single                    = 0.120077;
		cardriver_hhtype_kids_0to7                       = 0.112488;
		carpassenger_hhtype_kids_0to7                   = -0.350627;
		cycling_hhtype_kids_0to7                         = 0.314669;
		publictransport_hhtype_kids_0to7                = -0.075405;
		cardriver_hhtype_kids_8to12                      = 0.507019;
		carpassenger_hhtype_kids_8to12                  = -0.246969;
		cycling_hhtype_kids_8to12                        = 0.431619;
		publictransport_hhtype_kids_8to12                = 0.082508;
		cardriver_hhtype_kids_13to18                     = 0.631661;
		carpassenger_hhtype_kids_13to18                 = -0.074193;
		cycling_hhtype_kids_13to18                       = 0.353088;
		publictransport_hhtype_kids_13to18               = 0.239273;
		cardriver_hhtype_multiadult                      = 0.453400;
		carpassenger_hhtype_multiadult                   = 0.093475;
		cycling_hhtype_multiadult                        = 0.206083;
		publictransport_hhtype_multiadult                = 0.012141;
		
		cardriver_sex_female                            = -0.703300;
		carpassenger_sex_female                          = 0.533726;
		cycling_sex_female                              = -0.598913;
		publictransport_sex_female                       = 0.043465;
		
		cardriver_purpose_business                       = 0.573061;
		carpassenger_purpose_business                    = 0.431261;
		cycling_purpose_business                        = -0.322728;
		publictransport_purpose_business                = -0.241188;
		cardriver_purpose_education                     = -1.031462;
		carpassenger_purpose_education                   = 0.028056;
		cycling_purpose_education                       = -0.579789;
		publictransport_purpose_education               = -0.285908;
		cardriver_purpose_service                        = 0.568155;
		carpassenger_purpose_service                     = 0.823861;
		cycling_purpose_service                         = -0.943936;
		publictransport_purpose_service                 = -1.678536;
		cardriver_purpose_private_business              = -0.122658;
		carpassenger_purpose_private_business            = 1.256845;
		cycling_purpose_private_business                = -0.669710;
		publictransport_purpose_private_business        = -1.049605;
		cardriver_purpose_visit                         = -0.160583;
		carpassenger_purpose_visit                       = 1.207709;
		cycling_purpose_visit                           = -0.820965;
		publictransport_purpose_visit                   = -1.229343;
		cardriver_purpose_shopping_grocery              = -0.142683;
		carpassenger_purpose_shopping_grocery            = 1.114884;
		cycling_purpose_shopping_grocery                = -0.363446;
		publictransport_purpose_shopping_grocery        = -1.730322;
		cardriver_purpose_shopping_other                 = 0.041736;
		carpassenger_purpose_shopping_other              = 1.590658;
		cycling_purpose_shopping_other                  = -0.528039;
		publictransport_purpose_shopping_other          = -0.664884;
		cardriver_purpose_leisure_indoors               = -0.872912;
		carpassenger_purpose_leisure_indoors             = 1.423397;
		cycling_purpose_leisure_indoors                 = -1.467781;
		publictransport_purpose_leisure_indoors         = -0.689546;
		cardriver_purpose_leisure_outdoors               = 0.343707;
		carpassenger_purpose_leisure_outdoors            = 1.861680;
		cycling_purpose_leisure_outdoors                = -0.299683;
		publictransport_purpose_leisure_outdoors        = -1.003361;
		cardriver_purpose_leisure_other                 = -0.406262;
		carpassenger_purpose_leisure_other               = 1.224429;
		cycling_purpose_leisure_other                   = -0.774708;
		publictransport_purpose_leisure_other           = -0.722637;
		cardriver_purpose_leisure_walk                  = -2.330109;
		carpassenger_purpose_leisure_walk               = -0.537469;
		cycling_purpose_leisure_walk                    = -1.295886;
		publictransport_purpose_leisure_walk            = -2.187571;
		cardriver_purpose_other                          = 0.548270;
		carpassenger_purpose_other                       = 2.019820;
		cycling_purpose_other                           = -0.954924;
		publictransport_purpose_other                    = 0.014128;
		
		cardriver_day_Saturday                           = 0.006739;
		carpassenger_day_Saturday                        = 0.496944;
		cycling_day_Saturday                            = -0.149083;
		publictransport_day_Saturday                    = -0.064803;
		cardriver_day_Sunday                            = -0.422792;
		carpassenger_day_Sunday                          = 0.238015;
		cycling_day_Sunday                              = -0.434115;
		publictransport_day_Sunday                      = -0.666540;
		
		cardriver_intrazonal                            = -1.237140;
		carpassenger_intrazonal                         = -1.008293;
		cycling_intrazonal                               = 0.022051;
		publictransport_intrazonal                      = -3.424834;
		
		cardriver_num_activities                         = 0.228263;
		carpassenger_num_activities                      = 0.190771;
		cycling_num_activities                           = 0.158815;
		publictransport_num_activities                   = 0.201101;
		
		cardriver_containsStrolling                     = -1.136732;
		carpassenger_containsStrolling                  = -0.929561;
		cycling_containsStrolling                       = -0.651879;
		publictransport_containsStrolling               = -0.596733;
		cardriver_containsVisit                          = 0.487893;
		carpassenger_containsVisit                       = 0.245788;
		cycling_containsVisit                           = -0.214029;
		publictransport_containsVisit                   = -0.172028;
		cardriver_containsPrivateB                       = 0.225700;
		carpassenger_containsPrivateB                    = 0.241689;
		cycling_containsPrivateB                         = 0.067760;
		publictransport_containsPrivateB                 = 0.010105;
		cardriver_containsService                        = 0.153808;
		carpassenger_containsService                    = -0.114542;
		cycling_containsService                         = -0.813304;
		publictransport_containsService                 = -1.510703;
		cardriver_containsLeisure                        = 0.490477;
		carpassenger_containsLeisure                     = 1.062807;
		cycling_containsLeisure                          = 0.130598;
		publictransport_containsLeisure                  = 0.643439;
		cardriver_containsShopping                       = 0.259692;
		carpassenger_containsShopping                    = 0.116689;
		cycling_containsShopping                         = 0.095046;
		publictransport_containsShopping                 = 0.130346;
		cardriver_containsBusiness                       = 0.364819;
		carpassenger_containsBusiness                   = -0.240103;
		cycling_containsBusiness                         = 0.004306;
		publictransport_containsBusiness                 = 0.143649;
		
	System.out.println("test");	


		init();
	}



}
