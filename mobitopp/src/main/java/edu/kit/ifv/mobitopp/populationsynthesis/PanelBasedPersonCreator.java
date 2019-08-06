package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferenceCreator;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;
import edu.kit.ifv.mobitopp.simulation.modeChoice.UncorrelatedModeChoicePreferenceCreator;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

public class PanelBasedPersonCreator implements PersonCreator {

	private final CommutationTicketModelIfc commutationTicketModel;
	private final ModeChoicePreferenceCreator modeChoicePreferenceCreator;

	private int personIdCounter = 1;
	
  public PanelBasedPersonCreator(
      CommutationTicketModelIfc commutationTicketModel,
      ModeChoicePreferenceCreator modeChoicePreferenceCreator) {
    this.commutationTicketModel = commutationTicketModel;
    this.modeChoicePreferenceCreator = modeChoicePreferenceCreator;
  }

  public PanelBasedPersonCreator(CommutationTicketModelIfc commutationTicketModel) {
    this(commutationTicketModel, new UncorrelatedModeChoicePreferenceCreator(0));
  }

	public PersonBuilder createPerson(
		PersonOfPanelData panelPerson,
		HouseholdOfPanelData panelHousehold,
		HouseholdForSetup household,
		Zone zone
	) {
    boolean hasCommuterTicket = this.commutationTicketModel
        .estimateCommutationTicket(panelPerson, panelHousehold, zone);
    return newPerson(personIdCounter++, panelPerson, household, hasCommuterTicket);
  }

  protected PersonBuilder newPerson(
			int oid,
			PersonOfPanelData personOfPanelData,
			HouseholdForSetup household,
			boolean hasCommuterTicket
	) {
    PersonOfPanelDataId panel_id = personOfPanelData.getId(); 
    PersonId pid = new PersonId(oid, household.getId(), panel_id.getPersonNumber());
    return new DefaultPersonForSetup(
        pid, 
        household, 
        personOfPanelData.age(), 
        personOfPanelData.employment(), 
        personOfPanelData.gender(), 
        personOfPanelData.graduation(), 
        personOfPanelData.getIncome(), 
        createPreferencesFor(personOfPanelData))
        .setHasBike(personOfPanelData.hasBicycle())
        .setHasAccessToCar(personOfPanelData.hasAccessToCar())
        .setHasPersonalCar(personOfPanelData.hasPersonalCar())
        .setHasCommuterTicket(hasCommuterTicket)
        .setHasDrivingLicense(personOfPanelData.hasLicence())
        .setModeChoicePreferences(modeChoicePreferenceCreator.createPreferences());
	}

  private ModeChoicePreferences createPreferencesFor(PersonOfPanelData personOfPanelData) {
    Map<Mode, Double> preferences = new LinkedHashMap<>();
    preferences.put(Mode.BIKE, (double) personOfPanelData.pref_cycling);
    preferences.put(Mode.CAR, (double) personOfPanelData.pref_cardriver);
    preferences.put(Mode.PASSENGER, (double) personOfPanelData.pref_carpassenger);
    preferences.put(Mode.PEDESTRIAN, (double) personOfPanelData.pref_walking);
    preferences.put(Mode.PUBLICTRANSPORT, (double) personOfPanelData.pref_publictransport);
    return new ModeChoicePreferences(preferences);
  }

}
