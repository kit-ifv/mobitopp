package edu.kit.ifv.mobitopp.populationsynthesis.community;

import static edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel.community;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandDataForZoneCalculatorIfc;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

@ExtendWith(MockitoExtension.class)
public class IsolatedZonesCommunityDemandCalculatorTest {

	private static final String communityId = "1";

	@Mock
	private DemandDataForZoneCalculatorIfc zoneCalculator;
	@Mock
	private ImpedanceIfc impedance;

	@Test
	void calculateDemand() throws Exception {
		DemandZone someZone = ExampleDemandZones.create().getSomeZone();
		DemandZone otherZone = ExampleDemandZones.create().getOtherZone();
		Demography someDemography = someZone.nominalDemography();
		Community someCommunity = new MultipleZones(communityId, community, someDemography, someZone, otherZone);
		IsolatedZonesCommunityDemandCalculator calculator = new IsolatedZonesCommunityDemandCalculator(
				zoneCalculator);

		calculator.calculateDemandData(someCommunity, impedance);

		verify(zoneCalculator).calculateDemandData(someZone, impedance);
		verify(zoneCalculator).calculateDemandData(otherZone, impedance);
	}
}
