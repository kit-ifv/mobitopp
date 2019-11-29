package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.Mode;

public class WithinTourModeChoiceParameterTourmode 
	extends WithinTourModeChoiceParameterBase implements WithinTourModeChoiceParameter {

	

	/*
	 Call:
mlogit(formula = formulas_trip$tourmode, data = trip.TRAINING, 
    reflevel = "walking", method = "nr", print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
       0.274352        0.409314        0.133433        0.051132        0.131769 

nr method
8 iterations, 0h:0m:8s 
g'(-H)^-1g = 0.000456 
successive function values within tolerance limits 

Coefficients :
                                          Estimate Std. Error  t-value  Pr(>|t|)    
cardriver:(intercept)                    -4.126574   0.062636 -65.8814 < 2.2e-16 ***
carpassenger:(intercept)                 -3.188136   0.039660 -80.3875 < 2.2e-16 ***
cycling:(intercept)                      -5.994522   0.158311 -37.8655 < 2.2e-16 ***
publictransport:(intercept)              -3.263407   0.041121 -79.3602 < 2.2e-16 ***
cardriver:tourmode.cycling                2.579011   0.167865  15.3636 < 2.2e-16 ***
carpassenger:tourmode.cycling             1.640574   0.160712  10.2082 < 2.2e-16 ***
cycling:tourmode.cycling                  8.752375   0.172010  50.8829 < 2.2e-16 ***
publictransport:tourmode.cycling          2.080487   0.140837  14.7723 < 2.2e-16 ***
cardriver:tourmode.publictransport        1.783403   0.102023  17.4804 < 2.2e-16 ***
carpassenger:tourmode.publictransport     2.226813   0.060224  36.9754 < 2.2e-16 ***
cycling:tourmode.publictransport          1.354675   0.290610   4.6615 3.139e-06 ***
publictransport:tourmode.publictransport  4.836393   0.048755  99.1987 < 2.2e-16 ***
cardriver:tourmode.cardriver              7.114988   0.068014 104.6107 < 2.2e-16 ***
carpassenger:tourmode.cardriver           2.134005   0.064507  33.0817 < 2.2e-16 ***
cycling:tourmode.cardriver                2.295559   0.229692   9.9941 < 2.2e-16 ***
publictransport:tourmode.cardriver        1.291063   0.084661  15.2497 < 2.2e-16 ***
cardriver:tourmode.carpassenger           3.163553   0.088912  35.5809 < 2.2e-16 ***
carpassenger:tourmode.carpassenger        5.367851   0.052890 101.4902 < 2.2e-16 ***
cycling:tourmode.carpassenger             1.667091   0.330901   5.0380 4.704e-07 ***
publictransport:tourmode.carpassenger     2.542372   0.071088  35.7636 < 2.2e-16 ***
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -33603
McFadden R^2:  0.67986 
Likelihood ratio test : chisq = 142720 (p.value = < 2.22e-16)

		*/
	
	protected Double walking                                	= 0.0 ;
	protected Double walking_tourmode_cycling                	= 0.0 ;
	protected Double walking_tourmode_publictransport        	= 0.0 ;
	protected Double walking_tourmode_cardriver              	= 0.0 ;
	protected Double walking_tourmode_carpassenger           	= 0.0 ;
	
	protected Double cardriver                                = -4.126574 ;
	protected Double carpassenger                             = -3.188136 ;
	protected Double cycling                                  = -5.994522 ;
	protected Double publictransport                          = -3.263407 ;
	
	protected Double cardriver_tourmode_cycling               =  2.579011 ;
	protected Double carpassenger_tourmode_cycling            =  1.640574 ;
	protected Double cycling_tourmode_cycling                 =  8.752375 ;
	protected Double publictransport_tourmode_cycling         =  2.080487 ;
	protected Double cardriver_tourmode_publictransport       =  1.783403 ;
	protected Double carpassenger_tourmode_publictransport    =  2.226813 ;
	protected Double cycling_tourmode_publictransport         =  1.354675 ;
	protected Double publictransport_tourmode_publictransport =  4.836393 ;
	protected Double cardriver_tourmode_cardriver             =  7.114988 ;
	protected Double carpassenger_tourmode_cardriver          =  2.134005 ;
	protected Double cycling_tourmode_cardriver               =  2.295559 ;
	protected Double publictransport_tourmode_cardriver       =  1.291063 ;
	protected Double cardriver_tourmode_carpassenger          =  3.163553 ;
	protected Double carpassenger_tourmode_carpassenger       =  5.367851 ;
	protected Double cycling_tourmode_carpassenger            =  1.667091 ;
	protected Double publictransport_tourmode_carpassenger    =  2.542372 ;

	public WithinTourModeChoiceParameterTourmode() {
		init();
	}
	
	protected void init() {

		/*
		this.parameterGeneric.put("TIME", 					time);
		this.parameterGeneric.put("COST", 					cost);
		*/
		
		this.parameterWalk.putAll(this.parameterGeneric);
		this.parameterBike.putAll(this.parameterGeneric);
		this.parameterCar.putAll(this.parameterGeneric);
		this.parameterPassenger.putAll(this.parameterGeneric);
		this.parameterPt.putAll(this.parameterGeneric);
		
		// walk
		this.parameterWalk.put("CONST", 	walking);
		this.parameterWalk.put("TOURMODE_BIKE", 	walking_tourmode_cycling);
		this.parameterWalk.put("TOURMODE_PT", 	walking_tourmode_publictransport);
		this.parameterWalk.put("TOURMODE_CAR", 	walking_tourmode_cardriver);
		this.parameterWalk.put("TOURMODE_PASSENGER", 	walking_tourmode_carpassenger);
	
		// bike
		this.parameterBike.put("CONST", 	cycling);
		this.parameterBike.put("TOURMODE_BIKE", 	cycling_tourmode_cycling);
		this.parameterBike.put("TOURMODE_PT", 	cycling_tourmode_publictransport);
		this.parameterBike.put("TOURMODE_CAR", 	cycling_tourmode_cardriver);
		this.parameterBike.put("TOURMODE_PASSENGER", 	cycling_tourmode_carpassenger);
	
		// car
		this.parameterCar.put("CONST", 	cardriver);
		this.parameterCar.put("TOURMODE_BIKE", 	cardriver_tourmode_cycling);
		this.parameterCar.put("TOURMODE_PT", 	cardriver_tourmode_publictransport);
		this.parameterCar.put("TOURMODE_CAR", 	cardriver_tourmode_cardriver);
		this.parameterCar.put("TOURMODE_PASSENGER", 	cardriver_tourmode_carpassenger);
	
		// passenger
		this.parameterPassenger.put("CONST", 	carpassenger);
		this.parameterPassenger.put("TOURMODE_BIKE", 	carpassenger_tourmode_cycling);
		this.parameterPassenger.put("TOURMODE_PT", 	carpassenger_tourmode_publictransport);
		this.parameterPassenger.put("TOURMODE_CAR", 	carpassenger_tourmode_cardriver);
		this.parameterPassenger.put("TOURMODE_PASSENGER", 	carpassenger_tourmode_carpassenger);
	
	// pt
		this.parameterPt.put("CONST", 	publictransport);
		this.parameterPt.put("TOURMODE_BIKE", 	publictransport_tourmode_cycling);
		this.parameterPt.put("TOURMODE_PT", 	publictransport_tourmode_publictransport);
		this.parameterPt.put("TOURMODE_CAR", 	publictransport_tourmode_cardriver);
		this.parameterPt.put("TOURMODE_PASSENGER", 	publictransport_tourmode_carpassenger);
	}
	


	// Copied from TourModeChoiceParameterBase -- refactor?
	@Override
	public Map<String,Double> parameterForMode(Mode mode) {
		if (Mode.PEDESTRIAN.equals(mode)) {
			return parameterWalk;
		}
		if (Mode.BIKE.equals(mode)) {
			return parameterBike;
		}
		if (Mode.CAR.equals(mode)) {
			return parameterCar;
		}
		if (Mode.PASSENGER.equals(mode)) {
			return parameterPassenger;
		}
		if (Mode.PUBLICTRANSPORT.equals(mode)) {
			return parameterPt;
		}
		throw new AssertionError("invalid mode: " + mode);
	}

}
