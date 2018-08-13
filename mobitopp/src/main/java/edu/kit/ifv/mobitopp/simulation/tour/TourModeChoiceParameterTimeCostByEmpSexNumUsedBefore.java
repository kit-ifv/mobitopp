package edu.kit.ifv.mobitopp.simulation.tour;


public class TourModeChoiceParameterTimeCostByEmpSexNumUsedBefore 
	extends TourModeChoiceParameterBaseTimeCostByEmpSex 
	implements TourModeChoiceParameter
{

	
	protected Double	walking_numusedbefore = 0.0;
	protected Double	cardriver_numusedbefore;
	protected Double	carpassenger_numusedbefore;
	protected Double	cycling_numusedbefore;
	protected Double	publictransport_numusedbefore;
	
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
24 iterations, 0h:7m:47s 
g'(-H)^-1g = 4.19E-07 
gradient close to zero 

Coefficients :
                                            Estimate  Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                       0.447117    0.080625    5.55  2.9e-08 ***
carpassenger:(intercept)                   -3.771561    0.108276  -34.83  < 2e-16 ***
cycling:(intercept)                        -1.441602    0.107794  -13.37  < 2e-16 ***
publictransport:(intercept)                -0.038711    0.091338   -0.42  0.67170    
notavailable                              -24.506738 2658.508456   -0.01  0.99265    
time                                       -0.028335    0.000973  -29.11  < 2e-16 ***
cost                                       -0.545686    0.018875  -28.91  < 2e-16 ***
time:sex.female                            -0.002313    0.001175   -1.97  0.04902 *  
time:employment.parttime                   -0.013384    0.002108   -6.35  2.2e-10 ***
time:employment.marginal                    0.000168    0.003158    0.05  0.95764    
time:employment.homekeeper                 -0.006639    0.002624   -2.53  0.01141 *  
time:employment.unemployed                 -0.004901    0.004965   -0.99  0.32356    
time:employment.retired                    -0.003580    0.001409   -2.54  0.01108 *  
time:employment.pupil                      -0.009261    0.001758   -5.27  1.4e-07 ***
time:employment.student                     0.011526    0.002470    4.67  3.1e-06 ***
time:employment.apprentice                 -0.009200    0.005563   -1.65  0.09817 .  
time:employment.other                      -0.044578    0.012274   -3.63  0.00028 ***
cost:sex.female                             0.004472    0.024604    0.18  0.85578    
cost:employment.parttime                    0.096833    0.038217    2.53  0.01128 *  
cost:employment.marginal                    0.266500    0.079412    3.36  0.00079 ***
cost:employment.homekeeper                  0.304743    0.062331    4.89  1.0e-06 ***
cost:employment.unemployed                  0.251769    0.101808    2.47  0.01340 *  
cost:employment.retired                     0.150839    0.031221    4.83  1.4e-06 ***
cost:employment.pupil                       0.057724    0.046355    1.25  0.21304    
cost:employment.student                     0.217914    0.055452    3.93  8.5e-05 ***
cost:employment.apprentice                  0.069948    0.074753    0.94  0.34941    
cost:employment.other                       0.087214    0.139069    0.63  0.53058    
cardriver:sex.female                       -0.517692    0.044462  -11.64  < 2e-16 ***
carpassenger:sex.female                     0.403218    0.048851    8.25  2.2e-16 ***
cycling:sex.female                         -0.531069    0.054004   -9.83  < 2e-16 ***
publictransport:sex.female                  0.026576    0.044627    0.60  0.55149    
cardriver:employment.parttime              -0.134581    0.066243   -2.03  0.04219 *  
carpassenger:employment.parttime            0.192483    0.087061    2.21  0.02704 *  
cycling:employment.parttime                 0.224295    0.086812    2.58  0.00978 ** 
publictransport:employment.parttime         0.147577    0.079342    1.86  0.06289 .  
cardriver:employment.marginal               0.136550    0.100658    1.36  0.17491    
carpassenger:employment.marginal            0.659948    0.131355    5.02  5.1e-07 ***
cycling:employment.marginal                 0.507463    0.137325    3.70  0.00022 ***
publictransport:employment.marginal        -0.068402    0.160264   -0.43  0.66952    
cardriver:employment.homekeeper            -0.045645    0.079404   -0.57  0.56540    
carpassenger:employment.homekeeper          0.392988    0.102937    3.82  0.00013 ***
cycling:employment.homekeeper               0.422144    0.116398    3.63  0.00029 ***
publictransport:employment.homekeeper       0.098696    0.137381    0.72  0.47250    
cardriver:employment.unemployed            -0.141944    0.176767   -0.80  0.42197    
carpassenger:employment.unemployed          0.190695    0.202663    0.94  0.34673    
cycling:employment.unemployed               0.058544    0.256860    0.23  0.81971    
publictransport:employment.unemployed       0.537937    0.199623    2.69  0.00704 ** 
cardriver:employment.retired               -0.278013    0.085816   -3.24  0.00120 ** 
carpassenger:employment.retired            -0.056108    0.111393   -0.50  0.61448    
cycling:employment.retired                 -0.156770    0.143860   -1.09  0.27583    
publictransport:employment.retired          0.352281    0.118365    2.98  0.00292 ** 
cardriver:employment.pupil                  0.069790    0.176055    0.40  0.69180    
carpassenger:employment.pupil               0.359959    0.192389    1.87  0.06135 .  
cycling:employment.pupil                    0.326425    0.256922    1.27  0.20390    
publictransport:employment.pupil            0.131469    0.185273    0.71  0.47796    
cardriver:employment.student                0.144702    0.167790    0.86  0.38847    
carpassenger:employment.student             0.343638    0.205132    1.68  0.09389 .  
cycling:employment.student                  0.635060    0.230519    2.75  0.00587 ** 
publictransport:employment.student          0.172553    0.169819    1.02  0.30958    
cardriver:employment.apprentice             0.177228    0.241916    0.73  0.46380    
carpassenger:employment.apprentice          0.243291    0.266519    0.91  0.36132    
cycling:employment.apprentice              -0.342465    0.359420   -0.95  0.34068    
publictransport:employment.apprentice       0.292823    0.222484    1.32  0.18812    
cardriver:employment.other                 -0.818120    0.242803   -3.37  0.00075 ***
carpassenger:employment.other              -0.602879    0.317369   -1.90  0.05748 .  
cycling:employment.other                   -0.041568    0.287470   -0.14  0.88503    
publictransport:employment.other            0.812008    0.283629    2.86  0.00420 ** 
cardriver:age.06to09                       -0.340691 9222.610268    0.00  0.99997    
carpassenger:age.06to09                     1.127556    0.197799    5.70  1.2e-08 ***
cycling:age.06to09                         -0.649201    0.273030   -2.38  0.01742 *  
publictransport:age.06to09                 -0.614117    0.214728   -2.86  0.00424 ** 
cardriver:age.10to17                       -0.411025    0.301830   -1.36  0.17327    
carpassenger:age.10to17                     1.036450    0.191903    5.40  6.6e-08 ***
cycling:age.10to17                          0.323379    0.256823    1.26  0.20798    
publictransport:age.10to17                  0.468166    0.186633    2.51  0.01212 *  
cardriver:age.18to25                        0.409507    0.125639    3.26  0.00112 ** 
carpassenger:age.18to25                     0.733133    0.150032    4.89  1.0e-06 ***
cycling:age.18to25                         -0.060730    0.203638   -0.30  0.76553    
publictransport:age.18to25                  0.431699    0.146755    2.94  0.00326 ** 
cardriver:age.26to35                       -0.136967    0.062031   -2.21  0.02724 *  
carpassenger:age.26to35                    -0.147424    0.087948   -1.68  0.09369 .  
cycling:age.26to35                         -0.353870    0.108330   -3.27  0.00109 ** 
publictransport:age.26to35                  0.058217    0.085337    0.68  0.49511    
cardriver:age.51to60                        0.137530    0.046741    2.94  0.00326 ** 
carpassenger:age.51to60                     0.241737    0.064968    3.72  0.00020 ***
cycling:age.51to60                         -0.122604    0.076793   -1.60  0.11037    
publictransport:age.51to60                  0.106267    0.069094    1.54  0.12405    
cardriver:age.61to70                        0.099298    0.075598    1.31  0.18902    
carpassenger:age.61to70                     0.308667    0.097181    3.18  0.00149 ** 
cycling:age.61to70                          0.015502    0.127427    0.12  0.90317    
publictransport:age.61to70                  0.242769    0.106392    2.28  0.02250 *  
cardriver:age.71plus                        0.239121    0.088690    2.70  0.00701 ** 
carpassenger:age.71plus                     0.466543    0.112165    4.16  3.2e-05 ***
cycling:age.71plus                          0.291059    0.157463    1.85  0.06454 .  
publictransport:age.71plus                  0.450354    0.123852    3.64  0.00028 ***
cardriver:purpose.business                  0.348277    0.150618    2.31  0.02076 *  
carpassenger:purpose.business               0.748353    0.218884    3.42  0.00063 ***
cycling:purpose.business                   -0.369058    0.238241   -1.55  0.12136    
publictransport:purpose.business           -0.251698    0.196110   -1.28  0.19933    
cardriver:purpose.education                -1.256017    0.118363  -10.61  < 2e-16 ***
carpassenger:purpose.education             -0.060324    0.103428   -0.58  0.55973    
cycling:purpose.education                  -0.658196    0.107925   -6.10  1.1e-09 ***
publictransport:purpose.education          -0.350400    0.097107   -3.61  0.00031 ***
cardriver:purpose.service                   0.492787    0.068959    7.15  8.9e-13 ***
carpassenger:purpose.service                0.977048    0.106893    9.14  < 2e-16 ***
cycling:purpose.service                    -1.048308    0.121895   -8.60  < 2e-16 ***
publictransport:purpose.service            -1.984629    0.126318  -15.71  < 2e-16 ***
cardriver:purpose.private_business         -0.275393    0.066219   -4.16  3.2e-05 ***
carpassenger:purpose.private_business       1.146943    0.095448   12.02  < 2e-16 ***
cycling:purpose.private_business           -0.813002    0.106262   -7.65  2.0e-14 ***
publictransport:purpose.private_business   -1.256200    0.088887  -14.13  < 2e-16 ***
cardriver:purpose.visit                    -0.494574    0.075941   -6.51  7.4e-11 ***
carpassenger:purpose.visit                  0.965521    0.100920    9.57  < 2e-16 ***
cycling:purpose.visit                      -1.206957    0.127654   -9.45  < 2e-16 ***
publictransport:purpose.visit              -1.752837    0.109391  -16.02  < 2e-16 ***
cardriver:purpose.shopping_grocery         -0.251249    0.062948   -3.99  6.6e-05 ***
carpassenger:purpose.shopping_grocery       1.093678    0.093920   11.64  < 2e-16 ***
cycling:purpose.shopping_grocery           -0.438362    0.095671   -4.58  4.6e-06 ***
publictransport:purpose.shopping_grocery   -2.155677    0.096247  -22.40  < 2e-16 ***
cardriver:purpose.shopping_other            0.013438    0.087509    0.15  0.87795    
carpassenger:purpose.shopping_other         1.709526    0.111640   15.31  < 2e-16 ***
cycling:purpose.shopping_other             -0.623898    0.144707   -4.31  1.6e-05 ***
publictransport:purpose.shopping_other     -0.757384    0.110267   -6.87  6.5e-12 ***
cardriver:purpose.leisure_indoors          -1.118486    0.083436  -13.41  < 2e-16 ***
carpassenger:purpose.leisure_indoors        1.370797    0.104024   13.18  < 2e-16 ***
cycling:purpose.leisure_indoors            -1.848780    0.165451  -11.17  < 2e-16 ***
publictransport:purpose.leisure_indoors    -0.929085    0.099507   -9.34  < 2e-16 ***
cardriver:purpose.leisure_outdoors          0.181056    0.075925    2.38  0.01709 *  
carpassenger:purpose.leisure_outdoors       1.838675    0.098781   18.61  < 2e-16 ***
cycling:purpose.leisure_outdoors           -0.333494    0.109866   -3.04  0.00240 ** 
publictransport:purpose.leisure_outdoors   -1.329932    0.104303  -12.75  < 2e-16 ***
cardriver:purpose.leisure_other            -0.534154    0.074155   -7.20  5.9e-13 ***
carpassenger:purpose.leisure_other          1.136027    0.098609   11.52  < 2e-16 ***
cycling:purpose.leisure_other              -0.937763    0.114639   -8.18  2.2e-16 ***
publictransport:purpose.leisure_other      -1.015374    0.094103  -10.79  < 2e-16 ***
cardriver:purpose.leisure_walk             -2.568677    0.083592  -30.73  < 2e-16 ***
carpassenger:purpose.leisure_walk          -0.629099    0.117239   -5.37  8.1e-08 ***
cycling:purpose.leisure_walk               -1.252832    0.105072  -11.92  < 2e-16 ***
publictransport:purpose.leisure_walk       -2.330588    0.132214  -17.63  < 2e-16 ***
cardriver:purpose.other                    -0.064979    0.441737   -0.15  0.88305    
carpassenger:purpose.other                  1.572296    0.368316    4.27  2.0e-05 ***
cycling:purpose.other                      -2.040626    1.094952   -1.86  0.06237 .  
publictransport:purpose.other              -0.441282    0.565445   -0.78  0.43515    
cardriver:day.Saturday                     -0.321698    0.048924   -6.58  4.9e-11 ***
carpassenger:day.Saturday                   0.524874    0.054371    9.65  < 2e-16 ***
cycling:day.Saturday                       -0.352697    0.082833   -4.26  2.1e-05 ***
publictransport:day.Saturday               -0.365917    0.072468   -5.05  4.4e-07 ***
cardriver:day.Sunday                       -0.966451    0.060415  -16.00  < 2e-16 ***
carpassenger:day.Sunday                     0.016437    0.064664    0.25  0.79935    
cycling:day.Sunday                         -0.939068    0.104022   -9.03  < 2e-16 ***
publictransport:day.Sunday                 -1.111661    0.090238  -12.32  < 2e-16 ***
cardriver:intrazonal.TRUE                  -1.065002    0.036285  -29.35  < 2e-16 ***
carpassenger:intrazonal.TRUE               -1.013093    0.047102  -21.51  < 2e-16 ***
cycling:intrazonal.TRUE                     0.044656    0.049762    0.90  0.36952    
publictransport:intrazonal.TRUE            -3.041595    0.093618  -32.49  < 2e-16 ***
cardriver:num_activities                    0.289634    0.046023    6.29  3.1e-10 ***
carpassenger:num_activities                 0.368879    0.051668    7.14  9.4e-13 ***
cycling:num_activities                      0.175388    0.072398    2.42  0.01541 *  
publictransport:num_activities              0.296257    0.056200    5.27  1.4e-07 ***
cardriver:containsStrolling.TRUE           -1.658377    0.188435   -8.80  < 2e-16 ***
carpassenger:containsStrolling.TRUE        -1.211814    0.223715   -5.42  6.1e-08 ***
cycling:containsStrolling.TRUE             -0.762360    0.290433   -2.62  0.00867 ** 
publictransport:containsStrolling.TRUE     -1.268804    0.235831   -5.38  7.4e-08 ***
cardriver:containsVisit.TRUE                0.369270    0.138461    2.67  0.00765 ** 
carpassenger:containsVisit.TRUE            -0.135476    0.150282   -0.90  0.36733    
cycling:containsVisit.TRUE                 -0.001815    0.196111   -0.01  0.99262    
publictransport:containsVisit.TRUE         -0.184208    0.165299   -1.11  0.26511    
cardriver:containsPrivateB.TRUE             0.156079    0.107538    1.45  0.14668    
carpassenger:containsPrivateB.TRUE         -0.096198    0.126491   -0.76  0.44695    
cycling:containsPrivateB.TRUE              -0.022592    0.169467   -0.13  0.89394    
publictransport:containsPrivateB.TRUE      -0.114500    0.133032   -0.86  0.38941    
cardriver:containsService.TRUE              0.015256    0.186175    0.08  0.93469    
carpassenger:containsService.TRUE          -0.221204    0.239937   -0.92  0.35657    
cycling:containsService.TRUE               -0.719702    0.269510   -2.67  0.00758 ** 
publictransport:containsService.TRUE       -1.697657    0.250795   -6.77  1.3e-11 ***
cardriver:containsLeisure.TRUE              0.539112    0.109396    4.93  8.3e-07 ***
carpassenger:containsLeisure.TRUE           0.904149    0.115758    7.81  5.8e-15 ***
cycling:containsLeisure.TRUE               -0.042692    0.173111   -0.25  0.80520    
publictransport:containsLeisure.TRUE        0.511610    0.127671    4.01  6.1e-05 ***
cardriver:containsShopping.TRUE             0.218578    0.089848    2.43  0.01498 *  
carpassenger:containsShopping.TRUE         -0.047244    0.107291   -0.44  0.65970    
cycling:containsShopping.TRUE               0.082944    0.144176    0.58  0.56509    
publictransport:containsShopping.TRUE       0.016312    0.110850    0.15  0.88301    
cardriver:containsBusiness.TRUE             0.597812    0.214187    2.79  0.00525 ** 
carpassenger:containsBusiness.TRUE          0.110888    0.305980    0.36  0.71705    
cycling:containsBusiness.TRUE               0.398270    0.278746    1.43  0.15306    
publictransport:containsBusiness.TRUE       0.347964    0.247110    1.41  0.15909    
cardriver:firsttour.TRUE                    0.221979    0.051829    4.28  1.8e-05 ***
carpassenger:firsttour.TRUE                -0.011104    0.064080   -0.17  0.86243    
cycling:firsttour.TRUE                     -0.053900    0.072446   -0.74  0.45687    
publictransport:firsttour.TRUE              0.250610    0.058886    4.26  2.1e-05 ***
walking:usedbefore                          0.221488    0.006079   36.43  < 2e-16 ***
cardriver:usedbefore                        0.241348    0.005477   44.06  < 2e-16 ***
carpassenger:usedbefore                     0.503579    0.012366   40.72  < 2e-16 ***
cycling:usedbefore                          0.549314    0.011628   47.24  < 2e-16 ***
publictransport:usedbefore                  0.404801    0.011437   35.39  < 2e-16 ***
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -44400
McFadden R^2:  0.459 
Likelihood ratio test : chisq = 75400 (p.value = <2e-16)

		*/


	
	
	public TourModeChoiceParameterTimeCostByEmpSexNumUsedBefore() {
	
		walking																				 = 0.0		- 0.2;
		walking_intrazonal														 = 0.0		+ 0.2;
		walking_day_Saturday                           = 0.0		- 0.2;
		walking_day_Sunday                           	 = 0.0		- 0.2;
		

		// curr: v4, next: v4
		
		cardriver                                    = 0.447117	+ 0.1;				//    0.080625    5.55  2.9e-08 ***
		carpassenger                                = -3.771561;				//    0.108276  -34.83  < 2e-16 ***
		cycling                                     = -1.441602;				//    0.107794  -13.37  < 2e-16 ***
		publictransport                             = -0.038711 + 0.6;				//    0.091338   -0.42  0.67170    
		
		cardriver_intrazonal                        = -1.065002 - 0.3;				//    0.036285  -29.35  < 2e-16 ***
		carpassenger_intrazonal                     = -1.013093;				//    0.047102  -21.51  < 2e-16 ***
		cycling_intrazonal                           = 0.044656 + 0.6;				//    0.049762    0.90  0.36952    
		publictransport_intrazonal                  = -3.041595;				//    0.093618  -32.49  < 2e-16 ***
		
		notavailable                               = -24.506738;				// 2658.508456   -0.01  0.99265    
		time                                        = -0.028335;				//    0.000973  -29.11  < 2e-16 ***
		cost                                        = -0.545686;				//    0.018875  -28.91  < 2e-16 ***
		
		time_sex_female                             = -0.002313;				//    0.001175   -1.97  0.04902 *  
		time_employment_parttime                    = -0.013384;				//    0.002108   -6.35  2.2e-10 ***
		time_employment_marginal                     = 0.000168;				//    0.003158    0.05  0.95764    
		time_employment_homekeeper                  = -0.006639;				//    0.002624   -2.53  0.01141 *  
		time_employment_unemployed                  = -0.004901;				//    0.004965   -0.99  0.32356    
		time_employment_retired                     = -0.003580;				//    0.001409   -2.54  0.01108 *  
		time_employment_pupil                       = -0.009261;				//    0.001758   -5.27  1.4e-07 ***
		time_employment_student                      = 0.011526;				//    0.002470    4.67  3.1e-06 ***
		time_employment_apprentice                  = -0.009200;				//    0.005563   -1.65  0.09817 .  
		time_employment_other                       = -0.044578;				//    0.012274   -3.63  0.00028 ***
		cost_sex_female                              = 0.004472;				//    0.024604    0.18  0.85578    
		cost_employment_parttime                     = 0.096833;				//    0.038217    2.53  0.01128 *  
		cost_employment_marginal                     = 0.266500;				//    0.079412    3.36  0.00079 ***
		cost_employment_homekeeper                   = 0.304743;				//    0.062331    4.89  1.0e-06 ***
		cost_employment_unemployed                   = 0.251769;				//    0.101808    2.47  0.01340 *  
		cost_employment_retired                      = 0.150839;				//    0.031221    4.83  1.4e-06 ***
		cost_employment_pupil                        = 0.057724;				//    0.046355    1.25  0.21304    
		cost_employment_student                      = 0.217914;				//    0.055452    3.93  8.5e-05 ***
		cost_employment_apprentice                   = 0.069948;				//    0.074753    0.94  0.34941    
		cost_employment_other                        = 0.087214;				//    0.139069    0.63  0.53058    
		
		cardriver_sex_female                        = -0.517692 + 0.1;				//    0.044462  -11.64  < 2e-16 ***
		carpassenger_sex_female                      = 0.403218;				//    0.048851    8.25  2.2e-16 ***
		cycling_sex_female                          = -0.531069;				//    0.054004   -9.83  < 2e-16 ***
		publictransport_sex_female                   = 0.026576;				//    0.044627    0.60  0.55149    
		
		cardriver_employment_parttime               = -0.134581 + 0.4;				//    0.066243   -2.03  0.04219 *  
		carpassenger_employment_parttime             = 0.192483;				//    0.087061    2.21  0.02704 *  
		cycling_employment_parttime                  = 0.224295;				//    0.086812    2.58  0.00978 ** 
		publictransport_employment_parttime          = 0.147577;				//    0.079342    1.86  0.06289 .  
		
		cardriver_employment_marginal                = 0.136550;				//    0.100658    1.36  0.17491    
		carpassenger_employment_marginal             = 0.659948;				//    0.131355    5.02  5.1e-07 ***
		cycling_employment_marginal                  = 0.507463;				//    0.137325    3.70  0.00022 ***
		publictransport_employment_marginal         = -0.068402;				//    0.160264   -0.43  0.66952    
		
		cardriver_employment_homekeeper             = -0.045645	+ 0.2;				//    0.079404   -0.57  0.56540    
		carpassenger_employment_homekeeper           = 0.392988;				//    0.102937    3.82  0.00013 ***
		cycling_employment_homekeeper                = 0.422144;				//    0.116398    3.63  0.00029 ***
		publictransport_employment_homekeeper        = 0.098696;				//    0.137381    0.72  0.47250    
		
		cardriver_employment_unemployed             = -0.141944;				//    0.176767   -0.80  0.42197    
		carpassenger_employment_unemployed           = 0.190695;				//    0.202663    0.94  0.34673    
		cycling_employment_unemployed                = 0.058544;				//    0.256860    0.23  0.81971    
		publictransport_employment_unemployed        = 0.537937;				//    0.199623    2.69  0.00704 ** 
		cardriver_employment_retired                = -0.278013;				//    0.085816   -3.24  0.00120 ** 
		carpassenger_employment_retired             = -0.056108;				//    0.111393   -0.50  0.61448    
		cycling_employment_retired                  = -0.156770;				//    0.143860   -1.09  0.27583    
		publictransport_employment_retired           = 0.352281;				//    0.118365    2.98  0.00292 ** 
		cardriver_employment_pupil                   = 0.069790;				//    0.176055    0.40  0.69180    
		carpassenger_employment_pupil                = 0.359959;				//    0.192389    1.87  0.06135 .  
		cycling_employment_pupil                     = 0.326425;				//    0.256922    1.27  0.20390    
		publictransport_employment_pupil             = 0.131469;				//    0.185273    0.71  0.47796    
		cardriver_employment_student                 = 0.144702;				//    0.167790    0.86  0.38847    
		carpassenger_employment_student              = 0.343638;				//    0.205132    1.68  0.09389 .  
		cycling_employment_student                   = 0.635060;				//    0.230519    2.75  0.00587 ** 
		publictransport_employment_student           = 0.172553;				//    0.169819    1.02  0.30958    
		cardriver_employment_apprentice              = 0.177228;				//    0.241916    0.73  0.46380    
		carpassenger_employment_apprentice           = 0.243291;				//    0.266519    0.91  0.36132    
		cycling_employment_apprentice               = -0.342465;				//    0.359420   -0.95  0.34068    
		publictransport_employment_apprentice        = 0.292823;				//    0.222484    1.32  0.18812    
		cardriver_employment_other                  = -0.818120;				//    0.242803   -3.37  0.00075 ***
		carpassenger_employment_other               = -0.602879;				//    0.317369   -1.90  0.05748 .  
		cycling_employment_other                    = -0.041568;				//    0.287470   -0.14  0.88503    
		publictransport_employment_other             = 0.812008;				//    0.283629    2.86  0.00420 ** 
		cardriver_age_06to09                        = -0.340691;				// 9222.610268    0.00  0.99997    
		carpassenger_age_06to09                      = 1.127556;				//    0.197799    5.70  1.2e-08 ***
		cycling_age_06to09                          = -0.649201;				//    0.273030   -2.38  0.01742 *  
		publictransport_age_06to09                  = -0.614117;				//    0.214728   -2.86  0.00424 ** 
		cardriver_age_10to17                        = -0.411025;				//    0.301830   -1.36  0.17327    
		carpassenger_age_10to17                      = 1.036450;				//    0.191903    5.40  6.6e-08 ***
		cycling_age_10to17                           = 0.323379;				//    0.256823    1.26  0.20798    
		publictransport_age_10to17                   = 0.468166;				//    0.186633    2.51  0.01212 *  
		cardriver_age_18to25                         = 0.409507;				//    0.125639    3.26  0.00112 ** 
		carpassenger_age_18to25                      = 0.733133;				//    0.150032    4.89  1.0e-06 ***
		cycling_age_18to25                          = -0.060730;				//    0.203638   -0.30  0.76553    
		publictransport_age_18to25                   = 0.431699;				//    0.146755    2.94  0.00326 ** 
		cardriver_age_26to35                        = -0.136967;				//    0.062031   -2.21  0.02724 *  
		carpassenger_age_26to35                     = -0.147424;				//    0.087948   -1.68  0.09369 .  
		cycling_age_26to35                          = -0.353870;				//    0.108330   -3.27  0.00109 ** 
		publictransport_age_26to35                   = 0.058217;				//    0.085337    0.68  0.49511    
		cardriver_age_51to60                         = 0.137530;				//    0.046741    2.94  0.00326 ** 
		carpassenger_age_51to60                      = 0.241737;				//    0.064968    3.72  0.00020 ***
		cycling_age_51to60                          = -0.122604;				//    0.076793   -1.60  0.11037    
		publictransport_age_51to60                   = 0.106267;				//    0.069094    1.54  0.12405    
		cardriver_age_61to70                         = 0.099298;				//    0.075598    1.31  0.18902    
		carpassenger_age_61to70                      = 0.308667;				//    0.097181    3.18  0.00149 ** 
		cycling_age_61to70                           = 0.015502;				//    0.127427    0.12  0.90317    
		publictransport_age_61to70                   = 0.242769;				//    0.106392    2.28  0.02250 *  
		cardriver_age_71plus                         = 0.239121;				//    0.088690    2.70  0.00701 ** 
		carpassenger_age_71plus                      = 0.466543;				//    0.112165    4.16  3.2e-05 ***
		cycling_age_71plus                           = 0.291059;				//    0.157463    1.85  0.06454 .  
		publictransport_age_71plus                   = 0.450354;				//    0.123852    3.64  0.00028 ***
		cardriver_purpose_business                   = 0.348277;				//    0.150618    2.31  0.02076 *  
		carpassenger_purpose_business                = 0.748353;				//    0.218884    3.42  0.00063 ***
		cycling_purpose_business                    = -0.369058;				//    0.238241   -1.55  0.12136    
		publictransport_purpose_business            = -0.251698;				//    0.196110   -1.28  0.19933    
		cardriver_purpose_education                 = -1.256017;				//    0.118363  -10.61  < 2e-16 ***
		carpassenger_purpose_education              = -0.060324;				//    0.103428   -0.58  0.55973    
		cycling_purpose_education                   = -0.658196;				//    0.107925   -6.10  1.1e-09 ***
		publictransport_purpose_education           = -0.350400;				//    0.097107   -3.61  0.00031 ***
		cardriver_purpose_service                    = 0.492787;				//    0.068959    7.15  8.9e-13 ***
		carpassenger_purpose_service                 = 0.977048;				//    0.106893    9.14  < 2e-16 ***
		cycling_purpose_service                     = -1.048308;				//    0.121895   -8.60  < 2e-16 ***
		publictransport_purpose_service             = -1.984629;				//    0.126318  -15.71  < 2e-16 ***
		cardriver_purpose_private_business          = -0.275393;				//    0.066219   -4.16  3.2e-05 ***
		carpassenger_purpose_private_business        = 1.146943;				//    0.095448   12.02  < 2e-16 ***
		cycling_purpose_private_business            = -0.813002;				//    0.106262   -7.65  2.0e-14 ***
		publictransport_purpose_private_business    = -1.256200;				//    0.088887  -14.13  < 2e-16 ***
		cardriver_purpose_visit                     = -0.494574;				//    0.075941   -6.51  7.4e-11 ***
		carpassenger_purpose_visit                   = 0.965521;				//    0.100920    9.57  < 2e-16 ***
		cycling_purpose_visit                       = -1.206957;				//    0.127654   -9.45  < 2e-16 ***
		publictransport_purpose_visit               = -1.752837;				//    0.109391  -16.02  < 2e-16 ***
		cardriver_purpose_shopping_grocery          = -0.251249;				//    0.062948   -3.99  6.6e-05 ***
		carpassenger_purpose_shopping_grocery        = 1.093678;				//    0.093920   11.64  < 2e-16 ***
		cycling_purpose_shopping_grocery            = -0.438362;				//    0.095671   -4.58  4.6e-06 ***
		publictransport_purpose_shopping_grocery    = -2.155677;				//    0.096247  -22.40  < 2e-16 ***
		cardriver_purpose_shopping_other             = 0.013438;				//    0.087509    0.15  0.87795    
		carpassenger_purpose_shopping_other          = 1.709526;				//    0.111640   15.31  < 2e-16 ***
		cycling_purpose_shopping_other              = -0.623898;				//    0.144707   -4.31  1.6e-05 ***
		publictransport_purpose_shopping_other      = -0.757384;				//    0.110267   -6.87  6.5e-12 ***
		cardriver_purpose_leisure_indoors           = -1.118486;				//    0.083436  -13.41  < 2e-16 ***
		carpassenger_purpose_leisure_indoors         = 1.370797;				//    0.104024   13.18  < 2e-16 ***
		cycling_purpose_leisure_indoors             = -1.848780;				//    0.165451  -11.17  < 2e-16 ***
		publictransport_purpose_leisure_indoors     = -0.929085;				//    0.099507   -9.34  < 2e-16 ***
		cardriver_purpose_leisure_outdoors           = 0.181056;				//    0.075925    2.38  0.01709 *  
		carpassenger_purpose_leisure_outdoors        = 1.838675;				//    0.098781   18.61  < 2e-16 ***
		cycling_purpose_leisure_outdoors            = -0.333494;				//    0.109866   -3.04  0.00240 ** 
		publictransport_purpose_leisure_outdoors    = -1.329932;				//    0.104303  -12.75  < 2e-16 ***
		cardriver_purpose_leisure_other             = -0.534154;				//    0.074155   -7.20  5.9e-13 ***
		carpassenger_purpose_leisure_other           = 1.136027;				//    0.098609   11.52  < 2e-16 ***
		cycling_purpose_leisure_other               = -0.937763;				//    0.114639   -8.18  2.2e-16 ***
		publictransport_purpose_leisure_other       = -1.015374;				//    0.094103  -10.79  < 2e-16 ***
		cardriver_purpose_leisure_walk              = -2.568677;				//    0.083592  -30.73  < 2e-16 ***
		carpassenger_purpose_leisure_walk           = -0.629099;				//    0.117239   -5.37  8.1e-08 ***
		cycling_purpose_leisure_walk                = -1.252832;				//    0.105072  -11.92  < 2e-16 ***
		publictransport_purpose_leisure_walk        = -2.330588;				//    0.132214  -17.63  < 2e-16 ***
		cardriver_purpose_other                     = -0.064979;				//    0.441737   -0.15  0.88305    
		carpassenger_purpose_other                   = 1.572296;				//    0.368316    4.27  2.0e-05 ***
		cycling_purpose_other                       = -2.040626;				//    1.094952   -1.86  0.06237 .  
		publictransport_purpose_other               = -0.441282;				//    0.565445   -0.78  0.43515    
		
		cardriver_day_Saturday                      = -0.321698;				//    0.048924   -6.58  4.9e-11 ***
		carpassenger_day_Saturday                    = 0.524874;				//    0.054371    9.65  < 2e-16 ***
		cycling_day_Saturday                        = -0.352697;				//    0.082833   -4.26  2.1e-05 ***
		publictransport_day_Saturday                = -0.365917 + 0.2;				//    0.072468   -5.05  4.4e-07 ***
		
		cardriver_day_Sunday                        = -0.966451;				//    0.060415  -16.00  < 2e-16 ***
		carpassenger_day_Sunday                      = 0.016437;				//    0.064664    0.25  0.79935    
		cycling_day_Sunday                          = -0.939068	+ 0.2;				//    0.104022   -9.03  < 2e-16 ***
		publictransport_day_Sunday                  = -1.111661	+ 0.1;				//    0.090238  -12.32  < 2e-16 ***
		
		cardriver_num_activities                     = 0.289634;				//    0.046023    6.29  3.1e-10 ***
		carpassenger_num_activities                  = 0.368879;				//    0.051668    7.14  9.4e-13 ***
		cycling_num_activities                       = 0.175388;				//    0.072398    2.42  0.01541 *  
		publictransport_num_activities               = 0.296257;				//    0.056200    5.27  1.4e-07 ***
		cardriver_containsStrolling                 = -1.658377;				//    0.188435   -8.80  < 2e-16 ***
		carpassenger_containsStrolling              = -1.211814;				//    0.223715   -5.42  6.1e-08 ***
		cycling_containsStrolling                   = -0.762360;				//    0.290433   -2.62  0.00867 ** 
		publictransport_containsStrolling           = -1.268804;				//    0.235831   -5.38  7.4e-08 ***
		cardriver_containsVisit                      = 0.369270;				//    0.138461    2.67  0.00765 ** 
		carpassenger_containsVisit                  = -0.135476;				//    0.150282   -0.90  0.36733    
		cycling_containsVisit                       = -0.001815;				//    0.196111   -0.01  0.99262    
		publictransport_containsVisit               = -0.184208;				//    0.165299   -1.11  0.26511    
		cardriver_containsPrivateB                   = 0.156079;				//    0.107538    1.45  0.14668    
		carpassenger_containsPrivateB               = -0.096198;				//    0.126491   -0.76  0.44695    
		cycling_containsPrivateB                    = -0.022592;				//    0.169467   -0.13  0.89394    
		publictransport_containsPrivateB            = -0.114500;				//    0.133032   -0.86  0.38941    
		cardriver_containsService                    = 0.015256;				//    0.186175    0.08  0.93469    
		carpassenger_containsService                = -0.221204;				//    0.239937   -0.92  0.35657    
		cycling_containsService                     = -0.719702;				//    0.269510   -2.67  0.00758 ** 
		publictransport_containsService             = -1.697657;				//    0.250795   -6.77  1.3e-11 ***
		cardriver_containsLeisure                    = 0.539112;				//    0.109396    4.93  8.3e-07 ***
		carpassenger_containsLeisure                 = 0.904149;				//    0.115758    7.81  5.8e-15 ***
		cycling_containsLeisure                     = -0.042692;				//    0.173111   -0.25  0.80520    
		publictransport_containsLeisure              = 0.511610;				//    0.127671    4.01  6.1e-05 ***
		cardriver_containsShopping                   = 0.218578;				//    0.089848    2.43  0.01498 *  
		carpassenger_containsShopping               = -0.047244;				//    0.107291   -0.44  0.65970    
		cycling_containsShopping                     = 0.082944;				//    0.144176    0.58  0.56509    
		publictransport_containsShopping             = 0.016312;				//    0.110850    0.15  0.88301    
		cardriver_containsBusiness                   = 0.597812;				//    0.214187    2.79  0.00525 ** 
		carpassenger_containsBusiness                = 0.110888;				//    0.305980    0.36  0.71705    
		cycling_containsBusiness                     = 0.398270;				//    0.278746    1.43  0.15306    
		publictransport_containsBusiness             = 0.347964;				//    0.247110    1.41  0.15909    
		
		cardriver_firsttour                          = 0.221979;				//    0.051829    4.28  1.8e-05 ***
		carpassenger_firsttour                      = -0.011104;				//    0.064080   -0.17  0.86243    
		cycling_firsttour                           = -0.053900;				//    0.072446   -0.74  0.45687    
		publictransport_firsttour                    = 0.250610;				//    0.058886    4.26  2.1e-05 ***
		
		walking_numusedbefore                           = 0.221488;				//    0.006079   36.43  < 2e-16 ***
		cardriver_numusedbefore                         = 0.241348;				//    0.005477   44.06  < 2e-16 ***
		carpassenger_numusedbefore                      = 0.503579;				//    0.012366   40.72  < 2e-16 ***
		cycling_numusedbefore                           = 0.549314;				//    0.011628   47.24  < 2e-16 ***
		publictransport_numusedbefore                   = 0.404801;				//    0.011437   35.39  < 2e-16 ***

	
		

		init();
	}

	protected void init() {
		
		super.init();

		this.parameterWalk.put("NUM_USED_BEFORE", walking_numusedbefore);
		this.parameterBike.put("NUM_USED_BEFORE", cycling_numusedbefore);
		this.parameterCar.put("NUM_USED_BEFORE", cardriver_numusedbefore);
		this.parameterPassenger.put("NUM_USED_BEFORE", carpassenger_numusedbefore);
		this.parameterPt.put("NUM_USED_BEFORE", publictransport_numusedbefore);
		
		this.parameterWalk.put("INITIAL_TOUR", walking_firsttour);
		this.parameterBike.put("INITIAL_TOUR", cycling_firsttour);
		this.parameterCar.put("INITIAL_TOUR", cardriver_firsttour);
		this.parameterPassenger.put("INITIAL_TOUR", carpassenger_firsttour);
		this.parameterPt.put("INITIAL_TOUR", publictransport_firsttour);
	}



}
