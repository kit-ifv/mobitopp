package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.util.List;

public interface ForeignKeySerialiserFormat<T> {

	List<String> header();

	List<String> prepare(T element, PopulationContext context);

	T parse(List<String> data, PopulationContext context);

}
