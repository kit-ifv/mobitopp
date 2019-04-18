package edu.kit.ifv.mobitopp.data;

import java.io.Serializable;
import java.util.Objects;

public class ZoneId implements Serializable {

  private static final long serialVersionUID = 1L;
  private final static int UNDEFINED_OID = -1;

  private final String externalId;
  private final int matrixColumn;

  public ZoneId(String externalId, int matrixColumn) {
    super();
    assert matrixColumn != UNDEFINED_OID;
    Objects.requireNonNull(externalId);
    this.externalId = externalId;
    this.matrixColumn = matrixColumn;
  }

  public int getMatrixColumn() {
    return matrixColumn;
  }
  
  public String getExternalId() {
    return externalId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(externalId, matrixColumn);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ZoneId other = (ZoneId) obj;
    return Objects.equals(externalId, other.externalId) && matrixColumn == other.matrixColumn;
  }

  @Override
  public String toString() {
    return "ZoneId [externalId=" + externalId + ", matrixColumn=" + matrixColumn + "]";
  }

}
