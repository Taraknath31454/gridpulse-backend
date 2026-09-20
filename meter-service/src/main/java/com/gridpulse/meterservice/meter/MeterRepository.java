package com.gridpulse.meterservice.meter;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeterRepository extends JpaRepository<Meter, String> {

	Optional<Meter> findByIdIgnoreCase(String id);
}

