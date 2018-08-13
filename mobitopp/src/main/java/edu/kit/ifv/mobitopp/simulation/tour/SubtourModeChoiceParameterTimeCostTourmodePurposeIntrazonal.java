package edu.kit.ifv.mobitopp.simulation.tour;


public class SubtourModeChoiceParameterTimeCostTourmodePurposeIntrazonal 
	extends SubtourModeChoiceParameterBase 
	implements SubtourModeChoiceParameter
{

	


	/*

Call:
mlogit(formula = mode ~ notavailable + time + cost | istourmode + 
    purpose. + intrazonal., data = trainingdata, reflevel = "walking", 
    method = "nr", print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.4360          0.3782          0.0869          0.0247          0.0742 

nr method
22 iterations, 0h:0m:2s 
g'(-H)^-1g = 6.38E-07 
gradient close to zero 

Coefficients :
                                            Estimate  Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                       35.30803 16287.11329    0.00   0.9983    
carpassenger:(intercept)                    -1.37309     0.22375   -6.14  8.4e-10 ***
cycling:(intercept)                         23.11187 14825.08267    0.00   0.9988    
publictransport:(intercept)                 -1.08685     0.24795   -4.38  1.2e-05 ***
notavailable                               -58.63326 15528.62460    0.00   0.9970    
time                                        -0.01757     0.00183   -9.60  < 2e-16 ***
cost                                        -0.11872     0.05650   -2.10   0.0356 *  
cardriver:istourmodeTRUE                   -33.81687 16287.11329    0.00   0.9983    
carpassenger:istourmodeTRUE                  2.70860     0.21161   12.80  < 2e-16 ***
cycling:istourmodeTRUE                     -23.13256 14825.08267    0.00   0.9988    
publictransport:istourmodeTRUE               0.89505     0.19018    4.71  2.5e-06 ***
cardriver:purpose.education                 -3.16655     1.30941   -2.42   0.0156 *  
carpassenger:purpose.education              -2.31892     0.64185   -3.61   0.0003 ***
cycling:purpose.education                    0.07802     1.48497    0.05   0.9581    
publictransport:purpose.education            0.68087     0.39337    1.73   0.0835 .  
cardriver:purpose.leisure_indoors           -1.96293     0.21526   -9.12  < 2e-16 ***
carpassenger:purpose.leisure_indoors        -1.24386     0.26245   -4.74  2.1e-06 ***
cycling:purpose.leisure_indoors             -1.44810     0.54952   -2.64   0.0084 ** 
publictransport:purpose.leisure_indoors     -1.72845     0.29128   -5.93  3.0e-09 ***
cardriver:purpose.leisure_other             -0.74489     0.33351   -2.23   0.0255 *  
carpassenger:purpose.leisure_other          -1.67219     0.39061   -4.28  1.9e-05 ***
cycling:purpose.leisure_other               -0.74807     1.29691   -0.58   0.5641    
publictransport:purpose.leisure_other       -0.49348     0.34256   -1.44   0.1497    
cardriver:purpose.leisure_outdoors           0.15713     0.49919    0.31   0.7529    
carpassenger:purpose.leisure_outdoors       -0.97783     0.48631   -2.01   0.0444 *  
cycling:purpose.leisure_outdoors             1.05680     1.23252    0.86   0.3912    
publictransport:purpose.leisure_outdoors     0.18999     0.45273    0.42   0.6747    
cardriver:purpose.leisure_walk             -24.36560 37594.32223    0.00   0.9995    
carpassenger:purpose.leisure_walk          -24.70544 53571.55304    0.00   0.9996    
cycling:purpose.leisure_walk                -0.61935 57937.75501    0.00   1.0000    
publictransport:purpose.leisure_walk       -22.68385 72245.47809    0.00   0.9997    
cardriver:purpose.private_business          -0.76097     0.23339   -3.26   0.0011 ** 
carpassenger:purpose.private_business       -1.31222     0.32005   -4.10  4.1e-05 ***
cycling:purpose.private_business            -1.37044     0.58900   -2.33   0.0200 *  
publictransport:purpose.private_business    -1.02521     0.31258   -3.28   0.0010 ** 
cardriver:purpose.service                    0.98144     0.57050    1.72   0.0854 .  
carpassenger:purpose.service                -2.23678     0.92036   -2.43   0.0151 *  
cycling:purpose.service                     16.72217  3274.19874    0.01   0.9959    
publictransport:purpose.service             -0.26815     0.74232   -0.36   0.7179    
cardriver:purpose.shopping_grocery          -0.67976     0.22954   -2.96   0.0031 ** 
carpassenger:purpose.shopping_grocery       -2.64025     0.42676   -6.19  6.1e-10 ***
cycling:purpose.shopping_grocery            -0.95684     0.60033   -1.59   0.1110    
publictransport:purpose.shopping_grocery    -2.49086     0.46249   -5.39  7.2e-08 ***
cardriver:purpose.shopping_other            -0.69458     0.24863   -2.79   0.0052 ** 
carpassenger:purpose.shopping_other         -1.03862     0.32689   -3.18   0.0015 ** 
cycling:purpose.shopping_other              -2.55176     1.12708   -2.26   0.0236 *  
publictransport:purpose.shopping_other      -0.69747     0.32037   -2.18   0.0295 *  
cardriver:purpose.visit                      1.47258     0.62786    2.35   0.0190 *  
carpassenger:purpose.visit                  -1.57338     0.52353   -3.01   0.0027 ** 
cycling:purpose.visit                        1.17961     0.79183    1.49   0.1363    
publictransport:purpose.visit                0.24824     0.43785    0.57   0.5707    
cardriver:intrazonal.TRUE                   -0.80938     0.14985   -5.40  6.6e-08 ***
carpassenger:intrazonal.TRUE                -0.43189     0.20890   -2.07   0.0387 *  
cycling:intrazonal.TRUE                      0.13073     0.38099    0.34   0.7315    
publictransport:intrazonal.TRUE             -2.27770     0.39800   -5.72  1.0e-08 ***
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -1690
McFadden R^2:  0.498 
Likelihood ratio test : chisq = 3360 (p.value = <2e-16)


		*/
	
	
	public SubtourModeChoiceParameterTimeCostTourmodePurposeIntrazonal() {

		
			cardriver                                    = 35.30803;  //se=16287.11329
			carpassenger                                 = -1.37309;      //se=0.22375
			cycling                                      = 23.11187;  //se=14825.08267
			publictransport                              = -1.08685;      //se=0.24795
			
			notavailable                                = -58.63326;  //se=15528.62460
			
			time                                         = -0.01757;      //se=0.00183
			cost                                         = -0.11872;      //se=0.05650
			
			cardriver_istourmode                        = -33.81687;  //se=16287.11329
			carpassenger_istourmode                       = 2.70860;      //se=0.21161
			cycling_istourmode                          = -23.13256;  //se=14825.08267
			publictransport_istourmode                    = 0.89505;      //se=0.19018
			
			cardriver_purpose_education                  = -3.16655;      //se=1.30941
			cardriver_purpose_leisure_indoors            = -1.96293;      //se=0.21526
			cardriver_purpose_leisure_other              = -0.74489;      //se=0.33351
			cardriver_purpose_leisure_outdoors            = 0.15713;      //se=0.49919
			cardriver_purpose_leisure_walk              = -24.36560;  //se=37594.32223
			cardriver_purpose_private_business           = -0.76097;      //se=0.23339
			cardriver_purpose_service                     = 0.98144;      //se=0.57050
			cardriver_purpose_shopping_grocery           = -0.67976;      //se=0.22954
			cardriver_purpose_shopping_other             = -0.69458;      //se=0.24863
			cardriver_purpose_visit                       = 1.47258;      //se=0.62786
			carpassenger_purpose_education               = -2.31892;      //se=0.64185
			carpassenger_purpose_leisure_indoors         = -1.24386;      //se=0.26245
			carpassenger_purpose_leisure_other           = -1.67219;      //se=0.39061
			carpassenger_purpose_leisure_outdoors        = -0.97783;      //se=0.48631
			carpassenger_purpose_leisure_walk           = -24.70544;  //se=53571.55304
			carpassenger_purpose_private_business        = -1.31222;      //se=0.32005
			carpassenger_purpose_service                 = -2.23678;      //se=0.92036
			carpassenger_purpose_shopping_grocery        = -2.64025;      //se=0.42676
			carpassenger_purpose_shopping_other          = -1.03862;      //se=0.32689
			carpassenger_purpose_visit                   = -1.57338;      //se=0.52353
			cycling_purpose_education                     = 0.07802;      //se=1.48497
			cycling_purpose_leisure_indoors              = -1.44810;      //se=0.54952
			cycling_purpose_leisure_other                = -0.74807;      //se=1.29691
			cycling_purpose_leisure_outdoors              = 1.05680;      //se=1.23252
			cycling_purpose_leisure_walk                 = -0.61935;  //se=57937.75501
			cycling_purpose_private_business             = -1.37044;      //se=0.58900
			cycling_purpose_service                      = 16.72217;   //se=3274.19874
			cycling_purpose_shopping_grocery             = -0.95684;      //se=0.60033
			cycling_purpose_shopping_other               = -2.55176;      //se=1.12708
			cycling_purpose_visit                         = 1.17961;      //se=0.79183
			publictransport_purpose_education             = 0.68087;      //se=0.39337
			publictransport_purpose_leisure_indoors      = -1.72845;      //se=0.29128
			publictransport_purpose_leisure_other        = -0.49348;      //se=0.34256
			publictransport_purpose_leisure_outdoors      = 0.18999;      //se=0.45273
			publictransport_purpose_leisure_walk        = -22.68385;  //se=72245.47809
			publictransport_purpose_private_business     = -1.02521;      //se=0.31258
			publictransport_purpose_service              = -0.26815;      //se=0.74232
			publictransport_purpose_shopping_grocery     = -2.49086;      //se=0.46249
			publictransport_purpose_shopping_other       = -0.69747;      //se=0.32037
			publictransport_purpose_visit                 = 0.24824;      //se=0.43785
			
			cardriver_intrazonal                         = -0.80938;      //se=0.14985
			carpassenger_intrazonal                      = -0.43189;      //se=0.20890
			cycling_intrazonal                            = 0.13073;      //se=0.38099
			publictransport_intrazonal                   = -2.27770;      //se=0.39800
	
		
		
		//
		

		init();
	}

	

}
