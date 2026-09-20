package com.gridpulse.usageservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gridpulse.usageservice.model.Usage;
import com.gridpulse.usageservice.model.UsageSummary;
import com.gridpulse.usageservice.service.UsageService;

@RestController
@RequestMapping("/usage")
public class UsageController {

	private final UsageService usageService;

	public UsageController(UsageService usageService) {
		this.usageService = usageService;
	}

	@GetMapping
	public List<Usage> getAllUsage() {
		return usageService.getAllUsage();
	}

	@GetMapping("/meter/{meterId}")
	public List<Usage> getUsageByMeter(@PathVariable String meterId) {
		return usageService.getUsageByMeterId(meterId);
	}

	@GetMapping("/meter/{meterId}/summary")
	public UsageSummary getUsageSummary(@PathVariable String meterId) {
		return usageService.getUsageSummaryByMeterId(meterId);
	}
}

