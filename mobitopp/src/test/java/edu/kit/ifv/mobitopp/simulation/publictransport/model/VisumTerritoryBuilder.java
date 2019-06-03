package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumTerritory;

public class VisumTerritoryBuilder {

  private int id;
  private String code;
  private String name;
  private int surfaceId;
  private VisumSurface surface;

  public VisumTerritoryBuilder() {
    super();
  }

  public VisumTerritory build() {
    return new VisumTerritory(id, code, name, surfaceId, surface);
  }

  public VisumTerritoryBuilder withId(int id) {
    this.id = id;
    return this;
  }

  public VisumTerritoryBuilder withCode(String code) {
    this.code = code;
    return this;
  }

  public VisumTerritoryBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public VisumTerritoryBuilder with(VisumSurface surface) {
    this.surfaceId = surface.id;
    this.surface = surface;
    return this;
  }

}
