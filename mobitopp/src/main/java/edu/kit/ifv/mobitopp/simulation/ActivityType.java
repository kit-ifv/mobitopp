package edu.kit.ifv.mobitopp.simulation;

import java.util.EnumSet;

public enum ActivityType 
{ 

	WORK      (1),
	BUSINESS  (2),
	EDUCATION (3),
	SHOPPING  (4),
	LEISURE   (5),
	SERVICE   (6),
	HOME      (7),
	UNDEFINED (8),
	OTHERHOME (9),

	PRIVATE_BUSINESS (11),
	PRIVATE_VISIT    (12),
	SHOPPING_DAILY   (41),
	SHOPPING_OTHER   (42),
	LEISURE_INDOOR   (51),
	LEISURE_OUTDOOR  (52),
	LEISURE_OTHER    (53),
	LEISURE_WALK     (77),

	EDUCATION_PRIMARY    (31),
	EDUCATION_SECONDARY  (32),
	EDUCATION_TERTIARY   (33),
	EDUCATION_OCCUP 		 (34)
	;	

	private final int numericType;

	private ActivityType(int numericType) {
		this.numericType=numericType;	
	}

	public int getTypeAsInt() {
		return numericType;
	}

	public String getTypeAsString() {
		return name();
	}

	public boolean isFixedActivity() { return isFixedActivity(this); }
	public boolean isHomeActivity() { return isHomeActivity(this); }
	public boolean isShoppingActivity() { return isShoppingActivity(this); }
	public boolean isLeisureActivity() { return isLeisureActivity(this); }
	public boolean isWorkActivity() { return isWorkActivity(this); }
	public boolean isBusinessActivity() { return isBusinessActivity(this); }
	public boolean isEducationActivity() { return isEducationActivity(this); }


	public static boolean isFixedActivity(ActivityType type) {
		return type == WORK || type == EDUCATION;
	}

	public static boolean isHomeActivity(ActivityType type) {
		return type == HOME || type == OTHERHOME || type == UNDEFINED;
	}

	public static boolean isShoppingActivity(ActivityType type) {
		return type == SHOPPING 
				|| type == SHOPPING_DAILY
				|| type == SHOPPING_OTHER
				|| type == PRIVATE_BUSINESS
		;
	}

	public static boolean isLeisureActivity(ActivityType type) {
		return type == LEISURE
				|| type == LEISURE_INDOOR
				|| type == LEISURE_OUTDOOR
				|| type == LEISURE_OTHER
				|| type == LEISURE_WALK
				|| type == PRIVATE_VISIT
		;
	}

	public static boolean isEducationActivity(ActivityType type) {
		return type == EDUCATION
				|| type == EDUCATION_PRIMARY
				|| type == EDUCATION_SECONDARY
				|| type == EDUCATION_TERTIARY
				|| type == EDUCATION_OCCUP
		;
	}

	public static boolean isWorkActivity(ActivityType type) {
		return type == WORK;
	}

	public static boolean isBusinessActivity(ActivityType type) {
		return type == BUSINESS;
	}

	public static ActivityType getTypeFromInt(int type) {

		for (ActivityType activityType : EnumSet.allOf(ActivityType.class)) {

			if (activityType.getTypeAsInt() == type) {
				return activityType;
			}
		}
		throw new AssertionError("invalid type: " + type);
	}

}
