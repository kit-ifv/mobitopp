package edu.kit.ifv.mobitopp.simulation;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class SimulationTest {

	private DemandSimulator simulator;
	private SimulationContext context;
	private Simulation simulation;

	@Before
	public void initialise() {
		context = mock(SimulationContext.class);
		simulator = mock(DemandSimulator.class);
		simulation = simulation();
		when(context.simulationDays()).thenReturn(SimulationDays.containing(1));
	}

	private Simulation simulation() {
		return new Simulation(context) {

			@Override
			protected DemandSimulator simulator() {
				return simulator;
			}

		};
	}

	@Test
	public void simulationOrder() {
		InOrder inOrder = inOrder(context, simulator);
		simulation.simulate();

		inOrder.verify(context).beforeSimulation();
		inOrder.verify(simulator).startSimulation();
		inOrder.verify(context).afterSimulation();
	}
}
