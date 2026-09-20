package com.gridpulse.usageservice.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gridpulse.usageservice.client.MeterClient;
import com.gridpulse.usageservice.model.Usage;
import com.gridpulse.usageservice.model.UsageSummary;
import com.gridpulse.usageservice.repository.UsageRepository;

@Service
public class UsageService implements CommandLineRunner {

	private final UsageRepository usageRepository;
	private final MeterClient meterClient;

	public UsageService(UsageRepository usageRepository, MeterClient meterClient) {
		this.usageRepository = usageRepository;
		this.meterClient = meterClient;
	}

	@Override
	public void run(String... args) {
		if (usageRepository.count() == 0) {
			usageRepository.saveAll(List.of(
					// MTR-1001 readings
					new Usage("USG-1001-1", "MTR-1001", Instant.parse("2026-09-20T12:00:00Z"), 175.5, 14.6, 230.1, 50.01, 0.95),
					new Usage("USG-1001-2", "MTR-1001", Instant.parse("2026-09-20T12:05:00Z"), 180.2, 15.0, 230.4, 49.99, 0.96),
					new Usage("USG-1001-3", "MTR-1001", Instant.parse("2026-09-20T12:10:00Z"), 188.0, 15.7, 229.8, 50.00, 0.94),
					new Usage("USG-1001-4", "MTR-1001", Instant.parse("2026-09-20T12:15:00Z"), 185.3, 15.4, 230.2, 49.98, 0.95),
					new Usage("USG-1001-5", "MTR-1001", Instant.parse("2026-09-20T12:20:00Z"), 182.4, 15.2, 230.6, 49.98, 0.96),

					// MTR-1002 readings
					new Usage("USG-1002-1", "MTR-1002", Instant.parse("2026-09-20T12:00:00Z"), 92.4, 7.7, 228.1, 49.90, 0.91),
					new Usage("USG-1002-2", "MTR-1002", Instant.parse("2026-09-20T12:05:00Z"), 98.1, 8.2, 228.5, 49.93, 0.92),
					new Usage("USG-1002-3", "MTR-1002", Instant.parse("2026-09-20T12:10:00Z"), 105.6, 8.8, 227.9, 49.88, 0.89),
					new Usage("USG-1002-4", "MTR-1002", Instant.parse("2026-09-20T12:15:00Z"), 101.2, 8.4, 228.4, 49.92, 0.90),
					new Usage("USG-1002-5", "MTR-1002", Instant.parse("2026-09-20T12:20:00Z"), 96.8, 8.1, 228.9, 49.91, 0.91),

					// MTR-1003 readings
					new Usage("USG-1003-1", "MTR-1003", Instant.parse("2026-09-20T12:00:00Z"), 70.1, 5.8, 231.0, 50.01, 0.98),
					new Usage("USG-1003-2", "MTR-1003", Instant.parse("2026-09-20T12:10:00Z"), 76.5, 6.4, 231.5, 50.03, 0.97),
					new Usage("USG-1003-3", "MTR-1003", Instant.parse("2026-09-20T12:20:00Z"), 74.3, 6.2, 231.2, 50.02, 0.98),

					// MTR-1004 readings
					new Usage("USG-1004-1", "MTR-1004", Instant.parse("2026-09-20T11:50:00Z"), 12.0, 1.0, 220.0, 49.50, 0.85),
					new Usage("USG-1004-2", "MTR-1004", Instant.parse("2026-09-20T11:55:00Z"), 0.0, 0.0, 0.0, 0.0, 0.0)
			));
		}
	}

	public List<Usage> getAllUsage() {
		return usageRepository.findAll();
	}

	public List<Usage> getUsageByMeterId(String meterId) {
		validateMeterExists(meterId);
		return usageRepository.findByMeterIdIgnoreCaseOrderByTimestampAsc(meterId);
	}

	public UsageSummary getUsageSummaryByMeterId(String meterId) {
		validateMeterExists(meterId);
		List<Usage> readings = usageRepository.findByMeterIdIgnoreCaseOrderByTimestampAsc(meterId);

		if (readings.isEmpty()) {
			return new UsageSummary(meterId, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0);
		}

		double currentPower = readings.get(readings.size() - 1).getPowerKw();
		double averagePower = round(readings.stream().mapToDouble(Usage::getPowerKw).average().orElse(0.0));
		double peakPower = round(readings.stream().mapToDouble(Usage::getPowerKw).max().orElse(0.0));
		double totalEnergy = round(readings.stream().mapToDouble(Usage::getEnergyKwh).sum());
		double averageVoltage = round(readings.stream().mapToDouble(Usage::getVoltage).average().orElse(0.0));
		double averageFrequency = round(readings.stream().mapToDouble(Usage::getFrequency).average().orElse(0.0));
		long readingCount = readings.size();

		return new UsageSummary(
				meterId,
				currentPower,
				averagePower,
				peakPower,
				totalEnergy,
				averageVoltage,
				averageFrequency,
				readingCount);
	}

	private void validateMeterExists(String meterId) {
		boolean exists = meterClient.verifyMeterExists(meterId);
		if (!exists) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Meter not found: " + meterId);
		}
	}

	private double round(double value) {
		return Math.round(value * 100.0) / 100.0;
	}
}
