package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.time.Time;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Hooks {

	private List<Hook> hooks;

	public Hooks() {
		super();
		hooks = new ArrayList<>();
	}

	public void add(Hook hook) {
		if (hook == null) {
			log.warn("The list of Hooks is null");
			return;
		}
		hooks.add(hook);
	}
	
	public void remove(Hook hook) {
		if (hook == null) {
			log.warn("The list of Hooks is null");
			return;
		}
		hooks.remove(hook);
	}

	public void process(Time currentTime) {
		for (Hook hook : hooks) {
			hook.process(currentTime);
		}
	}

}
