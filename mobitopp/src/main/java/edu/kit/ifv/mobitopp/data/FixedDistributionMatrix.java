package edu.kit.ifv.mobitopp.data;

import java.util.List;

public class FixedDistributionMatrix extends FloatMatrix implements MatrixIfc, Matrix<Float> {

	private static final long serialVersionUID = 1L;

	public FixedDistributionMatrix(List<ZoneId> ids)
  {
    super(ids);
  }

  public MatrixType type()
  {
    return MatrixType.FIXEDDISTRIBUTIONMATRIX;
  }

}
