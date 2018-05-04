package edu.kit.ifv.mobitopp.simulation.emobility;

import java.io.Serializable;

import edu.kit.ifv.mobitopp.simulation.ChargingListener;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.time.Time;


public class ChargingFacility
  implements Serializable
{

	public static final long serialVersionUID = -8454533014527868654L;


	private final int id;
	private final Location location;
	private final Type type;
	private final ChargingPower power;
	private final int stationId;
	private ChargingListener chargingListener;

	private float requestedElectricity_kWh = 0.0f;

	private Time startOfCharging;
	private Time endOfCharging;

	private float totalElectricitySupplied_kWh = 0.0f;

	private State currentState = State.FREE;

	public enum State { FREE, OCCUPIED };
	public enum Type { PRIVATE, SEMIPUBLIC, PUBLIC };

	public ChargingFacility(
			int id, Location location, Type type, ChargingPower power) {
		this(id, id, location, type, power);
	}

	public ChargingFacility(
			int id, int stationId, Location location, Type type, ChargingPower power) {
		this.id = id;
		this.location = location;
		this.type = type;
		this.power = power;
		this.stationId = stationId;
	}

	public float chargingPowerInKW() {
		return power.inKw();
	}

	public float chargingKWperMinute() {
		return chargingPowerInKW() / 60;
	}

	public Time timeTillFull(float requestedElectricity_kWh, Time time) {
		int minutesTillFull = Math.round(requestedElectricity_kWh / chargingKWperMinute());
		return time.plusMinutes(minutesTillFull);
	}

	public void startCharging(Time currentTime, float requestedElectricity_kWh) {

		this.currentState = State.OCCUPIED;
		this.startOfCharging = currentTime;
		this.requestedElectricity_kWh = requestedElectricity_kWh;
	}

	public float stopCharging(ElectricCar car, Time currentTime) {

		this.endOfCharging = currentTime;

		float suppliedElectricity_kWh = Math.min( 
																		chargingDuration() * chargingKWperMinute(),
																		this.requestedElectricity_kWh
																	);

		assert suppliedElectricity_kWh <= this.requestedElectricity_kWh;


		int availableChargingDuration = chargingDuration();
		int chargingDurationMinutes = (int) Math.min(availableChargingDuration, 
																						requestedElectricity_kWh / chargingKWperMinute()
																					);

		float oldBattery = car.currentBatteryLevel();
		float newBattery = oldBattery + (1.0f-oldBattery) * suppliedElectricity_kWh/this.requestedElectricity_kWh;

		this.currentState = State.FREE;

		this.totalElectricitySupplied_kWh += suppliedElectricity_kWh;

		log(car, suppliedElectricity_kWh, availableChargingDuration, chargingDurationMinutes,
				oldBattery, newBattery);

		return suppliedElectricity_kWh;
	}

	private int chargingDuration() {
		return Math.toIntExact(endOfCharging.differenceTo(startOfCharging).toMinutes());
	}

	void log(
			ElectricCar car, float suppliedElectricity_kWh, int availableChargingDuration,
			int chargingDurationMinutes, float oldBattery, float newBattery) {
		if (null == chargingListener) {
			System.out.println("No charging listener/writer assigned.");
			return;
		}
		chargingListener.stopCharging(car, this.startOfCharging, availableChargingDuration,
				chargingDurationMinutes, oldBattery, newBattery, suppliedElectricity_kWh, this.id,
				type.toString(), this.totalElectricitySupplied_kWh, chargingPowerInKW(), stationId);
	}

	public boolean isFree() {
		return this.currentState == State.FREE;
	}

	public void register(ChargingListener listener) {
		chargingListener = listener;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((power == null) ? 0 : power.hashCode());
		result = prime * result + stationId;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ChargingFacility other = (ChargingFacility) obj;
		if (id != other.id) {
			return false;
		}
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (power == null) {
			if (other.power != null) {
				return false;
			}
		} else if (!power.equals(other.power)) {
			return false;
		}
		if (stationId != other.stationId) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ChargingFacility [id=" + id + ", location=" + location + ", type=" + type + ", power="
				+ power + ", stationId=" + stationId + "]";
	}

}
