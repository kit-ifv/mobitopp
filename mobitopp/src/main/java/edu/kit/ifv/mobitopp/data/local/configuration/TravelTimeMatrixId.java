package edu.kit.ifv.mobitopp.data.local.configuration;

import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DayType;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class TravelTimeMatrixId {

	private final StandardMode mode;
	private final DayType dayType;
	private final TimeSpan timeSpan;

	public StandardMode matrixType() {
		return mode;
	}

	public DayType dayType() {
		return dayType;
	}

	public TimeSpan timeSpan() {
		return timeSpan;
	}

	public Stream<TravelTimeMatrixId> split() {
		Function<TimeSpan, TravelTimeMatrixId> factory = timeSpan -> new TravelTimeMatrixId(mode,
				dayType, timeSpan);
		return timeSpan.hours().map(factory);
	}

}
