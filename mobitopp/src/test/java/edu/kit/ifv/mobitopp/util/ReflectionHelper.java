package edu.kit.ifv.mobitopp.util;

import java.lang.reflect.Field;
import java.util.Map;

import edu.kit.ifv.mobitopp.simulation.HouseholdForDemand;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;

public class ReflectionHelper {

	public static void clearTransportSystemSetCache()
			throws NoSuchFieldException, SecurityException, IllegalAccessException {
		Field transportSystemSets = VisumTransportSystemSet.class.getDeclaredField("cache");
		clear(transportSystemSets);
	}

	private static void clear(Field transportSystems) throws IllegalAccessException {
		transportSystems.setAccessible(true);
		Map<?, ?> map = (Map<?, ?>) transportSystems.get(null);
		map.clear();
	}

	public static void resetHouseholdIdSequence() throws ReflectiveOperationException {
		Field field = HouseholdForDemand.class.getDeclaredField("id_counter");
		field.setAccessible(true);
		field.set(null, 1);
	}
}
