package edu.kit.ifv.mobitopp.populationsynthesis.community;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;

@ExtendWith(MockitoExtension.class)
class PersonBasedStepTest {

	@Mock
	private PersonBuilder person;
	@Mock
	private HouseholdForSetup household;
	@Mock
	private Consumer<PersonBuilder> consumer;

	private Community community;

	@Test
	void assignsActivities() throws Exception {
		final DemandZone someZone = ExampleDemandZones.create().someZone();
		when(household.persons()).thenReturn(Stream.of(person));
		someZone.getPopulation().addHousehold(household);
		community = new SingleZone(someZone);
		
		new PersonBasedStep(consumer).process(community);
		
		verify(consumer).accept(person);
	}
}
