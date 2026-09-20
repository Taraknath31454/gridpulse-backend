package com.gridpulse.alertservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gridpulse.alertservice.model.Anomaly;
import com.gridpulse.alertservice.model.AnomalyStatus;
import com.gridpulse.alertservice.service.AlertService;

@RestController
@RequestMapping("/anomalies")
public class AnomalyController {

	private final AlertService alertService;

	public AnomalyController(AlertService alertService) {
		this.alertService = alertService;
	}

	@GetMapping
	public List<Anomaly> getAllAnomalies() {
		return alertService.getAllAnomalies();
	}

	@GetMapping("/{id}")
	public Anomaly getAnomalyById(@PathVariable String id) {
		return alertService.getAnomalyById(id);
	}

	@GetMapping("/meter/{meterId}")
	public List<Anomaly> getAnomaliesByMeter(@PathVariable String meterId) {
		return alertService.getAnomaliesByMeterId(meterId);
	}

	@RequestMapping(value = "/{id}/investigate", method = {RequestMethod.POST, RequestMethod.PATCH})
	public Anomaly investigateAnomaly(@PathVariable String id) {
		return alertService.transitionAnomalyStatus(id, AnomalyStatus.INVESTIGATING);
	}

	@RequestMapping(value = "/{id}/resolve", method = {RequestMethod.POST, RequestMethod.PATCH})
	public Anomaly resolveAnomaly(@PathVariable String id) {
		return alertService.transitionAnomalyStatus(id, AnomalyStatus.RESOLVED);
	}
}
