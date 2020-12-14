package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.IntegerMatrix;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.simulation.person.StartedTrip;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.util.dataexport.AbstractMatrixPrinter;
import edu.kit.ifv.mobitopp.util.dataexport.ZipMatrixPrinterDecorator;

/**
 * A factory for creating AggregateDemand objects.
 */
public class AggregateDemandFactory {

	private AbstractMatrixPrinter printer;
	private SimulationContext context;

	/**
	 * Instantiates a new aggregate demand factory with the given MatrixPrinter and
	 * SimulationContext
	 *
	 * @param context the context
	 * @param printer the printer
	 */
	public AggregateDemandFactory(SimulationContext context, AbstractMatrixPrinter printer) {
		this.printer = printer;
		this.context = context;
	}

	/**
	 * Instantiates a new aggregate demand factory with the given SimulationContext
	 * and a ZipMatrixPrinter for printing matrices.
	 *
	 * @param context the context
	 * @param printer the printer
	 */
	public AggregateDemandFactory(SimulationContext context) {
		this.printer = new ZipMatrixPrinterDecorator();
		this.context = context;
	}

	/**
	 * Creates a new weekday listener which will will listen for results on weekdays
	 * during the given hour, for the given mode and zoneIds.
	 *
	 * @param ids    the zone ids for which demand will be aggregated
	 * @param mode   the mode for which demand will be aggregated
	 * @param hour   the hour during which demand will be aggregated
	 * @param suffix the suffix appended to the matrix file name
	 * @return the aggregate demand person listener
	 */
	public AggregateDemandOfStartedTrips newWeekdayListener(List<ZoneId> ids, Mode mode, int hour,
			String suffix) {

		return new AggregateDemandOfStartedTrips(ids, filterBy(mode, hour).and(weekdayFilter()),
				createMatrixWriter(mode, hour, hour, suffix), scaleFactor());
	}

	/**
	 * Creates a new day listener which will listen for results on a specific day
	 * during the given hour, for the given mode and zoneIds.
	 *
	 * @param ids       the zone ids for which demand will be aggregated
	 * @param mode      the mode for which demand will be aggregated
	 * @param hour      the hour during which demand will be aggregated
	 * @param dayOfWeek the day of week on which demand will be aggregated
	 * @return the aggregate demand person listener
	 */
	public AggregateDemandOfStartedTrips newDayListener(List<ZoneId> ids, Mode mode, int hour,
			DayOfWeek dayOfWeek) {

		return new AggregateDemandOfStartedTrips(ids,
				filterBy(mode, hour).and(dayOfWeekFilter(dayOfWeek)),
				createMatrixWriter(mode, hour, hour, dayOfWeek.name().toLowerCase()),
				scaleFactor());
	}

	/**
	 * Create a BiPredicate to filter started trips by mode and hour
	 *
	 * @param mode the mode
	 * @param hour the hour
	 * @return the bi predicate
	 */
	private BiPredicate<Person, StartedTrip<?>> filterBy(Mode mode, int hour) {
		return modeFilter(mode).and(hourFilter(hour));
	}

	/**
	 * Creates a new matrix consumer to print matrices to files.
	 *
	 * @param mode   the mode for which demand was aggregated
	 * @param from   the time from where on demand was aggregated
	 * @param to     the time until which demand was aggregated
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
			printer.writeMatrixToFile(matrix, Integer.toString(from), Integer.toString(to),
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
	
	public static BiPredicate<Person, StartedTrip<?>> dayOfWeekFilter(DayOfWeek dayType) {
		return (person, trip) -> trip.startDate().weekDay().equals(dayType);
	}

	public static BiPredicate<Person, StartedTrip<?>> weekendFilter() {
		return dayOfWeekFilter(DayOfWeek.SATURDAY).or(dayOfWeekFilter(DayOfWeek.SUNDAY));
	}

	public static BiPredicate<Person, StartedTrip<?>> weekdayFilter() {
		return dayOfWeekFilter(DayOfWeek.MONDAY)
				.or(dayOfWeekFilter(DayOfWeek.TUESDAY))
				.or(dayOfWeekFilter(DayOfWeek.WEDNESDAY))
				.or(dayOfWeekFilter(DayOfWeek.THURSDAY))
				.or(dayOfWeekFilter(DayOfWeek.FRIDAY));
	}

	public static BiPredicate<Person, StartedTrip<?>> hourFilter(int hour) {
		return (person, trip) -> trip.startDate().getHour() == hour;
	}

	public static BiPredicate<Person, StartedTrip<?>> modeFilter(Mode mode) {
		return (person, trip) -> trip.mode().equals(mode);
	}

}