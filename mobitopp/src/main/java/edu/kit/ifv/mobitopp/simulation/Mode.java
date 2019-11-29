package edu.kit.ifv.mobitopp.simulation;

import java.util.List;
import java.util.Set;

public interface Mode {

	public static final Set<Mode> CHOICE_SET_FULL = StandardChoiceSet.CHOICE_SET_FULL;
	public static final Set<Mode> CHOICE_SET_FLEXIBLE = StandardChoiceSet.CHOICE_SET_FLEXIBLE;
	public static final Set<Mode> CHOICE_SET_WITHOUT_PASSENGER = StandardChoiceSet.CHOICE_SET_WITHOUT_PASSENGER;
	public static final Set<Mode> CHOICE_SET_SCHAUFENSTER = StandardChoiceSet.CHOICE_SET_SCHAUFENSTER;
	public static final Set<Mode> FIXED_MODES = StandardChoiceSet.FIXED_MODES;
	public static final Set<Mode> MODESET_CAR_BIKE_WALK_PT = StandardChoiceSet.MODESET_CAR_BIKE_WALK_PT;
	public static final Set<Mode> MODESET_CAR_ONLY = StandardChoiceSet.MODESET_CAR_ONLY;
	public static final Set<Mode> MODESET_BIKE_ONLY = StandardChoiceSet.MODESET_BIKE_ONLY;
	public static final Set<Mode> MODESET_WALK_PT = StandardChoiceSet.MODESET_WALK_PT;
	public static final List<Set<Mode>> MODESET_VALUES = StandardChoiceSet.MODESET_VALUES;

	public int getTypeAsInt();

	public boolean isFlexible();

	public boolean isDefined();

	public boolean usesCarAsDriver();

	public static Set<Mode> exclusive(Mode mode) {
		return Set.of(mode);
	}

}
