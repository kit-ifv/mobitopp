package edu.kit.ifv.mobitopp.visum;


public class POICategories {

	private final VisumTable categories;

	public POICategories(VisumTable categories) {

		assert null != categories;
		assert categories.containsAttribute("NR");
		assert categories.containsAttribute("CODE");

		this.categories = categories;
	}



	public boolean containsCode(String code) {
		for (int i=0; i < categories.numberOfRows(); i++) {

			String val = categories.getValue(i, "CODE");

			if (val.equals(code)) { return true; }
		}

		return false;
	}

	public int numberByCode(String code) {

		for (int i=0; i < categories.numberOfRows(); i++) {

			String val = categories.getValue(i, "CODE");

			if (val.equals(code)) { 
				return Integer.parseInt(categories.getValue(i, "NR"));
			}
		}

		throw new AssertionError();
	}

	public boolean containsNumber(int number) {

		for (int i=0; i < categories.numberOfRows(); i++) {

			int val = Integer.parseInt(categories.getValue(i, "NR"));

			if (val == number) { return true; }
		}

		return false;

	}

}
