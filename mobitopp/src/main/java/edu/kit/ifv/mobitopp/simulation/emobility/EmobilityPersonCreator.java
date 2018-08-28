package edu.kit.ifv.mobitopp.simulation.emobility;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.DefaultPersonCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.CommutationTicketModelIfc;
import edu.kit.ifv.mobitopp.populationsynthesis.ActivityScheduleCreator;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCustomerModel;

import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

import java.util.Map;
import java.util.TreeMap;
import java.util.Random;
import java.util.Collections;

public class EmobilityPersonCreator 
	extends DefaultPersonCreator 
	implements PersonCreator 
{

	protected final Random random;

	protected	final Map<String,CarSharingCustomerModel> carSharingCustomerModels;

	protected final PublicChargingInfluenceModel publicChargingInfluenceModel;

	public EmobilityPersonCreator(
		ActivityScheduleCreator scheduleCreator,
		CommutationTicketModelIfc commutationTicketModel,
		Map<String,CarSharingCustomerModel> carSharingCustomerModels,
		long seed
	) {
		super(scheduleCreator, commutationTicketModel);

		assert carSharingCustomerModels != null;

		this.carSharingCustomerModels = Collections.unmodifiableMap(carSharingCustomerModels);

		this.publicChargingInfluenceModel = new PublicChargingInfluenceModel();
	
		this.random = new Random(seed);
	}


	@Override
	protected Person newPerson(
		int oid,
		PersonOfPanelData personOfPanelData,
		Household household,
		boolean hasCommuterTicket,
		TourBasedActivityPattern activitySchedule
	) {

		Person person = super.newPerson(oid, personOfPanelData, household, hasCommuterTicket, activitySchedule);

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

    Person ePerson = new EmobilityPerson(
													person,
													eMobilityAcceptance,
													chargingInfluencesDestinationChoice,
													carSharingCustomership
												);


		return ePerson;
	}

}

