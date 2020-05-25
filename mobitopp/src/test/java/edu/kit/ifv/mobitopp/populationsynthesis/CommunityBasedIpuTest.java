package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHouseholdSelector;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class CommunityBasedIpuTest {

	@Test
	void testName() throws Exception {
		Results results = null;
		WeightedHouseholdSelector householdSelector = null;
		HouseholdCreator householdCreator = null;
		PersonCreator personCreator = null;
		DataRepositoryForPopulationSynthesis dataRepository = null;
		SynthesisContext context = null;
		AttributeType householdFilterType = null;
		Function<DemandZone, Predicate<HouseholdOfPanelData>> householdFilter = null;
		CommunityBasedIpu communityBasedIpu = new CommunityBasedIpu(results, householdSelector,
				householdCreator, personCreator, dataRepository, context, householdFilterType,
				householdFilter);

		DemandDataForZoneCalculatorIpu zonalIpu = new DemandDataForZoneCalculatorIpu(results,
				householdSelector, householdCreator, personCreator, dataRepository, context,
				householdFilterType, householdFilter);

		communityBasedIpu.calculateDemandData(null, null);
	}
}
