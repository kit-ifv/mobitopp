package edu.kit.ifv.mobitopp.simulation;


public enum Graduation {
  undefined(-1), universityDegree(1), other(2);
  
  private final int numeric;

  private Graduation(int numeric) {
    this.numeric = numeric;
  }

  public static Graduation getTypeFromNumeric(int numeric) {
    if (numeric == 1) {
      return universityDegree;
    }
    if (numeric == 2) {
      return Graduation.other;
    }
    return undefined;
  }

  public int getNumeric() {
    return numeric;
  }
}
