package edu.kit.ifv.mobitopp.simulation;

public enum HouseholdType
{

	SINGLE,
	COUPLE,
	KIDS0TO7,
	KIDS8TO12,
	KIDS13PLUS,
	MULTIADULT
	;

	public static HouseholdType type(Household household) {
		
		if (household.getSize() == 1) {
			return SINGLE;
		}
		if (hasKids0to7(household)) {
			return KIDS0TO7;
		}
		if (!hasKids0to7(household) && hasKids8to12(household)) {
			return KIDS8TO12;
		}
		if (!hasKids0to7(household) && !hasKids8to12(household) && hasKids(household)) {
			return KIDS13PLUS;
		}
		if (household.getSize() >= 3 && !hasKids(household)) {
			return MULTIADULT;
		}
		if (household.getSize() == 2 &&  (maxAge(household) - minAge(household) < 20)) {
			return COUPLE;
		}
		if (!hasKids(household) && (maxAge(household) - minAge(household) >= 20) ) {
			return MULTIADULT;
		}
		throw new AssertionError();
	}

	private static int minAge(Household household) {
		
		return household.getPersons().stream().mapToInt(p -> p.age()).min().getAsInt();
	}

	private static int maxAge(Household household) {
		
		return household.getPersons().stream().mapToInt(p -> p.age()).max().getAsInt();
	}

	private static boolean hasKids(Household household) {
		
		return household.numberOfNotSimulatedChildren() >= 1 || minAge(household) < 18;
	}

	private static boolean hasKids8to12(Household household) {
		
		return household.getPersons().stream().filter(p -> p.age() >= 8 && p.age() <=12).findFirst().isPresent();
	}

	private static boolean hasKids0to7(Household household) {
		
		return household.numberOfNotSimulatedChildren() >= 1
				|| household.getPersons().stream().filter(p -> p.age() <= 7).findFirst().isPresent();
	}
}
