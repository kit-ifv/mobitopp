package edu.kit.ifv.mobitopp.util.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class CollectionsHelper {

	public static <T> BiFunction<List<T>, List<T>, List<T>> mergeLists() {
		return (list1, list2) -> {
			ArrayList<T> combined = new ArrayList<>();
			combined.addAll(list1);
			combined.addAll(list2);
			return combined;
		};
	}
}
