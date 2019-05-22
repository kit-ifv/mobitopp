package edu.kit.ifv.mobitopp.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IntegerMatrix 
	implements Serializable, Matrix<Integer>
{

	private static final long serialVersionUID = 1L;

	private int[][] matrix;
  private final List<ZoneId> ids;
	private final int size;
	private final int defaultValue;


	protected IntegerMatrix(List<ZoneId> ids, int defaultValue) {

		this.ids = Collections.unmodifiableList(new ArrayList<>(ids));
		this.size = ids.size();

		this.defaultValue = defaultValue;

    this.matrix = new int[size][size]; 

		initMatrix();
	}

	public IntegerMatrix(List<ZoneId> oids) {
		this(oids, 0);
	}


	private void initMatrix() {

		for(int i=0; i<this.size; i++) {
			for(int j=0; j<this.size; j++) {
			this.matrix[i][j] = this.defaultValue;
			}
		}
	}

	@Override
	public List<ZoneId> ids() {
		return this.ids;
	}

  public int get(int row,int column)
  {
		assert 0 <= row && size > row;
		assert 0 <= column && size > column;

    return this.matrix[row][column];
  }
  
  @Override
  public Integer get(ZoneId origin, ZoneId destination) {
    return get(origin.getMatrixColumn(), destination.getMatrixColumn());
  }

  @Override
  public void set(int row, int column, Integer value)
  {
		assert 0 <= row && row <= size : ("row=" + row + ", size=" + size);
		assert 0 <= column && column <= size : ("column=" + column + ", size=" + size);

    this.matrix[row][column] = value;
  }

	public void increment(int row,int column){
		set(row, column, get(row, column)+1);
	}

  public void increment(ZoneId origin, ZoneId destination) {
    increment(origin.getMatrixColumn(), destination.getMatrixColumn());
  }
 
}
