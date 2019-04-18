package edu.kit.ifv.mobitopp.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FloatMatrix 
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	private float[][] matrix;
  private final List<Integer> oids;
	private final int size;
	private final float defaultValue;

	public FloatMatrix(List<Integer> oids, float defaultValue) {

		this.oids = Collections.unmodifiableList(new ArrayList<>(oids));
		this.size = oids.size();

		this.defaultValue = defaultValue;

    this.matrix = new float[size][size]; 

		initMatrix();
	}

	public FloatMatrix(List<Integer> oids) {
		this(oids, Float.NaN);
	}


	private void initMatrix() {

		for(int i=0; i<this.size; i++) {
			for(int j=0; j<this.size; j++) {
			this.matrix[i][j] = this.defaultValue;
			}
		}
	}

	public List<Integer> oids() {
		return this.oids;
	}

  public Float get(int row,int column)
  {
		assert row > 0 && row <= size;
		assert column > 0 && column <= size;

    return this.matrix[row-1][column-1];
  }

	public float get(ZoneId origin, ZoneId destination) {
		return get(origin.getMatrixColumn(), destination.getMatrixColumn());
	}

  public void set(int row, int column, Float value)
  {
		assert row > 0 && row <= size : ("row=" + row + ", size=" + size);
		assert column > 0 && column <= size : ("column=" + column + ", size=" + size);

    this.matrix[row-1][column-1] = value;
  }
 
}
