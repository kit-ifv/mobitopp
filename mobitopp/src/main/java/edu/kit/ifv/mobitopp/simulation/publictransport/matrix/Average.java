package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import java.time.Duration;

import edu.kit.ifv.mobitopp.time.Time;

public class Average implements TravelTime {

	private static final long minutesInTimeSlice = Duration.ofHours(1).toMinutes();

	private final ArrivalTimeSupplier arrival;

	public Average(ArrivalTimeSupplier arrival) {
		super();
		this.arrival = arrival;
	}

	@Override
	public Duration inHourAfter(Time startOfSlice) {
		Duration travelTime = Duration.ofMinutes(0);
		int elements = 0;
		for (int minute = 0; minute < minutesInTimeSlice; minute++) {
			Time departure = startOfSlice.plusMinutes(minute);
			Time earliestArrival = arrival.startingAt(departure);
			if (!Time.future.equals(earliestArrival)) {
				Duration duration = earliestArrival.differenceTo(departure).toDuration();
				travelTime = travelTime.plus(duration);
				elements++;
			}
		}
		return elements > 0 ? travelTime.dividedBy(elements) : Matrix.infinite;
	}

}
