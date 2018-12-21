package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;

public class PersonFixedDestination {

	private final PersonId person;
	private final FixedDestination destination;

	public PersonFixedDestination(PersonId person, FixedDestination destination) {
		super();
		this.person = person;
		this.destination = destination;
	}

	public PersonId person() {
		return person;
	}
	
	public FixedDestination fixedDestination() {
		return destination;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((person == null) ? 0 : person.hashCode());
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
		PersonFixedDestination other = (PersonFixedDestination) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (person == null) {
			if (other.person != null)
				return false;
		} else if (!person.equals(other.person))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PersonFixedDestination [person=" + person + ", destination=" + destination + "]";
	}


}
