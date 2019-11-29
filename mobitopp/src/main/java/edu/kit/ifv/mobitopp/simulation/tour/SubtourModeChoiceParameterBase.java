package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.Mode;


public class SubtourModeChoiceParameterBase
	extends WithinTourModeChoiceAttributes
	implements SubtourModeChoiceParameter  {
	
	protected final Map<String,Double> parameterGeneric = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterWalk = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterBike = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterCar = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterPassenger = new LinkedHashMap<String,Double>();
	protected final Map<String,Double> parameterPt = new LinkedHashMap<String,Double>();
	
	protected Double walking                        = 0.0;
	protected Double cardriver                      = 0.0;
	protected Double carpassenger                   = 0.0;
	protected Double cycling                        = 0.0;
	protected Double publictransport                = 0.0;

	protected Double time                           = 0.0;
	protected Double cost                           = 0.0;
	
	protected Double notavailable                     = 0.0;

	protected Double walking_istourmode             = 0.0;
	protected Double cardriver_istourmode           = 0.0;
	protected Double carpassenger_istourmode        = 0.0;
	protected Double cycling_istourmode             = 0.0;
	protected Double publictransport_istourmode     = 0.0;
	
	protected Double walking_intrazonal             = 0.0;
	protected Double cardriver_intrazonal           = 0.0;
	protected Double carpassenger_intrazonal        = 0.0;
	protected Double cycling_intrazonal             = 0.0;
	protected Double publictransport_intrazonal     = 0.0;

	protected Double walking_purpose_work        			 	        	= 0.0;
	protected Double walking_purpose_business    			 	        	= 0.0;
	protected Double walking_purpose_education          	        = 0.0;
	protected Double walking_purpose_leisure_indoors      	      = 0.0;
	protected Double walking_purpose_leisure_other          	    = 0.0;
	protected Double walking_purpose_leisure_outdoors         	  = 0.0;
	protected Double walking_purpose_leisure_walk              		= 0.0;
	protected Double walking_purpose_private_business         	  = 0.0;
	protected Double walking_purpose_service                  	  = 0.0;
	protected Double walking_purpose_shopping_grocery         	  = 0.0;
	protected Double walking_purpose_shopping_other           	  = 0.0;
	protected Double walking_purpose_visit                     		= 0.0;
	protected Double cardriver_purpose_work 		                  = 0.0;
	protected Double cardriver_purpose_business                   = 0.0;
	protected Double cardriver_purpose_education                  = 0.0;
	protected Double cardriver_purpose_leisure_indoors            = 0.0;
	protected Double cardriver_purpose_leisure_other              = 0.0;
	protected Double cardriver_purpose_leisure_outdoors           = 0.0;
	protected Double cardriver_purpose_leisure_walk              	= 0.0;
	protected Double cardriver_purpose_private_business           = 0.0;
	protected Double cardriver_purpose_service                    = 0.0;
	protected Double cardriver_purpose_shopping_grocery           = 0.0;
	protected Double cardriver_purpose_shopping_other             = 0.0;
	protected Double cardriver_purpose_visit                      = 0.0;
	protected Double carpassenger_purpose_work		                = 0.0;
	protected Double carpassenger_purpose_business	              = 0.0;
	protected Double carpassenger_purpose_education               = 0.0;
	protected Double carpassenger_purpose_leisure_indoors         = 0.0;
	protected Double carpassenger_purpose_leisure_other           = 0.0;
	protected Double carpassenger_purpose_leisure_outdoors        = 0.0;
	protected Double carpassenger_purpose_leisure_walk           	= 0.0;
	protected Double carpassenger_purpose_private_business        = 0.0;
	protected Double carpassenger_purpose_service                 = 0.0;
	protected Double carpassenger_purpose_shopping_grocery        = 0.0;
	protected Double carpassenger_purpose_shopping_other          = 0.0;
	protected Double carpassenger_purpose_visit                   = 0.0;
	protected Double cycling_purpose_work			                    = 0.0;
	protected Double cycling_purpose_business	                    = 0.0;
	protected Double cycling_purpose_education                    = 0.0;
	protected Double cycling_purpose_leisure_indoors              = 0.0;
	protected Double cycling_purpose_leisure_other                = 0.0;
	protected Double cycling_purpose_leisure_outdoors             = 0.0;
	protected Double cycling_purpose_leisure_walk                 = 0.0;
	protected Double cycling_purpose_private_business             = 0.0;
	protected Double cycling_purpose_service                      = 0.0;
	protected Double cycling_purpose_shopping_grocery             = 0.0;
	protected Double cycling_purpose_shopping_other               = 0.0;
	protected Double cycling_purpose_visit                        = 0.0;
	protected Double publictransport_purpose_work			            = 0.0;
	protected Double publictransport_purpose_business	            = 0.0;
	protected Double publictransport_purpose_education            = 0.0;
	protected Double publictransport_purpose_leisure_indoors      = 0.0;
	protected Double publictransport_purpose_leisure_other        = 0.0;
	protected Double publictransport_purpose_leisure_outdoors     = 0.0;
	protected Double publictransport_purpose_leisure_walk        	= 0.0;
	protected Double publictransport_purpose_private_business     = 0.0;
	protected Double publictransport_purpose_service              = 0.0;
	protected Double publictransport_purpose_shopping_grocery     = 0.0;
	protected Double publictransport_purpose_shopping_other       = 0.0;
	protected Double publictransport_purpose_visit                = 0.0;
	
	
	
	protected void init() {

		//TODO: Enums statt Strings!
		this.parameterGeneric.put("TIME", 					time);
		this.parameterGeneric.put("COST", 					cost);
	
		this.parameterWalk.putAll(this.parameterGeneric);
		this.parameterBike.putAll(this.parameterGeneric);
		this.parameterCar.putAll(this.parameterGeneric);
		this.parameterPassenger.putAll(this.parameterGeneric);
		this.parameterPt.putAll(this.parameterGeneric);
		
		// walk
		this.parameterWalk.put("CONST", 			walking);
		this.parameterWalk.put("ISTOURMODE", 	walking_istourmode);
		this.parameterWalk.put("INTRAZONAL", 	walking_intrazonal);
		
		this.parameterWalk.put("PURPOSE_WORK",  						walking_purpose_work);
		this.parameterWalk.put("PURPOSE_BUSINESS",  				walking_purpose_business);
		this.parameterWalk.put("PURPOSE_EDUCATION",  				walking_purpose_education);
		this.parameterWalk.put("PURPOSE_LEISURE_INDOOR",  	walking_purpose_leisure_indoors);
		this.parameterWalk.put("PURPOSE_LEISURE_OTHER",  		walking_purpose_leisure_other);
		this.parameterWalk.put("PURPOSE_LEISURE_OUTDOOR",	  walking_purpose_leisure_outdoors);
		this.parameterWalk.put("PURPOSE_LEISURE_WALK",  		walking_purpose_leisure_walk);
		this.parameterWalk.put("PURPOSE_PRIVATE_BUSINESS",  walking_purpose_private_business);
		this.parameterWalk.put("PURPOSE_SERVICE",  					walking_purpose_service);
		this.parameterWalk.put("PURPOSE_SHOPPING_DAILY",  walking_purpose_shopping_grocery);
		this.parameterWalk.put("PURPOSE_SHOPPING_OTHER",  	walking_purpose_shopping_other);
		this.parameterWalk.put("PURPOSE_PRIVATE_VISIT",  						walking_purpose_visit);
		

		// bike
		this.parameterBike.put("CONST", 	cycling);
		this.parameterBike.put("ISTOURMODE", 	cycling_istourmode);
		this.parameterBike.put("INTRAZONAL", 	cycling_intrazonal);
		
		this.parameterBike.put("PURPOSE_WORK",  						cycling_purpose_work);
		this.parameterBike.put("PURPOSE_BUSINESS", 					cycling_purpose_business);
		this.parameterBike.put("PURPOSE_EDUCATION",  				cycling_purpose_education);
		this.parameterBike.put("PURPOSE_LEISURE_INDOOR",  	cycling_purpose_leisure_indoors);
		this.parameterBike.put("PURPOSE_LEISURE_OTHER",  		cycling_purpose_leisure_other);
		this.parameterBike.put("PURPOSE_LEISURE_OUTDOOR", 	cycling_purpose_leisure_outdoors);
		this.parameterBike.put("PURPOSE_LEISURE_WALK",  		cycling_purpose_leisure_walk);
		this.parameterBike.put("PURPOSE_PRIVATE_BUSINESS",  cycling_purpose_private_business);
		this.parameterBike.put("PURPOSE_SERVICE",  					cycling_purpose_service);
		this.parameterBike.put("PURPOSE_SHOPPING_DAILY",  cycling_purpose_shopping_grocery);
		this.parameterBike.put("PURPOSE_SHOPPING_OTHER",  	cycling_purpose_shopping_other);
		this.parameterBike.put("PURPOSE_PRIVATE_VISIT",  						cycling_purpose_visit);
		
		// car
		this.parameterCar.put("CONST", 	cardriver);
		this.parameterCar.put("ISTOURMODE", 	cardriver_istourmode);
		this.parameterCar.put("INTRAZONAL", 	cardriver_intrazonal);
		
		this.parameterCar.put("PURPOSE_WORK",  						cardriver_purpose_work);
		this.parameterCar.put("PURPOSE_BUSINESS",  				cardriver_purpose_business);
		this.parameterCar.put("PURPOSE_EDUCATION",  			cardriver_purpose_education);
		this.parameterCar.put("PURPOSE_LEISURE_INDOOR",  	cardriver_purpose_leisure_indoors);
		this.parameterCar.put("PURPOSE_LEISURE_OTHER",  	cardriver_purpose_leisure_other);
		this.parameterCar.put("PURPOSE_LEISURE_OUTDOOR", 	cardriver_purpose_leisure_outdoors);
		this.parameterCar.put("PURPOSE_LEISURE_WALK",  		cardriver_purpose_leisure_walk);
		this.parameterCar.put("PURPOSE_PRIVATE_BUSINESS", cardriver_purpose_private_business);
		this.parameterCar.put("PURPOSE_SERVICE",  				cardriver_purpose_service);
		this.parameterCar.put("PURPOSE_SHOPPING_DAILY", cardriver_purpose_shopping_grocery);
		this.parameterCar.put("PURPOSE_SHOPPING_OTHER",  	cardriver_purpose_shopping_other);
		this.parameterCar.put("PURPOSE_PRIVATE_VISIT",  					cardriver_purpose_visit);
		
		
		// passenger
		this.parameterPassenger.put("CONST", 				carpassenger);
		this.parameterPassenger.put("ISTOURMODE", 	carpassenger_istourmode);
		this.parameterPassenger.put("INTRAZONAL", 	carpassenger_intrazonal);
		
		this.parameterPassenger.put("PURPOSE_WORK",  						carpassenger_purpose_work);
		this.parameterPassenger.put("PURPOSE_BUSINESS",  				carpassenger_purpose_business);
		this.parameterPassenger.put("PURPOSE_EDUCATION",  			carpassenger_purpose_education);
		this.parameterPassenger.put("PURPOSE_LEISURE_INDOOR",  	carpassenger_purpose_leisure_indoors);
		this.parameterPassenger.put("PURPOSE_LEISURE_OTHER",  	carpassenger_purpose_leisure_other);
		this.parameterPassenger.put("PURPOSE_LEISURE_OUTDOOR", 	carpassenger_purpose_leisure_outdoors);
		this.parameterPassenger.put("PURPOSE_LEISURE_WALK",  		carpassenger_purpose_leisure_walk);
		this.parameterPassenger.put("PURPOSE_PRIVATE_BUSINESS", carpassenger_purpose_private_business);
		this.parameterPassenger.put("PURPOSE_SERVICE",  				carpassenger_purpose_service);
		this.parameterPassenger.put("PURPOSE_SHOPPING_DAILY", carpassenger_purpose_shopping_grocery);
		this.parameterPassenger.put("PURPOSE_SHOPPING_OTHER",  	carpassenger_purpose_shopping_other);
		this.parameterPassenger.put("PURPOSE_PRIVATE_VISIT",  					carpassenger_purpose_visit);
		
	
	// pt
		this.parameterPt.put("CONST", 			publictransport);
		this.parameterPt.put("ISTOURMODE", 	publictransport_istourmode);
		this.parameterPt.put("INTRAZONAL", 	publictransport_intrazonal);
		
		this.parameterPt.put("PURPOSE_WORK",  						publictransport_purpose_work);
		this.parameterPt.put("PURPOSE_BUSINESS",  				publictransport_purpose_business);
		this.parameterPt.put("PURPOSE_EDUCATION",  				publictransport_purpose_education);
		this.parameterPt.put("PURPOSE_LEISURE_INDOOR",  	publictransport_purpose_leisure_indoors);
		this.parameterPt.put("PURPOSE_LEISURE_OTHER",  		publictransport_purpose_leisure_other);
		this.parameterPt.put("PURPOSE_LEISURE_OUTDOOR",  	publictransport_purpose_leisure_outdoors);
		this.parameterPt.put("PURPOSE_LEISURE_WALK",  		publictransport_purpose_leisure_walk);
		this.parameterPt.put("PURPOSE_PRIVATE_BUSINESS",  publictransport_purpose_private_business);
		this.parameterPt.put("PURPOSE_SERVICE",  					publictransport_purpose_service);
		this.parameterPt.put("PURPOSE_SHOPPING_DAILY",  publictransport_purpose_shopping_grocery);
		this.parameterPt.put("PURPOSE_SHOPPING_OTHER",  	publictransport_purpose_shopping_other);
		this.parameterPt.put("PURPOSE_PRIVATE_VISIT",  						publictransport_purpose_visit);
	}


	@Override
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
