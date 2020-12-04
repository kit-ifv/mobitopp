package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

@SuppressWarnings("serial")
public final class VisumPtTimeProfile implements Serializable {

  public final String profileId;
  public final String name;
  public final Map<Integer, VisumPtTimeProfileElement> elements;
  public final VisumPtLineRoute route;

  public VisumPtTimeProfile(
      String profileId, String name, VisumPtLineRoute route,
      Map<Integer, VisumPtTimeProfileElement> elements) {
    super();
    this.profileId = profileId;
    this.name = name;
    this.route = route;
    this.elements = Collections.unmodifiableMap(elements);
  }

  @Override
  public String toString() {
    return "VisumPtTimeProfile [profileId=" + profileId + ", name=" + name + ", elements="
        + elements + ", route=" + route + "]";
  }

}
