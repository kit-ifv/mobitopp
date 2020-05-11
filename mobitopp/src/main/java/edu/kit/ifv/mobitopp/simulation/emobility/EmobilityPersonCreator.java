package edu.kit.ifv.mobitopp.simulation.emobility;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.populationsynthesis.CommutationTicketModelIfc;
import edu.kit.ifv.mobitopp.populationsynthesis.PanelBasedPersonCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.EmobilityPersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.MobilityProviderCustomerModel;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class EmobilityPersonCreator extends PanelBasedPersonCreator implements PersonCreator {

  private final Random random;
	private	final Map<String,MobilityProviderCustomerModel> carSharingCustomerModels;
	private final PublicChargingInfluenceModel publicChargingInfluenceModel;

	public EmobilityPersonCreator(
		CommutationTicketModelIfc commutationTicketModel,
		Map<String,MobilityProviderCustomerModel> carSharingCustomerModels,
		long seed
	) {
		super(commutationTicketModel);
		assert carSharingCustomerModels != null;
		this.carSharingCustomerModels = Collections.unmodifiableMap(carSharingCustomerModels);
		this.publicChargingInfluenceModel = new PublicChargingInfluenceModel();
		this.random = new Random(seed);
	}


	@Override
	protected PersonBuilder newPerson(
		int oid,
		PersonOfPanelData personOfPanelData,
		HouseholdForSetup household,
		boolean hasCommuterTicket
	) {
		PersonBuilder person = super.newPerson(oid, personOfPanelData, household, hasCommuterTicket);
		float eMobilityAcceptance = this.random.nextFloat();
		Map<String, Boolean> carSharingCustomership = new TreeMap<String, Boolean>();
		for(String carSharingCompany : this.carSharingCustomerModels.keySet()) {
			MobilityProviderCustomerModel model = this.carSharingCustomerModels.get(carSharingCompany);
			Boolean isCustomer = model.estimateCustomership(person);
			carSharingCustomership.put(carSharingCompany, isCustomer);
		}
		double randomNumber = this.random.nextDouble();

		EmobilityPerson.PublicChargingInfluencesDestinationChoice chargingInfluencesDestinationChoice 
			=	this.publicChargingInfluenceModel.estimatePublicChargingInfluence(person, carSharingCustomership, randomNumber);

    return new EmobilityPersonBuilder(person)
        .setEmobilityAcceptance(eMobilityAcceptance)
        .setChargingInfluencesDestinationChoice(chargingInfluencesDestinationChoice)
        .setCarsharingMembership(carSharingCustomership);
	}

}

