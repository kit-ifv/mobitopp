package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PersonChanger;

public interface PersonChangerFactory {

	PersonChanger create(WrittenConfiguration configuration);

}
