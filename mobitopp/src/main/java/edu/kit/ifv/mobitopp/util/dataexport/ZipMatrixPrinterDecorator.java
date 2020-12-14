package edu.kit.ifv.mobitopp.util.dataexport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import edu.kit.ifv.mobitopp.data.Matrix;

/**
 * The Class ZipMatrixPrinterDecorator decorates a MatrixPrinter by ziping the
 * content provided by the wrapped {@link AbstractMatrixPrinter MatrixPrinter}.
 */
public class ZipMatrixPrinterDecorator extends AbstractMatrixPrinter {

	/** The matrix printer. */
	private MatrixPrinter matrixPrinter;

	/**
	 * Instantiates a new ZipMatrixPrinterDecorator with the given matrixPrinter as
	 * delegate to be decorated.
	 *
	 * @param matrixPrinter the matrix printer to be decorated
	 */
	public ZipMatrixPrinterDecorator(MatrixPrinter matrixPrinter) {
		super();
		this.matrixPrinter = matrixPrinter;
	}

	/**
	 * Instantiates a new ZipMatrixPrinterDecorator with a {@link MatrixPrinter} as
	 * delegate printer.
	 */
	public ZipMatrixPrinterDecorator() {
		this(new MatrixPrinter());
	}

	/**
	 * Gets the content to be saved as a file from the delegate
	 * {@link AbstractMatrixPrinter matrix printer}.
	 *
	 * @param matrix the matrix to be printed
	 * @param from   the time from
	 * @param to     the time to
	 * @return the content
	 */
	@Override
	protected StringBuffer getContent(Matrix<? extends Number> matrix, String from, String to) {
		return matrixPrinter.getContent(matrix, from, to);
	}

	/**
	 * Write the given content to the given output file inside a bz2 file. The
	 * zippde file has the name of the given output file followed by the ".bz2" file
	 * extension.
	 *
	 * @param content    the content to be printed
	 * @param outputFile the output file
	 */
	@Override
	protected void write(StringBuffer content, File outputFile) {
		File bz2OutputFile = new File(outputFile.getAbsolutePath() + ".bz2");

		try (FileOutputStream fos = new FileOutputStream(bz2OutputFile)) {
			System.out.println("writing matrix file: " + outputFile.getAbsolutePath());

			BZip2CompressorOutputStream zipOutputStream = new BZip2CompressorOutputStream(fos);
			OutputStreamWriter writer = new OutputStreamWriter(zipOutputStream);

			try {
				writer.write(content.toString());
			} finally {
				writer.close();
			}

			fos.close();
			zipOutputStream.close();

		} catch (Exception e) {
			System.out.println(e);
			throw new RuntimeException(e);

		}
	}

}
