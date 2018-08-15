package edu.kit.ifv.mobitopp.simulation;

import java.util.Collection;
import java.util.NoSuchElementException;

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
		return population
				.getHouseholdByOid(householdOid)
				.orElseThrow(() -> new NoSuchElementException("Missing household for id: " + householdOid));
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
		return population
				.getPersonByOid(id)
				.orElseThrow(() -> new NoSuchElementException("Missing person for id: " + id));
	}

}
