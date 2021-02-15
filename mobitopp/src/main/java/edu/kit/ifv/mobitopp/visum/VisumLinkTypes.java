package edu.kit.ifv.mobitopp.visum;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VisumLinkTypes implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<Integer, VisumLinkType> types;

	public VisumLinkTypes(Map<Integer, VisumLinkType> types) {
		super();
		this.types = types;
	}

	public VisumLinkType getById(int id) {
		if (types.containsKey(id)) {
			return types.get(id);
		}
		throw warn(new IllegalArgumentException(
				"VisumLinkType: link type with id=" + id + " does not exist"), log);
	}

  @Override
  public int hashCode() {
    return Objects.hash(types);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VisumLinkTypes other = (VisumLinkTypes) obj;
    return Objects.equals(types, other.types);
  }

  @Override
  public String toString() {
    return "VisumLinkTypes [types=" + types + "]";
  }

}
