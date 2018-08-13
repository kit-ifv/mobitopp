package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.Mode;

public class WithinTourModeChoiceParameterTourmodePurposeWithinTour 
	extends WithinTourModeChoiceParameterBase 
	implements WithinTourModeChoiceParameter 
{

	

	/*
Call:
mlogit(formula = mode ~ time + cost | tourmode. + purpose. + 
    tourmode.:within_tour., data = trainingdata, reflevel = "walking", 
    method = "nr", print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2744          0.4093          0.1334          0.0511          0.1318 

nr method
20 iterations, 0h:16m:22s 
g'(-H)^-1g = 9.81E-07 
gradient close to zero 

Coefficients :
                                                              Estimate   Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                                        20.154980  3827.044322    0.01  0.99580    
carpassenger:(intercept)                                     -4.547468     0.355301  -12.80  < 2e-16 ***
cycling:(intercept)                                          -5.413490     1.189322   -4.55  5.3e-06 ***
publictransport:(intercept)                                  -3.260421     0.364522   -8.94  < 2e-16 ***
time                                                         -0.019859     0.000492  -40.39  < 2e-16 ***
cost                                                         -0.171093     0.012443  -13.75  < 2e-16 ***
cardriver:tourmode.cycling                                    4.371124  9341.969780    0.00  0.99963    
carpassenger:tourmode.cycling                                 2.789515     0.275538   10.12  < 2e-16 ***
cycling:tourmode.cycling                                     10.686395     0.254629   41.97  < 2e-16 ***
publictransport:tourmode.cycling                              3.230010     0.259656   12.44  < 2e-16 ***
cardriver:tourmode.publictransport                            2.660434  6145.429519    0.00  0.99965    
carpassenger:tourmode.publictransport                         2.864906     0.090247   31.75  < 2e-16 ***
cycling:tourmode.publictransport                              2.135014     0.378687    5.64  1.7e-08 ***
publictransport:tourmode.publictransport                      5.837201     0.081043   72.03  < 2e-16 ***
cardriver:tourmode.cardriver                                -16.492162  3827.044303    0.00  0.99656    
carpassenger:tourmode.cardriver                               3.466537     0.117507   29.50  < 2e-16 ***
cycling:tourmode.cardriver                                    3.570626     0.352444   10.13  < 2e-16 ***
publictransport:tourmode.cardriver                            2.900368     0.146732   19.77  < 2e-16 ***
cardriver:tourmode.carpassenger                               3.078712  4777.706435    0.00  0.99949    
carpassenger:tourmode.carpassenger                            5.946077     0.075920   78.32  < 2e-16 ***
cycling:tourmode.carpassenger                                 2.121187     0.431251    4.92  8.7e-07 ***
publictransport:tourmode.carpassenger                         3.313806     0.095672   34.64  < 2e-16 ***
cardriver:purpose.business                                    0.796462     0.153517    5.19  2.1e-07 ***
carpassenger:purpose.business                                 1.472250     0.227014    6.49  8.9e-11 ***
cycling:purpose.business                                      1.848402     0.367683    5.03  5.0e-07 ***
publictransport:purpose.business                              0.740726     0.178397    4.15  3.3e-05 ***
cardriver:purpose.education                                   0.305235     0.368909    0.83  0.40801    
carpassenger:purpose.education                               -0.101409     0.243148   -0.42  0.67663    
cycling:purpose.education                                     0.346576     0.376213    0.92  0.35694    
publictransport:purpose.education                             0.391123     0.153747    2.54  0.01096 *  
cardriver:purpose.service                                     1.997506     0.159511   12.52  < 2e-16 ***
carpassenger:purpose.service                                  1.626208     0.208899    7.78  6.9e-15 ***
cycling:purpose.service                                       1.933486     0.379411    5.10  3.5e-07 ***
publictransport:purpose.service                               0.523929     0.193139    2.71  0.00667 ** 
cardriver:purpose.private_business                            0.788723     0.121858    6.47  9.6e-11 ***
carpassenger:purpose.private_business                         1.067485     0.177213    6.02  1.7e-09 ***
cycling:purpose.private_business                              0.989219     0.266970    3.71  0.00021 ***
publictransport:purpose.private_business                      0.316413     0.135452    2.34  0.01949 *  
cardriver:purpose.visit                                       1.271306     0.148540    8.56  < 2e-16 ***
carpassenger:purpose.visit                                    1.793907     0.179836    9.98  < 2e-16 ***
cycling:purpose.visit                                         1.653525     0.302813    5.46  4.7e-08 ***
publictransport:purpose.visit                                 0.661028     0.146648    4.51  6.6e-06 ***
cardriver:purpose.shopping_grocery                            1.266438     0.115679   10.95  < 2e-16 ***
carpassenger:purpose.shopping_grocery                         1.061028     0.169251    6.27  3.6e-10 ***
cycling:purpose.shopping_grocery                              1.529705     0.259531    5.89  3.8e-09 ***
publictransport:purpose.shopping_grocery                     -0.010823     0.129629   -0.08  0.93346    
cardriver:purpose.shopping_other                              0.599018     0.124196    4.82  1.4e-06 ***
carpassenger:purpose.shopping_other                           0.914319     0.176852    5.17  2.3e-07 ***
cycling:purpose.shopping_other                                0.992699     0.310427    3.20  0.00138 ** 
publictransport:purpose.shopping_other                       -0.024389     0.135447   -0.18  0.85710    
cardriver:purpose.leisure_indoors                            -0.647353     0.126116   -5.13  2.9e-07 ***
carpassenger:purpose.leisure_indoors                          0.810015     0.175840    4.61  4.1e-06 ***
cycling:purpose.leisure_indoors                              -0.043671     0.298128   -0.15  0.88354    
publictransport:purpose.leisure_indoors                      -0.132464     0.134829   -0.98  0.32587    
cardriver:purpose.leisure_outdoors                            0.749097     0.181499    4.13  3.7e-05 ***
carpassenger:purpose.leisure_outdoors                         1.479215     0.207153    7.14  9.3e-13 ***
cycling:purpose.leisure_outdoors                              1.556677     0.397514    3.92  9.0e-05 ***
publictransport:purpose.leisure_outdoors                      0.523775     0.171414    3.06  0.00225 ** 
cardriver:purpose.leisure_other                               0.143783     0.149342    0.96  0.33566    
carpassenger:purpose.leisure_other                            1.176243     0.182843    6.43  1.3e-10 ***
cycling:purpose.leisure_other                                 0.885994     0.335404    2.64  0.00825 ** 
publictransport:purpose.leisure_other                         0.582845     0.140530    4.15  3.4e-05 ***
cardriver:purpose.leisure_walk                               -3.872237     0.229686  -16.86  < 2e-16 ***
carpassenger:purpose.leisure_walk                            -2.672115     0.295959   -9.03  < 2e-16 ***
cycling:purpose.leisure_walk                                 -0.407068     0.353081   -1.15  0.24895    
publictransport:purpose.leisure_walk                         -3.253589     0.400977   -8.11  4.4e-16 ***
cardriver:purpose.other                                       0.572083     0.387879    1.47  0.14024    
carpassenger:purpose.other                                    0.676570     0.351545    1.92  0.05428 .  
cycling:purpose.other                                        -1.195973     1.172583   -1.02  0.30775    
publictransport:purpose.other                                -0.325922     0.360273   -0.90  0.36565    
cardriver:tourmode.walking:within_tour.TRUE                   0.947277  5686.611741    0.00  0.99987    
carpassenger:tourmode.walking:within_tour.TRUE                0.906345     0.331208    2.74  0.00621 ** 
cycling:tourmode.walking:within_tour.TRUE                    -0.714281     1.203259   -0.59  0.55276    
publictransport:tourmode.walking:within_tour.TRUE             1.187980     0.356282    3.33  0.00085 ***
cardriver:tourmode.cycling:within_tour.TRUE                  -2.103058 13959.315315    0.00  0.99988    
carpassenger:tourmode.cycling:within_tour.TRUE               -1.343817     0.465124   -2.89  0.00386 ** 
cycling:tourmode.cycling:within_tour.TRUE                    -5.439352     1.170028   -4.65  3.3e-06 ***
publictransport:tourmode.cycling:within_tour.TRUE            -1.366544     0.460426   -2.97  0.00300 ** 
cardriver:tourmode.publictransport:within_tour.TRUE          -0.019020  8952.622379    0.00  1.00000    
carpassenger:tourmode.publictransport:within_tour.TRUE       -2.092190     0.336915   -6.21  5.3e-10 ***
cycling:tourmode.publictransport:within_tour.TRUE            -3.626138     1.260397   -2.88  0.00401 ** 
publictransport:tourmode.publictransport:within_tour.TRUE    -3.311147     0.353664   -9.36  < 2e-16 ***
cardriver:tourmode.cardriver:within_tour.TRUE                -2.993961     0.389025   -7.70  1.4e-14 ***
carpassenger:tourmode.cardriver:within_tour.TRUE             -2.125550     0.344585   -6.17  6.9e-10 ***
cycling:tourmode.cardriver:within_tour.TRUE                  -3.523051     1.209524   -2.91  0.00358 ** 
publictransport:tourmode.cardriver:within_tour.TRUE          -2.109916     0.385719   -5.47  4.5e-08 ***
cardriver:tourmode.carpassenger:within_tour.TRUE             -0.319507  6352.758379    0.00  0.99996    
carpassenger:tourmode.carpassenger:within_tour.TRUE          -1.758649     0.327328   -5.37  7.8e-08 ***
cycling:tourmode.carpassenger:within_tour.TRUE               -2.711278     1.299739   -2.09  0.03698 *  
publictransport:tourmode.carpassenger:within_tour.TRUE       -1.537788     0.369173   -4.17  3.1e-05 ***
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -22400
McFadden R^2:  0.787 
Likelihood ratio test : chisq = 165000 (p.value = <2e-16)

		*/
	
	protected Double walking                                	= 0.0 ;
	protected Double walking_tourmode_cycling                	= 0.0 ;
	protected Double walking_tourmode_publictransport        	= 0.0 ;
	protected Double walking_tourmode_cardriver              	= 0.0 ;
	protected Double walking_tourmode_carpassenger           	= 0.0 ;
	
	
	protected Double cardriver                                                    =  20.154980 ;
	protected Double carpassenger                                                 = -4.547468 ;
	protected Double cycling                                                      = -5.413490 ;
	protected Double publictransport                                              = -3.260421 ;
	
	protected Double time                                                         = -0.019859 ;
	protected Double cost                                                         = -0.171093 ;
	
	protected Double cardriver_tourmode_cardriver                                 =  -16.492162 ;
	protected Double cardriver_tourmode_carpassenger                              =  3.078712 ;
	protected Double cardriver_tourmode_cycling                                   =  4.371124 ;
	protected Double cardriver_tourmode_publictransport                           =  2.660434 ;
	protected Double carpassenger_tourmode_cardriver                              =  3.466537 ;
	protected Double carpassenger_tourmode_carpassenger                           =  5.946077 ;
	protected Double carpassenger_tourmode_cycling                                =  2.789515 ;
	protected Double carpassenger_tourmode_publictransport                        =  2.864906 ;
	protected Double cycling_tourmode_cardriver                                   =  3.570626 ;
	protected Double cycling_tourmode_carpassenger                                =  2.121187 ;
	protected Double cycling_tourmode_cycling                                     =  10.686395 ;
	protected Double cycling_tourmode_publictransport                             =  2.135014 ;
	protected Double publictransport_tourmode_cardriver                           =  2.900368 ;
	protected Double publictransport_tourmode_carpassenger                        =  3.313806 ;
	protected Double publictransport_tourmode_cycling                             =  3.230010 ;
	protected Double publictransport_tourmode_publictransport                     =  5.837201 ;
	
	protected Double cardriver_purpose_business                                   =  0.796462 ;
	protected Double cardriver_purpose_education                                  =  0.305235 ;
	protected Double cardriver_purpose_leisure_indoors                            = -0.647353 ;
	protected Double cardriver_purpose_leisure_other                              =  0.143783 ;
	protected Double cardriver_purpose_leisure_outdoors                           =  0.749097 ;
	protected Double cardriver_purpose_leisure_walk                               = -3.872237 ;
	protected Double cardriver_purpose_other                                      =  0.572083 ;
	protected Double cardriver_purpose_private_business                           =  0.788723 ;
	protected Double cardriver_purpose_service                                    =  1.997506 ;
	protected Double cardriver_purpose_shopping_grocery                           =  1.266438 ;
	protected Double cardriver_purpose_shopping_other                             =  0.599018 ;
	protected Double cardriver_purpose_visit                                      =  1.271306 ;
	protected Double carpassenger_purpose_business                                =  1.472250 ;
	protected Double carpassenger_purpose_education                               = -0.101409 ;
	protected Double carpassenger_purpose_leisure_indoors                         =  0.810015 ;
	protected Double carpassenger_purpose_leisure_other                           =  1.176243 ;
	protected Double carpassenger_purpose_leisure_outdoors                        =  1.479215 ;
	protected Double carpassenger_purpose_leisure_walk                            = -2.672115 ;
	protected Double carpassenger_purpose_other                                   =  0.676570 ;
	protected Double carpassenger_purpose_private_business                        =  1.067485 ;
	protected Double carpassenger_purpose_service                                 =  1.626208 ;
	protected Double carpassenger_purpose_shopping_grocery                        =  1.061028 ;
	protected Double carpassenger_purpose_shopping_other                          =  0.914319 ;
	protected Double carpassenger_purpose_visit                                   =  1.793907 ;
	protected Double cycling_purpose_business                                     =  1.848402 ;
	protected Double cycling_purpose_education                                    =  0.346576 ;
	protected Double cycling_purpose_leisure_indoors                              = -0.043671 ;
	protected Double cycling_purpose_leisure_other                                =  0.885994 ;
	protected Double cycling_purpose_leisure_outdoors                             =  1.556677 ;
	protected Double cycling_purpose_leisure_walk                                 = -0.407068 ;
	protected Double cycling_purpose_other                                        = -1.195973 ;
	protected Double cycling_purpose_private_business                             =  0.989219 ;
	protected Double cycling_purpose_service                                      =  1.933486 ;
	protected Double cycling_purpose_shopping_grocery                             =  1.529705 ;
	protected Double cycling_purpose_shopping_other                               =  0.992699 ;
	protected Double cycling_purpose_visit                                        =  1.653525 ;
	protected Double publictransport_purpose_business                             =  0.740726 ;
	protected Double publictransport_purpose_education                            =  0.391123 ;
	protected Double publictransport_purpose_leisure_indoors                      = -0.132464 ;
	protected Double publictransport_purpose_leisure_other                        =  0.582845 ;
	protected Double publictransport_purpose_leisure_outdoors                     =  0.523775 ;
	protected Double publictransport_purpose_leisure_walk                         = -3.253589 ;
	protected Double publictransport_purpose_other                                = -0.325922 ;
	protected Double publictransport_purpose_private_business                     =  0.316413 ;
	protected Double publictransport_purpose_service                              =  0.523929 ;
	protected Double publictransport_purpose_shopping_grocery                     = -0.010823 ;
	protected Double publictransport_purpose_shopping_other                       = -0.024389 ;
	protected Double publictransport_purpose_visit                                =  0.661028 ;
	
	protected Double cardriver_tourmode_cardriver_withintour                			= -2.993961 ;
	protected Double cardriver_tourmode_carpassenger_withintour             			= -0.319507 ;
	protected Double cardriver_tourmode_cycling_withintour                  			= -2.103058 ;
	protected Double cardriver_tourmode_publictransport_withintour          			= -0.019020 ;
	protected Double cardriver_tourmode_walking_withintour                  			=  0.947277 ;
	protected Double carpassenger_tourmode_cardriver_withintour             			= -2.125550 ;
	protected Double carpassenger_tourmode_carpassenger_withintour          			= -1.758649 ;
	protected Double carpassenger_tourmode_cycling_withintour               			= -1.343817 ;
	protected Double carpassenger_tourmode_publictransport_withintour       			= -2.092190 ;
	protected Double carpassenger_tourmode_walking_withintour               			=  0.906345 ;
	protected Double cycling_tourmode_cardriver_withintour                  			= -3.523051 ;
	protected Double cycling_tourmode_carpassenger_withintour               			= -2.711278 ;
	protected Double cycling_tourmode_cycling_withintour                    			= -5.439352 ;
	protected Double cycling_tourmode_publictransport_withintour            			= -3.626138 ;
	protected Double cycling_tourmode_walking_withintour                    			= -0.714281 ;
	protected Double publictransport_tourmode_cardriver_withintour          			= -2.109916 ;
	protected Double publictransport_tourmode_carpassenger_withintour       			= -1.537788 ;
	protected Double publictransport_tourmode_cycling_withintour            			= -1.366544 ;
	protected Double publictransport_tourmode_publictransport_withintour    			= -3.311147 ;
	protected Double publictransport_tourmode_walking_withintour            			=  1.187980 ;

	
	public WithinTourModeChoiceParameterTourmodePurposeWithinTour() {
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
		
		this.parameterBike.put("TOURMODE_BIKE_WITHINTOUR", 	cycling_tourmode_cycling_withintour);
		this.parameterBike.put("TOURMODE_PT_WITHINTOUR", 	cycling_tourmode_publictransport_withintour);
		this.parameterBike.put("TOURMODE_CAR_WITHINTOUR", 	cycling_tourmode_cardriver_withintour);
		this.parameterBike.put("TOURMODE_PASSENGER_WITHINTOUR", 	cycling_tourmode_carpassenger_withintour);
	
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
		this.parameterCar.put("TOURMODE_BIKE", 	cardriver_tourmode_cycling);
		this.parameterCar.put("TOURMODE_PT", 	cardriver_tourmode_publictransport);
		this.parameterCar.put("TOURMODE_CAR", 	cardriver_tourmode_cardriver);
		this.parameterCar.put("TOURMODE_PASSENGER", 	cardriver_tourmode_carpassenger);
		
		this.parameterCar.put("TOURMODE_BIKE_WITHINTOUR", 	cardriver_tourmode_cycling_withintour);
		this.parameterCar.put("TOURMODE_PT_WITHINTOUR", 	cardriver_tourmode_publictransport_withintour);
		this.parameterCar.put("TOURMODE_CAR_WITHINTOUR", 	cardriver_tourmode_cardriver_withintour);
		this.parameterCar.put("TOURMODE_PASSENGER_WITHINTOUR", 	cardriver_tourmode_carpassenger_withintour);
	
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
		this.parameterPassenger.put("TOURMODE_BIKE", 	carpassenger_tourmode_cycling);
		this.parameterPassenger.put("TOURMODE_PT", 	carpassenger_tourmode_publictransport);
		this.parameterPassenger.put("TOURMODE_CAR", 	carpassenger_tourmode_cardriver);
		this.parameterPassenger.put("TOURMODE_PASSENGER", 	carpassenger_tourmode_carpassenger);
	
		this.parameterPassenger.put("TOURMODE_BIKE_WITHINTOUR", 	carpassenger_tourmode_cycling_withintour);
		this.parameterPassenger.put("TOURMODE_PT_WITHINTOUR", 	carpassenger_tourmode_publictransport_withintour);
		this.parameterPassenger.put("TOURMODE_CAR_WITHINTOUR", 	carpassenger_tourmode_cardriver_withintour);
		this.parameterPassenger.put("TOURMODE_PASSENGER_WITHINTOUR", 	carpassenger_tourmode_carpassenger_withintour);
	
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
		this.parameterPt.put("TOURMODE_BIKE", 	publictransport_tourmode_cycling);
		this.parameterPt.put("TOURMODE_PT", 	publictransport_tourmode_publictransport);
		this.parameterPt.put("TOURMODE_CAR", 	publictransport_tourmode_cardriver);
		this.parameterPt.put("TOURMODE_PASSENGER", 	publictransport_tourmode_carpassenger);
		
		this.parameterPt.put("TOURMODE_BIKE_WITHINTOUR", 	publictransport_tourmode_cycling_withintour);
		this.parameterPt.put("TOURMODE_PT_WITHINTOUR", 	publictransport_tourmode_publictransport_withintour);
		this.parameterPt.put("TOURMODE_CAR_WITHINTOUR", 	publictransport_tourmode_cardriver_withintour);
		this.parameterPt.put("TOURMODE_PASSENGER_WITHINTOUR", 	publictransport_tourmode_carpassenger_withintour);
	
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
