package com.gridpulse.usageservice.model;

public record UsageSummary(
		String meterId,
		double currentPower,
		double averagePower,
		double peakPower,
		double totalEnergy,
		double averageVoltage,
		double averageFrequency,
		long readingCount) {
}

