package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.joining;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VisumTransportSystemSet implements Serializable {

	private static final long serialVersionUID = 1L;

	static final String delimiter = ",";

	private static Map<String, VisumTransportSystemSet> cache = new HashMap<>();

	public final Set<VisumTransportSystem> transportSystems;

	private VisumTransportSystemSet(Set<VisumTransportSystem> transportSystems) {
		super();
		this.transportSystems = transportSystems;
	}

	public static VisumTransportSystemSet getByCode(String code, VisumTransportSystems systems) {
		if (isCached(code)) {
			return useCached(code);
		} else {
			Set<VisumTransportSystem> subset = systemsFrom(code, systems);
			VisumTransportSystemSet transport = new VisumTransportSystemSet(subset);
			cache.put(code, transport);
			return transport;
		}
	}

	private static Set<VisumTransportSystem> systemsFrom(String code, VisumTransportSystems systems) {
		Set<VisumTransportSystem> subset = new HashSet<VisumTransportSystem>();
		if (!code.isEmpty()) {
			for (String ts_code : code.split(delimiter)) {
				VisumTransportSystem transport = systems.getBy(ts_code);
				subset.add(transport);
			}
		}
		return Collections.unmodifiableSet(subset);
	}

	private static VisumTransportSystemSet useCached(String code) {
		return cache.get(code);
	}

	private static boolean isCached(String code) {
		return cache.containsKey(code);
	}

	public boolean contains(VisumTransportSystem system) {
		return this.transportSystems.contains(system);
	}
	
	public String serialise() {
		return transportSystems.stream().map(system -> system.code).collect(joining(delimiter));
	}

	public String toString() {
		return "VisumTransportSystemSet" + "(" + transportSystems + ")";
	}

}
