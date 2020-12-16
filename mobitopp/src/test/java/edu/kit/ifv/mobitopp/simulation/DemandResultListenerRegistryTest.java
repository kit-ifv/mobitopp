package edu.kit.ifv.mobitopp.simulation;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.verification.Times;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.verification.VerificationMode;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class DemandResultListenerRegistryTest {

	private MultipleResults personResults;
	private DemandResultListenerRegistry registry;
	private PersonListenerChangerHook registerHook;
	private PersonListenerChangerHook unregisterHook;
	private List<ZoneId> oids;
	private Set<Mode> choiceSet;
	private SimulationContext context;

	@BeforeEach
	void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {
		int matrixColumn = 0;
		ZoneId zoneId = new ZoneId("11", matrixColumn);
		oids = asList(zoneId);
		choiceSet = Sets.newLinkedHashSet(StandardMode.values());

		SimulationDays simDays = mock(SimulationDays.class);
		when(simDays.endDate()).thenReturn(Time.start.plusDays(7));

		WrittenConfiguration configuration = mock(WrittenConfiguration.class);
		when(configuration.getResultFolder()).thenReturn("testFolder");

		context = mock(SimulationContext.class);
		personResults = new MultipleResults();
		when(context.personResults()).thenReturn(personResults);
		when(context.simulationDays()).thenReturn(simDays);
		when(context.configuration()).thenReturn(configuration);

		DemandSimulatorPassenger simulator = mock(DemandSimulatorPassenger.class);
		when(simulator.context()).thenReturn(context);

		registry = new DemandResultListenerRegistry(simulator);

		Field registerHookField = registry.getClass().getDeclaredField("registerHook");
		Field unregisterHookField = registry.getClass().getDeclaredField("unregisterHook");
		registerHookField.setAccessible(true);
		unregisterHookField.setAccessible(true);
		registerHook = (PersonListenerChangerHook) registerHookField.get(registry);
		unregisterHook = (PersonListenerChangerHook) unregisterHookField.get(registry);

		verify(simulator).addBeforeTimeSliceHook(registerHook);
		verify(simulator).addAfterTimeSliceHook(unregisterHook);

		registerHook = mock(PersonListenerChangerHook.class);
		registerHookField.set(registry, registerHook);

		unregisterHook = mock(PersonListenerChangerHook.class);
		unregisterHookField.set(registry, unregisterHook);
	}

	@Test
	void addHourlyDayListeners() {

		registry.addHourlyDayListeners(oids, choiceSet, DayOfWeek.MONDAY, context);

		for (int hour = 0; hour < 24; hour++) {
			verify(registerHook, VerificationModeFactory.times(choiceSet.size())).add(
					eq(Time.start.plusHours(hour)), any(AggregateDemandOfStartedTrips.class));
			verify(unregisterHook, VerificationModeFactory.times(choiceSet.size())).add(
					eq(Time.start.plusHours(hour + 1)), any(AggregateDemandOfStartedTrips.class),
					any(Runnable.class));
		}

	}

	@Test
	void addHourlyWeekdayListeners() {

		registry.addHourlyWeekdayListeners(oids, choiceSet, context);
		

		int times = choiceSet.size() * 24;
		Times verificationMode = VerificationModeFactory.times(times);
		
		verify(registerHook, verificationMode).add(
				eq(Time.start), any(AggregateDemandOfStartedTrips.class));
		
		verify(unregisterHook, verificationMode).add(
				eq(SimpleTime.ofDays(5).plusHours(1)), any(AggregateDemandOfStartedTrips.class),
				any(Runnable.class));


	}

}
