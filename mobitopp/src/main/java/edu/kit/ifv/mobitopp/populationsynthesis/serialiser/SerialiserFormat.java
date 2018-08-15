package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.util.List;
import java.util.Optional;

public interface SerialiserFormat<T> {

	List<String> header();

	List<String> prepare(T t);

	Optional<T> parse(List<String> data);

}
