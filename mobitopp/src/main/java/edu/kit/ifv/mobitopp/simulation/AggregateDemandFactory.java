package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.IntegerMatrix;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.simulation.person.StartedTrip;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.dataexport.AbstractMatrixPrinter;
import edu.kit.ifv.mobitopp.util.dataexport.MatrixPrinter;
import edu.kit.ifv.mobitopp.util.dataexport.ZipMatrixPrinterDecorator;

/**
 * A factory for creating {@link AggregateDemand} objects.
 */
public class AggregateDemandFactory {

	/** The printer. */
	private AbstractMatrixPrinter printer;

	/** The context. */
	private SimulationContext context;

	/**
	 * Instantiates a new {@link AggregateDemandFactory} with the given
	 * {@link MatrixPrinter} and SimulationContext.
	 *
	 * @param context the context
	 * @param printer the printer
	 */
	public AggregateDemandFactory(SimulationContext context, AbstractMatrixPrinter printer) {
		this.printer = printer;
		this.context = context;
	}

	/**
	 * Instantiates a new {@link AggregateDemandFactory} with the given
	 * {@link SimulationContext} and a {@link ZipMatrixPrinterDecorator} for
	 * printing matrices.
	 *
	 * @param context the context
	 */
	public AggregateDemandFactory(SimulationContext context) {
		this.printer = new ZipMatrixPrinterDecorator();
		this.context = context;
	}

	/**
	 * Creates a new weekday listener which will will listen for results on weekdays
	 * during the given hour, for the given {@link Mode} and zoneIds.
	 *
	 * @param ids    the {@link ZoneId zone ids} for which demand will be aggregated
	 * @param mode   the {@link Mode mode} for which demand will be aggregated
	 * @param hour   the hour during which demand will be aggregated
	 * @param suffix the suffix appended to the matrix file name
	 * @return the aggregate demand person listener
	 */
	public AggregateDemandOfStartedTrips newWeekdayListener(List<ZoneId> ids, Mode mode, int hour,
		String suffix) {

		return new AggregateDemandOfStartedTrips(ids, filterBy(mode, hour).and(weekdayFilter()),
			createMatrixWriter(mode, hour, hour + 1, suffix), scaleFactor());
	}

	/**
	 * Creates a new day listener which will listen for results on a specific day
	 * during the given hour, for the given {@link Mode} and zoneIds.
	 *
	 * @param ids       the {@link ZoneId zone ids} for which demand will be
	 *                  aggregated
	 * @param mode      the {@link Mode mode} for which demand will be aggregated
	 * @param hour      the hour during which demand will be aggregated
	 * @param dayOfWeek the {@link DayOfWeek day of week} on which demand will be
	 *                  aggregated
	 * @return the aggregate demand person listener
	 */
	public AggregateDemandOfStartedTrips newDayListener(List<ZoneId> ids, Mode mode, int hour,
		DayOfWeek dayOfWeek) {

		return new AggregateDemandOfStartedTrips(ids,
			filterBy(mode, hour).and(dayOfWeekFilter(dayOfWeek)),
			createMatrixWriter(mode, hour, hour + 1, dayOfWeek.name().toLowerCase()),
			scaleFactor());
	}

	/**
	 * Creates a new listener which will listen for results in the given time slice
	 * [timeFrom, timeTo] on the given set of days, for the given {@link Mode} and
	 * zoneIds.
	 *
	 * @param ids       the {@link ZoneId zone ids} for which demand will be
	 *                  aggregated
	 * @param mode      the {@link Mode mode} for which demand will be aggregated
	 * @param hourFrom the hour from which demand will be aggregated (inclusive)
	 * @param hourTo   the hour until which demand will be aggregated (exclusive)
	 * @param days     the {@link DayOfWeek days of week} on which demand will be
	 *                  aggregated
	 * @return the aggregate demand of started trips
	 */
	public AggregateDemandOfStartedTrips newListener(List<ZoneId> ids, Mode mode, int hourFrom,
		int hourTo, Collection<DayOfWeek> days) {
		String suffix = days
			.stream()
			.map(DayOfWeek::name)
			.map(s -> s.substring(0, 2))
			.collect(Collectors.joining("-"));

		return new AggregateDemandOfStartedTrips(ids,
			filterBy(mode, hourFrom, hourTo).and(dayOfWeekFilter(days)),
			createMatrixWriter(mode, hourFrom, hourTo, suffix), scaleFactor());
	}

