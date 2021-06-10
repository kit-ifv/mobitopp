package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId.fromPersonId;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemandZoneBasedStep;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

public class SelectFixedDestinationStep extends DemandZoneBasedStep {

	public SelectFixedDestinationStep(FixedDestinationSelector selector, PanelDataRepository panelData) {
		super(demandZone -> setAllFixedDestinations(demandZone, selector, panelData));
	}

	public static void setAllFixedDestinations(DemandZone demandZone, FixedDestinationSelector selector,
			PanelDataRepository panelData) {
		
		Function<PersonBuilder, Integer> personToId = p -> p.getId().getOid();
		Function<PersonOfPanelDataId, PersonOfPanelData> idToPanelPerson = id -> panelData.getPerson(id);
		Function<PersonBuilder, PersonOfPanelData> personToPanelPerson = 
				p -> idToPanelPerson.apply(fromPersonId(p.getId()));

		Map<Integer, PersonBuilder> demandPersons = demandZone.getPopulation().households()
				.flatMap(HouseholdForSetup::persons).collect(toMap(personToId, identity()));

		Map<Integer, PersonOfPanelData> panelPersons = demandPersons.entrySet().stream()
				.collect(toMap(Map.Entry::getKey, e -> personToPanelPerson.apply(e.getValue())));

		selector.setFixedDestinations(demandZone.zone(), demandPersons, panelPersons);
	}

}
