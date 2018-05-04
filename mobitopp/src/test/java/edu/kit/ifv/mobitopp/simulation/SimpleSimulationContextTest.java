package edu.kit.ifv.mobitopp.simulation;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNetwork;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.DataRepositoryForSimulation;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.data.local.configuration.SimulationParser;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

public class SimpleSimulationContextTest {

	private ElectricChargingWriter chargingWriter;
	private ResultWriter resultWriter;
	private WrittenConfiguration configuration;
	private SimulationParser format;

	@Before
	public void initialise() {
		chargingWriter = mock(ElectricChargingWriter.class);
		resultWriter = mock(ResultWriter.class);
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
	}

	private SimpleSimulationContext context() {
		DynamicParameters experimental = new DynamicParameters(Collections.emptyMap());
		VisumNetwork network = visumNetwork().build();
		SimpleRoadNetwork roadNetwork = new SimpleRoadNetwork(network);
		DataRepositoryForSimulation dataRepository = mock(DataRepositoryForSimulation.class);
		SimulationDays simulationDays = SimulationDays.containing(1);
		PersonResults personResults = mock(PersonResults.class);
		return new SimpleSimulationContext(configuration, experimental, network, roadNetwork,
				dataRepository, simulationDays, format, resultWriter, chargingWriter, personResults);
	}
}
