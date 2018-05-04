package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class VisumTransportSystems implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<String, VisumTransportSystem> systems;

	public VisumTransportSystems(Map<String, VisumTransportSystem> systems) {
		super();
		this.systems = Collections.unmodifiableMap(systems);
	}

	public VisumTransportSystem getBy(String code) {
		if (systems.containsKey(code)) {
			return systems.get(code);
		}
		throw new IllegalArgumentException(
				"VisumTransportSystem: transport system with code=" + code + " does not exist");
	}

	public boolean isEmpty() {
		return systems.isEmpty();
	}

	public boolean containsFor(String code) {
		return systems.containsKey(code);
	}

}
