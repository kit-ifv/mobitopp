package edu.kit.ifv.mobitopp.data;

import java.util.List;

public class FixedDistributionMatrix extends FloatMatrix implements MatrixIfc, Matrix<Float> {

	private static final long serialVersionUID = 1L;

	public FixedDistributionMatrix(List<Integer> oids)
  {
    super(oids);
  }

  public MatrixType type()
  {
    return MatrixType.FIXEDDISTRIBUTIONMATRIX;
  }

}
