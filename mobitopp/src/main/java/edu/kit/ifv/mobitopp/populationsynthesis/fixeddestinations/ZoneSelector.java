package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations;

import java.util.Collection;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.community.OdPair;

public interface ZoneSelector {

	void select(PersonBuilder person, Collection<OdPair> relations, double randomNumber);

}
