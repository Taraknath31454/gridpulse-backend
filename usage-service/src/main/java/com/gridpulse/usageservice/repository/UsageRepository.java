package com.gridpulse.usageservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gridpulse.usageservice.model.Usage;

@Repository
public interface UsageRepository extends JpaRepository<Usage, String> {

	List<Usage> findByMeterIdIgnoreCaseOrderByTimestampAsc(String meterId);
}

