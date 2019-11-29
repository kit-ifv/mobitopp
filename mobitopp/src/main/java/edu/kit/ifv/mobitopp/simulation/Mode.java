package edu.kit.ifv.mobitopp.simulation;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;



public interface Mode {
	
	public static Mode UNDEFINED = StandardMode.UNDEFINED;
	public static Mode UNKNOWN = StandardMode.UNKNOWN;
	public static Mode BIKE = StandardMode.BIKE;
	public static Mode CAR = StandardMode.CAR;
	public static Mode PASSENGER = StandardMode.PASSENGER;
	public static Mode PEDESTRIAN = StandardMode.PEDESTRIAN;
	public static Mode PUBLICTRANSPORT = StandardMode.PUBLICTRANSPORT;
	public static Mode TRUCK = StandardMode.TRUCK;
	public static Mode PARK_AND_RIDE = StandardMode.PARK_AND_RIDE;
	public static Mode TAXI = StandardMode.TAXI;
	public static Mode CARSHARING_STATION = StandardMode.CARSHARING_STATION;
	public static Mode CARSHARING_FREE = StandardMode.CARSHARING_FREE;
	public static Mode PEDELEC = StandardMode.PEDELEC;
	public static Mode BIKESHARING = StandardMode.BIKESHARING;
	public static Mode RIDE_POOLING = StandardMode.RIDE_POOLING;
	public static Mode RIDE_HAILING = StandardMode.RIDE_HAILING;
	public static Mode PREMIUM_RIDE_HAILING = StandardMode.PREMIUM_RIDE_HAILING;
	
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

	public int getTypeAsInt();

	public boolean isFlexible();
	
	public boolean isDefined();
	
	public boolean usesCarAsDriver();

	public static Set<Mode> exclusive(Mode mode) {
		return Set.of(mode);
	}

}
