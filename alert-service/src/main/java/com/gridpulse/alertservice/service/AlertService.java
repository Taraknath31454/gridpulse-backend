package com.gridpulse.alertservice.service;

import java.time.Instant;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.gridpulse.alertservice.client.UsageClient;
import com.gridpulse.alertservice.model.Alert;
import com.gridpulse.alertservice.model.AlertEvidence;
import com.gridpulse.alertservice.model.AlertSeverity;
import com.gridpulse.alertservice.model.AlertStatus;
import com.gridpulse.alertservice.model.Anomaly;
import com.gridpulse.alertservice.model.AnomalyStatus;
import com.gridpulse.alertservice.model.UsageSummaryDto;
import com.gridpulse.alertservice.repository.AlertRepository;
import com.gridpulse.alertservice.repository.AnomalyRepository;

@Service
public class AlertService implements CommandLineRunner {

	private final AlertRepository alertRepository;
	private final AnomalyRepository anomalyRepository;
	private final UsageClient usageClient;

	public AlertService(AlertRepository alertRepository, AnomalyRepository anomalyRepository, UsageClient usageClient) {
		this.alertRepository = alertRepository;
		this.anomalyRepository = anomalyRepository;
		this.usageClient = usageClient;
	}

	@Override
	public void run(String... args) {
		if (alertRepository.count() == 0) {
			alertRepository.saveAll(List.of(
					new Alert("ALT-1001", "MTR-1002", "VOLTAGE_SAG", AlertSeverity.HIGH,
							"Voltage dropped below acceptable tolerance (227.9V < 230V nominal threshold)",
							AlertStatus.OPEN, Instant.parse("2026-09-20T12:10:00Z"), 230.0, 227.9,
							"Inspect tap changer and secondary transformer loading on Industrial Zone feeder."),
					new Alert("ALT-1002", "MTR-1002", "OVERLOAD_WARNING", AlertSeverity.MEDIUM,
							"Assembly Line load peak exceeded 100kW operational threshold",
							AlertStatus.OPEN, Instant.parse("2026-09-20T12:10:00Z"), 100.0, 105.6,
							"Balance line loads or stagger heavy machinery start cycles."),
					new Alert("ALT-1003", "MTR-1004", "COMMUNICATION_LOSS", AlertSeverity.CRITICAL,
							"Cold Storage meter stopped transmitting telemetry (0kW reported)",
							AlertStatus.INVESTIGATING, Instant.parse("2026-09-20T11:58:00Z"), 1.0, 0.0,
							"Dispatch field technician to check meter gateway power supply and antenna."),
					new Alert("ALT-1004", "MTR-1001", "FREQUENCY_FLUCTUATION", AlertSeverity.LOW,
							"Substation frequency fluctuated to 49.98Hz",
							AlertStatus.ACKNOWLEDGED, Instant.parse("2026-09-20T12:05:00Z"), 50.0, 49.98,
							"Monitor grid tie-in frequency stability."),
					new Alert("ALT-1005", "MTR-1003", "POWER_FACTOR_WARNING", AlertSeverity.LOW,
							"Power factor transient dipped to 0.94 before recovering",
							AlertStatus.RESOLVED, Instant.parse("2026-09-20T11:45:00Z"), 0.95, 0.94,
							"Engage capacitor bank if inductive load increases.")
			));
		}

		if (anomalyRepository.count() == 0) {
			anomalyRepository.saveAll(List.of(
					new Anomaly("ANM-1001", "MTR-1002", "LOAD_SPIKE_ANOMALY", AlertSeverity.HIGH,
							78.5, 105.6, 34.52, 0.94, AnomalyStatus.OPEN,
							Instant.parse("2026-09-20T12:10:00Z"),
							"Check for unscheduled high-draw motor operation in Industrial Zone."),
					new Anomaly("ANM-1002", "MTR-1004", "ZERO_CONSUMPTION_ANOMALY", AlertSeverity.CRITICAL,
							15.2, 0.0, 100.0, 0.99, AnomalyStatus.INVESTIGATING,
							Instant.parse("2026-09-20T11:55:00Z"),
							"Investigate potential telemetry blackout or distribution fault."),
					new Anomaly("ANM-1003", "MTR-1001", "OFF_PEAK_SURGE", AlertSeverity.MEDIUM,
							140.0, 188.0, 34.29, 0.88, AnomalyStatus.OPEN,
							Instant.parse("2026-09-20T12:10:00Z"),
							"Verify scheduled substation feeder transfers.")
			));
		}
	}

	public List<Alert> getAllAlerts() {
		return alertRepository.findAll();
	}

	public Alert getAlertById(String id) {
		return alertRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found: " + id));
	}

	public List<Alert> getAlertsByMeterId(String meterId) {
		return alertRepository.findByMeterIdIgnoreCase(meterId);
	}

	@Transactional
	public Alert transitionAlertStatus(String id, AlertStatus targetStatus) {
		Alert alert = getAlertById(id);
		AlertStatus currentStatus = alert.getStatus();

		if (currentStatus == targetStatus) {
			return alert;
		}

		boolean isValid = switch (targetStatus) {
			case INVESTIGATING -> currentStatus == AlertStatus.OPEN;
			case ACKNOWLEDGED -> currentStatus == AlertStatus.OPEN || currentStatus == AlertStatus.INVESTIGATING;
			case RESOLVED -> currentStatus == AlertStatus.OPEN || currentStatus == AlertStatus.INVESTIGATING || currentStatus == AlertStatus.ACKNOWLEDGED;
			default -> false;
		};

		if (!isValid) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					String.format("Invalid status transition from %s to %s for alert %s", currentStatus, targetStatus, id));
		}

		alert.setStatus(targetStatus);
		return alertRepository.save(alert);
	}

	public AlertEvidence getAlertEvidence(String id) {
		Alert alert = getAlertById(id);
		UsageSummaryDto summary = usageClient.getUsageSummary(alert.getMeterId())
				.orElse(new UsageSummaryDto(alert.getMeterId(), alert.getCurrentValue(), alert.getCurrentValue(), alert.getCurrentValue(), 0.0, 0.0, 0.0, 0));
		return new AlertEvidence(alert, summary);
	}

	public List<Anomaly> getAllAnomalies() {
		return anomalyRepository.findAll();
	}

	public Anomaly getAnomalyById(String id) {
		return anomalyRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anomaly not found: " + id));
	}

	public List<Anomaly> getAnomaliesByMeterId(String meterId) {
		return anomalyRepository.findByMeterIdIgnoreCase(meterId);
	}

	@Transactional
	public Anomaly transitionAnomalyStatus(String id, AnomalyStatus targetStatus) {
		Anomaly anomaly = getAnomalyById(id);
		AnomalyStatus currentStatus = anomaly.getStatus();

		if (currentStatus == targetStatus) {
			return anomaly;
		}

		boolean isValid = switch (targetStatus) {
			case INVESTIGATING -> currentStatus == AnomalyStatus.OPEN;
			case RESOLVED -> currentStatus == AnomalyStatus.OPEN || currentStatus == AnomalyStatus.INVESTIGATING;
			default -> false;
		};

		if (!isValid) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					String.format("Invalid status transition from %s to %s for anomaly %s", currentStatus, targetStatus, id));
		}

		anomaly.setStatus(targetStatus);
		return anomalyRepository.save(anomaly);
	}
}
