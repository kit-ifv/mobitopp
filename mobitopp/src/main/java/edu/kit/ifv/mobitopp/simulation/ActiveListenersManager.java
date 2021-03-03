package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.StartedTrip;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class ActiveListenersManager is a {@link PersonListener} 
 *  managing a set of other PersonListeners and delegating the
 *   calls to its currently active Listeners.
 */
@Slf4j
public class ActiveListenersManager implements PersonListener {

	private final ExecutorService executorService;
	private final Hook activateHook;
	private final Hook deactivateHook;
	private final Set<PersonListener> activeListeners;
	private final Map<Time, Collection<PersonListener>> startTimes;
	private final Map<Time, Collection<PersonListener>> stopTimes;
	private final Map<Time, Collection<Runnable>> actionTimes;
	private boolean actionsAtStart = false;

	/**
	 * Instantiates a new {@link ActiveListenersManager} with the default
	 * threadCount specified in {@link WrittenConfiguration}. Furthermore it will
	 * execute registered actions at the end of the timestep.
	 */
	public ActiveListenersManager() {
		this(WrittenConfiguration.defaulThreadCount, false);
	}

	/**
	 * Instantiates a new {@link ActiveListenersManager} with the given number of
	 * threads. Furthermore it will execute registered actions at the end of the
	 * timestep.
	 *
	 * @param threadCount the thread count
	 */
	public ActiveListenersManager(int threadCount) {
		this(threadCount, false);
	}

	/**
	 * Instantiates a new {@link ActiveListenersManager} with the given number of
	 * threads. Furthermore it will use the specified execution time for registered
	 * actions. For actionsAtStart == true, actions will be executed at the
	 * beginning of the timestep.
	 *
	 * @param threadCount    the amount of threads to be used
	 * @param actionsAtStart whether actions should be performed at the beginning of
	 *                       a time step
	 */
	public ActiveListenersManager(int threadCount, boolean actionsAtStart) {
		this.startTimes = new LinkedHashMap<>();
		this.stopTimes = new LinkedHashMap<>();
		this.activeListeners = new LinkedHashSet<>();
		
		this.actionTimes = new LinkedHashMap<>();
		this.actionsAtStart = actionsAtStart;
		
		this.activateHook = createActivationHook();
		this.deactivateHook = createDeactivationHook();
		this.executorService = Executors.newFixedThreadPool(threadCount);
	}

	/**
	 * Registers two {@link Hook hooks} at the given
	 * {@link DemandSimulatorPassenger}. One before and one after each time slice.
	 * Additionally registers this {@link ActiveListenersManager} as
	 * {@link PersonListener} at the given simulators {@link PersonResults}.
	 *
	 * @param simulator the simulator
	 */
	public void register(DemandSimulatorPassenger simulator) {
		simulator.addBeforeTimeSliceHook(activateHook);
		simulator.addAfterTimeSliceHook(deactivateHook);
		simulator.context().personResults().addListener(this);
	}

	/**
	 * Add the activation of the given {@link PersonListener} at the given
	 * {@link Time} to the activation plan.
	 *
	 * @param time     the time the given listener should be activated
	 * @param listener the listener to be activated
	 */
	public void planActivation(Time time, PersonListener listener) {
		addTo(startTimes, time, listener);
	}

	/**
	 * Add the deactivation of the given {@link PersonListener} at the given
	 * {@link Time} to the deactivation plan.
	 *
	 * @param time     the time the given listener should be deactivated
	 * @param listener the listener to be deactivated
	 */
	public void planDeactivation(Time time, PersonListener listener) {
		addTo(stopTimes, time, listener);
	}

	/**
	 * Add the execution of the given {@link Runnable action} at the given
	 * {@link Time} to the execution plan.
	 *
	 * @param time   the time the given action should be executed
	 * @param action the action to be executed
	 */
	public void planAction(Time time, Runnable action) {
		addTo(actionTimes, time, action);
	}

	/**
	 * Adds the given object to the given map using the given time as key. If the
	 * map already contains the given time, add the object to the list. Otherwise
	 * put a new list containing merely the given object into the map.
	 * 
	 * @param <T>    the generic type
	 * @param map    the map
	 * @param time   the time to be used as key
	 * @param object the object to be inserted
	 */
	private static <T> void addTo(Map<Time, Collection<T>> map, Time time, T object) {
		if (map.keySet().contains(time)) {
			map.get(time).add(object);

		} else {
			ArrayList<T> list = new ArrayList<T>();
			list.add(object);
			map.put(time, list);
		}
	}

	/**
	 * Process the activation of all {@link PersonListener} registered for
	 * activation at the given timestep.
	 *
	 * @param date the date
	 */
	private void processActivation(Time date) {
		process(startTimes, date, activeListeners::add);
	}

