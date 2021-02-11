package edu.kit.ifv.mobitopp.data.local;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixType;
import edu.kit.ifv.mobitopp.simulation.Mode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamicTypeMapping implements TypeMapping {

  private final Map<Mode, TravelTimeMatrixType> mapping;

  public DynamicTypeMapping() {
    super();
    this.mapping = new HashMap<>();
  }

  public void add(Mode mode, TravelTimeMatrixType type) {
    mapping.put(mode, type);
  }

  @Override
  public TravelTimeMatrixType resolve(Mode mode) {
    if (mapping.containsKey(mode)) {
      return mapping.get(mode);
    }
    throw warn(new IllegalArgumentException("No travel time matrix type available for " + mode), log);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mapping);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DynamicTypeMapping other = (DynamicTypeMapping) obj;
    return Objects.equals(mapping, other.mapping);
  }

  @Override
  public String toString() {
    return "TypeMapping [mapping=" + mapping + "]";
  }
}
