package edu.kit.ifv.mobitopp.simulation.tour;


public class SubtourModeChoiceParameterTimeCostTourmodePurpose 
	extends SubtourModeChoiceParameterBase 
	implements SubtourModeChoiceParameter
{

	


	/*
Call:
mlogit(formula = mode ~ notavailable + time + cost | istourmode + 
    purpose., data = trainingdata, reflevel = "walking", method = "nr", 
    print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.4360          0.3782          0.0869          0.0247          0.0742 

nr method
22 iterations, 0h:0m:2s 
g'(-H)^-1g = 6.29E-07 
gradient close to zero 

Coefficients :
                                            Estimate  Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                       35.02751 16339.31030    0.00  0.99829    
carpassenger:(intercept)                    -1.63537     0.21632   -7.56  4.0e-14 ***
cycling:(intercept)                         22.94956 14829.29713    0.00  0.99877    
publictransport:(intercept)                 -1.57129     0.24104   -6.52  7.1e-11 ***
notavailable                               -58.75347 15535.77564    0.00  0.99698    
time                                        -0.02210     0.00186  -11.89  < 2e-16 ***
cost                                        -0.07578     0.05946   -1.27  0.20249    
cardriver:istourmodeTRUE                   -33.91381 16339.31030    0.00  0.99834    
carpassenger:istourmodeTRUE                  2.67986     0.21197   12.64  < 2e-16 ***
cycling:istourmodeTRUE                     -23.01646 14829.29713    0.00  0.99876    
publictransport:istourmodeTRUE               1.08300     0.18531    5.84  5.1e-09 ***
cardriver:purpose.education                 -3.12522     1.30875   -2.39  0.01694 *  
carpassenger:purpose.education              -2.22138     0.63950   -3.47  0.00051 ***
cycling:purpose.education                    0.25839     1.47907    0.17  0.86132    
publictransport:purpose.education            0.94363     0.39345    2.40  0.01647 *  
cardriver:purpose.leisure_indoors           -1.98153     0.21312   -9.30  < 2e-16 ***
carpassenger:purpose.leisure_indoors        -1.20857     0.26265   -4.60  4.2e-06 ***
cycling:purpose.leisure_indoors             -1.42898     0.55062   -2.60  0.00945 ** 
publictransport:purpose.leisure_indoors     -1.70083     0.28480   -5.97  2.3e-09 ***
cardriver:purpose.leisure_other             -0.79714     0.33040   -2.41  0.01584 *  
carpassenger:purpose.leisure_other          -1.69836     0.39206   -4.33  1.5e-05 ***
cycling:purpose.leisure_other               -0.74213     1.30247   -0.57  0.56882    
publictransport:purpose.leisure_other       -0.54445     0.33218   -1.64  0.10121    
cardriver:purpose.leisure_outdoors           0.14443     0.49594    0.29  0.77088    
carpassenger:purpose.leisure_outdoors       -1.01862     0.49020   -2.08  0.03771 *  
cycling:purpose.leisure_outdoors             1.12965     1.23387    0.92  0.35991    
publictransport:purpose.leisure_outdoors     0.18843     0.44210    0.43  0.66995    
cardriver:purpose.leisure_walk             -24.75363 41409.66288    0.00  0.99952    
carpassenger:purpose.leisure_walk          -24.88000 56347.15626    0.00  0.99965    
cycling:purpose.leisure_walk                -0.25213 62326.99999    0.00  1.00000    
publictransport:purpose.leisure_walk       -23.68813 74656.89800    0.00  0.99975    
cardriver:purpose.private_business          -0.79033     0.23027   -3.43  0.00060 ***
carpassenger:purpose.private_business       -1.30852     0.32092   -4.08  4.6e-05 ***
cycling:purpose.private_business            -1.27669     0.57948   -2.20  0.02758 *  
publictransport:purpose.private_business    -1.03189     0.30488   -3.38  0.00071 ***
cardriver:purpose.service                    1.05260     0.57106    1.84  0.06529 .  
carpassenger:purpose.service                -2.30086     0.93771   -2.45  0.01414 *  
cycling:purpose.service                     16.83204  3282.74795    0.01  0.99591    
publictransport:purpose.service             -0.06404     0.74154   -0.09  0.93118    
cardriver:purpose.shopping_grocery          -0.76799     0.22416   -3.43  0.00061 ***
carpassenger:purpose.shopping_grocery       -2.62753     0.42720   -6.15  7.7e-10 ***
cycling:purpose.shopping_grocery            -0.88533     0.59635   -1.48  0.13765    
publictransport:purpose.shopping_grocery    -2.58234     0.45481   -5.68  1.4e-08 ***
cardriver:purpose.shopping_other            -0.74849     0.24491   -3.06  0.00224 ** 
carpassenger:purpose.shopping_other         -1.05047     0.32661   -3.22  0.00130 ** 
cycling:purpose.shopping_other              -2.57889     1.13060   -2.28  0.02255 *  
publictransport:purpose.shopping_other      -0.74350     0.31148   -2.39  0.01699 *  
cardriver:purpose.visit                      1.42185     0.62364    2.28  0.02261 *  
carpassenger:purpose.visit                  -1.66129     0.52988   -3.14  0.00172 ** 
cycling:purpose.visit                        1.14826     0.78898    1.46  0.14557    
publictransport:purpose.visit                0.00850     0.41116    0.02  0.98350    
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -1730
McFadden R^2:  0.487 
Likelihood ratio test : chisq = 3280 (p.value = <2e-16)


		*/
	
	
	public SubtourModeChoiceParameterTimeCostTourmodePurpose() {
		
		cardriver                                    = 35.02751;  //se=16339.31030
		carpassenger                                 = -1.63537;      //se=0.21632
		cycling                                      = 22.94956;  //se=14829.29713
		publictransport                              = -1.57129;      //se=0.24104
		
		notavailable                                = -58.75347;  //se=15535.77564
		
		time                                         = -0.02210;      //se=0.00186
		cost                                         = -0.07578;      //se=0.05946
		
		cardriver_istourmode                        = -33.91381;  //se=16339.31030
		carpassenger_istourmode                       = 2.67986;      //se=0.21197
		cycling_istourmode                          = -23.01646;  //se=14829.29713
		publictransport_istourmode                    = 1.08300;      //se=0.18531
		
		cardriver_purpose_education                  = -3.12522;      //se=1.30875
		cardriver_purpose_leisure_indoors            = -1.98153;      //se=0.21312
		cardriver_purpose_leisure_other              = -0.79714;      //se=0.33040
		cardriver_purpose_leisure_outdoors            = 0.14443;      //se=0.49594
		cardriver_purpose_leisure_walk              = -24.75363;  //se=41409.66288
		cardriver_purpose_private_business           = -0.79033;      //se=0.23027
		cardriver_purpose_service                     = 1.05260;      //se=0.57106
		cardriver_purpose_shopping_grocery           = -0.76799;      //se=0.22416
		cardriver_purpose_shopping_other             = -0.74849;      //se=0.24491
		cardriver_purpose_visit                       = 1.42185;      //se=0.62364
		carpassenger_purpose_education               = -2.22138;      //se=0.63950
		carpassenger_purpose_leisure_indoors         = -1.20857;      //se=0.26265
		carpassenger_purpose_leisure_other           = -1.69836;      //se=0.39206
		carpassenger_purpose_leisure_outdoors        = -1.01862;      //se=0.49020
		carpassenger_purpose_leisure_walk           = -24.88000;  //se=56347.15626
		carpassenger_purpose_private_business        = -1.30852;      //se=0.32092
		carpassenger_purpose_service                 = -2.30086;      //se=0.93771
		carpassenger_purpose_shopping_grocery        = -2.62753;      //se=0.42720
		carpassenger_purpose_shopping_other          = -1.05047;      //se=0.32661
		carpassenger_purpose_visit                   = -1.66129;      //se=0.52988
		cycling_purpose_education                     = 0.25839;      //se=1.47907
		cycling_purpose_leisure_indoors              = -1.42898;      //se=0.55062
		cycling_purpose_leisure_other                = -0.74213;      //se=1.30247
		cycling_purpose_leisure_outdoors              = 1.12965;      //se=1.23387
		cycling_purpose_leisure_walk                 = -0.25213;  //se=62326.99999
		cycling_purpose_private_business             = -1.27669;      //se=0.57948
		cycling_purpose_service                      = 16.83204;   //se=3282.74795
		cycling_purpose_shopping_grocery             = -0.88533;      //se=0.59635
		cycling_purpose_shopping_other               = -2.57889;      //se=1.13060
		cycling_purpose_visit                         = 1.14826;      //se=0.78898
		publictransport_purpose_education             = 0.94363;      //se=0.39345
		publictransport_purpose_leisure_indoors      = -1.70083;      //se=0.28480
		publictransport_purpose_leisure_other        = -0.54445;      //se=0.33218
		publictransport_purpose_leisure_outdoors      = 0.18843;      //se=0.44210
		publictransport_purpose_leisure_walk        = -23.68813;  //se=74656.89800
		publictransport_purpose_private_business     = -1.03189;      //se=0.30488
		publictransport_purpose_service              = -0.06404;      //se=0.74154
		publictransport_purpose_shopping_grocery     = -2.58234;      //se=0.45481
		publictransport_purpose_shopping_other       = -0.74350;      //se=0.31148
		publictransport_purpose_visit                 = 0.00850;      //se=0.41116

		init();
	}

	

}
