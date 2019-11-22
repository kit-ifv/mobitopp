package edu.kit.ifv.mobitopp.populationsynthesis.community;

import static org.mockito.Mockito.verify;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;

@ExtendWith(MockitoExtension.class)
public class HouseholdBasedStepTest {

	@Mock
	private HouseholdForSetup household;
	@Mock
	private Consumer<HouseholdForSetup> activityAssigner;

	private Community community;

	@Test
	void assignsActivities() throws Exception {
		final DemandZone someZone = ExampleDemandZones.create().someZone();
		someZone.getPopulation().addHousehold(household);
		community = new SingleZone(someZone);
		
		new HouseholdBasedStep(activityAssigner).process(community);
		
		verify(activityAssigner).accept(household);
	}
}
