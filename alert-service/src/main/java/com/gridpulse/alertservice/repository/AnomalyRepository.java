package com.gridpulse.alertservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gridpulse.alertservice.model.Anomaly;

@Repository
public interface AnomalyRepository extends JpaRepository<Anomaly, String> {

	List<Anomaly> findByMeterIdIgnoreCase(String meterId);
}

