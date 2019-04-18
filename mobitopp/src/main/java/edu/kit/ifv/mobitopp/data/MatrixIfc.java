package edu.kit.ifv.mobitopp.data;

import java.io.Serializable;
import java.util.List;

public interface MatrixIfc
  extends Serializable
{ 

  MatrixType type();

  List<ZoneId> ids();

}
