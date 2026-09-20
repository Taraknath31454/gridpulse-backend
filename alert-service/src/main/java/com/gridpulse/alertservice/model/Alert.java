package com.gridpulse.alertservice.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "alerts")
public class Alert {

	@Id
	private String id;
	private String meterId;
	private String type;

	@Enumerated(EnumType.STRING)
	private AlertSeverity severity;

	private String description;

	@Enumerated(EnumType.STRING)
	private AlertStatus status;

	private Instant timestamp;
	private double threshold;
	private double currentValue;
	private String recommendedAction;

	public Alert() {
	}

	public Alert(String id, String meterId, String type, AlertSeverity severity, String description,
			AlertStatus status, Instant timestamp, double threshold, double currentValue, String recommendedAction) {
		this.id = id;
		this.meterId = meterId;
		this.type = type;
		this.severity = severity;
		this.description = description;
		this.status = status;
		this.timestamp = timestamp;
		this.threshold = threshold;
		this.currentValue = currentValue;
		this.recommendedAction = recommendedAction;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public AlertSeverity getSeverity() {
		return severity;
	}

	public void setSeverity(AlertSeverity severity) {
		this.severity = severity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AlertStatus getStatus() {
		return status;
	}

	public void setStatus(AlertStatus status) {
		this.status = status;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public double getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

	public String getRecommendedAction() {
		return recommendedAction;
	}

	public void setRecommendedAction(String recommendedAction) {
		this.recommendedAction = recommendedAction;
	}
}
