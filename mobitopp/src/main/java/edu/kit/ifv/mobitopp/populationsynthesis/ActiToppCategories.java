package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.result.Category;

public class ActiToppCategories {

	public final Category person = person();
	public final Category trip = trip();

	private Category person() {
		List<String> header = new ArrayList<>();
		header.add("PersID");
		header.add("Jahr");
		header.add("PersNr");
		header.add("P0_10");
		header.add("kinder_u18");
		header.add("Alter	");
		header.add("Beruf");
		header.add("Sex");
		header.add("Raumtyp");
		header.add("PKWHH");
		return new Category("demanddataactiTopp_Person", header);
	}

	private Category trip() {
		List<String> header = new ArrayList<>();
		header.add("PersID");
		header.add("Jahr");
		header.add("PersNr");
		header.add("BERTAG");
		header.add("WOTAG");
		header.add("abzeit_woche");
		header.add("anzeit_woche");
		header.add("Dauer");
		header.add("Zweck");
		return new Category("demanddataactiTopp_Trip", header);
	}
}
