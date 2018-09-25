package edu.kit.ifv.mobitopp.dataimport;

import java.util.List;

import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;

public class StructuralData {

	private static final int defaultValue = 0;

	private final CsvFile structuralData;
	private int index;
	private final AreaTypeRepository areaTypeRepository;

	public StructuralData(CsvFile structuralData, AreaTypeRepository areaTypeRepository) {
		super();
		this.areaTypeRepository = areaTypeRepository;
		this.structuralData = structuralData;
		resetIndex();
	}

	public List<String> getAttributes() {
		return structuralData.getAttributes();
	}

	public int currentZone() {
		return Integer.parseInt(getValue("ID"));
	}

	public String getValue(String key) {
		return structuralData.getValue(index, key);
	}

	public boolean hasValue(String key) {
		return !getValue(key).isEmpty();
	}

	public int valueOrDefault(String key) {
		String test = getValue(key);
		if (test.isEmpty()) {
			return defaultValue;
		}
		return Math.toIntExact(Math.round(Double.parseDouble(test)));
	}

	public ZoneClassificationType currentClassification() {
		String classification = getValue("Outlyingarea");
		if (Integer.valueOf(classification) > 0) {
			return ZoneClassificationType.outlyingArea;
		}
		return ZoneClassificationType.areaOfInvestigation;
	}

	public AreaType currentZoneAreaType() {
		String areaType = getValue("AreaType");
		try {
			int code = Integer.parseInt(areaType);
			return areaTypeRepository.getTypeForCode(code);
		} catch (NumberFormatException cause) {
			return areaType.isEmpty() ? areaTypeRepository.getDefault()
					: areaTypeRepository.getTypeForName(areaType);
		}
	}

	public void next() {
		index++;
	}

	public void resetIndex() {
		index = 0;
	}

	public boolean hasNext() {
		return index < structuralData.getLength();
	}

}
