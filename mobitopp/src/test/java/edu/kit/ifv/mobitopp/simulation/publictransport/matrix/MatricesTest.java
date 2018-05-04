package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.FloatMatrix;

public class MatricesTest {

	private static final int someHour = 0;
	private static final String serialized = "matrix";
	private BufferedWriter writer;
	private File file;
	private TravelTimes travelTimes;
	private Matrix matrix;

	@Before
	public void initialise() throws Exception {
		file = mock(File.class);
		writer = mock(BufferedWriter.class);
		travelTimes = mock(TravelTimes.class);
		matrix = mock(Matrix.class);

		when(travelTimes.hours()).thenReturn(asList(someHour));
		when(travelTimes.matrixIn(someHour)).thenReturn(matrix);
	}

	@Test
	public void convertToFloatMatrix() {
		matrices().matrix(someHour);

		verify(matrix).toFloatMatrix();
	}

	@Test
	public void writesEntryBetweenZones() throws Exception {
		Matrices matrices = matrices();

		matrices.saveTo(file);

		verify(writer).write(serialized);
		verify(writer).close();
	}

	private Matrices matrices() {
		return new Matrices(travelTimes) {

			@Override
			BufferedWriter write(String slice, File outputFolder) throws IOException {
				return writer;
			}

			@Override
			protected String serialize(FloatMatrix matrix) {
				return serialized;
			}
		};
	}
}
