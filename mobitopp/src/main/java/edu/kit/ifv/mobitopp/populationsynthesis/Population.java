package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PopulationContext;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;

public class Population implements PopulationContext, Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<Integer, Household> households;
	private final Map<Integer, Person> persons;
	private final Map<Integer, List<ExtendedPatternActivity>> activityPatterns;

	public Population() {
		super();
		households = new LinkedHashMap<>();
		persons = new LinkedHashMap<>();
		activityPatterns = new LinkedHashMap<>();
	}

	public static Population empty() {
		return new Population();
	}

	public Collection<Integer> householdOids() {
		return Collections.unmodifiableCollection(households.keySet());
	}

	public void add(Household household) {
		households.put(household.getOid(), household);
		for (Person person : household.getPersons()) {
			add(person);
		}
	}

	public void add(Person person) {
		persons.put(person.getOid(), person);
	}

	@Override
	public Optional<Household> getHouseholdByOid(int householdOid) {
		return Optional.ofNullable(households.get(householdOid));
	}

	public Collection<Household> households() {
		return Collections.unmodifiableCollection(households.values());
	}

	public void removeHousehold(int oid) {
		Household household = households.remove(oid);
		household.getPersons().forEach(person -> persons.remove(person.getOid()));
	}

	public Collection<Integer> getPersonOids() {
		return Collections.unmodifiableCollection(persons.keySet());
	}

	@Override
	public Optional<Person> getPersonByOid(int id) {
		return Optional.ofNullable(persons.get(id));
	}

	@Override
	public TourBasedActivityPattern activityScheduleFor(int oid) {
		return activityScheduleOf(oid);
	}
	
	private TourBasedActivityPattern activityScheduleOf(int oid) {
		if (activityPatterns.containsKey(oid)) {
			List<ExtendedPatternActivity> activities = activityPatterns.get(oid);
			// return new PatternActivityWeek(activities);
			return TourBasedActivityPattern.fromExtendedPatternActivities(activities);
		}
		throw new IllegalArgumentException(
				"Could not find PatternActivityWeek for person with oid " + oid);
	}

	public void add(int personOid, ExtendedPatternActivity pattern) {
		List<ExtendedPatternActivity> patternWeek = activityPatterns.getOrDefault(personOid, new ArrayList<>());
		patternWeek.add(pattern);
		activityPatterns.put(personOid, patternWeek);
	}

}
