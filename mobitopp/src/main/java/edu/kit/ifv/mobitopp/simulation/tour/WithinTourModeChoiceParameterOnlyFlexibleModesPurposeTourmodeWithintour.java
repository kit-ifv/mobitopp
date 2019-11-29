package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;

public class WithinTourModeChoiceParameterOnlyFlexibleModesPurposeTourmodeWithintour 
	extends WithinTourModeChoiceParameterOnlyFlexibleModesBase 
	implements WithinTourModeChoiceParameter 
{
	
	protected Double walking                                	= 0.0 ;
	protected Double walking_istourmode                     	= 2.0 ;
	protected Double walking_withintour        						   	= 0.0 ;
	protected Double walking_istourmode_withintour           	= -5.0 ;
	


	/*
Call:
mlogit(formula = mode ~ time + cost | istourmode * withintour. + 
    purpose., data = trainingdata, reflevel = "walking", method = "nr", 
    print.level = 0)

Frequencies of alternatives:
        walking    carpassenger publictransport 
          0.504           0.248           0.249 

nr method
7 iterations, 0h:0m:9s 
g'(-H)^-1g = 2.3E-08 
gradient close to zero 

Coefficients :
                                                Estimate Std. Error t-value Pr(>|t|)    
carpassenger:(intercept)                       -3.216743   0.785380   -4.10  4.2e-05 ***
publictransport:(intercept)                    -1.239958   0.957489   -1.30   0.1953    
time                                           -0.039961   0.000576  -69.41  < 2e-16 ***
cost                                           -0.268273   0.016826  -15.94  < 2e-16 ***
carpassenger:istourmodeTRUE                     4.579022   0.039682  115.39  < 2e-16 ***
publictransport:istourmodeTRUE                  3.983918   0.040728   97.82  < 2e-16 ***
carpassenger:withintour.TRUE                   -0.733050   0.757163   -0.97   0.3330    
publictransport:withintour.TRUE                 0.257471   0.948498    0.27   0.7860    
carpassenger:purpose.business                   1.268819   0.319684    3.97  7.2e-05 ***
publictransport:purpose.business               -0.407124   0.231468   -1.76   0.0786 .  
carpassenger:purpose.education                 -0.264613   0.283170   -0.93   0.3501    
publictransport:purpose.education              -0.598124   0.195391   -3.06   0.0022 ** 
carpassenger:purpose.service                    1.585476   0.255035    6.22  5.1e-10 ***
publictransport:purpose.service                -0.463167   0.196969   -2.35   0.0187 *  
carpassenger:purpose.private_business           0.945520   0.235833    4.01  6.1e-05 ***
publictransport:purpose.private_business       -0.526921   0.163306   -3.23   0.0013 ** 
carpassenger:purpose.visit                      1.427257   0.234357    6.09  1.1e-09 ***
publictransport:purpose.visit                  -0.454662   0.166097   -2.74   0.0062 ** 
carpassenger:purpose.shopping_grocery           1.089732   0.226335    4.81  1.5e-06 ***
publictransport:purpose.shopping_grocery       -0.776623   0.155214   -5.00  5.6e-07 ***
carpassenger:purpose.shopping_other             0.744968   0.232589    3.20   0.0014 ** 
publictransport:purpose.shopping_other         -0.988409   0.161576   -6.12  9.5e-10 ***
carpassenger:purpose.leisure_indoors            0.686822   0.236386    2.91   0.0037 ** 
publictransport:purpose.leisure_indoors        -1.010535   0.165873   -6.09  1.1e-09 ***
carpassenger:purpose.leisure_outdoors           1.252623   0.255452    4.90  9.4e-07 ***
publictransport:purpose.leisure_outdoors       -0.703305   0.189061   -3.72   0.0002 ***
carpassenger:purpose.leisure_other              0.615508   0.235684    2.61   0.0090 ** 
publictransport:purpose.leisure_other          -0.781245   0.165682   -4.72  2.4e-06 ***
carpassenger:purpose.leisure_walk              -2.689685   0.298449   -9.01  < 2e-16 ***
publictransport:purpose.leisure_walk           -4.593084   0.396258  -11.59  < 2e-16 ***
carpassenger:purpose.other                     -0.351604   0.784867   -0.45   0.6542    
publictransport:purpose.other                  -1.571961   0.956952   -1.64   0.1005    
carpassenger:istourmodeTRUE:withintour.TRUE    -1.177358   0.076724  -15.35  < 2e-16 ***
publictransport:istourmodeTRUE:withintour.TRUE -2.648185   0.072608  -36.47  < 2e-16 ***
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -25500
McFadden R^2:  0.666 
Likelihood ratio test : chisq = 102000 (p.value = <2e-16)

		*/
	
	protected Double carpassenger                                    = -3.216743;    //se=0.785380
	protected Double publictransport                                 = -1.239958;    //se=0.957489
	
	protected Double time                                            = -0.039961;    //se=0.000576
	protected Double cost                                            = -0.268273;    //se=0.016826
	
	protected Double carpassenger_istourmode                          = 4.579022;    //se=0.039682
	protected Double publictransport_istourmode                       = 3.983918;    //se=0.040728
	
	protected Double carpassenger_withintour                         = -0.733050;    //se=0.757163
	protected Double publictransport_withintour                       = 0.257471;    //se=0.948498
	
	protected Double carpassenger_istourmode_withintour          			= -1.177358;    //se=0.076724
	protected Double publictransport_istourmode_withintour       			= -2.648185;    //se=0.072608
	
	protected Double carpassenger_purpose_business                    = 1.268819;    //se=0.319684
	protected Double publictransport_purpose_business                = -0.407124;    //se=0.231468
	protected Double carpassenger_purpose_education                  = -0.264613;    //se=0.283170
	protected Double publictransport_purpose_education               = -0.598124;    //se=0.195391
	protected Double carpassenger_purpose_service                     = 1.585476;    //se=0.255035
	protected Double publictransport_purpose_service                 = -0.463167;    //se=0.196969
	protected Double carpassenger_purpose_private_business            = 0.945520;    //se=0.235833
	protected Double publictransport_purpose_private_business        = -0.526921;    //se=0.163306
	protected Double carpassenger_purpose_visit                       = 1.427257;    //se=0.234357
	protected Double publictransport_purpose_visit                   = -0.454662;    //se=0.166097
	protected Double carpassenger_purpose_shopping_grocery            = 1.089732;    //se=0.226335
	protected Double publictransport_purpose_shopping_grocery        = -0.776623;    //se=0.155214
	protected Double carpassenger_purpose_shopping_other              = 0.744968;    //se=0.232589
	protected Double publictransport_purpose_shopping_other          = -0.988409;    //se=0.161576
	protected Double carpassenger_purpose_leisure_indoors             = 0.686822;    //se=0.236386
	protected Double publictransport_purpose_leisure_indoors         = -1.010535;    //se=0.165873
	protected Double carpassenger_purpose_leisure_outdoors            = 1.252623;    //se=0.255452
	protected Double publictransport_purpose_leisure_outdoors        = -0.703305;    //se=0.189061
	protected Double carpassenger_purpose_leisure_other               = 0.615508;    //se=0.235684
	protected Double publictransport_purpose_leisure_other           = -0.781245;    //se=0.165682
	protected Double carpassenger_purpose_leisure_walk               = -2.689685;    //se=0.298449
	protected Double publictransport_purpose_leisure_walk            = -4.593084;    //se=0.396258
	protected Double carpassenger_purpose_other                      = -0.351604;    //se=0.784867
	protected Double publictransport_purpose_other                   = -1.571961;    //se=0.956952

/*	
		protected Double carpassenger                                    = -3.100243;    //se=1.092641
		protected Double publictransport                                 = -1.150718;    //se=1.633817
		protected Double time                                            = -0.041889;    //se=0.000828
		protected Double cost                                            = -0.267582;    //se=0.023958
		
		protected Double carpassenger_istourmode                          = 4.614641;    //se=0.056866
		protected Double publictransport_istourmode                       = 3.982219;    //se=0.058058
		
		protected Double carpassenger_withintour                         	= -0.841832;    //se=1.059484
		protected Double publictransport_withintour                       = 0.169963;    //se=1.623415
		
		protected Double carpassenger_istourmode_withintour          			= -1.214047;    //se=0.109305
		protected Double publictransport_istourmode_withintour       			= -2.618165;    //se=0.105027
		
		protected Double carpassenger_purpose_business                    = 1.147036;    //se=0.438878
		protected Double carpassenger_purpose_education                   = 0.028722;    //se=0.375758
		protected Double carpassenger_purpose_leisure_indoors             = 0.538490;    //se=0.310487
		protected Double carpassenger_purpose_leisure_other               = 0.514148;    //se=0.310553
		protected Double carpassenger_purpose_leisure_outdoors            = 1.468279;    //se=0.335032
		protected Double carpassenger_purpose_leisure_walk               = -2.695808;    //se=0.409271
		protected Double carpassenger_purpose_other                      = -0.579038;    //se=1.091850
		protected Double carpassenger_purpose_private_business            = 0.991411;    //se=0.312334
		protected Double carpassenger_purpose_service                     = 1.339002;    //se=0.342496
		protected Double carpassenger_purpose_shopping_grocery            = 1.028884;    //se=0.295348
		protected Double carpassenger_purpose_shopping_other              = 0.493246;    //se=0.308502
		protected Double carpassenger_purpose_visit                       = 1.262755;    //se=0.309565
		protected Double publictransport_purpose_business                = -0.744594;    //se=0.341742
		protected Double publictransport_purpose_education               = -0.586067;    //se=0.279614
		protected Double publictransport_purpose_leisure_indoors         = -1.234372;    //se=0.232393
		protected Double publictransport_purpose_leisure_other           = -0.970798;    //se=0.230601
		protected Double publictransport_purpose_leisure_outdoors        = -0.655553;    //se=0.259718
		protected Double publictransport_purpose_leisure_walk            = -4.900233;    //se=0.598478
		protected Double publictransport_purpose_other                   = -1.709757;    //se=1.633158
		protected Double publictransport_purpose_private_business        = -0.598868;    //se=0.230644
		protected Double publictransport_purpose_service                 = -0.493642;    //se=0.269415
		protected Double publictransport_purpose_shopping_grocery        = -0.913495;    //se=0.214826
		protected Double publictransport_purpose_shopping_other          = -1.092926;    //se=0.225332
		protected Double publictransport_purpose_visit                   = -0.477064;    //se=0.233016
		
*/


	

	
	public WithinTourModeChoiceParameterOnlyFlexibleModesPurposeTourmodeWithintour() {
		init();
	}
	
	protected void init() {

		
		this.parameterGeneric.put("TIME", 					time);
		this.parameterGeneric.put("COST", 					cost);
		
		
		this.parameterWalk.putAll(this.parameterGeneric);
		this.parameterPassenger.putAll(this.parameterGeneric);
		this.parameterPt.putAll(this.parameterGeneric);
		
		// walk
		this.parameterWalk.put("CONST", 	walking);
		this.parameterWalk.put("ISTOURMODE", 	walking_istourmode);
		this.parameterWalk.put("WITHINTOUR", 	walking_withintour);
		this.parameterWalk.put("ISTOURMODE_WITHINTOUR", 	walking_istourmode_withintour);
	
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
