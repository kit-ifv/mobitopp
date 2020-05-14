package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.calculator.DemandDataCalculator;
import edu.kit.ifv.mobitopp.populationsynthesis.community.Community;
import edu.kit.ifv.mobitopp.populationsynthesis.community.CommunityRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.community.MultipleZones;
import edu.kit.ifv.mobitopp.populationsynthesis.community.PopulationSynthesisStep;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

@ExtendWith(MockitoExtension.class)
public class CommunityDemandCalculatorTest {

	private static final String communityId = "1";

	@Mock
	private DemandDataForCommunityCalculator communityCalculator;
	@Mock
	private DemandZoneRepository zoneRepository;
	@Mock
	private ImpedanceIfc impedance;
	@Mock
	private PopulationSynthesisStep firstStep;
	@Mock
	private PopulationSynthesisStep secondStep;
	@Mock
	private CommunityRepository communityToZone;

	@Test
	void calculateDemand() throws Exception {
		DemandZone someZone = ExampleDemandZones.create().getSomeZone();
		DemandZone otherZone = ExampleDemandZones.create().getOtherZone();
		Community someCommunity = new MultipleZones(communityId, someZone, otherZone);
		when(communityToZone.getCommunities()).thenReturn(asList(someCommunity));
		List<PopulationSynthesisStep> steps = asList(firstStep, secondStep);
		DemandDataCalculator calculator = new CommunityDemandCalculator(communityToZone,
				communityCalculator, steps, impedance);

		calculator.calculateDemand();

		verify(communityCalculator).calculateDemandData(someCommunity, impedance);
		steps.forEach(s -> verify(s).process(someCommunity));
	}
}
