package edu.kit.ifv.mobitopp.data.local.configuration;

import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DayType;

public class TravelTimeMatrixId {

	private TravelTimeMatrixType matrixType;
	private DayType dayType;
	private TimeSpan timeSpan;

	public TravelTimeMatrixId(TravelTimeMatrixType matrixType, DayType dayType, TimeSpan timeSpan) {
		super();
		this.matrixType = matrixType;
		this.dayType = dayType;
		this.timeSpan = timeSpan;
	}

	public TravelTimeMatrixType matrixType() {
		return matrixType;
	}

	public DayType dayType() {
		return dayType;
	}

	public TimeSpan timeSpan() {
		return timeSpan;
	}

	public Stream<TravelTimeMatrixId> split() {
		Function<TimeSpan, TravelTimeMatrixId> factory = timeSpan -> new TravelTimeMatrixId(matrixType,
				dayType, timeSpan);
		return timeSpan.hours().map(factory);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dayType == null) ? 0 : dayType.hashCode());
		result = prime * result + ((matrixType == null) ? 0 : matrixType.hashCode());
		result = prime * result + ((timeSpan == null) ? 0 : timeSpan.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TravelTimeMatrixId other = (TravelTimeMatrixId) obj;
		if (dayType != other.dayType)
			return false;
		if (matrixType != other.matrixType)
			return false;
		if (timeSpan == null) {
			if (other.timeSpan != null)
				return false;
		} else if (!timeSpan.equals(other.timeSpan))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TravelTimeMatrixId [matrixType=" + matrixType + ", dayType=" + dayType + ", timeSpan="
				+ timeSpan + "]";
	}

}
