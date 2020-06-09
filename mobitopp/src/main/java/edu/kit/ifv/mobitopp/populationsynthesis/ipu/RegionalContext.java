package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;

public interface RegionalContext {

	String name();

	boolean matches(RegionalLevel level);

}
