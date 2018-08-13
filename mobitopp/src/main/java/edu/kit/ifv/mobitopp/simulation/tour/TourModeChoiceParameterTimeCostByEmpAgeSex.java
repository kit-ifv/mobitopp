package edu.kit.ifv.mobitopp.simulation.tour;



public class TourModeChoiceParameterTimeCostByEmpAgeSex extends TourModeChoiceParameterTimeCostByEmpAgeSexBase 
	implements TourModeChoiceParameter
{

	


	/*


Call:
mlogit(formula = mode ~ notavailable + time + time:sex. + time:age. + 
    time:employment. + cost + cost:sex. + cost:age. + cost:employment. | 
    sex. + age. + employment. + purpose. + day. + intrazonal. + 
        num_activities + containsStrolling. + containsVisit. + 
        containsPrivateB. + containsService. + containsLeisure. + 
        containsShopping. + containsBusiness., data = trainingdata, 
    reflevel = "walking", method = "nr", print.level = 0)

Frequencies of alternatives:
        walking       cardriver    carpassenger         cycling publictransport 
         0.2858          0.3909          0.1277          0.0555          0.1401 

nr method
24 iterations, 0h:16m:34s 
g'(-H)^-1g = 9.41E-07 
gradient close to zero 

Coefficients :
                                             Estimate   Std. Error t-value Pr(>|t|)    
cardriver:(intercept)                       0.7838696    0.0534689   14.66  < 2e-16 ***
carpassenger:(intercept)                   -4.2743401    0.0750082  -56.98  < 2e-16 ***
cycling:(intercept)                        -1.4802683    0.0693887  -21.33  < 2e-16 ***
publictransport:(intercept)                 0.3532635    0.0597487    5.91  3.4e-09 ***
notavailable                              -24.7683719 1767.8866464   -0.01  0.98882    
time                                       -0.0345531    0.0009001  -38.39  < 2e-16 ***
cost                                       -0.7279562    0.0159515  -45.64  < 2e-16 ***
time:sex.female                            -0.0040490    0.0008593   -4.71  2.5e-06 ***
time:age.06to09                            -0.0046218    0.0039636   -1.17  0.24359    
time:age.10to17                            -0.0000482    0.0033291   -0.01  0.98846    
time:age.18to25                             0.0076624    0.0023077    3.32  0.00090 ***
time:age.26to35                            -0.0041852    0.0015789   -2.65  0.00803 ** 
time:age.51to60                             0.0007772    0.0012294    0.63  0.52724    
time:age.61to70                            -0.0045907    0.0020151   -2.28  0.02272 *  
time:age.71plus                            -0.0090961    0.0023978   -3.79  0.00015 ***
time:employment.parttime                   -0.0038189    0.0014572   -2.62  0.00877 ** 
time:employment.marginal                    0.0031919    0.0023838    1.34  0.18056    
time:employment.homekeeper                 -0.0022105    0.0019655   -1.12  0.26073    
time:employment.unemployed                  0.0017103    0.0035932    0.48  0.63408    
time:employment.retired                     0.0064668    0.0020515    3.15  0.00162 ** 
time:employment.pupil                      -0.0042474    0.0031073   -1.37  0.17165    
time:employment.student                     0.0107475    0.0025091    4.28  1.8e-05 ***
time:employment.apprentice                 -0.0005993    0.0035495   -0.17  0.86591    
time:employment.other                      -0.0364927    0.0083880   -4.35  1.4e-05 ***
cost:sex.female                            -0.0450965    0.0168323   -2.68  0.00738 ** 
cost:age.06to09                            -0.0709127    0.1287872   -0.55  0.58189    
cost:age.10to17                            -0.3714511    0.0759595   -4.89  1.0e-06 ***
cost:age.18to25                             0.0996225    0.0422466    2.36  0.01837 *  
cost:age.26to35                             0.1043360    0.0265933    3.92  8.7e-05 ***
cost:age.51to60                             0.0082832    0.0220942    0.37  0.70773    
cost:age.61to70                            -0.0984459    0.0385215   -2.56  0.01060 *  
cost:age.71plus                            -0.1874141    0.0491967   -3.81  0.00014 ***
cost:employment.parttime                    0.1348369    0.0258377    5.22  1.8e-07 ***
cost:employment.marginal                    0.4199254    0.0514948    8.15  4.4e-16 ***
cost:employment.homekeeper                  0.2992249    0.0430272    6.95  3.5e-12 ***
cost:employment.unemployed                  0.3297821    0.0713741    4.62  3.8e-06 ***
cost:employment.retired                     0.3083955    0.0412525    7.48  7.7e-14 ***
cost:employment.pupil                       0.0215389    0.0658038    0.33  0.74343    
cost:employment.student                     0.1869752    0.0482593    3.87  0.00011 ***
cost:employment.apprentice                  0.0885172    0.0603778    1.47  0.14263    
cost:employment.other                       0.1414563    0.0995768    1.42  0.15544    
cardriver:sex.female                       -0.6711930    0.0298619  -22.48  < 2e-16 ***
carpassenger:sex.female                     0.5537679    0.0330061   16.78  < 2e-16 ***
cycling:sex.female                         -0.5759933    0.0349929  -16.46  < 2e-16 ***
publictransport:sex.female                  0.0264326    0.0299141    0.88  0.37690    
cardriver:age.06to09                       -0.7273030 6439.3453579    0.00  0.99991    
carpassenger:age.06to09                     1.5820145    0.1562488   10.12  < 2e-16 ***
cycling:age.06to09                         -1.4484387    0.1742134   -8.31  < 2e-16 ***
publictransport:age.06to09                 -1.0742773    0.1714425   -6.27  3.7e-10 ***
cardriver:age.10to17                       -0.9221366    0.2339667   -3.94  8.1e-05 ***
carpassenger:age.10to17                     1.6587456    0.1502009   11.04  < 2e-16 ***
cycling:age.10to17                          0.3164702    0.1604103    1.97  0.04851 *  
publictransport:age.10to17                  0.6805827    0.1228932    5.54  3.1e-08 ***
cardriver:age.18to25                        0.6255608    0.1038801    6.02  1.7e-09 ***
carpassenger:age.18to25                     1.4422796    0.1197606   12.04  < 2e-16 ***
cycling:age.18to25                          0.0352118    0.1345910    0.26  0.79361    
publictransport:age.18to25                  0.5973844    0.1004728    5.95  2.8e-09 ***
cardriver:age.26to35                       -0.4486333    0.0507716   -8.84  < 2e-16 ***
carpassenger:age.26to35                    -0.1399111    0.0706090   -1.98  0.04754 *  
cycling:age.26to35                         -0.4236203    0.0702181   -6.03  1.6e-09 ***
publictransport:age.26to35                  0.0329015    0.0576261    0.57  0.56804    
cardriver:age.51to60                        0.1484501    0.0383198    3.87  0.00011 ***
carpassenger:age.51to60                     0.3974930    0.0533544    7.45  9.3e-14 ***
cycling:age.51to60                         -0.1425190    0.0507496   -2.81  0.00498 ** 
publictransport:age.51to60                  0.1503470    0.0482438    3.12  0.00183 ** 
cardriver:age.61to70                       -0.0333599    0.0614032   -0.54  0.58693    
carpassenger:age.61to70                     0.5393937    0.0787116    6.85  7.2e-12 ***
cycling:age.61to70                         -0.1398061    0.0846124   -1.65  0.09847 .  
publictransport:age.61to70                  0.3104732    0.0771942    4.02  5.8e-05 ***
cardriver:age.71plus                        0.0231284    0.0728128    0.32  0.75076    
carpassenger:age.71plus                     0.5105449    0.0920582    5.55  2.9e-08 ***
cycling:age.71plus                          0.0361356    0.1071707    0.34  0.73598    
publictransport:age.71plus                  0.5959458    0.0926550    6.43  1.3e-10 ***
cardriver:employment.parttime               0.0277271    0.0445385    0.62  0.53359    
carpassenger:employment.parttime            0.2247967    0.0599056    3.75  0.00018 ***
cycling:employment.parttime                 0.3380389    0.0559295    6.04  1.5e-09 ***
publictransport:employment.parttime        -0.1419514    0.0524099   -2.71  0.00676 ** 
cardriver:employment.marginal               0.0890117    0.0660056    1.35  0.17748    
carpassenger:employment.marginal            0.6199390    0.0876269    7.07  1.5e-12 ***
cycling:employment.marginal                 0.5998283    0.0830891    7.22  5.2e-13 ***
publictransport:employment.marginal        -0.5744861    0.1052609   -5.46  4.8e-08 ***
cardriver:employment.homekeeper            -0.1438843    0.0531253   -2.71  0.00676 ** 
carpassenger:employment.homekeeper          0.2685464    0.0699462    3.84  0.00012 ***
cycling:employment.homekeeper               0.0693459    0.0773975    0.90  0.37027    
publictransport:employment.homekeeper      -0.5490823    0.0883345   -6.22  5.1e-10 ***
cardriver:employment.unemployed            -0.3415459    0.1218360   -2.80  0.00506 ** 
carpassenger:employment.unemployed          0.3496902    0.1405678    2.49  0.01286 *  
cycling:employment.unemployed               0.0701598    0.1659389    0.42  0.67244    
publictransport:employment.unemployed       0.0715636    0.1375318    0.52  0.60282    
cardriver:employment.retired               -0.3223170    0.0651086   -4.95  7.4e-07 ***
carpassenger:employment.retired             0.0697935    0.0844154    0.83  0.40836    
cycling:employment.retired                 -0.2628417    0.0942860   -2.79  0.00531 ** 
publictransport:employment.retired         -0.2048216    0.0842944   -2.43  0.01511 *  
cardriver:employment.pupil                 -0.4200364    0.1351673   -3.11  0.00189 ** 
carpassenger:employment.pupil               0.1134845    0.1462048    0.78  0.43763    
cycling:employment.pupil                    0.4979826    0.1599276    3.11  0.00185 ** 
publictransport:employment.pupil            0.0441970    0.1211527    0.36  0.71526    
cardriver:employment.student               -0.1052058    0.1263720   -0.83  0.40512    
carpassenger:employment.student             0.0012473    0.1503477    0.01  0.99338    
cycling:employment.student                  0.2536414    0.1640400    1.55  0.12205    
publictransport:employment.student         -0.0332072    0.1183581   -0.28  0.77904    
cardriver:employment.apprentice             0.2089762    0.1572316    1.33  0.18382    
carpassenger:employment.apprentice          0.1874173    0.1700029    1.10  0.27027    
cycling:employment.apprentice              -0.1043799    0.1970011   -0.53  0.59622    
publictransport:employment.apprentice       0.1689285    0.1380726    1.22  0.22115    
cardriver:employment.other                 -0.9905885    0.1708046   -5.80  6.6e-09 ***
carpassenger:employment.other              -0.7682119    0.2260336   -3.40  0.00068 ***
cycling:employment.other                   -0.5498606    0.2296555   -2.39  0.01665 *  
publictransport:employment.other            0.0144271    0.1974330    0.07  0.94175    
cardriver:purpose.business                  0.5175456    0.1043755    4.96  7.1e-07 ***
carpassenger:purpose.business               0.7014024    0.1576211    4.45  8.6e-06 ***
cycling:purpose.business                   -0.3187110    0.1602540   -1.99  0.04672 *  
publictransport:purpose.business           -0.0810896    0.1333387   -0.61  0.54309    
cardriver:purpose.education                -0.9322594    0.0790213  -11.80  < 2e-16 ***
carpassenger:purpose.education              0.0629393    0.0706544    0.89  0.37303    
cycling:purpose.education                  -0.5634654    0.0691694   -8.15  4.4e-16 ***
publictransport:purpose.education          -0.2420669    0.0639436   -3.79  0.00015 ***
cardriver:purpose.service                   0.6085780    0.0460257   13.22  < 2e-16 ***
carpassenger:purpose.service                0.9808791    0.0732629   13.39  < 2e-16 ***
cycling:purpose.service                    -0.8740852    0.0770974  -11.34  < 2e-16 ***
publictransport:purpose.service            -1.6435283    0.0812271  -20.23  < 2e-16 ***
cardriver:purpose.private_business         -0.1352808    0.0445797   -3.03  0.00241 ** 
carpassenger:purpose.private_business       1.3073267    0.0652338   20.04  < 2e-16 ***
cycling:purpose.private_business           -0.6574665    0.0688112   -9.55  < 2e-16 ***
publictransport:purpose.private_business   -1.0172788    0.0593287  -17.15  < 2e-16 ***
cardriver:purpose.visit                    -0.2609514    0.0511826   -5.10  3.4e-07 ***
carpassenger:purpose.visit                  1.1499749    0.0687306   16.73  < 2e-16 ***
cycling:purpose.visit                      -0.8612081    0.0790031  -10.90  < 2e-16 ***
publictransport:purpose.visit              -1.2631978    0.0696286  -18.14  < 2e-16 ***
cardriver:purpose.shopping_grocery         -0.1354281    0.0423905   -3.19  0.00140 ** 
carpassenger:purpose.shopping_grocery       1.2587100    0.0645943   19.49  < 2e-16 ***
cycling:purpose.shopping_grocery           -0.3395593    0.0625112   -5.43  5.6e-08 ***
publictransport:purpose.shopping_grocery   -1.6920443    0.0631930  -26.78  < 2e-16 ***
cardriver:purpose.shopping_other            0.0206852    0.0581525    0.36  0.72206    
carpassenger:purpose.shopping_other         1.6282556    0.0761007   21.40  < 2e-16 ***
cycling:purpose.shopping_other             -0.5236341    0.0920528   -5.69  1.3e-08 ***
publictransport:purpose.shopping_other     -0.6412243    0.0725805   -8.83  < 2e-16 ***
cardriver:purpose.leisure_indoors          -0.9094498    0.0557157  -16.32  < 2e-16 ***
carpassenger:purpose.leisure_indoors        1.4098318    0.0712297   19.79  < 2e-16 ***
cycling:purpose.leisure_indoors            -1.4870794    0.1044673  -14.23  < 2e-16 ***
publictransport:purpose.leisure_indoors    -0.6861419    0.0654060  -10.49  < 2e-16 ***
cardriver:purpose.leisure_outdoors          0.3244002    0.0508744    6.38  1.8e-10 ***
carpassenger:purpose.leisure_outdoors       1.9027699    0.0674004   28.23  < 2e-16 ***
cycling:purpose.leisure_outdoors           -0.2844569    0.0717883   -3.96  7.4e-05 ***
publictransport:purpose.leisure_outdoors   -0.9619424    0.0688167  -13.98  < 2e-16 ***
cardriver:purpose.leisure_other            -0.4086380    0.0492320   -8.30  < 2e-16 ***
carpassenger:purpose.leisure_other          1.2632043    0.0667902   18.91  < 2e-16 ***
cycling:purpose.leisure_other              -0.7716156    0.0723262  -10.67  < 2e-16 ***
publictransport:purpose.leisure_other      -0.7001699    0.0612007  -11.44  < 2e-16 ***
cardriver:purpose.leisure_walk             -2.3710191    0.0548929  -43.19  < 2e-16 ***
carpassenger:purpose.leisure_walk          -0.4898041    0.0792197   -6.18  6.3e-10 ***
cycling:purpose.leisure_walk               -1.2977717    0.0674860  -19.23  < 2e-16 ***
publictransport:purpose.leisure_walk       -2.1626379    0.0878576  -24.62  < 2e-16 ***
cardriver:purpose.other                     0.1340078    0.3006256    0.45  0.65577    
carpassenger:purpose.other                  1.9025169    0.2770610    6.87  6.6e-12 ***
cycling:purpose.other                      -2.2640420    1.0198347   -2.22  0.02642 *  
publictransport:purpose.other              -0.0623683    0.3712028   -0.17  0.86657    
cardriver:day.Saturday                      0.0007690    0.0302798    0.03  0.97974    
carpassenger:day.Saturday                   0.4980165    0.0351552   14.17  < 2e-16 ***
cycling:day.Saturday                       -0.1432863    0.0489047   -2.93  0.00339 ** 
publictransport:day.Saturday               -0.0617667    0.0433606   -1.42  0.15431    
cardriver:day.Sunday                       -0.4360268    0.0369459  -11.80  < 2e-16 ***
carpassenger:day.Sunday                     0.2109442    0.0412115    5.12  3.1e-07 ***
cycling:day.Sunday                         -0.4328750    0.0587273   -7.37  1.7e-13 ***
publictransport:day.Sunday                 -0.6854566    0.0543281  -12.62  < 2e-16 ***
cardriver:intrazonal.TRUE                  -1.0422170    0.0241778  -43.11  < 2e-16 ***
carpassenger:intrazonal.TRUE               -0.7622830    0.0314201  -24.26  < 2e-16 ***
cycling:intrazonal.TRUE                     0.1861246    0.0318291    5.85  5.0e-09 ***
publictransport:intrazonal.TRUE            -3.2331886    0.0679553  -47.58  < 2e-16 ***
cardriver:num_activities                    0.2753905    0.0305622    9.01  < 2e-16 ***
carpassenger:num_activities                 0.2657019    0.0351839    7.55  4.3e-14 ***
cycling:num_activities                      0.1657983    0.0467570    3.55  0.00039 ***
publictransport:num_activities              0.2391671    0.0372695    6.42  1.4e-10 ***
cardriver:containsStrolling.TRUE           -1.4447877    0.1288543  -11.21  < 2e-16 ***
carpassenger:containsStrolling.TRUE        -1.2276555    0.1594553   -7.70  1.4e-14 ***
cycling:containsStrolling.TRUE             -0.7564671    0.1982751   -3.82  0.00014 ***
publictransport:containsStrolling.TRUE     -0.8938068    0.1529553   -5.84  5.1e-09 ***
cardriver:containsVisit.TRUE                0.3397790    0.0912748    3.72  0.00020 ***
carpassenger:containsVisit.TRUE             0.0289532    0.0996727    0.29  0.77145    
cycling:containsVisit.TRUE                 -0.1358974    0.1280034   -1.06  0.28839    
publictransport:containsVisit.TRUE         -0.2560120    0.1085059   -2.36  0.01830 *  
cardriver:containsPrivateB.TRUE             0.1473714    0.0729994    2.02  0.04351 *  
carpassenger:containsPrivateB.TRUE          0.0624550    0.0866051    0.72  0.47082    
cycling:containsPrivateB.TRUE               0.0311385    0.1105355    0.28  0.77817    
publictransport:containsPrivateB.TRUE      -0.0463065    0.0896615   -0.52  0.60553    
cardriver:containsService.TRUE             -0.0220235    0.1274564   -0.17  0.86281    
carpassenger:containsService.TRUE          -0.3350803    0.1712763   -1.96  0.05042 .  
cycling:containsService.TRUE               -0.8110238    0.1833742   -4.42  9.7e-06 ***
publictransport:containsService.TRUE       -1.6645441    0.1679541   -9.91  < 2e-16 ***
cardriver:containsLeisure.TRUE              0.4525169    0.0733463    6.17  6.8e-10 ***
carpassenger:containsLeisure.TRUE           0.9528912    0.0790378   12.06  < 2e-16 ***
cycling:containsLeisure.TRUE                0.1281304    0.1092181    1.17  0.24073    
publictransport:containsLeisure.TRUE        0.5994682    0.0848939    7.06  1.6e-12 ***
cardriver:containsShopping.TRUE             0.2223492    0.0608540    3.65  0.00026 ***
carpassenger:containsShopping.TRUE          0.0364500    0.0743199    0.49  0.62382    
cycling:containsShopping.TRUE               0.0779633    0.0949021    0.82  0.41135    
publictransport:containsShopping.TRUE       0.1339703    0.0744871    1.80  0.07209 .  
cardriver:containsBusiness.TRUE             0.2662244    0.1368237    1.95  0.05169 .  
carpassenger:containsBusiness.TRUE         -0.3276153    0.2155080   -1.52  0.12846    
cycling:containsBusiness.TRUE              -0.0366078    0.1866083   -0.20  0.84447    
publictransport:containsBusiness.TRUE      -0.0001258    0.1594929    0.00  0.99937    
---
Signif. codes:  0 ‘***’ 0.001 ‘**’ 0.01 ‘*’ 0.05 ‘.’ 0.1 ‘ ’ 1

Log-Likelihood: -99700
McFadden R^2:  0.393 
Likelihood ratio test : chisq = 129000 (p.value = <2e-16)

	 */
	
	
	public TourModeChoiceParameterTimeCostByEmpAgeSex() {
	
		
		
		
		/* *********************************************************************************************** */
		
		// last: V12, curr: V13

		walking																			=  0.0				- 0.1;
		cardriver                                  	= 0.7838696		+ 0.2;     //se=0.0534689
		carpassenger                               	= -4.2743401	- 0.0;     //se=0.0750082
		cycling                                    	= -1.4802683	+ 0.2;     //se=0.0693887
		publictransport                            	= 0.3532635		+ 0.9;     //se=0.0597487
		
		walking_intrazonal                       	 	= 0.0 				+ 0.4;
		cardriver_intrazonal                        = -1.0422170	- 0.5;     //se=0.0241778
		carpassenger_intrazonal                     = -0.7622830	+ 0.0;     //se=0.0314201
		cycling_intrazonal                           = 0.1861246	+ 0.05;     //se=0.0318291
		publictransport_intrazonal                  = -3.2331886	- 0.0;     //se=0.0679553
		
		walking_sex_female                        	=  0.0				- 0.0;
		cardriver_sex_female                        = -0.6711930	+ 0.2;     //se=0.0298619
		carpassenger_sex_female                      = 0.5537679	+ 0.1;     //se=0.0330061
		cycling_sex_female                          = -0.5759933;     //se=0.0349929
		publictransport_sex_female                   = 0.0264326;     //se=0.0299141
		
		notavailable                               = -24.7683719;  //se=1767.8866464
		
		time                                        = -0.0345531;     //se=0.0009001 // - 0.05
		cost                                        = -0.7279562;     //se=0.0159515
		
		time_sex_female                             = -0.0040490;     //se=0.0008593
		cost_sex_female                             = -0.0450965;     //se=0.0168323
		
		time_age_06to09                             = -0.0046218;     //se=0.0039636
		time_age_10to17                             = -0.0000482;     //se=0.0033291
		time_age_18to25                              = 0.0076624;     //se=0.0023077
		time_age_26to35                             = -0.0041852;     //se=0.0015789
		time_age_51to60                              = 0.0007772;     //se=0.0012294
		time_age_61to70                             = -0.0045907;     //se=0.0020151
		time_age_71plus                             = -0.0090961;     //se=0.0023978
		
		time_employment_parttime                    = -0.0038189;     //se=0.0014572
		time_employment_marginal                     = 0.0031919;     //se=0.0023838
		time_employment_homekeeper                  = -0.0022105;     //se=0.0019655
		time_employment_unemployed                   = 0.0017103;     //se=0.0035932
		time_employment_retired                      = 0.0064668;     //se=0.0020515
		time_employment_pupil                       = -0.0042474;     //se=0.0031073
		time_employment_student                      = 0.0107475;     //se=0.0025091
		time_employment_apprentice                  = -0.0005993;     //se=0.0035495
		time_employment_other                       = -0.0364927;     //se=0.0083880
		
		cost_age_06to09                             = -0.0709127;     //se=0.1287872
		cost_age_10to17                             = -0.3714511;     //se=0.0759595
		cost_age_18to25                              = 0.0996225;     //se=0.0422466
		cost_age_26to35                              = 0.1043360;     //se=0.0265933
		cost_age_51to60                              = 0.0082832;     //se=0.0220942
		cost_age_61to70                             = -0.0984459;     //se=0.0385215
		cost_age_71plus                             = -0.1874141;     //se=0.0491967
		
		cost_employment_parttime                     = 0.1348369;     //se=0.0258377
		cost_employment_marginal                     = 0.4199254;     //se=0.0514948
		cost_employment_homekeeper                   = 0.2992249;     //se=0.0430272
		cost_employment_unemployed                   = 0.3297821;     //se=0.0713741
		cost_employment_retired                      = 0.3083955;     //se=0.0412525
		cost_employment_pupil                        = 0.0215389;     //se=0.0658038
		cost_employment_student                      = 0.1869752;     //se=0.0482593
		cost_employment_apprentice                   = 0.0885172;     //se=0.0603778
		cost_employment_other                        = 0.1414563;     //se=0.0995768
		
		walking_age_26to35                         	= 0.0 - 0.2;    
		walking_age_51to60                         	= 0.0 - 0.0;    
		walking_age_61to70                         	= 0.0 - 0.2;    
		walking_age_71plus                         	= 0.0 - 0.0;    
		
		cardriver_age_06to09                        = -0.7273030;  //se=6439.3453579
		cardriver_age_10to17                        = -0.9221366;     //se=0.2339667
		cardriver_age_18to25                         = 0.6255608	- 0.0;     //se=0.1038801
		cardriver_age_26to35                        = -0.4486333 	+ 0.2;     //se=0.0507716
		cardriver_age_51to60                         = 0.1484501 	+ 0.2;     //se=0.0383198
		cardriver_age_61to70                        = -0.0333599	+ 0.2;     //se=0.0614032
		cardriver_age_71plus                         = 0.0231284	+ 0.0;     //se=0.0728128
		carpassenger_age_06to09                      = 1.5820145;     //se=0.1562488
		carpassenger_age_10to17                      = 1.6587456	+ 0.0;     //se=0.1502009
		carpassenger_age_18to25                      = 1.4422796;     //se=0.1197606
		carpassenger_age_26to35                     = -0.1399111;     //se=0.0706090
		carpassenger_age_51to60                      = 0.3974930	- 0.0;     //se=0.0533544
		carpassenger_age_61to70                      = 0.5393937;     //se=0.0787116
		carpassenger_age_71plus                      = 0.5105449	- 0.0;     //se=0.0920582
		cycling_age_06to09                          = -1.4484387;     //se=0.1742134
		cycling_age_10to17                           = 0.3164702	+ 0.2;     //se=0.1604103
		cycling_age_18to25                           = 0.0352118;     //se=0.1345910
		cycling_age_26to35                          = -0.4236203;     //se=0.0702181
		cycling_age_51to60                          = -0.1425190;     //se=0.0507496
		cycling_age_61to70                          = -0.1398061;     //se=0.0846124
		cycling_age_71plus                           = 0.0361356;     //se=0.1071707
		publictransport_age_06to09                  = -1.0742773;     //se=0.1714425
		publictransport_age_10to17                   = 0.6805827	- 0.2;     //se=0.1228932
		publictransport_age_18to25                   = 0.5973844	+ 0.0;     //se=0.1004728
		publictransport_age_26to35                   = 0.0329015;     //se=0.0576261
		publictransport_age_51to60                   = 0.1503470;     //se=0.0482438
		publictransport_age_61to70                   = 0.3104732	- 0.0;     //se=0.0771942
		publictransport_age_71plus                   = 0.5959458;     //se=0.0926550
		
		walking_employment_parttime                	= 0.0					- 0.0;
		
		cardriver_employment_apprentice              = 0.2089762;     //se=0.1572316
		cardriver_employment_homekeeper             = -0.1438843	+ 0.0;     //se=0.0531253
		cardriver_employment_marginal                = 0.0890117	+ 0.2;     //se=0.0660056
		cardriver_employment_other                  = -0.9905885;     //se=0.1708046
		cardriver_employment_parttime                = 0.0277271	+ 0.2;     //se=0.0445385
		cardriver_employment_pupil                  = -0.4200364;     //se=0.1351673
		cardriver_employment_retired                = -0.3223170;     //se=0.0651086
		cardriver_employment_student                = -0.1052058;     //se=0.1263720
		cardriver_employment_unemployed             = -0.3415459;     //se=0.1218360
		carpassenger_employment_apprentice           = 0.1874173;     //se=0.1700029
		carpassenger_employment_homekeeper           = 0.2685464;     //se=0.0699462
		carpassenger_employment_marginal             = 0.6199390;     //se=0.0876269
		carpassenger_employment_other               = -0.7682119;     //se=0.2260336
		carpassenger_employment_parttime             = 0.2247967;     //se=0.0599056
		carpassenger_employment_pupil                = 0.1134845;     //se=0.1462048
		carpassenger_employment_retired              = 0.0697935;     //se=0.0844154
		carpassenger_employment_student              = 0.0012473;     //se=0.1503477
		carpassenger_employment_unemployed           = 0.3496902;     //se=0.1405678
		cycling_employment_apprentice               = -0.1043799;     //se=0.1970011
		cycling_employment_homekeeper                = 0.0693459;     //se=0.0773975
		cycling_employment_marginal                  = 0.5998283	+ 0.0;     //se=0.0830891
		cycling_employment_other                    = -0.5498606;     //se=0.2296555
		cycling_employment_parttime                  = 0.3380389	- 0.0;     //se=0.0559295
		cycling_employment_pupil                     = 0.4979826;     //se=0.1599276
		cycling_employment_retired                  = -0.2628417;     //se=0.0942860
		cycling_employment_student                   = 0.2536414;     //se=0.1640400
		cycling_employment_unemployed                = 0.0701598;     //se=0.1659389
		publictransport_employment_apprentice        = 0.1689285;     //se=0.1380726
		publictransport_employment_homekeeper       = -0.5490823;     //se=0.0883345
		publictransport_employment_marginal         = -0.5744861;     //se=0.1052609
		publictransport_employment_other             = 0.0144271;     //se=0.1974330
		publictransport_employment_parttime         = -0.1419514;     //se=0.0524099
		publictransport_employment_pupil             = 0.0441970;     //se=0.1211527
		publictransport_employment_retired          = -0.2048216;     //se=0.0842944
		publictransport_employment_student          = -0.0332072;     //se=0.1183581
		publictransport_employment_unemployed        = 0.0715636;     //se=0.1375318
		
		walking_purpose_leisure_walk              	=  0.0 - 1.0;
		
		cardriver_purpose_business                   = 0.5175456;     //se=0.1043755
		cardriver_purpose_education                 = -0.9322594	- 0.2;     //se=0.0790213
		cardriver_purpose_leisure_indoors           = -0.9094498	+ 0.0;     //se=0.0557157
		cardriver_purpose_leisure_other             = -0.4086380	+ 0.0;     //se=0.0492320
		cardriver_purpose_leisure_outdoors           = 0.3244002	+ 0.2;     //se=0.0508744
		cardriver_purpose_leisure_walk              = -2.3710191	+ 0.4;     //se=0.0548929
		cardriver_purpose_other                      = 0.1340078;     //se=0.3006256
		cardriver_purpose_private_business          = -0.1352808;     //se=0.0445797
		cardriver_purpose_service                    = 0.6085780 + 0.5;     //se=0.0460257
		cardriver_purpose_shopping_grocery          = -0.1354281 + 0.0;     //se=0.0423905
		cardriver_purpose_shopping_other             = 0.0206852 + 0.2;     //se=0.0581525
		cardriver_purpose_visit                     = -0.2609514	- 0.2;     //se=0.0511826
		carpassenger_purpose_business                = 0.7014024;     //se=0.1576211
		carpassenger_purpose_education               = 0.0629393;     //se=0.0706544
		carpassenger_purpose_leisure_indoors         = 1.4098318;     //se=0.0712297
		carpassenger_purpose_leisure_other           = 1.2632043;     //se=0.0667902
		carpassenger_purpose_leisure_outdoors        = 1.9027699;     //se=0.0674004
		carpassenger_purpose_leisure_walk           = -0.4898041	+ 1.0;     //se=0.0792197
		carpassenger_purpose_other                   = 1.9025169;     //se=0.2770610
		carpassenger_purpose_private_business        = 1.3073267;     //se=0.0652338
		carpassenger_purpose_service                 = 0.9808791;     //se=0.0732629
		carpassenger_purpose_shopping_grocery        = 1.2587100;     //se=0.0645943
		carpassenger_purpose_shopping_other          = 1.6282556;     //se=0.0761007
		carpassenger_purpose_visit                   = 1.1499749	+ 0.2;     //se=0.0687306
		cycling_purpose_business                    = -0.3187110;     //se=0.1602540
		cycling_purpose_education                   = -0.5634654;     //se=0.0691694
		cycling_purpose_leisure_indoors             = -1.4870794;     //se=0.1044673
		cycling_purpose_leisure_other               = -0.7716156;     //se=0.0723262
		cycling_purpose_leisure_outdoors            = -0.2844569;     //se=0.0717883
		cycling_purpose_leisure_walk                = -1.2977717;     //se=0.0674860
		cycling_purpose_other                       = -2.2640420;     //se=1.0198347
		cycling_purpose_private_business            = -0.6574665;     //se=0.0688112
		cycling_purpose_service                     = -0.8740852;     //se=0.0770974
		cycling_purpose_shopping_grocery            = -0.3395593;     //se=0.0625112
		cycling_purpose_shopping_other              = -0.5236341;     //se=0.0920528
		cycling_purpose_visit                       = -0.8612081;     //se=0.0790031
		publictransport_purpose_business            = -0.0810896;     //se=0.1333387
		publictransport_purpose_education           = -0.2420669;     //se=0.0639436
		publictransport_purpose_leisure_indoors     = -0.6861419;     //se=0.0654060
		publictransport_purpose_leisure_other       = -0.7001699;     //se=0.0612007
		publictransport_purpose_leisure_outdoors    = -0.9619424;     //se=0.0688167
		publictransport_purpose_leisure_walk        = -2.1626379;     //se=0.0878576
		publictransport_purpose_other               = -0.0623683;     //se=0.3712028
		publictransport_purpose_private_business    = -1.0172788;     //se=0.0593287
		publictransport_purpose_service             = -1.6435283;     //se=0.0812271
		publictransport_purpose_shopping_grocery    = -1.6920443;     //se=0.0631930
		publictransport_purpose_shopping_other      = -0.6412243;     //se=0.0725805
		publictransport_purpose_visit               = -1.2631978;     //se=0.0696286
		
		cardriver_day_Saturday                       = 0.0007690 + 0.2;     //se=0.0302798
		cardriver_day_Sunday                        = -0.4360268 + 0.15;     //se=0.0369459
		carpassenger_day_Saturday                    = 0.4980165 + 0.2;     //se=0.0351552
		carpassenger_day_Sunday                      = 0.2109442 + 0.05;     //se=0.0412115
		
		cycling_day_Saturday                        = -0.1432863;     //se=0.0489047
		cycling_day_Sunday                          = -0.4328750;     //se=0.0587273
		publictransport_day_Saturday                = -0.0617667;     //se=0.0433606
		publictransport_day_Sunday                  = -0.6854566;     //se=0.0543281
		
		cardriver_num_activities                     = 0.2753905;     //se=0.0305622
		carpassenger_num_activities                  = 0.2657019;     //se=0.0351839
		cycling_num_activities                       = 0.1657983;     //se=0.0467570
		publictransport_num_activities               = 0.2391671;     //se=0.0372695
		cardriver_containsStrolling                 = -1.4447877;     //se=0.1288543
		carpassenger_containsStrolling              = -1.2276555;     //se=0.1594553
		cycling_containsStrolling                   = -0.7564671;     //se=0.1982751
		publictransport_containsStrolling           = -0.8938068;     //se=0.1529553
		cardriver_containsVisit                      = 0.3397790;     //se=0.0912748
		carpassenger_containsVisit                   = 0.0289532;     //se=0.0996727
		cycling_containsVisit                       = -0.1358974;     //se=0.1280034
		publictransport_containsVisit               = -0.2560120;     //se=0.1085059
		cardriver_containsPrivateB                   = 0.1473714;     //se=0.0729994
		carpassenger_containsPrivateB                = 0.0624550;     //se=0.0866051
		cycling_containsPrivateB                     = 0.0311385;     //se=0.1105355
		publictransport_containsPrivateB            = -0.0463065;     //se=0.0896615
		cardriver_containsService                   = -0.0220235;     //se=0.1274564
		carpassenger_containsService                = -0.3350803;     //se=0.1712763
		cycling_containsService                     = -0.8110238;     //se=0.1833742
		publictransport_containsService             = -1.6645441;     //se=0.1679541
		cardriver_containsLeisure                    = 0.4525169;     //se=0.0733463
		carpassenger_containsLeisure                 = 0.9528912;     //se=0.0790378
		cycling_containsLeisure                      = 0.1281304;     //se=0.1092181
		publictransport_containsLeisure              = 0.5994682;     //se=0.0848939
		cardriver_containsShopping                   = 0.2223492;     //se=0.0608540
		carpassenger_containsShopping                = 0.0364500;     //se=0.0743199
		cycling_containsShopping                     = 0.0779633;     //se=0.0949021
		publictransport_containsShopping             = 0.1339703;     //se=0.0744871
		cardriver_containsBusiness                   = 0.2662244;     //se=0.1368237
		carpassenger_containsBusiness               = -0.3276153;     //se=0.2155080
		cycling_containsBusiness                    = -0.0366078;     //se=0.1866083
		publictransport_containsBusiness            = -0.0001258;     //se=0.1594929
		
		
		
		/////////////

		cardriver_employment_fulltime    		        = 0.0;
		carpassenger_employment_fulltime            = 0.0;


		
	System.out.println("test");	


		init();
	}



}
