package com.gridpulse.alertservice.model;

public record UsageSummaryDto(
		String meterId,
		double currentPower,
		double averagePower,
		double peakPower,
		double totalEnergy,
		double averageVoltage,
		double averageFrequency,
		long readingCount) {
}
