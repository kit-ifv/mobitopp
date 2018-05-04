package edu.kit.ifv.mobitopp.util.collections;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class CollectionsHelperTest {

	@Test
	public void mergeLists() {
		Object anObject = new Object();
		Object otherObject = new Object();
		List<Object> first = asList(anObject);
		List<Object> second = asList(otherObject);

		List<Object> merged = CollectionsHelper.mergeLists().apply(first, second);
		
		assertThat(merged, contains(anObject, otherObject));
	}
}
