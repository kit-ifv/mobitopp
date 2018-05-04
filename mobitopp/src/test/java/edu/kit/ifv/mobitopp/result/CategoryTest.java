package edu.kit.ifv.mobitopp.result;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CategoryTest {

	@Test
	public void containsHeader() {
		String name = "name";
		List<String> columns = asList("column 1", "column 2");
		Category category = new Category(name, columns);
		
		String header = category.header();
		
		assertEquals("column 1;column 2;", header);
	}
	
	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(Category.class).withOnlyTheseFields("name").usingGetClass().verify();
	}
}
