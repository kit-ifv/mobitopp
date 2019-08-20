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
import edu.kit.ifv.mobitopp.populationsynthesis.calculator.DemandDataCalculator;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class PopulationSynthesisTest {

	private DemandDataCalculator calculator;
	private OpportunityLocationSelector locationSelector;
	private SynthesisContext context;
	private ImpedanceIfc impedance;
	private ExampleDemandZones zones;

	@Before
	public void initialise() {
		calculator = mock(DemandDataCalculator.class);
		locationSelector = mock(OpportunityLocationSelector.class);
		context = mock(SynthesisContext.class);
		impedance = mock(ImpedanceIfc.class);
		zones = ExampleDemandZones.create();
		
		DemandZoneRepository zoneRepository = mock(DemandZoneRepository.class);
		when(zoneRepository.getZones()).thenReturn(zones.asList());
		when(context.zoneRepository()).thenReturn(zoneRepository);
		when(context.impedance()).thenReturn(impedance);
	}

	@Test
	public void usesCalculatorToCalculateDemand() {
		PopulationSynthesis synthesis = newSynthesis();

		synthesis.doCreatePopulation(emptyMap());

		verify(calculator).calculateDemand();
	}

	private PopulationSynthesis newSynthesis() {
		return new PopulationSynthesis(context) {

			@Override
			protected DemandDataCalculator createCalculator(
					Map<ActivityType, FixedDistributionMatrix> commuterMatrices) {
				return calculator;
			}
			
			@Override
			protected OpportunityLocationSelector createOpportunityLocationSelector() {
				return locationSelector;
			}
		};
	}
}
