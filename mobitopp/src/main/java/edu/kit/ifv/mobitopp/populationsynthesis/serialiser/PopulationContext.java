package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;

public interface PopulationContext {

	Household getHouseholdByOid(int householdOid);

	Person getPersonByOid(int personOid);

	PatternActivityWeek activityScheduleFor(int oid);

}
