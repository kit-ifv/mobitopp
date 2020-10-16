package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonCreator;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class DefaultHouseholdBuilder implements HouseholdBuilder {

	private final HouseholdCreator householdCreator;
	private final PersonCreator personCreator;
	private final PanelDataRepository panelData;
	private HouseholdForSetup currentHousehold;
	
  public DefaultHouseholdBuilder(
      final HouseholdCreator householdCreator, final PersonCreator personCreator,
      final PanelDataRepository panelData) {
    super();
    this.householdCreator = householdCreator;
    this.personCreator = personCreator;
    this.panelData = panelData;
  }

	@Override
  public HouseholdForSetup householdFor(
      final HouseholdOfPanelData panelHousehold, final DemandZone zone) {
    currentHousehold = householdCreator.createHousehold(panelHousehold, zone.zone());
  	createSimulatedPeople(panelHousehold, zone);
		zone.actualDemography().incrementHousehold(currentHousehold.getSize());
		int income = currentHousehold.monthlyIncomeEur();
    zone.actualDemography().increment(StandardAttribute.income, income);
		return currentHousehold;
	}

  private void createSimulatedPeople(
      final HouseholdOfPanelData panelHousehold, final DemandZone zone) {
  	for (PersonOfPanelData panelPerson : peopleOf(panelHousehold)) {
			createPerson(panelPerson, panelHousehold, zone);
		}
	}

	private List<PersonOfPanelData> peopleOf(HouseholdOfPanelData panelHousehold) {
		return panelData.getPersonsOfHousehold(panelHousehold.id());
	}

  private void createPerson(
      final PersonOfPanelData panelPerson, final HouseholdOfPanelData panelHousehold,
      final DemandZone zone) {
    PersonBuilder person = personCreator
  			.createPerson(panelPerson, panelHousehold, currentHousehold, zone.zone());
		updateDemography(person, zone);
		currentHousehold.addPerson(person);
	}

	private void updateDemography(final PersonBuilder person, final DemandZone zone) {
		zone.actualDemography().incrementAge(person.gender(), person.age());
		zone.actualDemography().incrementEmployment(person.employment());
	}

}
