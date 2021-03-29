package edu.kit.ifv.mobitopp.simulation;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InOrder;

import edu.kit.ifv.mobitopp.data.PersonLoader;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.result.ResultsForTests;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityPeriodFixer;
import edu.kit.ifv.mobitopp.simulation.activityschedule.LeisureWalkActivityPeriodFixer;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.DefaultActivityDurationRandomizer;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportBehaviour;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.simulation.person.TripFactory;
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

	private TripFactory tripFactory;

	private ActivityPeriodFixer fixer;
	private DefaultActivityDurationRandomizer randomizer;

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
		PersonResults results = createResults(impedance);
		when(context.personResults()).thenReturn(results);
		tripFactory = mock(TripFactory.class);
		fixer = new LeisureWalkActivityPeriodFixer();
		randomizer = new DefaultActivityDurationRandomizer(1234);
		simulator = new DemandSimulatorPassenger(null, null, null, fixer, randomizer, tripFactory, null, null,
				context);
		simulator.addBeforeTimeSliceHook(firstBeforeSlice);
		simulator.addBeforeTimeSliceHook(secondBeforeSlice);
		simulator.addAfterTimeSliceHook(firstAfterSlice);
		simulator.addAfterTimeSliceHook(secondAfterSlice);
	}

	private PersonResults createResults(ImpedanceIfc impedance) throws IOException {
		MultipleResults results = new MultipleResults();
		results.addListener(new TripfileWriter(ResultsForTests.at(baseFolder), impedance));
		return results;
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

	private void executeCurrentTime() {
		simulator.handle(currentDate);
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

	@Test
	public void initHouseholds() {
		PersonLoader personLoader = mock(PersonLoader.class);
		Household household = mock(Household.class);
		Person inputPerson = mock(Person.class);
		SimulationPerson mockedPerson = mock(SimulationPerson.class);
		SimulationPersonFactory personFactory = mock(SimulationPersonFactory.class);
		when(household.persons()).thenReturn(Stream.of(inputPerson));
		when(personLoader.households()).thenReturn(Stream.of(household));
		when(personFactory
				.create(any(), any(), any(), any(), any(), any(), any(), any(), any(), anyLong(), any()))
						.thenReturn(mockedPerson);
		when(context.personLoader()).thenReturn(personLoader);
		simulator = new DemandSimulatorPassenger(null, null, null, fixer, randomizer, tripFactory, null,
				emptySet(), null, context, personFactory);
		EventQueue queue = mock(EventQueue.class);
		PublicTransportBehaviour boarder = mock(PublicTransportBehaviour.class);
		PersonListener listener = mock(PersonListener.class);
		long seed = 42;
		Set<Mode> modesInSimulation = emptySet();
		PersonState initialState = mock(PersonState.class);
		simulator
				.initFractionOfHouseholds(queue, boarder, seed, listener, modesInSimulation, initialState);
		
		verify(personFactory)
				.create(eq(inputPerson), any(), any(), any(), any(), any(), any(), any(), any(), anyLong(),
						any());
	}
}
