package edu.kit.ifv.mobitopp.dataimport;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.ZoneAreaType;

public class StructuralDataTest {

	private StructuralData demographyData;
	private StructuralData attractivityData;

	@Before
	public void initialise() throws URISyntaxException {
		demographyData = Example.demographyData();
		attractivityData = Example.attractivityData();
	}

	@Test
	public void getValue() {
		String first = demographyData.getValue("NAME");
		demographyData.next();
		String second = demographyData.getValue("NAME");
		demographyData.next();
		String third = demographyData.getValue("NAME");

		assertEquals("LB_Affalterbach (G)", first);
		assertEquals("LB_Asperg_West", second);
		assertEquals("LB_Asperg_Ost (G)", third);
	}

	@Test
	public void valueOrDefault() {
		int defaultValue = demographyData.valueOrDefault("Rundweg");
		demographyData.next();
		int existingValue = demographyData.valueOrDefault("Rundweg");

		assertEquals(0, defaultValue);
		assertEquals(8044, existingValue);
	}

	@Test
	public void classification() {
		ZoneClassificationType insideArea = attractivityData.currentClassification();
		attractivityData.next();
		ZoneClassificationType outsideArea = attractivityData.currentClassification();

		assertThat(insideArea, is(equalTo(ZoneClassificationType.areaOfInvestigation)));
		assertThat(outsideArea, is(equalTo(ZoneClassificationType.outlyingArea)));
	}

	@Test
	public void zoneAreaType() {
		AreaType first = attractivityData.currentZoneAreaType();
		attractivityData.next();
		AreaType second = attractivityData.currentZoneAreaType();
		attractivityData.next();
		AreaType third = attractivityData.currentZoneAreaType();

		assertThat(first, is(equalTo(ZoneAreaType.CITYOUTSKIRT)));
		assertThat(second, is(equalTo(ZoneAreaType.CONURBATION)));
		assertThat(third, is(equalTo(ZoneAreaType.DEFAULT)));
	}

	@Test
	public void zone() {
		int first = demographyData.currentZone();
		demographyData.next();
		int second = demographyData.currentZone();
		demographyData.next();
		int third = demographyData.currentZone();

		assertThat(first, is(equalTo(1)));
		assertThat(second, is(equalTo(2)));
		assertThat(third, is(equalTo(3)));
	}

	@Test
	public void hasNext() {
		boolean firstExists = demographyData.hasNext();
		demographyData.next();
		boolean secondExists = demographyData.hasNext();
		demographyData.next();
		boolean thirdExists = demographyData.hasNext();
		demographyData.next();
		boolean hasNoNext = demographyData.hasNext();

		assertTrue(firstExists);
		assertTrue(secondExists);
		assertTrue(thirdExists);
		assertFalse(hasNoNext);
	}

	@Test
	public void hasValue() {
		boolean hasNameValue = demographyData.hasValue("NAME");
		boolean hasNoPrivateVisitValue = demographyData.hasValue("Rundweg");

		assertTrue(hasNameValue);
		assertFalse(hasNoPrivateVisitValue);
	}

	@Test
	public void getAttributes() {
		List<String> attributes = demographyData.getAttributes();

		assertThat(attributes, contains("ID", "NAME", "Center:X", "Center:Y", "Einwohner in der Zone",
				"Age:M:0-5", "Age:M:6-9", "Age:M:10-15", "Age:M:16-18", "Age:M:19-24", "Age:M:25-29",
				"Age:M:30-44", "Age:M:45-59", "Age:M:60-64", "Age:M:65-74", "Age:M:75-", "Age:F:0-5",
				"Age:F:6-9", "Age:F:10-15", "Age:F:16-18", "Age:F:19-24", "Age:F:25-29", "Age:F:30-44",
				"Age:F:45-59", "Age:F:60-64", "Age:F:65-74", "Age:F:75-", "HHTyp:1", "HHTyp:2", "HHTyp:3",
				"HHTyp:4", "HHTyp:5", "HHTyp:6", "HHTyp:7", "HHTyp:8", "HHTyp:9", "HHTyp:10", "HHTyp:11",
				"HHTyp:12", "Job:FullTime", "Job:PartTime", "Job:None", "Job:Education_tertiary",
				"Job:Education_secondary", "Job:Education_primary", "Job:Education_occup", "Job:Retired",
				"Job:Infant", "Arbeit qualifiziert", "Arbeit einfach", "Arbeit selbst�ndig",
				"Arbeit Teilzeit", "t�gl. Bedarf", "sonst. Bedarf", "Erledigung", "Besuche, Fortb.",
				"Restaurant, Kultur", "Sport, Gr�nanl.", "Bringen/Holen", "Grundschule", "weiterf. Schule",
				"Universit�t", "Berufsschule", "Rundweg"));
	}
}