	/**
	 * Create a BiPredicate to filter {@link StartedTrip started trips} by
	 * {@link Mode} and hour.
	 *
	 * @param mode the {@link Mode mode}
	 * @param hour the hour
	 * @return the bi predicate
	 */
	private BiPredicate<Person, StartedTrip<?>> filterBy(Mode mode, int hour) {
		return modeFilter(mode).and(hourFilter(hour));
	}

	
	/**
	 * Create a BiPredicate to filter {@link StartedTrip started trips} by
	 * {@link Mode} and time slice [hourFrom, hourTo].
	 *
	 * @param mode the {@link Mode mode}
	 * @param hourFrom lower bound of the time slice (inclusive)
	 * @param hourTo upper bound of the time slice (exclusive)
	 * @return the bi predicate
	 */
	private BiPredicate<Person, StartedTrip<?>> filterBy(Mode mode, int hourFrom, int hourTo) {
		return modeFilter(mode).and(hourFilter(hourFrom, hourTo));
	}

	/**
	 * Creates a new matrix consumer to print matrices to files.
	 *
	 * @param mode   the {@link Mode mode} for which demand was aggregated
	 * @param from   the {@link Time time} from where on demand was aggregated
	 * @param to     the {@link Time time} until which demand was aggregated
	 * @param suffix the suffix appended to the matrix file name
	 * @return a matrix consumer for printing matrices to files
	 */
	private Consumer<IntegerMatrix> createMatrixWriter(Mode mode, int from, int to, String suffix) {
		String modeName = mode.toString();

		File resultFolder = Convert.asFile(context.configuration().getResultFolder());
		File matrixFolder = new File(resultFolder, "matrices");
		String fileName = modeName + "_" + from + "_" + to + "_" + suffix + "_demand.mtx";
		File toOutputFile = new File(matrixFolder, fileName);

		return matrix -> {
			printer
				.writeMatrixToFile(matrix, Integer.toString(from), Integer.toString(to),
					toOutputFile);
		};
	}

	/**
	 * Gets the scale factor by which matrix entries have to be multiplied. It can
	 * be derived from the fraction of the population that is being simulated as
	 * follows: 1/fractionOfPopulation.
	 *
	 * @return the scale factor
	 */
	private int scaleFactor() {
		return (int) Math.round(1.0d / context.fractionOfPopulation());
	}

	/**
	 * Creates a Day-of-week filter for filtering trips taking place on the given
	 * day.
	 *
	 * @param dayType the {@link DayOfWeek day of the week} to be filtered
	 * @return the filter
	 */
	public static BiPredicate<Person, StartedTrip<?>> dayOfWeekFilter(DayOfWeek dayType) {
		return (person, trip) -> trip.startDate().weekDay().equals(dayType);
	}

	/**
	 * Creates a Day-of-week filter for filtering trips taking place on one of the given
	 * days.
	 *
	 * @param dayTypes the {@link DayOfWeek days of the week} to be filtered
	 * @return the filter
	 */
	public static BiPredicate<Person, StartedTrip<?>> dayOfWeekFilter(
		Collection<DayOfWeek> dayTypes) {
		return (person, trip) -> dayTypes.contains(trip.startDate().weekDay());
	}

	/**
	 * Creates a weekend filter for filtering trips taking place on the weekend.
	 *
	 * @return the filter
	 */
	public static BiPredicate<Person, StartedTrip<?>> weekendFilter() {
		return dayOfWeekFilter(DayOfWeek.SATURDAY).or(dayOfWeekFilter(DayOfWeek.SUNDAY));
	}

	/**
	 * Creates a weekday filter for filtering trips taking place on a weekday (not
	 * weekend day).
	 *
	 * @return the filter
	 */
	public static BiPredicate<Person, StartedTrip<?>> weekdayFilter() {
		return dayOfWeekFilter(DayOfWeek.MONDAY)
			.or(dayOfWeekFilter(DayOfWeek.TUESDAY))
			.or(dayOfWeekFilter(DayOfWeek.WEDNESDAY))
			.or(dayOfWeekFilter(DayOfWeek.THURSDAY))
			.or(dayOfWeekFilter(DayOfWeek.FRIDAY));
	}

	/**
	 * Creates an hour filter for filtering trips taking place at the given hour.
	 *
	 * @param hour the hour to be filtered
	 * @return the filter
	 */
	public static BiPredicate<Person, StartedTrip<?>> hourFilter(int hour) {
		return (person, trip) -> trip.startDate().getHour() == hour;
	}

	/**
	 * Creates an hour filter for filtering trips taking place in the give time slice [hourFrom, hourTo].
	 *
	 * @param hourFrom the lower bound of the time slice (inclusive)
	 * @param hourTo the upper bound of the time slice (exclusive)
	 * @return the filter
	 */
	public static BiPredicate<Person, StartedTrip<?>> hourFilter(int hourFrom, int hourTo) {
		return (person, trip) -> {
			int hour = trip.startDate().getHour();
			return hourFrom <= hour && hour < hourTo;
		};
	}

	/**
	 * Creates a mode filter for filtering trips using the given mode.
	 *
	 * @param mode the mode to be filtered
	 * @return the filter
	 */
	public static BiPredicate<Person, StartedTrip<?>> modeFilter(Mode mode) {
		return (person, trip) -> trip.mode().equals(mode);
	}

}