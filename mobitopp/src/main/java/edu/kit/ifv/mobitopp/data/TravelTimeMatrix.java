package edu.kit.ifv.mobitopp.data;

import java.util.List;

public class TravelTimeMatrix extends FloatMatrix implements MatrixIfc, Matrix<Float> {

	private static final long serialVersionUID = 1L;

	public TravelTimeMatrix(List<ZoneId> ids)
  {
    super(ids);
  }

  public TravelTimeMatrix(List<ZoneId> ids, float defaultValue)
  {
    super(ids, defaultValue);
  }

  public MatrixType type()
  {
    return MatrixType.TRAVELTIMEMATRIX;
  }

}
