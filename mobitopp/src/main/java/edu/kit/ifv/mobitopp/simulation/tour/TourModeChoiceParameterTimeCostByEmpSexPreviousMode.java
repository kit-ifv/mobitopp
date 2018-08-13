package edu.kit.ifv.mobitopp.simulation.tour;


public class TourModeChoiceParameterTimeCostByEmpSexPreviousMode 
	extends TourModeChoiceParameterBaseTimeCostByEmpSex 
	implements TourModeChoiceParameter
{

	protected Double walking_prev_mode_cardriver = 0.0;
	protected Double walking_prev_mode_carpassenger = 0.0;
	protected Double walking_prev_mode_cycling = 0.0;
	protected Double walking_prev_mode_publictransport = 0.0;
	protected Double walking_prev_mode_walking = 0.0;
	
	protected Double cardriver_prev_mode_cardriver;
	protected Double cardriver_prev_mode_carpassenger;
	protected Double cardriver_prev_mode_cycling;
	protected Double cardriver_prev_mode_publictransport;
	protected Double cardriver_prev_mode_walking;
	protected Double carpassenger_prev_mode_cardriver;
	protected Double carpassenger_prev_mode_carpassenger;
	protected Double carpassenger_prev_mode_cycling;
	protected Double carpassenger_prev_mode_publictransport;
	protected Double carpassenger_prev_mode_walking;
	protected Double cycling_prev_mode_cardriver;
	protected Double cycling_prev_mode_carpassenger;
	protected Double cycling_prev_mode_cycling;
	protected Double cycling_prev_mode_publictransport;
	protected Double cycling_prev_mode_walking;
	protected Double publictransport_prev_mode_cardriver;
	protected Double publictransport_prev_mode_carpassenger;
	protected Double publictransport_prev_mode_cycling;
	protected Double publictransport_prev_mode_publictransport;
	protected Double publictransport_prev_mode_walking;
	
	protected Double walking_firsttour 		     ;
	protected Double cardriver_firsttour       ;
	protected Double carpassenger_firsttour    ;
	protected Double cycling_firsttour         ;
	protected Double publictransport_firsttour ;


	/*
Call:
mlogit(formula = mode ~ notavailable + time + time:sex. + time:employment. + 
    cost + cost:sex. + cost:employment. | sex. + employment. + 
    age. + purpose. + day. + intrazonal. + num_activities + containsStrolling. + 
    containsVisit. + containsPrivateB. + containsService. + containsLeisure. + 
    containsShopping. + containsBusiness. + firsttour. + prev_mode., 
    data = trainingdata, reflevel = "walking", method = "nr", 
    print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2876          0.3914          0.1279          0.0544          0.1388 

nr method
24 iterations, 0h:8m:47s 
g'(-H)^-1g = 4.02E-07 
gradient close to zero 

Coefficients :
                                             Estimate  Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                        0.068853    0.920162    0.07  0.94035    
carpassenger:(intercept)                    -3.856530    0.896802   -4.30  1.7e-05 ***
cycling:(intercept)                         -0.801598    0.746042   -1.07  0.28261    
publictransport:(intercept)                 -1.242684    1.215449   -1.02  0.30659    
notavailable                               -24.558803 2718.897729   -0.01  0.99279    
time                                        -0.028924    0.000974  -29.71  < 2e-16 ***
cost                                        -0.592273    0.019102  -31.01  < 2e-16 ***
time:sex.female                             -0.002969    0.001173   -2.53  0.01133 *  
time:employment.parttime                    -0.012140    0.002078   -5.84  5.2e-09 ***
time:employment.marginal                     0.001698    0.003105    0.55  0.58460    
time:employment.homekeeper                  -0.005457    0.002609   -2.09  0.03648 *  
time:employment.unemployed                  -0.005141    0.004813   -1.07  0.28541    
time:employment.retired                     -0.003375    0.001405   -2.40  0.01630 *  
time:employment.pupil                       -0.008946    0.001752   -5.11  3.3e-07 ***
time:employment.student                      0.011551    0.002500    4.62  3.8e-06 ***
time:employment.apprentice                  -0.010066    0.005679   -1.77  0.07630 .  
time:employment.other                       -0.040573    0.012210   -3.32  0.00089 ***
cost:sex.female                              0.002768    0.024631    0.11  0.91051    
cost:employment.parttime                     0.091496    0.038210    2.39  0.01664 *  
cost:employment.marginal                     0.248299    0.077753    3.19  0.00141 ** 
cost:employment.homekeeper                   0.296827    0.061958    4.79  1.7e-06 ***
cost:employment.unemployed                   0.264048    0.100701    2.62  0.00874 ** 
cost:employment.retired                      0.130596    0.031093    4.20  2.7e-05 ***
cost:employment.pupil                       -0.120705    0.046552   -2.59  0.00952 ** 
cost:employment.student                      0.222813    0.057254    3.89  1.0e-04 ***
cost:employment.apprentice                   0.079238    0.076098    1.04  0.29775    
cost:employment.other                        0.088002    0.130893    0.67  0.50138    
cardriver:sex.female                        -0.575417    0.043995  -13.08  < 2e-16 ***
carpassenger:sex.female                      0.481527    0.047893   10.05  < 2e-16 ***
cycling:sex.female                          -0.577745    0.053523  -10.79  < 2e-16 ***
publictransport:sex.female                  -0.004090    0.044232   -0.09  0.92633    
cardriver:employment.parttime               -0.074982    0.064686   -1.16  0.24639    
carpassenger:employment.parttime             0.067971    0.086080    0.79  0.42975    
cycling:employment.parttime                  0.282615    0.085008    3.32  0.00089 ***
publictransport:employment.parttime          0.044243    0.078216    0.57  0.57164    
cardriver:employment.marginal                0.117464    0.098184    1.20  0.23156    
carpassenger:employment.marginal             0.564529    0.128987    4.38  1.2e-05 ***
cycling:employment.marginal                  0.560722    0.131348    4.27  2.0e-05 ***
publictransport:employment.marginal         -0.303488    0.157440   -1.93  0.05390 .  
cardriver:employment.homekeeper             -0.124672    0.077220   -1.61  0.10642    
carpassenger:employment.homekeeper           0.240833    0.100830    2.39  0.01692 *  
cycling:employment.homekeeper                0.259024    0.115688    2.24  0.02516 *  
publictransport:employment.homekeeper       -0.310149    0.133660   -2.32  0.02032 *  
cardriver:employment.unemployed             -0.397318    0.171331   -2.32  0.02039 *  
carpassenger:employment.unemployed           0.127284    0.196382    0.65  0.51689    
cycling:employment.unemployed                0.040000    0.236243    0.17  0.86555    
publictransport:employment.unemployed        0.218285    0.193410    1.13  0.25906    
cardriver:employment.retired                -0.373601    0.083485   -4.48  7.6e-06 ***
carpassenger:employment.retired             -0.121065    0.108718   -1.11  0.26547    
cycling:employment.retired                  -0.275721    0.142904   -1.93  0.05368 .  
publictransport:employment.retired           0.077303    0.115464    0.67  0.50318    
cardriver:employment.pupil                   0.060984    0.174200    0.35  0.72628    
carpassenger:employment.pupil                0.479382    0.189435    2.53  0.01139 *  
cycling:employment.pupil                     0.456147    0.251463    1.81  0.06968 .  
publictransport:employment.pupil             0.099224    0.182929    0.54  0.58753    
cardriver:employment.student                 0.050009    0.167428    0.30  0.76518    
carpassenger:employment.student              0.320126    0.204288    1.57  0.11711    
cycling:employment.student                   0.614662    0.228998    2.68  0.00727 ** 
publictransport:employment.student           0.065357    0.167163    0.39  0.69582    
cardriver:employment.apprentice              0.230018    0.241147    0.95  0.34016    
carpassenger:employment.apprentice           0.268395    0.263628    1.02  0.30864    
cycling:employment.apprentice               -0.360440    0.359591   -1.00  0.31617    
publictransport:employment.apprentice        0.311980    0.219504    1.42  0.15523    
cardriver:employment.other                  -0.732216    0.229390   -3.19  0.00141 ** 
carpassenger:employment.other               -0.238893    0.290462   -0.82  0.41082    
cycling:employment.other                    -0.055138    0.287807   -0.19  0.84807    
publictransport:employment.other             0.713424    0.276867    2.58  0.00997 ** 
cardriver:age.06to09                        -0.621397 9351.150758    0.00  0.99995    
carpassenger:age.06to09                      1.192100    0.195128    6.11  1.0e-09 ***
cycling:age.06to09                          -0.968031    0.269119   -3.60  0.00032 ***
publictransport:age.06to09                  -0.704434    0.211778   -3.33  0.00088 ***
cardriver:age.10to17                        -0.511215    0.303897   -1.68  0.09253 .  
carpassenger:age.10to17                      1.178070    0.189432    6.22  5.0e-10 ***
cycling:age.10to17                           0.313407    0.252129    1.24  0.21385    
publictransport:age.10to17                   0.612112    0.184585    3.32  0.00091 ***
cardriver:age.18to25                         0.456868    0.125387    3.64  0.00027 ***
carpassenger:age.18to25                      1.005257    0.148878    6.75  1.5e-11 ***
cycling:age.18to25                          -0.031569    0.201990   -0.16  0.87581    
publictransport:age.18to25                   0.578898    0.145359    3.98  6.8e-05 ***
cardriver:age.26to35                        -0.213979    0.060389   -3.54  0.00040 ***
carpassenger:age.26to35                     -0.075515    0.086955   -0.87  0.38516    
cycling:age.26to35                          -0.399175    0.106393   -3.75  0.00018 ***
publictransport:age.26to35                   0.058408    0.084052    0.69  0.48712    
cardriver:age.51to60                         0.130035    0.045362    2.87  0.00415 ** 
carpassenger:age.51to60                      0.382150    0.063660    6.00  1.9e-09 ***
cycling:age.51to60                          -0.177686    0.075776   -2.34  0.01903 *  
publictransport:age.51to60                   0.129398    0.068126    1.90  0.05751 .  
cardriver:age.61to70                         0.053420    0.073694    0.72  0.46852    
carpassenger:age.61to70                      0.537967    0.095086    5.66  1.5e-08 ***
cycling:age.61to70                          -0.074780    0.127367   -0.59  0.55712    
publictransport:age.61to70                   0.279905    0.104409    2.68  0.00734 ** 
cardriver:age.71plus                         0.204727    0.086395    2.37  0.01780 *  
carpassenger:age.71plus                      0.715406    0.109716    6.52  7.0e-11 ***
cycling:age.71plus                           0.238217    0.157281    1.51  0.12988    
publictransport:age.71plus                   0.506102    0.121261    4.17  3.0e-05 ***
cardriver:purpose.business                   0.436379    0.148001    2.95  0.00319 ** 
carpassenger:purpose.business                0.842629    0.214311    3.93  8.4e-05 ***
cycling:purpose.business                    -0.139199    0.231351   -0.60  0.54739    
publictransport:purpose.business            -0.205614    0.195779   -1.05  0.29361    
cardriver:purpose.education                 -1.214730    0.118076  -10.29  < 2e-16 ***
carpassenger:purpose.education              -0.130291    0.102053   -1.28  0.20171    
cycling:purpose.education                   -0.717890    0.106036   -6.77  1.3e-11 ***
publictransport:purpose.education           -0.299913    0.094544   -3.17  0.00151 ** 
cardriver:purpose.service                    0.576754    0.066996    8.61  < 2e-16 ***
carpassenger:purpose.service                 0.959071    0.105230    9.11  < 2e-16 ***
cycling:purpose.service                     -0.900330    0.117055   -7.69  1.4e-14 ***
publictransport:purpose.service             -1.791015    0.122147  -14.66  < 2e-16 ***
cardriver:purpose.private_business          -0.146750    0.065039   -2.26  0.02405 *  
carpassenger:purpose.private_business        1.215070    0.094553   12.85  < 2e-16 ***
cycling:purpose.private_business            -0.697397    0.105388   -6.62  3.7e-11 ***
publictransport:purpose.private_business    -1.079746    0.087165  -12.39  < 2e-16 ***
cardriver:purpose.visit                     -0.354011    0.073836   -4.79  1.6e-06 ***
carpassenger:purpose.visit                   0.997066    0.099230   10.05  < 2e-16 ***
cycling:purpose.visit                       -1.112893    0.122648   -9.07  < 2e-16 ***
publictransport:purpose.visit               -1.536284    0.105362  -14.58  < 2e-16 ***
cardriver:purpose.shopping_grocery          -0.128815    0.061691   -2.09  0.03679 *  
carpassenger:purpose.shopping_grocery        1.173340    0.092900   12.63  < 2e-16 ***
cycling:purpose.shopping_grocery            -0.314423    0.094773   -3.32  0.00091 ***
publictransport:purpose.shopping_grocery    -1.942883    0.093563  -20.77  < 2e-16 ***
cardriver:purpose.shopping_other             0.122069    0.085679    1.42  0.15424    
carpassenger:purpose.shopping_other          1.689977    0.110337   15.32  < 2e-16 ***
cycling:purpose.shopping_other              -0.500212    0.141336   -3.54  0.00040 ***
publictransport:purpose.shopping_other      -0.619990    0.107273   -5.78  7.5e-09 ***
cardriver:purpose.leisure_indoors           -0.956102    0.081048  -11.80  < 2e-16 ***
carpassenger:purpose.leisure_indoors         1.360065    0.102517   13.27  < 2e-16 ***
cycling:purpose.leisure_indoors             -1.635935    0.154453  -10.59  < 2e-16 ***
publictransport:purpose.leisure_indoors     -0.788220    0.096130   -8.20  2.2e-16 ***
cardriver:purpose.leisure_outdoors           0.314900    0.074522    4.23  2.4e-05 ***
carpassenger:purpose.leisure_outdoors        1.857526    0.097815   18.99  < 2e-16 ***
cycling:purpose.leisure_outdoors            -0.245796    0.108483   -2.27  0.02347 *  
publictransport:purpose.leisure_outdoors    -1.158102    0.101966  -11.36  < 2e-16 ***
cardriver:purpose.leisure_other             -0.407620    0.072174   -5.65  1.6e-08 ***
carpassenger:purpose.leisure_other           1.142719    0.097256   11.75  < 2e-16 ***
cycling:purpose.leisure_other               -0.836120    0.110214   -7.59  3.3e-14 ***
publictransport:purpose.leisure_other       -0.824175    0.090951   -9.06  < 2e-16 ***
cardriver:purpose.leisure_walk              -2.463397    0.080828  -30.48  < 2e-16 ***
carpassenger:purpose.leisure_walk           -0.545988    0.113882   -4.79  1.6e-06 ***
cycling:purpose.leisure_walk                -1.236372    0.103110  -11.99  < 2e-16 ***
publictransport:purpose.leisure_walk        -2.202888    0.128345  -17.16  < 2e-16 ***
cardriver:purpose.other                     -0.001910    0.437927    0.00  0.99652    
carpassenger:purpose.other                   1.622196    0.378491    4.29  1.8e-05 ***
cycling:purpose.other                       -1.748573    1.061272   -1.65  0.09943 .  
publictransport:purpose.other               -0.358030    0.582546   -0.61  0.53882    
cardriver:day.Saturday                       0.023375    0.044292    0.53  0.59767    
carpassenger:day.Saturday                    0.408644    0.051305    7.96  1.6e-15 ***
cycling:day.Saturday                        -0.079593    0.074509   -1.07  0.28541    
publictransport:day.Saturday                 0.079263    0.064140    1.24  0.21654    
cardriver:day.Sunday                        -0.327869    0.054197   -6.05  1.5e-09 ***
carpassenger:day.Sunday                      0.037599    0.060553    0.62  0.53465    
cycling:day.Sunday                          -0.310332    0.088942   -3.49  0.00048 ***
publictransport:day.Sunday                  -0.510209    0.079339   -6.43  1.3e-10 ***
cardriver:intrazonal.TRUE                   -1.057522    0.035696  -29.63  < 2e-16 ***
carpassenger:intrazonal.TRUE                -0.937407    0.045762  -20.48  < 2e-16 ***
cycling:intrazonal.TRUE                      0.035268    0.049387    0.71  0.47515    
publictransport:intrazonal.TRUE             -3.086928    0.092725  -33.29  < 2e-16 ***
cardriver:num_activities                     0.315683    0.045123    7.00  2.6e-12 ***
carpassenger:num_activities                  0.357943    0.050843    7.04  1.9e-12 ***
cycling:num_activities                       0.143082    0.072443    1.98  0.04826 *  
publictransport:num_activities               0.295250    0.055311    5.34  9.4e-08 ***
cardriver:containsStrolling.TRUE            -1.651297    0.183755   -8.99  < 2e-16 ***
carpassenger:containsStrolling.TRUE         -1.248565    0.219555   -5.69  1.3e-08 ***
cycling:containsStrolling.TRUE              -0.756784    0.288073   -2.63  0.00861 ** 
publictransport:containsStrolling.TRUE      -1.273859    0.232821   -5.47  4.5e-08 ***
cardriver:containsVisit.TRUE                 0.361805    0.135328    2.67  0.00751 ** 
carpassenger:containsVisit.TRUE             -0.072709    0.145396   -0.50  0.61702    
cycling:containsVisit.TRUE                   0.046966    0.194263    0.24  0.80896    
publictransport:containsVisit.TRUE          -0.200086    0.162273   -1.23  0.21757    
cardriver:containsPrivateB.TRUE              0.135449    0.105941    1.28  0.20106    
carpassenger:containsPrivateB.TRUE          -0.082166    0.124564   -0.66  0.50949    
cycling:containsPrivateB.TRUE               -0.040503    0.170527   -0.24  0.81225    
publictransport:containsPrivateB.TRUE       -0.101990    0.131453   -0.78  0.43782    
cardriver:containsService.TRUE               0.134881    0.185633    0.73  0.46747    
carpassenger:containsService.TRUE           -0.063823    0.236880   -0.27  0.78760    
cycling:containsService.TRUE                -0.553133    0.268825   -2.06  0.03963 *  
publictransport:containsService.TRUE        -1.564110    0.248403   -6.30  3.0e-10 ***
cardriver:containsLeisure.TRUE               0.495250    0.106980    4.63  3.7e-06 ***
carpassenger:containsLeisure.TRUE            0.881868    0.113198    7.79  6.7e-15 ***
cycling:containsLeisure.TRUE                 0.042308    0.168461    0.25  0.80170    
publictransport:containsLeisure.TRUE         0.497551    0.125638    3.96  7.5e-05 ***
cardriver:containsShopping.TRUE              0.215478    0.088112    2.45  0.01446 *  
carpassenger:containsShopping.TRUE          -0.035480    0.105564   -0.34  0.73680    
cycling:containsShopping.TRUE                0.096747    0.143669    0.67  0.50069    
publictransport:containsShopping.TRUE        0.017965    0.109528    0.16  0.86971    
cardriver:containsBusiness.TRUE              0.524104    0.209339    2.50  0.01229 *  
carpassenger:containsBusiness.TRUE           0.177430    0.300839    0.59  0.55534    
cycling:containsBusiness.TRUE                0.594317    0.274161    2.17  0.03018 *  
publictransport:containsBusiness.TRUE        0.310781    0.242781    1.28  0.20051    
cardriver:firsttour.TRUE                     0.579157    0.918385    0.63  0.52829    
carpassenger:firsttour.TRUE                 -0.150346    0.893296   -0.17  0.86634    
cycling:firsttour.TRUE                      -0.649295    0.742090   -0.87  0.38160    
publictransport:firsttour.TRUE               1.500525    1.213868    1.24  0.21640    
cardriver:prev_mode.cycling                  0.366392    0.919962    0.40  0.69043    
carpassenger:prev_mode.cycling               0.112057    0.894650    0.13  0.90032    
cycling:prev_mode.cycling                    1.539177    0.740730    2.08  0.03772 *  
publictransport:prev_mode.cycling            1.341396    1.215723    1.10  0.26987    
cardriver:prev_mode.walking                 -0.165517    0.917632   -0.18  0.85686    
carpassenger:prev_mode.walking              -0.731070    0.891871   -0.82  0.41239    
cycling:prev_mode.walking                   -1.398392    0.740371   -1.89  0.05892 .  
publictransport:prev_mode.walking            0.847545    1.213317    0.70  0.48484    
cardriver:prev_mode.publictransport         -0.143715    0.918600   -0.16  0.87568    
carpassenger:prev_mode.publictransport      -0.380123    0.892701   -0.43  0.67024    
cycling:prev_mode.publictransport           -0.932473    0.741809   -1.26  0.20874    
publictransport:prev_mode.publictransport    2.176062    1.213540    1.79  0.07295 .  
cardriver:prev_mode.cardriver                1.178839    0.917453    1.28  0.19883    
carpassenger:prev_mode.cardriver            -0.101925    0.892069   -0.11  0.90903    
cycling:prev_mode.cardriver                 -0.847132    0.740766   -1.14  0.25279    
publictransport:prev_mode.cardriver          0.980603    1.213655    0.81  0.41911    
cardriver:prev_mode.carpassenger             0.081291    0.918566    0.09  0.92948    
carpassenger:prev_mode.carpassenger          0.909196    0.892115    1.02  0.30813    
cycling:prev_mode.carpassenger              -0.686794    0.741894   -0.93  0.35459    
publictransport:prev_mode.carpassenger       1.297881    1.214066    1.07  0.28505    
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -45900
McFadden R^2:  0.44 
Likelihood ratio test : chisq = 72200 (p.value = <2e-16)

	
		*/


	


	

	
	public TourModeChoiceParameterTimeCostByEmpSexPreviousMode() {
		super();
		
		
		walking																				 = 0.0 - 0.4;
		walking_intrazonal														 = 0.0 + 0.5;
		walking_day_Saturday                           = 0.0;
		walking_day_Sunday                           	 = 0.0;
		
		// curr: v4, next: v4
		
		cardriver                                    = 0.068853	+ 0.1; 					//0.920162    0.07  0.94035    
		carpassenger                                = -3.856530; 					//0.896802   -4.30  1.7e-05 ***
		cycling                                     = -0.801598 - 0.1; 					//0.746042   -1.07  0.28261    
		publictransport                             = -1.242684 + 0.6; 					//1.215449   -1.02  0.30659    
		
		cardriver_intrazonal                         = -1.057522	- 0.3; 					//0.035696  -29.63  < 2e-16 ***
		carpassenger_intrazonal                      = -0.937407; 					//0.045762  -20.48  < 2e-16 ***
		cycling_intrazonal                            = 0.035268 + 0.5; 					//0.049387    0.71  0.47515    
		publictransport_intrazonal                   = -3.086928 - 0.4; 					//0.092725  -33.29  < 2e-16 ***
		
		notavailable                                = -24.558803; 					//2718.897729   -0.01  0.99279    
		
		time                                         = -0.028924; 					//0.000974  -29.71  < 2e-16 ***
		cost                                         = -0.592273; 					//0.019102  -31.01  < 2e-16 ***
		
		time_sex_female                              = -0.002969; 					//0.001173   -2.53  0.01133 *  
		time_employment_parttime                     = -0.012140; 					//0.002078   -5.84  5.2e-09 ***
		time_employment_marginal                      = 0.001698; 					//0.003105    0.55  0.58460    
		time_employment_homekeeper                   = -0.005457; 					//0.002609   -2.09  0.03648 *  
		time_employment_unemployed                   = -0.005141; 					//0.004813   -1.07  0.28541    
		time_employment_retired                      = -0.003375; 					//0.001405   -2.40  0.01630 *  
		time_employment_pupil                        = -0.008946; 					//0.001752   -5.11  3.3e-07 ***
		time_employment_student                       = 0.011551; 					//0.002500    4.62  3.8e-06 ***
		time_employment_apprentice                   = -0.010066; 					//0.005679   -1.77  0.07630 .  
		time_employment_other                        = -0.040573; 					//0.012210   -3.32  0.00089 ***
		
		cost_sex_female                               = 0.002768; 					//0.024631    0.11  0.91051    
		cost_employment_parttime                      = 0.091496; 					//0.038210    2.39  0.01664 *  
		cost_employment_marginal                      = 0.248299; 					//0.077753    3.19  0.00141 ** 
		cost_employment_homekeeper                    = 0.296827; 					//0.061958    4.79  1.7e-06 ***
		cost_employment_unemployed                    = 0.264048; 					//0.100701    2.62  0.00874 ** 
		cost_employment_retired                       = 0.130596; 					//0.031093    4.20  2.7e-05 ***
		cost_employment_pupil                        = -0.120705; 					//0.046552   -2.59  0.00952 ** 
		cost_employment_student                       = 0.222813; 					//0.057254    3.89  1.0e-04 ***
		cost_employment_apprentice                    = 0.079238; 					//0.076098    1.04  0.29775    
		cost_employment_other                         = 0.088002; 					//0.130893    0.67  0.50138    
		
		cardriver_sex_female                         = -0.575417	+ 0.1; 					//0.043995  -13.08  < 2e-16 ***
		carpassenger_sex_female                       = 0.481527; 					//0.047893   10.05  < 2e-16 ***
		cycling_sex_female                           = -0.577745; 					//0.053523  -10.79  < 2e-16 ***
		publictransport_sex_female                   = -0.004090; 					//0.044232   -0.09  0.92633    
		
		cardriver_employment_parttime                = -0.074982 + 0.2; 					//0.064686   -1.16  0.24639    
		carpassenger_employment_parttime              = 0.067971; 					//0.086080    0.79  0.42975    
		cycling_employment_parttime                   = 0.282615; 					//0.085008    3.32  0.00089 ***
		publictransport_employment_parttime           = 0.044243; 					//0.078216    0.57  0.57164    
		
		cardriver_employment_marginal                 = 0.117464 + 0.2; 					//0.098184    1.20  0.23156    
		carpassenger_employment_marginal              = 0.564529; 					//0.128987    4.38  1.2e-05 ***
		cycling_employment_marginal                   = 0.560722; 					//0.131348    4.27  2.0e-05 ***
		publictransport_employment_marginal          = -0.303488; 					//0.157440   -1.93  0.05390 .  
		
		cardriver_employment_homekeeper              = -0.124672 + 0.2; 					//0.077220   -1.61  0.10642    
		carpassenger_employment_homekeeper            = 0.240833; 					//0.100830    2.39  0.01692 *  
		cycling_employment_homekeeper                 = 0.259024; 					//0.115688    2.24  0.02516 *  
		publictransport_employment_homekeeper        = -0.310149; 					//0.133660   -2.32  0.02032 *  
		
		cardriver_employment_unemployed              = -0.397318; 					//0.171331   -2.32  0.02039 *  
		carpassenger_employment_unemployed            = 0.127284; 					//0.196382    0.65  0.51689    
		cycling_employment_unemployed                 = 0.040000; 					//0.236243    0.17  0.86555    
		publictransport_employment_unemployed         = 0.218285; 					//0.193410    1.13  0.25906    
		cardriver_employment_retired                 = -0.373601; 					//0.083485   -4.48  7.6e-06 ***
		carpassenger_employment_retired              = -0.121065; 					//0.108718   -1.11  0.26547    
		cycling_employment_retired                   = -0.275721; 					//0.142904   -1.93  0.05368 .  
		publictransport_employment_retired            = 0.077303; 					//0.115464    0.67  0.50318    
		cardriver_employment_pupil                    = 0.060984; 					//0.174200    0.35  0.72628    
		carpassenger_employment_pupil                 = 0.479382; 					//0.189435    2.53  0.01139 *  
		cycling_employment_pupil                      = 0.456147; 					//0.251463    1.81  0.06968 .  
		publictransport_employment_pupil              = 0.099224; 					//0.182929    0.54  0.58753    
		cardriver_employment_student                  = 0.050009; 					//0.167428    0.30  0.76518    
		carpassenger_employment_student               = 0.320126; 					//0.204288    1.57  0.11711    
		cycling_employment_student                    = 0.614662; 					//0.228998    2.68  0.00727 ** 
		publictransport_employment_student            = 0.065357; 					//0.167163    0.39  0.69582    
		cardriver_employment_apprentice               = 0.230018; 					//0.241147    0.95  0.34016    
		carpassenger_employment_apprentice            = 0.268395; 					//0.263628    1.02  0.30864    
		cycling_employment_apprentice                = -0.360440; 					//0.359591   -1.00  0.31617    
		publictransport_employment_apprentice         = 0.311980; 					//0.219504    1.42  0.15523    
		cardriver_employment_other                   = -0.732216; 					//0.229390   -3.19  0.00141 ** 
		carpassenger_employment_other                = -0.238893; 					//0.290462   -0.82  0.41082    
		cycling_employment_other                     = -0.055138; 					//0.287807   -0.19  0.84807    
		publictransport_employment_other              = 0.713424; 					//0.276867    2.58  0.00997 ** 
		
		cardriver_age_06to09                         = -0.621397; 					//9351.150758    0.00  0.99995    
		carpassenger_age_06to09                       = 1.192100; 					//0.195128    6.11  1.0e-09 ***
		cycling_age_06to09                           = -0.968031; 					//0.269119   -3.60  0.00032 ***
		publictransport_age_06to09                   = -0.704434; 					//0.211778   -3.33  0.00088 ***
		cardriver_age_10to17                         = -0.511215; 					//0.303897   -1.68  0.09253 .  
		carpassenger_age_10to17                       = 1.178070; 					//0.189432    6.22  5.0e-10 ***
		cycling_age_10to17                            = 0.313407; 					//0.252129    1.24  0.21385    
		publictransport_age_10to17                    = 0.612112; 					//0.184585    3.32  0.00091 ***
		cardriver_age_18to25                          = 0.456868; 					//0.125387    3.64  0.00027 ***
		carpassenger_age_18to25                       = 1.005257; 					//0.148878    6.75  1.5e-11 ***
		cycling_age_18to25                           = -0.031569; 					//0.201990   -0.16  0.87581    
		publictransport_age_18to25                    = 0.578898; 					//0.145359    3.98  6.8e-05 ***
		cardriver_age_26to35                         = -0.213979; 					//0.060389   -3.54  0.00040 ***
		carpassenger_age_26to35                      = -0.075515; 					//0.086955   -0.87  0.38516    
		cycling_age_26to35                           = -0.399175; 					//0.106393   -3.75  0.00018 ***
		publictransport_age_26to35                    = 0.058408; 					//0.084052    0.69  0.48712    
		cardriver_age_51to60                          = 0.130035; 					//0.045362    2.87  0.00415 ** 
		carpassenger_age_51to60                       = 0.382150; 					//0.063660    6.00  1.9e-09 ***
		cycling_age_51to60                           = -0.177686; 					//0.075776   -2.34  0.01903 *  
		publictransport_age_51to60                    = 0.129398; 					//0.068126    1.90  0.05751 .  
		cardriver_age_61to70                          = 0.053420; 					//0.073694    0.72  0.46852    
		carpassenger_age_61to70                       = 0.537967; 					//0.095086    5.66  1.5e-08 ***
		cycling_age_61to70                           = -0.074780; 					//0.127367   -0.59  0.55712    
		publictransport_age_61to70                    = 0.279905; 					//0.104409    2.68  0.00734 ** 
		cardriver_age_71plus                          = 0.204727; 					//0.086395    2.37  0.01780 *  
		carpassenger_age_71plus                       = 0.715406; 					//0.109716    6.52  7.0e-11 ***
		cycling_age_71plus                            = 0.238217; 					//0.157281    1.51  0.12988    
		publictransport_age_71plus                    = 0.506102; 					//0.121261    4.17  3.0e-05 ***
		
		cardriver_purpose_business                    = 0.436379; 					//0.148001    2.95  0.00319 ** 
		carpassenger_purpose_business                 = 0.842629; 					//0.214311    3.93  8.4e-05 ***
		cycling_purpose_business                     = -0.139199; 					//0.231351   -0.60  0.54739    
		publictransport_purpose_business             = -0.205614; 					//0.195779   -1.05  0.29361    
		cardriver_purpose_education                  = -1.214730; 					//0.118076  -10.29  < 2e-16 ***
		carpassenger_purpose_education               = -0.130291; 					//0.102053   -1.28  0.20171    
		cycling_purpose_education                    = -0.717890; 					//0.106036   -6.77  1.3e-11 ***
		publictransport_purpose_education            = -0.299913; 					//0.094544   -3.17  0.00151 ** 
		cardriver_purpose_service                     = 0.576754; 					//0.066996    8.61  < 2e-16 ***
		carpassenger_purpose_service                  = 0.959071; 					//0.105230    9.11  < 2e-16 ***
		cycling_purpose_service                      = -0.900330; 					//0.117055   -7.69  1.4e-14 ***
		publictransport_purpose_service              = -1.791015; 					//0.122147  -14.66  < 2e-16 ***
		cardriver_purpose_private_business           = -0.146750; 					//0.065039   -2.26  0.02405 *  
		carpassenger_purpose_private_business         = 1.215070; 					//0.094553   12.85  < 2e-16 ***
		cycling_purpose_private_business             = -0.697397; 					//0.105388   -6.62  3.7e-11 ***
		publictransport_purpose_private_business     = -1.079746; 					//0.087165  -12.39  < 2e-16 ***
		cardriver_purpose_visit                      = -0.354011; 					//0.073836   -4.79  1.6e-06 ***
		carpassenger_purpose_visit                    = 0.997066; 					//0.099230   10.05  < 2e-16 ***
		cycling_purpose_visit                        = -1.112893; 					//0.122648   -9.07  < 2e-16 ***
		publictransport_purpose_visit                = -1.536284; 					//0.105362  -14.58  < 2e-16 ***
		cardriver_purpose_shopping_grocery           = -0.128815; 					//0.061691   -2.09  0.03679 *  
		carpassenger_purpose_shopping_grocery         = 1.173340; 					//0.092900   12.63  < 2e-16 ***
		cycling_purpose_shopping_grocery             = -0.314423; 					//0.094773   -3.32  0.00091 ***
		publictransport_purpose_shopping_grocery     = -1.942883; 					//0.093563  -20.77  < 2e-16 ***
		cardriver_purpose_shopping_other              = 0.122069; 					//0.085679    1.42  0.15424    
		carpassenger_purpose_shopping_other           = 1.689977; 					//0.110337   15.32  < 2e-16 ***
		cycling_purpose_shopping_other               = -0.500212; 					//0.141336   -3.54  0.00040 ***
		publictransport_purpose_shopping_other       = -0.619990; 					//0.107273   -5.78  7.5e-09 ***
		cardriver_purpose_leisure_indoors            = -0.956102; 					//0.081048  -11.80  < 2e-16 ***
		carpassenger_purpose_leisure_indoors          = 1.360065; 					//0.102517   13.27  < 2e-16 ***
		cycling_purpose_leisure_indoors              = -1.635935; 					//0.154453  -10.59  < 2e-16 ***
		publictransport_purpose_leisure_indoors      = -0.788220; 					//0.096130   -8.20  2.2e-16 ***
		cardriver_purpose_leisure_outdoors            = 0.314900; 					//0.074522    4.23  2.4e-05 ***
		carpassenger_purpose_leisure_outdoors         = 1.857526; 					//0.097815   18.99  < 2e-16 ***
		cycling_purpose_leisure_outdoors             = -0.245796; 					//0.108483   -2.27  0.02347 *  
		publictransport_purpose_leisure_outdoors     = -1.158102; 					//0.101966  -11.36  < 2e-16 ***
		cardriver_purpose_leisure_other              = -0.407620; 					//0.072174   -5.65  1.6e-08 ***
		carpassenger_purpose_leisure_other            = 1.142719; 					//0.097256   11.75  < 2e-16 ***
		cycling_purpose_leisure_other                = -0.836120; 					//0.110214   -7.59  3.3e-14 ***
		publictransport_purpose_leisure_other        = -0.824175; 					//0.090951   -9.06  < 2e-16 ***
		
		cardriver_purpose_leisure_walk               = -2.463397 + 1.9; 					//0.080828  -30.48  < 2e-16 ***
		carpassenger_purpose_leisure_walk            = -0.545988 + 1.5; 					//0.113882   -4.79  1.6e-06 ***
		cycling_purpose_leisure_walk                 = -1.236372 + 0.7; 					//0.103110  -11.99  < 2e-16 ***
		publictransport_purpose_leisure_walk         = -2.202888 + 1.0; 					//0.128345  -17.16  < 2e-16 ***
		
		cardriver_purpose_other                      = -0.001910; 					//0.437927    0.00  0.99652    
		carpassenger_purpose_other                    = 1.622196; 					//0.378491    4.29  1.8e-05 ***
		cycling_purpose_other                        = -1.748573; 					//1.061272   -1.65  0.09943 .  
		publictransport_purpose_other                = -0.358030; 					//0.582546   -0.61  0.53882    
		
		cardriver_day_Saturday                        = 0.023375; 					//0.044292    0.53  0.59767    
		carpassenger_day_Saturday                     = 0.408644; 					//0.051305    7.96  1.6e-15 ***
		cycling_day_Saturday                         = -0.079593; 					//0.074509   -1.07  0.28541    
		publictransport_day_Saturday                  = 0.079263; 					//0.064140    1.24  0.21654    
		cardriver_day_Sunday                         = -0.327869; 					//0.054197   -6.05  1.5e-09 ***
		carpassenger_day_Sunday                       = 0.037599; 					//0.060553    0.62  0.53465    
		cycling_day_Sunday                           = -0.310332; 					//0.088942   -3.49  0.00048 ***
		publictransport_day_Sunday                   = -0.510209; 					//0.079339   -6.43  1.3e-10 ***
		
		cardriver_num_activities                      = 0.315683; 					//0.045123    7.00  2.6e-12 ***
		carpassenger_num_activities                   = 0.357943; 					//0.050843    7.04  1.9e-12 ***
		cycling_num_activities                        = 0.143082; 					//0.072443    1.98  0.04826 *  
		publictransport_num_activities                = 0.295250; 					//0.055311    5.34  9.4e-08 ***
		cardriver_containsStrolling                  = -1.651297; 					//0.183755   -8.99  < 2e-16 ***
		carpassenger_containsStrolling               = -1.248565; 					//0.219555   -5.69  1.3e-08 ***
		cycling_containsStrolling                    = -0.756784; 					//0.288073   -2.63  0.00861 ** 
		publictransport_containsStrolling            = -1.273859; 					//0.232821   -5.47  4.5e-08 ***
		cardriver_containsVisit                       = 0.361805; 					//0.135328    2.67  0.00751 ** 
		carpassenger_containsVisit                   = -0.072709; 					//0.145396   -0.50  0.61702    
		cycling_containsVisit                         = 0.046966; 					//0.194263    0.24  0.80896    
		publictransport_containsVisit                = -0.200086; 					//0.162273   -1.23  0.21757    
		cardriver_containsPrivateB                    = 0.135449; 					//0.105941    1.28  0.20106    
		carpassenger_containsPrivateB                = -0.082166; 					//0.124564   -0.66  0.50949    
		cycling_containsPrivateB                     = -0.040503; 					//0.170527   -0.24  0.81225    
		publictransport_containsPrivateB             = -0.101990; 					//0.131453   -0.78  0.43782    
		cardriver_containsService                     = 0.134881; 					//0.185633    0.73  0.46747    
		carpassenger_containsService                 = -0.063823; 					//0.236880   -0.27  0.78760    
		cycling_containsService                      = -0.553133; 					//0.268825   -2.06  0.03963 *  
		publictransport_containsService              = -1.564110; 					//0.248403   -6.30  3.0e-10 ***
		cardriver_containsLeisure                     = 0.495250; 					//0.106980    4.63  3.7e-06 ***
		carpassenger_containsLeisure                  = 0.881868; 					//0.113198    7.79  6.7e-15 ***
		cycling_containsLeisure                       = 0.042308; 					//0.168461    0.25  0.80170    
		publictransport_containsLeisure               = 0.497551; 					//0.125638    3.96  7.5e-05 ***
		cardriver_containsShopping                    = 0.215478; 					//0.088112    2.45  0.01446 *  
		carpassenger_containsShopping                = -0.035480; 					//0.105564   -0.34  0.73680    
		cycling_containsShopping                      = 0.096747; 					//0.143669    0.67  0.50069    
		publictransport_containsShopping              = 0.017965; 					//0.109528    0.16  0.86971    
		cardriver_containsBusiness                    = 0.524104; 					//0.209339    2.50  0.01229 *  
		carpassenger_containsBusiness                 = 0.177430; 					//0.300839    0.59  0.55534    
		cycling_containsBusiness                      = 0.594317; 					//0.274161    2.17  0.03018 *  
		publictransport_containsBusiness              = 0.310781; 					//0.242781    1.28  0.20051    
		
		walking_firsttour                           	= 0.0;
		cardriver_firsttour                           = 0.579157; 					//0.918385    0.63  0.52829    
		carpassenger_firsttour                       = -0.150346; 					//0.893296   -0.17  0.86634    
		cycling_firsttour                            = -0.649295; 					//0.742090   -0.87  0.38160    
		publictransport_firsttour                     = 1.500525; 					//1.213868    1.24  0.21640    
		
		cardriver_prev_mode_cycling                   = 0.366392; 					//0.919962    0.40  0.69043    
		carpassenger_prev_mode_cycling                = 0.112057; 					//0.894650    0.13  0.90032    
		cycling_prev_mode_cycling                     = 1.539177; 					//0.740730    2.08  0.03772 *  
		publictransport_prev_mode_cycling             = 1.341396; 					//1.215723    1.10  0.26987    
		cardriver_prev_mode_walking                  = -0.165517; 					//0.917632   -0.18  0.85686    
		carpassenger_prev_mode_walking               = -0.731070; 					//0.891871   -0.82  0.41239    
		cycling_prev_mode_walking                    = -1.398392; 					//0.740371   -1.89  0.05892 .  
		publictransport_prev_mode_walking             = 0.847545; 					//1.213317    0.70  0.48484    
		cardriver_prev_mode_publictransport          = -0.143715; 					//0.918600   -0.16  0.87568    
		carpassenger_prev_mode_publictransport       = -0.380123; 					//0.892701   -0.43  0.67024    
		cycling_prev_mode_publictransport            = -0.932473; 					//0.741809   -1.26  0.20874    
		publictransport_prev_mode_publictransport     = 2.176062; 					//1.213540    1.79  0.07295 .  
		cardriver_prev_mode_cardriver                 = 1.178839; 					//0.917453    1.28  0.19883    
		carpassenger_prev_mode_cardriver             = -0.101925; 					//0.892069   -0.11  0.90903    
		cycling_prev_mode_cardriver                  = -0.847132; 					//0.740766   -1.14  0.25279    
		publictransport_prev_mode_cardriver           = 0.980603; 					//1.213655    0.81  0.41911    
		cardriver_prev_mode_carpassenger              = 0.081291; 					//0.918566    0.09  0.92948    
		carpassenger_prev_mode_carpassenger           = 0.909196; 					//0.892115    1.02  0.30813    
		cycling_prev_mode_carpassenger               = -0.686794; 					//0.741894   -0.93  0.35459    
		publictransport_prev_mode_carpassenger        = 1.297881; 					//1.214066    1.07  0.28505    

			
		init();
	}

	
	protected void init() {
		
		assert super.time_age_06to09 != null;
		
		super.init();

		
		this.parameterWalk.put("PREVIOUS_MODE_WALK", 			walking_prev_mode_walking);
		this.parameterWalk.put("PREVIOUS_MODE_BIKE", 			walking_prev_mode_cycling);
		this.parameterWalk.put("PREVIOUS_MODE_CAR", 			walking_prev_mode_cardriver);
		this.parameterWalk.put("PREVIOUS_MODE_PASSENGER", walking_prev_mode_carpassenger);
		this.parameterWalk.put("PREVIOUS_MODE_PT", 				walking_prev_mode_publictransport);
		this.parameterWalk.put("INITIAL_TOUR", 							walking_firsttour);
		
		this.parameterBike.put("PREVIOUS_MODE_WALK", 				cycling_prev_mode_walking);
		this.parameterBike.put("PREVIOUS_MODE_BIKE", 				cycling_prev_mode_cycling);
		this.parameterBike.put("PREVIOUS_MODE_CAR", 				cycling_prev_mode_cardriver);
		this.parameterBike.put("PREVIOUS_MODE_PASSENGER", 	cycling_prev_mode_carpassenger);
		this.parameterBike.put("PREVIOUS_MODE_PT", 					cycling_prev_mode_publictransport);
		this.parameterBike.put("INITIAL_TOUR", 								cycling_firsttour);
		
		this.parameterCar.put("PREVIOUS_MODE_WALK", 				cardriver_prev_mode_walking);
		this.parameterCar.put("PREVIOUS_MODE_BIKE", 				cardriver_prev_mode_cycling);
		this.parameterCar.put("PREVIOUS_MODE_CAR", 					cardriver_prev_mode_cardriver);
		this.parameterCar.put("PREVIOUS_MODE_PASSENGER", 		cardriver_prev_mode_carpassenger);
		this.parameterCar.put("PREVIOUS_MODE_PT", 					cardriver_prev_mode_publictransport);
		this.parameterCar.put("INITIAL_TOUR", 								cardriver_firsttour);
		
		this.parameterPassenger.put("PREVIOUS_MODE_WALK", 			carpassenger_prev_mode_walking);
		this.parameterPassenger.put("PREVIOUS_MODE_BIKE", 			carpassenger_prev_mode_cycling);
		this.parameterPassenger.put("PREVIOUS_MODE_CAR", 				carpassenger_prev_mode_cardriver);
		this.parameterPassenger.put("PREVIOUS_MODE_PASSENGER", 	carpassenger_prev_mode_carpassenger);
		this.parameterPassenger.put("PREVIOUS_MODE_PT", 				carpassenger_prev_mode_publictransport);
		this.parameterPassenger.put("INITIAL_TOUR", 							carpassenger_firsttour);
		
		this.parameterPt.put("PREVIOUS_MODE_WALK", 			publictransport_prev_mode_walking);
		this.parameterPt.put("PREVIOUS_MODE_BIKE", 			publictransport_prev_mode_cycling);
		this.parameterPt.put("PREVIOUS_MODE_CAR", 			publictransport_prev_mode_cardriver);
		this.parameterPt.put("PREVIOUS_MODE_PASSENGER", publictransport_prev_mode_carpassenger);
		this.parameterPt.put("PREVIOUS_MODE_PT", 				publictransport_prev_mode_publictransport);
		this.parameterPt.put("INITIAL_TOUR", 							publictransport_firsttour);
	}





}
