package com.gridpulse.meterservice.meter;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "meters")
public class Meter {

	@Id
	private String id;
	private String deviceName;
	private String facility;
	private String zone;

	@Enumerated(EnumType.STRING)
	private MeterStatus status;

	private double currentLoad;
	private double voltage;
	private double frequency;

	@Enumerated(EnumType.STRING)
	private MeterHealth health;

	private Instant lastCommunication;

	public Meter() {
	}

	public Meter(String id, String deviceName, String facility, String zone, MeterStatus status,
			double currentLoad, double voltage, double frequency, MeterHealth health, Instant lastCommunication) {
		this.id = id;
		this.deviceName = deviceName;
		this.facility = facility;
		this.zone = zone;
		this.status = status;
		this.currentLoad = currentLoad;
		this.voltage = voltage;
		this.frequency = frequency;
		this.health = health;
		this.lastCommunication = lastCommunication;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public MeterStatus getStatus() {
		return status;
	}

	public void setStatus(MeterStatus status) {
		this.status = status;
	}

	public double getCurrentLoad() {
		return currentLoad;
	}

	public void setCurrentLoad(double currentLoad) {
		this.currentLoad = currentLoad;
	}

	public double getVoltage() {
		return voltage;
	}

	public void setVoltage(double voltage) {
		this.voltage = voltage;
	}

	public double getFrequency() {
		return frequency;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	public MeterHealth getHealth() {
		return health;
	}

	public void setHealth(MeterHealth health) {
		this.health = health;
	}

	public Instant getLastCommunication() {
		return lastCommunication;
	}

	public void setLastCommunication(Instant lastCommunication) {
		this.lastCommunication = lastCommunication;
	}
}
