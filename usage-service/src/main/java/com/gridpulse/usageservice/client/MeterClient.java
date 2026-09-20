package com.gridpulse.usageservice.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Component
public class MeterClient {

	private final RestTemplate restTemplate;

	public MeterClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public boolean verifyMeterExists(String meterId) {
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(
					"http://METER-SERVICE/meters/{id}", String.class, meterId);
			return response.getStatusCode().is2xxSuccessful();
		} catch (HttpClientErrorException.NotFound ex) {
			return false;
		} catch (RestClientResponseException ex) {
			if (ex.getStatusCode().value() == 404) {
				return false;
			}
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error calling meter-service: " + ex.getMessage(), ex);
		} catch (Exception ex) {
			return false;
		}
	}
}

