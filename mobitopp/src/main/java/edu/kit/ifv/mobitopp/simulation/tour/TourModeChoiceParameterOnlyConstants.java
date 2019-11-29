package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.time.Time;

public class TourModeChoiceParameterOnlyConstants 
	implements TourModeChoiceParameter {

	protected final Map<String,Double> parameterWalk = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterBike = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterCar = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterPassenger = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterPt = new LinkedHashMap<String,Double>();
	
	/*

Call:
mlogit(formula = mode ~ 1 | day., data = trainingdata, reflevel = "walking", 
    method = "nr", print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2827          0.3916          0.1283          0.0559          0.1415 

nr method
5 iterations, 0h:0m:3s 
g'(-H)^-1g = 3.84E-07 
gradient close to zero 

Coefficients :
                             Estimate Std. Error t-value    Pr(>|t|)    
cardriver:(intercept)          0.4027     0.0117   34.36     < 2e-16 ***
carpassenger:(intercept)      -0.9231     0.0170  -54.26     < 2e-16 ***
cycling:(intercept)           -1.5011     0.0213  -70.64     < 2e-16 ***
publictransport:(intercept)   -0.5198     0.0149  -34.99     < 2e-16 ***
cardriver:day.Saturday        -0.0130     0.0307   -0.42        0.67    
carpassenger:day.Saturday      0.6114     0.0378   16.19     < 2e-16 ***
cycling:day.Saturday          -0.3071     0.0622   -4.94 0.000000781 ***
publictransport:day.Saturday  -0.6266     0.0471  -13.32     < 2e-16 ***
cardriver:day.Sunday          -0.7572     0.0349  -21.73     < 2e-16 ***
carpassenger:day.Sunday        0.2331     0.0402    5.79 0.000000007 ***
cycling:day.Sunday            -0.8853     0.0757  -11.70     < 2e-16 ***
publictransport:day.Sunday    -1.3022     0.0584  -22.29     < 2e-16 ***
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -82200
McFadden R^2:  0.0107 
Likelihood ratio test : chisq = 1770 (p.value = <2e-16)

	 */
	
	protected Double walking          	           =	 0.0			+ 0.53;
	protected Double cardriver                     =	 0.4027		+ 0.5;
	protected Double carpassenger                  =	-0.9231		- 0.5;
	protected Double cycling                       =	-1.5011		- 0.1;
	protected Double publictransport               =	-0.5198;
	
	protected Double walking_day_Friday        	 =	 0.0 - 0.2;
	protected Double cardriver_day_Friday        =	 0.0 + 0.1;
	protected Double carpassenger_day_Friday     =	 0.0 + 0.2;
	protected Double cycling_day_Friday          =	 0.0;
	protected Double publictransport_day_Friday  =	 0.0;
	
	protected Double walking_day_Saturday        	 =	 0.0		- 0.2;
	protected Double cardriver_day_Saturday        =	-0.0130;
	protected Double carpassenger_day_Saturday     =	 0.6114	+ 0.5;
	protected Double cycling_day_Saturday          =	-0.3071;
	protected Double publictransport_day_Saturday  =	-0.6266	+ 0.1;
	
	protected Double walking_day_Sunday          	 =	 0.0		- 0.3;
	protected Double cardriver_day_Sunday          =	-0.7572;
	protected Double carpassenger_day_Sunday       =	 0.2331	+ 0.5;
	protected Double cycling_day_Sunday            =	-0.8853;
	protected Double publictransport_day_Sunday    =	-1.3022	+ 0.1;
	

	public TourModeChoiceParameterOnlyConstants() {
		init();
	}

	protected void init() {
	
		this.parameterWalk.put("CONST", 	walking);
		this.parameterWalk.put("DAY_FR",  walking_day_Friday);
		this.parameterWalk.put("DAY_SA",  walking_day_Saturday);
		this.parameterWalk.put("DAY_SU",  walking_day_Sunday);
		
		this.parameterBike.put("CONST", 	cycling);
		this.parameterBike.put("DAY_FR",  cycling_day_Friday);
		this.parameterBike.put("DAY_SA",  cycling_day_Saturday);
		this.parameterBike.put("DAY_SU",  cycling_day_Sunday);
		
		this.parameterCar.put("CONST", 	cardriver);
		this.parameterCar.put("DAY_FR",  cardriver_day_Friday);
		this.parameterCar.put("DAY_SA",  cardriver_day_Saturday);
		this.parameterCar.put("DAY_SU",  cardriver_day_Sunday);
		
		this.parameterPassenger.put("CONST", 	carpassenger);
		this.parameterPassenger.put("DAY_FR",  carpassenger_day_Friday);
		this.parameterPassenger.put("DAY_SA",  carpassenger_day_Saturday);
		this.parameterPassenger.put("DAY_SU",  carpassenger_day_Sunday);
		
		this.parameterPt.put("CONST", 	publictransport);
		this.parameterPt.put("DAY_FR",  publictransport_day_Friday);
		this.parameterPt.put("DAY_SA",  publictransport_day_Saturday);
		this.parameterPt.put("DAY_SU",  publictransport_day_Sunday);
	}

	@Override
	public Map<Mode, Map<String, Double>> gatherAttributes(Set<Mode> modes, Tour tour, Person person, Map<Mode, Double> preferences, ImpedanceIfc impedance) {
	
			Time date = tour.mainActivity().startDate();
		
			double day_FR 		= date.weekDay() == DayOfWeek.FRIDAY ? 1 : 0;
			double day_SA 		= date.weekDay() == DayOfWeek.SATURDAY ? 1 : 0;
			double day_SU 		= date.weekDay() == DayOfWeek.SUNDAY ? 1 : 0;

			
			Map<String,Double> attributes = new LinkedHashMap<String,Double>();	
	
			attributes.put("CONST", 	1.0);
			attributes.put("DAY_FR",  day_FR);
			attributes.put("DAY_SA",  day_SA);
			attributes.put("DAY_SU",  day_SU);
			
			Map<Mode,Map<String,Double>> modeAttributes = new LinkedHashMap<Mode,Map<String,Double>>();
	
			for (Mode mode : modes) {
	
				Map<String,Double> attrib = new LinkedHashMap<String,Double>(attributes);
	
				modeAttributes.put(mode, attrib);
			}
	
			return  modeAttributes;
		}

	public Map<String, Double> parameterForMode(Mode mode) {
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