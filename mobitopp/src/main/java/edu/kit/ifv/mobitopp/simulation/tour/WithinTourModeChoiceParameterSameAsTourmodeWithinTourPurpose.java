package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.Mode;

public class WithinTourModeChoiceParameterSameAsTourmodeWithinTourPurpose 
	extends WithinTourModeChoiceParameterBase 
	implements WithinTourModeChoiceParameter 
{


	// TODO: Parameter auf Gesamtdatensatz schätzen!

	/*
Call:
mlogit(formula = mode ~ notavailable + time + cost | istourmode * 
    withintour. + purpose., data = trainingdata, reflevel = "walking", 
    method = "nr", print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2769          0.4089          0.1364          0.0456          0.1321 

nr method
24 iterations, 0h:1m:52s 
g'(-H)^-1g = 9.17E-07 
gradient close to zero 

Coefficients :
                                                 Estimate Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                            24.87272 4041.09874    0.01  0.99509    
carpassenger:(intercept)                         -3.67872    0.32342  -11.37  < 2e-16 ***
cycling:(intercept)                              -6.90532    0.58898  -11.72  < 2e-16 ***
publictransport:(intercept)                      -2.15738    0.36107   -5.97  2.3e-09 ***
notavailable                                    -50.01982 4313.63854   -0.01  0.99075    
time                                             -0.02040    0.00045  -45.37  < 2e-16 ***
cost                                             -0.21102    0.01337  -15.79  < 2e-16 ***
cardriver:istourmodeTRUE                        -21.57652 4041.09873   -0.01  0.99574    
carpassenger:istourmodeTRUE                       4.30742    0.05098   84.50  < 2e-16 ***
cycling:istourmodeTRUE                            8.91580    0.16569   53.81  < 2e-16 ***
publictransport:istourmodeTRUE                    4.13697    0.05332   77.59  < 2e-16 ***
cardriver:withintour.TRUE                        -0.96855 4061.36616    0.00  0.99981    
carpassenger:withintour.TRUE                      0.36980    0.29488    1.25  0.20981    
cycling:withintour.TRUE                           1.69621    0.55497    3.06  0.00224 ** 
publictransport:withintour.TRUE                   0.32149    0.35096    0.92  0.35965    
cardriver:purpose.business                        0.93578    0.15537    6.02  1.7e-09 ***
carpassenger:purpose.business                     1.44218    0.21917    6.58  4.7e-11 ***
cycling:purpose.business                          1.57983    0.37582    4.20  2.6e-05 ***
publictransport:purpose.business                  0.68074    0.17913    3.80  0.00014 ***
cardriver:purpose.education                      -0.24011    0.33005   -0.73  0.46691    
carpassenger:purpose.education                   -0.08971    0.22552   -0.40  0.69080    
cycling:purpose.education                         0.10176    0.35127    0.29  0.77205    
publictransport:purpose.education                 0.35459    0.15377    2.31  0.02112 *  
cardriver:purpose.service                         2.18605    0.17295   12.64  < 2e-16 ***
carpassenger:purpose.service                      1.46426    0.19772    7.41  1.3e-13 ***
cycling:purpose.service                           1.28627    0.42984    2.99  0.00277 ** 
publictransport:purpose.service                   0.48734    0.18153    2.68  0.00726 ** 
cardriver:purpose.private_business                0.92494    0.12448    7.43  1.1e-13 ***
carpassenger:purpose.private_business             0.90569    0.17000    5.33  1.0e-07 ***
cycling:purpose.private_business                  1.01215    0.28508    3.55  0.00038 ***
publictransport:purpose.private_business          0.26766    0.13080    2.05  0.04073 *  
cardriver:purpose.visit                           1.13474    0.14172    8.01  1.1e-15 ***
carpassenger:purpose.visit                        1.50532    0.17004    8.85  < 2e-16 ***
cycling:purpose.visit                             1.36077    0.30082    4.52  6.1e-06 ***
publictransport:purpose.visit                     0.60718    0.14053    4.32  1.6e-05 ***
cardriver:purpose.shopping_grocery                1.37052    0.11717   11.70  < 2e-16 ***
carpassenger:purpose.shopping_grocery             0.88638    0.15961    5.55  2.8e-08 ***
cycling:purpose.shopping_grocery                  1.16448    0.26684    4.36  1.3e-05 ***
publictransport:purpose.shopping_grocery         -0.05492    0.12568   -0.44  0.66211    
cardriver:purpose.shopping_other                  0.79555    0.12630    6.30  3.0e-10 ***
carpassenger:purpose.shopping_other               0.69786    0.17115    4.08  4.6e-05 ***
cycling:purpose.shopping_other                    0.77284    0.31137    2.48  0.01306 *  
publictransport:purpose.shopping_other            0.04539    0.13289    0.34  0.73265    
cardriver:purpose.leisure_indoors                -0.63673    0.12492   -5.10  3.4e-07 ***
carpassenger:purpose.leisure_indoors              0.58808    0.16901    3.48  0.00050 ***
cycling:purpose.leisure_indoors                  -0.26982    0.30621   -0.88  0.37823    
publictransport:purpose.leisure_indoors          -0.22447    0.13215   -1.70  0.08939 .  
cardriver:purpose.leisure_outdoors                1.05643    0.19479    5.42  5.8e-08 ***
carpassenger:purpose.leisure_outdoors             1.55418    0.20164    7.71  1.3e-14 ***
cycling:purpose.leisure_outdoors                  1.28113    0.39687    3.23  0.00125 ** 
publictransport:purpose.leisure_outdoors          0.58383    0.17580    3.32  0.00090 ***
cardriver:purpose.leisure_other                  -0.10926    0.15034   -0.73  0.46738    
carpassenger:purpose.leisure_other                0.66209    0.17461    3.79  0.00015 ***
cycling:purpose.leisure_other                     0.14924    0.32411    0.46  0.64519    
publictransport:purpose.leisure_other             0.42172    0.13283    3.17  0.00150 ** 
cardriver:purpose.leisure_walk                   -3.78775    0.24580  -15.41  < 2e-16 ***
carpassenger:purpose.leisure_walk                -2.54114    0.27285   -9.31  < 2e-16 ***
cycling:purpose.leisure_walk                      0.06520    0.37195    0.18  0.86084    
publictransport:purpose.leisure_walk             -2.96962    0.33933   -8.75  < 2e-16 ***
cardriver:purpose.other                          -0.19856    0.30329   -0.65  0.51266    
carpassenger:purpose.other                        0.73525    0.32164    2.29  0.02226 *  
cycling:purpose.other                             1.33922    0.57605    2.32  0.02008 *  
publictransport:purpose.other                    -0.53730    0.35908   -1.50  0.13457    
cardriver:istourmodeTRUE:withintour.TRUE         -1.66102 4061.36615    0.00  0.99967    
carpassenger:istourmodeTRUE:withintour.TRUE      -1.27034    0.08714  -14.58  < 2e-16 ***
cycling:istourmodeTRUE:withintour.TRUE           -3.83550    0.22414  -17.11  < 2e-16 ***
publictransport:istourmodeTRUE:withintour.TRUE   -2.93784    0.08172  -35.95  < 2e-16 ***
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -24700
McFadden R^2:  0.762 
Likelihood ratio test : chisq = 158000 (p.value = <2e-16)


		*/
	
	protected Double walking                                	= 0.0 ;
	protected Double walking_istourmode           						= 0.0 ;
	protected Double walking_withintour        						   	= 0.0 ;
	protected Double walking_istourmode_withintour           	= 0.0 ;
	

	protected Double cardriver                                        = 24.87272;  // se =4041.09874
	protected Double carpassenger                                     = -3.67872;     // se =0.32342
	protected Double cycling                                          = -6.90532;     // se =0.58898
	protected Double publictransport                                  = -2.15738;     // se =0.36107
	
	protected Double notavailable                                    = -50.01982;  // se =4313.63854
	
	protected Double time                                             = -0.02040;     // se =0.00045
	protected Double cost                                             = -0.21102;     // se =0.01337
	
	protected Double cardriver_istourmode                            = -21.57652;  // se =4041.09873
	protected Double carpassenger_istourmode                           = 4.30742;     // se =0.05098
	protected Double cycling_istourmode                                = 8.91580;     // se =0.16569
	protected Double publictransport_istourmode                        = 4.13697;     // se =0.05332
	
	protected Double cardriver_withintour                             = -0.96855;  // se =4061.36616
	protected Double carpassenger_withintour                           = 0.36980;     // se =0.29488
	protected Double cycling_withintour                                = 1.69621;     // se =0.55497
	protected Double publictransport_withintour                        = 0.32149;     // se =0.35096
	
	protected Double cardriver_purpose_business                        = 0.93578;     // se =0.15537
	protected Double carpassenger_purpose_business                     = 1.44218;     // se =0.21917
	protected Double cycling_purpose_business                          = 1.57983;     // se =0.37582
	protected Double publictransport_purpose_business                  = 0.68074;     // se =0.17913
	protected Double cardriver_purpose_education                      = -0.24011;     // se =0.33005
	protected Double carpassenger_purpose_education                   = -0.08971;     // se =0.22552
	protected Double cycling_purpose_education                         = 0.10176;     // se =0.35127
	protected Double publictransport_purpose_education                 = 0.35459;     // se =0.15377
	protected Double cardriver_purpose_service                         = 2.18605;     // se =0.17295
	protected Double carpassenger_purpose_service                      = 1.46426;     // se =0.19772
	protected Double cycling_purpose_service                           = 1.28627;     // se =0.42984
	protected Double publictransport_purpose_service                   = 0.48734;     // se =0.18153
	protected Double cardriver_purpose_private_business                = 0.92494;     // se =0.12448
	protected Double carpassenger_purpose_private_business             = 0.90569;     // se =0.17000
	protected Double cycling_purpose_private_business                  = 1.01215;     // se =0.28508
	protected Double publictransport_purpose_private_business          = 0.26766;     // se =0.13080
	protected Double cardriver_purpose_visit                           = 1.13474;     // se =0.14172
	protected Double carpassenger_purpose_visit                        = 1.50532;     // se =0.17004
	protected Double cycling_purpose_visit                             = 1.36077;     // se =0.30082
	protected Double publictransport_purpose_visit                     = 0.60718;     // se =0.14053
	protected Double cardriver_purpose_shopping_grocery                = 1.37052;     // se =0.11717
	protected Double carpassenger_purpose_shopping_grocery             = 0.88638;     // se =0.15961
	protected Double cycling_purpose_shopping_grocery                  = 1.16448;     // se =0.26684
	protected Double publictransport_purpose_shopping_grocery         = -0.05492;     // se =0.12568
	protected Double cardriver_purpose_shopping_other                  = 0.79555;     // se =0.12630
	protected Double carpassenger_purpose_shopping_other               = 0.69786;     // se =0.17115
	protected Double cycling_purpose_shopping_other                    = 0.77284;     // se =0.31137
	protected Double publictransport_purpose_shopping_other            = 0.04539;     // se =0.13289
	protected Double cardriver_purpose_leisure_indoors                = -0.63673;     // se =0.12492
	protected Double carpassenger_purpose_leisure_indoors              = 0.58808;     // se =0.16901
	protected Double cycling_purpose_leisure_indoors                  = -0.26982;     // se =0.30621
	protected Double publictransport_purpose_leisure_indoors          = -0.22447;     // se =0.13215
	protected Double cardriver_purpose_leisure_outdoors                = 1.05643;     // se =0.19479
	protected Double carpassenger_purpose_leisure_outdoors             = 1.55418;     // se =0.20164
	protected Double cycling_purpose_leisure_outdoors                  = 1.28113;     // se =0.39687
	protected Double publictransport_purpose_leisure_outdoors          = 0.58383;     // se =0.17580
	protected Double cardriver_purpose_leisure_other                  = -0.10926;     // se =0.15034
	protected Double carpassenger_purpose_leisure_other                = 0.66209;     // se =0.17461
	protected Double cycling_purpose_leisure_other                     = 0.14924;     // se =0.32411
	protected Double publictransport_purpose_leisure_other             = 0.42172;     // se =0.13283
	protected Double cardriver_purpose_leisure_walk                   = -3.78775;     // se =0.24580
	protected Double carpassenger_purpose_leisure_walk                = -2.54114;     // se =0.27285
	protected Double cycling_purpose_leisure_walk                      = 0.06520;     // se =0.37195
	protected Double publictransport_purpose_leisure_walk             = -2.96962;     // se =0.33933
	protected Double cardriver_purpose_other                          = -0.19856;     // se =0.30329
	protected Double carpassenger_purpose_other                        = 0.73525;     // se =0.32164
	protected Double cycling_purpose_other                             = 1.33922;     // se =0.57605
	protected Double publictransport_purpose_other                    = -0.53730;     // se =0.35908
	
	protected Double cardriver_istourmode_withintour                  = -1.66102;  // se =4061.36615
	protected Double carpassenger_istourmode_withintour               = -1.27034;     // se =0.08714
	protected Double cycling_istourmode_withintour                    = -3.83550;     // se =0.22414
	protected Double publictransport_istourmode_withintour            = -2.93784;     // se =0.08172
	
	

	
	public WithinTourModeChoiceParameterSameAsTourmodeWithinTourPurpose() {
		init();
	}
	
	protected void init() {

		
		this.parameterGeneric.put("TIME", 					time);
		this.parameterGeneric.put("COST", 					cost);
		
		
		this.parameterWalk.putAll(this.parameterGeneric);
		this.parameterBike.putAll(this.parameterGeneric);
		this.parameterCar.putAll(this.parameterGeneric);
		this.parameterPassenger.putAll(this.parameterGeneric);
		this.parameterPt.putAll(this.parameterGeneric);
		
		// walk
		this.parameterWalk.put("CONST", 	walking);
		this.parameterWalk.put("ISTOURMODE", 	walking_istourmode);
		this.parameterWalk.put("WITHINTOUR", 	walking_withintour);
		this.parameterWalk.put("ISTOURMODE_WITHINTOUR", 	walking_istourmode_withintour);
	
		// bike
		this.parameterBike.put("CONST", 	cycling);
		this.parameterBike.put("ISTOURMODE", 	cycling_istourmode);
		this.parameterBike.put("WITHINTOUR", 	cycling_withintour);
		this.parameterBike.put("ISTOURMODE_WITHINTOUR", 	cycling_istourmode_withintour);
	
		this.parameterBike.put("PURPOSE_BUSINESS",  				cycling_purpose_business);
		this.parameterBike.put("PURPOSE_EDUCATION",  				cycling_purpose_education);
		this.parameterBike.put("PURPOSE_HOME",  						0.0);
		this.parameterBike.put("PURPOSE_LEISURE_INDOOR",  	cycling_purpose_leisure_indoors);
		this.parameterBike.put("PURPOSE_LEISURE_OUTDOOR",  	cycling_purpose_leisure_outdoors);
		this.parameterBike.put("PURPOSE_LEISURE_OTHER",  		cycling_purpose_leisure_other);
		this.parameterBike.put("PURPOSE_LEISURE_WALK",  		cycling_purpose_leisure_walk);
		this.parameterBike.put("PURPOSE_OTHER",  						cycling_purpose_other);
		this.parameterBike.put("PURPOSE_PRIVATE_BUSINESS",  cycling_purpose_private_business);
		this.parameterBike.put("PURPOSE_PRIVATE_VISIT",  		cycling_purpose_visit);
		this.parameterBike.put("PURPOSE_SERVICE",  					cycling_purpose_service);
		this.parameterBike.put("PURPOSE_SHOPPING_DAILY",  	cycling_purpose_shopping_grocery);
		this.parameterBike.put("PURPOSE_SHOPPING_OTHER",  	cycling_purpose_shopping_other);
		this.parameterBike.put("PURPOSE_BUSINESS",  				cycling_purpose_business);
	
		// car
		this.parameterCar.put("CONST", 	cardriver);
		this.parameterCar.put("ISTOURMODE", 	cardriver_istourmode);
		this.parameterCar.put("WITHINTOUR", 	cardriver_withintour);
		this.parameterCar.put("ISTOURMODE_WITHINTOUR", 	cardriver_istourmode_withintour);
	
		this.parameterCar.put("PURPOSE_BUSINESS",  				cardriver_purpose_business);
		this.parameterCar.put("PURPOSE_EDUCATION",  				cardriver_purpose_education);
		this.parameterCar.put("PURPOSE_HOME",  						0.0);
		this.parameterCar.put("PURPOSE_LEISURE_INDOOR",  	cardriver_purpose_leisure_indoors);
		this.parameterCar.put("PURPOSE_LEISURE_OUTDOOR",  	cardriver_purpose_leisure_outdoors);
		this.parameterCar.put("PURPOSE_LEISURE_OTHER",  		cardriver_purpose_leisure_other);
		this.parameterCar.put("PURPOSE_LEISURE_WALK",  		cardriver_purpose_leisure_walk);
		this.parameterCar.put("PURPOSE_OTHER",  						cardriver_purpose_other);
		this.parameterCar.put("PURPOSE_PRIVATE_BUSINESS",  cardriver_purpose_private_business);
		this.parameterCar.put("PURPOSE_PRIVATE_VISIT",  		cardriver_purpose_visit);
		this.parameterCar.put("PURPOSE_SERVICE",  					cardriver_purpose_service);
		this.parameterCar.put("PURPOSE_SHOPPING_DAILY",  	cardriver_purpose_shopping_grocery);
		this.parameterCar.put("PURPOSE_SHOPPING_OTHER",  	cardriver_purpose_shopping_other);
		this.parameterCar.put("PURPOSE_BUSINESS",  				cardriver_purpose_business);
	
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
		
		switch(mode) {
			case PEDESTRIAN:
				return parameterWalk;
			case BIKE:
				return parameterBike;
			case CAR:
				return parameterCar;
			case PASSENGER:
				return parameterPassenger;
			case PUBLICTRANSPORT:
				return parameterPt;
			default:
				throw new AssertionError("invalid mode: " + mode);
		}
	}

}
