package edu.kit.ifv.mobitopp.populationsynthesis.calculator;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandDataForZoneCalculatorIfc;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;
import edu.kit.ifv.mobitopp.populationsynthesis.calculator.SingleZoneDemandCalculator;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class SingleZoneDemandCalculatorTest {

	@Test
	void calculateForEachZone() throws Exception {
		DemandZone someZone = ExampleDemandZones.create().someZone();
		DemandZone otherZone = ExampleDemandZones.create().otherZone();
		DemandDataForZoneCalculatorIfc zoneCalculator = mock(DemandDataForZoneCalculatorIfc.class);
		DemandZoneRepository zoneRepository = mock(DemandZoneRepository.class);
		when(zoneRepository.getZones()).thenReturn(asList(someZone, otherZone));
		ImpedanceIfc impedance = mock(ImpedanceIfc.class);
		SingleZoneDemandCalculator calculator = new SingleZoneDemandCalculator(zoneCalculator,
				zoneRepository, impedance);
		
		calculator.calculateDemand();
		
		verify(zoneCalculator).calculateDemandData(someZone, impedance);
		verify(zoneCalculator).calculateDemandData(otherZone, impedance);
	}
}
