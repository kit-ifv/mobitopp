package edu.kit.ifv.mobitopp.simulation.tour;


public class TourModeChoiceParameterTimeCostByEmpSexSameMode 
	extends TourModeChoiceParameterBaseTimeCostByEmpSex 
	implements TourModeChoiceParameter
{

	protected Double	walking_same_mode = 0.0;
	
	protected Double	cardriver_same_mode;
	protected Double	carpassenger_same_mode;
	protected Double	cycling_same_mode;
	protected Double	publictransport_same_mode;
	
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
    containsShopping. + containsBusiness. + firsttour. | same_mode., 
    data = trainingdata, reflevel = "walking", method = "nr", 
    print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2876          0.3914          0.1279          0.0544          0.1388 

nr method
24 iterations, 0h:7m:43s 
g'(-H)^-1g = 3.98E-07 
gradient close to zero 

Coefficients :
                                            Estimate  Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                       0.238823    0.080633    2.96  0.00306 ** 
carpassenger:(intercept)                   -4.064172    0.108346  -37.51  < 2e-16 ***
cycling:(intercept)                        -1.683817    0.108350  -15.54  < 2e-16 ***
publictransport:(intercept)                -0.095500    0.090639   -1.05  0.29205    
notavailable                              -24.568471 2731.488202   -0.01  0.99282    
time                                       -0.029051    0.000974  -29.84  < 2e-16 ***
cost                                       -0.607678    0.018961  -32.05  < 2e-16 ***
time:sex.female                            -0.002886    0.001171   -2.46  0.01376 *  
time:employment.parttime                   -0.011965    0.002076   -5.76  8.2e-09 ***
time:employment.marginal                    0.001836    0.003103    0.59  0.55401    
time:employment.homekeeper                 -0.005348    0.002606   -2.05  0.04015 *  
time:employment.unemployed                 -0.005140    0.004806   -1.07  0.28484    
time:employment.retired                    -0.003221    0.001403   -2.30  0.02171 *  
time:employment.pupil                      -0.008749    0.001753   -4.99  6.0e-07 ***
time:employment.student                     0.011724    0.002495    4.70  2.6e-06 ***
time:employment.apprentice                 -0.009922    0.005663   -1.75  0.07977 .  
time:employment.other                      -0.040967    0.012187   -3.36  0.00078 ***
cost:sex.female                             0.006759    0.024590    0.27  0.78342    
cost:employment.parttime                    0.091065    0.038119    2.39  0.01690 *  
cost:employment.marginal                    0.258251    0.077892    3.32  0.00091 ***
cost:employment.homekeeper                  0.301019    0.062116    4.85  1.3e-06 ***
cost:employment.unemployed                  0.266063    0.100625    2.64  0.00819 ** 
cost:employment.retired                     0.135998    0.031055    4.38  1.2e-05 ***
cost:employment.pupil                      -0.092830    0.046346   -2.00  0.04518 *  
cost:employment.student                     0.228080    0.056979    4.00  6.3e-05 ***
cost:employment.apprentice                  0.076961    0.075825    1.01  0.31011    
cost:employment.other                       0.078786    0.131342    0.60  0.54861    
cardriver:sex.female                       -0.586067    0.043685  -13.42  < 2e-16 ***
carpassenger:sex.female                     0.468136    0.047626    9.83  < 2e-16 ***
cycling:sex.female                         -0.579995    0.053125  -10.92  < 2e-16 ***
publictransport:sex.female                 -0.000712    0.044059   -0.02  0.98710    
cardriver:employment.parttime              -0.054832    0.064567   -0.85  0.39576    
carpassenger:employment.parttime            0.077768    0.085986    0.90  0.36577    
cycling:employment.parttime                 0.288491    0.084663    3.41  0.00066 ***
publictransport:employment.parttime         0.053897    0.078077    0.69  0.49000    
cardriver:employment.marginal               0.145099    0.098105    1.48  0.13914    
carpassenger:employment.marginal            0.573619    0.128741    4.46  8.4e-06 ***
cycling:employment.marginal                 0.564679    0.130669    4.32  1.6e-05 ***
publictransport:employment.marginal        -0.281770    0.157528   -1.79  0.07366 .  
cardriver:employment.homekeeper            -0.090419    0.077117   -1.17  0.24100    
carpassenger:employment.homekeeper          0.239018    0.100617    2.38  0.01752 *  
cycling:employment.homekeeper               0.255170    0.114835    2.22  0.02628 *  
publictransport:employment.homekeeper      -0.278890    0.133695   -2.09  0.03698 *  
cardriver:employment.unemployed            -0.378595    0.170930   -2.21  0.02677 *  
carpassenger:employment.unemployed          0.097169    0.195899    0.50  0.61988    
cycling:employment.unemployed               0.038322    0.233836    0.16  0.86982    
publictransport:employment.unemployed       0.256518    0.193663    1.32  0.18532    
cardriver:employment.retired               -0.349140    0.083344   -4.19  2.8e-05 ***
carpassenger:employment.retired            -0.137111    0.108476   -1.26  0.20624    
cycling:employment.retired                 -0.276320    0.142262   -1.94  0.05210 .  
publictransport:employment.retired          0.100817    0.115184    0.88  0.38143    
cardriver:employment.pupil                  0.029568    0.173702    0.17  0.86483    
carpassenger:employment.pupil               0.457639    0.189231    2.42  0.01559 *  
cycling:employment.pupil                    0.436756    0.250972    1.74  0.08181 .  
publictransport:employment.pupil            0.103886    0.182912    0.57  0.57007    
cardriver:employment.student                0.030443    0.166844    0.18  0.85522    
carpassenger:employment.student             0.296876    0.204045    1.45  0.14568    
cycling:employment.student                  0.600650    0.228525    2.63  0.00858 ** 
publictransport:employment.student          0.051836    0.166981    0.31  0.75623    
cardriver:employment.apprentice             0.205212    0.239701    0.86  0.39193    
carpassenger:employment.apprentice          0.238704    0.262588    0.91  0.36333    
cycling:employment.apprentice              -0.390075    0.358550   -1.09  0.27663    
publictransport:employment.apprentice       0.298438    0.219441    1.36  0.17383    
cardriver:employment.other                 -0.717842    0.228925   -3.14  0.00171 ** 
carpassenger:employment.other              -0.264009    0.290469   -0.91  0.36340    
cycling:employment.other                   -0.076421    0.286532   -0.27  0.78969    
publictransport:employment.other            0.743973    0.277345    2.68  0.00731 ** 
cardriver:age.06to09                       -0.588773 9449.027954    0.00  0.99995    
carpassenger:age.06to09                     1.135080    0.194608    5.83  5.5e-09 ***
cycling:age.06to09                         -0.977666    0.267991   -3.65  0.00026 ***
publictransport:age.06to09                 -0.673800    0.211810   -3.18  0.00147 ** 
cardriver:age.10to17                       -0.558644    0.303314   -1.84  0.06550 .  
carpassenger:age.10to17                     1.137471    0.189034    6.02  1.8e-09 ***
cycling:age.10to17                          0.319687    0.251295    1.27  0.20332    
publictransport:age.10to17                  0.619020    0.184341    3.36  0.00079 ***
cardriver:age.18to25                        0.425448    0.125083    3.40  0.00067 ***
carpassenger:age.18to25                     0.995542    0.149035    6.68  2.4e-11 ***
cycling:age.18to25                         -0.013344    0.201843   -0.07  0.94729    
publictransport:age.18to25                  0.573381    0.145334    3.95  8.0e-05 ***
cardriver:age.26to35                       -0.222321    0.060317   -3.69  0.00023 ***
carpassenger:age.26to35                    -0.090776    0.086879   -1.04  0.29609    
cycling:age.26to35                         -0.398059    0.106028   -3.75  0.00017 ***
publictransport:age.26to35                  0.049814    0.083903    0.59  0.55271    
cardriver:age.51to60                        0.123821    0.045310    2.73  0.00628 ** 
carpassenger:age.51to60                     0.379313    0.063662    5.96  2.5e-09 ***
cycling:age.51to60                         -0.179067    0.075555   -2.37  0.01779 *  
publictransport:age.51to60                  0.131555    0.067905    1.94  0.05270 .  
cardriver:age.61to70                        0.043212    0.073546    0.59  0.55683    
carpassenger:age.61to70                     0.534171    0.094954    5.63  1.8e-08 ***
cycling:age.61to70                         -0.072971    0.126955   -0.57  0.56544    
publictransport:age.61to70                  0.284048    0.104177    2.73  0.00640 ** 
cardriver:age.71plus                        0.190858    0.086242    2.21  0.02689 *  
carpassenger:age.71plus                     0.702001    0.109566    6.41  1.5e-10 ***
cycling:age.71plus                          0.235179    0.156807    1.50  0.13367    
publictransport:age.71plus                  0.509671    0.121070    4.21  2.6e-05 ***
cardriver:purpose.business                  0.426680    0.147887    2.89  0.00391 ** 
carpassenger:purpose.business               0.848164    0.214344    3.96  7.6e-05 ***
cycling:purpose.business                   -0.146032    0.230642   -0.63  0.52663    
publictransport:purpose.business           -0.207613    0.195333   -1.06  0.28784    
cardriver:purpose.education                -1.170002    0.117617   -9.95  < 2e-16 ***
carpassenger:purpose.education             -0.110776    0.101889   -1.09  0.27694    
cycling:purpose.education                  -0.688868    0.105277   -6.54  6.0e-11 ***
publictransport:purpose.education          -0.284921    0.094653   -3.01  0.00261 ** 
cardriver:purpose.service                   0.563007    0.066938    8.41  < 2e-16 ***
carpassenger:purpose.service                0.972419    0.105235    9.24  < 2e-16 ***
cycling:purpose.service                    -0.904971    0.116532   -7.77  8.2e-15 ***
publictransport:purpose.service            -1.804269    0.122193  -14.77  < 2e-16 ***
cardriver:purpose.private_business         -0.171168    0.064935   -2.64  0.00839 ** 
carpassenger:purpose.private_business       1.213156    0.094603   12.82  < 2e-16 ***
cycling:purpose.private_business           -0.707540    0.104989   -6.74  1.6e-11 ***
publictransport:purpose.private_business   -1.092388    0.087080  -12.54  < 2e-16 ***
cardriver:purpose.visit                    -0.377785    0.073700   -5.13  3.0e-07 ***
carpassenger:purpose.visit                  1.002966    0.099250   10.11  < 2e-16 ***
cycling:purpose.visit                      -1.116992    0.122171   -9.14  < 2e-16 ***
publictransport:purpose.visit              -1.546975    0.105284  -14.69  < 2e-16 ***
cardriver:purpose.shopping_grocery         -0.163975    0.061535   -2.66  0.00770 ** 
carpassenger:purpose.shopping_grocery       1.160410    0.092881   12.49  < 2e-16 ***
cycling:purpose.shopping_grocery           -0.324953    0.094258   -3.45  0.00057 ***
publictransport:purpose.shopping_grocery   -1.963814    0.093496  -21.00  < 2e-16 ***
cardriver:purpose.shopping_other            0.096364    0.085471    1.13  0.25956    
carpassenger:purpose.shopping_other         1.680237    0.110297   15.23  < 2e-16 ***
cycling:purpose.shopping_other             -0.529382    0.141145   -3.75  0.00018 ***
publictransport:purpose.shopping_other     -0.625672    0.107150   -5.84  5.2e-09 ***
cardriver:purpose.leisure_indoors          -0.975469    0.080951  -12.05  < 2e-16 ***
carpassenger:purpose.leisure_indoors        1.365875    0.102593   13.31  < 2e-16 ***
cycling:purpose.leisure_indoors            -1.648378    0.154134  -10.69  < 2e-16 ***
publictransport:purpose.leisure_indoors    -0.805053    0.095992   -8.39  < 2e-16 ***
cardriver:purpose.leisure_outdoors          0.276751    0.074287    3.73  0.00019 ***
carpassenger:purpose.leisure_outdoors       1.854214    0.097751   18.97  < 2e-16 ***
cycling:purpose.leisure_outdoors           -0.275468    0.107991   -2.55  0.01075 *  
publictransport:purpose.leisure_outdoors   -1.178573    0.101919  -11.56  < 2e-16 ***
cardriver:purpose.leisure_other            -0.427900    0.072061   -5.94  2.9e-09 ***
carpassenger:purpose.leisure_other          1.142863    0.097284   11.75  < 2e-16 ***
cycling:purpose.leisure_other              -0.849066    0.109771   -7.73  1.0e-14 ***
publictransport:purpose.leisure_other      -0.837022    0.090826   -9.22  < 2e-16 ***
cardriver:purpose.leisure_walk             -2.481828    0.080748  -30.74  < 2e-16 ***
carpassenger:purpose.leisure_walk          -0.542581    0.113955   -4.76  1.9e-06 ***
cycling:purpose.leisure_walk               -1.225735    0.102362  -11.97  < 2e-16 ***
publictransport:purpose.leisure_walk       -2.233804    0.128240  -17.42  < 2e-16 ***
cardriver:purpose.other                     0.005454    0.438033    0.01  0.99007    
carpassenger:purpose.other                  1.642237    0.376211    4.37  1.3e-05 ***
cycling:purpose.other                      -1.761051    1.059956   -1.66  0.09663 .  
publictransport:purpose.other              -0.325086    0.581850   -0.56  0.57636    
cardriver:day.Saturday                      0.024280    0.044152    0.55  0.58238    
carpassenger:day.Saturday                   0.412240    0.051198    8.05  8.9e-16 ***
cycling:day.Saturday                       -0.072867    0.074157   -0.98  0.32580    
publictransport:day.Saturday                0.089564    0.064031    1.40  0.16188    
cardriver:day.Sunday                       -0.322378    0.053925   -5.98  2.3e-09 ***
carpassenger:day.Sunday                     0.034224    0.060350    0.57  0.57065    
cycling:day.Sunday                         -0.294217    0.088201   -3.34  0.00085 ***
publictransport:day.Sunday                 -0.484080    0.079172   -6.11  9.7e-10 ***
cardriver:intrazonal.TRUE                  -1.057085    0.035471  -29.80  < 2e-16 ***
carpassenger:intrazonal.TRUE               -0.927258    0.045518  -20.37  < 2e-16 ***
cycling:intrazonal.TRUE                     0.059232    0.048813    1.21  0.22496    
publictransport:intrazonal.TRUE            -3.106246    0.092794  -33.47  < 2e-16 ***
cardriver:num_activities                    0.316958    0.045068    7.03  2.0e-12 ***
carpassenger:num_activities                 0.361232    0.050762    7.12  1.1e-12 ***
cycling:num_activities                      0.141970    0.072389    1.96  0.04985 *  
publictransport:num_activities              0.299922    0.055240    5.43  5.7e-08 ***
cardriver:containsStrolling.TRUE           -1.645880    0.183768   -8.96  < 2e-16 ***
carpassenger:containsStrolling.TRUE        -1.249491    0.219823   -5.68  1.3e-08 ***
cycling:containsStrolling.TRUE             -0.747275    0.287458   -2.60  0.00933 ** 
publictransport:containsStrolling.TRUE     -1.274220    0.232356   -5.48  4.2e-08 ***
cardriver:containsVisit.TRUE                0.362738    0.135139    2.68  0.00727 ** 
carpassenger:containsVisit.TRUE            -0.076400    0.145064   -0.53  0.59843    
cycling:containsVisit.TRUE                  0.045008    0.193647    0.23  0.81621    
publictransport:containsVisit.TRUE         -0.201958    0.162286   -1.24  0.21333    
cardriver:containsPrivateB.TRUE             0.134349    0.105779    1.27  0.20405    
carpassenger:containsPrivateB.TRUE         -0.088240    0.124341   -0.71  0.47792    
cycling:containsPrivateB.TRUE              -0.047898    0.170185   -0.28  0.77837    
publictransport:containsPrivateB.TRUE      -0.100364    0.131292   -0.76  0.44461    
cardriver:containsService.TRUE              0.125003    0.185509    0.67  0.50041    
carpassenger:containsService.TRUE          -0.061428    0.236447   -0.26  0.79502    
cycling:containsService.TRUE               -0.552562    0.267776   -2.06  0.03906 *  
publictransport:containsService.TRUE       -1.555483    0.248041   -6.27  3.6e-10 ***
cardriver:containsLeisure.TRUE              0.487875    0.106862    4.57  5.0e-06 ***
carpassenger:containsLeisure.TRUE           0.878384    0.112962    7.78  7.5e-15 ***
cycling:containsLeisure.TRUE                0.024606    0.168257    0.15  0.88373    
publictransport:containsLeisure.TRUE        0.498773    0.125535    3.97  7.1e-05 ***
cardriver:containsShopping.TRUE             0.213372    0.088018    2.42  0.01534 *  
carpassenger:containsShopping.TRUE         -0.044545    0.105427   -0.42  0.67265    
cycling:containsShopping.TRUE               0.091174    0.143358    0.64  0.52478    
publictransport:containsShopping.TRUE       0.025287    0.109359    0.23  0.81714    
cardriver:containsBusiness.TRUE             0.529965    0.209561    2.53  0.01144 *  
carpassenger:containsBusiness.TRUE          0.189094    0.300744    0.63  0.52951    
cycling:containsBusiness.TRUE               0.596958    0.273862    2.18  0.02927 *  
publictransport:containsBusiness.TRUE       0.310194    0.242565    1.28  0.20097    
cardriver:firsttour.TRUE                    0.423738    0.053388    7.94  2.0e-15 ***
carpassenger:firsttour.TRUE                 0.080719    0.064531    1.25  0.21099    
cycling:firsttour.TRUE                      0.225874    0.073715    3.06  0.00218 ** 
publictransport:firsttour.TRUE              0.337065    0.059893    5.63  1.8e-08 ***
walking:same_mode.TRUE                      0.393756    0.030545   12.89  < 2e-16 ***
cardriver:same_mode.TRUE                    1.025784    0.027462   37.35  < 2e-16 ***
carpassenger:same_mode.TRUE                 1.126115    0.037947   29.68  < 2e-16 ***
cycling:same_mode.TRUE                      2.274833    0.048875   46.54  < 2e-16 ***
publictransport:same_mode.TRUE              1.151092    0.040087   28.71  < 2e-16 ***
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -46000
McFadden R^2:  0.439 
Likelihood ratio test : chisq = 72000 (p.value = <2e-16)

		*/


	
	
	public TourModeChoiceParameterTimeCostByEmpSexSameMode() {
	
		walking																				 = 0.0	- 0.5;
		walking_intrazonal														 = 0.0	+ 0.6;
		walking_day_Saturday                           = 0.0;
		walking_day_Sunday                           	 = 0.0;
		
		// current v15, next: v16
		
		cardriver                                    = 0.238823;				//    0.080633    2.96  0.00306 ** 
		carpassenger                                = -4.064172 - 0.2;				//    0.108346  -37.51  < 2e-16 ***
		cycling                                     = -1.683817;				//    0.108350  -15.54  < 2e-16 ***
		publictransport                             = -0.095500	+ 0.65;				//    0.090639   -1.05  0.29205    
		
		cardriver_intrazonal                        = -1.057085 - 0.4;				//    0.035471  -29.80  < 2e-16 ***
		carpassenger_intrazonal                     = -0.927258;				//    0.045518  -20.37  < 2e-16 ***
		cycling_intrazonal                           = 0.059232 + 0.3;				//    0.048813    1.21  0.22496    
		publictransport_intrazonal                  = -3.106246 - 0.3;				//    0.092794  -33.47  < 2e-16 ***
		
		notavailable                               = -24.568471;				// 2731.488202   -0.01  0.99282    
		time                                        = -0.029051;				//    0.000974  -29.84  < 2e-16 ***
		cost                                        = -0.607678;				//    0.018961  -32.05  < 2e-16 ***
		
		time_sex_female                             = -0.002886;				//    0.001171   -2.46  0.01376 *  
		time_employment_parttime                    = -0.011965;				//    0.002076   -5.76  8.2e-09 ***
		time_employment_marginal                     = 0.001836;				//    0.003103    0.59  0.55401    
		time_employment_homekeeper                  = -0.005348;				//    0.002606   -2.05  0.04015 *  
		time_employment_unemployed                  = -0.005140;				//    0.004806   -1.07  0.28484    
		time_employment_retired                     = -0.003221;				//    0.001403   -2.30  0.02171 *  
		time_employment_pupil                       = -0.008749;				//    0.001753   -4.99  6.0e-07 ***
		time_employment_student                      = 0.011724;				//    0.002495    4.70  2.6e-06 ***
		time_employment_apprentice                  = -0.009922;				//    0.005663   -1.75  0.07977 .  
		time_employment_other                       = -0.040967;				//    0.012187   -3.36  0.00078 ***
		cost_sex_female                              = 0.006759;				//    0.024590    0.27  0.78342    
		cost_employment_parttime                     = 0.091065;				//    0.038119    2.39  0.01690 *  
		cost_employment_marginal                     = 0.258251;				//    0.077892    3.32  0.00091 ***
		cost_employment_homekeeper                   = 0.301019;				//    0.062116    4.85  1.3e-06 ***
		cost_employment_unemployed                   = 0.266063;				//    0.100625    2.64  0.00819 ** 
		cost_employment_retired                      = 0.135998;				//    0.031055    4.38  1.2e-05 ***
		cost_employment_pupil                       = -0.092830;				//    0.046346   -2.00  0.04518 *  
		cost_employment_student                      = 0.228080;				//    0.056979    4.00  6.3e-05 ***
		cost_employment_apprentice                   = 0.076961;				//    0.075825    1.01  0.31011    
		cost_employment_other                        = 0.078786;				//    0.131342    0.60  0.54861    
		
		cardriver_sex_female                        = -0.586067 + 0.1;				//    0.043685  -13.42  < 2e-16 ***
		carpassenger_sex_female                      = 0.468136 + 0.05;				//    0.047626    9.83  < 2e-16 ***
		cycling_sex_female                          = -0.579995;				//    0.053125  -10.92  < 2e-16 ***
		publictransport_sex_female                  = -0.000712 - 0.1;				//    0.044059   -0.02  0.98710    
		
		cardriver_employment_parttime               = -0.054832 + 0.3;				//    0.064567   -0.85  0.39576    
		carpassenger_employment_parttime             = 0.077768 + 0.1;				//    0.085986    0.90  0.36577    
		cycling_employment_parttime                  = 0.288491;				//    0.084663    3.41  0.00066 ***
		publictransport_employment_parttime          = 0.053897;				//    0.078077    0.69  0.49000    
		
		cardriver_employment_marginal                = 0.145099 + 1.0;				//    0.098105    1.48  0.13914    
		carpassenger_employment_marginal             = 0.573619	+ 0.5;				//    0.128741    4.46  8.4e-06 ***
		cycling_employment_marginal                  = 0.564679	+ 0.2;				//    0.130669    4.32  1.6e-05 ***
		publictransport_employment_marginal         = -0.281770;				//    0.157528   -1.79  0.07366 .  
		
		cardriver_employment_homekeeper             = -0.090419 + 0.3;				//    0.077117   -1.17  0.24100    
		carpassenger_employment_homekeeper           = 0.239018;				//    0.100617    2.38  0.01752 *  
		cycling_employment_homekeeper                = 0.255170;				//    0.114835    2.22  0.02628 *  
		publictransport_employment_homekeeper       = -0.278890;				//    0.133695   -2.09  0.03698 *  
		
		cardriver_employment_unemployed             = -0.378595;				//    0.170930   -2.21  0.02677 *  
		carpassenger_employment_unemployed           = 0.097169;				//    0.195899    0.50  0.61988    
		cycling_employment_unemployed                = 0.038322;				//    0.233836    0.16  0.86982    
		publictransport_employment_unemployed        = 0.256518;				//    0.193663    1.32  0.18532    
		cardriver_employment_retired                = -0.349140;				//    0.083344   -4.19  2.8e-05 ***
		carpassenger_employment_retired             = -0.137111;				//    0.108476   -1.26  0.20624    
		cycling_employment_retired                  = -0.276320;				//    0.142262   -1.94  0.05210 .  
		publictransport_employment_retired           = 0.100817;				//    0.115184    0.88  0.38143    
		
		cardriver_employment_pupil                   = 0.029568;				//    0.173702    0.17  0.86483    
		carpassenger_employment_pupil                = 0.457639;				//    0.189231    2.42  0.01559 *  
		cycling_employment_pupil                     = 0.436756;				//    0.250972    1.74  0.08181 .  
		publictransport_employment_pupil             = 0.103886 - 0.1;				//    0.182912    0.57  0.57007    
		
		cardriver_employment_student                 = 0.030443;				//    0.166844    0.18  0.85522    
		carpassenger_employment_student              = 0.296876;				//    0.204045    1.45  0.14568    
		cycling_employment_student                   = 0.600650;				//    0.228525    2.63  0.00858 ** 
		publictransport_employment_student           = 0.051836;				//    0.166981    0.31  0.75623    
		
		cardriver_employment_apprentice              = 0.205212 + 0.3;				//    0.239701    0.86  0.39193    
		carpassenger_employment_apprentice           = 0.238704;				//    0.262588    0.91  0.36333    
		cycling_employment_apprentice               = -0.390075;				//    0.358550   -1.09  0.27663    
		publictransport_employment_apprentice        = 0.298438;				//    0.219441    1.36  0.17383    
		
		cardriver_employment_other                  = -0.717842;				//    0.228925   -3.14  0.00171 ** 
		carpassenger_employment_other               = -0.264009;				//    0.290469   -0.91  0.36340    
		cycling_employment_other                    = -0.076421;				//    0.286532   -0.27  0.78969    
		publictransport_employment_other             = 0.743973;				//    0.277345    2.68  0.00731 ** 
		cardriver_age_06to09                        = -0.588773;				// 9449.027954    0.00  0.99995    
		carpassenger_age_06to09                      = 1.135080;				//    0.194608    5.83  5.5e-09 ***
		cycling_age_06to09                          = -0.977666;				//    0.267991   -3.65  0.00026 ***
		publictransport_age_06to09                  = -0.673800;				//    0.211810   -3.18  0.00147 ** 
		cardriver_age_10to17                        = -0.558644;				//    0.303314   -1.84  0.06550 .  
		carpassenger_age_10to17                      = 1.137471;				//    0.189034    6.02  1.8e-09 ***
		cycling_age_10to17                           = 0.319687;				//    0.251295    1.27  0.20332    
		publictransport_age_10to17                   = 0.619020;				//    0.184341    3.36  0.00079 ***
		
		cardriver_age_18to25                         = 0.425448 - 0.1;				//    0.125083    3.40  0.00067 ***
		carpassenger_age_18to25                      = 0.995542;				//    0.149035    6.68  2.4e-11 ***
		cycling_age_18to25                          = -0.013344;				//    0.201843   -0.07  0.94729    
		publictransport_age_18to25                   = 0.573381;				//    0.145334    3.95  8.0e-05 ***
		
		cardriver_age_26to35                        = -0.222321;				//    0.060317   -3.69  0.00023 ***
		carpassenger_age_26to35                     = -0.090776;				//    0.086879   -1.04  0.29609    
		cycling_age_26to35                          = -0.398059;				//    0.106028   -3.75  0.00017 ***
		publictransport_age_26to35                   = 0.049814;				//    0.083903    0.59  0.55271    
		
		cardriver_age_51to60                         = 0.123821 + 0.2;				//    0.045310    2.73  0.00628 ** 
		carpassenger_age_51to60                      = 0.379313 - 0.1;				//    0.063662    5.96  2.5e-09 ***
		cycling_age_51to60                          = -0.179067;				//    0.075555   -2.37  0.01779 *  
		publictransport_age_51to60                   = 0.131555;				//    0.067905    1.94  0.05270 .  
		
		cardriver_age_61to70                         = 0.043212	+ 0.3;				//    0.073546    0.59  0.55683    
		carpassenger_age_61to70                      = 0.534171 - 0.2;				//    0.094954    5.63  1.8e-08 ***
		cycling_age_61to70                          = -0.072971;				//    0.126955   -0.57  0.56544    
		publictransport_age_61to70                   = 0.284048;				//    0.104177    2.73  0.00640 ** 
		
		cardriver_age_71plus                         = 0.190858 + 0.2;				//    0.086242    2.21  0.02689 *  
		carpassenger_age_71plus                      = 0.702001 - 0.1;				//    0.109566    6.41  1.5e-10 ***
		cycling_age_71plus                           = 0.235179;				//    0.156807    1.50  0.13367    
		publictransport_age_71plus                   = 0.509671;				//    0.121070    4.21  2.6e-05 ***
		
		cardriver_purpose_business                   = 0.426680;				//    0.147887    2.89  0.00391 ** 
		carpassenger_purpose_business                = 0.848164;				//    0.214344    3.96  7.6e-05 ***
		cycling_purpose_business                    = -0.146032;				//    0.230642   -0.63  0.52663    
		publictransport_purpose_business            = -0.207613;				//    0.195333   -1.06  0.28784    
		cardriver_purpose_education                 = -1.170002;				//    0.117617   -9.95  < 2e-16 ***
		carpassenger_purpose_education              = -0.110776;				//    0.101889   -1.09  0.27694    
		cycling_purpose_education                   = -0.688868;				//    0.105277   -6.54  6.0e-11 ***
		publictransport_purpose_education           = -0.284921;				//    0.094653   -3.01  0.00261 ** 
		cardriver_purpose_service                    = 0.563007;				//    0.066938    8.41  < 2e-16 ***
		carpassenger_purpose_service                 = 0.972419;				//    0.105235    9.24  < 2e-16 ***
		cycling_purpose_service                     = -0.904971;				//    0.116532   -7.77  8.2e-15 ***
		publictransport_purpose_service             = -1.804269;				//    0.122193  -14.77  < 2e-16 ***
		cardriver_purpose_private_business          = -0.171168;				//    0.064935   -2.64  0.00839 ** 
		carpassenger_purpose_private_business        = 1.213156;				//    0.094603   12.82  < 2e-16 ***
		cycling_purpose_private_business            = -0.707540;				//    0.104989   -6.74  1.6e-11 ***
		publictransport_purpose_private_business    = -1.092388;				//    0.087080  -12.54  < 2e-16 ***
		
		cardriver_purpose_visit                     = -0.377785;				//    0.073700   -5.13  3.0e-07 ***
		carpassenger_purpose_visit                   = 1.002966 + 0.1;				//    0.099250   10.11  < 2e-16 ***
		cycling_purpose_visit                       = -1.116992;				//    0.122171   -9.14  < 2e-16 ***
		publictransport_purpose_visit               = -1.546975;				//    0.105284  -14.69  < 2e-16 ***
		
		cardriver_purpose_shopping_grocery          = -0.163975;				//    0.061535   -2.66  0.00770 ** 
		carpassenger_purpose_shopping_grocery        = 1.160410;				//    0.092881   12.49  < 2e-16 ***
		cycling_purpose_shopping_grocery            = -0.324953;				//    0.094258   -3.45  0.00057 ***
		publictransport_purpose_shopping_grocery    = -1.963814;				//    0.093496  -21.00  < 2e-16 ***
		cardriver_purpose_shopping_other             = 0.096364;				//    0.085471    1.13  0.25956    
		carpassenger_purpose_shopping_other          = 1.680237;				//    0.110297   15.23  < 2e-16 ***
		cycling_purpose_shopping_other              = -0.529382;				//    0.141145   -3.75  0.00018 ***
		publictransport_purpose_shopping_other      = -0.625672;				//    0.107150   -5.84  5.2e-09 ***
		cardriver_purpose_leisure_indoors           = -0.975469;				//    0.080951  -12.05  < 2e-16 ***
		carpassenger_purpose_leisure_indoors         = 1.365875;				//    0.102593   13.31  < 2e-16 ***
		cycling_purpose_leisure_indoors             = -1.648378;				//    0.154134  -10.69  < 2e-16 ***
		publictransport_purpose_leisure_indoors     = -0.805053;				//    0.095992   -8.39  < 2e-16 ***
		cardriver_purpose_leisure_outdoors           = 0.276751;				//    0.074287    3.73  0.00019 ***
		carpassenger_purpose_leisure_outdoors        = 1.854214;				//    0.097751   18.97  < 2e-16 ***
		cycling_purpose_leisure_outdoors            = -0.275468;				//    0.107991   -2.55  0.01075 *  
		publictransport_purpose_leisure_outdoors    = -1.178573;				//    0.101919  -11.56  < 2e-16 ***
		cardriver_purpose_leisure_other             = -0.427900;				//    0.072061   -5.94  2.9e-09 ***
		carpassenger_purpose_leisure_other           = 1.142863;				//    0.097284   11.75  < 2e-16 ***
		cycling_purpose_leisure_other               = -0.849066;				//    0.109771   -7.73  1.0e-14 ***
		publictransport_purpose_leisure_other       = -0.837022;				//    0.090826   -9.22  < 2e-16 ***
		
		cardriver_purpose_leisure_walk              = -2.481828 + 1.8;				//    0.080748  -30.74  < 2e-16 ***
		carpassenger_purpose_leisure_walk           = -0.542581	+ 2.0;				//    0.113955   -4.76  1.9e-06 ***
		cycling_purpose_leisure_walk                = -1.225735	+ 0.8;				//    0.102362  -11.97  < 2e-16 ***
		publictransport_purpose_leisure_walk        = -2.233804 + 0.8;				//    0.128240  -17.42  < 2e-16 ***
		
		cardriver_purpose_other                      = 0.005454;				//    0.438033    0.01  0.99007    
		carpassenger_purpose_other                   = 1.642237;				//    0.376211    4.37  1.3e-05 ***
		cycling_purpose_other                       = -1.761051;				//    1.059956   -1.66  0.09663 .  
		publictransport_purpose_other               = -0.325086;				//    0.581850   -0.56  0.57636    
		
		cardriver_day_Saturday                       = 0.024280;				//    0.044152    0.55  0.58238    
		carpassenger_day_Saturday                    = 0.412240 + 0.3;				//    0.051198    8.05  8.9e-16 ***
		cycling_day_Saturday                        = -0.072867;				//    0.074157   -0.98  0.32580    
		publictransport_day_Saturday                 = 0.089564;				//    0.064031    1.40  0.16188    
		walking_day_Saturday                 					= 0.0			- 0.1;
		
		cardriver_day_Sunday                        = -0.322378 - 0.1;				//    0.053925   -5.98  2.3e-09 ***
		carpassenger_day_Sunday                      = 0.034224 + 0.2;				//    0.060350    0.57  0.57065    
		cycling_day_Sunday                          = -0.294217;				//    0.088201   -3.34  0.00085 ***
		publictransport_day_Sunday                  = -0.484080;				//    0.079172   -6.11  9.7e-10 ***
		
		cardriver_num_activities                     = 0.316958;				//    0.045068    7.03  2.0e-12 ***
		carpassenger_num_activities                  = 0.361232;				//    0.050762    7.12  1.1e-12 ***
		cycling_num_activities                       = 0.141970;				//    0.072389    1.96  0.04985 *  
		publictransport_num_activities               = 0.299922;				//    0.055240    5.43  5.7e-08 ***
		cardriver_containsStrolling                 = -1.645880;				//    0.183768   -8.96  < 2e-16 ***
		carpassenger_containsStrolling              = -1.249491;				//    0.219823   -5.68  1.3e-08 ***
		cycling_containsStrolling                   = -0.747275;				//    0.287458   -2.60  0.00933 ** 
		publictransport_containsStrolling           = -1.274220;				//    0.232356   -5.48  4.2e-08 ***
		cardriver_containsVisit                      = 0.362738;				//    0.135139    2.68  0.00727 ** 
		carpassenger_containsVisit                  = -0.076400;				//    0.145064   -0.53  0.59843    
		cycling_containsVisit                        = 0.045008;				//    0.193647    0.23  0.81621    
		publictransport_containsVisit               = -0.201958;				//    0.162286   -1.24  0.21333    
		cardriver_containsPrivateB                   = 0.134349;				//    0.105779    1.27  0.20405    
		carpassenger_containsPrivateB               = -0.088240;				//    0.124341   -0.71  0.47792    
		cycling_containsPrivateB                    = -0.047898;				//    0.170185   -0.28  0.77837    
		publictransport_containsPrivateB            = -0.100364;				//    0.131292   -0.76  0.44461    
		cardriver_containsService                    = 0.125003;				//    0.185509    0.67  0.50041    
		carpassenger_containsService                = -0.061428;				//    0.236447   -0.26  0.79502    
		cycling_containsService                     = -0.552562;				//    0.267776   -2.06  0.03906 *  
		publictransport_containsService             = -1.555483;				//    0.248041   -6.27  3.6e-10 ***
		cardriver_containsLeisure                    = 0.487875;				//    0.106862    4.57  5.0e-06 ***
		carpassenger_containsLeisure                 = 0.878384;				//    0.112962    7.78  7.5e-15 ***
		cycling_containsLeisure                      = 0.024606;				//    0.168257    0.15  0.88373    
		publictransport_containsLeisure              = 0.498773;				//    0.125535    3.97  7.1e-05 ***
		cardriver_containsShopping                   = 0.213372;				//    0.088018    2.42  0.01534 *  
		carpassenger_containsShopping               = -0.044545;				//    0.105427   -0.42  0.67265    
		cycling_containsShopping                     = 0.091174;				//    0.143358    0.64  0.52478    
		publictransport_containsShopping             = 0.025287;				//    0.109359    0.23  0.81714    
		cardriver_containsBusiness                   = 0.529965;				//    0.209561    2.53  0.01144 *  
		carpassenger_containsBusiness                = 0.189094;				//    0.300744    0.63  0.52951    
		cycling_containsBusiness                     = 0.596958;				//    0.273862    2.18  0.02927 *  
		publictransport_containsBusiness             = 0.310194;				//    0.242565    1.28  0.20097    
		
		cardriver_firsttour                          = 0.423738;				//    0.053388    7.94  2.0e-15 ***
		carpassenger_firsttour                       = 0.080719;				//    0.064531    1.25  0.21099    
		cycling_firsttour                            = 0.225874;				//    0.073715    3.06  0.00218 ** 
		publictransport_firsttour                    = 0.337065;				//    0.059893    5.63  1.8e-08 ***
		
		walking_same_mode                            = 0.393756;				//    0.030545   12.89  < 2e-16 ***
		cardriver_same_mode                          = 1.025784;				//    0.027462   37.35  < 2e-16 ***
		carpassenger_same_mode                       = 1.126115;				//    0.037947   29.68  < 2e-16 ***
		cycling_same_mode                            = 2.274833;				//    0.048875   46.54  < 2e-16 ***
		publictransport_same_mode                    = 1.151092;				//    0.040087   28.71  < 2e-16 ***

		

		init();
	}

	protected void init() {
		
		super.init();

		this.parameterWalk.put("SAME_MODE_AS_BEFORE", walking_same_mode);
		this.parameterBike.put("SAME_MODE_AS_BEFORE", cycling_same_mode);
		this.parameterCar.put("SAME_MODE_AS_BEFORE", cardriver_same_mode);
		this.parameterPassenger.put("SAME_MODE_AS_BEFORE", carpassenger_same_mode);
		this.parameterPt.put("SAME_MODE_AS_BEFORE", publictransport_same_mode);
		
		this.parameterWalk.put("INITIAL_TOUR", walking_firsttour);
		this.parameterBike.put("INITIAL_TOUR", cycling_firsttour);
		this.parameterCar.put("INITIAL_TOUR", cardriver_firsttour);
		this.parameterPassenger.put("INITIAL_TOUR", carpassenger_firsttour);
		this.parameterPt.put("INITIAL_TOUR", publictransport_firsttour);
	}



}
