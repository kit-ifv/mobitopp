package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.util.Optional;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;

public interface PopulationContext {

	Optional<Household> getHouseholdByOid(int householdOid);

	Optional<Person> getPersonByOid(int personOid);

	TourBasedActivityPattern activityScheduleFor(int oid);

  Stream<FixedDestination> destinations(PersonId forPerson);


}
