package edu.kit.ifv.mobitopp.util.panel;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.util.file.StreamContent;


public class PaneldataReader {


	public final Map<String,List<PaneldataInfo>> data;

	
	public PaneldataReader(File file) {
		super();

		List<PaneldataInfo> input = readFile(file);
		this.data = groupByHousehold(input);
	}

	public List<HouseholdOfPanelData> readHouseholds() {

		return createHouseholds(this.data);
	}

	public List<PersonOfPanelData> readPersons() {

		return createPersons(data);
	}

	private List<PaneldataInfo> readFile(File file) 
	{

		List<PaneldataInfo> data = new ArrayList<PaneldataInfo>();

		Reader reader = null;

		try {
		 reader = readerFor(file);	
		}
		catch(IOException cause) {
			throw new UncheckedIOException("Could not open file '" + file.getName() + "'", cause);
		}

		LineNumberReader lnReader = new LineNumberReader(reader);

		String line = null;

		Map<String,Integer> columnNames = null;

		try {

			while (lnReader.ready()) {
			
				line = lnReader.readLine();
				if ( lnReader.getLineNumber() == 1) { 
					columnNames = this.processColumnNames(line);
				} else {
					assert columnNames != null;

					PaneldataInfo info = this.processLine(line, columnNames);

					data.add(info);
				}
			}
		} catch (IOException cause) {
			throw new UncheckedIOException("Could read file '" + file.getName() + "'", cause);
		}

		return data;
	}

	private Reader readerFor(File file) throws IOException {
		return new InputStreamReader(StreamContent.of(file));
	}

	private Map<String,Integer> processColumnNames(String header) {

		if (header.startsWith("#")) {
			header = header.substring(1);
		}

		String[] fields = header.split(";");

		Map<String,Integer> columnNames = new TreeMap<String,Integer>();

		for(int i=0; i<fields.length; i++) {	
			columnNames.put(fields[i].toLowerCase(), i);
		}

		System.out.println("--- ");

		for(String c: columnNames.keySet()) {
			System.out.println(columnNames.get(c) + ": " + c);
		}

		return columnNames;
	}

