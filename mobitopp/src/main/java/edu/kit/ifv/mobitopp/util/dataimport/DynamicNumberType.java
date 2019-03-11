package edu.kit.ifv.mobitopp.util.dataimport;

import java.util.Objects;

import edu.kit.ifv.mobitopp.data.areatype.AreaType;

public class DynamicNumberType implements AreaType, Comparable<DynamicNumberType> {

  private final int code;

  public DynamicNumberType(int code) {
    super();
    this.code = code;
  }

  @Override
  public int getTypeAsInt() {
    return code;
  }

  @Override
  public String getTypeAsString() {
    return String.valueOf(code);
  }

  @Override
  public int compareTo(DynamicNumberType other) {
    return Integer.compare(code, other.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DynamicNumberType other = (DynamicNumberType) obj;
    return code == other.code;
  }

  @Override
  public String toString() {
    return "DynamicNumberType [code=" + code + "]";
  }

}
