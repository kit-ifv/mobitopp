package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;

public class WithinTourModeChoiceParameterOnlyFlexibleModesAgeSexPurposeTourmodeWithintour 
	extends WithinTourModeChoiceParameterOnlyFlexibleModesBase 
	implements WithinTourModeChoiceParameter 
{
	
	protected Double walking                                	= 0.0 ;
	protected Double walking_istourmode                     	= 0.0 ;
	protected Double walking_withintour        						   	= 0.0 ;
	protected Double walking_istourmode_withintour           	= 0.0 ;
	
	protected Double walking_sex_female 											= 0.0 ;
	
	protected Double walking_age_06to09 						       	= 0.0 ;
	protected Double walking_age_10to17 						       	= 0.0 ;
	protected Double walking_age_18to25 						       	= 0.0 ;
	protected Double walking_age_26to35 						       	= 0.0 ;
	protected Double walking_age_36to50 						       	= 0.0 ;
	protected Double walking_age_51to60 						       	= 0.0 ;
	protected Double walking_age_61to70 						       	= 0.0 ;
	protected Double walking_age_71plus 						       	= 0.0 ;
	
	protected Double time_age_36to50 						          	= 0.0 ;
	protected Double cost_age_36to50 						          	= 0.0 ;
	
	protected Double publictransport_age_36to50 						= 0.0 ;
	protected Double carpassenger_age_36to50 						   	= 0.0 ;
	


	/*
Call:
mlogit(formula = mode ~ time + time:sex. + time:age. + cost + 
    cost:sex. + cost:age. | sex. + age. + purpose. + istourmode * 
    withintour., data = trainingdata, reflevel = "walking", method = "nr", 
    print.level = 0)

Frequencies of alternatives:
        walking    carpassenger publictransport 
          0.498           0.252           0.251 

nr method
7 iterations, 0h:0m:36s 
g'(-H)^-1g = 2.51E-07 
gradient close to zero 

Coefficients :
                                                Estimate Std. Error t-value Pr(>|t|)    
carpassenger:(intercept)                       -3.563764   0.291970  -12.21  < 2e-16 ***
publictransport:(intercept)                    -2.499502   0.278962   -8.96  < 2e-16 ***
time                                           -0.026580   0.000966  -27.52  < 2e-16 ***
cost                                           -0.234218   0.035269   -6.64  3.1e-11 ***
time:sex.female                                 0.001271   0.000762    1.67  0.09509 .  
time:age.06to09                                 0.006682   0.001672    4.00  6.4e-05 ***
time:age.10to17                                -0.001862   0.001341   -1.39  0.16505    
time:age.18to25                                 0.008169   0.001410    5.80  6.8e-09 ***
time:age.26to35                                -0.009940   0.001888   -5.26  1.4e-07 ***
time:age.51to60                                 0.008028   0.001232    6.52  7.1e-11 ***
time:age.61to70                                 0.007377   0.001209    6.10  1.1e-09 ***
time:age.71plus                                 0.002433   0.001539    1.58  0.11388    
cost:sex.female                                 0.043584   0.029122    1.50  0.13449    
cost:age.06to09                                 0.176072   0.074775    2.35  0.01854 *  
cost:age.10to17                                -0.075177   0.050891   -1.48  0.13962    
cost:age.18to25                                 0.035566   0.053889    0.66  0.50926    
cost:age.26to35                                 0.213633   0.058315    3.66  0.00025 ***
cost:age.51to60                                 0.127763   0.047436    2.69  0.00707 ** 
cost:age.61to70                                -0.008522   0.048040   -0.18  0.85920    
cost:age.71plus                                -0.044232   0.057429   -0.77  0.44118    
carpassenger:sex.female                         0.362410   0.038710    9.36  < 2e-16 ***
publictransport:sex.female                      0.050370   0.037631    1.34  0.18073    
carpassenger:age.06to09                         1.051657   0.069515   15.13  < 2e-16 ***
publictransport:age.06to09                     -0.461712   0.116386   -3.97  7.3e-05 ***
carpassenger:age.10to17                         0.901639   0.060741   14.84  < 2e-16 ***
publictransport:age.10to17                      0.643287   0.057535   11.18  < 2e-16 ***
carpassenger:age.18to25                         1.104650   0.084698   13.04  < 2e-16 ***
publictransport:age.18to25                      0.734742   0.073356   10.02  < 2e-16 ***
carpassenger:age.26to35                        -0.221630   0.089262   -2.48  0.01303 *  
publictransport:age.26to35                     -0.230845   0.076384   -3.02  0.00251 ** 
carpassenger:age.51to60                         0.409833   0.068960    5.94  2.8e-09 ***
publictransport:age.51to60                      0.031619   0.069153    0.46  0.64750    
carpassenger:age.61to70                         0.400658   0.063275    6.33  2.4e-10 ***
publictransport:age.61to70                      0.143918   0.067103    2.14  0.03197 *  
carpassenger:age.71plus                         0.369634   0.071544    5.17  2.4e-07 ***
publictransport:age.71plus                      0.312660   0.076816    4.07  4.7e-05 ***
carpassenger:purpose.business                   1.264344   0.234016    5.40  6.6e-08 ***
publictransport:purpose.business                0.345286   0.160748    2.15  0.03171 *  
carpassenger:purpose.education                 -0.916566   0.204723   -4.48  7.6e-06 ***
publictransport:purpose.education              -0.109752   0.127262   -0.86  0.38847    
carpassenger:purpose.service                    1.316689   0.187958    7.01  2.5e-12 ***
publictransport:purpose.service                 0.383406   0.151050    2.54  0.01114 *  
carpassenger:purpose.private_business           0.804560   0.166605    4.83  1.4e-06 ***
publictransport:purpose.private_business        0.214561   0.111248    1.93  0.05377 .  
carpassenger:purpose.visit                      1.024897   0.166076    6.17  6.8e-10 ***
publictransport:purpose.visit                   0.288592   0.119405    2.42  0.01565 *  
carpassenger:purpose.shopping_grocery           0.968511   0.157252    6.16  7.3e-10 ***
publictransport:purpose.shopping_grocery       -0.059384   0.104676   -0.57  0.57050    
carpassenger:purpose.shopping_other             0.732254   0.164075    4.46  8.1e-06 ***
publictransport:purpose.shopping_other         -0.101840   0.110879   -0.92  0.35837    
carpassenger:purpose.leisure_indoors            0.623007   0.164855    3.78  0.00016 ***
publictransport:purpose.leisure_indoors        -0.392124   0.111670   -3.51  0.00045 ***
carpassenger:purpose.leisure_outdoors           1.052911   0.188378    5.59  2.3e-08 ***
publictransport:purpose.leisure_outdoors        0.202767   0.142446    1.42  0.15460    
carpassenger:purpose.leisure_other              0.451873   0.167773    2.69  0.00707 ** 
publictransport:purpose.leisure_other           0.099158   0.114875    0.86  0.38804    
carpassenger:purpose.leisure_walk              -2.769003   0.241177  -11.48  < 2e-16 ***
publictransport:purpose.leisure_walk           -3.622060   0.362969   -9.98  < 2e-16 ***
carpassenger:purpose.other                     -0.353567   0.288762   -1.22  0.22079    
publictransport:purpose.other                  -0.584330   0.275911   -2.12  0.03419 *  
carpassenger:istourmodeTRUE                     4.465707   0.038629  115.60  < 2e-16 ***
publictransport:istourmodeTRUE                  4.127795   0.040579  101.72  < 2e-16 ***
carpassenger:withintour.TRUE                   -0.493687   0.254312   -1.94  0.05223 .  
publictransport:withintour.TRUE                 0.627112   0.266292    2.35  0.01852 *  
carpassenger:istourmodeTRUE:withintour.TRUE    -1.362657   0.068617  -19.86  < 2e-16 ***
publictransport:istourmodeTRUE:withintour.TRUE -3.046045   0.065747  -46.33  < 2e-16 ***
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -29200
McFadden R^2:  0.628 
Likelihood ratio test : chisq = 98900 (p.value = <2e-16)


		*/

	
	protected Double carpassenger                                   = -3.563764;    // se =0.291970
	protected Double publictransport                                = -2.499502;    // se =0.278962
	
	protected Double time                                           = -0.026580;    // se =0.000966
	protected Double cost                                           = -0.234218;    // se =0.035269
	
	protected Double time_sex_female                                 = 0.001271;    // se =0.000762
	protected Double cost_sex_female                                 = 0.043584;    // se =0.029122
	
	protected Double time_age_06to09                                 = 0.006682;    // se =0.001672
	protected Double time_age_10to17                                = -0.001862;    // se =0.001341
	protected Double time_age_18to25                                 = 0.008169;    // se =0.001410
	protected Double time_age_26to35                                = -0.009940;    // se =0.001888
	protected Double time_age_51to60                                 = 0.008028;    // se =0.001232
	protected Double time_age_61to70                                 = 0.007377;    // se =0.001209
	protected Double time_age_71plus                                 = 0.002433;    // se =0.001539
	
	protected Double cost_age_06to09                                 = 0.176072;    // se =0.074775
	protected Double cost_age_10to17                                = -0.075177;    // se =0.050891
	protected Double cost_age_18to25                                 = 0.035566;    // se =0.053889
	protected Double cost_age_26to35                                 = 0.213633;    // se =0.058315
	protected Double cost_age_51to60                                 = 0.127763;    // se =0.047436
	protected Double cost_age_61to70                                = -0.008522;    // se =0.048040
	protected Double cost_age_71plus                                = -0.044232;    // se =0.057429
	
	protected Double carpassenger_sex_female                         = 0.362410;    // se =0.038710
	protected Double publictransport_sex_female                      = 0.050370;    // se =0.037631
	
	protected Double carpassenger_age_06to09                         = 1.051657;    // se =0.069515
	protected Double publictransport_age_06to09                     = -0.461712;    // se =0.116386
	protected Double carpassenger_age_10to17                         = 0.901639;    // se =0.060741
	protected Double publictransport_age_10to17                      = 0.643287;    // se =0.057535
	protected Double carpassenger_age_18to25                         = 1.104650;    // se =0.084698
	protected Double publictransport_age_18to25                      = 0.734742;    // se =0.073356
	protected Double carpassenger_age_26to35                        = -0.221630;    // se =0.089262
	protected Double publictransport_age_26to35                     = -0.230845;    // se =0.076384
	protected Double carpassenger_age_51to60                         = 0.409833;    // se =0.068960
	protected Double publictransport_age_51to60                      = 0.031619;    // se =0.069153
	protected Double carpassenger_age_61to70                         = 0.400658;    // se =0.063275
	protected Double publictransport_age_61to70                      = 0.143918;    // se =0.067103
	protected Double carpassenger_age_71plus                         = 0.369634;    // se =0.071544
	protected Double publictransport_age_71plus                      = 0.312660;    // se =0.076816
	
	protected Double carpassenger_purpose_business                   = 1.264344;    // se =0.234016
	protected Double publictransport_purpose_business                = 0.345286;    // se =0.160748
	protected Double carpassenger_purpose_education                 = -0.916566;    // se =0.204723
	protected Double publictransport_purpose_education              = -0.109752;    // se =0.127262
	protected Double carpassenger_purpose_service                    = 1.316689;    // se =0.187958
	protected Double publictransport_purpose_service                 = 0.383406;    // se =0.151050
	protected Double carpassenger_purpose_private_business           = 0.804560;    // se =0.166605
	protected Double publictransport_purpose_private_business        = 0.214561;    // se =0.111248
	protected Double carpassenger_purpose_visit                      = 1.024897;    // se =0.166076
	protected Double publictransport_purpose_visit                   = 0.288592;    // se =0.119405
	protected Double carpassenger_purpose_shopping_grocery           = 0.968511;    // se =0.157252
	protected Double publictransport_purpose_shopping_grocery       = -0.059384;    // se =0.104676
	protected Double carpassenger_purpose_shopping_other             = 0.732254;    // se =0.164075
	protected Double publictransport_purpose_shopping_other         = -0.101840;    // se =0.110879
	protected Double carpassenger_purpose_leisure_indoors            = 0.623007;    // se =0.164855
	protected Double publictransport_purpose_leisure_indoors        = -0.392124;    // se =0.111670
	protected Double carpassenger_purpose_leisure_outdoors           = 1.052911;    // se =0.188378
	protected Double publictransport_purpose_leisure_outdoors        = 0.202767;    // se =0.142446
	protected Double carpassenger_purpose_leisure_other              = 0.451873;    // se =0.167773
	protected Double publictransport_purpose_leisure_other           = 0.099158;    // se =0.114875
	protected Double carpassenger_purpose_leisure_walk              = -2.769003;    // se =0.241177
	protected Double publictransport_purpose_leisure_walk           = -3.622060;    // se =0.362969
	protected Double carpassenger_purpose_other                     = -0.353567;    // se =0.288762
	protected Double publictransport_purpose_other                  = -0.584330;    // se =0.275911
	protected Double carpassenger_istourmode                         = 4.465707;    // se =0.038629
	protected Double publictransport_istourmode                      = 4.127795;    // se =0.040579
	protected Double carpassenger_withintour                        = -0.493687;    // se =0.254312
	protected Double publictransport_withintour                      = 0.627112;    // se =0.266292
	protected Double carpassenger_istourmode_withintour             = -1.362657;    // se =0.068617
	protected Double publictransport_istourmode_withintour          = -3.046045;    // se =0.065747
	

	
	public WithinTourModeChoiceParameterOnlyFlexibleModesAgeSexPurposeTourmodeWithintour() {
		init();
	}
	
	protected void init() {

		
		this.parameterGeneric.put("TIME", 					time);
		this.parameterGeneric.put("COST", 					cost);
		
		this.parameterGeneric.put("TIME:FEMALE", 					time_sex_female);
		this.parameterGeneric.put("COST:FEMALE", 					cost_sex_female);
		
		this.parameterGeneric.put("TIME:AGE_0TO9",  time_age_06to09);
		this.parameterGeneric.put("TIME:AGE_10TO17",  time_age_10to17);
		this.parameterGeneric.put("TIME:AGE_18TO25",  time_age_18to25);
		this.parameterGeneric.put("TIME:AGE_26TO35",  time_age_26to35);
		this.parameterGeneric.put("TIME:AGE_36TO50",  time_age_36to50);
		this.parameterGeneric.put("TIME:AGE_51TO60",  time_age_51to60);
		this.parameterGeneric.put("TIME:AGE_61TO70",  time_age_61to70);
		this.parameterGeneric.put("TIME:AGE_71PLUS",  time_age_71plus);
		
		this.parameterGeneric.put("COST:AGE_0TO9",  cost_age_06to09);
		this.parameterGeneric.put("COST:AGE_10TO17",  cost_age_10to17);
		this.parameterGeneric.put("COST:AGE_18TO25",  cost_age_18to25);
		this.parameterGeneric.put("COST:AGE_26TO35",  cost_age_26to35);
		this.parameterGeneric.put("COST:AGE_36TO50",  cost_age_36to50);
		this.parameterGeneric.put("COST:AGE_51TO60",  cost_age_51to60);
		this.parameterGeneric.put("COST:AGE_61TO70",  cost_age_61to70);
		this.parameterGeneric.put("COST:AGE_71PLUS",  cost_age_71plus);
		
		
		this.parameterWalk.putAll(this.parameterGeneric);
		this.parameterPassenger.putAll(this.parameterGeneric);
		this.parameterPt.putAll(this.parameterGeneric);
		
		// walk
		this.parameterWalk.put("CONST", 	walking);
		this.parameterWalk.put("ISTOURMODE", 	walking_istourmode);
		this.parameterWalk.put("WITHINTOUR", 	walking_withintour);
		this.parameterWalk.put("ISTOURMODE_WITHINTOUR", 	walking_istourmode_withintour);
		
		this.parameterWalk.put("FEMALE", walking_sex_female);
		
		this.parameterWalk.put("AGE_0TO9",  	walking_age_06to09);
		this.parameterWalk.put("AGE_10TO17",  walking_age_10to17);
		this.parameterWalk.put("AGE_18TO25",  walking_age_18to25);
		this.parameterWalk.put("AGE_26TO35",  walking_age_26to35);
		this.parameterWalk.put("AGE_36TO50",  walking_age_36to50);
		this.parameterWalk.put("AGE_51TO60",  walking_age_51to60);
		this.parameterWalk.put("AGE_61TO70",  walking_age_61to70);
		this.parameterWalk.put("AGE_71PLUS",  walking_age_71plus);
	
		// bike
	
		// passenger
		this.parameterPassenger.put("CONST", 	carpassenger);
		this.parameterPassenger.put("ISTOURMODE", 	carpassenger_istourmode);
		this.parameterPassenger.put("WITHINTOUR", 	carpassenger_withintour);
		this.parameterPassenger.put("ISTOURMODE_WITHINTOUR", 	carpassenger_istourmode_withintour);
	
		this.parameterPassenger.put("PURPOSE_BUSINESS",  				carpassenger_purpose_business);
		this.parameterPassenger.put("PURPOSE_EDUCATION",  				carpassenger_purpose_education);
		this.parameterPassenger.put("PURPOSE_HOME",  						0.0);
		this.parameterPassenger.put("PURPOSE_LEISURE_INDOOR",  	carpassenger_purpose_leisure_indoors);
		this.parameterPassenger.put("PURPOSE_LEISURE_OUTDOOR",  	carpassenger_purpose_leisure_outdoors);
		this.parameterPassenger.put("PURPOSE_LEISURE_OTHER",  		carpassenger_purpose_leisure_other);
		this.parameterPassenger.put("PURPOSE_LEISURE_WALK",  		carpassenger_purpose_leisure_walk);
		this.parameterPassenger.put("PURPOSE_OTHER",  						carpassenger_purpose_other);
		this.parameterPassenger.put("PURPOSE_PRIVATE_BUSINESS",  carpassenger_purpose_private_business);
		this.parameterPassenger.put("PURPOSE_PRIVATE_VISIT",  		carpassenger_purpose_visit);
		this.parameterPassenger.put("PURPOSE_SERVICE",  					carpassenger_purpose_service);
		this.parameterPassenger.put("PURPOSE_SHOPPING_DAILY",  	carpassenger_purpose_shopping_grocery);
		this.parameterPassenger.put("PURPOSE_SHOPPING_OTHER",  	carpassenger_purpose_shopping_other);
		this.parameterPassenger.put("PURPOSE_BUSINESS",  				carpassenger_purpose_business);
		
		this.parameterPassenger.put("FEMALE", carpassenger_sex_female);
		
		this.parameterPassenger.put("AGE_0TO9",  	carpassenger_age_06to09);
		this.parameterPassenger.put("AGE_10TO17",  carpassenger_age_10to17);
		this.parameterPassenger.put("AGE_18TO25",  carpassenger_age_18to25);
		this.parameterPassenger.put("AGE_26TO35",  carpassenger_age_26to35);
		this.parameterPassenger.put("AGE_36TO50",  carpassenger_age_36to50);
		this.parameterPassenger.put("AGE_51TO60",  carpassenger_age_51to60);
		this.parameterPassenger.put("AGE_61TO70",  carpassenger_age_61to70);
		this.parameterPassenger.put("AGE_71PLUS",  carpassenger_age_71plus);
	
	// pt
		this.parameterPt.put("CONST", 	publictransport);
		this.parameterPt.put("ISTOURMODE", 	publictransport_istourmode);
		this.parameterPt.put("WITHINTOUR", 	publictransport_withintour);
		this.parameterPt.put("ISTOURMODE_WITHINTOUR", 	publictransport_istourmode_withintour);
	
		this.parameterPt.put("PURPOSE_BUSINESS",  				publictransport_purpose_business);
		this.parameterPt.put("PURPOSE_EDUCATION",  				publictransport_purpose_education);
		this.parameterPt.put("PURPOSE_HOME",  						0.0);
		this.parameterPt.put("PURPOSE_LEISURE_INDOOR",  	publictransport_purpose_leisure_indoors);
		this.parameterPt.put("PURPOSE_LEISURE_OUTDOOR",  	publictransport_purpose_leisure_outdoors);
		this.parameterPt.put("PURPOSE_LEISURE_OTHER",  		publictransport_purpose_leisure_other);
		this.parameterPt.put("PURPOSE_LEISURE_WALK",  		publictransport_purpose_leisure_walk);
		this.parameterPt.put("PURPOSE_OTHER",  						publictransport_purpose_other);
		this.parameterPt.put("PURPOSE_PRIVATE_BUSINESS",  publictransport_purpose_private_business);
		this.parameterPt.put("PURPOSE_PRIVATE_VISIT",  		publictransport_purpose_visit);
		this.parameterPt.put("PURPOSE_SERVICE",  					publictransport_purpose_service);
		this.parameterPt.put("PURPOSE_SHOPPING_DAILY",  	publictransport_purpose_shopping_grocery);
		this.parameterPt.put("PURPOSE_SHOPPING_OTHER",  	publictransport_purpose_shopping_other);
		this.parameterPt.put("PURPOSE_BUSINESS",  				publictransport_purpose_business);
		
		this.parameterPt.put("FEMALE", publictransport_sex_female);
		
		this.parameterPt.put("AGE_0TO9",  	publictransport_age_06to09);
		this.parameterPt.put("AGE_10TO17",  publictransport_age_10to17);
		this.parameterPt.put("AGE_18TO25",  publictransport_age_18to25);
		this.parameterPt.put("AGE_26TO35",  publictransport_age_26to35);
		this.parameterPt.put("AGE_36TO50",  publictransport_age_36to50);
		this.parameterPt.put("AGE_51TO60",  publictransport_age_51to60);
		this.parameterPt.put("AGE_61TO70",  publictransport_age_61to70);
		this.parameterPt.put("AGE_71PLUS",  publictransport_age_71plus);
	
	}
	


	// Copied from TourModeChoiceParameterBase -- refactor?
	@Override
	public Map<String,Double> parameterForMode(Mode mode) {
		assert mode != StandardMode.BIKE : mode;
		assert mode != StandardMode.CAR : mode;
		if (StandardMode.PEDESTRIAN.equals(mode)) {
			return parameterWalk;
		}
		if (StandardMode.PASSENGER.equals(mode)) {
			return parameterPassenger;
		}
		if (StandardMode.PUBLICTRANSPORT.equals(mode)) {
			return parameterPt;
		}
		throw new AssertionError("invalid mode: " + mode);
	}

}
