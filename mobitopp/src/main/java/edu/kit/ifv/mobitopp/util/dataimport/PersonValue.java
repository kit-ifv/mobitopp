package edu.kit.ifv.mobitopp.util.dataimport;

import java.util.HashMap;

class PersonValue extends Value {

	private final String gender;
	private final String age;

	public PersonValue(int zoneId, String gender, String age, String amount) {
		super(zoneId, amount);
		this.gender = gender;
		this.age = age;
	}

	static PersonValue from(String[] line) {
		int zoneId = Integer.parseInt(line[0]);
		String gender = genderOf(line);
		String age = ageOf(line);
		String amount = line[4];
		return new PersonValue(zoneId, gender, age, amount);
	}

	private static String ageOf(String[] line) {
		return ages().get(line[3]);
	}

	private static HashMap<String, String> ages() {
		HashMap<String, String> ages = new HashMap<>();
		ages.put("1", "0-5");
		ages.put("2", "6-9");
		ages.put("3", "10-14");
		ages.put("4", "15-17");
		ages.put("5", "18-24");
		ages.put("6", "25-29");
		ages.put("7", "30-44");
		ages.put("8", "45-59");
		ages.put("9", "60-64");
		ages.put("10", "65-74");
		ages.put("11", "75-");
		return ages;
	}

	private static String genderOf(String[] line) {
		return "male".equals(line[2]) ? "M" : "F";
	}

	@Override
	protected String constraint() {
		return String.format("Age:%s:%s", gender, age);
	}
}
