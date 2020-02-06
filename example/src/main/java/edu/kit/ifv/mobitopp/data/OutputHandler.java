package edu.kit.ifv.mobitopp.data;

import java.util.function.Consumer;
import edu.kit.ifv.mobitopp.simulation.Mode;

public class OutputHandler {
	
	private final Consumer<IntegerMatrix> writer;
	private final IntegerMatrix matrix;
	private final Mode mode;
	private final int from;
	private final int to;
	private final int scalingfactor;
	
	public OutputHandler(Consumer<IntegerMatrix> writer, IntegerMatrix matrix, Mode mode, int from, int to, int scalingfactor) {

		this.writer = writer;
		this.matrix = matrix;
		this.mode = mode;
		this.from = from;
		this.to = to;
		this.scalingfactor = scalingfactor;
	}

	public Consumer<IntegerMatrix> writer() {
		return writer;
	}
	
	public IntegerMatrix matrix() {
		return matrix;
	}

	public Mode mode() {
		return mode;
	}

	public int from() {
		return from;
	}

	public int to() {
		return to;
	}
	
	public int scalingfactor() {
		return scalingfactor;
	}
	

}
