package com.gridpulse.alertservice.client;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.gridpulse.alertservice.model.UsageSummaryDto;

@Component
public class UsageClient {

	private final RestTemplate restTemplate;

	public UsageClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public Optional<UsageSummaryDto> getUsageSummary(String meterId) {
		try {
			ResponseEntity<UsageSummaryDto> response = restTemplate.getForEntity(
					"http://USAGE-SERVICE/usage/meter/{meterId}/summary",
					UsageSummaryDto.class,
					meterId);

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				return Optional.of(response.getBody());
			}
			return Optional.empty();
		} catch (HttpClientErrorException.NotFound ex) {
			return Optional.empty();
		} catch (RestClientResponseException ex) {
			if (ex.getStatusCode().value() == 404) {
				return Optional.empty();
			}
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error fetching usage data: " + ex.getMessage(), ex);
		} catch (Exception ex) {
			return Optional.empty();
		}
	}
}
