package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Collections.emptyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class PopulationSynthesisTest {

	private DemandDataForZoneCalculatorIfc calculator;
	private SynthesisContext context;
	private ImpedanceIfc impedance;
	private ExampleDemandZones zones;

	@Before
	public void initialise() {
		calculator = mock(DemandDataForZoneCalculatorIfc.class);
		context = mock(SynthesisContext.class);
		impedance = mock(ImpedanceIfc.class);
		zones = ExampleDemandZones.create();
		
		DemandZoneRepository zoneRepository = mock(DemandZoneRepository.class);
		when(zoneRepository.getZones()).thenReturn(zones.asList());
		when(context.zoneRepository()).thenReturn(zoneRepository);
		when(context.impedance()).thenReturn(impedance);
	}

	@Test
	public void createsPopulationForEachZone() {
		PopulationSynthesis synthesis = newSynthesis();

		synthesis.doCreatePopulation(emptyMap());

		verify(calculator).calculateDemandData(zones.someZone(), impedance);
		verify(calculator).calculateDemandData(zones.otherZone(), impedance);
	}

	private PopulationSynthesis newSynthesis() {
		return new PopulationSynthesis(context) {

			@Override
			protected DemandDataForZoneCalculatorIfc createCalculator(
					Map<ActivityType, FixedDistributionMatrix> fdMatrices) {
				return calculator;
			}
		};
	}
}
