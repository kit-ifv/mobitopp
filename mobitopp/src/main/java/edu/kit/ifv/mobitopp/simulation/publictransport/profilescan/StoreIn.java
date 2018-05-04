package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Validity.always;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.Timetable;
import edu.kit.ifv.mobitopp.time.Time;

public class StoreIn implements Store {

	private final File output;
	private final Timetable timetable;
	private final EntrySplitter splitter;
	private AtomicInteger savedProfiles;

	public StoreIn(File output, Timetable timetable, EntrySplitter splitter) {
		super();
		this.output = output;
		this.timetable = timetable;
		this.splitter = splitter;
		savedProfiles = new AtomicInteger(0);
	}

	public static Store folder(File file, Timetable timetable, EntrySplitter splitter) {
		file.mkdirs();
		return new StoreIn(file, timetable, splitter);
	}

	@Override
	public void save(Profile profile) {
		List<Profile> parts = profile.split(splitter);
		for (Profile part : parts) {
			saveSingle(part);
		}
		int profiles = savedProfiles.incrementAndGet();
		System.out.println(LocalDateTime.now() + "Profiles: " + profiles);
	}

	private void saveSingle(Profile profile) {
		try (ProfileWriter stream = to(profile)) {
			profile.saveTo(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	ProfileWriter to(Profile profile) throws IOException {
		File file = fileFor(profile.target(), profile.validity());
		File parent = file.getParentFile();
		parent.mkdirs();
		return writer(file);
	}

	protected ProfileWriter writer(File file) throws IOException {
		return WriteBinary.to(file);
	}

	private File fileFor(Stop target, Validity validity) {
		return new File(output, nameFor(target, validity));
	}

	private static String nameFor(Stop target, Validity validity) {
		return validity.asFileName() + "/" +  target.id() + ".bin";
	}

	Profile newProfile(Stop target) {
		return new Profile(target);
	}

	ProfileReader from(Stop target) throws FileNotFoundException {
		File file = fileFor(target, always);
		return readFrom(file);
	}

	@Override
	public Profile profileTo(Stop target, Time time) {
		Profile profile = newProfile(target, time);
		try (ProfileReader stream = from(target, time)) {
			profile.loadFrom(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return profile;
	}

	ProfileReader from(Stop target, Time time) {
		File file = fileFor(target, splitter.validity(time));
		return readFrom(file);
	}

	private ProfileReader readFrom(File file) {
		if (file.exists()) {
			try {
				return reader(file, timetable);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Should never happen.", e);
			}
		}
		return ProfileReader.emptyReader();
	}

	protected ProfileReader reader(File file, Timetable timetable) throws FileNotFoundException {
		return ReadBinary.from(file, timetable);
	}

	Profile newProfile(Stop target, Time time) {
		return new Profile(target, splitter.validity(time));
	}

}
