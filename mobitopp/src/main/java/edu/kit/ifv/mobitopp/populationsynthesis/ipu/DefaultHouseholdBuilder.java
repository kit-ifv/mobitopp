package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonForSetup;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class DefaultHouseholdBuilder implements HouseholdBuilder {

	private final DemandZone demandZone;
	private final HouseholdCreator householdCreator;
	private final PersonCreator personCreator;
	private final PanelDataRepository panelData;
	private HouseholdForSetup currentHousehold;
	
	public DefaultHouseholdBuilder(
			DemandZone demandZone, HouseholdCreator householdCreator, PersonCreator personCreator,
			PanelDataRepository panelData) {
		super();
		this.demandZone = demandZone;
		this.householdCreator = householdCreator;
		this.personCreator = personCreator;
		this.panelData = panelData;
	}

	@Override
  public HouseholdForSetup householdFor(HouseholdOfPanelData panelHousehold) {
		currentHousehold = householdCreator.createHousehold(panelHousehold, zone());
		createSimulatedPeople(panelHousehold);
		demandZone.actualDemography().incrementHousehold(currentHousehold.getSize());
		int income = currentHousehold.monthlyIncomeEur();
    demandZone.actualDemography().increment(StandardAttribute.income, income);
		return currentHousehold;
	}

	private void createSimulatedPeople(HouseholdOfPanelData panelHousehold) {
		for (PersonOfPanelData panelPerson : peopleOf(panelHousehold)) {
			createPerson(panelPerson, panelHousehold);
		}
	}

	private List<PersonOfPanelData> peopleOf(HouseholdOfPanelData panelHousehold) {
		return panelData.getPersonsOfHousehold(panelHousehold.id());
	}

	private void createPerson(PersonOfPanelData panelPerson, HouseholdOfPanelData panelHousehold) {
	  PersonForSetup person = personCreator
				.createPerson(panelPerson, panelHousehold, currentHousehold, zone());
		updateDemography(person);
		currentHousehold.addPerson(person);
	}

	private void updateDemography(PersonForSetup person) {
		demandZone.actualDemography().incrementAge(person.gender(), person.age());
		demandZone.actualDemography().incrementEmployment(person.employment());
	}

	private Zone zone() {
		return demandZone.zone();
	}

}
