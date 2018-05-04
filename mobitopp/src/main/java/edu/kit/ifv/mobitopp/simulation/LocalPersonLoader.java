package edu.kit.ifv.mobitopp.simulation;

import java.util.Collection;

import edu.kit.ifv.mobitopp.data.PersonLoader;
import edu.kit.ifv.mobitopp.populationsynthesis.Population;


public class LocalPersonLoader implements PersonLoader {

	private Population population;

	public LocalPersonLoader(Population population) {
		super();
		this.population = population;
	}

	@Override
	public Collection<Integer> getHouseholdOids() {
		return population.householdOids();
	}

	@Override
	public Household getHouseholdByOid(int householdOid) {
		return population.getHouseholdByOid(householdOid);
	}

	@Override
	public void removeHousehold(int oid) {
		population.removeHousehold(oid);
	}

	@Override
	public Collection<Integer> getPersonOids() {
		return population.getPersonOids();
	}

	@Override
	public Person getPersonByOid(int id) {
		return population.getPersonByOid(id);
	}

}
