package edu.kit.ifv.mobitopp.simulation;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.MockitoJUnitRunner;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

public class ActiveListenersManagerBuilderTest {
	
	@Captor ArgumentCaptor<Entry<Time, PersonListener>> activationCaptor;
	@Captor ArgumentCaptor<Entry<Time, PersonListener>> deactivationCaptor;
	@Captor ArgumentCaptor<Entry<Time, Runnable>> actionCaptor;

	private MultipleResults personResults;
	private ActiveListenersManagerBuilder managerBuilder;
	private List<ZoneId> oids;
	private Set<Mode> choiceSet;
	private SimulationContext context;
	
	private Collection<Entry<Time, PersonListener>> activations;
	private Collection<Entry<Time, PersonListener>> deactivations;
	private Collection<Entry<Time, Runnable>> actions;

	@BeforeEach
	public void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
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

		
		
		managerBuilder = new ActiveListenersManagerBuilder();
		
		activations = (Collection<Entry<Time,PersonListener>>) mock(Collection.class);
		deactivations = (Collection<Entry<Time,PersonListener>>) mock(Collection.class);
		actions = (Collection<Entry<Time,Runnable>>) mock(Collection.class);
		
		when(activations.add(any(Entry.class))).thenReturn(true);
		when(deactivations.add(any(Entry.class))).thenReturn(true);
		when(actions.add(any(Entry.class))).thenReturn(true);
		
		when(activations.iterator()).thenReturn(Collections.emptyIterator());
		when(deactivations.iterator()).thenReturn(Collections.emptyIterator());
		when(actions.iterator()).thenReturn(Collections.emptyIterator());
		
		Field activationsFiled = managerBuilder.getClass().getDeclaredField("activations");
		Field deactivationsFiled = managerBuilder.getClass().getDeclaredField("deactivations");
		Field actionsFiled = managerBuilder.getClass().getDeclaredField("actions");
		
		activationsFiled.setAccessible(true);
		deactivationsFiled.setAccessible(true);
		actionsFiled.setAccessible(true);

		activationsFiled.set(managerBuilder, activations);
		deactivationsFiled.set(managerBuilder, deactivations);
		actionsFiled.set(managerBuilder, actions);

		
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void addHourlyDayListeners() {

		managerBuilder.useThreads(2).performActionsAtEnd().addHourlyDayListeners(oids, choiceSet, DayOfWeek.MONDAY, context).build();

		Times times = VerificationModeFactory.times(choiceSet.size() * 24);

		verify(activations, times).add(activationCaptor.capture());
		verify(deactivations, times).add(deactivationCaptor.capture());
		verify(actions, times).add(actionCaptor.capture());
		
		Time time = Time.start;
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < choiceSet.size(); j++) {
				
				int index = i*choiceSet.size() + j;
				
				Time actual = activationCaptor.getAllValues().get(index).getKey();
				assertEquals(time.plusHours(i), actual);
				
				actual = deactivationCaptor.getAllValues().get(index).getKey();
				assertEquals(time.plusHours(i+1), actual);
				
				actual = actionCaptor.getAllValues().get(index).getKey();
				assertEquals(time.plusHours(i+1), actual);
			}
		}
		



	}

	@Test
	public void addHourlyWeekdayListeners() {

		managerBuilder.useThreads(2).performActionsAtEnd().addHourlyWeekdayListeners(oids, choiceSet, context).build();

		Times timesListener = VerificationModeFactory.times(choiceSet.size() * 24 * 5);
		Times timesAction = VerificationModeFactory.times(choiceSet.size() * 24);

		verify(activations, timesListener).add(activationCaptor.capture());
		verify(deactivations, timesListener).add(deactivationCaptor.capture());
		verify(actions, timesAction).add(actionCaptor.capture());
		
		Time time = Time.start;
		for (int j = 0; j < choiceSet.size(); j++) {
			for (int i = 0; i < 24; i++) {
				
				Time actual = null; 
				for (int k=0; k < 5; k++) {
					int index = j*24*5 + i*5 + k;
					actual = activationCaptor.getAllValues().get(index).getKey();
					assertEquals(time.plusDays(k).plusHours(i), actual);
				
					actual = deactivationCaptor.getAllValues().get(index).getKey();
					assertEquals(time.plusDays(k).plusHours(i+1), actual);
				}
				
				actual = actionCaptor.getAllValues().get(j*24 + i).getKey();
				assertEquals(time.plusDays(4).plusHours(i+1), actual);
				
			}
		}


	}

}
