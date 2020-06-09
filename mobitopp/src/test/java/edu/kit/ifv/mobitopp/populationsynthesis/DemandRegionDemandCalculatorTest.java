package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel.community;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.calculator.DemandDataCalculator;
import edu.kit.ifv.mobitopp.populationsynthesis.community.MultipleZones;
import edu.kit.ifv.mobitopp.populationsynthesis.region.PopulationSynthesisStep;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

@ExtendWith(MockitoExtension.class)
public class DemandRegionDemandCalculatorTest {

	private static final String communityId = "1";

	@Mock
	private DemandDataForDemandRegionCalculator regionCalculator;
	@Mock
	private DemandZoneRepository zoneRepository;
	@Mock
	private ImpedanceIfc impedance;
	@Mock
	private PopulationSynthesisStep firstStep;
	@Mock
	private PopulationSynthesisStep secondStep;
	@Mock
	private List<DemandRegion> regions;

	@Test
	void calculateDemand() throws Exception {
		DemandZone someZone = ExampleDemandZones.create().getSomeZone();
		DemandZone otherZone = ExampleDemandZones.create().getOtherZone();
		Demography demography = someZone.nominalDemography();
		DemandRegion someRegion = new MultipleZones(communityId, community, demography, someZone,
				otherZone);
		regions = List.of(someRegion);
		List<PopulationSynthesisStep> steps = asList(firstStep, secondStep);
		DemandDataCalculator calculator = new DemandRegionDemandCalculator(regions, regionCalculator,
				steps, impedance);

		calculator.calculateDemand();

		verify(regionCalculator).calculateDemandData(someRegion, impedance);
		steps.forEach(s -> verify(s).process(someRegion));
	}
}
