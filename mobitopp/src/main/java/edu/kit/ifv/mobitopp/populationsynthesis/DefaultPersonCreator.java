package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.person.PersonForDemand;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Employment;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

public class DefaultPersonCreator 
	implements PersonCreator 
{

	private final ActivityScheduleCreator scheduleCreator;
	private final CommutationTicketModelIfc commutationTicketModel;

	private static int personIdCounter = 1;

	public DefaultPersonCreator(
		ActivityScheduleCreator scheduleCreator,
		CommutationTicketModelIfc commutationTicketModel
	) {
		this.scheduleCreator = scheduleCreator;
		this.commutationTicketModel = commutationTicketModel;
	}
	


	public Person createPerson(
		PersonOfPanelData panelPerson,
		HouseholdOfPanelData panelHousehold,
		Household household,
		Zone zone
	) {
			boolean hasCommuterTicket = this.commutationTicketModel.estimateCommutationTicket(
				panelPerson,
				panelHousehold,
				zone
				);
			
			
			PatternActivityWeek activitySchedule = this.scheduleCreator.createActivitySchedule(
																																													panelPerson,
																																													panelHousehold,
																																													household
																																												);

			Person person = newPerson(
    										personIdCounter++,
												panelPerson, 
												household,
												hasCommuterTicket,
												activitySchedule
											);
			


		return person;
	}

	protected Person newPerson(
			int oid,
			PersonOfPanelData personOfPanelData,
			Household household,
			boolean hasCommuterTicket,
			PatternActivityWeek activitySchedule
	) {

		PersonOfPanelDataId panel_id = personOfPanelData.getId();
		PersonId pid = new PersonId(household.getId(), panel_id.getPersonNumber());

		int age 										= personOfPanelData.age();
		Employment employment 			= personOfPanelData.employment();
		Gender gender 					= personOfPanelData.gender();
		int income 							= personOfPanelData.getIncome();

    PersonForSetup person = new PersonForDemand(
													oid, 
													pid, 
													household,
													age,
													employment,
													gender,
													income,
													personOfPanelData.hasBicycle(),
													personOfPanelData.hasAccessToCar(),
													personOfPanelData.hasPersonalCar(),
													hasCommuterTicket,
													activitySchedule	
												);
		return person;
	}

}
