package com.gridpulse.meterservice.meter;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MeterService implements CommandLineRunner {

	private final MeterRepository meterRepository;

	public MeterService(MeterRepository meterRepository) {
		this.meterRepository = meterRepository;
	}

	@Override
	public void run(String... args) {
		if (meterRepository.count() == 0) {
			meterRepository.saveAll(List.of(
					new Meter("MTR-1001", "North Feeder Meter", "North Substation", "North Zone",
							MeterStatus.ONLINE, 182.4, 230.6, 49.98, MeterHealth.HEALTHY,
							Instant.parse("2026-09-20T12:25:30Z")),
					new Meter("MTR-1002", "Assembly Line Meter", "Manufacturing Plant A", "Industrial Zone",
							MeterStatus.WARNING, 96.8, 228.9, 49.91, MeterHealth.DEGRADED,
							Instant.parse("2026-09-20T12:24:48Z")),
					new Meter("MTR-1003", "East Campus Meter", "GridPulse Research Campus", "East Zone",
							MeterStatus.ONLINE, 74.3, 231.2, 50.02, MeterHealth.HEALTHY,
							Instant.parse("2026-09-20T12:25:11Z")),
					new Meter("MTR-1004", "Cold Storage Meter", "Distribution Center", "South Zone",
							MeterStatus.OFFLINE, 0.0, 0.0, 0.0, MeterHealth.CRITICAL,
							Instant.parse("2026-09-20T11:58:02Z"))));
		}
	}

	public List<Meter> findAll() {
		return meterRepository.findAll();
	}

	public Optional<Meter> findById(String id) {
		return meterRepository.findByIdIgnoreCase(id);
	}

	@Transactional
	public Meter create(MeterRequest request) {
		if (findById(request.id()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Meter already exists: " + request.id());
		}
		Meter meter = new Meter();
		meter.setId(request.id());
		applyValues(meter, request);
		return meterRepository.saveAndFlush(meter);
	}

	@Transactional
	public Meter update(String id, MeterRequest request) {
		Meter meter = requireMeter(id);
		if (request.id() != null && !request.id().equalsIgnoreCase(id)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meter ID must match the URL");
		}
		applyValues(meter, request);
		return meterRepository.saveAndFlush(meter);
	}

	@Transactional
	public void delete(String id) {
		meterRepository.delete(requireMeter(id));
		meterRepository.flush();
	}

	private Meter requireMeter(String id) {
		return findById(id).orElseThrow(() -> new ResponseStatusException(
				HttpStatus.NOT_FOUND, "Meter not found: " + id));
	}

	private void applyValues(Meter meter, MeterRequest request) {
		meter.setDeviceName(request.deviceName());
		meter.setFacility(request.facility());
		meter.setZone(request.zone());
		meter.setStatus(request.status());
		meter.setCurrentLoad(request.currentLoad());
		meter.setVoltage(request.voltage());
		meter.setFrequency(request.frequency());
		meter.setHealth(request.health());
		meter.setLastCommunication(request.lastCommunication());
	}
}
