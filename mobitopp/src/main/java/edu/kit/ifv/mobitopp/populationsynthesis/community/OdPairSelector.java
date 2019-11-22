package edu.kit.ifv.mobitopp.populationsynthesis.community;

import java.util.Collection;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;

public interface OdPairSelector {

	Collection<OdPair> select(PersonBuilder person);

	void scale(Community community, int numberOfCommuters);

}
