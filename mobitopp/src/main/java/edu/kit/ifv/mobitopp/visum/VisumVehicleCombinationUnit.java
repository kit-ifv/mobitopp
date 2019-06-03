package edu.kit.ifv.mobitopp.visum;


public final class VisumVehicleCombinationUnit {

  public final int combinationId;
  public final VisumVehicleUnit unit;
  public final int quantity;

  public VisumVehicleCombinationUnit(int combinationId, VisumVehicleUnit unit, int quantity) {
    super();
    this.combinationId = combinationId;
    this.unit = unit;
    this.quantity = quantity;
  }

}
