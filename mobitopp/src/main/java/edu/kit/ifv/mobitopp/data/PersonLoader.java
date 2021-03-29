package edu.kit.ifv.mobitopp.data;

import java.util.Collection;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.person.PersonForDemand;

public interface PersonLoader {

	Household getHouseholdByOid(int aHouseholdOid);

	void removeHousehold(int oid);

	/**
	 * After initializing a single {@link PersonForDemand}. The corresponding input
	 * data can be deleted. This method deletes the input data for the person with
	 * the given oid.
	 * 
	 * @param oid oid of person to delete input data from
	 */
	void removePerson(int oid);

	Collection<Integer> getPersonOids();

	Person getPersonByOid(int id);

	Stream<Household> households();

	/**
	 * After the initialization of the short term module, the input data is no
	 * longer needed. This method removes all {@link HouseholdForSetup} and
	 * {@link PersonBuilder} instances.
	 */
	void clearInput();

}
