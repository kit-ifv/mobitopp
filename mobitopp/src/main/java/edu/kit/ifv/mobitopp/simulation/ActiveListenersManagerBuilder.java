package edu.kit.ifv.mobitopp.simulation;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.dataexport.AbstractMatrixPrinter;
import edu.kit.ifv.mobitopp.util.dataexport.ZipMatrixPrinterDecorator;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class ActiveListenersManagerBuilder is a fluent builder for
 * {@link ActiveListenersManager}.
 */
@Slf4j
public class ActiveListenersManagerBuilder {

	private int threads;
	private DemandSimulatorPassenger simulator;
	private boolean actionsAtStart;
	private final Collection<Entry<Time, PersonListener>> activations;
	private final Collection<Entry<Time, PersonListener>> deactivations;
	private final Collection<Entry<Time, Runnable>> actions;
	private AbstractMatrixPrinter matrixPrinter;

	/**
	 * Instantiates a new {@link ActiveListenersManagerBuilder}.
	 */
	public ActiveListenersManagerBuilder() {
		this.activations = new ArrayList<>();
		this.deactivations = new ArrayList<>();
		this.actions = new ArrayList<>();

		this.threads = WrittenConfiguration.defaulThreadCount;
		this.simulator = null;
		this.actionsAtStart = false;
		this.matrixPrinter = new ZipMatrixPrinterDecorator();
	}

	/**
	 * Builds the {@link ActiveListenersManager} using the previously specified
	 * settings.
	 *
	 * @return an {@link ActiveListenersManager}
	 */
	public ActiveListenersManager build() {
		ActiveListenersManager manager = new ActiveListenersManager(threads, actionsAtStart);

		for (Entry<Time, PersonListener> e : activations) {
			manager.planActivation(e.getKey(), e.getValue());
		}

		for (Entry<Time, PersonListener> e : deactivations) {
			manager.planDeactivation(e.getKey(), e.getValue());
		}

		for (Entry<Time, Runnable> e : actions) {
			manager.planAction(e.getKey(), e.getValue());
		}

		if (simulator != null) {
			manager.register(simulator);
		}

		return manager;
	}

	/**
	 * Register the {@link ActiveListenersManager} to be built at the given
	 * {@link DemandSimulatorPassenger}.
	 *
	 * @param simulator the simulator to be used for registration
	 * @return this {@link ActiveListenersManagerBuilder}
	 */
	public ActiveListenersManagerBuilder register(DemandSimulatorPassenger simulator) {
		this.simulator = simulator;
		return this;
	}

	/**
	 * Specifies that the {@link ActiveListenersManager} to be built should perform
	 * actions at the beginning of a timestep.
	 *
	 * @return this {@link ActiveListenersManagerBuilder}
	 */
	public ActiveListenersManagerBuilder performActionsAtStart() {
		this.actionsAtStart = true;
		return this;
	}

	/**
	 * Specifies that the {@link ActiveListenersManager} to be built should perform
	 * actions at the end of a timestep.
	 *
	 * @return this {@link ActiveListenersManagerBuilder}
	 */
	public ActiveListenersManagerBuilder performActionsAtEnd() {
		this.actionsAtStart = false;
		return this;
	}

	/**
	 * Specifies that the {@link ActiveListenersManager} to be built should use the
	 * given number of threads.
	 *
	 * @param threadCount the number of threads to be used
	 * @return this {@link ActiveListenersManagerBuilder}
	 */
	public ActiveListenersManagerBuilder useThreads(int threadCount) {
		this.threads = threadCount;
		return this;
	}

	/**
	 * Specifies that the following (matrix output) added to the schedule use the
	 * given {@link AbstractMatrixPrinter}.
	 *
	 * @param matrixPrinter the {@link AbstractMatrixPrinter} to be used for the
	 *                      next actions
	 * @return this {@link ActiveListenersManagerBuilder}
	 */
	public ActiveListenersManagerBuilder useMatrixPrinter(AbstractMatrixPrinter matrixPrinter) {
		this.matrixPrinter = matrixPrinter;
		return this;
	}

	/**
	 * Adds {@link AggregateDemandOfStartedTrips listeners} to the
	 * {@link ActiveListenersManager} to be built that are active during the week
	 * for every mode in the given choice set and every hour of the day.
	 *
	 * @param zoneIds   the zone ids for which the demand will be aggregated
	 * @param choiceSet the set of {@link Mode modes} for which listeners will be
	 *                  created
	 * @param context   the {@link SimulationContext}
	 * @return this {@link ActiveListenersManagerBuilder}
	 */
	public ActiveListenersManagerBuilder addHourlyWeekdayListeners(List<ZoneId> zoneIds,
		Set<Mode> choiceSet, SimulationContext context) {

		AggregateDemandFactory factory = new AggregateDemandFactory(context, this.matrixPrinter);

		for (Mode mode : choiceSet) {
			for (int hour = 0; hour < 24; hour++) {
				AggregateDemandOfStartedTrips agg = factory
					.newWeekdayListener(zoneIds, mode, hour, "weekday");
				actions.add(pair(Time.start.plusDays(4).plusHours(hour + 1), agg::writeMatrix));

				for (int day = 0; day < 5; day++) {

					Time start = Time.start.plusDays(day).plusHours(hour);
					Time stop = start.plusHours(1);

					activations.add(pair(start, agg));
					deactivations.add(pair(stop, agg));

				}
			}
		}

		return this;

	}

