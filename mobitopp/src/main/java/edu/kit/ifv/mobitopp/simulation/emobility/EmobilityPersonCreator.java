package edu.kit.ifv.mobitopp.simulation.emobility;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.populationsynthesis.CommutationTicketModelIfc;
import edu.kit.ifv.mobitopp.populationsynthesis.DefaultPersonCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.EmobilityPersonForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonForSetup;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCustomerModel;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class EmobilityPersonCreator 
	extends DefaultPersonCreator 
	implements PersonCreator 
{

	protected final Random random;

	protected	final Map<String,CarSharingCustomerModel> carSharingCustomerModels;

	protected final PublicChargingInfluenceModel publicChargingInfluenceModel;

	public EmobilityPersonCreator(
		CommutationTicketModelIfc commutationTicketModel,
		Map<String,CarSharingCustomerModel> carSharingCustomerModels,
		long seed
	) {
		super(commutationTicketModel);

		assert carSharingCustomerModels != null;

		this.carSharingCustomerModels = Collections.unmodifiableMap(carSharingCustomerModels);

		this.publicChargingInfluenceModel = new PublicChargingInfluenceModel();
	
		this.random = new Random(seed);
	}


	@Override
	protected PersonForSetup newPerson(
		int oid,
		PersonOfPanelData personOfPanelData,
		HouseholdForSetup household,
		boolean hasCommuterTicket
	) {

		PersonForSetup person = super.newPerson(oid, personOfPanelData, household, hasCommuterTicket);

		float eMobilityAcceptance = this.random.nextFloat();


		Map<String, Boolean> carSharingCustomership = new TreeMap<String, Boolean>();

		for(String carSharingCompany : this.carSharingCustomerModels.keySet()) {

			CarSharingCustomerModel model = this.carSharingCustomerModels.get(carSharingCompany);

			Boolean isCustomer = model.estimateCustomership(person);

			carSharingCustomership.put(carSharingCompany, isCustomer);
		}

		double randomNumber = this.random.nextDouble();

		EmobilityPerson.PublicChargingInfluencesDestinationChoice chargingInfluencesDestinationChoice 
			=	this.publicChargingInfluenceModel.estimatePublicChargingInfluence(person, carSharingCustomership, randomNumber);

    PersonForSetup ePerson = new EmobilityPersonForSetup(
													person,
													eMobilityAcceptance,
													chargingInfluencesDestinationChoice,
													carSharingCustomership
												);
		return ePerson;
	}

}

