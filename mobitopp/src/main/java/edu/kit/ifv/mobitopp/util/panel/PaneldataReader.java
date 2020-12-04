package edu.kit.ifv.mobitopp.util.panel;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.util.file.StreamContent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaneldataReader {

	private static final int defaultActivityRadiusMode = StandardMode.CAR.getCode();
	private static final float defaultDistanceWork = 0.0f;
	private static final int defaultAdditionalChildren = 0;
	private static final int defaultType = 0;
	private static final int defaultDomCode = 0;
	private static final int graduationUndefined = -1;
	private static final float defaultPoleDistance = 0.0f;
  private static final float defaultWeight = 1.0f;
	private final Map<String, String> missingColumns;
  public final Map<String,List<PaneldataInfo>> data;

	
	public PaneldataReader(File file) {
		super();
		missingColumns = new LinkedHashMap<>();
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
		printMissingColumns();
		return data;
	}

	private void printMissingColumns() {
    missingColumns
        .entrySet()
        .stream()
        .map(e -> e.getKey() + " column not available in population data, using " + e.getValue())
        .forEach(log::warn);
  }

  Reader readerFor(File file) throws IOException {
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

		log.info("--- ");

		for(String c: columnNames.keySet()) {
			log.info(columnNames.get(c) + ": " + c);
		}

		return columnNames;
	}

	private PaneldataInfo processLine(String line, Map<String,Integer> columnNames) {

		assert columnNames != null;

		PaneldataInfo info = new PaneldataInfo();

		String[] field = line.split(";");

		info.household.household_number 	= Long.parseLong(field[columnNames.get("id")]);
		info.household.year 							= Short.parseShort(field[columnNames.get("year")]);
		info.household.area_type 					= Integer.parseInt(field[columnNames.get("areatype")]);
		info.household.household_size 		= Integer.parseInt(field[columnNames.get("size")]);
		info.household.household_type 		= getIntegerOrDefault(columnNames, field, "type", defaultType);
		info.household.domcode 						= getIntegerOrDefault(columnNames, field, "hhtype", defaultDomCode);
		info.household.cars							= Integer.parseInt(field[columnNames.get("cars")]);
		info.household.weight           = getFloatOrDefault(columnNames, field, "weight", defaultWeight);
		info.person.person_number				= Integer.parseInt(field[columnNames.get("personnumber")]);
		info.person.sex 								= readGender(columnNames, field);
		info.person.graduation					= getIntegerOrDefault(columnNames, field, "graduation", graduationUndefined);
		info.person.birthYear 					= Integer.parseInt(field[columnNames.get("birthyear")]);
		info.person.employmentType 		= Integer.parseInt(field[columnNames.get("employmenttype")]);
		info.person.commutationTicket 	= field[columnNames.get("commuterticket")].trim().equals("1");
		info.person.fahrrad 						= field[columnNames.get("bicycle")].trim().equals("1");
		info.person.apkwverf 						= hasCarAvailable(columnNames, field);
		info.person.ppkwverf 						= hasPersonalCar(columnNames, field);
		
		info.person.licence 						= hasLicence(columnNames, field);
		
		info.household.additionalchildren	= additionalChildren(columnNames, field);
		
		info.household.additionalchildrenmaxage	= getIntegerOrDefault(columnNames, field, "notcontainedchildrenmaxage", defaultAdditionalChildren); 

		info.household.income = getIntegerOrDefault(columnNames, field, "hhincome", 0);
		info.household.income_class = getIntegerOrDefault(columnNames, field, "hhincome_class", 0);
		info.household.activity_radius_time = getFloatOrDefault(columnNames, field, "hhactivity_radius_time", 0.0f);
		info.household.activity_radius_mode = getIntegerOrDefault(columnNames, field, "hhactivity_radius_mode", defaultActivityRadiusMode);
		info.person.distanceWork = getDistanceWork(columnNames, field);
		info.person.distanceEducation = getFloatOrDefault(columnNames, field, "distance_education", 0.0f);
		info.person.income = getIntegerOrDefault(columnNames, field, "incomeperson", 0);
															
		info.person.pref_cardriver = getFloatOrDefault(columnNames, field, "pref_cardriver", 0.0f);
		info.person.pref_carpassenger = getFloatOrDefault(columnNames, field, "pref_cardriver", 0.0f);
		info.person.pref_walking = getFloatOrDefault(columnNames, field, "pref_walking", 0.0f);
		info.person.pref_cycling = getFloatOrDefault(columnNames, field, "pref_cycling", 0.0f);
		info.person.pref_publictransport = getFloatOrDefault(columnNames, field, "pref_publictransport", 0.0f);

		if (columnNames.containsKey("activitypattern")) {
			parseActivityPattern(columnNames, info, field);
		}

		return info;
	}

	private float getDistanceWork(Map<String, Integer> columnNames, String[] field) {
		String key = "distance_work";
		Optional<String> value = get(columnNames, field, key);
		if (value.isPresent()) {
			return get(value, defaultDistanceWork);
		}
		missingColumns.put(key, "poledistance");
		return getPoleDistance(columnNames, field);
	}

	private int getPoleDistance(Map<String, Integer> columnNames, String[] field) {
		return ((Float) getFloatOrDefault(columnNames, field, "poledistance", defaultPoleDistance))
				.intValue();
	}

	private void parseActivityPattern(
			Map<String, Integer> columnNames, PaneldataInfo info, String[] field) {
		int startPattern = columnNames.get("activitypattern");

		for (int i = startPattern; i < field.length; i += 4) {
			if ("\"".equals(field[i])) {
				break;
			}
			int purpose = java.lang.Integer.parseInt(field[i + 1]);
			if (purpose == 10) {
				purpose = 9;
			}

			info.activity_pattern
					.add(new ActivityOfPanelData(
							Integer.parseInt(field[i].replaceAll("\"", "")),
							ActivityType.getTypeFromInt(purpose), 
							Integer.parseInt(field[i + 2]),
							Integer.parseInt(field[i + 3])));
		}
	}
	
	private float getFloatOrDefault(
	    Map<String, Integer> columnNames, String[] field, String key, float defaultValue) {
		Optional<String> value = get(columnNames, field, key);
		if (!value.isPresent()) {
			missingColumns.put(key, String.valueOf(defaultValue));
		}
	  return get(value, defaultValue);
	}

	private Float get(Optional<String> value, float defaultValue) {
		return value.filter(s -> !s.isEmpty()).map(Float::parseFloat).orElse(defaultValue);
	}

  private int getIntegerOrDefault(
      Map<String, Integer> columnNames, String[] field, String key, int defaultValue) {
    Optional<String> value = get(columnNames, field, key);
    if (!value.isPresent()) {
    	missingColumns.put(key, String.valueOf(defaultValue));
    }
    return value.map(Integer::parseInt).orElse(defaultValue);
  }
  
  private Optional<String> get(Map<String, Integer> columnNames, String[] field, String key) {
  	if (columnNames.containsKey(key)) {
  		return Optional.of(field[columnNames.get(key)]);
  	}
  	return Optional.empty();
  }

  private int additionalChildren(Map<String, Integer> columnNames, String[] field) {
    if (columnNames.containsKey("notcontainedchildren")) {
      return Integer.parseInt(field[columnNames.get("notcontainedchildren")]);
    }
    missingColumns.put("notcontainedchildren", "children");
    if (columnNames.containsKey("children")) {
      return Integer.parseInt(field[columnNames.get("children")]);
    }
    missingColumns.put("children", String.valueOf(defaultAdditionalChildren));
    return defaultAdditionalChildren;
  }

  private boolean hasLicence(Map<String, Integer> columnNames, String[] field) {
    if (columnNames.containsKey("licence")) {
      return field[columnNames.get("licence")].trim().equals("1");
    }
    missingColumns.put("licence", "caravailable and personalcar");
    return hasCarAvailable(columnNames, field) || hasPersonalCar(columnNames, field);
  }

  private boolean hasCarAvailable(Map<String, Integer> columnNames, String[] field) {
    return getBooleanOrDefault(columnNames, field, "caravailable");
  }

  private boolean hasPersonalCar(Map<String, Integer> columnNames, String[] field) {
    return getBooleanOrDefault(columnNames, field, "personalcar");
  }

  private boolean getBooleanOrDefault(
      Map<String, Integer> columnNames, String[] field, String key) {
    if (columnNames.containsKey(key)) {
      return field[columnNames.get(key)].trim().equals("1");
    }
    missingColumns.put(key, String.valueOf(false));
    return false;
  }

  private int readGender(Map<String, Integer> columnNames, String[] field) {
    if (columnNames.containsKey("gender")) {
      return Integer.parseInt(field[columnNames.get("gender")]);
    }
    missingColumns.put("gender", "sex");
    return Integer.parseInt(field[columnNames.get("sex")]);
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
				
				if ( pinfo.household.year - pinfo.person.birthYear < 18) {
					minors++;
				}

			}


			if (info.household.household_size != infos.get(id).size() + info.household.additionalchildren) {
				log.warn(id + " " + 
						"size=" + info.household.household_size 
							+ ", children=" + info.household.additionalchildren 
							+ ", reported=" + infos.get(id).size() );

				invalid++;

				adjustSizeAndDomcode(info, infos.get(id).size());
			}
			
			int income = info.household.income > 0 ? info.household.income 
																			: info.person.income*info.household.household_size;

			float activityRadius = info.household.activity_radius_time;
			int activityRadiusMode = info.household.activity_radius_mode;
			HouseholdOfPanelData hh = new HouseholdOfPanelData(
																		new HouseholdOfPanelDataId(	
																																info.household.year,
																																info.household.household_number
																														),
																	info.household.area_type,
																	info.household.household_size,
																	info.household.household_type,
																	info.household.domcode,
																	infos.get(id).size(), // reporting persons
																	minors,
																	info.household.additionalchildren,
																	info.household.cars,
																	income,
																	info.household.income_class,
																	activityRadius,
																	activityRadiusMode,
																	info.household.weight
																);

			assert (info.household.household_size == infos.get(id).size() + info.household.additionalchildren);

			result.add(hh);

		}

		log.info(result.size() + " households read");
		log.info(invalid + " households corrected");

		return result;
	}


	private void adjustSizeAndDomcode(PaneldataInfo info, int reporting_persons) {

		int correctedSize = info.household.additionalchildren + reporting_persons;
		int correctedDomcode = domcode(correctedSize, info.household.cars);

			log.info("size: " +  info.household.household_size + "->" + correctedSize 
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
																	info.person.graduation,
																	info.person.birthYear,
																	info.household.year - info.person.birthYear,
																	info.person.employmentType,
																	info.person.distanceWork,
																	info.person.distanceEducation,
																	info.person.commutationTicket,
																	info.person.licence,
																	info.person.fahrrad,
																	info.person.ppkwverf,
																	info.person.apkwverf,
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

  public Map<String, String> missingColumns() {
    return Collections.unmodifiableMap(missingColumns);
  }

}
