package edu.kit.ifv.mobitopp.dataimport;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

public class AttractivitiesDataTest {

	private AttractivitiesData attractivityData;

	@BeforeEach
	public void initialise() throws URISyntaxException, IOException {
		attractivityData = new AttractivitiesData(content());
	}

	private StructuralData content() throws IOException {
		String content = "zoneId;Attractivity:Work;Attractivity:Business\r\n"
				+ "1;2745;2745" + System.lineSeparator()
				+ "2;1388;1388" + System.lineSeparator()
				+ "3;2872;2872" + System.lineSeparator();
		return new StructuralData(CsvFile.createFrom(new ByteArrayInputStream(content.getBytes())));
	}

	@Test
  public void attractivities() {
  	Map<Integer, Attractivities> build = attractivityData.build();

  	assertThat(build).containsEntry(1, createAttractivities(2745));
  	assertThat(build).containsEntry(2, createAttractivities(1388));
  	assertThat(build).containsEntry(3, createAttractivities(2872));
  }

	private Attractivities createAttractivities(int value) {
		Attractivities attractivities = new Attractivities();
		attractivities.addAttractivity(ActivityType.WORK, value);
		attractivities.addAttractivity(ActivityType.BUSINESS, value);
		attractivities.addAttractivity(ActivityType.EDUCATION, 0);
		attractivities.addAttractivity(ActivityType.SHOPPING, 0);
		attractivities.addAttractivity(ActivityType.LEISURE, 0);
		attractivities.addAttractivity(ActivityType.SERVICE, 0);
		return attractivities;
	}

}
