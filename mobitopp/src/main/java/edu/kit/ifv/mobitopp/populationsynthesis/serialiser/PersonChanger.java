package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;

public interface PersonChanger {

	PersonBuilder attributesOf(PersonBuilder person);

	/**
	 * Returns a {@link PersonChanger} not changing any attribute.
	 * 
	 * @return {@link PersonChanger} not changing any attribute
	 */
	static PersonChanger noChange() {
		return p -> p;
	}

}
