package edu.kit.ifv.mobitopp.simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TargetChoiceParameterCost {


	private final Map<ActivityType,Float> time = new HashMap<ActivityType,Float>();
	private final Map<ActivityType,Float> cost = new HashMap<ActivityType,Float>();
	private final Map<ActivityType,Float> opportunity = new HashMap<ActivityType,Float>();



	public TargetChoiceParameterCost(String filename) {
		readFile(filename);
		createAggregateOpportunities();
	}



	private void readFile(String fileName)
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
					parseLine(in);
				}
			} catch (FileNotFoundException e) {
				log.error("TargetChoiceParameterCost: Datei wurde nicht gefunden : " + fileName);
			} catch (IOException e) {
				log.error(" Datei konnte nicht geöffnet werden :" + e);
			}

			printParameter();

		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(2);
		}

	}

	private void parseLine(String line_) {

		if (line_.indexOf("#") >= 0) {
			return;
		}

		StringTokenizer tokens = new StringTokenizer(line_, ";");

		if (tokens.countTokens() != 4) {
			log.error("Probleme mit der Zeile " + line_ );
			return;
		}

		try {
			ActivityType activity = ActivityType.getTypeFromInt(Integer.parseInt(tokens.nextToken().trim()));

			float opportunity = Float.parseFloat(tokens.nextToken().trim());
			float time = Float.parseFloat(tokens.nextToken().trim());
			float cost = Float.parseFloat(tokens.nextToken().trim());

			this.opportunity.put(activity,opportunity);
			this.cost.put(activity,cost);
			this.time.put(activity,time);


		} catch (NumberFormatException nex) {
			log.error("Probleme mit der Zeile " + line_ + "  " + nex);
		}
	}

	private void printParameter()
	{
		log.info("Targetparameter");

		for (ActivityType activity : this.opportunity.keySet()) {

			log.info(activity.getTypeAsInt() + ": " 
																	+ this.opportunity.get(activity) + " "
																	+ this.time.get(activity) + " "
																	+ this.cost.get(activity)
												);


		}


		for (ActivityType activity : this.opportunity.keySet()) {

			log.info(activity.getTypeAsInt() + ": " 
																	+ getParameterOpportunity(activity) + " "
																	+ getParameterTime(activity) + " "
																	+ getParameterCost(activity)
												);
		}
	}

	public float getParameterCost(ActivityType activity) { 
		return this.cost.get(activity);
	}

	public float getParameterTime(ActivityType activity) { 
		return this.time.get(activity);
	}

	public float getParameterOpportunity(ActivityType activity) { 
		assert this.opportunity != null;

		assert activity != null;

		assert this.opportunity.containsKey(activity) : activity;

		float val = this.opportunity.get(activity);

		return val;
	}

	private void createAggregateOpportunities() {

		if (!this.opportunity.containsKey(ActivityType.LEISURE)) {

			assert this.opportunity.containsKey(ActivityType.LEISURE_INDOOR);
			assert this.opportunity.containsKey(ActivityType.LEISURE_OUTDOOR);
			assert this.opportunity.containsKey(ActivityType.LEISURE_OTHER);

			float leisure = this.opportunity.get(ActivityType.LEISURE_INDOOR)
										+ this.opportunity.get(ActivityType.LEISURE_OUTDOOR)
										+ this.opportunity.get(ActivityType.LEISURE_OTHER);

			this.opportunity.put(ActivityType.LEISURE,leisure);
		}

		if (!this.opportunity.containsKey(ActivityType.SHOPPING)) {

			assert this.opportunity.containsKey(ActivityType.SHOPPING_DAILY);
			assert this.opportunity.containsKey(ActivityType.SHOPPING_OTHER);

			float shopping = this.opportunity.get(ActivityType.SHOPPING_DAILY)
											+ this.opportunity.get(ActivityType.SHOPPING_OTHER);

			this.opportunity.put(ActivityType.SHOPPING,shopping);
		}

		if (!this.time.containsKey(ActivityType.LEISURE)) {
			this.time.put(ActivityType.LEISURE,this.time.get(ActivityType.LEISURE_INDOOR));
		}
		if (!this.time.containsKey(ActivityType.SHOPPING)) {
			this.time.put(ActivityType.SHOPPING,this.time.get(ActivityType.SHOPPING_DAILY));
		}

		if (!this.cost.containsKey(ActivityType.LEISURE)) {
			this.cost.put(ActivityType.LEISURE,this.cost.get(ActivityType.LEISURE_INDOOR));
		}
		if (!this.cost.containsKey(ActivityType.SHOPPING)) {
			this.cost.put(ActivityType.SHOPPING,this.cost.get(ActivityType.SHOPPING_DAILY));
		}
	}
}
