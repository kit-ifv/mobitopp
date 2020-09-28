package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static edu.kit.ifv.mobitopp.populationsynthesis.PersonOfPanelDataBuilder.personOfPanelData;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHouseholdOfPanelData;
import edu.kit.ifv.mobitopp.populationsynthesis.ExamplePersonOfPanelData;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonOfPanelDataBuilder;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

@ExtendWith(MockitoExtension.class)
public class MaleAgeTest {

	@Mock
	private RegionalContext context;

	@BeforeEach
	public void initialise() {
		lenient().when(context.name()).thenReturn("my-context-1");
	}

	@Test
	public void createsConstraint() {
		Demography demography = ExampleDemandZones.create().someZone().nominalDemography();
		MaleAge maleAge = new MaleAge(context, StandardAttribute.maleAge, 0, 10);

		Constraint constraint = maleAge.createConstraint(demography);

		assertThat(constraint).isEqualTo(new SimpleConstraint(maleAge.name(), 4));
		verify(context, times(2)).name();
	}

	@Test
	public void valueForHousehold() {
		MaleAge maleAge = new MaleAge(context, StandardAttribute.maleAge, 0, 5);

		HouseholdOfPanelData household = ExampleHouseholdOfPanelData.household;
		PanelDataRepository panelDataRepository = mock(PanelDataRepository.class);
		PersonOfPanelData matchingConstraint = personOfPanelData()
				.withId(ExamplePersonOfPanelData.anId)
				.withGender(PersonOfPanelDataBuilder.genderMale)
				.withAge(2)
				.build();
		PersonOfPanelData notMatchingConstraint = personOfPanelData()
				.withId(ExamplePersonOfPanelData.otherId)
				.withGender(PersonOfPanelDataBuilder.genderMale)
				.withAge(6)
				.build();
		when(panelDataRepository.getPersonsOfHousehold(household.id()))
				.thenReturn(asList(matchingConstraint, notMatchingConstraint));

		int matchingPeople = maleAge.valueFor(household, panelDataRepository);

		assertThat(matchingPeople, is(1));
	}
}
