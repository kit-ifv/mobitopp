package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.util.List;

public interface SerialiserFormat<T> {

	List<String> header();

	List<String> prepare(T t);

	T parse(List<String> data);

}
