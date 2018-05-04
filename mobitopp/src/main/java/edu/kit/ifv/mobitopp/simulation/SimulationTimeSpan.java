
package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.time.Time;

public class SimulationTimeSpan {

	private final Time start;
	private final Time end;

	public SimulationTimeSpan(
		Time start,
		Time end
	) {
		this.start = start;
		this.end = end;
	}

	public Time start() {
		return this.start;
	}

	public Time end() {
		return this.end;
	}

}
