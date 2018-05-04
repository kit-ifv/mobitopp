package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.time.Time;

public class Hooks {

	private List<Hook> hooks;

	public Hooks() {
		super();
		hooks = new ArrayList<>();
	}

	public void add(Hook hook) {
		if (hook == null) {
			System.out.println("Hook is null");
			return;
		}
		hooks.add(hook);
	}

	public void process(Time currentTime) {
		for (Hook hook : hooks) {
			hook.process(currentTime);
		}
	}

}
