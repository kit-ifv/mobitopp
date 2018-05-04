package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static java.time.temporal.ChronoUnit.HOURS;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class Accept implements EntryAcceptor, Validity {

	private Time time;

	private Accept(Time time) {
		super();
		this.time = time;
	}

	public static Accept perHour(Time time) {
		return new Accept(time);
	}
	
	@Override
	public String asFileName() {
		int dayOfMonth = time.getDay();
		int hour = time.getHour();
		return String.valueOf(dayOfMonth + "-" + hour);
	}

	@Override
	public boolean isTooLate(FunctionEntry entry) {
		return nextHour().isBeforeOrEqualTo(entry.departure());
	}

	@Override
	public boolean accept(FunctionEntry entry) {
		return isInCurrentHour(entry) && isBeforeNextHour(entry);
	}

	private boolean isInCurrentHour(FunctionEntry entry) {
		return time.isBeforeOrEqualTo(entry.departure());
	}

	private boolean isBeforeNextHour(FunctionEntry entry) {
		return nextHour().isAfter(entry.departure());
	}

	private Time nextHour() {
		return time.plus(RelativeTime.of(1, HOURS));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((time == null) ? 0 : time.hashCode());
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
		Accept other = (Accept) obj;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Accept [time=" + time + "]";
	}

}
