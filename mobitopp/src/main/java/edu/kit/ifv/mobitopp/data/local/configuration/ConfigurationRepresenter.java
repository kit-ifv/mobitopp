package edu.kit.ifv.mobitopp.data.local.configuration;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

public class ConfigurationRepresenter extends Representer {

	public ConfigurationRepresenter(Tag tag, String timeSpanSeparator) {
		super();
		this.representers.put(TimeSpan.class, new RepresentTimeSpan(tag, timeSpanSeparator));
	}

	private class RepresentTimeSpan implements Represent {

		private final Tag tag;
		private final String timeSpanSeparator;

		public RepresentTimeSpan(Tag tag, String timeSpanSeparator) {
			super();
			this.tag = tag;
			this.timeSpanSeparator = timeSpanSeparator;
		}

		@Override
		public Node representData(Object data) {
			TimeSpan span = (TimeSpan) data;
			String value = span.getFrom() + " " + timeSpanSeparator + " " + span.getTo();
			return representScalar(tag, value);
		}

	}
}