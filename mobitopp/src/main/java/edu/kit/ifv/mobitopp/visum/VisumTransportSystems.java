package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

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

  @Override
  public int hashCode() {
    return Objects.hash(systems);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VisumTransportSystems other = (VisumTransportSystems) obj;
    return Objects.equals(systems, other.systems);
  }

  @Override
  public String toString() {
    return "VisumTransportSystems [systems=" + systems + "]";
  }

}
