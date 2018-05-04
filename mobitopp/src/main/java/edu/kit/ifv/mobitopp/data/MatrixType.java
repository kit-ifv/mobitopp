package edu.kit.ifv.mobitopp.data;


public enum MatrixType
{

  ACTIVITYTRIPMATRIX,
  AIRDISTANCEMATRIX,
  COSTMATRIX,
  FIXEDDISTRIBUTIONMATRIX,
  MODETRIPMATRIX,
  POLEMATRIX,
  TRAVELTIMEMATRIX
	;

	public boolean hasSubtype()
	{
		switch (this)
		{
			case ACTIVITYTRIPMATRIX:
			case COSTMATRIX:
			case FIXEDDISTRIBUTIONMATRIX:
			case MODETRIPMATRIX:
			case TRAVELTIMEMATRIX:
				return true;
			case AIRDISTANCEMATRIX:
			case POLEMATRIX:
				return false;
			default:
				throw new AssertionError();
		}
  }


}

