package edu.kit.ifv.mobitopp.util.dataexport;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import edu.kit.ifv.mobitopp.data.Matrix;
import edu.kit.ifv.mobitopp.data.ZoneId;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MatrixPrinter extends AbstractMatrixPrinter {

	private static final int defaultValuesPerLine = 10;
	private final int valuesPerLine;

	public MatrixPrinter(int valuesPerLine) {
		super();
		this.valuesPerLine = valuesPerLine;
	}

	public MatrixPrinter() {
		this(defaultValuesPerLine);
	}

	protected StringBuffer getContent(Matrix<? extends Number> matrix, String from, String to) {
		return header(matrix, from, to).append(dataToString(matrix));
	}

	protected void write(StringBuffer content, File outputFile) {
		try (FileWriter fw = new FileWriter(outputFile)) {
			log.info("writing matrix file: " + outputFile.getAbsolutePath());
			fw.write(content.toString());
			log.info("DONE.");
		} catch (Exception e) {
			throw warn(new RuntimeException(e), log);
		}
	}

	private StringBuffer header(Matrix<? extends Number> matrix, String from, String to) {

		String typ = "$V"; // Add number of decimals

		String factor = "1.00";
		String names = names(matrix);
		String number = Integer.toString(matrix.ids().size());

		StringBuffer buf = new StringBuffer();

		buf.append(typ);
		buf.append("\n* Von Bis\n");
		buf.append(from + " " + to);
		buf.append("\n* Faktor\n");
		buf.append(factor);
		buf.append("\n* Anzahl Netzobjekte\n");
		buf.append(number);
		buf.append("\n* Netzobjekt-Nummern\n");
		buf.append(names);
		buf.append("\r\n");
		buf.append("*\n");

		return buf;
	}

	private String names(Matrix<? extends Number> matrix) {

		List<ZoneId> matrixOids = new ArrayList<>(matrix.ids());

		String matrixIds = "";

		int cnt = 0;

		for (ZoneId id : matrixOids) {
			matrixIds += id.getExternalId() + " ";
			matrixIds += (++cnt % 10 == 0 ? "\r\n" : "");
		}

		return matrixIds;
	}

	protected StringBuffer dataToString(Matrix<? extends Number> matrix) {

		Collection<ZoneId> ids = matrix.ids();

		StringBuffer buf = new StringBuffer(2800000);

		for (ZoneId row : ids) {

			StringBuffer buf_line = new StringBuffer(28000);

			float total = 0.0f;

			int cnt = 0;

			for (ZoneId col : ids) {

				Number val = matrix.get(row, col);
				buf_line.append(String.format(Locale.US, "%1$6.3f", val.doubleValue())).append(" ");

				total += val.floatValue();

				if (++cnt % valuesPerLine == 0) {
					buf_line.append("\r\n");
				}
			}
			buf.append("* Obj " + row.getExternalId() + " Summe = "
					+ String.format(Locale.US, "%1$6.3f", total) + "\r\n");
			buf.append(buf_line.toString());
			buf.append("\r\n");
		}

		return buf;
	}

}