	private PaneldataInfo processLine(String line, Map<String,Integer> columnNames) {

		assert columnNames != null;

		PaneldataInfo info = new PaneldataInfo();

		String[] field = line.split(";");

		info.household.household_number 	= java.lang.Long.parseLong(field[columnNames.get("id")]);
		info.household.year 							= java.lang.Short.parseShort(field[columnNames.get("year")]);
		info.household.area_type 					= java.lang.Integer.parseInt(field[columnNames.get("areatype")]);
		info.household.household_size 		= java.lang.Integer.parseInt(field[columnNames.get("size")]);
		info.household.domcode 						= java.lang.Integer.parseInt(field[columnNames.get("hhtype")]);
		info.household.cars							= java.lang.Integer.parseInt(field[columnNames.get("cars")]);
		info.person.pole								= java.lang.Integer.parseInt(field[columnNames.get("pole")]);
		info.person.weight							= java.lang.Float.parseFloat(field[columnNames.get("weight")]);
		info.person.person_number				= java.lang.Integer.parseInt(field[columnNames.get("personnumber")]);
		info.person.sex 								= java.lang.Integer.parseInt(field[columnNames.get("sex")]);
		info.person.birth_year 					= java.lang.Integer.parseInt(field[columnNames.get("birthyear")]);
		info.person.employment_type 		= java.lang.Integer.parseInt(field[columnNames.get("employmenttype")]);
		info.person.pole_distance 			= java.lang.Integer.parseInt(field[columnNames.get("poledistance")]);
		info.person.commutation_ticket 	= field[columnNames.get("commuterticket")].trim().equals("1");
		info.person.fahrrad 						= field[columnNames.get("bicycle")].trim().equals("1");
		info.person.apkwverf 						= field[columnNames.get("caravailable")].trim().equals("1");
		info.person.ppkwverf 						= field[columnNames.get("personalcar")].trim().equals("1");
		info.person.relvmselbst 				= field[columnNames.get("mw4")].trim().equals("1");
		info.person.relvmoev 						= field[columnNames.get("mw5")].trim().equals("1");
		
		info.person.licence 						= columnNames.containsKey("licence")
																		? field[columnNames.get("licence")].trim().equals("1") 
																		: field[columnNames.get("caravailable")].trim().equals("1") || field[columnNames.get("personalcar")].trim().equals("1");
		
		info.household.additionalchildren	= columnNames.containsKey("notcontainedchildren")
															? java.lang.Integer.parseInt(field[columnNames.get("notcontainedchildren")])
															: (
																	columnNames.containsKey("children")
															? java.lang.Integer.parseInt(field[columnNames.get("children")]) : -1
																	);
		
		info.household.additionalchildrenmaxage	= columnNames.containsKey("notcontainedchildrenmaxage") 
															? java.lang.Integer.parseInt(field[columnNames.get("notcontainedchildrenmaxage")]) : -1;

		info.household.income	=  columnNames.containsKey("hhincome")
															? java.lang.Integer.parseInt(field[columnNames.get("hhincome")]) : 0;
		info.person.income = columnNames.containsKey("incomeperson")
															? java.lang.Integer.parseInt(field[columnNames.get("incomeperson")]) : 0;
															

		info.day 			= java.lang.Integer.parseInt(field[columnNames.get("day")]);
		info.month 			= java.lang.Integer.parseInt(field[columnNames.get("month")]);
		
		info.person.pref_cardriver = columnNames.containsKey("pref_cardriver")
															? java.lang.Float.parseFloat(field[columnNames.get("pref_cardriver")]) : 0.0f;
		info.person.pref_carpassenger = columnNames.containsKey("pref_cardriver")
															? java.lang.Float.parseFloat(field[columnNames.get("pref_carpassenger")]) : 0.0f;
		info.person.pref_walking = columnNames.containsKey("pref_walking")
															? java.lang.Float.parseFloat(field[columnNames.get("pref_walking")]) : 0.0f;
		info.person.pref_cycling = columnNames.containsKey("pref_cycling")
															? java.lang.Float.parseFloat(field[columnNames.get("pref_cycling")]) : 0.0f;
		info.person.pref_publictransport = columnNames.containsKey("pref_publictransport")
															? java.lang.Float.parseFloat(field[columnNames.get("pref_publictransport")]) : 0.0f;

		int startPattern = columnNames.get("activitypattern");

		for (int i=startPattern; i<field.length; i+=4) {

			int purpose = java.lang.Integer.parseInt(field[i+1]);
			if (purpose == 10) { purpose=9; }
			
			

			info.activity_pattern.add(
				new ActivityOfPanelData(
					java.lang.Integer.parseInt(field[i]),
					ActivityType.getTypeFromInt(purpose),
					java.lang.Integer.parseInt(field[i+2]),
					java.lang.Integer.parseInt(field[i+3])
				)
			);
		}


		return info;
	}

	private Map<String,List<PaneldataInfo>> groupByHousehold(List<PaneldataInfo> input) {
		List<String> hhIds = new ArrayList<String>();

		Map<String,List<PaneldataInfo>> infoByHousehold = new LinkedHashMap<String,List<PaneldataInfo>>();

		for (PaneldataInfo info : input) {
			String id = info.household.id();

			if (!hhIds.contains(id)) {
				hhIds.add(id);

				infoByHousehold.put(id,new ArrayList<PaneldataInfo>());
			}

			infoByHousehold.get(id).add(info);
		}


		return infoByHousehold;
	}