	/**
	 * Process the deactivation of all {@link PersonListener} registered for
	 * dactivation at the given timestep.
	 *
	 * @param date the date
	 */
	private void processDeactivation(Time date) {
		process(stopTimes, date, activeListeners::remove);
	}

	/**
	 * Execute all {@link Runnable actions} registered for execution at the given
	 * timestep.
	 *
	 * @param date the date
	 */
	private void processActions(Time date) {
		process(actionTimes, date, executorService::submit);
	}

	/**
	 * Process the given action for all elements contained in the map at the given
	 * timestep.
	 *
	 * @param <T>    the generic type
	 * @param map    the map
	 * @param time   the time
	 * @param action the action to be performed
	 */
	private static <T> void process(Map<Time, Collection<T>> map, Time time, Consumer<T> action) {
		if (map.containsKey(time)) {
			
			for (T object : map.get(time)) {
				action.accept(object);
			}

		}

		map.remove(time);
	}

	/**
	 * Shuts down the {@link ExecutorService} and blocks until termination. Timeout
	 * waiting after 10 hours. In this time, the remaining actions can be processed.
	 */
	private void shutDown() {
		if (executorService != null) {
			try {
				executorService.shutdown();
				log.info("Await termination of {}", executorService);
				
				if (executorService != null) {
					executorService.awaitTermination(10, TimeUnit.HOURS);
				}
				log.info("Terminated execution service");
				
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}

	}

	/**
	 * Creates the deactivation hook. When it is triggered it processes the
	 * deactivation at the current timestep. If actions are performed at the end of
	 * each timestep, the execution of actions is triggered after the deactivation.
	 *
	 * @return the deactivation hook
	 */
	private Hook createDeactivationHook() {
		return new Hook() {

			@Override
			public void process(Time date) {
				processDeactivation(date);

				if (!hasActionsAtStart()) {
					processActions(date);
				}

			}

		};
	}

	/**
	 * Creates the activation hook. When it is triggered it processes the activation
	 * at the current timestep. If actions are performed at the beginning of each
	 * timestep, the execution of actions is triggered after the activation.
	 *
	 * @return the activation hook
	 */
	private Hook createActivationHook() {
		return new Hook() {

			@Override
			public void process(Time date) {
				processActivation(date);

				if (hasActionsAtStart()) {
					processActions(date);
				}
			}

		};
	}

	/**
	 * Checks whether actions should be performed at the beginning of each time
	 * step.
	 *
	 * @return true, if actions should be performed at the beginning of each time
	 *         step
	 */
	private boolean hasActionsAtStart() {
		return this.actionsAtStart;
	}

	/**
	 * Notify all active listeners.
	 * 
	 * Perform the given action on all currently active {@link PersonListener listeners}.
	 *
	 * @param action the action to be performed
	 */
	private void notifyActiveListener(Consumer<PersonListener> action) {
		activeListeners.stream().forEach(action);
	}


	@Override
	public void notifyEndTrip(Person person, FinishedTrip trip) {
		notifyActiveListener(l -> l.notifyEndTrip(person, trip));
	}

	@Override
	public void notifyStartTrip(Person person, StartedTrip trip) {
		notifyActiveListener(l -> l.notifyStartTrip(person, trip));
	}

	@Override
	public void notifyFinishCarTrip(Person person, Car car, FinishedTrip trip,
		ActivityIfc activity) {
		notifyActiveListener(l -> l.notifyFinishCarTrip(person, car, trip, activity));
	}

	@Override
	public void notifyStartActivity(Person person, ActivityIfc activity) {
		notifyActiveListener(l -> l.notifyStartActivity(person, activity));
	}

	@Override
	public void notifySelectCarRoute(Person person, Car car, TripData trip, Path route) {
		notifyActiveListener(l -> l.notifySelectCarRoute(person, car, trip, route));
	}

	@Override
	public void writeSubtourinfoToFile(Person person, Tour tour, Subtour subtour, Mode tourMode) {
		notifyActiveListener(l -> l.writeSubtourinfoToFile(person, tour, subtour, tourMode));
	}

	@Override
	public void writeTourinfoToFile(Person person, Tour tour, Zone tourDestination, Mode tourMode) {
		notifyActiveListener(l -> l.writeTourinfoToFile(person, tour, tourDestination, tourMode));
	}

	@Override
	public void notifyStateChanged(StateChange stateChange) {
		notifyActiveListener(l -> l.notifyStateChanged(stateChange));
	}

	/**
	 * Notify finish simulation.
	 * 
	 * Notify all active listeners about the end of the simulation.
	 * Then execute all remaining {@link Runnable actions} and shutdown the {@link ExecutorService}.
	 */
	@Override
	public void notifyFinishSimulation() {
		notifyActiveListener(l -> l.notifyFinishSimulation());

		List<Time> timeLeft = new ArrayList<Time>(actionTimes.keySet());
		timeLeft.forEach(this::processActions);

		shutDown();
	}

}
