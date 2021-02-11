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
import edu.kit.ifv.mobitopp.util.dataexport.ZipMatrixPrinterDecorator;

// TODO: Auto-generated Javadoc
/**
 * The Class ActiveListenersManagerBuilder is a fluent builder for
 * {@link ActiveListenersManager}.
 */
public class ActiveListenersManagerBuilder {

	private int threads = WrittenConfiguration.defaulThreadCount;
	private DemandSimulatorPassenger simulator = null;
	private boolean actionsAtStart = false;
	private final Collection<Entry<Time, PersonListener>> activations;
	private final Collection<Entry<Time, PersonListener>> deactivations;
	private final Collection<Entry<Time, Runnable>> actions;

	/**
	 * Instantiates a new {@link ActiveListenersManagerBuilder}.
	 */
	public ActiveListenersManagerBuilder() {
		this.activations = new ArrayList<>();
		this.deactivations = new ArrayList<>();
		this.actions = new ArrayList<>();
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

		AggregateDemandFactory factory = new AggregateDemandFactory(context,
			new ZipMatrixPrinterDecorator());

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
	 * @param day the day of week on which demand will be aggregated
	 * @param context   the simulation context
	 * @return this {@link ActiveListenersManagerBuilder}
	 */
	public ActiveListenersManagerBuilder addHourlyDayListeners(List<ZoneId> zoneIds,
		Set<Mode> choiceSet, DayOfWeek day, SimulationContext context) {

		AggregateDemandFactory factory = new AggregateDemandFactory(context,
			new ZipMatrixPrinterDecorator());

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

}
