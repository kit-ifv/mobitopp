package edu.kit.ifv.mobitopp.result;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ResultRepository {

	private final Map<Category, ResultOutput> outputs;
	private final Function<Category, ResultOutput> factory;

	public ResultRepository(Function<Category, ResultOutput> factory) {
		super();
		this.outputs = new HashMap<>();
		this.factory = factory;
	}

	public ResultOutput fileFor(Category category) {
		ensureOutputIsAvailableFor(category);
		return outputs.get(category);
	}

	private void ensureOutputIsAvailableFor(Category category) {
		if (misses(category)) {
			outputs.put(category, factory.apply(category));
		}
	}

	private boolean misses(Category category) {
		return !outputs.containsKey(category);
	}

	public void close() throws IOException {
		for (ResultOutput output : outputs.values()) {
			output.close();
		}
	}

}
