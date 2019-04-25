package edu.kit.ifv.mobitopp.simulation;

import java.util.EnumSet;
import java.util.Set;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;



public enum Mode { 
	UNDEFINED(-2),
	UNKNOWN(-1),
	BIKE(0),
	CAR(1),
	PASSENGER(2),
	PEDESTRIAN(3),
	PUBLICTRANSPORT(4),
	TRUCK(5),
	PARK_AND_RIDE(6),
	TAXI(7),
	CARSHARING_STATION(11),
	CARSHARING_FREE(12),
	PEDELEC(16),
	BIKESHARING(17),
	RIDE_POOLING(21),
	RIDE_HAILING(22),
	PREMIUM_RIDE_HAILING(23), 
	;

	private final int mode_numeric;

	private Mode(int mode_numeric) {
		this.mode_numeric=mode_numeric;	
	}




	public static final Set<Mode> CHOICE_SET_FULL;
	public static final Set<Mode> CHOICE_SET_FLEXIBLE;
	public static final Set<Mode> CHOICE_SET_WITHOUT_PASSENGER;
	public static final Set<Mode> CHOICE_SET_SCHAUFENSTER;

	public static final Set<Mode> FIXED_MODES;

	static {
		CHOICE_SET_FULL = Collections.unmodifiableSet(EnumSet.of(
																														Mode.CAR,
																														Mode.PASSENGER,
																														Mode.BIKE,
																														Mode.PEDESTRIAN,
																														Mode.PUBLICTRANSPORT
																									));
		CHOICE_SET_FLEXIBLE = Collections.unmodifiableSet(EnumSet.of(
																														Mode.PASSENGER,
																														Mode.PEDESTRIAN,
																														Mode.PUBLICTRANSPORT));
		CHOICE_SET_WITHOUT_PASSENGER = Collections.unmodifiableSet(EnumSet.of(
																														Mode.CAR,
																														Mode.BIKE,
																														Mode.PEDESTRIAN,
																														Mode.PUBLICTRANSPORT));

		CHOICE_SET_SCHAUFENSTER = Collections.unmodifiableSet(EnumSet.of(
																														Mode.CAR,
																														Mode.PASSENGER,
																														Mode.BIKE,
																														Mode.PEDESTRIAN,
																														Mode.PUBLICTRANSPORT,
																														Mode.CARSHARING_STATION,
																														Mode.CARSHARING_FREE,
																														Mode.PEDELEC,
																														Mode.BIKESHARING
																				));

		FIXED_MODES = Collections.unmodifiableSet(EnumSet.of(
																														Mode.CAR,
																														Mode.BIKE,
																														Mode.CARSHARING_STATION,
																														Mode.PEDELEC
																				));
	}

	public static final Set<Mode> MODESET_CAR_BIKE_WALK_PT;
	public static final Set<Mode> MODESET_CAR_ONLY;
	public static final Set<Mode> MODESET_BIKE_ONLY;
	public static final Set<Mode> MODESET_WALK_PT;

	public static final List<Set<Mode>> MODESET_VALUES;

	static {
		MODESET_CAR_BIKE_WALK_PT = Collections.unmodifiableSet(EnumSet.of(
																													Mode.CAR,
																													Mode.BIKE,
																													Mode.PEDESTRIAN,
																													Mode.PUBLICTRANSPORT
																											));
		MODESET_CAR_ONLY = Collections.unmodifiableSet(EnumSet.of(Mode.CAR));
		MODESET_BIKE_ONLY = Collections.unmodifiableSet(EnumSet.of(Mode.BIKE));
		MODESET_WALK_PT = Collections.unmodifiableSet(EnumSet.of(
																													Mode.PEDESTRIAN,
																													Mode.PUBLICTRANSPORT));
		List<Set<Mode>> tmp = new ArrayList<Set<Mode>>();
		tmp.add(MODESET_CAR_BIKE_WALK_PT);
		tmp.add(MODESET_CAR_ONLY);
		tmp.add(MODESET_BIKE_ONLY);
		tmp.add(MODESET_WALK_PT);

		MODESET_VALUES = Collections.unmodifiableList(tmp);
	}


	public int getTypeAsInt() { return this.mode_numeric; }

	public static Mode getTypeFromInt(int mode_numeric) {


		for (Mode mode : EnumSet.allOf(Mode.class)) {

			if (mode.getTypeAsInt() ==  mode_numeric) {
				return mode;
			}
		}
		return Mode.UNKNOWN;
	}

	public boolean isFlexible() {

		switch(this) {
			case PASSENGER:
			case PEDESTRIAN:
			case PUBLICTRANSPORT:
			case CARSHARING_FREE:
			case BIKESHARING:
			case RIDE_POOLING:
			case RIDE_HAILING:
			case PREMIUM_RIDE_HAILING:
				return true;
			case BIKE:
			case CAR:
			case CARSHARING_STATION:
			case PEDELEC:
				return false;
			default:
				throw new AssertionError("invalid mode: " + this);
		}
	}
	
	public boolean isDefined() {
			return this.mode_numeric >= 0;
	}
	
	public boolean usesCarAsDriver() {
		return this == CAR || this == CARSHARING_STATION || this == CARSHARING_FREE;
	}
	
	public static Set<Mode> exclusive(Mode mode) {
		return Collections.unmodifiableSet(EnumSet.of(mode));
	}

}
