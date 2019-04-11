package edu.kit.ifv.mobitopp.dataimport;

import java.util.Objects;

public class DefaultRegionType implements RegionType {

  private static final long serialVersionUID = 1L;
  
  private final int code;

  public DefaultRegionType(int code) {
    super();
    this.code = code;
  }
  
  @Override
  public int code() {
    return code;
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
    DefaultRegionType other = (DefaultRegionType) obj;
    return code == other.code;
  }

  @Override
  public String toString() {
    return "DefaultRegionType [code=" + code + "]";
  }

}
