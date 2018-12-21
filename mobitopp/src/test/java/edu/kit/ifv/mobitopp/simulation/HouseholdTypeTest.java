package edu.kit.ifv.mobitopp.simulation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.simulation.person.PersonForDemand;


public class HouseholdTypeTest {
	
	Person personSingle;
	
	Person person1Couple;
	Person person2Couple;
	
	Person person1Multiadult;
	Person person2Multiadult;
	Person person3Multiadult;
	
	Person person1Multigeneration;
	Person person2Multigeneration;
	
	Person adultWithKids0to7;
	Person kid1WithKids0to7;
	Person kid2WithKids0to7;
	
	Person adult1WithKids8to12;
	Person adult2WithKids8to12;
	Person kid1WithKids8to12;
	Person kid2WithKids8to12;
	
	Person adult1WithKids13plus;
	Person adult2WithKids13plus;
	Person kidWithKids13plus;
	Person adultkidWithKids13plus;
	
	Household single;
	Household couple;
	Household multiadult;
	Household multigeneration;
	Household withKids0to7;
	Household withKids8to12;
	Household withKids13plus;
	
	@Before
	public void setUp() {
		
		single = new HouseholdForDemand(null, 1, 0, null, null, 0, 0, 0, false);
		couple = new HouseholdForDemand(null, 2, 0, null, null, 0, 0, 0, false);
		multiadult = new HouseholdForDemand(null, 3, 0, null, null, 0, 0, 0, false);
		multigeneration = new HouseholdForDemand(null, 2, 0, null, null, 0, 0, 0, false);
		withKids0to7 = new HouseholdForDemand(null, 3, 0, null, null, 0, 0, 0, false);
		withKids8to12 = new HouseholdForDemand(null, 4, 0, null, null, 0, 0, 0, false);
		withKids13plus = new HouseholdForDemand(null, 4, 0, null, null, 0, 0, 0, false);
		
		personSingle = new PersonForDemand(null, single, 20, null, null, 0, false, false, false, false, false, null, null, null, null);
		single.addPerson(personSingle);
		
		person1Couple = new PersonForDemand(null, couple, 20, null, null, 0, false, false, false, false, false, null, null, null, null);
		person2Couple = new PersonForDemand(null, couple, 22, null, null, 0, false, false, false, false, false, null, null, null, null);
		couple.addPerson(person1Couple);
		couple.addPerson(person2Couple);
		
		person1Multiadult = new PersonForDemand(null, multiadult, 20, null, null, 0, false, false, false, false, false, null, null, null, null);
		person2Multiadult = new PersonForDemand(null, multiadult, 25, null, null, 0, false, false, false, false, false, null, null, null, null);
		person3Multiadult = new PersonForDemand(null, multiadult, 30, null, null, 0, false, false, false, false, false, null, null, null, null);
		multiadult.addPerson(person1Multiadult);
		multiadult.addPerson(person2Multiadult);
		multiadult.addPerson(person3Multiadult);
		
		person1Multigeneration = new PersonForDemand(null, multigeneration, 20, null, null, 0, false, false, false, false, false, null, null, null, null);
		person2Multigeneration = new PersonForDemand(null, multigeneration, 40, null, null, 0, false, false, false, false, false, null, null, null, null);
		multigeneration.addPerson(person1Multigeneration);
		multigeneration.addPerson(person2Multigeneration);
		
		adultWithKids0to7 = new PersonForDemand(null, withKids0to7, 30, null, null, 0, false, false, false, false, false, null, null, null, null);
		kid1WithKids0to7 = new PersonForDemand(null, withKids0to7, 10, null, null, 0, false, false, false, false, false, null, null, null, null);
		kid2WithKids0to7 = new PersonForDemand(null, withKids0to7, 5, null, null, 0, false, false, false, false, false, null, null, null, null);
		withKids0to7.addPerson(adultWithKids0to7);
		withKids0to7.addPerson(kid1WithKids0to7);
		withKids0to7.addPerson(kid2WithKids0to7);
	
		adult1WithKids8to12 = new PersonForDemand(null, withKids8to12, 40, null, null, 0, false, false, false, false, false, null, null, null, null);
		adult2WithKids8to12 = new PersonForDemand(null, withKids8to12, 35, null, null, 0, false, false, false, false, false, null, null, null, null);
		kid1WithKids8to12 = new PersonForDemand(null, withKids8to12, 12, null, null, 0, false, false, false, false, false, null, null, null, null);
		kid2WithKids8to12 = new PersonForDemand(null, withKids8to12, 10, null, null, 0, false, false, false, false, false, null, null, null, null);
		withKids8to12.addPerson(adult1WithKids8to12);
		withKids8to12.addPerson(adult2WithKids8to12);
		withKids8to12.addPerson(kid1WithKids8to12);
		withKids8to12.addPerson(kid2WithKids8to12);
	
		adult1WithKids13plus = new PersonForDemand(null, withKids13plus, 50, null, null, 0, false, false, false, false, false, null, null, null, null);
		adult2WithKids13plus = new PersonForDemand(null, withKids13plus, 45, null, null, 0, false, false, false, false, false, null, null, null, null);
		kidWithKids13plus = new PersonForDemand(null, withKids13plus, 13, null, null, 0, false, false, false, false, false, null, null, null, null);
		adultkidWithKids13plus = new PersonForDemand(null, withKids13plus, 19, null, null, 0, false, false, false, false, false, null, null, null, null);
		withKids13plus.addPerson(adult1WithKids13plus);
		withKids13plus.addPerson(adult2WithKids13plus);
		withKids13plus.addPerson(kidWithKids13plus);
		withKids13plus.addPerson(adultkidWithKids13plus);
	}
	
	@Test
	public void testSingle() {
		assertEquals(HouseholdType.SINGLE, HouseholdType.type(single));
	}
	
	@Test
	public void testCouple() {
		assertEquals(HouseholdType.COUPLE, HouseholdType.type(couple));
	}
	
	@Test
	public void testMultiadult() {
		assertEquals(HouseholdType.MULTIADULT, HouseholdType.type(multiadult));
	}
	
	@Test
	public void testMultigeneration() {
		assertEquals(HouseholdType.MULTIADULT, HouseholdType.type(multigeneration));
	}
	
	@Test
	public void testwithKids0to7() {
		assertEquals(HouseholdType.KIDS0TO7, HouseholdType.type(withKids0to7));
	}
	
	@Test
	public void testWithKids8to12() {
		assertEquals(HouseholdType.KIDS8TO12, HouseholdType.type(withKids8to12));
	}
	
	@Test
	public void testWithKids13plus() {
		assertEquals(HouseholdType.KIDS13PLUS, HouseholdType.type(withKids13plus));
	}

}
