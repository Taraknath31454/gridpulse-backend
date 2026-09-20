package com.gridpulse.meterservice.meter;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/meters")
public class MeterController {

	private final MeterService meterService;

	public MeterController(MeterService meterService) {
		this.meterService = meterService;
	}

	@GetMapping
	public List<Meter> getMeters() {
		return meterService.findAll();
	}

	@GetMapping("/{id}")
	public Meter getMeter(@PathVariable String id) {
		return meterService.findById(id)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Meter not found: " + id));
	}
}
