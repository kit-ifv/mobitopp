package edu.kit.ifv.mobitopp.util.dataimport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.areatype.AreaType;

public class Bbsr17RepositoryTest {

	private Bbsr17Repository repository;
	
	@Before
	public void initialise() {
		repository = new Bbsr17Repository();
	}

	@Test
	public void getDefault() {
		AreaType defaultType = repository.getDefault();
		
		assertThat(defaultType, is(equalTo(Bbsr17.defaultType)));
	}
	
	@Test
	public void getTypeForCode() {
		for (Bbsr17 type : Bbsr17.values()) {
			AreaType resolved = repository.getTypeForCode(type.getTypeAsInt());
			
			assertThat(resolved, is(equalTo(type)));
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void getTypeForMissingCode() {
		repository.getTypeForCode(Integer.MAX_VALUE);
	}
	
	@Test
	public void getTypeForName() {
		for (Bbsr17 type : Bbsr17.values()) {
			AreaType resolved = repository.getTypeForName(type.getTypeAsString());
			
			assertThat(resolved, is(equalTo(type)));
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void getTypeForMissingName() {
		repository.getTypeForName("Missing Area Type Name");
	}
}
