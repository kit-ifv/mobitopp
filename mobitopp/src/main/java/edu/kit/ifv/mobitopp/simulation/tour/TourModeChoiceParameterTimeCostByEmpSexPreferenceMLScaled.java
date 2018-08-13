package edu.kit.ifv.mobitopp.simulation.tour;


public class TourModeChoiceParameterTimeCostByEmpSexPreferenceMLScaled 
	extends TourModeChoiceParameterBaseTimeCostByEmpSex 
	implements TourModeChoiceParameter
{

	protected final Double	walking_preference					= 1.0 ;
	protected final Double	cardriver_preference				= 1.0 ;
	protected final Double	carpassenger_preference			= 1.0 ;
	protected final Double	cycling_preference					= 1.0 ;
	protected final Double	publictransport_preference	= 1.0 ;
	
	protected final Double	scaling	= 1.5 ;


	/*


Model estimated on: Mo Apr 16 09:27:05 2018 

Call:
gmnl(formula = formulas_ml$empsex, data = ds.TRAINING, model = "mixl", 
    ranp = c(`carpassenger:(intercept)` = "n", `cycling:(intercept)` = "n", 
        `publictransport:(intercept)` = "n", `cardriver:(intercept)` = "n"), 
    R = 50, panel = TRUE, reflevel = "walking", method = "bfgs")

Frequencies of categories:

        walking       cardriver    carpassenger         cycling publictransport 
         0.2876          0.3914          0.1279          0.0544          0.1388 

The estimation took: 63h:20m:36s 

Coefficients:
                                           Estimate Std. Error z-value Pr(>|z|)    
notavailable                              -23.45103  438.19441   -0.05  0.95732    
time                                       -0.02873    0.00113  -25.41  < 2e-16 ***
cost                                       -0.69125    0.03225  -21.43  < 2e-16 ***
time:sex.female                            -0.00190    0.00134   -1.42  0.15680    
time:employment.parttime                   -0.01378    0.00235   -5.86  4.6e-09 ***
time:employment.marginal                    0.00257    0.00350    0.74  0.46226    
time:employment.homekeeper                 -0.00503    0.00287   -1.75  0.07991 .  
time:employment.unemployed                 -0.00286    0.00513   -0.56  0.57748    
time:employment.retired                    -0.00565    0.00163   -3.47  0.00053 ***
time:employment.pupil                      -0.01443    0.00213   -6.78  1.2e-11 ***
time:employment.student                     0.01333    0.00257    5.19  2.1e-07 ***
time:employment.apprentice                 -0.01804    0.00791   -2.28  0.02263 *  
time:employment.other                      -0.07811    0.01760   -4.44  9.1e-06 ***
cost:sex.female                            -0.02227    0.04126   -0.54  0.58941    
cost:employment.parttime                    0.20959    0.05987    3.50  0.00046 ***
cost:employment.marginal                    0.41063    0.11435    3.59  0.00033 ***
cost:employment.homekeeper                  0.34985    0.08877    3.94  8.1e-05 ***
cost:employment.unemployed                  0.18780    0.15142    1.24  0.21489    
cost:employment.retired                     0.33110    0.05134    6.45  1.1e-10 ***
cost:employment.pupil                       0.10832    0.07034    1.54  0.12360    
cost:employment.student                     0.23196    0.10253    2.26  0.02368 *  
cost:employment.apprentice                 -0.10668    0.12171   -0.88  0.38074    
cost:employment.other                       0.17381    0.19920    0.87  0.38293    
cardriver:sex.female                       -0.90219    0.08611  -10.48  < 2e-16 ***
carpassenger:sex.female                     0.89794    0.08461   10.61  < 2e-16 ***
cycling:sex.female                         -1.20155    0.12262   -9.80  < 2e-16 ***
publictransport:sex.female                  0.18077    0.08135    2.22  0.02628 *  
cardriver:employment.parttime              -0.13102    0.12153   -1.08  0.28099    
carpassenger:employment.parttime            0.13981    0.13993    1.00  0.31773    
cycling:employment.parttime                 1.01569    0.16985    5.98  2.2e-09 ***
publictransport:employment.parttime        -0.28484    0.13435   -2.12  0.03399 *  
cardriver:employment.marginal              -0.08234    0.18513   -0.44  0.65646    
carpassenger:employment.marginal            0.87145    0.20430    4.27  2.0e-05 ***
cycling:employment.marginal                 1.14744    0.23467    4.89  1.0e-06 ***
publictransport:employment.marginal        -0.93867    0.26352   -3.56  0.00037 ***
cardriver:employment.homekeeper            -0.33496    0.15598   -2.15  0.03176 *  
carpassenger:employment.homekeeper          0.30400    0.17122    1.78  0.07582 .  
cycling:employment.homekeeper               1.26606    0.26793    4.73  2.3e-06 ***
publictransport:employment.homekeeper      -0.73599    0.20685   -3.56  0.00037 ***
cardriver:employment.unemployed            -0.63340    0.29485   -2.15  0.03170 *  
carpassenger:employment.unemployed          0.05225    0.33513    0.16  0.87610    
cycling:employment.unemployed              -0.07726    0.42327   -0.18  0.85516    
publictransport:employment.unemployed       0.10167    0.32506    0.31  0.75445    
cardriver:employment.retired               -0.83690    0.16404   -5.10  3.4e-07 ***
carpassenger:employment.retired             0.02235    0.18714    0.12  0.90493    
cycling:employment.retired                  0.27078    0.27939    0.97  0.33246    
publictransport:employment.retired         -0.38773    0.18876   -2.05  0.03997 *  
cardriver:employment.pupil                 -0.37357    0.29397   -1.27  0.20381    
carpassenger:employment.pupil               0.36108    0.29453    1.23  0.22021    
cycling:employment.pupil                    0.32238    0.46765    0.69  0.49060    
publictransport:employment.pupil            0.31979    0.29103    1.10  0.27184    
cardriver:employment.student               -0.19420    0.28468   -0.68  0.49513    
carpassenger:employment.student             0.29293    0.33603    0.87  0.38336    
cycling:employment.student                  0.09300    0.55995    0.17  0.86810    
publictransport:employment.student          0.50818    0.26927    1.89  0.05913 .  
cardriver:employment.apprentice             0.34800    0.40757    0.85  0.39320    
carpassenger:employment.apprentice         -0.17295    0.43127   -0.40  0.68841    
cycling:employment.apprentice              -1.06744    0.72552   -1.47  0.14122    
publictransport:employment.apprentice       1.26578    0.35931    3.52  0.00043 ***
cardriver:employment.other                 -2.11786    0.41147   -5.15  2.6e-07 ***
carpassenger:employment.other              -0.89930    0.57557   -1.56  0.11818    
cycling:employment.other                    0.66821    0.44838    1.49  0.13615    
publictransport:employment.other            0.80914    0.45910    1.76  0.07799 .  
cardriver:age.06to09                       -1.14277 1795.83173    0.00  0.99949    
carpassenger:age.06to09                     2.07201    0.32222    6.43  1.3e-10 ***
cycling:age.06to09                         -0.41538    0.51960   -0.80  0.42404    
publictransport:age.06to09                 -1.76485    0.34133   -5.17  2.3e-07 ***
cardriver:age.10to17                       -1.00683    0.55774   -1.81  0.07104 .  
carpassenger:age.10to17                     1.89556    0.30296    6.26  3.9e-10 ***
cycling:age.10to17                          1.46758    0.48273    3.04  0.00236 ** 
publictransport:age.10to17                  0.83650    0.29955    2.79  0.00523 ** 
cardriver:age.18to25                        0.37504    0.21067    1.78  0.07504 .  
carpassenger:age.18to25                     1.48487    0.22911    6.48  9.1e-11 ***
cycling:age.18to25                          0.51747    0.40169    1.29  0.19767    
publictransport:age.18to25                  0.87009    0.21988    3.96  7.6e-05 ***
cardriver:age.26to35                       -0.27181    0.12748   -2.13  0.03299 *  
carpassenger:age.26to35                    -0.27407    0.14994   -1.83  0.06757 .  
cycling:age.26to35                         -0.34351    0.21780   -1.58  0.11476    
publictransport:age.26to35                  0.31668    0.14437    2.19  0.02827 *  
cardriver:age.51to60                        0.09284    0.09233    1.01  0.31467    
carpassenger:age.51to60                     0.32217    0.11389    2.83  0.00467 ** 
cycling:age.51to60                         -0.17842    0.14619   -1.22  0.22227    
publictransport:age.51to60                  0.24066    0.11520    2.09  0.03670 *  
cardriver:age.61to70                        0.00372    0.14767    0.03  0.97990    
carpassenger:age.61to70                     0.39485    0.16772    2.35  0.01857 *  
cycling:age.61to70                         -0.37175    0.24463   -1.52  0.12861    
publictransport:age.61to70                  0.52879    0.16845    3.14  0.00169 ** 
cardriver:age.71plus                        0.05382    0.18392    0.29  0.76980    
carpassenger:age.71plus                     0.50279    0.20209    2.49  0.01285 *  
cycling:age.71plus                         -0.28199    0.35277   -0.80  0.42408    
publictransport:age.71plus                  0.78551    0.20430    3.84  0.00012 ***
cardriver:purpose.business                  0.45208    0.18898    2.39  0.01675 *  
carpassenger:purpose.business               0.99944    0.26750    3.74  0.00019 ***
cycling:purpose.business                   -0.38556    0.28783   -1.34  0.18039    
publictransport:purpose.business           -0.50268    0.25155   -2.00  0.04568 *  
cardriver:purpose.education                -1.58438    0.15605  -10.15  < 2e-16 ***
carpassenger:purpose.education             -0.16547    0.12657   -1.31  0.19109    
cycling:purpose.education                  -1.01979    0.13863   -7.36  1.9e-13 ***
publictransport:purpose.education          -0.37537    0.12029   -3.12  0.00180 ** 
cardriver:purpose.service                   0.84762    0.09026    9.39  < 2e-16 ***
carpassenger:purpose.service                1.37317    0.13117   10.47  < 2e-16 ***
cycling:purpose.service                    -1.29021    0.15344   -8.41  < 2e-16 ***
publictransport:purpose.service            -2.28110    0.15337  -14.87  < 2e-16 ***
cardriver:purpose.private_business         -0.06877    0.08378   -0.82  0.41171    
carpassenger:purpose.private_business       1.51188    0.11679   12.95  < 2e-16 ***
cycling:purpose.private_business           -1.02734    0.13565   -7.57  3.6e-14 ***
publictransport:purpose.private_business   -1.45443    0.10992  -13.23  < 2e-16 ***
cardriver:purpose.visit                    -0.36729    0.09430   -3.89  9.8e-05 ***
carpassenger:purpose.visit                  1.35704    0.12171   11.15  < 2e-16 ***
cycling:purpose.visit                      -1.32613    0.15263   -8.69  < 2e-16 ***
publictransport:purpose.visit              -1.97561    0.13047  -15.14  < 2e-16 ***
cardriver:purpose.shopping_grocery          0.04419    0.08024    0.55  0.58178    
carpassenger:purpose.shopping_grocery       1.48860    0.11499   12.95  < 2e-16 ***
cycling:purpose.shopping_grocery           -0.50424    0.12445   -4.05  5.1e-05 ***
publictransport:purpose.shopping_grocery   -2.51002    0.11809  -21.26  < 2e-16 ***
cardriver:purpose.shopping_other            0.22437    0.10678    2.10  0.03561 *  
carpassenger:purpose.shopping_other         2.15026    0.13487   15.94  < 2e-16 ***
cycling:purpose.shopping_other             -0.83407    0.17586   -4.74  2.1e-06 ***
publictransport:purpose.shopping_other     -0.87984    0.13155   -6.69  2.3e-11 ***
cardriver:purpose.leisure_indoors          -1.08655    0.10142  -10.71  < 2e-16 ***
carpassenger:purpose.leisure_indoors        1.91877    0.12591   15.24  < 2e-16 ***
cycling:purpose.leisure_indoors            -2.19888    0.18598  -11.82  < 2e-16 ***
publictransport:purpose.leisure_indoors    -0.99945    0.11832   -8.45  < 2e-16 ***
cardriver:purpose.leisure_outdoors          0.45021    0.09488    4.75  2.1e-06 ***
carpassenger:purpose.leisure_outdoors       2.36856    0.12044   19.67  < 2e-16 ***
cycling:purpose.leisure_outdoors           -0.48837    0.13847   -3.53  0.00042 ***
publictransport:purpose.leisure_outdoors   -1.56011    0.12553  -12.43  < 2e-16 ***
cardriver:purpose.leisure_other            -0.47897    0.09277   -5.16  2.4e-07 ***
carpassenger:purpose.leisure_other          1.54822    0.11947   12.96  < 2e-16 ***
cycling:purpose.leisure_other              -1.19487    0.14085   -8.48  < 2e-16 ***
publictransport:purpose.leisure_other      -1.15562    0.11390  -10.15  < 2e-16 ***
cardriver:purpose.leisure_walk             -2.92575    0.10518  -27.82  < 2e-16 ***
carpassenger:purpose.leisure_walk          -0.47612    0.13987   -3.40  0.00066 ***
cycling:purpose.leisure_walk               -1.56453    0.13929  -11.23  < 2e-16 ***
publictransport:purpose.leisure_walk       -2.53273    0.15964  -15.87  < 2e-16 ***
cardriver:purpose.other                    -0.25395    0.59475   -0.43  0.66939    
carpassenger:purpose.other                  1.85786    0.50961    3.65  0.00027 ***
cycling:purpose.other                      -2.91288    1.22762   -2.37  0.01765 *  
publictransport:purpose.other              -0.77447    0.73685   -1.05  0.29323    
cardriver:day.Saturday                     -0.01886    0.05290   -0.36  0.72148    
carpassenger:day.Saturday                   0.70481    0.05991   11.77  < 2e-16 ***
cycling:day.Saturday                       -0.23559    0.08815   -2.67  0.00753 ** 
publictransport:day.Saturday               -0.11593    0.07468   -1.55  0.12059    
cardriver:day.Sunday                       -0.53668    0.06481   -8.28  2.2e-16 ***
carpassenger:day.Sunday                     0.34791    0.07072    4.92  8.7e-07 ***
cycling:day.Sunday                         -0.70721    0.10567   -6.69  2.2e-11 ***
publictransport:day.Sunday                 -0.86919    0.09341   -9.31  < 2e-16 ***
cardriver:intrazonal.TRUE                  -1.82384    0.05227  -34.89  < 2e-16 ***
carpassenger:intrazonal.TRUE               -1.64235    0.06271  -26.19  < 2e-16 ***
cycling:intrazonal.TRUE                     0.15240    0.07342    2.08  0.03793 *  
publictransport:intrazonal.TRUE            -3.75229    0.11165  -33.61  < 2e-16 ***
cardriver:num_activities                    0.35783    0.05402    6.62  3.5e-11 ***
carpassenger:num_activities                 0.43309    0.05971    7.25  4.1e-13 ***
cycling:num_activities                      0.10203    0.08870    1.15  0.25002    
publictransport:num_activities              0.30399    0.06679    4.55  5.3e-06 ***
cardriver:containsStrolling.TRUE           -2.16474    0.22005   -9.84  < 2e-16 ***
carpassenger:containsStrolling.TRUE        -1.54947    0.26016   -5.96  2.6e-09 ***
cycling:containsStrolling.TRUE             -0.82139    0.37481   -2.19  0.02841 *  
publictransport:containsStrolling.TRUE     -1.45783    0.28032   -5.20  2.0e-07 ***
cardriver:containsVisit.TRUE                0.29854    0.16377    1.82  0.06831 .  
carpassenger:containsVisit.TRUE            -0.17147    0.17239   -0.99  0.31990    
cycling:containsVisit.TRUE                 -0.07468    0.24078   -0.31  0.75645    
publictransport:containsVisit.TRUE         -0.31104    0.19613   -1.59  0.11278    
cardriver:containsPrivateB.TRUE             0.24399    0.12778    1.91  0.05620 .  
carpassenger:containsPrivateB.TRUE         -0.05186    0.14805   -0.35  0.72613    
cycling:containsPrivateB.TRUE               0.01848    0.20964    0.09  0.92975    
publictransport:containsPrivateB.TRUE      -0.10177    0.15755   -0.65  0.51830    
cardriver:containsService.TRUE             -0.07218    0.21853   -0.33  0.74117    
carpassenger:containsService.TRUE          -0.35609    0.27555   -1.29  0.19626    
cycling:containsService.TRUE               -1.15255    0.33698   -3.42  0.00063 ***
publictransport:containsService.TRUE       -2.40125    0.30535   -7.86  3.8e-15 ***
cardriver:containsLeisure.TRUE              0.65219    0.12899    5.06  4.3e-07 ***
carpassenger:containsLeisure.TRUE           1.08988    0.13471    8.09  6.7e-16 ***
cycling:containsLeisure.TRUE                0.03228    0.20533    0.16  0.87508    
publictransport:containsLeisure.TRUE        0.52742    0.15094    3.49  0.00048 ***
cardriver:containsShopping.TRUE             0.37205    0.10658    3.49  0.00048 ***
carpassenger:containsShopping.TRUE         -0.05578    0.12550   -0.44  0.65672    
cycling:containsShopping.TRUE               0.17258    0.17733    0.97  0.33045    
publictransport:containsShopping.TRUE      -0.03843    0.13226   -0.29  0.77137    
cardriver:containsBusiness.TRUE             0.70762    0.25087    2.82  0.00479 ** 
carpassenger:containsBusiness.TRUE          0.30816    0.34690    0.89  0.37437    
cycling:containsBusiness.TRUE               0.61344    0.33870    1.81  0.07012 .  
publictransport:containsBusiness.TRUE       0.35273    0.28815    1.22  0.22091    
cardriver:(intercept)                       1.35489    0.11478   11.80  < 2e-16 ***
carpassenger:(intercept)                   -5.25419    0.15425  -34.06  < 2e-16 ***
cycling:(intercept)                        -3.17427    0.17724  -17.91  < 2e-16 ***
publictransport:(intercept)                -0.17270    0.12922   -1.34  0.18138    
sd.cardriver:(intercept)                    1.79411    0.03524   50.92  < 2e-16 ***
sd.carpassenger:(intercept)                 1.90536    0.04115   46.30  < 2e-16 ***
sd.cycling:(intercept)                      2.81035    0.06461   43.50  < 2e-16 ***
sd.publictransport:(intercept)              1.62396    0.04541   35.76  < 2e-16 ***
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Optimization of log-likelihood by BFGS maximization
Log Likelihood: -41900
Number of observations: 57729
Number of iterations: 691
Exit of MLE: successful convergence 
Simulation based on 50 draws

		*/


	
	
	public TourModeChoiceParameterTimeCostByEmpSexPreferenceMLScaled() {
	
		walking																				 = 0.0 + 0.0;
		walking_intrazonal														 = 0.0 + 0.65;
		walking_day_Saturday                           = 0.0;
		walking_day_Sunday                           	 = 0.0;
	
		
		// curr: v12, next: v12

		cardriver                                    = 1.35489	+ 1.8;			//    0.11478   11.80  < 2e-16 ***
		carpassenger                                = -5.25419	- 0.5;			//    0.15425  -34.06  < 2e-16 ***
		cycling                                     = -3.17427	- 1.2;			//    0.17724  -17.91  < 2e-16 ***
		publictransport                             = -0.17270	+ 1.4;			//    0.12922   -1.34  0.18138    
		
		cardriver_intrazonal                        = -1.82384 	- 1.6;			//    0.05227  -34.89  < 2e-16 ***
		carpassenger_intrazonal                     = -1.64235 	- 0.3;			//    0.06271  -26.19  < 2e-16 ***
		cycling_intrazonal                           = 0.15240	+ 0.5;			//    0.07342    2.08  0.03793 *  
		publictransport_intrazonal                  = -3.75229 	- 1.4;			//    0.11165  -33.61  < 2e-16 ***
		
		notavailable                               = -23.45103;			//  438.19441   -0.05  0.95732    
		
		time                                        = -0.02873;			//    0.00113  -25.41  < 2e-16 ***
		cost                                        = -0.69125;			//    0.03225  -21.43  < 2e-16 ***
		
		time_sex_female                             = -0.00190;			//    0.00134   -1.42  0.15680    
		time_employment_parttime                    = -0.01378;			//    0.00235   -5.86  4.6e-09 ***
		time_employment_marginal                     = 0.00257;			//    0.00350    0.74  0.46226    
		time_employment_homekeeper                  = -0.00503;			//    0.00287   -1.75  0.07991 .  
		time_employment_unemployed                  = -0.00286;			//    0.00513   -0.56  0.57748    
		time_employment_retired                     = -0.00565;			//    0.00163   -3.47  0.00053 ***
		time_employment_pupil                       = -0.01443;			//    0.00213   -6.78  1.2e-11 ***
		time_employment_student                      = 0.01333;			//    0.00257    5.19  2.1e-07 ***
		time_employment_apprentice                  = -0.01804;			//    0.00791   -2.28  0.02263 *  
		time_employment_other                       = -0.07811;			//    0.01760   -4.44  9.1e-06 ***
		
		cost_sex_female                             = -0.02227;			//    0.04126   -0.54  0.58941    
		cost_employment_parttime                     = 0.20959;			//    0.05987    3.50  0.00046 ***
		cost_employment_marginal                     = 0.41063;			//    0.11435    3.59  0.00033 ***
		cost_employment_homekeeper                   = 0.34985;			//    0.08877    3.94  8.1e-05 ***
		cost_employment_unemployed                   = 0.18780;			//    0.15142    1.24  0.21489    
		cost_employment_retired                      = 0.33110;			//    0.05134    6.45  1.1e-10 ***
		cost_employment_pupil                        = 0.10832;			//    0.07034    1.54  0.12360    
		cost_employment_student                      = 0.23196;			//    0.10253    2.26  0.02368 *  
		cost_employment_apprentice                  = -0.10668;			//    0.12171   -0.88  0.38074    
		cost_employment_other                        = 0.17381;			//    0.19920    0.87  0.38293    
		
		cardriver_sex_female                        = -0.90219 + 0.0;			//    0.08611  -10.48  < 2e-16 ***
		carpassenger_sex_female                      = 0.89794;			//    0.08461   10.61  < 2e-16 ***
		cycling_sex_female                          = -1.20155;			//    0.12262   -9.80  < 2e-16 ***
		publictransport_sex_female                   = 0.18077;			//    0.08135    2.22  0.02628 *  
		
		cardriver_employment_parttime               = -0.13102	+ 0.5;			//    0.12153   -1.08  0.28099    
		carpassenger_employment_parttime             = 0.13981;			//    0.13993    1.00  0.31773    
		cycling_employment_parttime                  = 1.01569;			//    0.16985    5.98  2.2e-09 ***
		publictransport_employment_parttime         = -0.28484;			//    0.13435   -2.12  0.03399 *  
		
		cardriver_employment_marginal               = -0.08234;			//    0.18513   -0.44  0.65646    
		carpassenger_employment_marginal             = 0.87145;			//    0.20430    4.27  2.0e-05 ***
		cycling_employment_marginal                  = 1.14744;			//    0.23467    4.89  1.0e-06 ***
		publictransport_employment_marginal         = -0.93867;			//    0.26352   -3.56  0.00037 ***
		
		cardriver_employment_homekeeper             = -0.33496	+ 0.3;			//    0.15598   -2.15  0.03176 *  
		carpassenger_employment_homekeeper           = 0.30400;			//    0.17122    1.78  0.07582 .  
		cycling_employment_homekeeper                = 1.26606;			//    0.26793    4.73  2.3e-06 ***
		publictransport_employment_homekeeper       = -0.73599;			//    0.20685   -3.56  0.00037 ***
		
		cardriver_employment_unemployed             = -0.63340;			//    0.29485   -2.15  0.03170 *  
		carpassenger_employment_unemployed           = 0.05225;			//    0.33513    0.16  0.87610    
		cycling_employment_unemployed               = -0.07726;			//    0.42327   -0.18  0.85516    
		publictransport_employment_unemployed        = 0.10167;			//    0.32506    0.31  0.75445    
		
		cardriver_employment_retired                = -0.83690 - 0.2;			//    0.16404   -5.10  3.4e-07 ***
		carpassenger_employment_retired              = 0.02235;			//    0.18714    0.12  0.90493    
		cycling_employment_retired                   = 0.27078;			//    0.27939    0.97  0.33246    
		publictransport_employment_retired          = -0.38773;			//    0.18876   -2.05  0.03997 *  
		
		cardriver_employment_pupil                  = -0.37357;			//    0.29397   -1.27  0.20381    
		carpassenger_employment_pupil                = 0.36108	+ 0.2;			//    0.29453    1.23  0.22021    
		cycling_employment_pupil                     = 0.32238;			//    0.46765    0.69  0.49060    
		publictransport_employment_pupil             = 0.31979;			//    0.29103    1.10  0.27184    
		
		cardriver_employment_student                = -0.19420;			//    0.28468   -0.68  0.49513    
		carpassenger_employment_student              = 0.29293;			//    0.33603    0.87  0.38336    
		cycling_employment_student                   = 0.09300;			//    0.55995    0.17  0.86810    
		publictransport_employment_student           = 0.50818;			//    0.26927    1.89  0.05913 .  
		
		cardriver_employment_apprentice              = 0.34800 + 0.5;			//    0.40757    0.85  0.39320    
		carpassenger_employment_apprentice          = -0.17295 + 0.3;			//    0.43127   -0.40  0.68841    
		cycling_employment_apprentice               = -1.06744 + 0.2;			//    0.72552   -1.47  0.14122    
		publictransport_employment_apprentice        = 1.26578 - 0.5;			//    0.35931    3.52  0.00043 ***
		
		cardriver_employment_other                  = -2.11786;			//    0.41147   -5.15  2.6e-07 ***
		carpassenger_employment_other               = -0.89930;			//    0.57557   -1.56  0.11818    
		cycling_employment_other                     = 0.66821;			//    0.44838    1.49  0.13615    
		publictransport_employment_other             = 0.80914;			//    0.45910    1.76  0.07799 .  
		
		cardriver_age_06to09                        = -1.14277;			// 1795.83173    0.00  0.99949    
		carpassenger_age_06to09                      = 2.07201;			//    0.32222    6.43  1.3e-10 ***
		cycling_age_06to09                          = -0.41538;			//    0.51960   -0.80  0.42404    
		publictransport_age_06to09                  = -1.76485;			//    0.34133   -5.17  2.3e-07 ***
		cardriver_age_10to17                        = -1.00683;			//    0.55774   -1.81  0.07104 .  
		carpassenger_age_10to17                      = 1.89556;			//    0.30296    6.26  3.9e-10 ***
		cycling_age_10to17                           = 1.46758;			//    0.48273    3.04  0.00236 ** 
		publictransport_age_10to17                   = 0.83650;			//    0.29955    2.79  0.00523 ** 
		
		cardriver_age_18to25                         = 0.37504 - 0.2;			//    0.21067    1.78  0.07504 .  
		carpassenger_age_18to25                      = 1.48487;			//    0.22911    6.48  9.1e-11 ***
		cycling_age_18to25                           = 0.51747	+ 0.1;			//    0.40169    1.29  0.19767    
		publictransport_age_18to25                   = 0.87009;			//    0.21988    3.96  7.6e-05 ***
		
		cardriver_age_26to35                        = -0.27181 - 0.2;			//    0.12748   -2.13  0.03299 *  
		carpassenger_age_26to35                     = -0.27407;			//    0.14994   -1.83  0.06757 .  
		cycling_age_26to35                          = -0.34351;			//    0.21780   -1.58  0.11476    
		publictransport_age_26to35                   = 0.31668 + 0.1;			//    0.14437    2.19  0.02827 *  
		
		cardriver_age_51to60                         = 0.09284 + 0.2;			//    0.09233    1.01  0.31467    
		carpassenger_age_51to60                      = 0.32217;			//    0.11389    2.83  0.00467 ** 
		cycling_age_51to60                          = -0.17842;			//    0.14619   -1.22  0.22227    
		publictransport_age_51to60                   = 0.24066;			//    0.11520    2.09  0.03670 *  
		
		cardriver_age_61to70                         = 0.00372 + 0.2;			//    0.14767    0.03  0.97990    
		carpassenger_age_61to70                      = 0.39485;			//    0.16772    2.35  0.01857 *  
		cycling_age_61to70                          = -0.37175;			//    0.24463   -1.52  0.12861    
		publictransport_age_61to70                   = 0.52879;			//    0.16845    3.14  0.00169 ** 
		
		cardriver_age_71plus                         = 0.05382 + 0.2;			//    0.18392    0.29  0.76980    
		carpassenger_age_71plus                      = 0.50279;			//    0.20209    2.49  0.01285 *  
		cycling_age_71plus                          = -0.28199;			//    0.35277   -0.80  0.42408    
		publictransport_age_71plus                   = 0.78551;			//    0.20430    3.84  0.00012 ***
		
		cardriver_purpose_business                   = 0.45208;			//    0.18898    2.39  0.01675 *  
		carpassenger_purpose_business                = 0.99944;			//    0.26750    3.74  0.00019 ***
		cycling_purpose_business                    = -0.38556;			//    0.28783   -1.34  0.18039    
		publictransport_purpose_business            = -0.50268;			//    0.25155   -2.00  0.04568 *  
		cardriver_purpose_education                 = -1.58438;			//    0.15605  -10.15  < 2e-16 ***
		carpassenger_purpose_education              = -0.16547;			//    0.12657   -1.31  0.19109    
		cycling_purpose_education                   = -1.01979;			//    0.13863   -7.36  1.9e-13 ***
		publictransport_purpose_education           = -0.37537;			//    0.12029   -3.12  0.00180 ** 
		
		cardriver_purpose_service                    = 0.84762 + 0.3;			//    0.09026    9.39  < 2e-16 ***
		carpassenger_purpose_service                 = 1.37317;			//    0.13117   10.47  < 2e-16 ***
		cycling_purpose_service                     = -1.29021;			//    0.15344   -8.41  < 2e-16 ***
		publictransport_purpose_service             = -2.28110;			//    0.15337  -14.87  < 2e-16 ***
		
		cardriver_purpose_private_business          = -0.06877;			//    0.08378   -0.82  0.41171    
		carpassenger_purpose_private_business        = 1.51188	+ 0.3;			//    0.11679   12.95  < 2e-16 ***
		cycling_purpose_private_business            = -1.02734;			//    0.13565   -7.57  3.6e-14 ***
		publictransport_purpose_private_business    = -1.45443;			//    0.10992  -13.23  < 2e-16 ***
		
		cardriver_purpose_visit                     = -0.36729;			//    0.09430   -3.89  9.8e-05 ***
		carpassenger_purpose_visit                   = 1.35704	+ 0.7;			//    0.12171   11.15  < 2e-16 ***
		cycling_purpose_visit                       = -1.32613;			//    0.15263   -8.69  < 2e-16 ***
		publictransport_purpose_visit               = -1.97561;			//    0.13047  -15.14  < 2e-16 ***
		
		cardriver_purpose_shopping_grocery           = 0.04419;			//    0.08024    0.55  0.58178    
		carpassenger_purpose_shopping_grocery        = 1.48860	+ 0.3;			//    0.11499   12.95  < 2e-16 ***
		cycling_purpose_shopping_grocery            = -0.50424	- 0.2;			//    0.12445   -4.05  5.1e-05 ***
		publictransport_purpose_shopping_grocery    = -2.51002;			//    0.11809  -21.26  < 2e-16 ***
		
		cardriver_purpose_shopping_other             = 0.22437;			//    0.10678    2.10  0.03561 *  
		carpassenger_purpose_shopping_other          = 2.15026	+ 0.5;			//    0.13487   15.94  < 2e-16 ***
		cycling_purpose_shopping_other              = -0.83407;			//    0.17586   -4.74  2.1e-06 ***
		publictransport_purpose_shopping_other      = -0.87984;			//    0.13155   -6.69  2.3e-11 ***
		
		cardriver_purpose_leisure_indoors           = -1.08655 - 0.2;			//    0.10142  -10.71  < 2e-16 ***
		carpassenger_purpose_leisure_indoors         = 1.91877;			//    0.12591   15.24  < 2e-16 ***
		cycling_purpose_leisure_indoors             = -2.19888;			//    0.18598  -11.82  < 2e-16 ***
		publictransport_purpose_leisure_indoors     = -0.99945;			//    0.11832   -8.45  < 2e-16 ***
		walking_purpose_leisure_indoors      				= 	0.0			+ 0.1;			
		
		cardriver_purpose_leisure_outdoors           = 0.45021 - 0.1;			//    0.09488    4.75  2.1e-06 ***
		carpassenger_purpose_leisure_outdoors        = 2.36856;			//    0.12044   19.67  < 2e-16 ***
		cycling_purpose_leisure_outdoors            = -0.48837;			//    0.13847   -3.53  0.00042 ***
		publictransport_purpose_leisure_outdoors    = -1.56011;			//    0.12553  -12.43  < 2e-16 ***
		
		cardriver_purpose_leisure_other             = -0.47897;			//    0.09277   -5.16  2.4e-07 ***
		carpassenger_purpose_leisure_other           = 1.54822;			//    0.11947   12.96  < 2e-16 ***
		cycling_purpose_leisure_other               = -1.19487;			//    0.14085   -8.48  < 2e-16 ***
		publictransport_purpose_leisure_other       = -1.15562	- 0.2;			//    0.11390  -10.15  < 2e-16 ***
		walking_purpose_leisure_other       				= 	0.0			+ 0.1;			
		
		cardriver_purpose_leisure_walk              = -2.92575	+ 0.8;			//    0.10518  -27.82  < 2e-16 ***
		carpassenger_purpose_leisure_walk           = -0.47612	+ 1.6;			//    0.13987   -3.40  0.00066 ***
		cycling_purpose_leisure_walk                = -1.56453	+ 0.2;			//    0.13929  -11.23  < 2e-16 ***
		publictransport_purpose_leisure_walk        = -2.53273	+ 0.8;			//    0.15964  -15.87  < 2e-16 ***
		
		cardriver_purpose_other                     = -0.25395;			//    0.59475   -0.43  0.66939    
		carpassenger_purpose_other                   = 1.85786;			//    0.50961    3.65  0.00027 ***
		cycling_purpose_other                       = -2.91288;			//    1.22762   -2.37  0.01765 *  
		publictransport_purpose_other               = -0.77447;			//    0.73685   -1.05  0.29323    
		
		cardriver_day_Saturday                      = -0.01886;			//    0.05290   -0.36  0.72148    
		carpassenger_day_Saturday                    = 0.70481 + 0.8;			//    0.05991   11.77  < 2e-16 ***
		cycling_day_Saturday                        = -0.23559 + 0.2;			//    0.08815   -2.67  0.00753 ** 
		publictransport_day_Saturday                = -0.11593 + 0.2;			//    0.07468   -1.55  0.12059    
		
		cardriver_day_Sunday                        = -0.53668;			//    0.06481   -8.28  2.2e-16 ***
		carpassenger_day_Sunday                      = 0.34791 + 1.2;			//    0.07072    4.92  8.7e-07 ***
		cycling_day_Sunday                          = -0.70721 + 0.2;			//    0.10567   -6.69  2.2e-11 ***
		publictransport_day_Sunday                  = -0.86919 + 0.2;			//    0.09341   -9.31  < 2e-16 ***
		
		cardriver_num_activities                     = 0.35783;			//    0.05402    6.62  3.5e-11 ***
		carpassenger_num_activities                  = 0.43309;			//    0.05971    7.25  4.1e-13 ***
		cycling_num_activities                       = 0.10203;			//    0.08870    1.15  0.25002    
		publictransport_num_activities               = 0.30399;			//    0.06679    4.55  5.3e-06 ***
		
		cardriver_containsStrolling                 = -2.16474;			//    0.22005   -9.84  < 2e-16 ***
		carpassenger_containsStrolling              = -1.54947;			//    0.26016   -5.96  2.6e-09 ***
		cycling_containsStrolling                   = -0.82139;			//    0.37481   -2.19  0.02841 *  
		publictransport_containsStrolling           = -1.45783;			//    0.28032   -5.20  2.0e-07 ***
		cardriver_containsVisit                      = 0.29854;			//    0.16377    1.82  0.06831 .  
		carpassenger_containsVisit                  = -0.17147;			//    0.17239   -0.99  0.31990    
		cycling_containsVisit                       = -0.07468;			//    0.24078   -0.31  0.75645    
		publictransport_containsVisit               = -0.31104;			//    0.19613   -1.59  0.11278    
		cardriver_containsPrivateB                   = 0.24399;			//    0.12778    1.91  0.05620 .  
		carpassenger_containsPrivateB               = -0.05186;			//    0.14805   -0.35  0.72613    
		cycling_containsPrivateB                     = 0.01848;			//    0.20964    0.09  0.92975    
		publictransport_containsPrivateB            = -0.10177;			//    0.15755   -0.65  0.51830    
		cardriver_containsService                   = -0.07218;			//    0.21853   -0.33  0.74117    
		carpassenger_containsService                = -0.35609;			//    0.27555   -1.29  0.19626    
		cycling_containsService                     = -1.15255;			//    0.33698   -3.42  0.00063 ***
		publictransport_containsService             = -2.40125;			//    0.30535   -7.86  3.8e-15 ***
		cardriver_containsLeisure                    = 0.65219;			//    0.12899    5.06  4.3e-07 ***
		carpassenger_containsLeisure                 = 1.08988;			//    0.13471    8.09  6.7e-16 ***
		cycling_containsLeisure                      = 0.03228;			//    0.20533    0.16  0.87508    
		publictransport_containsLeisure              = 0.52742;			//    0.15094    3.49  0.00048 ***
		cardriver_containsShopping                   = 0.37205;			//    0.10658    3.49  0.00048 ***
		carpassenger_containsShopping               = -0.05578;			//    0.12550   -0.44  0.65672    
		cycling_containsShopping                     = 0.17258;			//    0.17733    0.97  0.33045    
		publictransport_containsShopping            = -0.03843;			//    0.13226   -0.29  0.77137    
		cardriver_containsBusiness                   = 0.70762;			//    0.25087    2.82  0.00479 ** 
		carpassenger_containsBusiness                = 0.30816;			//    0.34690    0.89  0.37437    
		cycling_containsBusiness                     = 0.61344;			//    0.33870    1.81  0.07012 .  
		publictransport_containsBusiness             = 0.35273;			//    0.28815    1.22  0.22091    
		
	/*
		sd.cardriver:(intercept)                     = 1.79411;			//    0.03524   50.92  < 2e-16 ***
		sd.carpassenger:(intercept)                  = 1.90536;			//    0.04115   46.30  < 2e-16 ***
		sd.cycling:(intercept)                       = 2.81035;			//    0.06461   43.50  < 2e-16 ***
		sd.publictransport:(intercept)               = 1.62396;			//    0.04541   35.76  < 2e-16 ***
		*/
		
		
		
		

		init();
	}

	protected void init() {
		
		super.init();

		this.parameterWalk.put("PREFERENCE", scaling*walking_preference);
		this.parameterBike.put("PREFERENCE", scaling*cycling_preference);
		this.parameterCar.put("PREFERENCE", scaling*cardriver_preference);
		this.parameterPassenger.put("PREFERENCE", scaling*carpassenger_preference);
		this.parameterPt.put("PREFERENCE", scaling*publictransport_preference);
	}



}
