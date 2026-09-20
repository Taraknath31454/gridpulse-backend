package com.gridpulse.alertservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gridpulse.alertservice.model.Alert;

@Repository
public interface AlertRepository extends JpaRepository<Alert, String> {

	List<Alert> findByMeterIdIgnoreCase(String meterId);
}

