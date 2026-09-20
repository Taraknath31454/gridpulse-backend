package com.gridpulse.usageservice.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usage_readings")
public class Usage {

	@Id
	private String id;
	private String meterId;
	private Instant timestamp;
	private double powerKw;
	private double energyKwh;
	private double voltage;
	private double frequency;
	private double powerFactor;

	public Usage() {
	}

	public Usage(String id, String meterId, Instant timestamp, double powerKw, double energyKwh,
			double voltage, double frequency, double powerFactor) {
		this.id = id;
		this.meterId = meterId;
		this.timestamp = timestamp;
		this.powerKw = powerKw;
		this.energyKwh = energyKwh;
		this.voltage = voltage;
		this.frequency = frequency;
		this.powerFactor = powerFactor;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMeterId() {
		return meterId;
	}

	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public double getPowerKw() {
		return powerKw;
	}

	public void setPowerKw(double powerKw) {
		this.powerKw = powerKw;
	}

	public double getEnergyKwh() {
		return energyKwh;
	}

	public void setEnergyKwh(double energyKwh) {
		this.energyKwh = energyKwh;
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

	public double getPowerFactor() {
		return powerFactor;
	}

	public void setPowerFactor(double powerFactor) {
		this.powerFactor = powerFactor;
	}
}
