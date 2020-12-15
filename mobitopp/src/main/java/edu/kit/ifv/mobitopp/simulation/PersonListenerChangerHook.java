package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class PersonListenerChangerHook is a {@link Hook} that can change {@link PersonListener PersonListeners}
 * at specific change times throughout the simulation.
 */
public class PersonListenerChangerHook implements Hook {

	private static ExecutorService executorService;

	private Consumer<PersonListener> hookConsumer;
	private Map<Time, Collection<PersonListener>> changeTimes;
	private Map<PersonListener, Runnable> actions;

	/**
	 * Instantiates a new person listener changer hook with the given hookConsumer
	 * which will be executed for every registered {@link PersonListener} once their change
	 * time is reached.
	 * 
	 * @param hookConsumer the hook consumer
	 */
	public PersonListenerChangerHook(Consumer<PersonListener> hookConsumer) {
		this.hookConsumer = hookConsumer;
		this.changeTimes = new LinkedHashMap<Time, Collection<PersonListener>>();
		this.actions = new LinkedHashMap<PersonListener, Runnable>();
	}

	/**
	 * Instantiates a new
	 * {@link PersonListenerChangerHook#PersonListenerChangerHook(Consumer) PersonListenerChangerHook}.
	 * Additionally initializes the executor service with the amount of threads
	 * specified in the given context's configuration.
	 * 
	 * @param context      the context
	 * @param hookConsumer the hook consumer
	 */
	public PersonListenerChangerHook(SimulationContext context,
			Consumer<PersonListener> hookConsumer) {
		this(hookConsumer);

		createExecutor(context.configuration().getThreadCount());
	}

	/**
	 * Adds the given person listener at the given change time with the given
	 * action.
	 *
	 * @param time     the change time at which the given person listener should to
	 *                 be updated
	 * @param listener the person listener to be changed
	 * @param action   the action to be performed after the person listener was
	 *                 changed
	 */
	public void add(Time time, PersonListener listener, Runnable action) {
		if (changeTimes.keySet().contains(time)) {
			changeTimes.get(time).add(listener);

		} else {
			ArrayList<PersonListener> list = new ArrayList<PersonListener>(Arrays.asList(listener));
			changeTimes.put(time, list);
		}

		if (action != null) {
			actions.put(listener, action);
		}

	}

	/**
	 * Processes all registered person listeners. All listeners with change times
	 * that predate the given time are processed by the general hook consumer.
	 * Additionally their specific actions are executed (in a separate thread). Every person listener that
	 * has been changed is then removed from this hook.
	 *
	 * @param date the current time
	 */
	@Override
	public void process(Time date) {
		ArrayList<Time> processed = new ArrayList<Time>();

		for (Time t : changeTimes.keySet()) {

			if (date.isAfterOrEqualTo(t)) {
				System.out.println("Process ResultListeners: ");

				for (PersonListener listener : changeTimes.get(t)) {
					// Execute global action (same for all listeners)
					hookConsumer.accept(listener);

					if (actions.containsKey(listener)) {
						// Submit person listener specific action as job to executor service
						getExecutor().submit(actions.get(listener));
					}

				}

				processed.add(t);
			}
		}

		for (Time t : processed) {
			changeTimes.remove(t);
		}

	}

	/**
	 * Shuts down the executor service by waiting the termination of ongoing jobs.
	 * Timeout waiting after 10 hours.
	 */
	public static void shutDown() {
		try {
			executorService.awaitTermination(10, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the service executor. If it has not been created yet, creates a new one
	 * with the default thread count.
	 *
	 * @return the executor service
	 */
	private static ExecutorService getExecutor() {
		if (executorService != null) {
			int threads = WrittenConfiguration.defaulThreadCount;
			return createExecutor(threads);
		}

		return executorService;
	}

	/**
	 * Creates a new fixed thread pool with the given amount of threads.
	 *
	 * @param threads the amount of threads to be used
	 * @return the executor service
	 */
	private static ExecutorService createExecutor(int threads) {
		if (executorService != null) {
			executorService = Executors.newFixedThreadPool(threads);
		}

		return executorService;
	}

}
