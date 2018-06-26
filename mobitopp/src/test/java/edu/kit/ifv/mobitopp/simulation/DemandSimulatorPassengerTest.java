package edu.kit.ifv.mobitopp.simulation;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InOrder;

import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.result.ResultsForTests;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.DefaultActivityDurationRandomizer;
import edu.kit.ifv.mobitopp.time.Time;

public class DemandSimulatorPassengerTest {

	@Rule
	public TemporaryFolder baseFolder = new TemporaryFolder();

	private SimulationContext context;
	private DemandSimulatorPassenger simulator;
	private ZoneRepository zoneRepository;
	private Hook firstBeforeSlice;
	private Hook secondBeforeSlice;
	private Hook firstAfterSlice;
	private Hook secondAfterSlice;
	private Time currentDate;

	@Before
	public void initialise() throws Exception {
		context = mock(SimulationContext.class);
		zoneRepository = mock(ZoneRepository.class);
		firstBeforeSlice = mock(Hook.class);
		secondBeforeSlice = mock(Hook.class);
		firstAfterSlice = mock(Hook.class);
		secondAfterSlice = mock(Hook.class);
		currentDate = Data.someTime();
		ImpedanceIfc impedance = mock(ImpedanceIfc.class);
		SimulationDays simulationDays = simulationDays();
		when(context.simulationDays()).thenReturn(simulationDays);
		when(context.configuration()).thenReturn(new WrittenConfiguration());
		when(context.zoneRepository()).thenReturn(zoneRepository);
		when(context.vehicleBehaviour()).thenReturn(VehicleBehaviour.noBehaviour);
		PersonResults results = new TripfileWriter(ResultsForTests.at(baseFolder), impedance);
		when(context.personResults()).thenReturn(results);
		simulator = new DemandSimulatorPassenger(null, null, null,
				new DefaultActivityDurationRandomizer(1234), null, null, context);
		simulator.addBeforeTimeSliceHook(firstBeforeSlice);
		simulator.addBeforeTimeSliceHook(secondBeforeSlice);
		simulator.addAfterTimeSliceHook(firstAfterSlice);
		simulator.addAfterTimeSliceHook(secondAfterSlice);
	}

	private SimulationDays simulationDays() {
		List<Time> days = asList(firstDay(), lastDay());
		return new SimulationDays(days);
	}

	private Time lastDay() {
		return firstDay().nextDay();
	}

	private Time firstDay() {
		return SimulationDays.simulationStart();
	}

	@Test
	public void executesHooksBeforeAndAfterTimeSlice() throws Exception {
		InOrder firstInOrder = inOrder(firstBeforeSlice, firstAfterSlice);
		InOrder secondInOrder = inOrder(secondBeforeSlice, secondAfterSlice);

		executeCurrentTime();

		firstInOrder.verify(firstBeforeSlice).process(currentDate);
		secondInOrder.verify(secondBeforeSlice).process(currentDate);
		firstInOrder.verify(firstAfterSlice).process(currentDate);
		secondInOrder.verify(secondAfterSlice).process(currentDate);
	}

	@Test
	public void simulationStart() {
		assertThat(simulator.simulationStart(), is(equalTo(firstDay())));
	}

	@Test
	public void simulationEnd() {
		Time dayAfterLastDay = lastDay().nextDay();
		assertThat(simulator.simulationEnd(), is(equalTo(dayAfterLastDay)));
	}

	private void executeCurrentTime() {
		simulator.handle(currentDate);
	}
}
