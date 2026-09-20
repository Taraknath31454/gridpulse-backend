package com.gridpulse.alertservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gridpulse.alertservice.model.Alert;
import com.gridpulse.alertservice.model.AlertEvidence;
import com.gridpulse.alertservice.model.AlertStatus;
import com.gridpulse.alertservice.service.AlertService;

@RestController
@RequestMapping("/alerts")
public class AlertController {

	private final AlertService alertService;

	public AlertController(AlertService alertService) {
		this.alertService = alertService;
	}

	@GetMapping
	public List<Alert> getAllAlerts() {
		return alertService.getAllAlerts();
	}

	@GetMapping("/{id}")
	public Alert getAlertById(@PathVariable String id) {
		return alertService.getAlertById(id);
	}

	@GetMapping("/meter/{meterId}")
	public List<Alert> getAlertsByMeter(@PathVariable String meterId) {
		return alertService.getAlertsByMeterId(meterId);
	}

	@GetMapping("/{id}/evidence")
	public AlertEvidence getAlertEvidence(@PathVariable String id) {
		return alertService.getAlertEvidence(id);
	}

	@RequestMapping(value = "/{id}/investigate", method = {RequestMethod.POST, RequestMethod.PATCH})
	public Alert investigateAlert(@PathVariable String id) {
		return alertService.transitionAlertStatus(id, AlertStatus.INVESTIGATING);
	}

	@RequestMapping(value = "/{id}/acknowledge", method = {RequestMethod.POST, RequestMethod.PATCH})
	public Alert acknowledgeAlert(@PathVariable String id) {
		return alertService.transitionAlertStatus(id, AlertStatus.ACKNOWLEDGED);
	}

	@RequestMapping(value = "/{id}/resolve", method = {RequestMethod.POST, RequestMethod.PATCH})
	public Alert resolveAlert(@PathVariable String id) {
		return alertService.transitionAlertStatus(id, AlertStatus.RESOLVED);
	}
}
