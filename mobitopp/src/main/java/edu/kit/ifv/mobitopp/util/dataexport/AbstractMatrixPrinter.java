package edu.kit.ifv.mobitopp.util.dataexport;

import java.io.File;

import edu.kit.ifv.mobitopp.data.Matrix;

public abstract class AbstractMatrixPrinter {

	public void writeMatrixToFile(Matrix<? extends Number> matrix, String from, String to,
			String filename) {

		File outputFile = new File(filename);
		writeMatrixToFile(matrix, from, to, outputFile);
	}

	public void writeMatrixToFile(Matrix<? extends Number> matrix, String from, String to,
			File toOutputFile) {
		createParentFoldersOf(toOutputFile);
		StringBuffer content = getContent(matrix, from, to);
		write(content, toOutputFile);
	}

	private void createParentFoldersOf(File outputFile) {
		outputFile.getParentFile().mkdirs();
	}

	abstract protected StringBuffer getContent(Matrix<? extends Number> matrix, String from,
			String to);

	abstract protected void write(StringBuffer content, File outputFile);

}
