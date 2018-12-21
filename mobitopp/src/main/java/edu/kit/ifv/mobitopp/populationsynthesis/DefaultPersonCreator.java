package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferenceCreator;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;
import edu.kit.ifv.mobitopp.simulation.modeChoice.UncorrelatedModeChoicePreferenceCreator;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

public class DefaultPersonCreator 
	implements PersonCreator 
{

	private final CommutationTicketModelIfc commutationTicketModel;
	private final ModeChoicePreferenceCreator modeChoicePreferenceCreator;

	private static int personIdCounter = 1;
	
  public DefaultPersonCreator(
      CommutationTicketModelIfc commutationTicketModel,
      ModeChoicePreferenceCreator modeChoicePreferenceCreator) {
    this.commutationTicketModel = commutationTicketModel;
    this.modeChoicePreferenceCreator = modeChoicePreferenceCreator;
  }

  public DefaultPersonCreator(CommutationTicketModelIfc commutationTicketModel) {
    this(commutationTicketModel, new UncorrelatedModeChoicePreferenceCreator(0));
  }

	public PersonForSetup createPerson(
		PersonOfPanelData panelPerson,
		HouseholdOfPanelData panelHousehold,
		HouseholdForSetup household,
		Zone zone
	) {
    boolean hasCommuterTicket = this.commutationTicketModel
        .estimateCommutationTicket(panelPerson, panelHousehold, zone);
    return newPerson(personIdCounter++, panelPerson, household, hasCommuterTicket);
  }

  protected PersonForSetup newPerson(
			int oid,
			PersonOfPanelData personOfPanelData,
			HouseholdForSetup household,
			boolean hasCommuterTicket
	) {

    PersonOfPanelDataId panel_id = personOfPanelData.getId(); 

    PersonId pid = 
      new PersonId(oid, 
        household.getId(), panel_id.getPersonNumber());

    int age 										= personOfPanelData.age();
		Employment employment 			= personOfPanelData.employment();
		Gender gender 					= personOfPanelData.gender();
		int income 							= personOfPanelData.getIncome();
		
		Map<Mode,Double> preferences = new LinkedHashMap<Mode,Double>();
		preferences.put(Mode.BIKE,(double) personOfPanelData.pref_cycling);
		preferences.put(Mode.CAR,(double) personOfPanelData.pref_cardriver);
		preferences.put(Mode.PASSENGER,(double) personOfPanelData.pref_carpassenger);
		preferences.put(Mode.PEDESTRIAN,(double) personOfPanelData.pref_walking);
		preferences.put(Mode.PUBLICTRANSPORT,(double) personOfPanelData.pref_publictransport);
		
		ModeChoicePreferences modePreferences = new ModeChoicePreferences(preferences);

		PersonForSetup person = new DefaultPersonForSetup(
													pid, 
													household,
													age,
													Employment.getTypeFromInt(employment.getTypeAsInt()),
													Gender.getTypeFromInt(gender.getTypeAsInt()),
													income,
													personOfPanelData.hasBicycle(),
													personOfPanelData.hasAccessToCar(),
													personOfPanelData.hasPersonalCar(),
													hasCommuterTicket,
													personOfPanelData.hasLicence(),
													modePreferences,
													modeChoicePreferenceCreator.createPreferences()
												);
		return person;
	}

}
