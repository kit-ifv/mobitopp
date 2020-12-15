package edu.kit.ifv.mobitopp.util.dataexport;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import edu.kit.ifv.mobitopp.data.Matrix;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class ZipMatrixPrinterDecorator decorates a MatrixPrinter by ziping the
 * content provided by the wrapped {@link AbstractMatrixPrinter MatrixPrinter}.
 */
@Slf4j
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

		try (OutputStream fos = Files.newOutputStream(bz2OutputFile.toPath());
				BZip2CompressorOutputStream zipOutputStream = new BZip2CompressorOutputStream(fos);
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(zipOutputStream);
				OutputStreamWriter writer = new OutputStreamWriter(bufferedOutputStream)) {

			log.info("writing matrix file: " + outputFile.getAbsolutePath());
			writer.write(content.toString());


		} catch (Exception e) {
			log.warn(e.getMessage());
			System.out.println(e);
		}
	}

}
