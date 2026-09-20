package com.gridpulse.alertservice.model;

public record AlertEvidence(
		Alert alert,
		UsageSummaryDto usageSummary) {
}
