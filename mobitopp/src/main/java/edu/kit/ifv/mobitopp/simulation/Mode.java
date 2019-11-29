package edu.kit.ifv.mobitopp.simulation;

import java.util.Set;

public interface Mode {

	public int getTypeAsInt();

	public boolean isFlexible();

	public boolean isDefined();

	public boolean usesCarAsDriver();

	public static Set<Mode> exclusive(Mode mode) {
		return Set.of(mode);
	}

}
