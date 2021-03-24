package edu.kit.ifv.mobitopp.simulation;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.PersonLoader;
import edu.kit.ifv.mobitopp.populationsynthesis.Population;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
				.orElseThrow(() -> warn(new NoSuchElementException("Missing household for id: " + householdOid), log));
	}

	@Override
	public void removeHousehold(int oid) {
		population.removeHousehold(oid);
	}
	
	@Override
	public void removePerson(int oid) {
		population.removePerson(oid);
	}

	@Override
	public Collection<Integer> getPersonOids() {
		return population.getPersonOids();
	}

	@Override
	public Person getPersonByOid(int id) {
		return population
				.getPersonByOid(id)
				.orElseThrow(() -> warn(new NoSuchElementException("Missing person for id: " + id), log));
	}

	@Override
	public Stream<Household> households() {
		return population.households();
	}

	@Override
	public void clearInput() {
		population.clearLongTermData();
	}

}
