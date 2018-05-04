package edu.kit.ifv.mobitopp.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.io.Serializable;

public class IntegerMatrix 
	implements Serializable, Matrix<Integer>
{

	private static final long serialVersionUID = 1L;

	private int[][] matrix;
  private final List<Integer> oids;
	private final int size;
	private final int defaultValue;


	protected IntegerMatrix(List<Integer> oids, int defaultValue) {

		this.oids = Collections.unmodifiableList(new ArrayList<>(oids));
		this.size = oids.size();

		this.defaultValue = defaultValue;

    this.matrix = new int[size][size]; 

		initMatrix();
	}

	public IntegerMatrix(List<Integer> oids) {
		this(oids, 0);
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

  public Integer get(int row,int column)
  {
		assert row > 0 && row <= size;
		assert column > 0 && column <= size;

    return this.matrix[row-1][column-1];
  }

  public void set(int row, int column, Integer value)
  {
		assert row > 0 && row <= size : ("row=" + row + ", size=" + size);
		assert column > 0 && column <= size : ("column=" + column + ", size=" + size);

    this.matrix[row-1][column-1] = value;
  }

	public void increment(int row,int column){

		set(row, column, get(row, column)+1);
	}
 
}
