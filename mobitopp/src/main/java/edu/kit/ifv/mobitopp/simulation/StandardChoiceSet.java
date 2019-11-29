package edu.kit.ifv.mobitopp.simulation;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class StandardChoiceSet {
	private StandardChoiceSet() {
	}
	
	public static final Set<Mode>	CHOICE_SET_FULL = Collections.unmodifiableSet(EnumSet.of(
			StandardMode.CAR,
			StandardMode.PASSENGER,
			StandardMode.BIKE,
			StandardMode.PEDESTRIAN,
			StandardMode.PUBLICTRANSPORT
	));
	public static final Set<Mode> CHOICE_SET_FLEXIBLE = Collections.unmodifiableSet(EnumSet.of(
					StandardMode.PASSENGER,
					StandardMode.PEDESTRIAN,
					StandardMode.PUBLICTRANSPORT));
	public static final Set<Mode> CHOICE_SET_WITHOUT_PASSENGER = Collections.unmodifiableSet(EnumSet.of(
					StandardMode.CAR,
					StandardMode.BIKE,
					StandardMode.PEDESTRIAN,
					StandardMode.PUBLICTRANSPORT));
	
	public static final Set<Mode> CHOICE_SET_SCHAUFENSTER = Collections.unmodifiableSet(EnumSet.of(
					StandardMode.CAR,
					StandardMode.PASSENGER,
					StandardMode.BIKE,
					StandardMode.PEDESTRIAN,
					StandardMode.PUBLICTRANSPORT,
					StandardMode.CARSHARING_STATION,
					StandardMode.CARSHARING_FREE,
					StandardMode.PEDELEC,
					StandardMode.BIKESHARING
	));
	
	public static final Set<Mode> FIXED_MODES = Collections.unmodifiableSet(EnumSet.of(
					StandardMode.CAR,
					StandardMode.BIKE,
					StandardMode.CARSHARING_STATION,
					StandardMode.PEDELEC
	));
	
	public static final Set<Mode> MODESET_CAR_BIKE_WALK_PT = Collections.unmodifiableSet(EnumSet.of(
				StandardMode.CAR,
				StandardMode.BIKE,
				StandardMode.PEDESTRIAN,
				StandardMode.PUBLICTRANSPORT
		));
	public static final Set<Mode> MODESET_CAR_ONLY = Collections.unmodifiableSet(EnumSet.of(StandardMode.CAR));
	public static final Set<Mode> MODESET_BIKE_ONLY = Collections.unmodifiableSet(EnumSet.of(StandardMode.BIKE));
	public static final Set<Mode> MODESET_WALK_PT = Collections.unmodifiableSet(EnumSet.of(
				StandardMode.PEDESTRIAN,
				StandardMode.PUBLICTRANSPORT));
	
	public static final List<Set<Mode>> MODESET_VALUES = Collections
	.unmodifiableList(
	List.of(MODESET_CAR_BIKE_WALK_PT, MODESET_CAR_ONLY, MODESET_BIKE_ONLY, MODESET_WALK_PT));

}