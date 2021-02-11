package edu.kit.ifv.mobitopp.simulation;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.data.areatype.ZoneAreaType;
import edu.kit.ifv.mobitopp.util.collections.StreamUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TargetChoiceParameter {

	private Map<AreaType, Map<ActivityType, Map<Set<Mode>, Float>>> parameter = new HashMap<>();
	private final AreaTypeRepository areaTypeRepository;

	public TargetChoiceParameter(String filename, AreaTypeRepository areaTypeRepository) {
		super();
		this.areaTypeRepository = areaTypeRepository;
		parseConfigFile(filename);
	}

	private void parseConfigFile(String fileName)
	{
		File f = new File(fileName);
		if (!f.exists()) {
			log.error("FEHLER: Datei nicht da!  Aktuelles Verzeichnis " +
					f.getAbsolutePath());
		}

		try {
			String in = null;
			try (BufferedReader inp = new BufferedReader(new FileReader(f))) {
				while ((in = inp.readLine()) != null) {
					parseParameterData(in);
				}
			} catch (FileNotFoundException e) {
				warn(e, "TargetChoiceParameter: Datei wurde nicht gefunden : " + fileName, log);
			} catch (IOException e) {
				warn(e, " Datei konnte nicht geöffnet werden :" + e, log);
			}

			printParameter(this.parameter);

		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			System.exit(2);
		}

	}

	private void parseParameterData(String line_) {

		if (line_.indexOf("#") >= 0) {
			return;
		}

		StringTokenizer tokens = new StringTokenizer(line_, ";");

		if (tokens.countTokens() < 3) {
			return;
		}

		try {
			int type = Integer.parseInt(tokens.nextToken().trim());
			int activity = Integer.parseInt(tokens.nextToken().trim());

			Map<Set<Mode>,Float> map = new HashMap<Set<Mode>,Float>(4,1.0f);
			int nr = tokens.countTokens();

			for (int i = 0; i < nr; i++) {
				float val = Float.parseFloat(tokens.nextToken().trim());
				map.put(StandardChoiceSet.MODESET_VALUES.get(i),val);
			}

			addTypeAndActivity(type, activity, map);

		} catch (NumberFormatException nex) {
			StreamUtils.warn(nex, "Probleme mit der Zeile " + line_ + "  " + nex, log);
		}
	}


	private void addTypeAndActivity(int zoneType, int activity, Map<Set<Mode>,Float> values) {
		AreaType zoneKey = areaTypeRepository.getTypeForCode(zoneType);
		ActivityType activityKey = ActivityType.getTypeFromInt(activity);

		if (!this.parameter.containsKey(zoneKey)) {
			this.parameter.put(zoneKey, new HashMap<>() );
		}

		this.parameter.get(zoneKey).put(activityKey, values);
	}

	private static void printParameter(
		Map<AreaType, Map<ActivityType, Map<Set<Mode>, Float>>> parameter
	) {

		log.info("Targetparameter");

		for (AreaType zone : parameter.keySet()) {

			log.info("Type: " + zone.getTypeAsInt() + " " + zone.getTypeAsString());

			for (ActivityType act : parameter.get(zone).keySet()) {

				log.info("    Act: " + act.getTypeAsInt() + " " + act.getTypeAsString());

				String msg = ("     ");
				for (Set<Mode> ms : StandardChoiceSet.MODESET_VALUES ) {

					float val = parameter.get(zone).get(act).get(ms);

					msg += ("  " + ms + ": " + val);
				}

				log.info(msg);
			}
		}
	}

	public float getParameter(
		AreaType zoneType, 
		ActivityType attType, 
		Set<Mode> choiceSetForModes
	) {

		Map<ActivityType, Map<Set<Mode>,Float>> tmp = this.parameter.get(zoneType);

		if (tmp == null) {
			tmp =this.parameter.get(ZoneAreaType.DEFAULT);
		}

		return tmp.get(attType).get(choiceSetForModes);
	}

}
