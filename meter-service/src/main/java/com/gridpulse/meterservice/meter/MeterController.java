package com.gridpulse.meterservice.meter;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

	@PostMapping
	public ResponseEntity<Meter> createMeter(
			@Validated(MeterRequest.Create.class) @RequestBody MeterRequest request) {
		Meter meter = meterService.create(request);
		return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(meter.getId()).toUri()).body(meter);
	}

	@PutMapping("/{id}")
	public Meter updateMeter(@PathVariable String id, @Valid @RequestBody MeterRequest request) {
		return meterService.update(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMeter(@PathVariable String id) {
		meterService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ProblemDetail handleDataConflict(DataIntegrityViolationException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Meter data conflicts with an existing meter");
	}
}
