package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.time.Time;

public class StateChange {

	private final Person person;
	private final Time date;
	private final PersonState previous;
	private final PersonState next;

	public StateChange(
			Person person, Time date, PersonState previous, PersonState next) {
		this.person = person;
		this.date = date;
		this.previous = previous;
		this.next = next;
	}

	public Person person() {
		return person;
	}

	public Time date() {
		return date;
	}

	public PersonState next() {
		return next;
	}

	public PersonState previous() {
		return previous;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((next == null) ? 0 : next.hashCode());
		result = prime * result + ((person == null) ? 0 : person.hashCode());
		result = prime * result + ((previous == null) ? 0 : previous.hashCode());
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
		StateChange other = (StateChange) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (next == null) {
			if (other.next != null)
				return false;
		} else if (!next.equals(other.next))
			return false;
		if (person == null) {
			if (other.person != null)
				return false;
		} else if (!person.equals(other.person))
			return false;
		if (previous == null) {
			if (other.previous != null)
				return false;
		} else if (!previous.equals(other.previous))
			return false;
		return true;
	}

}
