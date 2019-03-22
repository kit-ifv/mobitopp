package edu.kit.ifv.mobitopp.simulation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.DataRepositoryForSimulation;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.data.local.configuration.SimulationParser;
import edu.kit.ifv.mobitopp.result.ResultWriter;

public class SimpleSimulationContextTest {

	private ElectricChargingWriter chargingWriter;
	private ResultWriter resultWriter;
	private WrittenConfiguration configuration;
	private SimulationParser format;
  private PersonResults personResults;

	@Before
	public void initialise() {
		chargingWriter = mock(ElectricChargingWriter.class);
		resultWriter = mock(ResultWriter.class);
		personResults = mock(PersonResults.class);
		configuration = new WrittenConfiguration();
		format = new SimulationParser(Collections.emptyMap());
	}

	@Test
	public void beforeSimulation() {
		context().beforeSimulation();

		verify(chargingWriter).clear();
	}

	@Test
	public void afterSimulation() {
		context().afterSimulation();

		verify(chargingWriter).print();
		verify(resultWriter).close();
		verify(personResults).close();
	}

	private SimpleSimulationContext context() {
		DynamicParameters experimental = new DynamicParameters(Collections.emptyMap());
		DataRepositoryForSimulation dataRepository = mock(DataRepositoryForSimulation.class);
		SimulationDays simulationDays = SimulationDays.containing(1);
		DynamicParameters modeChoice = new DynamicParameters(Collections.emptyMap());
		return new SimpleSimulationContext(configuration, experimental, dataRepository, simulationDays,
				format, resultWriter, chargingWriter, personResults, modeChoice);
	}

}
