package edu.kit.ifv.mobitopp.data;

import java.util.List;

public class CostMatrix extends FloatMatrix implements MatrixIfc {

	private static final long serialVersionUID = 1L;

	public CostMatrix(List<ZoneId> ids)
  {
    super(ids);
  }

  public MatrixType type()
  {
    return MatrixType.COSTMATRIX;
  }

}