	/**
	 * Adds {@link AggregateDemandOfStartedTrips listeners} to the
	 * {@link ActiveListenersManager} to be built that are active during the given
	 * day of week for every mode in the given choice set and every hour of the day.
	 *
	 * @param zoneIds   the zone ids for which the demand will be aggregated
	 * @param choiceSet the set of modes for which listeners will be created
	 * @param day       the day of week on which demand will be aggregated
	 * @param context   the simulation context
	 * @return this {@link ActiveListenersManagerBuilder}
	 */
	public ActiveListenersManagerBuilder addHourlyDayListeners(List<ZoneId> zoneIds,
		Set<Mode> choiceSet, DayOfWeek day, SimulationContext context) {

		AggregateDemandFactory factory = new AggregateDemandFactory(context, this.matrixPrinter);

		for (int hour = 0; hour < 24; hour++) {
			Time start = Time.start.plusDays(day.getTypeAsInt()).plusHours(hour);
			Time stop = start.plusHours(1);

			for (Mode mode : choiceSet) {
				AggregateDemandOfStartedTrips agg = factory
					.newDayListener(zoneIds, mode, hour, day);

				activations.add(pair(start, agg));
				deactivations.add(pair(stop, agg));
				actions.add(pair(stop, agg::writeMatrix));
			}
		}

		return this;
	}

	/**
	 * Adds {@link AggregateDemandOfStartedTrips listeners} to the
	 * {@link ActiveListenersManager} to be built that are active during a time
	 * slice between the given hours on the given days for every mode in the given
	 * choice.
	 * 
	 * The upper bound timeTo is exclusive. Listeners are generated for all
	 * consecutive time slices that fit in [timeFrom, timeTo].
	 *
	 * @param zoneIds   the zone ids for which the demand will be aggregated
	 * @param choiceSet the set of modes for which listeners will be created
	 * @param days      the days of week on which demand will be aggregated
	 * @param context   the context
	 * @param hourFrom  the hour from which on demand will be aggregated (inclusive)
	 * @param hourTo    the hour until which on demand will be aggregated
	 *                  (exclusive)
	 * @param timeSlice the duration of a time slice during which demand will be
	 *                  aggregated
	 * @return this {@link ActiveListenersManagerBuilder}
	 */
	public ActiveListenersManagerBuilder addTimeSlicesBetween(List<ZoneId> zoneIds,
		Set<Mode> choiceSet, Collection<DayOfWeek> days, SimulationContext context, int hourFrom,
		int hourTo, int timeSlice) {

		if ((hourTo - hourFrom) % timeSlice != 0) {
			log	.warn("The given interval [{},{})"
				+ " is not devisible by {} hour time slices. "
				+ "The remaining {} hours are not covered when aggregating demand!",
				hourFrom, hourTo, timeSlice, (hourTo - hourFrom) % timeSlice);
		}

		AggregateDemandFactory factory = new AggregateDemandFactory(context, this.matrixPrinter);

		int lastDay = days.stream().mapToInt(DayOfWeek::getTypeAsInt).max().orElseGet(() -> 6);

		for (Mode mode : choiceSet) {
			for (int hour = hourFrom; hour + timeSlice <= hourTo; hour += timeSlice) {

				AggregateDemandOfStartedTrips agg = factory
					.newListener(zoneIds, mode, hour, hour + timeSlice, days);

				Time startWriting = Time.start.plusDays(lastDay).plusHours(hour + timeSlice);
				actions.add(pair(startWriting, agg::writeMatrix));

				for (DayOfWeek day : days) {
					Time start = Time.start.plusDays(day.getTypeAsInt()).plusHours(hour);
					Time stop = start.plusHours(timeSlice);

					activations.add(pair(start, agg));
					deactivations.add(pair(stop, agg));
				}

			}
		}

		return this;
	}

	/**
	 * Returns a {@link SimpleEntry} containing the given key and value.
	 *
	 * @param <K>   the key type
	 * @param <V>   the value type
	 * @param key   the key
	 * @param value the value
	 * @return a {@link SimpleEntry} containing the given pair of key and value
	 */
	private static <K, V> SimpleEntry<K, V> pair(K key, V value) {
		return new SimpleEntry<K, V>(key, value);
	}

	/**
	 * Log the planned schedule. Log the activation, deactivation and action times.
	 *
	 * @return this {@link ActiveListenersManagerBuilder}
	 */
	public ActiveListenersManagerBuilder logPlannedSchedule() {
		Time time = Time.start;
		Time end = Time.start.plusDays(7);

		while (time.isBeforeOrEqualTo(end)) {
			final Time current = time;

			long activate = this.activations
				.stream()
				.filter(e -> e.getKey().equals(current))
				.count();
			long deactivate = this.deactivations
				.stream()
				.filter(e -> e.getKey().equals(current))
				.count();
			long perform = this.actions.stream().filter(e -> e.getKey().equals(current)).count();

			if (deactivate > 0) {
				log.info("[{}] -> deactivate: {}", current.toString(), deactivate);
			}
			if (perform > 0) {
				log.info("[{}] -> perform actions: {}", current.toString(), perform);
			}
			if (activate > 0) {
				log.info("[{}] -> activate: {}", current.toString(), activate);
			}

			time = time.plusHours(1);
		}

		return this;
	}

}
