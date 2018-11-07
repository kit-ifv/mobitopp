package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.util.List;
import java.util.Optional;

public interface ForeignKeySerialiserFormat<T> {

	List<String> header();

	List<String> prepare(T element);

	Optional<T> parse(List<String> data, PopulationContext context);

}
