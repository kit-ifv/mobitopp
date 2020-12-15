package edu.kit.ifv.mobitopp.simulation;

import java.util.List;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.dataexport.ZipMatrixPrinterDecorator;

/**
 * The Class DemandResultListenerRegistry provides two
 * {@link PersonListenerChangerHook Hooks} for aktivating {@link AggregateDemand
 * demand listeners} for a specific time period and writing the result matrices
 * after they are deactivated.
 */
public class DemandResultListenerRegistry {

	private PersonListenerChangerHook registerHook;
	private PersonListenerChangerHook unregisterHook;

	/**
	 * Instantiates a new DemandResultListenerRegistry and registers its
	 * {@link PersonListenerChangerHook Hooks} in the given simulator.
	 *
	 * @param simulator the simulator
	 */
	public DemandResultListenerRegistry(DemandSimulatorPassenger simulator) {
		PersonResults results = simulator.context().personResults();

		registerHook = createRegisterHook(results);
		unregisterHook = createUnregisterHook(results);
		
		simulator.addBeforeTimeSliceHook(registerHook);
		simulator.addAfterTimeSliceHook(unregisterHook);
	}

	/**
	 * Adds {@link AggregateDemand listeners} that are active during the week for
	 * every mode in the given choice set and every hour of the day.
	 *
	 * @param zoneIds   the zone ids for which the demand will be aggregated
	 * @param choiceSet the set of modes for which listeners will be created
	 * @param context   the simulatuion context
	 */
	public void addHourlyWeekdayListeners(List<ZoneId> zoneIds, Set<Mode> choiceSet,
			SimulationContext context) {

		AggregateDemandFactory factory = new AggregateDemandFactory(context,
				new ZipMatrixPrinterDecorator());
		Time stop = SimpleTime.ofDays(5).plusHours(1);

		for (Mode mode : choiceSet) {
			for (int hour = 0; hour < 24; hour++) {
				AggregateDemandOfStartedTrips ad = factory.newWeekdayListener(zoneIds, mode, hour, "weekday");

				registerHook.add(Time.start, ad);

				unregisterHook.add(stop, ad, () -> ad.writeMatrix());
			}
		}

	}

	/**
	 * Adds {@link AggregateDemand listeners} that are active during the given day
	 * of week for every mode in the given choice set and every hour of the day.
	 *
	 * @param zoneIds   the zone ids for which the demand will be aggregated
	 * @param choiceSet the set of modes for which listeners will be created
	 * @param dayOfWeek the day of week on which demand will be aggregated
	 * @param context   the simulation context
	 */
	public void addHourlyDayListeners(List<ZoneId> zoneIds, Set<Mode> choiceSet,
			DayOfWeek dayOfWeek, SimulationContext context) {

		AggregateDemandFactory factory = new AggregateDemandFactory(context,
				new ZipMatrixPrinterDecorator());

		Time day = Time.start.plusDays(dayOfWeek.getTypeAsInt());

		for (Mode mode : choiceSet) {
			for (int hour = 0; hour < 24; hour++) {
				AggregateDemandOfStartedTrips ad = factory.newDayListener(zoneIds, mode, hour, dayOfWeek);

				registerHook.add(day.plusHours(hour), ad);

				Time stop = day.plusHours(hour + 1);
				unregisterHook.add(stop, ad, () -> ad.writeMatrix());
			}
		}

	}
	
	public void addCompleteDayListeners(List<ZoneId> zoneIds, Set<Mode> choiceSet,
			DayOfWeek dayOfWeek, SimulationContext context) {

		AggregateDemandFactory factory = new AggregateDemandFactory(context,
				new ZipMatrixPrinterDecorator());

		Time day = Time.start.plusDays(dayOfWeek.getTypeAsInt());

		for (Mode mode : choiceSet) {
			AggregateDemandOfStartedTrips ad = factory.newDayListener(zoneIds, mode, 0, dayOfWeek);

			// Register listener one hour too early just to be safe
			registerHook.add(day, ad, null);

			Time stop = day.nextDay();

			// Unregister listener one hour too late just to be safe
			unregisterHook.add(stop, ad, () -> ad.writeMatrix());
		}

	}

	/**
	 * Adds the given {@link PersonListener listener} which is active between the
	 * given time from and to. The given start and stop action are executed when the
	 * listener is activated resp. deactivated.
	 *
	 * @param from        the time from which on the listener should be active
	 * @param to          the to until which the listener should be active
	 * @param listener    the person listener
	 * @param startAction the start action executed when the listener is activated
	 * @param stopAction  the stop action executed when the listener is deactivated
	 * @param context     the simulation context
	 */
	public void addListenerBetween(Time from, Time to, PersonListener listener,
			Runnable startAction, Runnable stopAction, SimulationContext context) {
		registerHook.add(from, listener, startAction);
		unregisterHook.add(to, listener, stopAction);
	}


	/**
	 * Creates the register hook.
	 * Adding listeners to the given {@link PersonResults person results} is the global hook action.
	 *
	 * @param personResults the person results
	 * @return the person listener changer hook
	 */
	private static PersonListenerChangerHook createRegisterHook(PersonResults personResults) {
		return new PersonListenerChangerHook(listener -> personResults.addListener(listener));
	}

	/**
	 * Creates the unregister hook.
	 * Removing listeners from the given {@link PersonResults person results} is the global hook action.
	 *
	 * @param personResults the person results
	 * @return the person listener changer hook
	 */
	private static PersonListenerChangerHook createUnregisterHook(PersonResults personResults) {
		return new PersonListenerChangerHook(listener -> personResults.removeListener(listener));
	}

}
