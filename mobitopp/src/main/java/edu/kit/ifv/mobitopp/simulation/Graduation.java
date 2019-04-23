package edu.kit.ifv.mobitopp.simulation;

import java.util.Arrays;

public enum Graduation {
  undefined(-1),
  other(0),
  notHighSchool(1),
  highSchoolGraduate(2),
  someCollegeCreditNoDegree(3),
  associateTechnicalSchoolDegree(4),
  bachelorDegree(5),
  graduateDegree(6);

  private final int numeric;

  private Graduation(int numeric) {
    this.numeric = numeric;
  }

  public static Graduation getTypeFromNumeric(int numeric) {
    return Arrays.stream(values()).filter(g -> g.numeric == numeric).findFirst().orElse(undefined);
  }

  public int getNumeric() {
    return numeric;
  }
}
