package edu.kit.ifv.mobitopp.simulation.emobility;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.util.logit.DefaultLogitModel;
import edu.kit.ifv.mobitopp.util.logit.LogitModel;

public class PublicChargingInfluenceModel {

	protected final double	    LAD1 =   -2.37115;       
	protected final double	C_ALT_21 =    0.07438;     // Age 18-34
	protected final double	C_ALT_31 =    0.38369;     //	Age 35-64
	protected final double	C_BARB_1 =    1.27720;     //	Employment 1,21,22
	protected final double	C_BKARB1 =    1.62277;     //	Employment 3,6,7
	protected final double	C_EINKO1 =    0.10364;     //	(EINKO==1, 0.45), (EINKO==2, 0.75), (EINKO==3, 1.25), (EINKO==4, 1.75), (EINKO==5, 2.5), (EINKO==6, 3.5), (EINKO==7, 4.5)
	protected final double	C_ZEITK1 =    0.44080;     //	Commutation ticket
	protected final double	  C_FEM1 =    0.07613;     //	Female
	protected final double	 C_PHHM1 =    0.37699;     // Number of cars pro household / size of household
	protected final double	  C_CSF1 =   -0.77325;     // Member of free floating car sharing
	protected final double	  C_CSS1 =    0.17540;     // Member of station based car sharing
	
	protected final double	    LAD2 =    0.51978;     
	protected final double	C_ALT_22 =   -0.28973;     // Age 18-34                                                                                                                
	protected final double	C_ALT_32 =   -0.21246;     // Age 35-64                                                                                                                
	protected final double	C_BARB_2 =   -0.28400;     // Employment 1,21,22                                                                                                       
	protected final double	C_BKARB2 =   -0.69985;     // Employment 3,6,7                                                                                                         
	protected final double	C_EINKO2 =    0.09525;     // (EINKO==1, 0.45), (EINKO==2, 0.75), (EINKO==3, 1.25), (EINKO==4, 1.75), (EINKO==5, 2.5), (EINKO==6, 3.5), (EINKO==7, 4.5)
	protected final double	C_ZEITK2 =    -.40427;     // Commutation ticket                                                                                                       
	protected final double	  C_FEM2 =    0.63672;     // Female                                                                                                                   
	protected final double	 C_PHHM2 =    0.54713;     // Number of cars pro household / size of household                                                                         
	protected final double	  C_CSF2 =    0.14558;     // Member of free floating car sharing                                                                                      
	protected final double	  C_CSS2 =    0.70177;     // Member of station based car sharing                                                                                      



	private final LogitModel<EmobilityPerson.PublicChargingInfluencesDestinationChoice> choiceModel 
			= new DefaultLogitModel<EmobilityPerson.PublicChargingInfluencesDestinationChoice>();


	public PublicChargingInfluenceModel() {
	}


	public EmobilityPerson.PublicChargingInfluencesDestinationChoice estimatePublicChargingInfluence(
		PersonBuilder person,
		Map<String, Boolean> carSharingCustomership,
		double randomNumber
	) {

		Map<EmobilityPerson.PublicChargingInfluencesDestinationChoice,Double> utilities = calculateUtilities(person, carSharingCustomership);

		return choiceModel.select(utilities, randomNumber);
	}

	protected Map<EmobilityPerson.PublicChargingInfluencesDestinationChoice,Double> calculateUtilities(
		PersonBuilder person,
		Map<String, Boolean> carSharingCustomership
	) {


		HouseholdForSetup household = person.household();

		int alt_2 			= (person.age() >= 18 && person.age() <= 34) ? 1 : 0;
		int alt_3 			= (person.age() >= 35 && person.age() <= 64) ? 1 : 0;

		int ber_arb 		= (  person.employment() == Employment.FULLTIME
											|| person.employment() == Employment.PARTTIME) ? 1 : 0;

		int ber_karb 		= (  person.employment() == Employment.HOMEKEEPER
											|| person.employment() == Employment.UNEMPLOYED
											|| person.employment() == Employment.RETIRED) ? 1 : 0;

		double einko_m 	= household.monthlyIncomeEur() / 1000.0f;

		int zeitkrtb 		= person.hasCommuterTicket() ? 1 : 0; 

		int fem 				= person.isFemale() ? 1 : 0;

		double pkw_hhm 	=  household.getTotalNumberOfCars() / household.nominalSize();

		int mitgl_ff 		= carSharingCustomership.containsKey("Car2Go") && carSharingCustomership.get("Car2Go") ? 1 : 0;
		int mitgl_sb 		= ( carSharingCustomership.containsKey("Stadtmobil") && carSharingCustomership.get("Stadtmobil")
											|| carSharingCustomership.containsKey("Flinkster") && carSharingCustomership.get("Flinkster") ) ? 1 : 0;


		double u1 = LAD1 
							+ C_ALT_21*alt_2 
							+ C_ALT_31*alt_3 
							+ C_BARB_1*ber_arb 
							+ C_BKARB1*ber_karb 
							+ C_EINKO1*einko_m 
							+ C_ZEITK1*zeitkrtb 
							+ C_FEM1*fem 
							+ C_PHHM1*pkw_hhm 
							+ C_CSF1*mitgl_ff 
							+ C_CSS1*mitgl_sb
							;
		
		double u2 = LAD2
							+ C_ALT_22*alt_2 
							+ C_ALT_32*alt_3 
							+ C_BARB_2*ber_arb 
							+ C_BKARB2*ber_karb 
							+ C_EINKO2*einko_m 
							+ C_ZEITK2*zeitkrtb 
							+ C_FEM2*fem 
							+ C_PHHM2*pkw_hhm 
							+ C_CSF2*mitgl_ff 
							+ C_CSS2*mitgl_sb
							; 

		Map<EmobilityPerson.PublicChargingInfluencesDestinationChoice,Double> utilities 
				= new LinkedHashMap<EmobilityPerson.PublicChargingInfluencesDestinationChoice,Double>();

		utilities.put(EmobilityPerson.PublicChargingInfluencesDestinationChoice.ALWAYS, u1);
		utilities.put(EmobilityPerson.PublicChargingInfluencesDestinationChoice.ONLY_WHEN_BATTERY_LOW, u2);
		utilities.put(EmobilityPerson.PublicChargingInfluencesDestinationChoice.NEVER, 0.0);

		return utilities;
	}

}
