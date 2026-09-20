package com.gridpulse.alertservice.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "anomalies")
public class Anomaly {

	@Id
	private String id;
	private String meterId;
	private String type;

	@Enumerated(EnumType.STRING)
	private AlertSeverity severity;

	private double expectedValue;
	private double actualValue;
	private double differencePercent;
	private double confidence;

	@Enumerated(EnumType.STRING)
	private AnomalyStatus status;

	private Instant timestamp;
	private String recommendedAction;

	public Anomaly() {
	}

	public Anomaly(String id, String meterId, String type, AlertSeverity severity, double expectedValue,
			double actualValue, double differencePercent, double confidence, AnomalyStatus status,
			Instant timestamp, String recommendedAction) {
		this.id = id;
		this.meterId = meterId;
		this.type = type;
		this.severity = severity;
		this.expectedValue = expectedValue;
		this.actualValue = actualValue;
		this.differencePercent = differencePercent;
		this.confidence = confidence;
		this.status = status;
		this.timestamp = timestamp;
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

	public double getExpectedValue() {
		return expectedValue;
	}

	public void setExpectedValue(double expectedValue) {
		this.expectedValue = expectedValue;
	}

	public double getActualValue() {
		return actualValue;
	}

	public void setActualValue(double actualValue) {
		this.actualValue = actualValue;
	}

	public double getDifferencePercent() {
		return differencePercent;
	}

	public void setDifferencePercent(double differencePercent) {
		this.differencePercent = differencePercent;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	public AnomalyStatus getStatus() {
		return status;
	}

	public void setStatus(AnomalyStatus status) {
		this.status = status;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public String getRecommendedAction() {
		return recommendedAction;
	}

	public void setRecommendedAction(String recommendedAction) {
		this.recommendedAction = recommendedAction;
	}
}
