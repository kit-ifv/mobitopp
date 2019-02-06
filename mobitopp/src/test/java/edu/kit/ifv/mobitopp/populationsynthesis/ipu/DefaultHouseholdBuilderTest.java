package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static edu.kit.ifv.mobitopp.populationsynthesis.HouseholdOfPanelDataBuilder.householdOfPanelData;
import static edu.kit.ifv.mobitopp.populationsynthesis.PersonOfPanelDataBuilder.personOfPanelData;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHousehold;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonForSetup;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class DefaultHouseholdBuilderTest {

	@Test
	public void createsSimulatedPersons() {
		DemandZone demandZone = ExampleDemandZones.create().someZone();
		HouseholdCreator householdCreator = mock(HouseholdCreator.class);
		PersonCreator personCreator = mock(PersonCreator.class);
		PanelDataRepository panelData = mock(PanelDataRepository.class);
		HouseholdOfPanelData panelHousehold = householdOfPanelData().build();

		HouseholdForSetup demandHousehold = ExampleHousehold.createHousehold(demandZone.zone());
		when(householdCreator.createHousehold(panelHousehold, demandZone.zone()))
				.thenReturn(demandHousehold);
		PersonOfPanelData someMember = personOfPanelData().build();
		PersonOfPanelData otherMember = personOfPanelData().build();
		PersonForSetup somePerson = createPerson();
		PersonForSetup otherPerson = createPerson();
		when(personCreator.createPerson(someMember, panelHousehold, demandHousehold, demandZone.zone()))
				.thenReturn(somePerson);
		when(personCreator.createPerson(otherMember, panelHousehold, demandHousehold, demandZone.zone()))
		.thenReturn(otherPerson);
		List<PersonOfPanelData> householdMembers = asList(someMember, otherMember);
		when(panelData.getPersonsOfHousehold(panelHousehold.id())).thenReturn(householdMembers);

		HouseholdBuilder build = new DefaultHouseholdBuilder(demandZone, householdCreator, personCreator,
				panelData);

		HouseholdForSetup household = build.householdFor(panelHousehold);

		assertThat(demandZone.actualDemography().maleAge().getItems(),
				hasItem(new RangeDistributionItem(11, Integer.MAX_VALUE, 2)));
		verify(householdCreator).createHousehold(panelHousehold, demandZone.zone());
		verify(personCreator, times(2))
				.createPerson(any(), eq(panelHousehold), eq(household), eq(demandZone.zone()));
	}

	private PersonForSetup createPerson() {
		PersonForSetup person = mock(PersonForSetup.class);
		when(person.gender()).thenReturn(Gender.MALE);
		when(person.age()).thenReturn(20);
		when(person.employment()).thenReturn(Employment.FULLTIME);
		return person;
	}
}
