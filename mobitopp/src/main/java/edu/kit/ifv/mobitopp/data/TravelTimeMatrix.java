package edu.kit.ifv.mobitopp.data;

import java.util.List;

public class TravelTimeMatrix extends FloatMatrix implements MatrixIfc, Matrix<Float> {

	private static final long serialVersionUID = 1L;

	public TravelTimeMatrix(List<Integer> oids)
  {
    super(oids);
  }

  public TravelTimeMatrix(List<Integer> oids, float defaultValue)
  {
    super(oids, defaultValue);
  }

  public MatrixType type()
  {
    return MatrixType.TRAVELTIMEMATRIX;
  }

}