	private List<HouseholdOfPanelData> createHouseholds(Map<String,List<PaneldataInfo>> infos) {

		List<HouseholdOfPanelData> result = new ArrayList<HouseholdOfPanelData>();

		int invalid = 0;

		for (String id : infos.keySet()) {

			PaneldataInfo info =  infos.get(id).get(0);

			int minors = info.household.additionalchildren;

			for (PaneldataInfo pinfo : infos.get(id)) {
				
				if ( pinfo.household.year - pinfo.person.birth_year < 18) {
					minors++;
				}

			}


			if (info.household.household_size != infos.get(id).size() + info.household.additionalchildren) {
				System.out.println(id + " " + 
						"size=" + info.household.household_size 
							+ ", children=" + info.household.additionalchildren 
							+ ", reported=" + infos.get(id).size() );

				invalid++;

				adjustSizeAndDomcode(info, infos.get(id).size());
			}
			
			int income = info.household.income > 0 ? info.household.income 
																			: info.person.income*info.household.household_size;

			HouseholdOfPanelData hh = new HouseholdOfPanelData(
																		new HouseholdOfPanelDataId(	
																																info.household.year,
																																info.household.household_number
																														),
																	info.household.area_type,
																	info.household.household_size,
																	info.household.domcode,
																	infos.get(id).size(), // reporting persons
																	minors,
																	info.household.additionalchildren,
																	info.household.cars,
																	income
																);

			assert (info.household.household_size == infos.get(id).size() + info.household.additionalchildren);

			result.add(hh);

		}

		System.out.println(result.size() + " households read");
		System.out.println(invalid + " households corrected");

		return result;
	}


	private void adjustSizeAndDomcode(PaneldataInfo info, int reporting_persons) {

		int correctedSize = info.household.additionalchildren + reporting_persons;
		int correctedDomcode = domcode(correctedSize, info.household.cars);

			System.out.println("size: " +  info.household.household_size + "->" + correctedSize 
											+ " domcode: " + info.household.domcode + "->" + correctedDomcode);

		info.household.household_size = correctedSize;
		info.household.domcode = correctedDomcode;
	}

	private int domcode(int size, int cars) {

		assert size > 0;
		assert cars >= 0;

		switch(size) {
			case 1:
				switch(cars) {
					case 0:
						return 1;
					case 1:
						return 2;
					default: // >= 2
						return 3;
				}
			case 2:	
				switch(cars) {
					case 0:
						return 4;
					case 1:
						return 5;
					default: // >= 2
						return 6;
				}
			case 3:
				switch(cars) {
					case 0:
						return 7;
					case 1:
						return 8;
					default: // >= 2
						return 9;
				}
			default: // >= 4
				switch(cars) {
					case 0:
						return 10;
					case 1:
						return 11;
					default: // >= 2
						return 12;
				}
		}

	}	

	private List<PersonOfPanelData> createPersons(Map<String,List<PaneldataInfo>> infos) {

		List<PersonOfPanelData> result = new ArrayList<PersonOfPanelData>();

		for (String id : infos.keySet()) {

			int cnt=1;

			for (PaneldataInfo info : infos.get(id)) {

				int income = info.person.income > 0 ? info.person.income 
																						: info.household.income / info.household.household_size;

				PersonOfPanelData p  = new PersonOfPanelData(
																	new PersonOfPanelDataId(
																			new HouseholdOfPanelDataId(
																																info.household.year,
																																info.household.household_number
																															),
																		cnt++ 
																	),
																	info.person.sex,
																	info.person.birth_year,
																	info.household.year - info.person.birth_year,
																	info.person.employment_type,
																	info.person.pole_distance,
																	info.person.commutation_ticket,
																	info.person.licence,
																	info.person.fahrrad,
																	info.person.ppkwverf,
																	info.person.apkwverf,
																	info.person.weight,
																	income,
																	activitiesAsString(info.activity_pattern),
																	info.person.pref_cardriver,
																	info.person.pref_carpassenger,
																	info.person.pref_walking,
																	info.person.pref_cycling,
																	info.person.pref_publictransport
																);


				result.add(p);
			}
		}

		return result;
	}


	private String activitiesAsString(List<ActivityOfPanelData> activities) {
		
		if (activities.isEmpty()) {
				return "";
		}
		
		String result = "";

		for (ActivityOfPanelData activity : activities) {
			result += activity.asCSV() + ";";
		}

		return result.substring(0,result.lastIndexOf(";"));
	}

}
