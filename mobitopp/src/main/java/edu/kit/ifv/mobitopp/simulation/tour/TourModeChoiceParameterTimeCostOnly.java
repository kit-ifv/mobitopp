package edu.kit.ifv.mobitopp.simulation.tour;



public class TourModeChoiceParameterTimeCostOnly extends TourModeChoiceParameterTimeCostByEmpAgeSexBase 
	implements TourModeChoiceParameter
{

	


	/*
	 
	 Call:
mlogit(formula = mode ~ notavailable + time + cost, data = trainingdata, 
    reflevel = "walking", method = "nr", print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2876          0.3914          0.1279          0.0544          0.1388 

nr method
24 iterations, 0h:0m:5s 
g'(-H)^-1g = 4.85E-07 
gradient close to zero 

Coefficients :
                               Estimate  Std. Error t-value Pr(>|t|)    
cardriver:(intercept)         -0.336482    0.015843  -21.24   <2e-16 ***
carpassenger:(intercept)      -2.563598    0.020054 -127.84   <2e-16 ***
cycling:(intercept)           -2.488457    0.020779 -119.76   <2e-16 ***
publictransport:(intercept)   -0.755515    0.016153  -46.77   <2e-16 ***
notavailable                 -25.360802 2366.286565   -0.01     0.99    
time                          -0.052200    0.000576  -90.62   <2e-16 ***
cost                          -0.574752    0.009355  -61.44   <2e-16 ***
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -59600
McFadden R^2:  0.273 
Likelihood ratio test : chisq = 44900 (p.value = <2e-16)


	 */
	
	
	public TourModeChoiceParameterTimeCostOnly() {
	
		
		
		
		/* *********************************************************************************************** */

		cardriver                   = -0.336482;
		carpassenger                = -2.563598;
		cycling                     = -2.488457;
		publictransport             = -0.755515;

		time                        = -0.052200;
		cost                        = -0.574752;
		
		notavailable                               = 0.0;
		
		
		time_sex_female                             = 0.0;
		cost_sex_female                             = 0.0;
		
		time_age_06to09                             = 0.0;
		time_age_10to17                             = 0.0;
		time_age_18to25                              = 0.0;
		time_age_26to35                             = 0.0;
		time_age_51to60                              = 0.0;
		time_age_61to70                             = 0.0;
		time_age_71plus                             = 0.0;
		
		time_employment_parttime                    = 0.0;
		time_employment_marginal                     = 0.0;
		time_employment_homekeeper                  = 0.0;
		time_employment_unemployed                   = 0.0;
		time_employment_retired                      = 0.0;
		time_employment_pupil                       = 0.0;
		time_employment_student                      = 0.0;
		time_employment_apprentice                  = 0.0;
		time_employment_other                       = 0.0;
		
		cost_age_06to09                             = 0.0;
		cost_age_10to17                             = 0.0;
		cost_age_18to25                              = 0.0;
		cost_age_26to35                              = 0.0;
		cost_age_51to60                              = 0.0;
		cost_age_61to70                             = 0.0;
		cost_age_71plus                             = 0.0;
		
		cost_employment_parttime                     = 0.0;
		cost_employment_marginal                     = 0.0;
		cost_employment_homekeeper                   = 0.0;
		cost_employment_unemployed                   = 0.0;
		cost_employment_retired                      = 0.0;
		cost_employment_pupil                        = 0.0;
		cost_employment_student                      = 0.0;
		cost_employment_apprentice                   = 0.0;
		cost_employment_other                        = 0.0;
		
		cardriver_age_06to09                        = 0.0;
		cardriver_age_10to17                        = 0.0;
		cardriver_age_18to25                         = 0.0;
		cardriver_age_26to35                        = 0.0;
		cardriver_age_51to60                         = 0.0;
		cardriver_age_61to70                        = 0.0;
		cardriver_age_71plus                         = 0.0;
		cardriver_sex_female                        = 0.0;
		carpassenger_age_06to09                      = 0.0;
		carpassenger_age_10to17                      = 0.0;
		carpassenger_age_18to25                      = 0.0;
		carpassenger_age_26to35                     = 0.0;
		carpassenger_age_51to60                      = 0.0;
		carpassenger_age_61to70                      = 0.0;
		carpassenger_age_71plus                      = 0.0;
		carpassenger_sex_female                      = 0.0;
		cycling_age_06to09                          = 0.0;
		cycling_age_10to17                           = 0.0;
		cycling_age_18to25                           = 0.0;
		cycling_age_26to35                          = 0.0;
		cycling_age_51to60                          = 0.0;
		cycling_age_61to70                          = 0.0;
		cycling_age_71plus                           = 0.0;
		cycling_sex_female                          = 0.0;
		publictransport_age_06to09                  = 0.0;
		publictransport_age_10to17                   = 0.0;
		publictransport_age_18to25                   = 0.0;
		publictransport_age_26to35                   = 0.0;
		publictransport_age_51to60                   = 0.0;
		publictransport_age_61to70                   = 0.0;
		publictransport_age_71plus                   = 0.0;
		publictransport_sex_female                   = 0.0;
		
		cardriver_employment_apprentice              = 0.0;
		cardriver_employment_homekeeper             = 0.0;
		cardriver_employment_marginal                = 0.0;
		cardriver_employment_other                  = 0.0;
		cardriver_employment_parttime                = 0.0;
		cardriver_employment_pupil                  = 0.0;
		cardriver_employment_retired                = 0.0;
		cardriver_employment_student                = 0.0;
		cardriver_employment_unemployed             = 0.0;
		carpassenger_employment_apprentice           = 0.0;
		carpassenger_employment_homekeeper           = 0.0;
		carpassenger_employment_marginal             = 0.0;
		carpassenger_employment_other               = 0.0;
		carpassenger_employment_parttime             = 0.0;
		carpassenger_employment_pupil                = 0.0;
		carpassenger_employment_retired              = 0.0;
		carpassenger_employment_student              = 0.0;
		carpassenger_employment_unemployed           = 0.0;
		cycling_employment_apprentice               = 0.0;
		cycling_employment_homekeeper                = 0.0;
		cycling_employment_marginal                  = 0.0;
		cycling_employment_other                    = 0.0;
		cycling_employment_parttime                  = 0.0;
		cycling_employment_pupil                     = 0.0;
		cycling_employment_retired                  = 0.0;
		cycling_employment_student                   = 0.0;
		cycling_employment_unemployed                = 0.0;
		publictransport_employment_apprentice        = 0.0;
		publictransport_employment_homekeeper       = 0.0;
		publictransport_employment_marginal         = 0.0;
		publictransport_employment_other             = 0.0;
		publictransport_employment_parttime         = 0.0;
		publictransport_employment_pupil             = 0.0;
		publictransport_employment_retired          = 0.0;
		publictransport_employment_student          = 0.0;
		publictransport_employment_unemployed        = 0.0;
		
		cardriver_purpose_business                   = 0.0;
		carpassenger_purpose_business                = 0.0;
		cycling_purpose_business                    = 0.0;
		publictransport_purpose_business            = 0.0;
		cardriver_purpose_education                 = 0.0;
		carpassenger_purpose_education               = 0.0;
		cycling_purpose_education                   = 0.0;
		publictransport_purpose_education           = 0.0;
		cardriver_purpose_service                    = 0.0;
		carpassenger_purpose_service                 = 0.0;
		cycling_purpose_service                     = 0.0;
		publictransport_purpose_service             = 0.0;
		cardriver_purpose_private_business          = 0.0;
		carpassenger_purpose_private_business        = 0.0;
		cycling_purpose_private_business            = 0.0;
		publictransport_purpose_private_business    = 0.0;
		cardriver_purpose_visit                     = 0.0;
		carpassenger_purpose_visit                   = 0.0;
		cycling_purpose_visit                       = 0.0;
		publictransport_purpose_visit               = 0.0;
		cardriver_purpose_shopping_grocery          = 0.0;
		carpassenger_purpose_shopping_grocery        = 0.0;
		cycling_purpose_shopping_grocery            = 0.0;
		publictransport_purpose_shopping_grocery    = 0.0;
		cardriver_purpose_shopping_other             = 0.0;
		carpassenger_purpose_shopping_other          = 0.0;
		cycling_purpose_shopping_other              = 0.0;
		publictransport_purpose_shopping_other      = 0.0;
		cardriver_purpose_leisure_indoors           = 0.0;
		carpassenger_purpose_leisure_indoors         = 0.0;
		cycling_purpose_leisure_indoors             = 0.0;
		publictransport_purpose_leisure_indoors     = 0.0;
		cardriver_purpose_leisure_outdoors           = 0.0;
		carpassenger_purpose_leisure_outdoors        = 0.0;
		cycling_purpose_leisure_outdoors            = 0.0;
		publictransport_purpose_leisure_outdoors    = 0.0;
		cardriver_purpose_leisure_other             = 0.0;
		carpassenger_purpose_leisure_other           = 0.0;
		cycling_purpose_leisure_other               = 0.0;
		publictransport_purpose_leisure_other       = 0.0;
		cardriver_purpose_leisure_walk              = 0.0;
		carpassenger_purpose_leisure_walk           = 0.0;
		cycling_purpose_leisure_walk                = 0.0;
		publictransport_purpose_leisure_walk        = 0.0;
		cardriver_purpose_other                      = 0.0;
		carpassenger_purpose_other                   = 0.0;
		cycling_purpose_other                       = 0.0;
		publictransport_purpose_other               = 0.0;
		
		cardriver_day_Saturday                       = 0.0;
		carpassenger_day_Saturday                    = 0.0;
		cycling_day_Saturday                        = 0.0;
		publictransport_day_Saturday                = 0.0;
		cardriver_day_Sunday                        = 0.0;
		carpassenger_day_Sunday                      = 0.0;
		cycling_day_Sunday                          = 0.0;
		publictransport_day_Sunday                  = 0.0;
		
		cardriver_intrazonal                        = 0.0;
		carpassenger_intrazonal                     = 0.0;
		cycling_intrazonal                           = 0.0;
		publictransport_intrazonal                  = 0.0;
		
		cardriver_num_activities                     = 0.0;
		carpassenger_num_activities                  = 0.0;
		cycling_num_activities                       = 0.0;
		publictransport_num_activities               = 0.0;
		cardriver_containsStrolling                 = 0.0;
		carpassenger_containsStrolling              = 0.0;
		cycling_containsStrolling                   = 0.0;
		publictransport_containsStrolling           = 0.0;
		cardriver_containsVisit                      = 0.0;
		carpassenger_containsVisit                   = 0.0;
		cycling_containsVisit                       = 0.0;
		publictransport_containsVisit               = 0.0;
		cardriver_containsPrivateB                   = 0.0;
		carpassenger_containsPrivateB                = 0.0;
		cycling_containsPrivateB                     = 0.0;
		publictransport_containsPrivateB            = 0.0;
		cardriver_containsService                   = 0.0;
		carpassenger_containsService                = 0.0;
		cycling_containsService                     = 0.0;
		publictransport_containsService             = 0.0;
		cardriver_containsLeisure                    = 0.0;
		carpassenger_containsLeisure                 = 0.0;
		cycling_containsLeisure                      = 0.0;
		publictransport_containsLeisure              = 0.0;
		cardriver_containsShopping                   = 0.0;
		carpassenger_containsShopping                = 0.0;
		cycling_containsShopping                     = 0.0;
		publictransport_containsShopping             = 0.0;
		cardriver_containsBusiness                   = 0.0;
		carpassenger_containsBusiness               = 0.0;
		cycling_containsBusiness                    = 0.0;
		publictransport_containsBusiness            = 0.0;



		
	System.out.println("test");	


		init();
	}



}
