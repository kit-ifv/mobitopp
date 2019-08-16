package edu.kit.ifv.mobitopp.data;

import java.util.Collection;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;

public interface PersonLoader {

	Collection<Integer> getHouseholdOids();

	Household getHouseholdByOid(int aHouseholdOid);

	void removeHousehold(int oid);

	Collection<Integer> getPersonOids();

	Person getPersonByOid(int id);

	Stream<Household> households();

}
