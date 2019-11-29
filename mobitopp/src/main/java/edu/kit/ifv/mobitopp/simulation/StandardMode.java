package edu.kit.ifv.mobitopp.simulation;

import java.util.EnumSet;

public enum StandardMode implements Mode {

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

	private StandardMode(int mode_numeric) {
		this.mode_numeric = mode_numeric;
	}
	
	public int getTypeAsInt() { return this.mode_numeric; }

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
			case TAXI:
			case TRUCK:
			case PARK_AND_RIDE:
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

	public static Mode getTypeFromInt(int mode_numeric) {
		for (Mode mode : EnumSet.allOf(StandardMode.class)) {

			if (mode.getTypeAsInt() ==  mode_numeric) {
				return mode;
			}
		}
		return StandardMode.UNKNOWN;
	}
}