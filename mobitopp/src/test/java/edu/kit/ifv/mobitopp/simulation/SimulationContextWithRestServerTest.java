package edu.kit.ifv.mobitopp.simulation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

public class SimulationContextWithRestServerTest {

	private WrittenConfiguration config;
	private SimulationContext context;
	private SimulationContextWithRestServer decorator;

	@BeforeEach
	public void initialise() {
		config = mock(WrittenConfiguration.class);
		when(config.getPort()).thenReturn(5432, 5433, 5434, 5435, 5436, 5437, 5438, 5439, 5440, 5441, 5442, 5443, 5444, 5445, 5446, 5447, 5448, 5449, 5450);
		
		context = mock(SimulationContext.class);
		when(context.configuration()).thenReturn(config);
		
		decorator = new SimulationContextWithRestServer(context);
		Mockito.reset(context);
	}

	@MethodSource("functions")
	@ParameterizedTest
	void getter(Function<SimulationContext, ?> method) throws Exception {
		method.apply(decorator);

		method.apply(verify(context));
	}

	static Stream<Function<SimulationContext, ?>> functions() {
		return Stream.of(SimulationContext::configuration, SimulationContext::destinationChoiceParameters,
				SimulationContext::modeChoiceParameters, SimulationContext::experimentalParameters, 
				SimulationContext::seed, SimulationContext::fractionOfPopulation, SimulationContext::simulationDays,
				SimulationContext::dataRepository, SimulationContext::zoneRepository, SimulationContext::impedance,
				SimulationContext::vehicleBehaviour, SimulationContext::personLoader, SimulationContext::results, 
				SimulationContext::personResults);
	}
	
	//@Test
	public void beforeAfterSimulation() {
		decorator.beforeSimulation();
		
		verify(context).beforeSimulation();
		
		assertTrue(decorator.restServer().isStarted());
		
		decorator.afterSimulation();
		
		verify(context).afterSimulation();
		
		assertFalse(decorator.restServer().isStarted());
	}



}
